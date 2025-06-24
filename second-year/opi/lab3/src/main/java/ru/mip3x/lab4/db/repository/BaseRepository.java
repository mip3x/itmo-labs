package ru.mip3x.lab4.db.repository;

import jakarta.persistence.EntityManager;
import ru.mip3x.lab4.utils.DatabaseUtil;

import java.util.List;

/**
 * A generic base repository providing common CRUD operations for any JPA entity
 *
 * @param <T>  the entity type
 * @param <ID> the identifier type
 */
public abstract class BaseRepository<T, ID> {
    private final Class<T> entityClass;

    /**
     * Constructs a repository for a specific entity class
     *
     * @param entityClass the class of the entity
     */
    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Persists a new entity in the database
     *
     * @param entity the entity to save
     */
    public void save(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

     /**
     * Updates an existing entity in the database
     *
     * @param entity the entity to update
     */
    public void update(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        }
    }

    /**
     * Deletes an entity from the database
     *
     * @param entity the entity to delete
     */
    public void delete(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        }
    }

    /**
     * Finds an entity by its identifier
     *
     * @param id the ID of the entity
     * @return the found entity, or null if not found
     */
    public T findById(ID id) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    /**
     * Retrieves all entities of the given type from the database
     *
     * @return a list of all entities
     */
    public List<T> findAll() {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        }
    }
}
