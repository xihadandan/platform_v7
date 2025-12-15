/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;


/**
 * Description: 数据库表BOT_RULE_OBJ_RELA的对应的DTO类
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
public class BotRuleObjRelaDto implements Serializable {

    private static final long serialVersionUID = 1536891500419L;

    private String uuid;
    // 关系单据ID
    private String relaObjId;
    // 关系单据名称
    private String relaObjName;
    // 单据转换规则UUID
    private String ruleConfUuid;

    private List<BotRuleObjRelaMappingDto> relaMappingDtos = Lists.newArrayList();

    /**
     * @return the relaObjId
     */
    public String getRelaObjId() {
        return this.relaObjId;
    }

    /**
     * @param relaObjId
     */
    public void setRelaObjId(String relaObjId) {
        this.relaObjId = relaObjId;
    }

    /**
     * @return the relaObjName
     */
    public String getRelaObjName() {
        return this.relaObjName;
    }

    /**
     * @param relaObjName
     */
    public void setRelaObjName(String relaObjName) {
        this.relaObjName = relaObjName;
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

    public List<BotRuleObjRelaMappingDto> getRelaMappingDtos() {
        return relaMappingDtos;
    }

    public void setRelaMappingDtos(
            List<BotRuleObjRelaMappingDto> relaMappingDtos) {
        this.relaMappingDtos = relaMappingDtos;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
