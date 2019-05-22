package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.AbstractConnectionManager;

/**
 * Factory interface which is used to creates DAOs.
 */
public interface DaoFactory {

    /**
     * Creates AccountDao using specified connection manager.
     * @param connectionManager Connection manager which is used to provide connection to the DAO
     * @return Account dao
     */
    AccountDao createAccountDao(AbstractConnectionManager connectionManager);

    /**
     * Creates MessageDao using specified connection manager.
     * @param connectionManager Connection manager which is used to provide connection to the DAO
     * @return Message dao
     */
    MessageDao createMessageDao(AbstractConnectionManager connectionManager);

    /**
     * Creates TopicDao using specified connection manager.
     * @param connectionManager Connection manager which is used to provide connection to the DAO
     * @return Topic dao
     */
    TopicDao createTopicDao(AbstractConnectionManager connectionManager);
}
