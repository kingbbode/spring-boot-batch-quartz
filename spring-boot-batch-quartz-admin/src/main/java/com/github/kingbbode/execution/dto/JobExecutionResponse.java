package com.github.kingbbode.execution.dto;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */


import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class JobExecutionResponse {
    private Long id;
    private Long createTIme;
    private Long endTime;
    private String status;
    private Map<String, String> parameters;
}
