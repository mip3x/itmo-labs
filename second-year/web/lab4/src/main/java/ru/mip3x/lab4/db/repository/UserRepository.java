package ru.mip3x.lab4.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.utils.DatabaseUtil;

/**
 * Repository class for managing {@link User} entities
 */
public class UserRepository extends BaseRepository<User, Long> {
    /**
     * Constructs a new UserRepository
     */
    public UserRepository() {
        super(User.class);
    }

    /**
     * Finds a user by their username
     *
     * @param username the username to search for
     * @return the {@link User} if found, otherwise {@code null}
     */
    public User findByUsername(String username) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :input", User.class);
            query.setParameter("input", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
