package ru.mip3x.lab4.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import ru.mip3x.lab4.beans.PointStats;
import ru.mip3x.lab4.beans.ClickInterval;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

@Startup
@Singleton
public class MBeanRegistryUtil {
    @Inject
    private PointStats pointStats;

    @Inject
    private ClickInterval clickInterval;

    private static final String PS_NAME = "lab4.beans:type=PointStats";
    private static final String CI_NAME = "lab4.beans:type=ClickInterval";

    private final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    @PostConstruct
    public void init() {
        try {
            register(pointStats, PS_NAME);
            System.out.println("PointStats registered");

            register(clickInterval, CI_NAME);
            System.out.println("ClickInterval registered");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Set<ObjectName> names = null;
        try {
            names = mbs.queryNames(new ObjectName("lab4.beans:*"), null);
            System.out.println(">>> Registered MBeans: " + names);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        try { unregister(PS_NAME); } catch (Exception ignored) {}
        try { unregister(CI_NAME); } catch (Exception ignored) {}
    }

    private void register(Object bean, String name) throws Exception {
        ObjectName objectName = new ObjectName(name);
        if (mbs.isRegistered(objectName)) {
            mbs.unregisterMBean(objectName);
        }
        mbs.registerMBean(bean, objectName);
        System.out.println("registered " + name);
    }

    private void unregister(String name) throws Exception {
        ObjectName on = new ObjectName(name);
        if (mbs.isRegistered(on)) {
            mbs.unregisterMBean(on);
        }
    }
}
