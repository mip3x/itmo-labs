package ru.mip3x.lab4.utils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import ru.mip3x.lab4.beans.PointStatsBean;
import ru.mip3x.lab4.beans.ClickIntervalBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@ApplicationScoped
public class MBeanRegistryUtil {

    @PostConstruct
    public void init() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            ObjectName name1 = new ObjectName("ru.mip3x.lab4.beans:type=PointStats");
            if (!mbs.isRegistered(name1)) {
                mbs.registerMBean(new PointStatsBean(), name1);
                System.out.println("registered PointStatsMBean");
            }

            ObjectName name2 = new ObjectName("ru.mip3x.lab4.beans:type=ClickInterval");
            if (!mbs.isRegistered(name2)) {
                System.out.println("registered ClickIntervalMBean");
                mbs.registerMBean(new ClickIntervalBean(), name2);
            }

        } catch (Exception e) {
            System.out.println("Cant register MBeans");
            throw new RuntimeException("Не удалось зарегистрировать MBean’ы", e);
        }
    }
}
