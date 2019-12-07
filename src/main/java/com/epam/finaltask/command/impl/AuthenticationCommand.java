package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AuthenticationException;
import com.epam.finaltask.service.AuthenticationService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that allows to authenticate user.
 */
public class AuthenticationCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACCOUNT_BLOCKED_PROPERTY = "login.account_blocked";
    private static final String ACCOUNT_DOES_NOT_EXIST_OR_PASSWORD_DOES_NOT_MATCH_PROPERTY = "login.incorrect_email_or_password";
    private static final String AUTHENTICATION_ERROR_PROPERTY = "login.authentication_error";

    private AuthenticationService authenticationService;

    public AuthenticationCommand(CommandConstraints commandConstraints) {
        super(commandConstraints);
        this.authenticationService = new AuthenticationService();
    }

    public AuthenticationCommand(CommandConstraints commandConstraints, AuthenticationService authenticationService) {
        super(commandConstraints);
        this.authenticationService = authenticationService;
    }
    @Override
    public CommandResult performAction(CommandData data) {
        String email = data.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        String password = data.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);

        CommandResult commandResult = new CommandResult();
        commandResult.setPage(ApplicationConstants.SHOW_LOGIN_PAGE);
        try {
            Account account = authenticationService.authenticate(email, password);
                if (account.isBlocked()) {
                    logger.log(Level.INFO, "account with email=" + email + " is blocked, cannot authorize");
                    data.putSessionAttribute(ApplicationConstants.AUTHORIZATION_MESSAGE_ATTRIBUTE, ACCOUNT_BLOCKED_PROPERTY);
                    return commandResult;
                }
                data.putSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE, account);
                commandResult.setPage(ApplicationConstants.SHOW_MAIN_PAGE);
        } catch (AuthenticationException e) {
            logger.log(Level.INFO, "user with email=" + email +
                    " does not exist or password does not match, cannot authorize");
            data.putSessionAttribute(ApplicationConstants.AUTHORIZATION_MESSAGE_ATTRIBUTE,
                    ACCOUNT_DOES_NOT_EXIST_OR_PASSWORD_DOES_NOT_MATCH_PROPERTY);
        } catch (ServiceException e) {
            logger.log(Level.ERROR, "unable to authenticate user email=" + email, e);
            data.putSessionAttribute(ApplicationConstants.AUTHORIZATION_MESSAGE_ATTRIBUTE, AUTHENTICATION_ERROR_PROPERTY);
        }
        return commandResult;
    }
}
