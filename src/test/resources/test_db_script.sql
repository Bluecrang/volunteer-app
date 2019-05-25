SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE SCHEMA IF NOT EXISTS `volunteerdbtest` DEFAULT CHARACTER SET utf8 ;
USE `volunteerdbtest` ;
CREATE TABLE IF NOT EXISTS `volunteerdbtest`.`account_type` (
  `account_type_id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`account_type_id`),
  UNIQUE INDEX `idaccount_type_UNIQUE` (`account_type_id` ASC),
  UNIQUE INDEX `type_UNIQUE` (`type` ASC))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `volunteerdbtest`.`account` (
  `account_id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `account_type_id` BIGINT NOT NULL,
  `email` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `rating` INT NOT NULL,
  `blocked` TINYINT NOT NULL,
  `salt` VARCHAR(32) NOT NULL,
  `avatar` BLOB NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE INDEX `login_UNIQUE` (`username` ASC),
  INDEX `fk_account_account_type_idx` (`account_type_id` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  CONSTRAINT `fk_account_account_type`
    FOREIGN KEY (`account_type_id`)
    REFERENCES `volunteerdbtest`.`account_type` (`account_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `volunteerdbtest`.`topic` (
  `topic_id` BIGINT NOT NULL AUTO_INCREMENT,
  `closed` TINYINT NOT NULL,
  `title` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `text` TEXT NULL,
  `date_posted` DATETIME NULL,
  `account_id` BIGINT NOT NULL,
  `hidden` TINYINT NOT NULL,
  PRIMARY KEY (`topic_id`),
  INDEX `fk_topic_account1_idx` (`account_id` ASC),
  CONSTRAINT `fk_topic_account1`
    FOREIGN KEY (`account_id`)
    REFERENCES `volunteerdbtest`.`account` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `volunteerdbtest`.`message` (
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
    REFERENCES `volunteerdbtest`.`account` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_message_topic1`
    FOREIGN KEY (`topic_id`)
    REFERENCES `volunteerdbtest`.`topic` (`topic_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
INSERT INTO `volunteerdbtest`.`account_type` (`type`) VALUES("USER"), ("ADMIN");
INSERT INTO `volunteerdbtest`.`account`(`username`, `password`, `account_type_id`, `email`, `rating`, `blocked`, `salt`) VALUES("username", "password", 1, "user@mail.com", 10, 0, "salt");
INSERT INTO `volunteerdbtest`.`topic`(`title`, `closed`, `account_id`, `hidden`) VALUES("title", 0, 1, 0);