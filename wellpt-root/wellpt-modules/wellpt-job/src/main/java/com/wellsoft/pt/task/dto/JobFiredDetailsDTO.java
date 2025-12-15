/*
 * @(#)Apr 26, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.dto;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.task.job.JobDetail;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 26, 2018.1	zhulh		Apr 26, 2018		Create
 * </pre>
 * @date Apr 26, 2018
 */
public class JobFiredDetailsDTO extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3477105236968796117L;

    // 任务名
    private String name;

    // 任务类型：定时、临时
    private String type;

    // 触发类型
    private Integer firedType;

    // 任务类名
    private String jobClassName;

    private JobDetail jobDetail;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the firedType
     */
    public Integer getFiredType() {
        return firedType;
    }

    /**
     * @param firedType 要设置的firedType
     */
    public void setFiredType(Integer firedType) {
        this.firedType = firedType;
    }

    /**
     * @return the jobClassName
     */
    public String getJobClassName() {
        return jobClassName;
    }

    /**
     * @param jobClassName 要设置的jobClassName
     */
    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    /**
     * @return the jobDetail
     */
    public JobDetail getJobDetail() {
        return jobDetail;
    }

    /**
     * @param jobDetail 要设置的jobDetail
     */
    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

}
