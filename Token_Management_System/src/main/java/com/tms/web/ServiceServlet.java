package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.ServiceDao;

/** admin page: list + add + enable/disable services */
@WebServlet("/admin/services")
public class ServiceServlet extends HttpServlet {

    private ServiceDao serviceDao = new ServiceDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            req.setAttribute("services", serviceDao.listAll());
            req.getRequestDispatcher("/admin/services.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load services", e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        String back = "../admin/services";

        try {
            if ("add".equals(action)) {
                String name = req.getParameter("name");
                String code = req.getParameter("code");
                int avg = parseInt(req.getParameter("avg_minutes"));

                if (name == null || name.trim().isEmpty()
                        || code == null || !code.trim().toUpperCase().matches("[A-Z]")) {
                    res.sendRedirect(back + "?err=Service+name+and+a+single+letter+code+are+needed");
                    return;
                }
                if (avg < 1 || avg > 120) avg = 5;   // sane default

                serviceDao.insert(name.trim(), code.trim(), avg);
                res.sendRedirect(back + "?msg=Service+added");
                return;
            }

            if ("enable".equals(action) || "disable".equals(action)) {
                int serviceId = parseInt(req.getParameter("service_id"));
                serviceDao.setActive(serviceId, "enable".equals(action));
                res.sendRedirect(back + "?msg=Service+updated");
                return;
            }

            res.sendRedirect(back);

        } catch (SQLException e) {
            throw new ServletException("service action failed", e);
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
