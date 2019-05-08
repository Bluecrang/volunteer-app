package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.service.RegistrationService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import com.epam.finaltask.validation.EmailValidator;
import com.epam.finaltask.validation.LoginValidator;
import com.epam.finaltask.validation.PasswordValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrationCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String ILLEGAL_EMAIL = "registration.illegal_email";
    private static final String ILLEGAL_LOGIN = "registration.illegal_login";
    private static final String ILLEGAL_PASSWORD = "registration.illegal_password";
    private static final String ACCOUNT_EXISTS = "registration.account_exists";
    private static final String ACCOUNT_SUCCESSFULLY_REGISTERED = "login.account_successfully_registered";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        String login = data.getRequestParameter(ApplicationConstants.LOGIN_PARAMETER);
        String password = data.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        String email = data.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);

        CommandResult commandResult = new CommandResult();
        commandResult.setPage(PageConstants.REGISTRATION_PAGE);
        commandResult.assignTransitionTypeForward();

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.validate(email)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_EMAIL);
            return commandResult;
        }
        LoginValidator loginValidator = new LoginValidator();
        if (!loginValidator.validate(login)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_LOGIN);
            return commandResult;
        }
        PasswordValidator passwordValidator = new PasswordValidator();
        if (!passwordValidator.validate(password)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_PASSWORD);
            return commandResult;
        }

        RegistrationService registrationService = new RegistrationService();
        try {
            if (registrationService.registerUser(login, password, email)) {
                logger.log(Level.INFO, "user with login=" + login + " successfully registered");
                data.putRequestAttribute(ApplicationConstants.AUTHORIZATION_MESSAGE_ATTRIBUTE, ACCOUNT_SUCCESSFULLY_REGISTERED);
                commandResult.setPage(PageConstants.LOGIN_PAGE);
            } else {
                logger.log(Level.INFO, "could not register user with login=" + login);
                data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ACCOUNT_EXISTS);
            }
        } catch (ServiceException e) {
            throw new CommandException("unable to create new account email=" + email, e);
        }
        return commandResult;
    }
}
