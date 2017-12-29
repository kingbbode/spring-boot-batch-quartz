/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-08-02
 */

package com.github.kingbbode.utils.batch;

import com.github.kingbbode.autoconfigure.quartz.BatchJobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The type Batch helper.
 */
@Slf4j
public class BatchHelper {

    private static final String JOB_NAME_KEY = "jobName";
    private static final String QUARTZ_INSTANCE_ID_KEY = "instanceId";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String JOB_PARAMETERS_NAME_KEY = "jobParameters";
    private static final String FORCE_JOB_PARAMETERS_NAME_KEY = "forceJobParameters";
    private static final String TOKEN = "_";
    private static final List<String> KEYWORDS = Arrays.asList(JOB_NAME_KEY, JOB_PARAMETERS_NAME_KEY);

    /**
     * JobDetailFactoryBean 용 Builder 생성
     *
     * @return JobDetailFactoryBeanBuilder job detail factory bean builder
     * @see JobDetailFactoryBeanBuilder
     */
    public static JobDetailFactoryBeanBuilder jobDetailFactoryBeanBuilder() {
        return new JobDetailFactoryBeanBuilder();
    }

    /**
     * CronTriggerFactoryBean 용 Builder 생성
     *
     * @return CronTriggerFactoryBeanBuilder cron trigger factory bean builder
     * @see CronTriggerFactoryBeanBuilder
     */
    public static CronTriggerFactoryBeanBuilder cronTriggerFactoryBeanBuilder() {
        return new CronTriggerFactoryBeanBuilder();
    }

    /**
     * quartz JobDataMap 로부터 job Name 을 추출
     *
     * @param jobDataMap quartz JobDataMap
     * @return job name
     */
    public static String getJobName(JobDataMap jobDataMap){
        return (String) jobDataMap.get(JOB_NAME_KEY);
    }

    /**
     * quartz JobDataMap 로부터 JobParameters 를 추출
     * <p>
     * Spring Batch Job 은 Job Name 과 Job Parameter 로 동일 잡을 확인하므로,
     * 실행 시간을 적재하여 새로운 Job Parameter 를 생성하여 반환.
     *
     * @param context quartz JobDataMap
     * @return Spring Batch JobParameters
     * @throws SchedulerException the scheduler exception
     */
    public static JobParameters getJobParameters(JobExecutionContext context) throws SchedulerException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSSSSS Z");
        String now = ZonedDateTime.now().format(formatter);
        JobParameters jobParameters  =  new JobParametersBuilder(mergeJobParameters(jobDataMap))
                .addString(QUARTZ_INSTANCE_ID_KEY, context.getScheduler().getSchedulerInstanceId())
                //springbatch job instance는 jobinstance 아이디 + 파라미터이기 때문에
                //잡 파라미터를 변경할 경우 충돌이 발생할수 있다 그래서
                //시작시 파라미터에 타임 스탬프를 추가 한다.
                .addString(TIMESTAMP_KEY, context.getJobDetail().getKey().getName() + "_" + now)
                .toJobParameters();

        log.info("=" + QUARTZ_INSTANCE_ID_KEY+"========" + context.getScheduler().getSchedulerInstanceId());
        log.info("=" + TIMESTAMP_KEY+"========" + context.getJobDetail().getKey().getName() + "_" + now);

        return jobParameters;
    }

    /**
     * 최초 세팅된 Paramaers와 동적으로 들어온 Parameters를 합침.
     * @param jobDataMap Quartz JobDataMap
     * @return JobParameters
     */
    private static JobParameters mergeJobParameters(JobDataMap jobDataMap) {
        JobParameters initJobParameters = (JobParameters) jobDataMap.get(JOB_PARAMETERS_NAME_KEY);
        JobParameters forceJobParameters = (JobParameters) jobDataMap.get(FORCE_JOB_PARAMETERS_NAME_KEY);
        if(ObjectUtils.isEmpty(forceJobParameters)) {
            return initJobParameters;
        }
        Map<String, JobParameter> merged = new HashMap<>();
        merged.putAll(initJobParameters.getParameters());
        merged.putAll(forceJobParameters.getParameters());
        return new JobParameters(merged);
    }


    /**
     * JobDetailFactoryBean Builder
     * <p>
     * JobDetailFactoryBean 내부 설정 값과 map, JobParametersBuilder 를 조합하여 JobDetailFactoryBean 을 생성
     *
     * @see JobDetailFactoryBean
     * @see JobParametersBuilder
     */
    public static class JobDetailFactoryBeanBuilder {

        /**
         * The Durability.
         */
        boolean durability = true;
        /**
         * The Requests recovery.
         */
        boolean requestsRecovery = true;
        private Map<String, Object> map;
        private JobParametersBuilder jobParametersBuilder;

        /**
         * Instantiates a new Job detail factory bean builder.
         */
        JobDetailFactoryBeanBuilder() {
            this.map = new HashMap<>();
            this.jobParametersBuilder = new JobParametersBuilder();
        }

        /**
         * Job job detail factory bean builder.
         *
         * @param job Spring Batch Job For Name
         * @return the job detail factory bean builder
         */
        public JobDetailFactoryBeanBuilder job(Job job) {
            this.map.put(JOB_NAME_KEY, job.getName());
            return this;
        }

        /**
         * Durability job detail factory bean builder.
         *
         * @param durability 작업의 비 지속성 여부. false 인 경우 트리거가 연결되지 않으면 자동 삭제. ( default true )
         * @return the job detail factory bean builder
         */
        public JobDetailFactoryBeanBuilder durability(boolean durability){
            this.durability = durability;
            return this;
        }

        /**
         * Requests recovery job detail factory bean builder.
         *
         * @param requestsRecovery 작업의 복구 여부. (실패한 작업에 대한 재실행) ( default true )
         * @return the job detail factory bean builder
         */
        public JobDetailFactoryBeanBuilder requestsRecovery(boolean requestsRecovery){
            this.requestsRecovery = requestsRecovery;
            return this;
        }

        /**
         * Spring Batch Job 으로 전달할 Job Parameter
         *
         * @param key   job parameter key
         * @param value job parameter value
         * @return the job detail factory bean builder
         */
        public JobDetailFactoryBeanBuilder parameter(String key, Object value){
            if(KEYWORDS.contains(key)){
                throw new RuntimeException("Invalid Parameter.");
            }
            this.addParameter(key, value);
            return this;
        }

        private void addParameter(String key, Object value) {
            if (value instanceof String) {
                this.jobParametersBuilder.addString(key, (String) value);
                return;
            } else if (value instanceof Float || value instanceof Double) {
                this.jobParametersBuilder.addDouble(key, ((Number) value).doubleValue());
                return;
            } else if (value instanceof Integer || value instanceof Long) {
                this.jobParametersBuilder.addLong(key, ((Number) value).longValue());
                return;
            } else if (value instanceof Date) {
                this.jobParametersBuilder.addDate(key, (Date) value);
                return;
            } else if (value instanceof JobParameter) {
                this.jobParametersBuilder.addParameter(key, (JobParameter) value);
                return;
            }
            throw new RuntimeException("Not Supported Parameter Type.");
        }

        /**
         * Build job detail factory bean.
         *
         * @return the job detail factory bean
         */
        public JobDetailFactoryBean build(){
            if(!map.containsKey(JOB_NAME_KEY)) {
                throw new RuntimeException("Not Found Job Name.");
            }
            map.put(JOB_PARAMETERS_NAME_KEY, jobParametersBuilder.toJobParameters());

            JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
            jobDetailFactory.setName((String) map.get(JOB_NAME_KEY));
            jobDetailFactory.setJobClass(BatchJobExecutor.class);
            jobDetailFactory.setDurability(this.durability);
            jobDetailFactory.setRequestsRecovery(this.requestsRecovery);
            jobDetailFactory.setJobDataAsMap(this.map);
            return jobDetailFactory;
        }
    }

    /**
     * CronTriggerFactoryBean Builder
     * <p>
     * CronTriggerFactoryBean 내부 설정 값을 조합하여 CronTriggerFactoryBean 을 생성
     *
     * @see CronTriggerFactoryBean
     */
    public static class CronTriggerFactoryBeanBuilder {
        private String name;
        private String cronExpression;
        private JobDetailFactoryBean jobDetailFactoryBean;

        /**
         * 작성되지 않으면, bean Name 을 사용
         *
         * @param name the name
         * @return the cron trigger factory bean builder
         * @see CronTriggerFactoryBean#setName(String) CronTriggerFactoryBean#setName(String)
         */
        public CronTriggerFactoryBeanBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Cron expression cron trigger factory bean builder.
         *
         * @param cronExpression Quartz 전용 Cron 양식의 Expression
         * @return the cron trigger factory bean builder
         * @link http ://www.cronmaker.com
         */
        public CronTriggerFactoryBeanBuilder cronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        /**
         * Job detail factory bean cron trigger factory bean builder.
         *
         * @param jobDetailFactoryBean Quartz Schedule Job Detail Factory
         * @return the cron trigger factory bean builder
         */
        public CronTriggerFactoryBeanBuilder jobDetailFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
            this.jobDetailFactoryBean = jobDetailFactoryBean;
            return this;
        }

        /**
         * Build cron trigger factory bean.
         *
         * @return the cron trigger factory bean
         */
        public CronTriggerFactoryBean build() {
            if(this.cronExpression == null || this.jobDetailFactoryBean == null){
                throw new RuntimeException("cronExpression and jobDetailFactoryBean is required.");
            }
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setName(this.name);
            cronTriggerFactoryBean.setJobDetail(this.jobDetailFactoryBean.getObject());
            cronTriggerFactoryBean.setCronExpression(this.cronExpression);
            return cronTriggerFactoryBean;
        }
    }


    /**
     * Quartz Job 이름을 생성 한다 .
     * Job 이름은 충돌을 피하기 위해서 Class 전체 이름으로 하고 끝에 구분자 "_JOB"으로 한다.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String createJobName(String schedulerName, Class clazz) {
        return schedulerName + TOKEN + createJobOrStepName(clazz);
    }

    /**
     * Step 명을 지정 한다.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String createStepName(Class clazz) {
        return createJobOrStepName(clazz);
    }

    private static String createJobOrStepName (Class clazz) {
        return clazz.getSimpleName().split("\\$")[0].replace("Config", "").replace("Configuration", "");
    }
}