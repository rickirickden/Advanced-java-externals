package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.CounterDao;
import com.tms.dao.ServiceDao;
import com.tms.dao.TokenDao;
import com.tms.model.Counter;
import com.tms.model.Token;

/**
 * One servlet for the counters page.
 * GET draws the table, POST carries out whichever button was pressed
 * (the action parameter says which one).
 */
@WebServlet("/admin/counters")
public class CounterServlet extends HttpServlet {

    private CounterDao counterDao = new CounterDao();
    private ServiceDao serviceDao = new ServiceDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            req.setAttribute("counters", counterDao.listAll());
            req.setAttribute("services", serviceDao.listAll());
            req.getRequestDispatcher("/admin/counters.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load counters", e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        String back = "../admin/counters";

        try {
            if ("add".equals(action)) {
                String name = req.getParameter("name");
                String operator = req.getParameter("operator");
                int serviceId = parseInt(req.getParameter("service_id"));
                if (name == null || name.trim().isEmpty()) {
                    res.sendRedirect(back + "?err=Counter+name+is+required");
                    return;
                }
                counterDao.insert(name.trim(), operator, serviceId);
                res.sendRedirect(back + "?msg=Counter+added");
                return;
            }

            if ("open".equals(action) || "close".equals(action)) {
                int counterId = parseInt(req.getParameter("counter_id"));
                counterDao.setStatus(counterId, action.equals("open") ? "OPEN" : "CLOSED");
                res.sendRedirect(back + "?msg=Counter+updated");
                return;
            }

            if ("remove".equals(action)) {
                int counterId = parseInt(req.getParameter("counter_id"));
                if (counterDao.hasLiveTokens(counterId)) {
                    res.sendRedirect(back + "?err=Counter+still+has+waiting+or+live+tokens");
                } else {
                    counterDao.remove(counterId);
                    res.sendRedirect(back + "?msg=Counter+removed");
                }
                return;
            }

            if ("call".equals(action)) {
                int counterId = parseInt(req.getParameter("counter_id"));
                Counter counter = counterDao.get(counterId);

                if (counter == null || !"OPEN".equals(counter.getStatus())) {
                    res.sendRedirect(back + "?err=Open+the+counter+first");
                } else if (counter.getServiceId() == 0) {
                    res.sendRedirect(back + "?err=Assign+a+service+to+this+counter+first");
                } else {
                    Token token = TokenDao.callNext(counter);
                    if (token == null) {
                        res.sendRedirect(back + "?err=No+waiting+token+for+"
                                + counter.getServiceName().replace(" ", "+"));
                    } else {
                        res.sendRedirect(back + "?msg=Calling+" + token.getTokenNo());
                    }
                }
                return;
            }

            if ("served".equals(action) || "noshow".equals(action)) {
                int counterId = parseInt(req.getParameter("counter_id"));
                Token current = TokenDao.currentForCounter(counterId);
                if (current == null) {
                    res.sendRedirect(back + "?err=No+token+is+being+served+here");
                } else {
                    boolean ok = "served".equals(action)
                            ? TokenDao.markServed(current.getTokenId())
                            : TokenDao.markNoShow(current.getTokenId());
                    res.sendRedirect(back + (ok ? "?msg=Token+" + current.getTokenNo() + "+done"
                                                : "?err=Could+not+update+the+token"));
                }
                return;
            }

            // unknown action - just reload the page
            res.sendRedirect(back);

        } catch (SQLException e) {
            throw new ServletException("counter action failed", e);
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
