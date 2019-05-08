package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.Dao;
import com.epam.finaltask.entity.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class AbstractDao<T extends Entity> implements Dao<T> {

    private Connection connection;

    AbstractDao(AbstractConnectionManager connectionManager) {
        this.connection = connectionManager.getConnection();
    }

    public boolean delete(long id) throws PersistenceException {

        try (PreparedStatement statement = getConnection().prepareStatement(getDeleteByIdQuery())){
            statement.setLong(1, id);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while deleting by id", e);
        }
    }

    protected Connection getConnection() {
        return connection;
    }
}
