package com.github.kingbbode.execution.repository;

/*
 * Created By Kingbbode
 * blog : http://kingbbode.github.io
 * github : http://github.com/kingbbode
 *
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 * kingbbode                2017-10-20
 */


import com.github.kingbbode.execution.domain.BatchJobExecution;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BatchJobExecutionRepository extends Repository<BatchJobExecution, Long> {
    Optional<BatchJobExecution> findOne(Long aLong);
}
