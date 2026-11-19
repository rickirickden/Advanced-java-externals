package com.tms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.tms.model.Counter;
import com.tms.model.Token;
import com.tms.util.DBConnection;

public class TokenDao {

    private static final int TODAY_LIST_SIZE = 15;

    /**
     * Creates a new token for the given service and returns it.
     *
     * This method is synchronized: if two users press "Get Token" at the
     * same moment, one of them waits here until the other has finished.
     * Without this the SELECT MAX + INSERT pair can run twice at once and
     * both users would receive the same number.
     */
    public static synchronized Token issueToken(int userId, int serviceId) throws SQLException {

        Connection con = DBConnection.getConnection();
        try {
            // the service must exist and be active
            PreparedStatement ps = con.prepareStatement(
                    "SELECT code, active FROM services WHERE service_id = ?");
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || rs.getInt("active") != 1) {
                return null;
            }
            String code = rs.getString("code");

            // today's biggest number for this service, then add one
            ps = con.prepareStatement(
                    "SELECT MAX(CAST(SUBSTRING(token_no, 3) AS UNSIGNED)) FROM tokens " +
                    "WHERE service_id = ? AND DATE(issued_at) = CURDATE()");
            ps.setInt(1, serviceId);
            rs = ps.executeQuery();
            int next = 1;
            if (rs.next() && rs.getObject(1) != null) {
                next = rs.getInt(1) + 1;
            }
            String tokenNo = code + "-" + String.format("%03d", next);

            ps = con.prepareStatement(
                    "INSERT INTO tokens (token_no, user_id, service_id) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tokenNo);
            ps.setInt(2, userId);
            ps.setInt(3, serviceId);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            rs.next();
            return get(con, rs.getInt(1));
        } finally {
            DBConnection.close(con);
        }
    }

    /** full row by id (own connection version) */
    public static Token get(int tokenId) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            return get(con, tokenId);
        } finally {
            DBConnection.close(con);
        }
    }

    /** tokens of this user that are still WAITING or CALLED */
    public static List<Token> activeForUser(int userId) throws SQLException {
        String sql = baseSelect() +
                " WHERE t.user_id = ? AND t.status IN ('WAITING','CALLED') ORDER BY t.token_id";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Token> list = new ArrayList<Token>();
            while (rs.next()) {
                Token t = mapRow(rs);
                if ("WAITING".equals(t.getStatus())) {
                    t.setPosition(countAhead(con, t));
                }
                list.add(t);
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    /** last 20 tokens of this user, newest first */
    public static List<Token> historyForUser(int userId) throws SQLException {
        String sql = baseSelect() +
                " WHERE t.user_id = ? ORDER BY t.token_id DESC LIMIT 20";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Token> list = new ArrayList<Token>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    /** a user can cancel only his own token, and only while waiting */
    public static boolean cancel(int tokenId, int userId) throws SQLException {
        String sql = "UPDATE tokens SET status = 'CANCELLED', done_at = NOW() " +
                     "WHERE token_id = ? AND user_id = ? AND status = 'WAITING'";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, tokenId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } finally {
            DBConnection.close(con);
        }
    }

    /**
     * Counter calls the next waiting token of its service.
     * Returns the token that is now being served, or null if the
     * queue is empty / the counter already has a token on screen.
     */
    public static Token callNext(Counter counter) throws SQLException {
        if (counter.getServiceId() == 0) return null;   // no service assigned

        Connection con = DBConnection.getConnection();
        try {
            // the counter must finish its current token first
            PreparedStatement ps = con.prepareStatement(
                    "SELECT token_id FROM tokens WHERE counter_id = ? AND status = 'CALLED'");
            ps.setInt(1, counter.getCounterId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return null;

            // smallest waiting token for this service = longest waiting
            ps = con.prepareStatement(
                    "SELECT token_id FROM tokens WHERE service_id = ? AND status = 'WAITING' " +
                    "ORDER BY issued_at, token_id LIMIT 1");
            ps.setInt(1, counter.getServiceId());
            rs = ps.executeQuery();
            if (!rs.next()) return null;

            int tokenId = rs.getInt(1);

            // the "AND status = 'WAITING'" here protects against two counters
            // grabbing the same token at the same moment
            ps = con.prepareStatement(
                    "UPDATE tokens SET status = 'CALLED', counter_id = ?, called_at = NOW() " +
                    "WHERE token_id = ? AND status = 'WAITING'");
            ps.setInt(1, counter.getCounterId());
            ps.setInt(2, tokenId);
            if (ps.executeUpdate() == 0) return null;    // lost the race

            return get(con, tokenId);
        } finally {
            DBConnection.close(con);
        }
    }

    /** the token a counter is currently serving (status CALLED), or null */
    public static Token currentForCounter(int counterId) throws SQLException {
        String sql = baseSelect() +
                " WHERE t.counter_id = ? AND t.status = 'CALLED' ORDER BY t.called_at DESC";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, counterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            DBConnection.close(con);
        }
    }

    public static boolean markServed(int tokenId) throws SQLException {
        return finishToken(tokenId, 'S');
    }

    public static boolean markNoShow(int tokenId) throws SQLException {
        return finishToken(tokenId, 'N');
    }

    /** today's numbers for the admin dashboard: issued, waiting, served, gone */
    public static int[] todayCounts() throws SQLException {
        String sql =
            "SELECT COUNT(*) AS issued, " +
            "  SUM(CASE WHEN status = 'WAITING' THEN 1 ELSE 0 END) AS waiting, " +
            "  SUM(CASE WHEN status = 'SERVED'  THEN 1 ELSE 0 END) AS served, " +
            "  SUM(CASE WHEN status IN ('CANCELLED','NOSHOW') THEN 1 ELSE 0 END) AS gone " +
            "FROM tokens WHERE DATE(issued_at) = CURDATE()";
        Connection con = DBConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            return new int[] {
                rs.getInt("issued"),
                rs.getInt("waiting"),
                rs.getInt("served"),
                rs.getInt("gone")
            };
        } finally {
            DBConnection.close(con);
        }
    }

    /** today's tokens, newest first - for the admin dashboard table */
    public static List<Token> todayList() throws SQLException {
        String sql = baseSelect() +
                " WHERE DATE(t.issued_at) = CURDATE() ORDER BY t.token_id DESC LIMIT " + TODAY_LIST_SIZE;
        Connection con = DBConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Token> list = new ArrayList<Token>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    // ------------------------------------------------------------------
    // internal helpers
    // ------------------------------------------------------------------

    private static boolean finishToken(int tokenId, char kind) throws SQLException {
        String status = (kind == 'S') ? "SERVED" : "NOSHOW";
        String sql = "UPDATE tokens SET status = ?, done_at = NOW() " +
                     "WHERE token_id = ? AND status = 'CALLED'";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, tokenId);
            return ps.executeUpdate() == 1;
        } finally {
            DBConnection.close(con);
        }
    }

    /** how many WAITING tokens of the same service were issued before this one */
    private static int countAhead(Connection con, Token t) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM tokens WHERE service_id = ? AND status = 'WAITING' AND token_id < ?");
        ps.setInt(1, t.getServiceId());
        ps.setInt(2, t.getTokenId());
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private static String baseSelect() {
        return
            "SELECT t.*, u.full_name AS user_name, s.name AS service_name, " +
            "       s.avg_minutes, c.name AS counter_name " +
            "FROM tokens t " +
            "JOIN users u    ON u.user_id = t.user_id " +
            "JOIN services s ON s.service_id = t.service_id " +
            "LEFT JOIN counters c ON c.counter_id = t.counter_id";
    }

    private static Token get(Connection con, int tokenId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(baseSelect() + " WHERE t.token_id = ?");
        ps.setInt(1, tokenId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    private static Token mapRow(ResultSet rs) throws SQLException {
        Token t = new Token();
        t.setTokenId(rs.getInt("token_id"));
        t.setTokenNo(rs.getString("token_no"));
        t.setUserId(rs.getInt("user_id"));
        t.setServiceId(rs.getInt("service_id"));
        t.setCounterId(rs.getInt("counter_id"));
        t.setStatus(rs.getString("status"));
        Timestamp issued = rs.getTimestamp("issued_at");
        t.setIssuedAt(issued);
        t.setCalledAt(rs.getTimestamp("called_at"));
        t.setDoneAt(rs.getTimestamp("done_at"));
        t.setUserName(rs.getString("user_name"));
        t.setServiceName(rs.getString("service_name"));
        t.setCounterName(rs.getString("counter_name"));
        t.setAvgMinutes(rs.getInt("avg_minutes"));
        return t;
    }
}
