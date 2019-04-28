package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface Dao<T extends Entity> {
    Logger logger = LogManager.getLogger();

    List<T> findAll() throws PersistenceException;
    int update(T entity) throws PersistenceException;
    boolean create(T entity) throws PersistenceException;
    T findEntityById(long id) throws PersistenceException;
    String getDeleteByIdQuery();
    boolean delete(long id) throws PersistenceException;
}
