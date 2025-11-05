package com.finedge.finedgeapi.repository;

import com.finedge.finedgeapi.entity.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {

}
