/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 职位职务关系表
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_JOB_DUTY")
@DynamicUpdate
@DynamicInsert
public class MultiOrgJobDuty extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6893403405731781213L;
    // 对应的元素UUID
    private String jobId;
    // 对应的角色UUID
    private String dutyId;

    private Long orgVersionUuid;

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId 要设置的jobId
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the dutyId
     */
    public String getDutyId() {
        return dutyId;
    }

    /**
     * @param dutyId 要设置的dutyId
     */
    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }
}
