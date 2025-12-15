/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务事项定义实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@ApiModel("业务事项定义")
@Entity
@Table(name = "BIZ_ITEM_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class BizItemDefinitionEntity extends IdEntity {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("事项类型，10单个事项，20串联事项，30并联事项")
    private String type;

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty("使用定义单ID")
    private String formId;

    @ApiModelProperty("事项名称字段")
    private String itemNameField;

    @ApiModelProperty("事项编码字段")
    private String itemCodeField;

    @ApiModelProperty("事项所有者字段")
    private String itemOwnerField;

    @ApiModelProperty("时限从表ID")
    private String timeLimitSubformId;

    @ApiModelProperty("时限从表的时限字段")
    private String timeLimitField;

    @ApiModelProperty("材料从表ID")
    private String materialSubformId;

    @ApiModelProperty("材料从表的材料名称字段")
    private String materialNameField;

    @ApiModelProperty("材料从表的材料编码字段")
    private String materialCodeField;

    @ApiModelProperty("材料从表的材料是否必填字段")
    private String materialRequiredField;

    @ApiModelProperty("包含事项从表ID")
    private String includeItemSubformId;

    @ApiModelProperty("包含事项从表的事项名称字段")
    private String includeItemNameField;

    @ApiModelProperty("包含事项从表的事项编码字段")
    private String includeItemCodeField;

    @ApiModelProperty("包含事项从表的前置事项编码")
    private String frontItemCodeField;

    @ApiModelProperty("互斥事项从表ID")
    private String mutexItemSubformId;

    @ApiModelProperty("互斥事项从表的事项编码字段，字段值多个以分号隔开")
    private String mutexItemCodeField;

    @ApiModelProperty("关联事项从表ID")
    private String relateItemSubformId;

    @ApiModelProperty("关联事项从表的事项编码字段，字段值多个以分号隔开")
    private String relateItemCodeField;

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
     * @return the businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId 要设置的businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

    /**
     * @return the itemNameField
     */
    public String getItemNameField() {
        return itemNameField;
    }

    /**
     * @param itemNameField 要设置的itemNameField
     */
    public void setItemNameField(String itemNameField) {
        this.itemNameField = itemNameField;
    }

    /**
     * @return the itemCodeField
     */
    public String getItemCodeField() {
        return itemCodeField;
    }

    /**
     * @param itemCodeField 要设置的itemCodeField
     */
    public void setItemCodeField(String itemCodeField) {
        this.itemCodeField = itemCodeField;
    }


    /**
     * @return the itemOwnerField
     */
    public String getItemOwnerField() {
        return itemOwnerField;
    }

    /**
     * @param itemOwnerField 要设置的itemOwnerField
     */
    public void setItemOwnerField(String itemOwnerField) {
        this.itemOwnerField = itemOwnerField;
    }

    /**
     * @return the timeLimitSubformId
     */
    public String getTimeLimitSubformId() {
        return timeLimitSubformId;
    }

    /**
     * @param timeLimitSubformId 要设置的timeLimitSubformId
     */
    public void setTimeLimitSubformId(String timeLimitSubformId) {
        this.timeLimitSubformId = timeLimitSubformId;
    }

    /**
     * @return the timeLimitField
     */
    public String getTimeLimitField() {
        return timeLimitField;
    }

    /**
     * @param timeLimitField 要设置的timeLimitField
     */
    public void setTimeLimitField(String timeLimitField) {
        this.timeLimitField = timeLimitField;
    }

    /**
     * @return the materialSubformId
     */
    public String getMaterialSubformId() {
        return materialSubformId;
    }

    /**
     * @param materialSubformId 要设置的materialSubformId
     */
    public void setMaterialSubformId(String materialSubformId) {
        this.materialSubformId = materialSubformId;
    }

    /**
     * @return the materialNameField
     */
    public String getMaterialNameField() {
        return materialNameField;
    }

    /**
     * @param materialNameField 要设置的materialNameField
     */
    public void setMaterialNameField(String materialNameField) {
        this.materialNameField = materialNameField;
    }

    /**
     * @return the materialCodeField
     */
    public String getMaterialCodeField() {
        return materialCodeField;
    }

    /**
     * @param materialCodeField 要设置的materialCodeField
     */
    public void setMaterialCodeField(String materialCodeField) {
        this.materialCodeField = materialCodeField;
    }

    /**
     * @return the materialRequiredField
     */
    public String getMaterialRequiredField() {
        return materialRequiredField;
    }

    /**
     * @param materialRequiredField 要设置的materialRequiredField
     */
    public void setMaterialRequiredField(String materialRequiredField) {
        this.materialRequiredField = materialRequiredField;
    }

    /**
     * @return the includeItemSubformId
     */
    public String getIncludeItemSubformId() {
        return includeItemSubformId;
    }

    /**
     * @param includeItemSubformId 要设置的includeItemSubformId
     */
    public void setIncludeItemSubformId(String includeItemSubformId) {
        this.includeItemSubformId = includeItemSubformId;
    }

    /**
     * @return the includeItemNameField
     */
    public String getIncludeItemNameField() {
        return includeItemNameField;
    }

    /**
     * @param includeItemNameField 要设置的includeItemNameField
     */
    public void setIncludeItemNameField(String includeItemNameField) {
        this.includeItemNameField = includeItemNameField;
    }

    /**
     * @return the includeItemCodeField
     */
    public String getIncludeItemCodeField() {
        return includeItemCodeField;
    }

    /**
     * @param includeItemCodeField 要设置的includeItemCodeField
     */
    public void setIncludeItemCodeField(String includeItemCodeField) {
        this.includeItemCodeField = includeItemCodeField;
    }

    /**
     * @return the frontItemCodeField
     */
    public String getFrontItemCodeField() {
        return frontItemCodeField;
    }

    /**
     * @param frontItemCodeField 要设置的frontItemCodeField
     */
    public void setFrontItemCodeField(String frontItemCodeField) {
        this.frontItemCodeField = frontItemCodeField;
    }

    /**
     * @return the mutexItemSubformId
     */
    public String getMutexItemSubformId() {
        return mutexItemSubformId;
    }

    /**
     * @param mutexItemSubformId 要设置的mutexItemSubformId
     */
    public void setMutexItemSubformId(String mutexItemSubformId) {
        this.mutexItemSubformId = mutexItemSubformId;
    }

    /**
     * @return the mutexItemCodeField
     */
    public String getMutexItemCodeField() {
        return mutexItemCodeField;
    }

    /**
     * @param mutexItemCodeField 要设置的mutexItemCodeField
     */
    public void setMutexItemCodeField(String mutexItemCodeField) {
        this.mutexItemCodeField = mutexItemCodeField;
    }

    /**
     * @return the relateItemSubformId
     */
    public String getRelateItemSubformId() {
        return relateItemSubformId;
    }

    /**
     * @param relateItemSubformId 要设置的relateItemSubformId
     */
    public void setRelateItemSubformId(String relateItemSubformId) {
        this.relateItemSubformId = relateItemSubformId;
    }

    /**
     * @return the relateItemCodeField
     */
    public String getRelateItemCodeField() {
        return relateItemCodeField;
    }

    /**
     * @param relateItemCodeField 要设置的relateItemCodeField
     */
    public void setRelateItemCodeField(String relateItemCodeField) {
        this.relateItemCodeField = relateItemCodeField;
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
