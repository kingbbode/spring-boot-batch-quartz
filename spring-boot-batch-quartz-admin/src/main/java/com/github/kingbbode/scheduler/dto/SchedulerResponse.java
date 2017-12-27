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


import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class SchedulerResponse {
    private String name;
    private String version;
    private String jobName;
    private List<CronTrigger> cronTriggerList;
    private List<SimpleTrigger> simpleTriggerList;
    private Map<String, String> params;

    @Getter
    @Builder
    public static class CronTrigger {
        private String name;
        private String cronExpression;
    }

    @Getter
    @Builder
    public static class SimpleTrigger {
        private String name;
        private int repeat;
        private int repeatInterval;
        private String executor;
        private long executeTimeStamp;
        private Map<String, String> params;
    }
}
