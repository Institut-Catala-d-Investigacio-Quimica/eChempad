package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface GenericService<T, S extends Serializable> {

    T saveOrUpdate(T entity);

    T update(T entity, S id) throws ExceptionResourceNotExists;

    Set<T> getAll();

    T get(S id) throws ExceptionResourceNotExists;

    void add(T entity);

    void remove(S id) throws ExceptionResourceNotExists;

}
