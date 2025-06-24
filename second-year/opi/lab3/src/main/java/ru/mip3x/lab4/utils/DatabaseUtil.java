package ru.mip3x.lab4.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class for managing JPA {@link EntityManager} instances
 * <p>
 * Provides access to the {@link EntityManagerFactory} configured
 * in {@code persistence.xml} under the name "default"
 */
public class DatabaseUtil {
    /** 
     * Singleton instance of {@link EntityManagerFactory} 
     * initialized with the persistence unit "default"
     */
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    /**
     * Returns a new {@link EntityManager} instance
     *
     * @return a new {@link EntityManager}
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
