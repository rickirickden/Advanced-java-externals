package com.tms.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tms.dao.TokenDao;
import com.tms.model.User;

/** cancels one of the logged in user's waiting tokens */
@WebServlet("/user/cancel")
public class CancelTokenServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        int tokenId;
        try {
            tokenId = Integer.parseInt(req.getParameter("token_id"));
        } catch (NumberFormatException e) {
            res.sendRedirect("../user/home?err=Nothing+to+cancel");
            return;
        }

        try {
            if (TokenDao.cancel(tokenId, user.getUserId())) {
                res.sendRedirect("../user/home?msg=Token+cancelled");
            } else {
                res.sendRedirect("../user/home?err=That+token+can+no+longer+be+cancelled");
            }
        } catch (SQLException e) {
            throw new ServletException("could not cancel token", e);
        }
    }
}
