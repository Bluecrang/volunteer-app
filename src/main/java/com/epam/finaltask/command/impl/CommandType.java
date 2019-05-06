package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;

public enum CommandType {
    AUTHENTICATION(new AuthenticationCommand()),
    USER_REGISTRATION(new RegistrationCommand()),
    LOGOUT(new LogoutCommand()),
    SHOW_TOPICS(new ShowTopicsCommand()),
    SHOW_TOPIC_PAGE(new ShowTopicPageCommand()),
    CHANGE_TOPIC_HIDDEN_STATE(new ChangeTopicHiddenStateCommand()),
    CLOSE_TOPIC(new CloseTopicCommand()),
    MOVE_TO_INDEX_PAGE(new MoveToIndexPageCommand()),
    CREATE_TOPIC(new CreateTopicCommand()),
    SHOW_PROFILE(new ShowProfileCommand()),
    CREATE_MESSAGE(new CreateMessageCommand()),
    DELETE_MESSAGE(new DeleteMessageCommand()),
    CHANGE_ACCOUNT_BLOCK_STATE(new ChangeAccountBlockStateCommand()),
    CHANGE_LOCALE(new ChangeLocaleCommand()),
    SHOW_RANKING_PAGE(new ShowRankingCommand()),
    CHANGE_RATING(new ChangeRatingCommand());

    private Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
