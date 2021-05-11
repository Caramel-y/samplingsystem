package com.project.samplingsystem.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

/**
 * created by chen on 2021/05/11 at 13:15
 *
 * @author chen
 */
@Data
@Entity
@Table(name = "tb_sampling")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sampling implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = 11)
    private Long id;

    @Column(columnDefinition = "varchar(64) comment '采样编号'")
    private String sampleId;

    @Column(columnDefinition = "varchar(125) comment '采样名称'")
    private String sampleName;

    @Column(columnDefinition = "varchar(125) comment '矿点信息'")
    private String oreLocation;

    @Column(columnDefinition = "varchar(125) comment '供应商信息'")
    private String provider;

    @Column(columnDefinition = "varchar(50) comment '采样天气'")
    private String samplingWeather;

    @Column(columnDefinition = "varchar(25) comment '煤种'")
    private String coalType;

    @Column(columnDefinition = "varchar(125) comment '采样地点'")
    private String samplingPlace;

    @Column(columnDefinition = "varchar(25) comment '采样人员编号'")
    private Long userId;

    @Column(columnDefinition = "varchar(25) comment '采样人员'")
    private String samplingPerson;

    @Column(columnDefinition = "datetime comment '采样时间'")
    @Builder.Default
    private LocalDateTime samplingTime = now();


//    @ManyToOne
//    @JoinColumn(name = "user_refer_id")
//    private NBSysUser user;
}
