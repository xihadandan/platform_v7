/*
 * @(#)2020-10-22 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户管理角色信息列表
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020-10-22.1	shenhb		2020-10-22		Create
 * </pre>
 * @date 2020-10-22
 */
@ApiModel("用户权限对象")
public class UserRoleInfoDto {

    @ApiModelProperty("权限uuid")
    private String roleUuid;
    @ApiModelProperty("权限id")
    private String roleId;
    @ApiModelProperty("权限code")
    private String roleCode;
    @ApiModelProperty("权限名")
    private String roleName;
    @ApiModelProperty("source")
    private String source;
    @ApiModelProperty("计算路径")
    private String calculatePath;

    public UserRoleInfoDto(String roleUuid, String roleId, String roleCode, String roleName, String source,
                           String calculatePath) {
        this.roleUuid = roleUuid;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.source = source;
        this.calculatePath = calculatePath;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCalculatePath() {
        return calculatePath;
    }

    public void setCalculatePath(String calculatePath) {
        this.calculatePath = calculatePath;
    }
}
