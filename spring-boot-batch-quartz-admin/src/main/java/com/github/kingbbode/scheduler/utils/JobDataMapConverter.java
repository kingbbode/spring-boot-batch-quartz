package com.github.kingbbode.scheduler.utils;

import org.quartz.JobDataMap;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class JobDataMapConverter {

    private static final String JOB_PARAMETERS_NAME_KEY = "jobParameters";
    private static final String FORCE_JOB_PARAMETERS_NAME_KEY = "forceJobParameters";
    private static final JobParameters EMPTY_JOB_PARAMETERS = new JobParametersBuilder().toJobParameters();

    public static JobDataMap convertMapToForceJobData(Map<String, String> parameters) {
        JobParametersBuilder builder = new JobParametersBuilder();
        parameters.forEach(builder::addString);
        return new JobDataMap(Collections.singletonMap(FORCE_JOB_PARAMETERS_NAME_KEY, builder.toJobParameters()));
    }

    public static Map<String, String> convertJobDataToDefaultMap(JobDataMap jobDataMap) { 
        return convertJobDataToMap(JOB_PARAMETERS_NAME_KEY, jobDataMap);
    }
    
    public static Map<String, String> convertJobDataToForceMap(JobDataMap jobDataMap) {
        return convertJobDataToMap(FORCE_JOB_PARAMETERS_NAME_KEY, jobDataMap);
    }
    
    public static Map<String, String> convertJobDataToMap(String key, JobDataMap jobDataMap) {
        return ((JobParameters)jobDataMap.getOrDefault(key, EMPTY_JOB_PARAMETERS)).getParameters()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, o-> o.getValue().toString()));
    }
}
