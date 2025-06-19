package ru.mip3x.lab4.utils;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class MBeanRegistryUtil {
    @Inject
    private PointStats pointStats;

    @Inject
    private ClickInterval clickInterval;

    @PostConstruct
    public void init() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            register(pointStats, "lab4.beans:type=PointStats");
            System.out.println("PointStats registered");

            register(clickInterval, "lab4.beans:type=ClickInterval");
            System.out.println("ClickInterval registered");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Set<ObjectName> names = null;
        try {
            names = mbs.queryNames(new ObjectName("lab4.beans:*"), null);
            System.err.println(">>> Registered MBeans: " + names);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Object bean, String name) throws Exception {
        ObjectName objectName = new ObjectName(name);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (!mbs.isRegistered(objectName)) {
            mbs.registerMBean(bean, objectName);
            System.out.println("registered " + name);
        }
    }
}
