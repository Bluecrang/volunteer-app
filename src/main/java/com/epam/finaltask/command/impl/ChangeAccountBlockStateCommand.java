package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that allows to change account block state.
 */
public class ChangeAccountBlockStateCommand extends Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String ACCOUNT_BLOCK_PARAMETER = "block";

    public ChangeAccountBlockStateCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            boolean block = Boolean.parseBoolean(data.getRequestParameter(ACCOUNT_BLOCK_PARAMETER));
            try {
                AccountService accountService = new AccountService();
                if (accountService.changeAccountBlockState(accountId, block)) {
                    logger.log(Level.INFO, "account id=" + accountId + " block state changed to " + block);
                } else {
                    logger.log(Level.WARN, "could not change account id="+ accountId +" block state to " + block);
                }
            } catch (ServiceException e) {
                throw new CommandException("unable to change account block state to " + block, e);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse account id to long value", e);
        }
        return commandResult;
    }
}
