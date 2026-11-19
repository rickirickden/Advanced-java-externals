package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.TokenDao;
import com.tms.model.Token;
import com.tms.model.User;

/** the normal user home page: active tokens + position in the queue */
@WebServlet("/user/home")
public class UserHomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            List<Token> active = TokenDao.activeForUser(user.getUserId());
            req.setAttribute("activeTokens", active);
            req.getRequestDispatcher("/user/home.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load your tokens", e);
        }
    }
}
