package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.AbstractConnectionManager;

public interface DaoFactory {

    AccountDao createAccountDao(AbstractConnectionManager connectionManager);
    MessageDao createMessageDao(AbstractConnectionManager connectionManager);
    TopicDao createTopicDao(AbstractConnectionManager connectionManager);
}
