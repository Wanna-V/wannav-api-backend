package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long> {

    List<PointLog> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}