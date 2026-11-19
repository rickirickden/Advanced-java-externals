package com.tms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tms.model.User;
import com.tms.util.DBConnection;
import com.tms.util.PasswordUtil;

public class UserDao {

    /** returns the user for a login attempt, or null if wrong username/password */
    public User checkLogin(String username, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password_hash");
                if (PasswordUtil.matches(plainPassword, stored)) {
                    return mapRow(rs);
                }
            }
            return null;   // no such user or wrong password
        } finally {
            DBConnection.close(con);
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(con);
        }
    }

    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO users (full_name, username, password_hash, role, phone) VALUES (?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, "USER");           // public sign-up is always a normal user
            ps.setString(5, u.getPhone());
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}
