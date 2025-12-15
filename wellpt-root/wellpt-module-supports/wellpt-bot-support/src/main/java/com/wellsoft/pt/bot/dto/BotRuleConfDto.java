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
 * Description: 数据库表BOT_RULE_CONF的对应的DTO类
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
public class BotRuleConfDto implements Serializable {

    private static final long serialVersionUID = 1536891500300L;

    private String uuid;

    // 转换类型：0 单据转单据 1 报文转单据
    private Integer transferType;
    // 转换后脚本
    private String scriptAfterTrans;
    // 转换前脚本
    private String scriptBeforeTrans;
    // 目标单据id
    private String targetObjId;
    // 目标单据名称
    private String targetObjName;
    // 规则名称
    private String ruleName;
    // 规则id
    private String id;
    // 是否持久化转换单据
    private Boolean isPersist;

    private List<BotRuleObjMappingDto> objMappingDtos = Lists.newArrayList();

    private List<BotRuleObjMappingIgnoreDto> ignoreMappings = Lists.newArrayList();

    private BotRuleObjRelaDto relaDto;

    //源单据id
    private String sourceObjId;

    // 自动映射同名字段
    private Boolean autoMapSameColumn;

    /**
     * @return the transferType
     */
    public Integer getTransferType() {
        return this.transferType;
    }

    /**
     * @param transferType
     */
    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    /**
     * @return the scriptAfterTrans
     */
    public String getScriptAfterTrans() {
        return this.scriptAfterTrans;
    }

    /**
     * @param scriptAfterTrans
     */
    public void setScriptAfterTrans(String scriptAfterTrans) {
        this.scriptAfterTrans = scriptAfterTrans;
    }

    /**
     * @return the scriptBeforeTrans
     */
    public String getScriptBeforeTrans() {
        return this.scriptBeforeTrans;
    }

    /**
     * @param scriptBeforeTrans
     */
    public void setScriptBeforeTrans(String scriptBeforeTrans) {
        this.scriptBeforeTrans = scriptBeforeTrans;
    }

    /**
     * @return the targetObjId
     */
    public String getTargetObjId() {
        return this.targetObjId;
    }

    /**
     * @param targetObjId
     */
    public void setTargetObjId(String targetObjId) {
        this.targetObjId = targetObjId;
    }

    /**
     * @return the targetObjName
     */
    public String getTargetObjName() {
        return this.targetObjName;
    }

    /**
     * @param targetObjName
     */
    public void setTargetObjName(String targetObjName) {
        this.targetObjName = targetObjName;
    }

    /**
     * @return the ruleName
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * @param ruleName
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the isPersist
     */
    public Boolean getIsPersist() {
        return this.isPersist;
    }

    /**
     * @param isPersist
     */
    public void setIsPersist(Boolean isPersist) {
        this.isPersist = isPersist;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<BotRuleObjMappingDto> getObjMappingDtos() {
        return objMappingDtos;
    }

    public void setObjMappingDtos(
            List<BotRuleObjMappingDto> objMappingDtos) {
        this.objMappingDtos = objMappingDtos;
    }

    public BotRuleObjRelaDto getRelaDto() {
        return relaDto;
    }

    public void setRelaDto(BotRuleObjRelaDto relaDto) {
        this.relaDto = relaDto;
    }

    public String getSourceObjId() {
        return sourceObjId;
    }

    public void setSourceObjId(String sourceObjId) {
        this.sourceObjId = sourceObjId;
    }

    public Boolean getAutoMapSameColumn() {
        return autoMapSameColumn;
    }

    public void setAutoMapSameColumn(Boolean autoMapSameColumn) {
        this.autoMapSameColumn = autoMapSameColumn;
    }

    public List<BotRuleObjMappingIgnoreDto> getIgnoreMappings() {
        return ignoreMappings;
    }

    public void setIgnoreMappings(List<BotRuleObjMappingIgnoreDto> ignoreMappings) {
        this.ignoreMappings = ignoreMappings;
    }
}
