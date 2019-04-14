package com.epam.finaltask.entity;

import java.sql.Clob;
import java.sql.Date;

public class Topic extends Entity {
    private long topicId;
    private boolean closed;
    private Clob title;
    private Clob text;
    private Date date;
    private long accountId;

    public Topic() {
    }

    public Topic(long topicId, boolean closed, Clob title, Clob text, Date date, long accountid) {
        this.topicId = topicId;
        this.closed = closed;
        this.title = title;
        this.text = text;
        this.date = date;
        this.accountId = accountid;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Clob getTitle() {
        return title;
    }

    public void setTitle(Clob title) {
        this.title = title;
    }

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (topicId != topic.topicId) return false;
        if (closed != topic.closed) return false;
        if (accountId != topic.accountId) return false;
        if (title != null ? !title.equals(topic.title) : topic.title != null) return false;
        if (text != null ? !text.equals(topic.text) : topic.text != null) return false;
        return date != null ? date.equals(topic.date) : topic.date == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (topicId ^ (topicId >>> 32));
        result = 31 * result + (closed ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Topic{");
        sb.append("topicId=").append(topicId);
        sb.append(", closed=").append(closed);
        sb.append(", title=").append(title);
        sb.append(", text=").append(text);
        sb.append(", date=").append(date);
        sb.append(", accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
