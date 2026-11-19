package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.CounterDao;

/**
 * The waiting-room display board. Kept public (no login) because it
 * runs on a TV/screen in the hall. Refreshes itself every 10 seconds
 * with a meta tag - no javascript needed.
 */
@WebServlet("/board")
public class BoardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            req.setAttribute("counters", new CounterDao().listAll());
            req.getRequestDispatcher("/board.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load the board", e);
        }
    }
}
