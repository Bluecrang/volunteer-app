package com.epam.finaltask.util;

public final class ApplicationConstants {

    public static final String HASHING_ALGORITHM = "SHA-256";

    public static final String LOGIN_PARAMETER = "login";
    public static final String PASSWORD_PARAMETER = "password";
    public static final String EMAIL_PARAMETER = "email";
    public static final String PAGE_PARAMETER = "page";
    public static final String ACCOUNT_ID_PARAMETER = "account_id";
    public static final String TOPIC_ID_PARAMETER = "topic_id";
    public static final String MESSAGE_ID_PARAMETER = "message_id";
    public static final String TOPIC_LOAD_MESSAGES_PARAMETER = "load_messages";
    public static final String TOPIC_LOAD_ACCOUNTS_PARAMETER = "load_accounts";
    public static final String LOCALE_PARAMETER = "locale";
    public static final String CHANGE_RATING_PARAMETER = "add_rating";

    public static final String TOPIC_CURRENT_PAGE_ATTRIBUTE = "topic_current_page";
    public static final String TOPIC_PAGE_COUNT_ATTRIBUTE = "topic_number_of_pages";
    public static final String RANKING_CURRENT_PAGE_ATTRIBUTE = "ranking_current_page";
    public static final String RANKING_PAGE_COUNT_ATTRIBUTE = "ranking_number_of_pages";
    public static final String AUTHORIZATION_MESSAGE_ATTRIBUTE = "authorization_message";
    public static final String REGISTRATION_MESSAGE_ATTRIBUTE = "registration_message";
    public static final String LOCALE_ATTRIBUTE = "locale";

    public static final String SHOW_TOPICS = "/topics?command=show_topics"; //todo filter
    public static final String SHOW_PROFILE = "/profile?command=show_profile&account_id=";
    public static final String SHOW_TOPIC = "/topic?command=show_topic_page&load_messages=true&page=1&topic_id=";
    public static final String SHOW_TOPIC_LAST_PAGE = "/topic?command=show_topic_page&load_messages=true&page=last&topic_id=";

    public static final String ACCOUNT_ATTRIBUTE = "account";

    public static final String TOPIC_ACTION_NOTIFICATION = "topic_action_notification";

    private ApplicationConstants() {
    }
}
