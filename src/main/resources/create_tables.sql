CREATE TABLE `account_type` (
  `account_type_id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`account_type_id`),
  UNIQUE INDEX `idaccount_type_UNIQUE` (`account_type_id` ASC),
  UNIQUE INDEX `type_UNIQUE` (`type` ASC));
CREATE TABLE `account` (
  `account_id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `account_type_id` BIGINT NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `rating` INT NOT NULL,
  `blocked` TINYINT NOT NULL,
  `salt` VARCHAR(32) NOT NULL,
  `avatar` MEDIUMBLOB NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE INDEX `login_UNIQUE` (`username` ASC),
  INDEX `fk_account_account_type_idx` (`account_type_id` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  CONSTRAINT `fk_account_account_type`
    FOREIGN KEY (`account_type_id`)
    REFERENCES `account_type` (`account_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
CREATE TABLE `topic` (
  `topic_id` BIGINT NOT NULL AUTO_INCREMENT,
  `closed` TINYINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `text` TEXT NULL,
  `date_posted` DATETIME NULL,
  `account_id` BIGINT NOT NULL,
  `hidden` TINYINT NOT NULL,
  PRIMARY KEY (`topic_id`),
  INDEX `fk_topic_account1_idx` (`account_id` ASC),
  CONSTRAINT `fk_topic_account1`
    FOREIGN KEY (`account_id`)
    REFERENCES `account` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
CREATE TABLE `message` (
  `message_id` BIGINT NOT NULL AUTO_INCREMENT,
  `message` TINYTEXT NOT NULL,
  `account_id` BIGINT NOT NULL,
  `date_posted` DATETIME NOT NULL,
  `topic_id` BIGINT NOT NULL,
  PRIMARY KEY (`message_id`),
  INDEX `fk_message_account1_idx` (`account_id` ASC),
  INDEX `fk_message_topic1_idx` (`topic_id` ASC),
  CONSTRAINT `fk_message_account1`
    FOREIGN KEY (`account_id`)
    REFERENCES `account` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_message_topic1`
    FOREIGN KEY (`topic_id`)
    REFERENCES `topic` (`topic_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
INSERT INTO `account_type` (`type`) VALUES ('USER'), ('ADMIN'), ('VOLUNTEER');
INSERT INTO `account`(`username`, `password`, `account_type_id`, `email`, `rating`, `blocked`, `salt`) VALUES('admin', 'f9a81477552594c79f2abc3fc099daa896a6e3a3590a55ffa392b6000412e80b', 1, 'admin@mail.com', 10, 0, 'salt');