package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.UploadCommand;

public enum UploadCommandType {
    UPLOAD_AVATAR(new UploadAvatarCommand());

    private UploadCommand uploadCommand;

    UploadCommandType(UploadCommand uploadCommand) {
        this.uploadCommand = uploadCommand;
    }

    public UploadCommand getUploadCommand() {
        return uploadCommand;
    }
}
