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

/**
 * Command which is used to upload avatar.
 */
public class UploadAvatarCommand extends UploadCommand {

    private static final Logger logger = LogManager.getLogger();
    private static final String PROFILE_MESSAGE_ATTRIBUTE = "action_message";
    private static final String UNABLE_TO_UPLOAD_AVATAR_PROPERTY = "profile.action_message.unable_to_upload_avatar";

    public UploadAvatarCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData commandData, Collection<Part> parts) throws CommandException { //todo add id
        CommandResult result = new CommandResult();
        Account sessionAccount = commandData.getSessionAccount();
        if (sessionAccount != null) {
            result.setPage(ApplicationConstants.SHOW_PROFILE + sessionAccount.getAccountId());
            AccountService accountService = new AccountService();
            try {
                if (accountService.updateAvatar(sessionAccount, parts.stream().findFirst().get())) {
                    logger.log(Level.INFO, "sessionAccount id=" + sessionAccount.getAccountId() + " avatar was successfully updated");
                } else {
                    logger.log(Level.WARN, "could not update sessionAccount id=" + sessionAccount.getAccountId() + " avatar");
                    commandData.putRequestAttribute(PROFILE_MESSAGE_ATTRIBUTE, UNABLE_TO_UPLOAD_AVATAR_PROPERTY);
                }
            } catch (ServiceException e) {
                throw new CommandException("could not update sessionAccount avatar", e);
            }
        } else {
            throw new CommandException("Could not perform action: session account is null");
        }
        return result;
    }
}
