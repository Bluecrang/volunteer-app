package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.UploadCommand;
import com.epam.finaltask.entity.AccountType;

/**
 * Enum that contains all UploadCommand types.
 */
public enum UploadCommandType {
    UPLOAD_AVATAR(new UploadAvatarCommand(CommandConstraints.builder()
            .buildHttpMethods(HttpMethodType.POST)
            .buildAccountTypes(AccountType.USER, AccountType.ADMIN)
            .build()));

    private UploadCommand uploadCommand;

    UploadCommandType(UploadCommand uploadCommand) {
        this.uploadCommand = uploadCommand;
    }

    public UploadCommand getUploadCommand() {
        return uploadCommand;
    }
}
