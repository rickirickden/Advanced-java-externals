package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.CounterDao;
import com.tms.dao.TokenDao;
import com.tms.model.Counter;

/** admin dashboard: today's numbers + counter snapshot + latest tokens */
@WebServlet("/admin/home")
public class AdminHomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            int[] counts = TokenDao.todayCounts();
            req.setAttribute("issuedToday", counts[0]);
            req.setAttribute("waitingNow", counts[1]);
            req.setAttribute("servedToday", counts[2]);
            req.setAttribute("goneToday", counts[3]);
            req.setAttribute("counters", new CounterDao().listAll());
            req.setAttribute("todayTokens", TokenDao.todayList());
            req.getRequestDispatcher("/admin/home.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load dashboard", e);
        }
    }
}
