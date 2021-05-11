package com.project.samplingsystem.dao.repository;

import com.project.samplingsystem.model.entity.NBUpload;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * created by Wuwenbin on 2018/8/4 at 0:03
 * @author wuwenbin
 */
public interface UploadRepository extends JpaRepository<NBUpload, Long> {
}
