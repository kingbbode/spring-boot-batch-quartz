package com.github.kingbbode.scheduler.repository;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */


import com.github.kingbbode.scheduler.domain.QrtzJobDetails;
import com.github.kingbbode.scheduler.domain.QrtzJobDetailsId;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface QuartzJobDetailsRepository extends Repository<QrtzJobDetails, QrtzJobDetailsId> {
    Optional<QrtzJobDetails> findByIdSchedNameAndIdJobName(String SchedulerName, String jobName);

    Stream<QrtzJobDetails> findAll();
}
