package ru.mip3x.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class L2CacheStatsAspect {
    private final SessionFactory sessionFactory;

    private final Environment env;

    public L2CacheStatsAspect(EntityManagerFactory emf, Environment env) {
        this.sessionFactory = emf.unwrap(SessionFactory.class);
        this.env = env;
    }

    @Around("@annotation(ru.mip3x.cache.LogL2CacheStats) || @within(ru.mip3x.cache.LogL2CacheStats)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        boolean enabled = Boolean.parseBoolean(
                env.getProperty("app.cache.stats-logging-enabled", "false")
        );

        if (!enabled)
            return pjp.proceed();

        Statistics stats = sessionFactory.getStatistics();

        // taking snapshot before
        long hitBefore = stats.getSecondLevelCacheHitCount();
        long missBefore = stats.getSecondLevelCacheMissCount();
        long putBefore = stats.getSecondLevelCachePutCount();

        // executing method
        Object result = pjp.proceed();

        // taking snapshot after
        long hitAfter = stats.getSecondLevelCacheHitCount();
        long missAfter = stats.getSecondLevelCacheMissCount();
        long putAfter = stats.getSecondLevelCachePutCount();

        // delta
        long hitDelta = hitAfter - hitBefore;
        long missDelta = missAfter - missBefore;
        long putDelta = putAfter - putBefore;

        log.info(
            "L2Cache BEFORE | hit={} miss={} put={}",
            hitBefore, missBefore, putBefore
        );

        log.info(
            "L2Cache AFTER | hit={} miss={} put={}",
            hitAfter, missAfter, putAfter
        );

        log.info(
            "L2Cache DELTA | method={} | hit=+{} miss=+{} put=+{}",
            pjp.getSignature().toShortString(),
            hitDelta, missDelta, putDelta
        );

        return result;
    }
}
