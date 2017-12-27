package com.github.kingbbode.execution.controller;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */

import com.github.kingbbode.execution.dto.ExecutionDetailRequest;
import com.github.kingbbode.execution.dto.JobExecutionResponse;
import com.github.kingbbode.execution.dto.StepExecutionResponse;
import com.github.kingbbode.execution.service.JobExecutionService;
import com.github.kingbbode.scheduler.dto.JobRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobController {
    
    private final JobExecutionService jobExecutionService;

    @Autowired
    public JobController(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    @GetMapping("/schedulers/{schedulerName}/versions/{version}/jobs/{jobName}/executions")
    public List<JobExecutionResponse> jobsExecutions(JobRequest jobRequest) {
        return jobExecutionService.getExecutionList(jobRequest);
    }

    @GetMapping("/schedulers/{schedulerName}/versions/{version}/jobs/{jobName}/executions/{executionId}/steps")
    public List<StepExecutionResponse> jobsExecutionsDetail(ExecutionDetailRequest executionDetailRequest) {
        return jobExecutionService.getStepExecutionList(executionDetailRequest);
    }
}
