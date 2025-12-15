/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.dto;

import java.io.Serializable;


/**
 * Description: 数据库表BOT_RULE_OBJ_RELA_MAPPING的对应的DTO类
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
public class BotRuleObjRelaMappingDto implements Serializable {

    private static final long serialVersionUID = 1536891500441L;

    private String uuid;


    private String sourceObjFieldName;
    // 关联关系字段
    private String relaObjField;
    // 源单据ID
    private String sourceObjId;
    // 规则关联关系UUID
    private String ruleObjRelaUuid;
    // 源单据字段
    private String sourceObjField;

    private String relaObjFieldName;

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
     * @return the relaObjField
     */
    public String getRelaObjField() {
        return this.relaObjField;
    }

    /**
     * @param relaObjField
     */
    public void setRelaObjField(String relaObjField) {
        this.relaObjField = relaObjField;
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
     * @return the ruleObjRelaUuid
     */
    public String getRuleObjRelaUuid() {
        return this.ruleObjRelaUuid;
    }

    /**
     * @param ruleObjRelaUuid
     */
    public void setRuleObjRelaUuid(String ruleObjRelaUuid) {
        this.ruleObjRelaUuid = ruleObjRelaUuid;
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
     * @return the relaObjFieldName
     */
    public String getRelaObjFieldName() {
        return this.relaObjFieldName;
    }

    /**
     * @param relaObjFieldName
     */
    public void setRelaObjFieldName(String relaObjFieldName) {
        this.relaObjFieldName = relaObjFieldName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
