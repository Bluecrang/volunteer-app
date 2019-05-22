package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.Dao;
import com.epam.finaltask.entity.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract Dao, which defines basic delete and getConnection implementations.
 * @param <T> Application's Entity
 */
abstract class AbstractDao<T extends Entity> implements Dao<T> {

    /**
     * Connection used by DAO to access database.
     */
    private Connection connection;

    /**
     * Constructor which retrieves connection from connection manager.
     * @param connectionManager ConnectionManager which connection will be used for database interactions
     */
    AbstractDao(AbstractConnectionManager connectionManager) {
        this.connection = connectionManager.getConnection();
    }

    /**
     * Deletes entity from the database by id.
     * @param id Id of the entity to delete
     * @return {@code true} if entity is deleted, else returns {@code false}
     * @throws PersistenceException If SQLException occurs
     */
    public boolean delete(long id) throws PersistenceException {
        try (PreparedStatement statement = getConnection().prepareStatement(getDeleteByIdQuery())){
            statement.setLong(1, id);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while deleting by id", e);
        }
    }

    /**
     * Returns connection. Used by subclasses to get connection.
     * @return {@link AbstractDao#connection}
     */
    protected Connection getConnection() {
        return connection;
    }
}
