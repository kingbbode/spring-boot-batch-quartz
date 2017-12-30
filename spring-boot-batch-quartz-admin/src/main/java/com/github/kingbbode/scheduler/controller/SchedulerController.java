package com.github.kingbbode.scheduler.controller;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */


import com.github.kingbbode.scheduler.dto.*;
import com.github.kingbbode.scheduler.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
public class SchedulerController {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping("/schedulers")
    public List<SchedulerResponse> schedulers() {
        return schedulerService.getSchedulerList();
    }

    @GetMapping("/schedulers/{schedulerName}/versions/{version}/jobs/{jobName}")
    public SchedulerResponse schedulerDetail(JobRequest jobRequest) {
        return schedulerService.getSchedulerDetail(jobRequest);
    }
    
    @PostMapping("/schedulers/{schedulerName}/versions/{version}/jobs/{jobName}/triggers/{triggerName}")
    public ResponseEntity<String> alterJob(JobTriggerRequest jobTriggerRequest, @RequestBody JobTriggerInfo jobTriggerInfo) {
        schedulerService.updateTrigger(jobTriggerRequest, jobTriggerInfo);
        return ResponseEntity.ok("Success");
    }
    
    @PostMapping("/schedulers/{schedulerName}/versions/{version}/jobs/{jobName}/execute")
    public ResponseEntity<String> execute(JobRequest jobRequest, @RequestBody JobExecuteInfo jobExecuteInfo) {
        schedulerService.executeJob(jobRequest, jobExecuteInfo);
        return ResponseEntity.ok("Success");
    }
}
