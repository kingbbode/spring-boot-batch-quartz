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


import com.github.kingbbode.scheduler.domain.QrtzTriggers;
import com.github.kingbbode.scheduler.domain.QrtzTriggersId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface QuartzTriggersRepository extends Repository<QrtzTriggers, QrtzTriggersId> {
    Optional<QrtzTriggers> findByIdSchedNameAndIdTriggerNameAndJobNameAndTriggerType(String schedName, String triggerName, String jobName, String type);

    void save(QrtzTriggers qrtzTriggers);
}
