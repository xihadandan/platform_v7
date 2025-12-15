/*
 * @(#)2018年10月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月10日.1	zhulh		2018年10月10日		Create
 * </pre>
 * @date 2018年10月10日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveElement extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5379630866858647542L;

    // 归档ID
    private String archiveId;
    // 归档方式
    private String archiveWay;
    // 归档策略
    private String archiveStrategy;
    // 转换规则名称
    private String botRuleName;
    // 转换规则ID
    private String botRuleId;
    // 文件夹名称
    private String destFolderName;
    // 文件夹UUID
    private String destFolderUuid;
    // 时间变量补位
    private Boolean fillDateTime;
    // 子夹生成规则
    private String subFolderRule;
    // 归档脚本类型
    private String archiveScriptType;
    // 归档脚本
    private String archiveScript;

    /**
     * @return the archiveId
     */
    public String getArchiveId() {
        if (StringUtils.isBlank(archiveId)) {
            archiveId = DigestUtils.md2Hex(archiveWay + botRuleId + destFolderUuid + archiveScriptType
                    + archiveScriptType);
        }
        return archiveId;
    }

    /**
     * @param archiveId 要设置的archiveId
     */
    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    /**
     * @return the archiveWay
     */
    public String getArchiveWay() {
        return archiveWay;
    }

    /**
     * @param archiveWay 要设置的archiveWay
     */
    public void setArchiveWay(String archiveWay) {
        this.archiveWay = archiveWay;
    }

    /**
     * @return the archiveStrategy
     */
    public String getArchiveStrategy() {
        return archiveStrategy;
    }

    /**
     * @param archiveStrategy 要设置的archiveStrategy
     */
    public void setArchiveStrategy(String archiveStrategy) {
        this.archiveStrategy = archiveStrategy;
    }

    /**
     * @return the botRuleName
     */
    public String getBotRuleName() {
        return botRuleName;
    }

    /**
     * @param botRuleName 要设置的botRuleName
     */
    public void setBotRuleName(String botRuleName) {
        this.botRuleName = botRuleName;
    }

    /**
     * @return the botRuleId
     */
    public String getBotRuleId() {
        return botRuleId;
    }

    /**
     * @param botRuleId 要设置的botRuleId
     */
    public void setBotRuleId(String botRuleId) {
        this.botRuleId = botRuleId;
    }

    /**
     * @return the destFolderName
     */
    public String getDestFolderName() {
        return destFolderName;
    }

    /**
     * @param destFolderName 要设置的destFolderName
     */
    public void setDestFolderName(String destFolderName) {
        this.destFolderName = destFolderName;
    }

    /**
     * @return the destFolderUuid
     */
    public String getDestFolderUuid() {
        return destFolderUuid;
    }

    /**
     * @param destFolderUuid 要设置的destFolderUuid
     */
    public void setDestFolderUuid(String destFolderUuid) {
        this.destFolderUuid = destFolderUuid;
    }

    public Boolean getFillDateTime() {
        return fillDateTime;
    }

    public void setFillDateTime(Boolean fillDateTime) {
        this.fillDateTime = fillDateTime;
    }

    /**
     * @return the subFolderRule
     */
    public String getSubFolderRule() {
        return subFolderRule;
    }

    /**
     * @param subFolderRule 要设置的subFolderRule
     */
    public void setSubFolderRule(String subFolderRule) {
        this.subFolderRule = subFolderRule;
    }

    /**
     * @return the archiveScriptType
     */
    public String getArchiveScriptType() {
        return archiveScriptType;
    }

    /**
     * @param archiveScriptType 要设置的archiveScriptType
     */
    public void setArchiveScriptType(String archiveScriptType) {
        this.archiveScriptType = archiveScriptType;
    }

    /**
     * @return the archiveScript
     */
    public String getArchiveScript() {
        return archiveScript;
    }

    /**
     * @param archiveScript 要设置的archiveScript
     */
    public void setArchiveScript(String archiveScript) {
        this.archiveScript = archiveScript;
    }

}
