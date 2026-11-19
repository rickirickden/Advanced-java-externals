package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tms.dao.UserDao;
import com.tms.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // already logged in? go straight to the right home page
        User user = (User) req.getSession().getAttribute("user");
        if (user != null) {
            redirectHome(res, user.getRole());
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            res.sendRedirect("login?err=Enter+username+and+password");
            return;
        }

        try {
            User user = userDao.checkLogin(username.trim(), password);
            if (user == null) {
                res.sendRedirect("login?err=Wrong+username+or+password");
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            redirectHome(res, user.getRole());

        } catch (SQLException e) {
            throw new ServletException("login failed", e);
        }
    }

    private void redirectHome(HttpServletResponse res, String role) throws IOException {
        if ("ADMIN".equals(role)) {
            res.sendRedirect("admin/home");
        } else {
            res.sendRedirect("user/home");
        }
    }
}
