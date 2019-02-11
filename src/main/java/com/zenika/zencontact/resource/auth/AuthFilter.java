package com.zenika.zencontact.resource.auth;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(urlPatterns = {"api/v0/users/*"})
public class AuthFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(AuthFilter.class
        .getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = ((HttpServletRequest)request);
        HttpServletResponse res = ((HttpServletResponse)response);
        LOG.log(Level.INFO, "Filtre d'authentification");
        String pathInfo = req.getPathInfo(); // /{id}
        if(pathInfo != null) { //{id}
            String[] pathParts = pathInfo.split("/");
            //only admin can delete
            if (req.getMethod() == "DELETE"
                && !AuthenticationService.getInstance().isAdmin()) {
                res.setStatus(403);
                return;
            }

            if(AuthenticationService.getInstance().getUser() != null) {
                res.setHeader("Username", AuthenticationService.getInstance().getUsername());
                res.setHeader("Logout", AuthenticationService.getInstance().getLogoutURL("/#/clear"));
            } else {
                //only authent users can edit
                res.setHeader("Location", AuthenticationService.getInstance().getLoginURL("/#/edit/" + pathParts[1]));
                res.setHeader("Logout", AuthenticationService.getInstance().getLogoutURL("/#/clear"));
                res.setStatus(401);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}