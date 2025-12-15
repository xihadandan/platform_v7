/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description: 岗位职能关联表
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-24.1  zhengky	2014-8-24	  Create
 * </pre>
 * @date 2014-8-24
 */
//@Entity
//@Table(name = "org_job_function")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class JobFunction extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7233136739272882265L;

    private Job job;

    private String tenantId;
    private String functionUuid;

    public String getTenantId() {
        return tenantId;
    }
//	private DataDictionary function;

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFunctionUuid() {
        return functionUuid;
    }

    public void setFunctionUuid(String functionUuid) {
        this.functionUuid = functionUuid;
    }

    /**
     * @return the job
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "job_uuid")
    public Job getJob() {
        return job;
    }

    /**
     * @param job 要设置的job
     */
    public void setJob(Job job) {
        this.job = job;
    }

//	/**
//	 * @return the leader
//	 */
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "function_uuid")
//	public DataDictionary getFunction() {
//		return function;
//	}
//
//	/**
//	 * @param leader 要设置的leader
//	 */
//	public void setFunction(DataDictionary function) {
//		this.function = function;
//	}

}
