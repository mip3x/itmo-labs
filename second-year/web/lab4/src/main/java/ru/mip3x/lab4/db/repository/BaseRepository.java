package ru.mip3x.lab4.db.repository;

import jakarta.persistence.EntityManager;
import ru.mip3x.lab4.utils.DatabaseUtil;

import java.util.List;

public abstract class BaseRepository<T, ID> {
    private final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

    public void update(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        }
    }

    public void delete(T entity) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        }
    }

    public T findById(ID id) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    public List<T> findAll() {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        }
    }
}
