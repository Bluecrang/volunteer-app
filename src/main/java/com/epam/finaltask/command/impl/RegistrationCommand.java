package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.service.RegistrationService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.EmailValidator;
import com.epam.finaltask.validation.PasswordValidator;
import com.epam.finaltask.validation.UsernameValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrationCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String ILLEGAL_EMAIL = "registration.illegal_email";
    private static final String ILLEGAL_USERNAME = "registration.illegal_username";
    private static final String ILLEGAL_PASSWORD = "registration.illegal_password";
    private static final String ACCOUNT_EXISTS = "registration.account_exists";
    private static final String ACCOUNT_SUCCESSFULLY_REGISTERED = "login.account_successfully_registered";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        String username = data.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        String password = data.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        String email = data.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);

        CommandResult commandResult = new CommandResult();
        commandResult.setPage(ApplicationConstants.SHOW_REGISTRATION_PAGE);

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.validate(email)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_EMAIL);
            return commandResult;
        }
        UsernameValidator usernameValidator = new UsernameValidator();
        if (!usernameValidator.validate(username)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_USERNAME);
            return commandResult;
        }
        PasswordValidator passwordValidator = new PasswordValidator();
        if (!passwordValidator.validate(password)) {
            data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ILLEGAL_PASSWORD);
            return commandResult;
        }

        RegistrationService registrationService = new RegistrationService();
        try {
            if (registrationService.registerUser(username, password, email)) {
                logger.log(Level.INFO, "user with username=" + username + " successfully registered");
                data.putRequestAttribute(ApplicationConstants.AUTHORIZATION_MESSAGE_ATTRIBUTE, ACCOUNT_SUCCESSFULLY_REGISTERED);
                commandResult.setPage(ApplicationConstants.SHOW_LOGIN_PAGE);
            } else {
                logger.log(Level.INFO, "could not register user with username=" + username);
                data.putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, ACCOUNT_EXISTS);
            }
        } catch (ServiceException e) {
            throw new CommandException("unable to create new account email=" + email, e);
        }
        return commandResult;
    }
}
