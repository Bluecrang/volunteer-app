package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.ApplicationConstants;

/**
 * Result of the executed command. Contains page to move to and transition type.
 * Contains sessionInvalidationFlag to signal about the need to invalidate session.
 */
public class CommandResult {
    /**
     * Page to move to.
     */
    private String page = ApplicationConstants.SHOW_INDEX_PAGE;
    /**
     * Type of the transition.
     */
    private TransitionType transitionType = TransitionType.REDIRECT;
    /**
     * Flag that tells if session should be invalidated.
     */
    private boolean sessionInvalidationFlag;
    /**
     * Code of the error, if error has occurred.
     */
    private int code;

    /**
     * Set transition type to {@link TransitionType#FORWARD}.
     */
    public void assignTransitionTypeForward() {
        transitionType = TransitionType.FORWARD;
    }

    /**
     * Set transition type to {@link TransitionType#ERROR}.
     */
    public void assignTransitionTypeError() {
        transitionType = TransitionType.ERROR;
    }

    /**
     * Set session invalidation flag to {@code true}.
     */
    public void raiseSessionInvalidationFlag() {
        sessionInvalidationFlag = true;
    }

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        if (page != null) {
            this.page = page;
        }
    }

    public boolean isSessionInvalidationFlag() {
        return sessionInvalidationFlag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
