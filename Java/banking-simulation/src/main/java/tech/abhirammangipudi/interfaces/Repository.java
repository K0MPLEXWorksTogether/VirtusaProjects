package tech.abhirammangipudi.interfaces;

import java.util.List;

import tech.abhirammangipudi.errors.ResourceNotFoundException;

public interface Repository<T, ID> {
    void save(T entity);

    T findById(ID id) throws ResourceNotFoundException;

    List<T> findAll(int page, int limit);

    void update(ID id, T entity) throws ResourceNotFoundException;

    void deleteById(ID id) throws ResourceNotFoundException;
}
