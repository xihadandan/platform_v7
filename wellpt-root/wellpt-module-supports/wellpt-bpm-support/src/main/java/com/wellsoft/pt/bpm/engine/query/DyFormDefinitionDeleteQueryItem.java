/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;

import java.text.DecimalFormat;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
public class DyFormDefinitionDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5610234262734135079L;
    /* ///////////以下为临时变量/////////////////** */
    private final String minVersion = "1.0";// 最低的版本
    // 表名
    private String name;
    // 显示名称
    private String displayName;
    // 表单属性id
    private String outerId;
    // 表单编号
    private String code;
    // 表单显示形式 ： 两种 一种是可编辑展示、 一种是直接展示文本
    private String formDisplay;
    // 显示单据的名称
    private String displayFormModelName;
    // 显示单据对应的表单uuid
    private String displayFormModelId;
    // 应用于
    private String applyTo;
    // 打印模板的ID
    private String printTemplateId;
    // 打印模板的名称
    private String printTemplateName;
    // 描述
    private String remark;
    private String relationTbl;
    // html body内容
    private String html;
    // 版本 ,形式：1.0
    private String version;
    // 模块ID
    private String moduleId;
    // 模块名
    private String moduleName;
    // 是否启用表单签名
    private String enableSignature;
    // 以json的形式保存整个数据表单的定义
    private String definitionJson;
    // 版本格式
    private DecimalFormat versionFormat = new DecimalFormat("0.0");

    // 是否升级1.是 0.否
    private String isUp = "0";// 非持久化属性

    public DyFormDefinitionDeleteQueryItem(DyFormFormDefinition dyFormDefinition) {
        this.code = dyFormDefinition.getCode();
        this.name = dyFormDefinition.getTableName();
        this.displayName = dyFormDefinition.getName();
        this.definitionJson = dyFormDefinition.getDefinitionJson();
        this.enableSignature = dyFormDefinition.getEnableSignature();
        this.isUp = dyFormDefinition.getIsUp();
    }

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
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName 要设置的displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the outerId
     */
    public String getOuterId() {
        return outerId;
    }

    /**
     * @param outerId 要设置的outerId
     */
    public void setOuterId(String outerId) {
        this.outerId = outerId;
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
     * @return the formDisplay
     */
    public String getFormDisplay() {
        return formDisplay;
    }

    /**
     * @param formDisplay 要设置的formDisplay
     */
    public void setFormDisplay(String formDisplay) {
        this.formDisplay = formDisplay;
    }

    /**
     * @return the displayFormModelName
     */
    public String getDisplayFormModelName() {
        return displayFormModelName;
    }

    /**
     * @param displayFormModelName 要设置的displayFormModelName
     */
    public void setDisplayFormModelName(String displayFormModelName) {
        this.displayFormModelName = displayFormModelName;
    }

    /**
     * @return the displayFormModelId
     */
    public String getDisplayFormModelId() {
        return displayFormModelId;
    }

    /**
     * @param displayFormModelId 要设置的displayFormModelId
     */
    public void setDisplayFormModelId(String displayFormModelId) {
        this.displayFormModelId = displayFormModelId;
    }

    /**
     * @return the applyTo
     */
    public String getApplyTo() {
        return applyTo;
    }

    /**
     * @param applyTo 要设置的applyTo
     */
    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    /**
     * @return the printTemplateId
     */
    public String getPrintTemplateId() {
        return printTemplateId;
    }

    /**
     * @param printTemplateId 要设置的printTemplateId
     */
    public void setPrintTemplateId(String printTemplateId) {
        this.printTemplateId = printTemplateId;
    }

    /**
     * @return the printTemplateName
     */
    public String getPrintTemplateName() {
        return printTemplateName;
    }

    /**
     * @param printTemplateName 要设置的printTemplateName
     */
    public void setPrintTemplateName(String printTemplateName) {
        this.printTemplateName = printTemplateName;
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

    /**
     * @return the relationTbl
     */
    public String getRelationTbl() {
        return relationTbl;
    }

    /**
     * @param relationTbl 要设置的relationTbl
     */
    public void setRelationTbl(String relationTbl) {
        this.relationTbl = relationTbl;
    }

    /**
     * @return the html
     */
    public String getHtml() {
        return html;
    }

    /**
     * @param html 要设置的html
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName 要设置的moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the enableSignature
     */
    public String getEnableSignature() {
        return enableSignature;
    }

    /**
     * @param enableSignature 要设置的enableSignature
     */
    public void setEnableSignature(String enableSignature) {
        this.enableSignature = enableSignature;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the versionFormat
     */
    public DecimalFormat getVersionFormat() {
        return versionFormat;
    }

    /**
     * @param versionFormat 要设置的versionFormat
     */
    public void setVersionFormat(DecimalFormat versionFormat) {
        this.versionFormat = versionFormat;
    }

    /**
     * @return the isUp
     */
    public String getIsUp() {
        return isUp;
    }

    /**
     * @param isUp 要设置的isUp
     */
    public void setIsUp(String isUp) {
        this.isUp = isUp;
    }

    /**
     * @return the minVersion
     */
    public String getMinVersion() {
        return minVersion;
    }

}
