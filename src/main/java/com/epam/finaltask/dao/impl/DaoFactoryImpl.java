package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.TopicDao;

/**
 * Factory for DAO creation.
 */
public class DaoFactoryImpl implements DaoFactory {

    /**
     * Creates {@link AccountDaoImpl} using chosen connection manager to provide connection.
     * @param connectionManager ConnectionManager which provides connection
     * @return {@link AccountDaoImpl}
     */
    @Override
    public AccountDao createAccountDao(AbstractConnectionManager connectionManager) {
        return new AccountDaoImpl(connectionManager);
    }

    /**
     * Creates {@link MessageDaoImpl} using chosen connection manager to provide connection.
     * @param connectionManager ConnectionManager which provides connection
     * @return {@link MessageDaoImpl}
     */
    @Override
    public MessageDao createMessageDao(AbstractConnectionManager connectionManager) {
        return new MessageDaoImpl(connectionManager);
    }

    /**
     * Creates {@link TopicDaoImpl} using chosen connection manager to provide connection.
     * @param connectionManager ConnectionManager which provides connection
     * @return {@link TopicDaoImpl}
     */
    @Override
    public TopicDao createTopicDao(AbstractConnectionManager connectionManager) {
        return new TopicDaoImpl(connectionManager);
    }
}
