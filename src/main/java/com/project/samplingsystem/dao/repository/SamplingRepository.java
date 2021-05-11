package com.project.samplingsystem.dao.repository;

import com.project.samplingsystem.model.entity.Sampling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * created by chen on 2021/05/11 at 13:15
 *
 * @author chen
 */
public interface SamplingRepository extends JpaSpecificationExecutor<Sampling> , JpaRepository<Sampling, Long> {

    List<Sampling> findByUserId(String userId);
}
