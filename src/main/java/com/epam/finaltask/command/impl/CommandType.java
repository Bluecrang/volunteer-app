package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.AccountType;

/**
 * Enum that contains all command types.
 */
public enum CommandType {
    AUTHORIZATION(new AuthenticationCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.GUEST)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    REGISTRATION(new RegistrationCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.GUEST)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    LOGOUT(new LogoutCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.USER, AccountType.ADMIN, AccountType.VOLUNTEER)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    SHOW_TOPICS(new ShowTopicsCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_TOPIC_PAGE(new ShowTopicPageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    CHANGE_TOPIC_HIDDEN_STATE(new ChangeTopicHiddenStateCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    CLOSE_TOPIC(new CloseTopicCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN, AccountType.VOLUNTEER)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    MOVE_TO_INDEX_PAGE(new MoveToIndexPageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    CREATE_TOPIC(new CreateTopicCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.USER, AccountType.ADMIN, AccountType.VOLUNTEER)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    SHOW_PROFILE(new ShowProfileCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    CREATE_MESSAGE(new CreateMessageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.USER, AccountType.ADMIN, AccountType.VOLUNTEER)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    DELETE_MESSAGE(new DeleteMessageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    CHANGE_ACCOUNT_BLOCK_STATE(new ChangeAccountBlockStateCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    CHANGE_LOCALE(new ChangeLocaleCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_RANKING_PAGE(new ShowRankingCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    CHANGE_RATING(new ChangeRatingCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    CHANGE_ACCOUNT_TYPE(new ChangeAccountTypeCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.POST)
            .build())),
    SEARCH_FOR_TOPICS(new SearchForTopicsCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_MAIN_PAGE(new ShowMainPageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.values())
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_REGISTRATION_PAGE(new ShowRegistrationPageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.GUEST)
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_LOGIN_PAGE(new ShowLoginPageCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.GUEST)
            .buildHttpMethods(HttpMethodType.GET)
            .build())),
    SHOW_ADMINISTRATORS(new ShowAdministratorsCommand(CommandConstraints.builder()
            .buildAccountTypes(AccountType.ADMIN)
            .buildHttpMethods(HttpMethodType.GET)
            .build()));

    /**
     * Command that can be executed.
     */
    private Command command;

    /**
     * Creates CommandType with chosen command.
     * @param command Command that can be executed
     */
    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
