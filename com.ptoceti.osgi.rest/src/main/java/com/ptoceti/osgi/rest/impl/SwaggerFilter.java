package com.ptoceti.osgi.rest.impl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SwaggerFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Activator.getLogger().info("SwaggerFilter initialiazed with props: " + filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (req.getPathInfo() == null) {
            res.sendRedirect(req.getRequestURI() + "/index.html");
        } else if (req.getPathInfo().equals("/")) {
            res.sendRedirect(req.getRequestURI() + "index.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
