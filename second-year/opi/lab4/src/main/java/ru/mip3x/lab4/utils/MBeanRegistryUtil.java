package ru.mip3x.lab4.utils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.mip3x.lab4.beans.PointStatsBean;
import ru.mip3x.lab4.beans.ClickIntervalBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@ApplicationScoped
public class MBeanRegistryUtil {
    @Inject
    private PointStatsBean pointStats;

    @Inject
    private ClickIntervalBean clickInterval;

    @PostConstruct
    public void init() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            register(pointStats, "lab4.beans:type=PointStats");
            register(clickInterval, "lab4.beans:type=ClickInterval");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Object bean, String name) throws Exception {
        ObjectName on = new ObjectName(name);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (!mbs.isRegistered(on)) {
            mbs.registerMBean(bean, on);
            System.out.println("registered " + name);
        }
    }
}
