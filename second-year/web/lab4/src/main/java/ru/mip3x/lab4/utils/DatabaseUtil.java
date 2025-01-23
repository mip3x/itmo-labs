package ru.mip3x.lab4.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseUtil {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}