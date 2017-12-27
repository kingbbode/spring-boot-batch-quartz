package com.github.kingbbode.autoconfigure.quartz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

@Setter
@Getter
@ConfigurationProperties("quartz.properties")
public class QuartzProperties {
    private static final String PREFIX = "org.quartz";
    private static final String DEFAULT_SCHEDULER_INSTANCE_ID = "AUTO";
    private static final String DEFAULT_SCHEDULER_NAME = "SIMPLE";
    private static final String DEFAULT_SCHEDULER_VERSION = "1.0.0";
    private static final String DEFAULT_SCHEDULER_MAKE_SCHEDULER_TREAD_DAEMON = "true";
    private static final String DEFAULT_SCHEDULER_INTERRUPT_JOBS_ON_SHUTDOWN = "true";
    private static final String DEFAULT_JOB_STORE_CLUSTER_CHECK_IN_INTERVAL = "20000";
    private static final String DEFAULT_JOB_STORE_DRIVER_DELEGATE_CLASS = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
    private static final String DEFAULT_JOB_STORE_IS_CLUSTERED = "true";
    private static final String DEFAULT_JOB_STORE_MISFIRE_THRESHOLD = "60000";
    private static final String DEFAULT_JOB_STORE_TABLE_PREFIX = "QRTZ_";
    private static final String DEFAULT_JOB_STORE_USE_PROPERTIES = "false";
    private static final String DEFAULT_THREAD_POOL_THREADS_COUNT = "10";
    private static final String DEFAULT_THREAD_POOL_MAKE_THREADS_DAEMONS = "true";

    private Scheduler scheduler;

    private JobStore jobStore;

    private ThreadPool threadPool;

    @Setter
    @Getter
    public static class Scheduler {
        private String instanceId = DEFAULT_SCHEDULER_INSTANCE_ID;
        private String makeSchedulerThreadDaemon = DEFAULT_SCHEDULER_MAKE_SCHEDULER_TREAD_DAEMON;
        private String interruptJobsOnShutdown = DEFAULT_SCHEDULER_INTERRUPT_JOBS_ON_SHUTDOWN;
        private String name = DEFAULT_SCHEDULER_NAME;
        private String version = DEFAULT_SCHEDULER_VERSION;
    }

    @Setter
    public static class JobStore {
        private String clusterCheckinInterval = DEFAULT_JOB_STORE_CLUSTER_CHECK_IN_INTERVAL;
        private String driverDelegateClass = DEFAULT_JOB_STORE_DRIVER_DELEGATE_CLASS;
        private String isClustered = DEFAULT_JOB_STORE_IS_CLUSTERED;
        private String misfireThreshold = DEFAULT_JOB_STORE_MISFIRE_THRESHOLD;
        private String tablePrefix = DEFAULT_JOB_STORE_TABLE_PREFIX;
        private String useProperties = DEFAULT_JOB_STORE_USE_PROPERTIES;
    }

    @Setter
    public static class ThreadPool {
        private String threadCount = DEFAULT_THREAD_POOL_THREADS_COUNT;
        private String makeThreadsDaemons = DEFAULT_THREAD_POOL_MAKE_THREADS_DAEMONS;
    }

    public Properties toProperties() throws IllegalAccessException {
        Properties properties = new Properties();
        findProperties(PREFIX, this, properties);
        return properties;
    }
    
    public String getSchedulerName() {
        return this.scheduler.name + "-" + this.scheduler.version;
    }

    private void findProperties(String prefix, Object object, Properties properties) {
        Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        putStringProperties(prefix, object, properties, field);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void putStringProperties(String prefix, Object object, Properties properties, Field field) throws IllegalAccessException {
        if(String.class == field.getType()){
            properties.put(prefix + "." + field.getName(), field.get(object));
            return;
        }
        findProperties(prefix + "." + field.getName(), field.get(object), properties);
    }
}