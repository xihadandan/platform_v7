/*
 * @(#)5/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
 * 5/6/23.1	zhulh		5/6/23		Create
 * </pre>
 * @date 5/6/23
 */
public class CdMaterialParamsBuilder {

    // 材料编码列表
    private List<String> materialCodes = Lists.newArrayListWithCapacity(0);

    // 所在业务类型
    private String bizType;

    // 所在业务ID
    private String bizId;

    // 所在业务数据UUID
    private String dataUuid;

    // 所在业务场景
    private String purpose;

    // 所有者ID列表
    private List<String> ownerIds = Lists.newArrayListWithCapacity(0);

    // 材料附件UUID列表
    private List<String> repoFileUuids = Lists.newArrayListWithCapacity(0);

    // 材料有效标识，1有效，0无效
    private String validationFlag;

    // 材料附件扩展属性信息
    private Map<String, Object> attributes = Maps.newHashMap();

    // 备注
    private String remark;

    // 是否更新
    private boolean update;

    /**
     *
     */
    private CdMaterialParamsBuilder(List<String> materialCodes) {
        this.materialCodes = materialCodes;
    }

    public static CdMaterialParamsBuilder create(List<String> materialCodes) {
        return new CdMaterialParamsBuilder(materialCodes);
    }

    public CdMaterialParams build() {
        CdMaterialParams materialParams = new CdMaterialParams();
        materialParams.setMaterialCodes(materialCodes);
        materialParams.setBizType(bizType);
        materialParams.setBizId(bizId);
        materialParams.setDataUuid(dataUuid);
        materialParams.setPurpose(purpose);
        materialParams.setOwnerIds(ownerIds);
        materialParams.setRepoFileUuids(repoFileUuids);
        materialParams.setValidationFlag(validationFlag);
        materialParams.setAttributes(attributes);
        materialParams.setRemark(remark);
        materialParams.setUpdate(update);
        return materialParams;
    }

    /**
     * @param bizType
     * @return
     */
    public CdMaterialParamsBuilder bizType(String bizType) {
        this.bizType = bizType;
        return this;
    }

    /**
     * @param bizId
     * @return
     */
    public CdMaterialParamsBuilder bizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    /**
     * @param dataUuid
     * @return
     */
    public CdMaterialParamsBuilder dataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
        return this;
    }

    /**
     * @param purpose
     * @return
     */
    public CdMaterialParamsBuilder purpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    /**
     * @param ownerIds
     * @return
     */
    public CdMaterialParamsBuilder ownerIds(List<String> ownerIds) {
        this.ownerIds = ownerIds;
        return this;
    }

    /**
     * @param repoFileUuids
     * @return
     */
    public CdMaterialParamsBuilder repoFileUuids(List<String> repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
        return this;
    }

    /**
     * @param validationFlag
     * @return
     */
    public CdMaterialParamsBuilder validationFlag(String validationFlag) {
        this.validationFlag = validationFlag;
        return this;
    }

    /**
     * @param attributes
     * @return
     */
    public CdMaterialParamsBuilder attributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * @param remark
     * @return
     */
    public CdMaterialParamsBuilder remark(String remark) {
        this.remark = remark;
        return this;
    }

    /**
     * @param update
     * @return
     */
    public CdMaterialParamsBuilder update(boolean update) {
        this.update = update;
        return this;
    }
}
