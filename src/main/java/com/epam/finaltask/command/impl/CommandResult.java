package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.PageConstants;

public class CommandResult {
    private String page = PageConstants.INDEX_PAGE;
    private TransitionType transitionType = TransitionType.REDIRECT;

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public void assignTransitionTypeForward() {
        transitionType = TransitionType.FORWARD;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        if (page != null) {
            this.page = page;
        }
    }
}
