package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Part;
import java.util.Collection;

public class UploadAvatarCommand implements UploadCommand {

    private static final Logger logger = LogManager.getLogger();
    private static final String PROFILE_MESSAGE_ATTRIBUTE = "action_message";
    private static final String UNABLE_TO_UPLOAD_AVATAR_PROPERTY = "profile.action_message.unable_to_upload_avatar";

    @Override
    public CommandResult execute(CommandData commandData, Collection<Part> parts) throws CommandException {
        CommandResult result = new CommandResult();
        Object accountObject = commandData.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        if (accountObject instanceof Account) {
            Account account = (Account) accountObject;
            result.setPage(ApplicationConstants.SHOW_PROFILE + account.getAccountId());
            AccountService accountService = new AccountService();
            try {
                if (accountService.updateAvatar(account, parts.stream().findFirst().get())) {
                    logger.log(Level.INFO, "account id=" + account.getAccountId() + " avatar was successfully updated");
                } else {
                    logger.log(Level.WARN, "could not update account id=" + account.getAccountId() + " avatar");
                    commandData.putRequestAttribute(PROFILE_MESSAGE_ATTRIBUTE, UNABLE_TO_UPLOAD_AVATAR_PROPERTY);
                }
            } catch (ServiceException e) {
                throw new CommandException("could not update account avatar", e);
            }
        } else {
            throw new CommandException("account attribute is not an object of type Account");
        }
        return result;
    }
}
