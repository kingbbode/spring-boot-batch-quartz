package com.github.kingbbode.autoconfigure.quartz;

import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnClass({JobFactory.class, EnableBatchProcessing.class, SchedulerFactoryBean.class})
public class QuartzBatchAutoConfiguration {
    
    @Configuration
    @ConditionalOnBean(name = "scheduleDataSource")
    public static class JdbcConfiguration {
        @Bean("scheduleTxManager")
        @ConditionalOnBean(PlatformTransactionManager.class)
        public PlatformTransactionManager schedulePrimaryTransactionManager(@Qualifier("scheduleDataSource") DataSource datasource) {
            return new DataSourceTransactionManager(datasource);
        }

        @Bean("scheduleTxManager")
        @ConditionalOnMissingBean(PlatformTransactionManager.class)
        @Primary
        public PlatformTransactionManager scheduleTransactionManager(@Qualifier("scheduleDataSource") DataSource datasource) {
            return new DataSourceTransactionManager(datasource);
        }
        
        @Bean
        public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(@Qualifier("scheduleDataSource") DataSource datasource, @Qualifier("scheduleTxManager") PlatformTransactionManager platformTransactionManager) {
            return schedulerFactoryBean -> {
                schedulerFactoryBean.setDataSource(datasource);
                schedulerFactoryBean.setTransactionManager(platformTransactionManager);
            };
        }

        @Bean
        public BatchConfigurer configurer(@Qualifier("schedulerDataSource") DataSource dataSource) {
            return new DefaultBatchConfigurer(dataSource);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class BatchConfig {
        /**
         * JobRegistry 에 Job 을 자동으로 등록하기 위한 설정.
         *
         * @param jobRegistry ths Spring Batch Job Registry
         * @return JobRegistry BeanPostProcessor
         */
        @Bean
        @ConditionalOnMissingBean
        public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
            JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
            jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
            return jobRegistryBeanPostProcessor;
        }
    }

    @Configuration
    @EnableConfigurationProperties(QuartzProperties.class)
    public static class QuartzConfig {
        private final QuartzProperties quartzProperties;
        private final List<CronTriggerFactoryBean> cronTriggerFactoryBeanList;
        private final List<SchedulerFactoryBeanCustomizer> customizers;

        public QuartzConfig(QuartzProperties quartzProperties, ObjectProvider<List<CronTriggerFactoryBean>> cronTriggerFactoryBeanList, ObjectProvider<List<SchedulerFactoryBeanCustomizer>> customizers) {
            this.quartzProperties = quartzProperties;
            this.cronTriggerFactoryBeanList = cronTriggerFactoryBeanList.getIfAvailable();
            this.customizers = customizers.getIfAvailable();
        }

        @Bean
        @ConditionalOnMissingBean
        public JobFactory jobFactory(ApplicationContext applicationContext) {
            return new AutowireCapableBeanJobFactory(applicationContext);
        }

        @Bean
        public Trigger[] registryTrigger() {
            if(cronTriggerFactoryBeanList == null) {
                return new Trigger[]{};
            }
            return cronTriggerFactoryBeanList.stream().map(CronTriggerFactoryBean::getObject).toArray(Trigger[]::new);
        }
        
        @Bean
        @ConditionalOnMissingBean
        public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, Trigger[] registryTrigger) throws Exception {
            SchedulerFactoryBean factory = new SchedulerFactoryBean();
            factory.setSchedulerName(quartzProperties.getSchedulerName());
            //Register JobFactory
            factory.setJobFactory(jobFactory);
            //Graceful Shutdown 을 위한 설정으로 Job 이 완료될 때까지 Shutdown 을 대기하는 설정
            factory.setWaitForJobsToCompleteOnShutdown(true);
            //Job Detail 데이터 Overwrite 유무
            factory.setOverwriteExistingJobs(true);
            //Register QuartzProperties
            factory.setQuartzProperties(quartzProperties.toProperties());
            //Register Triggers
            if(registryTrigger.length > 0) {
                factory.setTriggers(registryTrigger);
            }
            if(customizers == null) {
                return factory;
            }
            for(SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer : customizers) {
                schedulerFactoryBeanCustomizer.customize(factory);
            }
            return factory;
        }

        /**
         * Spring Framework 의 Shutdown Hook 설정.
         * Quartz 의 Shutdown 동작을 위임받아 Graceful Shutdown 을 보장.
         * Quartz 의 자체 Shutdown Plugin 을 사용하면 Spring 의 Datasource 가 먼저 Close 되므로,
         * Spring 에게 Shutdown 동작을 위임하여, 상위에서 컨트롤.
         *
         * @param schedulerFactoryBean quartz schedulerFactoryBean.
         * @return SmartLifecycle
         */
        @Bean
        public SmartLifecycle gracefulShutdownHookForQuartz(SchedulerFactoryBean schedulerFactoryBean) {
            return new QuartzGracefulShutdownHook(schedulerFactoryBean);
        }
    }
}
