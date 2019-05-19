package com.epam.finaltask.command;

import com.epam.finaltask.util.ApplicationConstants;

public class CommandResult {
    private String page = ApplicationConstants.SHOW_INDEX_PAGE;
    private TransitionType transitionType = TransitionType.REDIRECT;
    private boolean sessionInvalidationFlag;
    private int code;

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public void assignTransitionTypeForward() {
        transitionType = TransitionType.FORWARD;
    }

    public void assignTransitionTypeError() {
        transitionType = TransitionType.ERROR;
    }

    public void raiseSessionInvalidationFlag() {
        sessionInvalidationFlag = true;
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
