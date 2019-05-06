package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Topic;

public interface TopicDao extends Dao<Topic> {

    boolean createWithGeneratedDate(Topic entity) throws PersistenceException;
    Topic findTopicByTitle(String title) throws PersistenceException;
}
