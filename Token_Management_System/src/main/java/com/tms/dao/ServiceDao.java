package com.tms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tms.model.Service;
import com.tms.util.DBConnection;

public class ServiceDao {

    /** all services, active ones first - for the admin page */
    public List<Service> listAll() throws SQLException {
        String sql = "SELECT * FROM services ORDER BY active DESC, name";
        Connection con = DBConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Service> list = new ArrayList<Service>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    /** only active services - for the "get token" dropdown */
    public List<Service> listActive() throws SQLException {
        String sql = "SELECT * FROM services WHERE active = 1 ORDER BY name";
        Connection con = DBConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            List<Service> list = new ArrayList<Service>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            DBConnection.close(con);
        }
    }

    public Service get(int serviceId) throws SQLException {
        String sql = "SELECT * FROM services WHERE service_id = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            DBConnection.close(con);
        }
    }

    public void insert(String name, String code, int avgMinutes) throws SQLException {
        String sql = "INSERT INTO services (name, code, avg_minutes) VALUES (?,?,?)";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, code.toUpperCase());
            ps.setInt(3, avgMinutes);
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }

    public void setActive(int serviceId, boolean active) throws SQLException {
        String sql = "UPDATE services SET active = ? WHERE service_id = ?";
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, active);
            ps.setInt(2, serviceId);
            ps.executeUpdate();
        } finally {
            DBConnection.close(con);
        }
    }

    private Service mapRow(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setServiceId(rs.getInt("service_id"));
        s.setName(rs.getString("name"));
        s.setCode(rs.getString("code"));
        s.setAvgMinutes(rs.getInt("avg_minutes"));
        s.setActive(rs.getInt("active") == 1);
        return s;
    }
}
