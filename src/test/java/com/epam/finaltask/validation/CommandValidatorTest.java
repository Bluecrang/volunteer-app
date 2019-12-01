package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandValidatorTest {

    private CommandValidator commandValidator = new CommandValidator();

    @Test
    public void validate_commandExists_true() {
        String command = "move_to_index_page";

        boolean actual = commandValidator.validate(command);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_commandNull_false() {
        String command = null;

        boolean actual = commandValidator.validate(command);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_NonexistentCommand_false() {
        String command = "nonexistent_command";

        boolean actual = commandValidator.validate(command);

        Assert.assertFalse(actual);
    }
}
