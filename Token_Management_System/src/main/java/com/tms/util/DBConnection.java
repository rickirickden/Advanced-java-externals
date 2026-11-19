package com.tms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gives a fresh JDBC connection to whoever asks.
 * Change the three values below to match your MySQL setup
 * (XAMPP default is root with an empty password).
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/token_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "ricK999_@777";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver jar missing from WEB-INF/lib", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // small helpers so the DAO code stays short
    public static void close(Connection con) {
        if (con != null) try { con.close(); } catch (SQLException ignored) {}
    }

    public static void close(java.sql.Statement st) {
        if (st != null) try { st.close(); } catch (SQLException ignored) {}
    }

    public static void close(java.sql.ResultSet rs) {
        if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
    }
}
