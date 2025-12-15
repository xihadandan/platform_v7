/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月24日.1	zyguo		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
@Entity
@ApiModel("群组对象")
public class OrgGroupVo extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -629750368428555421L;
    private String id;
    // 节点名称
    @NotBlank
    @ApiModelProperty("节点名称")
    private String name;
    // 节点编码
    @NotBlank
    @ApiModelProperty("节点编码")
    private String code;
    // 节点类型
    @NotBlank
    @ApiModelProperty("节点类型")
    private Integer type;
    // 备注
    @MaxLength(max = 255)
    @ApiModelProperty("备注")
    private String remark;
    // 对应的成员
    @NotBlank
    @ApiModelProperty("对应的成员")
    private String memberIdPaths;
    @NotBlank
    @MaxLength(max = 4000)
    @ApiModelProperty("对应的成员名称")
    private String memberNames;
    // 对应的角色UUID
    @ApiModelProperty("对应的角色UUID")
    private String roleUuids;
    @ApiModelProperty("成员列表")
    private List<MultiOrgGroupMember> memberList;

    // 添加使用范围人员显示值字段
    @ApiModelProperty("添加使用范围人员显示值字段")
    private String userRangeDisplays;
    // 添加使用范围人员真实值字段
    @ApiModelProperty("添加使用范围人员真实值字段")
    private String userRangeReals;

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

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return the memberIds
     */
    public String getMemberIdPaths() {
        return memberIdPaths;
    }

    /**
     * @param memberIdPaths 要设置的memberIds
     */
    public void setMemberIdPaths(String memberIdPaths) {
        this.memberIdPaths = memberIdPaths;
    }

    /**
     * @return the memberNames
     */
    public String getMemberNames() {
        return memberNames;
    }

    /**
     * @param memberNames 要设置的memberNames
     */
    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
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
     * @return the memberList
     */
    @Transient
    public List<MultiOrgGroupMember> getMemberList() {
        return memberList;
    }

    /**
     * @param memberList 要设置的memberList
     */
    public void setMemberList(List<MultiOrgGroupMember> memberList) {
        this.memberList = memberList;
    }

    public String getUserRangeDisplays() {
        return this.userRangeDisplays;
    }

    public void setUserRangeDisplays(final String userRangeDisplays) {
        this.userRangeDisplays = userRangeDisplays;
    }

    public String getUserRangeReals() {
        return this.userRangeReals;
    }

    public void setUserRangeReals(final String userRangeReals) {
        this.userRangeReals = userRangeReals;
    }
}
