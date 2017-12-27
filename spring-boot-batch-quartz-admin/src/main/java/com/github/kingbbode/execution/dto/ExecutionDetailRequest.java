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

import com.github.kingbbode.scheduler.dto.JobRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionDetailRequest extends JobRequest {
    private String executionId;
}
