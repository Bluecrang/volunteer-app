package com.epam.finaltask.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Web filter that is used to set request encoding.
 */
@WebFilter(filterName = "EncodingFilter")
public class EncodingFilter implements Filter {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Character encoding to which request encoding will be set.
     */
    private static final String REQUEST_ENCODING = "UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.log(Level.TRACE, "Entered EncodingFilter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        logger.log(Level.INFO, "EncodingFilter uri = " + uri);
        httpRequest.setCharacterEncoding(REQUEST_ENCODING);
        logger.log(Level.TRACE, "Exiting EncodingFilter");
        chain.doFilter(request, response);
    }
}
