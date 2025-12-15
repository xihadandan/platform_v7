/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表DI_CONFIG的实体类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
@Entity
@Table(name = "DI_CONFIG")
@DynamicUpdate
@DynamicInsert
public class DiConfigEntity extends TenantEntity {

    private static final long serialVersionUID = 1563853754921L;

    // 定时任务UUID（平台的定时任务配置）
    private String jobUuid;
    // 最大重试次数
    private Integer redeliveryMaximum;
    // 配置名称
    private String name;
    // 重试规则
    private String redeliveryRulePattern;
    // 是否启用
    private Boolean isEnable;
    // 配置ID
    private String id;
    // 重试间隔时间
    private Long redeliveryInterval;
    // 间隔执行时间
    private Long timeInterval;

    /**
     * @return the jobUuid
     */
    public String getJobUuid() {
        return this.jobUuid;
    }

    /**
     * @param jobUuid
     */
    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    /**
     * @return the redeliveryMaximum
     */
    public Integer getRedeliveryMaximum() {
        return this.redeliveryMaximum;
    }

    /**
     * @param redeliveryMaximum
     */
    public void setRedeliveryMaximum(Integer redeliveryMaximum) {
        this.redeliveryMaximum = redeliveryMaximum;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the redeliveryRulePattern
     */
    public String getRedeliveryRulePattern() {
        return this.redeliveryRulePattern;
    }

    /**
     * @param redeliveryRulePattern
     */
    public void setRedeliveryRulePattern(String redeliveryRulePattern) {
        this.redeliveryRulePattern = redeliveryRulePattern;
    }

    /**
     * @return the isEnable
     */
    public Boolean getIsEnable() {
        return this.isEnable;
    }

    /**
     * @param isEnable
     */
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the redeliveryInterval
     */
    public Long getRedeliveryInterval() {
        return this.redeliveryInterval;
    }

    /**
     * @param redeliveryInterval
     */
    public void setRedeliveryInterval(Long redeliveryInterval) {
        this.redeliveryInterval = redeliveryInterval;
    }

    /**
     * @return the timeInterval
     */
    public Long getTimeInterval() {
        return this.timeInterval;
    }

    /**
     * @param timeInterval
     */
    public void setTimeInterval(Long timeInterval) {
        this.timeInterval = timeInterval;
    }

}
