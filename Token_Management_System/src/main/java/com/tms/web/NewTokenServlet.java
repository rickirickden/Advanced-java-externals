package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.ServiceDao;
import com.tms.dao.TokenDao;
import com.tms.model.Service;
import com.tms.model.Token;
import com.tms.model.User;

/** GET shows the "get token" form, POST actually issues the token */
@WebServlet("/user/new")
public class NewTokenServlet extends HttpServlet {

    private ServiceDao serviceDao = new ServiceDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<Service> services = serviceDao.listActive();
            req.setAttribute("services", services);
            req.getRequestDispatcher("/user/new.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("could not load services", e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        int serviceId;
        try {
            serviceId = Integer.parseInt(req.getParameter("service_id"));
        } catch (NumberFormatException e) {
            res.sendRedirect("new?err=Pick+a+service+first");
            return;
        }

        try {
            // one live token per user at a time keeps the queue fair
            List<Token> active = TokenDao.activeForUser(user.getUserId());
            if (!active.isEmpty()) {
                res.sendRedirect("../user/home?err=You+already+have+token+"
                        + active.get(0).getTokenNo()+ "+in+the+queue");
                return;
            }

            Token token = TokenDao.issueToken(user.getUserId(), serviceId);
            if (token == null) {
                res.sendRedirect("new?err=That+service+is+not+available+right+now");
                return;
            }
            res.sendRedirect("../user/home?msg=Token+" + token.getTokenNo() + "+issued");

        } catch (SQLException e) {
            throw new ServletException("could not issue token", e);
        }
    }
}
