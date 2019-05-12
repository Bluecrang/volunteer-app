package com.epam.finaltask.command;

import com.epam.finaltask.util.PageConstants;

public class CommandResult {
    private String page = PageConstants.INDEX_PAGE;
    private TransitionType transitionType = TransitionType.REDIRECT;
    boolean sessionInvalidationFlag;

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public void assignTransitionTypeForward() {
        transitionType = TransitionType.FORWARD;
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
}
