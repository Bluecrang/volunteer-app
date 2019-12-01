package com.epam.finaltask;

import com.epam.finaltask.command.impl.CommandConstraints;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.HttpMethodType;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.validation.*;

public class Tester {

    public static void main(String[] args) {
        EmailValidator emailValidator = new EmailValidator();

        System.out.println("EmailValidator.validate(String email):");
        System.out.println("a-d: " + emailValidator.validate(null));
        System.out.println("a-b-d: " + emailValidator.validate("r8"));
        System.out.println("a-b-c-d: " + emailValidator.validate("email@mail.ru"));

        TextValidator textValidator = new TextValidator();

        System.out.println("TextValidator.validate(String text, int maxLength):");
        System.out.println("a-d: " + textValidator.validate(null, 3));
        System.out.println("a-b-d: " + textValidator.validate("arb8", 1));
        System.out.println("a-b-c-d: " + textValidator.validate("abc", 5));

        ImageFilenameValidator imageFilenameValidator = new ImageFilenameValidator();

        System.out.println("ImageFilenameValidator.validate(String filename):");
        System.out.println("a-e: " + imageFilenameValidator.validate(null));
        System.out.println("a-b-e: " + imageFilenameValidator.validate("abcd"));
        System.out.println("a-b-c-e: " + imageFilenameValidator.validate("abc.zip"));
        System.out.println("a-b-c-d-e: " + imageFilenameValidator.validate("abc.jpg"));

        UsernameValidator usernameValidator = new UsernameValidator();

        System.out.println("UsernameValidator.validate(String username):");
        System.out.println("a-d: " + usernameValidator.validate(null));
        System.out.println("a-b-d: " + usernameValidator.validate("abcfeq&"));
        System.out.println("a-b-c-d: " + usernameValidator.validate("abcdefg13"));

        CommandDataValidator commandDataValidator = new CommandDataValidator();

        System.out.println("commandDataValidator.validate(CommandData data, CommandConstraints Constraints):");
        System.out.println("a-i: " + commandDataValidator.validate(null, null));
        System.out.println("a-b-i: " + commandDataValidator.validate(new CommandData(null), null));
        System.out.println("a-b-c-i: " + commandDataValidator.validate(new CommandData(null),
                CommandConstraints.builder().build()));
        System.out.println("a-b-c-d-f-g-i: " + commandDataValidator.validate(new CommandData(null),
                CommandConstraints.builder().buildHttpMethods(HttpMethodType.UNDEFINED).build()));
        System.out.println("a-b-c-d-e-f-g-i: " + commandDataValidator.validate(
                new CommandData(HttpMethodType.UNDEFINED, new Account()),
                CommandConstraints.builder()
                        .buildHttpMethods(HttpMethodType.UNDEFINED)
                        .build()));
        System.out.println("a-b-c-d-e-g-h-i: " + commandDataValidator.validate(new CommandData(HttpMethodType.UNDEFINED,
                        new Account()),
                CommandConstraints.builder()
                        .buildHttpMethods(HttpMethodType.UNDEFINED)
                        .buildAccountTypes(AccountType.GUEST)
                        .build()));
    }
}
