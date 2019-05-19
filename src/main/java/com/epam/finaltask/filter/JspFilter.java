package com.epam.finaltask.filter;

import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(dispatcherTypes = {DispatcherType.REQUEST}, urlPatterns = "/*")
public class JspFilter implements Filter{

    private static final Logger logger = LogManager.getLogger();

    private static final Pattern JSP_EXTENSION_PATTERN = Pattern.compile(".*[.]jsp$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        logger.log(Level.DEBUG, "uri = " + uri);
        Matcher matcher = JSP_EXTENSION_PATTERN.matcher(uri);
        if (matcher.matches()) {
            logger.log(Level.DEBUG, "jsp extension found, sending error");
            httpResponse.sendError(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
            return;
        }
        chain.doFilter(request, response);
    }
}
