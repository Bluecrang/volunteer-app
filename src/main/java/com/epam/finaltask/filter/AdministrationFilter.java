package com.epam.finaltask.filter;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that forbids access to the pages that only administrators should see for simple users.
 */
@WebFilter(dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.REQUEST},
        filterName = "AdministrationFilter")
public class AdministrationFilter implements Filter {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Object accountObject = httpRequest.getSession().getAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        String uri = httpRequest.getRequestURI();
        logger.log(Level.DEBUG, "uri = " + uri);
        if(accountObject instanceof Account)  {
            Account account = (Account) accountObject;
            if (AccountType.ADMIN.equals(account.getAccountType())) {
                logger.log(Level.DEBUG, "account type is admin");
                chain.doFilter(request, response);
                return;
            }
        }
        logger.log(Level.DEBUG, "account is not present or account type is not admin: sending error");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
    }
}