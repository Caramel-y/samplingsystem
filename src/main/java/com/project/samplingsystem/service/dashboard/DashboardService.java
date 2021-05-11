package com.project.samplingsystem.service.dashboard;

import com.project.samplingsystem.model.pojo.vo.BaseDataStatistics;
import com.project.samplingsystem.model.pojo.vo.LatestComment;

import java.util.List;

/**
 * created by Wuwenbin on 2019/1/7 at 14:03
 *
 * @author wuwenbin
 */
public interface DashboardService {

    /**
     * 首页统计数据面板
     *
     * @return
     */
    List<BaseDataStatistics> calculateData();


    /**
     * 找出最新的一条评论
     *
     * @return
     */
    LatestComment findLatestComment();


    /**
     * 统计图的数据
     *
     * @return
     */
    List<Object[]> findTableStatistics();


}
