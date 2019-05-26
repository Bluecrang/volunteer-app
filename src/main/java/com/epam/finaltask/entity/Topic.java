package com.epam.finaltask.entity;

import java.time.LocalDateTime;

/**
 * Topic entity.
 */
public class Topic extends Entity {
    private long topicId;
    private String title;
    private String text;

    /**
     * Date of the topic creation.
     */
    private LocalDateTime date;

    /**
     * Account of the topic author.
     */
    private Account account;
    private boolean closed;
    private boolean hidden;

    public Topic() {
    }

    public Topic(long topicId) {
        this.topicId = topicId;
    }

    public Topic(String title, String text, LocalDateTime date, Account account, boolean closed, boolean hidden) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.account = account;
        this.closed = closed;
        this.hidden = hidden;
    }

    public Topic(long topicId, String title, String text, LocalDateTime date, Account account, boolean closed, boolean hidden) {
        this.topicId = topicId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.account = account;
        this.closed = closed;
        this.hidden = hidden;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (topicId != topic.topicId) return false;
        if (closed != topic.closed) return false;
        if (hidden != topic.hidden) return false;
        if (title != null ? !title.equals(topic.title) : topic.title != null) return false;
        if (text != null ? !text.equals(topic.text) : topic.text != null) return false;
        if (date != null ? !date.equals(topic.date) : topic.date != null) return false;
        return account != null ? account.equals(topic.account) : topic.account == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (topicId ^ (topicId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (closed ? 1 : 0);
        result = 31 * result + (hidden ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Topic{");
        sb.append("topicId=").append(topicId);
        sb.append(", title='").append(title).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", date=").append(date);
        sb.append(", account=").append(account);
        sb.append(", closed=").append(closed);
        sb.append(", hidden=").append(hidden);
        sb.append('}');
        return sb.toString();
    }
}
