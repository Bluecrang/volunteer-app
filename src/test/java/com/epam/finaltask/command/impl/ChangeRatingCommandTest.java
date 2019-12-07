package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeRatingCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ChangeRatingCommand changeRatingCommand;

    private String accountId = "1";
    private long longAccountId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_ratingChanged_successNotificationSet() throws ServiceException, CommandException {
        String rating = "15";
        int intRating = 15;
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER))
                .thenReturn(rating);
        when(accountService.addValueToRating(longAccountId, intRating))
                .thenReturn(true);

        CommandResult actual = changeRatingCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + accountId);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER);
        verify(accountService).addValueToRating(longAccountId, intRating);
        verify(commandData).putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE,
                "profile.rating_changed");
    }

    @Test
    public void performAction_ratingNotChanged_failureNotificationSet() throws ServiceException, CommandException {
        String rating = "15";
        int intRating = 15;
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER))
                .thenReturn(rating);
        when(accountService.addValueToRating(longAccountId, intRating))
                .thenReturn(false);

        CommandResult actual = changeRatingCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + accountId);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER);
        verify(accountService).addValueToRating(longAccountId, intRating);
        verify(commandData).putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE,
                "profile.could_not_change_rating");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        String rating = "15";
        int intRating = 15;
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER))
                .thenReturn(rating);
        when(accountService.addValueToRating(longAccountId, intRating))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            changeRatingCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER);
        verify(accountService).addValueToRating(longAccountId, intRating);
    }

    @Test
    public void performAction_unparsableAccountId_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn("qqvqev");

        Assert.assertThrows(CommandException.class, () -> {
            changeRatingCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
    }

    @Test
    public void performAction_unparsableRating_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER))
                .thenReturn("qeqve");

        Assert.assertThrows(CommandException.class, () -> {
            changeRatingCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER);
    }
}
