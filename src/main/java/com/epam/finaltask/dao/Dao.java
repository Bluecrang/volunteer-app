package com.epam.finaltask.dao;

import com.epam.finaltask.entity.Entity;

import java.util.List;

public interface Dao<T extends Entity> {
    List<T> findAll();
    T findEntityById(long id);
    boolean create(T entity);
    boolean delete(long id);
    boolean update(T entity);
}
