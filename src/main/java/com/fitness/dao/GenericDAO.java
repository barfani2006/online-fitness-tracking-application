package com.fitness.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface demonstrating generics, collections, and standard CRUD contracts.
 *
 * @param <T> entity type
 * @param <ID> identifier type
 */
public interface GenericDAO<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    T save(T entity);

    boolean update(T entity);

    boolean deleteById(ID id);
}
