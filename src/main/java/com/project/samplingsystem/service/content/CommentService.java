package com.project.samplingsystem.service.content;

import com.project.samplingsystem.model.entity.NBComment;
import com.project.samplingsystem.model.pojo.bo.CommentQueryBO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * created by Wuwenbin on 2018/9/7 at 9:42
 *
 * @author wuwenbin
 */
public interface CommentService {

    /**
     * 查询评论的分页信息
     *
     * @param pageable
     * @param commentQueryBO
     * @return
     */
    Page<NBComment> findPageInfo(Pageable pageable, CommentQueryBO commentQueryBO);
}
