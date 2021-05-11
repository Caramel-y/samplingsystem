package com.project.samplingsystem.model.pojo.vo;

import com.project.samplingsystem.model.entity.NBComment;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * created by Wuwenbin on 2019/1/7 at 14:11
 *
 * @author wuwenbin
 */
@Data
@Builder
public class LatestComment implements Serializable {

    private Long articleId;
    private String articleTitle;
    private LocalDateTime articleDate;
    private NBComment comment;
}
