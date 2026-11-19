package com.tms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tms.model.User;

/**
 * Guards everything under /user and /admin.
 * Pages inside /user need any logged in user,
 * pages inside /admin need the ADMIN role.
 */
@WebFilter({ "/user/*", "/admin/*" })
public class AuthFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            // not logged in at all
            res.sendRedirect(req.getContextPath() + "/login?err=Please+log+in+first");
            return;
        }

        if (req.getRequestURI().startsWith(req.getContextPath() + "/admin/")
                && !"ADMIN".equals(user.getRole())) {
            // a normal user tried to open an admin page
            res.sendRedirect(req.getContextPath() + "/user/home?err=Admin+access+only");
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {}
}
