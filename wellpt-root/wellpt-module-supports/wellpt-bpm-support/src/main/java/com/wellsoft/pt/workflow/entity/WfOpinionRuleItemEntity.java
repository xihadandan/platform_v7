/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表WF_OPINION_RULE_ITEM的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-05-11.1	zenghw		2021-05-11		Create
 * </pre>
 * @date 2021-05-11
 */
@Entity
@Table(name = "WF_OPINION_RULE_ITEM")
@DynamicUpdate
@DynamicInsert
public class WfOpinionRuleItemEntity extends IdEntity {

    private static final long serialVersionUID = 1620718433210L;

    // 校验条件 枚举：ItemConditionEnum
    // IC01:等于
    // IC02:不等于
    // IC03:大于
    // IC04:大于等于
    // IC05:小于
    // IC06:小于等于
    // IC07:包含
    // IC08:不包含
    private String itemCondition;

    private String itemValue;

    private String opinionRuleUuid;
    // 固定值：意见内容或意见长度
    private String itemName;

    /**
     * @return the itemCondition
     */
    public String getItemCondition() {
        return this.itemCondition;
    }

    /**
     * @param itemCondition
     */
    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    /**
     * @return the itemValue
     */
    public String getItemValue() {
        return this.itemValue;
    }

    /**
     * @param itemValue
     */
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    /**
     * @return the opinionRuleUuid
     */
    public String getOpinionRuleUuid() {
        return this.opinionRuleUuid;
    }

    /**
     * @param opinionRuleUuid
     */
    public void setOpinionRuleUuid(String opinionRuleUuid) {
        this.opinionRuleUuid = opinionRuleUuid;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * @param itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
