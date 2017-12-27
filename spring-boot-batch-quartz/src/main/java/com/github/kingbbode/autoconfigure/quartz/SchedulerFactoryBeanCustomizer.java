package com.github.kingbbode.autoconfigure.quartz;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@FunctionalInterface
public interface SchedulerFactoryBeanCustomizer {
    void customize(SchedulerFactoryBean schedulerFactoryBean);
}
