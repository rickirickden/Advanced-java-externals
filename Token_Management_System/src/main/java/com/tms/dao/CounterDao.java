package com.tms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tms.model.Counter;
import com.tms.util.DBConnection;

public class CounterDao {

    /**
     * All counters with their service name, the token being served
     * right now and how many people are waiting for that service.
     */
    public List<Counter> listAll() throws SQLException {
        String sql =
            "SELECT c.*, s.name AS service_name, t.token_no AS current_token, " +
            "  (SELECT COUNT(*) FROM tokens w WHERE w.service_id = c.service_id " +
            "   AND w.status = 'WAITING') AS waiting " +
            "FROM counters c " +
            "LEFT JOIN services s ON s.service_id = c.service_id " +
            "LEFT JOIN tokens t ON t.counter_id = c.counter_id AND t.status = 'CALLED' " +
            "ORDER BY c.counter_id";

        Connection con = DBConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Counter> list = new ArrayList<Counter>();
            while (rs.next()) {
                Counter c = new Counter();
                c.setCounterId(rs.getInt("counter_id"));
                c.setName(rs.getString("name"));
                c.setOperatorName(rs.getString("operator_name"));
                c.setServiceId(rs.getInt("service_id"));
                c.setStatus(rs.getString("status"));
                c.setServiceName(rs.getString("service_name"));
                c.setCurrentTokenNo(rs.getString("current_token"));
                c.setWaitingCount(rs.getInt("waiting"));
                list.add(c);
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    public Counter get(int counterId) throws SQLException {
        String sql = "SELECT c.*, s.name AS service_name FROM counters c " +
                     "LEFT JOIN services s ON s.service_id = c.service_id " +
                     "WHERE c.counter_id = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, counterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Counter c = new Counter();
                c.setCounterId(rs.getInt("counter_id"));
                c.setName(rs.getString("name"));
                c.setOperatorName(rs.getString("operator_name"));
                c.setServiceId(rs.getInt("service_id"));
                c.setStatus(rs.getString("status"));
                c.setServiceName(rs.getString("service_name"));
                return c;
            }
            return null;
        } finally {
            DBConnection.close(con);
        }
    }

    public void insert(String name, String operatorName, int serviceId) throws SQLException {
        String sql = "INSERT INTO counters (name, operator_name, service_id) VALUES (?,?,?)";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, operatorName);
            if (serviceId > 0) {
                ps.setInt(3, serviceId);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }

    public void setStatus(int counterId, String status) throws SQLException {
        String sql = "UPDATE counters SET status = ? WHERE counter_id = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, counterId);
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }

    /** a counter can only be removed while it has no live tokens */
    public boolean hasLiveTokens(int counterId) throws SQLException {
        String sql = "SELECT token_id FROM tokens WHERE counter_id = ? AND status IN ('WAITING','CALLED')";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, counterId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(con);
        }
    }

    public void remove(int counterId) throws SQLException {
        String sql = "DELETE FROM counters WHERE counter_id = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, counterId);
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }
}
