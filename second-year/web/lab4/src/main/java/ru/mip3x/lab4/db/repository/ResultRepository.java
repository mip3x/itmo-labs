package ru.mip3x.lab4.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.mip3x.lab4.db.model.Result;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.utils.DatabaseUtil;

import java.util.List;

public class ResultRepository extends BaseRepository<Result, Long> {
    public ResultRepository() {
        super(Result.class);
    }

    public List<Result> findResultsByUser(User user) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            TypedQuery<Result> query = em.createQuery("SELECT r FROM Result r WHERE r.user = :input", Result.class);
            query.setParameter("input", user);
            return query.getResultList();
        }
    }
}
