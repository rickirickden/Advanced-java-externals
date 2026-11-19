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

/** past tokens of the logged in user */
@WebServlet("/user/history")
public class HistoryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            List<Token> history = TokenDao.historyForUser(user.getUserId());
            req.setAttribute("history", history);
            req.getRequestDispatcher("/user/history.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load history", e);
        }
    }
}
