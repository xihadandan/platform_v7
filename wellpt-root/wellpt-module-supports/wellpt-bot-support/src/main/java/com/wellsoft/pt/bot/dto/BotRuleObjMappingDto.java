/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.dto;

import java.io.Serializable;


/**
 * Description: 数据库表BOT_RULE_OBJ_MAPPING的对应的DTO类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-09-14.1	chenq		2018-09-14		Create
 * </pre>
 * @date 2018-09-14
 */
public class BotRuleObjMappingDto implements Serializable {

    private static final long serialVersionUID = 1536891500400L;

    private String uuid;

    // 源单据字段名称
    private String sourceObjFieldName;
    // 目标单据字段
    private String targetObjField;
    // 目标单据字段名称
    private String targetObjFieldName;
    // 源单据ID
    private String sourceObjId;
    // 是否反写映射
    private Boolean isReverseWrite;
    // 规则配置UUID
    private String ruleConfUuid;
    // 源单据字段
    private String sourceObjField;
    // 值计算方式
    private Integer renderValueType;
    // 值计算表达式
    private String renderValueExpression;

    private int seq;

    /**
     * @return the sourceObjFieldName
     */
    public String getSourceObjFieldName() {
        return this.sourceObjFieldName;
    }

    /**
     * @param sourceObjFieldName
     */
    public void setSourceObjFieldName(String sourceObjFieldName) {
        this.sourceObjFieldName = sourceObjFieldName;
    }

    /**
     * @return the targetObjField
     */
    public String getTargetObjField() {
        return this.targetObjField;
    }

    /**
     * @param targetObjField
     */
    public void setTargetObjField(String targetObjField) {
        this.targetObjField = targetObjField;
    }

    /**
     * @return the targetObjFieldName
     */
    public String getTargetObjFieldName() {
        return this.targetObjFieldName;
    }

    /**
     * @param targetObjFieldName
     */
    public void setTargetObjFieldName(String targetObjFieldName) {
        this.targetObjFieldName = targetObjFieldName;
    }

    /**
     * @return the sourceObjId
     */
    public String getSourceObjId() {
        return this.sourceObjId;
    }

    /**
     * @param sourceObjId
     */
    public void setSourceObjId(String sourceObjId) {
        this.sourceObjId = sourceObjId;
    }

    /**
     * @return the isReverseWrite
     */
    public Boolean getIsReverseWrite() {
        return this.isReverseWrite;
    }

    /**
     * @param isReverseWrite
     */
    public void setIsReverseWrite(Boolean isReverseWrite) {
        this.isReverseWrite = isReverseWrite;
    }

    /**
     * @return the ruleConfUuid
     */
    public String getRuleConfUuid() {
        return this.ruleConfUuid;
    }

    /**
     * @param ruleConfUuid
     */
    public void setRuleConfUuid(String ruleConfUuid) {
        this.ruleConfUuid = ruleConfUuid;
    }

    /**
     * @return the sourceObjField
     */
    public String getSourceObjField() {
        return this.sourceObjField;
    }

    /**
     * @param sourceObjField
     */
    public void setSourceObjField(String sourceObjField) {
        this.sourceObjField = sourceObjField;
    }

    /**
     * @return the renderValueType
     */
    public Integer getRenderValueType() {
        return this.renderValueType;
    }

    /**
     * @param renderValueType
     */
    public void setRenderValueType(Integer renderValueType) {
        this.renderValueType = renderValueType;
    }

    /**
     * @return the renderValueExpression
     */
    public String getRenderValueExpression() {
        return this.renderValueExpression;
    }

    /**
     * @param renderValueExpression
     */
    public void setRenderValueExpression(String renderValueExpression) {
        this.renderValueExpression = renderValueExpression;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
