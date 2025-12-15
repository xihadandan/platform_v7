/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务材料
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
@ApiModel("业务材料")
@Entity
@Table(name = "CD_MATERIAL")
@DynamicUpdate
@DynamicInsert
public class CdMaterialEntity extends com.wellsoft.context.jdbc.entity.Entity {

    @ApiModelProperty("材料定义UUID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialDefUuid;

    @ApiModelProperty("材料名称")
    private String materialName;

    @ApiModelProperty("材料编号")
    private String materialCode;

    @ApiModelProperty("所在业务类型")
    private String bizType;

    @ApiModelProperty("所在业务ID")
    private String bizId;

    @ApiModelProperty("所在业务数据UUID")
    private String dataUuid;

    @ApiModelProperty("所在业务场景")
    private String purpose;

    @ApiModelProperty("所有者ID")
    private String ownerId;

    @ApiModelProperty("版本号")
    private Double version;

    @ApiModelProperty("材料附件UUID列表，多个以分号隔开")
    private String repoFileUuids;

    @ApiModelProperty("材料有效标识，1有效，0无效")
    private String validationFlag;

    @ApiModelProperty("材料附件扩展属性信息")
    private String attributes;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * @return the materialDefUuid
     */
    public Long getMaterialDefUuid() {
        return materialDefUuid;
    }

    /**
     * @param materialDefUuid 要设置的materialDefUuid
     */
    public void setMaterialDefUuid(Long materialDefUuid) {
        this.materialDefUuid = materialDefUuid;
    }

    /**
     * @return the materialName
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * @param materialName 要设置的materialName
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * @return the materialCode
     */
    public String getMaterialCode() {
        return materialCode;
    }

    /**
     * @param materialCode 要设置的materialCode
     */
    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    /**
     * @return the bizType
     */
    public String getBizType() {
        return bizType;
    }

    /**
     * @param bizType 要设置的bizType
     */
    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    /**
     * @return the bizId
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * @param bizId 要设置的bizId
     */
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose 要设置的purpose
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the repoFileUuids
     */
    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    /**
     * @param repoFileUuids 要设置的repoFileUuids
     */
    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    /**
     * @return the validationFlag
     */
    public String getValidationFlag() {
        return validationFlag;
    }

    /**
     * @param validationFlag 要设置的validationFlag
     */
    public void setValidationFlag(String validationFlag) {
        this.validationFlag = validationFlag;
    }

    /**
     * @return the attributes
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * @param attributes 要设置的attributes
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
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
