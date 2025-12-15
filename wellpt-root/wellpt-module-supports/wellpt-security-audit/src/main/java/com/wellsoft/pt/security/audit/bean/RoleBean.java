/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.bean;

import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.security.audit.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@ApiModel("角色bean")
public class RoleBean extends Role {

    private static final long serialVersionUID = 2983650552314183604L;

    @ApiModelProperty("orgRows")
    private Set<String> orgRows = new HashSet<String>();
    @ApiModelProperty("orgAddRows")
    private Set<String> orgAddRows = new HashSet<String>();
    @ApiModelProperty("orgDeleteRows")
    private Set<String> orgDeleteRows = new HashSet<String>();
    // begin 2014-12-17 yuyq
    @ApiModelProperty("角色分配信息(用户、部门)")
    @MaxLength(max = 4000)
    private String memberNames;
    @ApiModelProperty("memberSmartNames")
    private String memberSmartNames;

    @ApiModelProperty("删除的分配信息id")
    @MaxLength(max = 4000)
    private String deleteids;

    @ApiModelProperty("分配信息ID")
    @MaxLength(max = 4000)
    private String memberIds;

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getDeleteids() {
        return deleteids;
    }

    public void setDeleteids(String deleteids) {
        this.deleteids = deleteids;
    }

    public String getMemberSmartNames() {
        return memberSmartNames;
    }

    public void setMemberSmartNames(String memberSmartNames) {
        this.memberSmartNames = memberSmartNames;
    }

    public String getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }

    // end 2014-12-17

    public Set<String> getOrgRows() {
        return orgRows;
    }

    public void setOrgRows(Set<String> orgRows) {
        this.orgRows = orgRows;
    }

    public Set<String> getOrgAddRows() {
        return orgAddRows;
    }

    public void setOrgAddRows(Set<String> orgAddRows) {
        this.orgAddRows = orgAddRows;
    }

    public Set<String> getOrgDeleteRows() {
        return orgDeleteRows;
    }

    public void setOrgDeleteRows(Set<String> orgDeleteRows) {
        this.orgDeleteRows = orgDeleteRows;
    }

}
