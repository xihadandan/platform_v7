/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 如何描述该类
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
@Table(name = "MULTI_ORG_VERSION")
@DynamicUpdate
@DynamicInsert
@ApiModel("组织版本")
public class MultiOrgVersion extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1511233285636L;
    @ApiModelProperty("id")
    private String id;
    // 组织版本的名称
    @ApiModelProperty("组织版本的名称")
    private String name;
    // 对应的系统单位ID
    @ApiModelProperty("对应的系统单位ID")
    private String systemUnitId;
    // 该版本建立时对应的系统单位名称，一旦设置不可修改
    @ApiModelProperty("该版本建立时对应的系统单位名称，一旦设置不可修改")
    private String initSystemUnitName;
    // 版本号
    @ApiModelProperty("版本号")
    private String version;
    // 来源版本UUID
    @ApiModelProperty("来源版本UUID")
    private String sourceVersionUuid;
    // 状态 0:不启用，1：启用，默认0
    @ApiModelProperty("状态 0:不启用，1：启用，默认0")
    private Integer status;
    // 类型
    @NotBlank
    @ApiModelProperty("类型")
    private String functionType;
    // 类型名称
    @ApiModelProperty("类型名称")
    private String functionTypeName;
    // 备注
    @ApiModelProperty("备注")
    private String remark;
    // 根版本的版本ID
    @ApiModelProperty("根版本的版本ID")
    private String rootVersionId;
    @ApiModelProperty("自身对应的业务单位ID")
    private String selfBusinessUnitId; // 自身对应的业务单位ID
    @ApiModelProperty("是否默认")
    private Boolean isDefault;


    /**
     * @return the version
     */
    @Column(name = "VERSION")
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the status
     */
    @Column(name = "STATUS")
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the functionType
     */
    public String getFunctionType() {
        return functionType;
    }

    /**
     * @param functionType 要设置的functionType
     */
    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    /**
     * @return the functionTypeName
     */
    public String getFunctionTypeName() {
        return functionTypeName;
    }

    /**
     * @param functionTypeName 要设置的functionTypeName
     */
    public void setFunctionTypeName(String functionTypeName) {
        this.functionTypeName = functionTypeName;
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

    @Transient
    public String getFullName(String unitName) {
        String[] names = new String[]{unitName, this.functionTypeName, this.version};
        return StringUtils.join(names, "-");
    }

    @Transient
    @ApiModelProperty("全名称")
    public String getFullName() {
        String[] names = new String[]{this.name, this.functionTypeName, this.version};
        return StringUtils.join(names, "-");
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
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
     * @return the initSystemUnitName
     */
    public String getInitSystemUnitName() {
        return initSystemUnitName;
    }

    /**
     * @param initSystemUnitName 要设置的initSystemUnitName
     */
    public void setInitSystemUnitName(String initSystemUnitName) {
        this.initSystemUnitName = initSystemUnitName;
    }

    /**
     * @return the sourceVersionUuid
     */
    public String getSourceVersionUuid() {
        return sourceVersionUuid;
    }

    /**
     * @param sourceVersionUuid 要设置的sourceVersionUuid
     */
    public void setSourceVersionUuid(String sourceVersionUuid) {
        this.sourceVersionUuid = sourceVersionUuid;
    }

    /**
     * 转成treeNode节点格式
     *
     * @return
     */
    public OrgTreeNode convert2TreeNode() {
        OrgTreeNode tn = new OrgTreeNode();
        tn.setName(this.getFullName());
        tn.setId(this.getId());
        tn.setType(IdPrefix.ORG_VERSION.getValue());
        tn.setOrgVersionId(this.getId());
        tn.setPath(this.getId());
        tn.setSystemUnitId(this.getSystemUnitId());
        return tn;

    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    /**
     * @return the rootVersionId
     */
    public String getRootVersionId() {
        return rootVersionId;
    }

    /**
     * @param rootVersionId 要设置的rootVersionId
     */
    public void setRootVersionId(String rootVersionId) {
        this.rootVersionId = rootVersionId;
    }

    /**
     * @return the selfBusinessUnitId
     */
    public String getSelfBusinessUnitId() {
        return selfBusinessUnitId;
    }

    /**
     * @param selfBusinessUnitId 要设置的selfBusinessUnitId
     */
    public void setSelfBusinessUnitId(String selfBusinessUnitId) {
        this.selfBusinessUnitId = selfBusinessUnitId;
    }
}
