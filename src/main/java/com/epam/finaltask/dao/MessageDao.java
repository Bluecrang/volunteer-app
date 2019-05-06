package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Message;

import java.util.List;

public interface MessageDao extends Dao<Message> {

    List<Message> findMessagesByTopicId(long topicId) throws PersistenceException;
    boolean createWithGeneratedDate(Message entity) throws PersistenceException;
    int countMessagesByTopic(long topicId) throws PersistenceException;
    List<Message> findPageAccountsSortByRating(long topicId, int startPage, int numberOfMessagesPerPage)
            throws PersistenceException;
}
