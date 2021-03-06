package com.epam.finaltask.entity;

import java.time.LocalDateTime;

/**
 * Message entity.
 */
public class Message extends Entity {

    /**
     * Id of the message.
     */
    private long messageId;

    /**
     * Text of the message;
     */
    private String text;
    /**
     * Account of the message author.
     */
    private Account account;
    /**
     * Date of the message creation.
     */
    private LocalDateTime date;
    /**
     * Topic to which message was added.
     */
    private Topic topic;

    public Message() {
    }

    public Message(String text, Account account, LocalDateTime date, Topic topic) {
        this.text = text;
        this.account = account;
        this.date = date;
        this.topic = topic;
    }

    public Message(long messageId, String text, Account account, LocalDateTime date, Topic topic) {
        this.messageId = messageId;
        this.text = text;
        this.account = account;
        this.date = date;
        this.topic = topic;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (messageId != message1.messageId) return false;
        if (text != null ? !text.equals(message1.text) : message1.text != null) return false;
        if (account != null ? !account.equals(message1.account) : message1.account != null) return false;
        if (date != null ? !date.equals(message1.date) : message1.date != null) return false;
        return topic != null ? topic.equals(message1.topic) : message1.topic == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("messageId=").append(messageId);
        sb.append(", text='").append(text).append('\'');
        sb.append(", account=").append(account);
        sb.append(", date=").append(date);
        sb.append(", topic=").append(topic);
        sb.append('}');
        return sb.toString();
    }
}
