/*
 * @(#)5/5/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/5/23.1	zhulh		5/5/23		Create
 * </pre>
 * @date 5/5/23
 */
@ApiModel("业务材料参数")
public class CdMaterialParams extends BaseObject {
    private static final long serialVersionUID = 8310093197832963450L;

    @ApiModelProperty("材料编码列表")
    private List<String> materialCodes = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("所在业务类型")
    private String bizType;

    @ApiModelProperty("所在业务ID")
    private String bizId;

    @ApiModelProperty("所在业务数据UUID")
    private String dataUuid;

    @ApiModelProperty("所在业务场景")
    private String purpose;

    @ApiModelProperty("所有者ID列表")
    private List<String> ownerIds = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("材料附件UUID列表")
    private List<String> repoFileUuids = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("材料有效标识，1有效，0无效")
    private String validationFlag;

    @ApiModelProperty("材料附件扩展属性信息")
    private Map<String, Object> attributes = Maps.newHashMap();

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否更新")
    private boolean update;

    /**
     * @return the materialCodes
     */
    public List<String> getMaterialCodes() {
        return materialCodes;
    }

    /**
     * @param materialCodes 要设置的materialCodes
     */
    public void setMaterialCodes(List<String> materialCodes) {
        this.materialCodes = materialCodes;
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
     * @return the ownerIds
     */
    public List<String> getOwnerIds() {
        return ownerIds;
    }

    /**
     * @param ownerIds 要设置的ownerIds
     */
    public void setOwnerIds(List<String> ownerIds) {
        this.ownerIds = ownerIds;
    }

    /**
     * @return the repoFileUuids
     */
    public List<String> getRepoFileUuids() {
        return repoFileUuids;
    }

    /**
     * @param repoFileUuids 要设置的repoFileUuids
     */
    public void setRepoFileUuids(List<String> repoFileUuids) {
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
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes 要设置的attributes
     */
    public void setAttributes(Map<String, Object> attributes) {
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

    /**
     * @return
     */
    public boolean isUpdate() {
        return this.update;
    }

    /**
     * @param update
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }
}
