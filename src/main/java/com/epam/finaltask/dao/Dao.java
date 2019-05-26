package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Interface that provides basic DAO methods.
 * @param <T> Entity to work with
 */
public interface Dao<T extends Entity> {
    Logger logger = LogManager.getLogger();

    /**
     * Finds all entities.
     * @return All entities from the database
     * @throws PersistenceException if SQLException is thrown
     */
    List<T> findAll() throws PersistenceException;

    /**
     * Updates entity.
     * @param entity Entity to update
     * @return Number of records updated
     * @throws PersistenceException If SQLException is thrown
     */
    int update(T entity) throws PersistenceException;

    /**
     * Adds entity to the database.
     * @param entity Entity to add
     * @return {@code true} if entity was successfully added, else returns {@code false}
     * @throws PersistenceException If SQLException is thrown
     */
    boolean create(T entity) throws PersistenceException;

    /**
     * Find entity by id.
     * @param id Id of the entity to find
     * @return Entity if it was found, else returns null
     * @throws PersistenceException If SQLException is thrown
     */
    T findEntityById(long id) throws PersistenceException;

    /**
     * Returns query of entity deletion by id.
     * @return query of entity deletion by id
     */
    String getDeleteByIdQuery();

    /**
     * Removes entity from the database by id.
     * @param id Id of the entity to remove
     * @return {@code true} if entity was successfully deleted, else returns null
     * @throws PersistenceException If SQLException is thrown
     */
    boolean delete(long id) throws PersistenceException;
}
