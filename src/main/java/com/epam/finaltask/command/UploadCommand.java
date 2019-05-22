package com.epam.finaltask.command;

import javax.servlet.http.Part;
import java.util.Collection;

/**
 * UploadCommand interface. Implementations of this interface are used to perform different file upload actions.
 */
public interface UploadCommand {
    /**
     * Executes command using chosen data and parts.
     * @param data Data for command execution
     * @param parts Parts to fetch files from
     * @return CommandResult, which contains page and transition type
     * @throws CommandException If command execution failed
     */
    CommandResult execute(CommandData data, Collection<Part> parts) throws CommandException;
}
