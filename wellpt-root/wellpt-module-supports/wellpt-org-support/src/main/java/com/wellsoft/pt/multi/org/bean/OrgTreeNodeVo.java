/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
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
@ApiModel("组织节点vo")
public class OrgTreeNodeVo extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -629750368428555421L;
    // 节点名称
    @NotBlank(message = "节点名称不能为空")
    @ApiModelProperty(value = "节点名称", required = true)
    private String name;
    // 节点编码
    @NotBlank(message = "节点编码不能为空")
    @ApiModelProperty(value = "节点编码", required = true)
    private String code;
    // 节点类型
    @NotBlank(message = "节点类型不能为空")
    @ApiModelProperty(value = "节点类型", required = true)
    private String type;
    // 简称
    @ApiModelProperty("简称")
    private String shortName;
    // 用户拼音
    @ApiModelProperty("用户拼音")
    private String userNamePy;
    // 对应的组织版本ID
    @ApiModelProperty("对应的组织版本ID")
    private String orgVersionId;
    // SAP_CODE
    @ApiModelProperty("SAP_CODE")
    private String sapCode;
    // 备注
    @MaxLength(max = 255)
    @ApiModelProperty(value = "备注,最大长度255")
    private String remark;
    // 父节点ID全路径
    @ApiModelProperty("父节点ID全路径")
    private String parentEleIdPath;
    // 父节点名称全路径
    @NotBlank(message = "父节点名称全路径不能为空")
    @ApiModelProperty(value = "父节点名称全路径", required = true)
    private String parentEleNamePath;
    // 对应的节点对象的ID
    @ApiModelProperty("对应的节点对象的ID")
    private String eleId;
    // 对应的节点对象的UUID
    @ApiModelProperty("对应的节点对象的UUID")
    private String eleUuid;
    // 节点的归属系统单位Id
    @ApiModelProperty("节点的归属系统单位Id")
    private String systemUnitId;

    // 该节点是否有其他引用的版本
    @ApiModelProperty(value = "该节点是否有其他引用的版本")
    private List<MultiOrgVersion> otherVersion;

    @ApiModelProperty(value = "负责人IdPaths")
    private String bossIdPaths; // 负责人
    @ApiModelProperty(value = "负责人names")
    private String bossNames;
    @ApiModelProperty(value = "分管领导IdPaths")
    private String branchLeaderIdPaths;
    @ApiModelProperty(value = "分管领导Names")
    private String branchLeaderNames;
    @ApiModelProperty(value = "管理员IdPaths")
    private String managerIdPaths; // 管理员
    @ApiModelProperty(value = "管理员Names")
    private String managerNames;
    // 对应的角色UUID
    @ApiModelProperty(value = "对应的角色UUID")
    private String roleUuids;
    @ApiModelProperty(value = "额外参数")
    private OrgTreeNodeParams params;
    @ApiModelProperty(value = "归属职务ID")
    private String dutyId; // 归属职务ID
    @ApiModelProperty(value = "归属职务名称")
    private String dutyName;

    @ApiModelProperty(value = "组织元素属性")
    private List<OrgElementAttrVo> orgElementAttrs;

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName 要设置的shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUserNamePy() {
        return userNamePy;
    }

    public void setUserNamePy(String userNamePy) {
        this.userNamePy = userNamePy;
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
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
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
     * 从组织节点元素中转化基本的节点属性
     *
     * @param unit
     */
    @ApiModelProperty(hidden = true)
    public void setAttrFromOrgNode(OrgTreeNodeDto dto) {
        this.name = dto.getName();
        this.code = dto.getCode();
        this.remark = dto.getRemark();
        this.shortName = dto.getShortName();
        this.userNamePy = dto.getUserNamePy();
        this.sapCode = dto.getSapCode();
        this.type = dto.getType();
        this.eleId = dto.getEleId();
        this.eleUuid = dto.getEleUuid();
        this.orgVersionId = dto.getOrgVersionId();
        this.uuid = dto.getUuid();
        this.parentEleIdPath = dto.getParentIdPath();
        this.parentEleNamePath = dto.getParentNamePath();
        this.systemUnitId = dto.getSystemUnitId();
    }

    @ApiModelProperty(hidden = true)
    public void setAttrFromOrgElement(MultiOrgElement ele) {
        this.name = ele.getName();
        this.code = ele.getCode();
        this.remark = ele.getRemark();
        this.shortName = ele.getShortName();
        this.sapCode = ele.getSapCode();
        this.type = ele.getType();
        this.eleId = ele.getId();
        this.eleUuid = ele.getUuid();
    }

    @Transient
    @ApiModelProperty("节点IdPath")
    public String getEleIdPath() {
        if (StringUtils.isBlank(this.eleId)) {
            return null;
        } else if (StringUtils.isBlank(this.parentEleIdPath)) {
            return null;
        }
        return this.parentEleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL + this.eleId;

    }

    @Transient
    @ApiModelProperty("节点名称Path")
    public String getEleNamePath() {
        if (StringUtils.isBlank(this.name)) {
            return null;
        } else if (StringUtils.isBlank(this.parentEleNamePath)) {
            return null;
        }
        return this.parentEleNamePath + MultiOrgService.PATH_SPLIT_SYSMBOL + this.name;
    }

    /**
     * @return the eleId
     */
    public String getEleId() {
        return eleId;
    }

    /**
     * @param eleId 要设置的eleId
     */
    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    /**
     * @return the eleUuid
     */
    public String getEleUuid() {
        return eleUuid;
    }

    /**
     * @param eleUuid 要设置的eleUuid
     */
    public void setEleUuid(String eleUuid) {
        this.eleUuid = eleUuid;
    }

    /**
     * @return the parentEleIdPath
     */
    public String getParentEleIdPath() {
        return parentEleIdPath;
    }

    /**
     * @param parentEleIdPath 要设置的parentEleIdPath
     */
    public void setParentEleIdPath(String parentEleIdPath) {
        this.parentEleIdPath = parentEleIdPath;
    }

    /**
     * @return the parentEleNamePath
     */
    public String getParentEleNamePath() {
        return parentEleNamePath;
    }

    /**
     * @param parentEleNamePath 要设置的parentEleNamePath
     */
    public void setParentEleNamePath(String parentEleNamePath) {
        this.parentEleNamePath = parentEleNamePath;
    }

    /**
     * @return the otherVersion
     */
    @Transient
    public List<MultiOrgVersion> getOtherVersion() {
        return otherVersion;
    }

    /**
     * @param otherVersion 要设置的otherVersion
     */
    public void setOtherVersion(List<MultiOrgVersion> otherVersion) {
        this.otherVersion = otherVersion;
    }

    /**
     * @return the bossIds
     */
    public String getBossIdPaths() {
        return bossIdPaths;
    }

    /**
     * @param bossIdPaths 要设置的bossIds
     */
    public void setBossIdPaths(String bossIdPaths) {
        this.bossIdPaths = bossIdPaths;
    }

    /**
     * @return the bossNames
     */
    public String getBossNames() {
        return bossNames;
    }

    /**
     * @param bossNames 要设置的bossNames
     */
    public void setBossNames(String bossNames) {
        this.bossNames = bossNames;
    }

    /**
     * @return the managerIds
     */
    public String getManagerIdPaths() {
        return managerIdPaths;
    }

    /**
     * @param managerIdPaths 要设置的managerIds
     */
    public void setManagerIdPaths(String managerIdPaths) {
        this.managerIdPaths = managerIdPaths;
    }

    /**
     * @return the managerNames
     */
    public String getManagerNames() {
        return managerNames;
    }

    /**
     * @param managerNames 要设置的managerNames
     */
    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    public String getBranchLeaderIdPaths() {
        return branchLeaderIdPaths;
    }

    public void setBranchLeaderIdPaths(String branchLeaderIdPaths) {
        this.branchLeaderIdPaths = branchLeaderIdPaths;
    }

    public String getBranchLeaderNames() {
        return branchLeaderNames;
    }

    public void setBranchLeaderNames(String branchLeaderNames) {
        this.branchLeaderNames = branchLeaderNames;
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
     * @return the sapCode
     */
    public String getSapCode() {
        return sapCode;
    }

    /**
     * @param sapCode 要设置的sapCode
     */
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    /**
     * @return the params
     */
    public OrgTreeNodeParams getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(OrgTreeNodeParams params) {
        this.params = params;
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

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    @Transient
    public List<OrgElementAttrVo> getOrgElementAttrs() {
        return orgElementAttrs;
    }

    public void setOrgElementAttrs(
            List<OrgElementAttrVo> orgElementAttrs) {
        this.orgElementAttrs = orgElementAttrs;
    }
}
