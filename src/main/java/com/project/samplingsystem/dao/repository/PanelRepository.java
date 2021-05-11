package com.project.samplingsystem.dao.repository;

import com.project.samplingsystem.model.entity.NBPanel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * created by Wuwenbin on 2018/7/19 at 下午3:04
 * @author wuwenbin
 */
public interface PanelRepository extends JpaRepository<NBPanel, Long> {
}
