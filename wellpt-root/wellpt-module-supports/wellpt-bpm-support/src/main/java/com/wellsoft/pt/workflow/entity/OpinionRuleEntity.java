/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表UF_OPINION_RULE的实体类
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
@Table(name = "WF_OPINION_RULE")
@DynamicUpdate
@DynamicInsert
public class OpinionRuleEntity extends TenantEntity {

    private static final long serialVersionUID = 1620718431739L;

    // 满足条件
    private String satisfyCondition;
    // 提示3s自动关闭
    private String isAlertAutoClose;
    // 提示语
    private String cueWords;
    // 规则名称
    private String opinionRuleName;

    // 校验项查询值 多个校验项就拼接在一起
    private String opinionRuleItem;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    public String getOpinionRuleItem() {
        return opinionRuleItem;
    }

    public void setOpinionRuleItem(String opinionRuleItem) {
        this.opinionRuleItem = opinionRuleItem;
    }

    /**
     * @return the satisfyCondition
     */
    public String getSatisfyCondition() {
        return this.satisfyCondition;
    }

    /**
     * @param satisfyCondition
     */
    public void setSatisfyCondition(String satisfyCondition) {
        this.satisfyCondition = satisfyCondition;
    }

    public String getIsAlertAutoClose() {
        return isAlertAutoClose;
    }

    public void setIsAlertAutoClose(String isAlertAutoClose) {
        this.isAlertAutoClose = isAlertAutoClose;
    }

    /**
     * @return the cueWords
     */
    public String getCueWords() {
        return this.cueWords;
    }

    /**
     * @param cueWords
     */
    public void setCueWords(String cueWords) {
        this.cueWords = cueWords;
    }

    /**
     * @return the opinionRuleName
     */
    public String getOpinionRuleName() {
        return this.opinionRuleName;
    }

    /**
     * @param opinionRuleName
     */
    public void setOpinionRuleName(String opinionRuleName) {
        this.opinionRuleName = opinionRuleName;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
