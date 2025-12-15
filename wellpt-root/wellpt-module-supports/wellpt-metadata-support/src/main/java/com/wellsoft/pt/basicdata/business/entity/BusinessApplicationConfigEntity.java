/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表BUSINESS_APPLICATION_CONFIG的实体类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-21.1	leo		2019-02-21		Create
 * </pre>
 * @date 2019-02-21
 */
@Entity
@Table(name = "BUSINESS_APPLICATION_CONFIG")
@DynamicUpdate
@DynamicInsert
public class BusinessApplicationConfigEntity extends IdEntity {

    private static final long serialVersionUID = 1550738921971L;

    private String businessApplicationUuid;

    private String dictType;
    private String dictCode;

    // 规则名称
    private String formUuid;
    // 规则id
    private String ruleUuid;

    public String getBusinessApplicationUuid() {
        return businessApplicationUuid;
    }

    public void setBusinessApplicationUuid(String businessApplicationUuid) {
        this.businessApplicationUuid = businessApplicationUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getRuleUuid() {
        return ruleUuid;
    }

    public void setRuleUuid(String ruleUuid) {
        this.ruleUuid = ruleUuid;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

}
