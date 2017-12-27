/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-08-02
 */
package com.github.kingbbode.autoconfigure.quartz;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class QuartzGracefulShutdownHook implements SmartLifecycle {
    private boolean isRunning = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SchedulerFactoryBean schedulerFactoryBean;

    public QuartzGracefulShutdownHook(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        logger.info("Spring container is shutting down.");
        callback.run();
    }

    @Override
    public void start() {
        logger.info("Quartz Graceful Shutdown Hook started.");
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
        try {
            logger.info("Quartz Graceful Shutdown... ");
            schedulerFactoryBean.destroy();
        } catch (SchedulerException e) {
            try {
                logger.info(
                        "Error shutting down Quartz: " + e.getMessage(), e);
                schedulerFactoryBean.getScheduler().shutdown(false);
            } catch (SchedulerException ex) {
                logger.error("Unable to shutdown the Quartz scheduler.", ex);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
