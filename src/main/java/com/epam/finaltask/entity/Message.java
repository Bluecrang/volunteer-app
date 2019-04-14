package com.epam.finaltask.entity;

import java.sql.Clob;
import java.sql.Date;

public class Message extends Entity {
    private long messageId;
    private Clob message;
    private long accountId;
    private Date date;
    private long topicId;

    public Message() {
    }

    public Message(long messageId, Clob message, long accountId, Date date, long topicId) {
        this.messageId = messageId;
        this.message = message;
        this.accountId = accountId;
        this.date = date;
        this.topicId = topicId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Clob getMessage() {
        return message;
    }

    public void setMessage(Clob message) {
        this.message = message;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (messageId != message1.messageId) return false;
        if (accountId != message1.accountId) return false;
        if (topicId != message1.topicId) return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null) return false;
        return date != null ? date.equals(message1.date) : message1.date == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (topicId ^ (topicId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("messageId=").append(messageId);
        sb.append(", message=").append(message);
        sb.append(", accountId=").append(accountId);
        sb.append(", date=").append(date);
        sb.append(", topicId=").append(topicId);
        sb.append('}');
        return sb.toString();
    }
}
