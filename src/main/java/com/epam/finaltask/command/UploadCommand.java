package com.epam.finaltask.command;

import javax.servlet.http.Part;
import java.util.Collection;

public interface UploadCommand {
    CommandResult execute(CommandData data, Collection<Part> parts) throws CommandException;
}
