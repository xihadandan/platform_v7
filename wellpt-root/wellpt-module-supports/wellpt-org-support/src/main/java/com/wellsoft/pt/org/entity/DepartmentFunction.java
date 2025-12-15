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
 * Description: 部门职能关联表
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
//@Table(name = "org_department_function")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class DepartmentFunction extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8326383345941168971L;

    private Department department;

//	private DataDictionary function;

    private String functionUuid;

    private String tenantId;


    public String getTenantId() {
        return tenantId;
    }

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
     * @return the department
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "department_uuid")
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department 要设置的department
     */
    public void setDepartment(Department department) {
        this.department = department;
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
