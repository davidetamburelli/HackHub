package repository;

import java.util.List;

public interface IRepository<T> {

    T getById(Long id);

    void save(T item);

    void delete(T item);

    List<T> findAll();

}
