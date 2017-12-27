package com.github.kingbbode.scheduler.service;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */

import com.github.kingbbode.application.exceptions.NotFoundException;
import com.github.kingbbode.scheduler.domain.QrtzJobDetails;
import com.github.kingbbode.scheduler.domain.QrtzSimpleTriggersHistory;
import com.github.kingbbode.scheduler.domain.QrtzTriggers;
import com.github.kingbbode.scheduler.domain.QrtzTriggersId;
import com.github.kingbbode.scheduler.dto.*;
import com.github.kingbbode.scheduler.repository.QuartzJobDetailsRepository;
import com.github.kingbbode.scheduler.repository.QuartzTriggersHistoryRepository;
import com.github.kingbbode.scheduler.repository.QuartzTriggersRepository;
import com.github.kingbbode.scheduler.utils.JobDataMapConverter;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SchedulerService {
    
    @Autowired
    private QuartzJobDetailsRepository quartzJobDetailsRepository;

    @Autowired
    private QuartzTriggersRepository quartzTriggersRepository;

    @Autowired
    private QuartzTriggersHistoryRepository quartzTriggersHistoryRepository;

    public List<SchedulerResponse> getSchedulerList() {
        return this.quartzJobDetailsRepository.findAll()
                .map(QrtzJobDetails::toSchedulerResponse)
                .collect(Collectors.toList());
    }

    public SchedulerResponse getSchedulerDetail(JobRequest jobRequest) {
        QrtzJobDetails jobDetails =  this.quartzJobDetailsRepository.findByIdSchedNameAndIdJobName(
                jobRequest.getMergedSchedulerName(), jobRequest.getMergedJobName()
        ).orElseThrow(NotFoundException::new);
        SchedulerResponse schedulerDetailResponse = jobDetails.toSchedulerDetailResponse();
        schedulerDetailResponse.setSimpleTriggerList(
                this.quartzTriggersHistoryRepository.findBySchedNameAndJobName(jobRequest.getMergedSchedulerName(), jobRequest.getMergedJobName())
                        .map(QrtzSimpleTriggersHistory::toSimpleTrigger)
                        .collect(Collectors.toList()));
        return schedulerDetailResponse;
    }

    public void updateTrigger(JobTriggerRequest jobTriggerRequest, JobTriggerInfo jobTriggerInfo) {
        QrtzTriggers qrtzTriggers = this.quartzTriggersRepository.findByIdSchedNameAndIdTriggerNameAndJobNameAndTriggerType(
                jobTriggerRequest.getMergedSchedulerName(), 
                jobTriggerRequest.getTriggerName(), 
                jobTriggerRequest.getMergedJobName(), 
                "CRON").orElseThrow(NotFoundException::new);
        qrtzTriggers.getQrtzCronTriggers().updateCron(jobTriggerInfo.getCronExpression());
    }

    public void executeJob(JobRequest jobRequest, JobExecuteInfo jobExecuteRequest) {
        LocalDateTime now = LocalDateTime.now();
        JobDataMap jobDataMap = JobDataMapConverter.convertMapToForceJobData(jobExecuteRequest.getParameters());
        saveTrigger(jobRequest, jobExecuteRequest, now, jobDataMap);
        saveHistory(jobRequest, jobExecuteRequest, now, jobDataMap);
    }
    
    private void saveHistory(JobRequest jobRequest, JobExecuteInfo jobExecuteRequest, LocalDateTime now, JobDataMap jobDataMap) {
        QrtzSimpleTriggersHistory history = QrtzSimpleTriggersHistory.builder()
                .schedName(jobRequest.getMergedSchedulerName())
                .jobName(jobRequest.getMergedJobName())
                .triggerName("OneTimeTrigger-" + now.toString())
                .repeat(jobExecuteRequest.getRepeatCount())
                .repeatInterval(jobExecuteRequest.getRepeatInterval())
                .executor("admin")
                .executeDateTime(now)
                .jobDataMap(jobDataMap)
                .build();
        this.quartzTriggersHistoryRepository.save(history);
    }

    private void saveTrigger(JobRequest jobRequest, JobExecuteInfo jobExecuteRequest, LocalDateTime now, JobDataMap jobDataMap) {
        QrtzTriggersId qrtzTriggersId = QrtzTriggersId.builder()
                .schedName(jobRequest.getMergedSchedulerName())
                .triggerName("OneTimeTrigger-" + now.toString())
                .triggerGroup("OneTimeTrigger")
                .build();

        QrtzTriggers qrtzTriggers = new QrtzTriggers.SimpleTriggerBuilder()
                .id(qrtzTriggersId)
                .jobName(jobRequest.getMergedJobName())
                .jobGroup("DEFAULT")
                .repeatCount(jobExecuteRequest.getRepeatCount())
                .repeatInterval(jobExecuteRequest.getRepeatInterval())
                .param(jobDataMap)
                .startTime(now)
                .build();

        this.quartzTriggersRepository.save(qrtzTriggers);
    }
}
