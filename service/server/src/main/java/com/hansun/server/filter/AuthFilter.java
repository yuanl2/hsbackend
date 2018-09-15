package com.hansun.server.filter;

import com.hansun.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yuanl2
 */
public class AuthFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ServletContext context = filterConfig.getServletContext();

        logger.info(context.toString());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String user_agent = request.getHeader("User-Agent");
        String token = request.getHeader("x-access-token");
        logger.info("User-Agent: {} token {}", user_agent, token);


//        try {
//            String userName = userService.getUserNameByToken(token);
//
//
//            if (userName == null) {
//                logger.info("request {}", request);
//            }
//        } catch (Exception e) {
//            logger.error("{}", e);
//        }
        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
