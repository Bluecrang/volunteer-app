package com.epam.finaltask.util;

/**
 * Utility class, contains application constants.
 */
public final class ApplicationConstants {

    public static final String HASHING_ALGORITHM = "SHA-256";
    public static final int PAGE_NOT_FOUND_ERROR_CODE = 404;

    public static final String ACCOUNT_TYPE_GUEST_NOTIFICATION_KEY = "topics.account_type_guest";

    public static final String USERNAME_PARAMETER = "username";
    public static final String PASSWORD_PARAMETER = "password";
    public static final String EMAIL_PARAMETER = "email";
    public static final String PAGE_PARAMETER = "page";
    public static final String ACCOUNT_ID_PARAMETER = "account_id";
    public static final String TOPIC_ID_PARAMETER = "topic_id";
    public static final String MESSAGE_ID_PARAMETER = "message_id";
    public static final String LOCALE_PARAMETER = "locale";
    public static final String CHANGE_RATING_PARAMETER = "rating";
    public static final String COMMAND_PARAMETER = "command";

    public static final String TOPIC_CURRENT_PAGE_ATTRIBUTE = "topic_current_page";
    public static final String TOPIC_PAGE_COUNT_ATTRIBUTE = "topic_number_of_pages";
    public static final String RANKING_CURRENT_PAGE_ATTRIBUTE = "ranking_current_page";
    public static final String RANKING_PAGE_COUNT_ATTRIBUTE = "ranking_number_of_pages";
    public static final String AUTHORIZATION_MESSAGE_ATTRIBUTE = "authorization_message";
    public static final String REGISTRATION_MESSAGE_ATTRIBUTE = "registration_message";
    public static final String LOCALE_ATTRIBUTE = "locale";
    public static final String TOPICS_MESSAGE_ATTRIBUTE = "topics_message";
    public static final String ACCOUNT_ATTRIBUTE = "account";
    public static final String TOPIC_ACTION_NOTIFICATION_ATTRIBUTE = "topic_action_notification";
    public static final String PROFILE_ACTION_NOTIFICATION_ATTRIBUTE = "profile_action_notification";
    public static final String ACCOUNT_TYPE_PARAMETER = "account_type";

    public static final String SHOW_TOPICS = "/controller?command=show_topics";
    public static final String SHOW_PROFILE = "/controller?command=show_profile&account_id=";
    public static final String SHOW_TOPIC = "/controller?command=show_topic_page&load_messages=true&page=1&topic_id=";
    public static final String SHOW_TOPIC_LAST_PAGE = "/controller?command=show_topic_page&load_messages=true&page=last&topic_id=";
    public static final String SHOW_MAIN_PAGE = "/controller?command=show_main_page";
    public static final String MOVE_TO_INDEX_PAGE = "/controller?command=move_to_index_page";
    public static final String SHOW_REGISTRATION_PAGE = "/controller?command=show_registration_page";
    public static final String SHOW_LOGIN_PAGE = "/controller?command=show_login_page";
    public static final int TOPIC_CLOSE_RATING_BONUS = 1;

    private ApplicationConstants() {
    }
}
