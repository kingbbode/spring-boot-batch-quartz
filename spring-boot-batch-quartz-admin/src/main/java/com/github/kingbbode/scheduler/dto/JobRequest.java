package com.github.kingbbode.scheduler.dto;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {
    private String schedulerName;
    private String version;
    private String jobName;

    public String getMergedSchedulerName() {
        return schedulerName + "_" + version;
    }

    public String getMergedJobName() {
        return getMergedSchedulerName() + "_" + jobName;
    }
}
