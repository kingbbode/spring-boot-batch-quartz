package com.github.kingbbode.scheduler.repository;

import com.github.kingbbode.scheduler.domain.QrtzSimpleTriggersHistory;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

/**
 * Created by YG-MAC on 2017. 10. 22..
 */
public interface QuartzTriggersHistoryRepository extends Repository<QrtzSimpleTriggersHistory, Long> {
    Stream<QrtzSimpleTriggersHistory> findBySchedNameAndJobName(String schedName, String jobName);

    void save(QrtzSimpleTriggersHistory history);
}
