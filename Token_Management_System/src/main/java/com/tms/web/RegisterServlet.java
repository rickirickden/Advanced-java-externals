package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.UserDao;
import com.tms.model.User;
import com.tms.util.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String fullName = req.getParameter("fullname");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirm");
        String phone    = req.getParameter("phone");

        // basic checks before touching the database
        if (isBlank(fullName) || isBlank(username) || isBlank(password)) {
            res.sendRedirect("register?err=All+fields+marked+*+are+required");
            return;
        }
        if (!password.equals(confirm)) {
            res.sendRedirect("register?err=Passwords+do+not+match");
            return;
        }
        if (password.length() < 6) {
            res.sendRedirect("register?err=Password+needs+at+least+6+characters");
            return;
        }
        if (!username.matches("[A-Za-z0-9_]{3,30}")) {
            res.sendRedirect("register?err=Username+can+have+letters,+numbers+and+_+only");
            return;
        }

        try {
            if (userDao.usernameExists(username.trim())) {
                res.sendRedirect("register?err=That+username+is+already+taken");
                return;
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setUsername(username.trim());
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setPhone(phone == null ? null : phone.trim());
            userDao.insert(user);

            res.sendRedirect("login?msg=Account+created.+Please+log+in");

        } catch (SQLException e) {
            throw new ServletException("registration failed", e);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
