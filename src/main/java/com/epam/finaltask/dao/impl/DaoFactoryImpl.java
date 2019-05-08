package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.TopicDao;

public class DaoFactoryImpl implements DaoFactory {

    @Override
    public AccountDao createAccountDao(AbstractConnectionManager connectionManager) {
        return new AccountDaoImpl(connectionManager);
    }

    @Override
    public MessageDao createMessageDao(AbstractConnectionManager connectionManager) {
        return new MessageDaoImpl(connectionManager);
    }

    @Override
    public TopicDao createTopicDao(AbstractConnectionManager connectionManager) {
        return new TopicDaoImpl(connectionManager);
    }
}
