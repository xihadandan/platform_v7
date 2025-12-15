/*
 * @(#)11/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/25/25.1	    zhulh		11/25/25		    Create
 * </pre>
 * @date 11/25/25
 */
@Entity
@Table(name = "DMS_ROLE_MODEL")
@DynamicUpdate
@DynamicInsert
@ApiModel("权限模型")
public class DmsRoleModelEntity extends SysEntity {
    private static final long serialVersionUID = -2126644814352393850L;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("是否内置")
    private Boolean builtIn;

    @ApiModelProperty("权限组定义UUID，多个以分号分隔")
    private String roleUuids;

    @ApiModelProperty("备注")
    private String remark;

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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the builtIn
     */
    public Boolean getBuiltIn() {
        return builtIn;
    }

    /**
     * @param builtIn 要设置的builtIn
     */
    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    /**
     * @return the roleUuids
     */
    public String getRoleUuids() {
        return roleUuids;
    }

    /**
     * @param roleUuids 要设置的roleUuids
     */
    public void setRoleUuids(String roleUuids) {
        this.roleUuids = roleUuids;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
