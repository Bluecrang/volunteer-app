package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandValidatorTest {

    CommandValidator commandValidator = new CommandValidator();

    @Test
    public void validateTestValidCommand() {
        String command = "move_to_index_page";

        boolean actual = commandValidator.validate(command);

        Assert.assertTrue(actual);
    }

    @Test
    public void validateTestCommandNull() {
        String command = null;

        boolean actual = commandValidator.validate(command);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestNonexistentCommand() {
        String command = "nonexistent_command";

        boolean actual = commandValidator.validate(command);

        Assert.assertFalse(actual);
    }
}
