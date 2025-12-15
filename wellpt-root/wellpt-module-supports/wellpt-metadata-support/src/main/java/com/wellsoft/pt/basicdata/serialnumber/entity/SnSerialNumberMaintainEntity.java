/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/21/22.1	zhulh		7/21/22		Create
 * </pre>
 * @date 7/21/22
 */
@ApiModel("流水号维护")
@Entity
@Table(name = "sn_serial_number_maintain")
@DynamicUpdate
@DynamicInsert
public class SnSerialNumberMaintainEntity extends TenantEntity {
    private static final long serialVersionUID = 8548340852121524683L;

    @ApiModelProperty("流水号定义UUID")
    private String serialNumberDefUuid;

    @ApiModelProperty("指针初始值")
    private String initialValue;

    @ApiModelProperty("指针")
    private String pointer;

    @ApiModelProperty("指针重置类型")
    private String pointerResetType;

    @ApiModelProperty("指针重置规则")
    private String pointerResetRule;

    @ApiModelProperty("指针重置规则值")
    private String pointerResetRuleValue;

    /**
     * @return the serialNumberDefUuid
     */
    public String getSerialNumberDefUuid() {
        return serialNumberDefUuid;
    }

    /**
     * @param serialNumberDefUuid 要设置的serialNumberDefUuid
     */
    public void setSerialNumberDefUuid(String serialNumberDefUuid) {
        this.serialNumberDefUuid = serialNumberDefUuid;
    }

    /**
     * @return the initialValue
     */
    public String getInitialValue() {
        return initialValue;
    }

    /**
     * @param initialValue 要设置的initialValue
     */
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * @return the pointer
     */
    public String getPointer() {
        return pointer;
    }

    /**
     * @param pointer 要设置的pointer
     */
    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    /**
     * @return the pointerResetType
     */
    public String getPointerResetType() {
        return pointerResetType;
    }

    /**
     * @param pointerResetType 要设置的pointerResetType
     */
    public void setPointerResetType(String pointerResetType) {
        this.pointerResetType = pointerResetType;
    }

    /**
     * @return the pointerResetRule
     */
    public String getPointerResetRule() {
        return pointerResetRule;
    }

    /**
     * @param pointerResetRule 要设置的pointerResetRule
     */
    public void setPointerResetRule(String pointerResetRule) {
        this.pointerResetRule = pointerResetRule;
    }

    /**
     * @return the pointerResetRuleValue
     */
    public String getPointerResetRuleValue() {
        return pointerResetRuleValue;
    }

    /**
     * @param pointerResetRuleValue 要设置的pointerResetRuleValue
     */
    public void setPointerResetRuleValue(String pointerResetRuleValue) {
        this.pointerResetRuleValue = pointerResetRuleValue;
    }
}
