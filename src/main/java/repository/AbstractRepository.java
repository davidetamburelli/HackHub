package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public abstract class AbstractRepository<T> implements IRepository<T> {

    protected final EntityManager em;
    private final Class<T> entityClass;

    protected AbstractRepository(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public T getById(Long id) {
        if (id == null) return null;
        return em.find(entityClass, id);
    }

    @Override
    public void save(T item) {
        if (item != null) {
            em.merge(item);
        }
    }

    @Override
    public void delete(T item) {
        if (item == null) return;
        Object managedItem = em.contains(item) ? item : em.merge(item);
        em.remove(managedItem);
    }

    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " +  entityClass.getSimpleName() + " e";
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        return query.getResultList();
    }

}
