/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
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
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
@Entity
@Table(name = "cd_code_generator_config")
@DynamicUpdate
@DynamicInsert
public class CodeGeneratorConfig extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7511634192515196012L;
    private static final String KEY_COMPUTER_NAME = "COMPUTERNAME";

    // 计算机名称
    @UnCloneable
    private String machineName = System.getenv().get(KEY_COMPUTER_NAME);
    // 名称
    private String name;
    // 作者
    private String author;
    // 编号
    private String code;
    // 模板类型
    private String templateType;
    // 自定义模板文件目录
    private String customTemplateDir;
    // java文件输出目录
    private String javaFileOutputDir;
    // java类包名
    private String javaPackage;
    // pt/app模块请求路径
    private String moduleRequestPath;
    // jsp文件输出目录
    private String jspFileOutputDir;
    // js文件输出目录
    private String jsFileOutputDir;
    // 生成方式
    private String generateType;
    // 不同生成方式共同的定义信息(type、tableName、className、formUuid、flowDefUuid、outputType)
    private String configJson;

    /**
     * @return the machineName
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * @param machineName 要设置的machineName
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author 要设置的author
     */
    public void setAuthor(String author) {
        this.author = author;
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
     * @return the templateType
     */
    public String getTemplateType() {
        return templateType;
    }

    /**
     * @param templateType 要设置的templateType
     */
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    /**
     * @return the customTemplateDir
     */
    public String getCustomTemplateDir() {
        return customTemplateDir;
    }

    /**
     * @param customTemplateDir 要设置的customTemplateDir
     */
    public void setCustomTemplateDir(String customTemplateDir) {
        this.customTemplateDir = customTemplateDir;
    }

    /**
     * @return the javaFileOutputDir
     */
    public String getJavaFileOutputDir() {
        return javaFileOutputDir;
    }

    /**
     * @param javaFileOutputDir 要设置的javaFileOutputDir
     */
    public void setJavaFileOutputDir(String javaFileOutputDir) {
        this.javaFileOutputDir = javaFileOutputDir;
    }

    /**
     * @return the javaPackage
     */
    public String getJavaPackage() {
        return javaPackage;
    }

    /**
     * @param javaPackage 要设置的javaPackage
     */
    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    /**
     * @return the moduleRequestPath
     */
    public String getModuleRequestPath() {
        return moduleRequestPath;
    }

    /**
     * @param moduleRequestPath 要设置的moduleRequestPath
     */
    public void setModuleRequestPath(String moduleRequestPath) {
        this.moduleRequestPath = moduleRequestPath;
    }

    /**
     * @return the jspFileOutputDir
     */
    public String getJspFileOutputDir() {
        return jspFileOutputDir;
    }

    /**
     * @param jspFileOutputDir 要设置的jspFileOutputDir
     */
    public void setJspFileOutputDir(String jspFileOutputDir) {
        this.jspFileOutputDir = jspFileOutputDir;
    }

    /**
     * @return the jsFileOutputDir
     */
    public String getJsFileOutputDir() {
        return jsFileOutputDir;
    }

    /**
     * @param jsFileOutputDir 要设置的jsFileOutputDir
     */
    public void setJsFileOutputDir(String jsFileOutputDir) {
        this.jsFileOutputDir = jsFileOutputDir;
    }

    /**
     * @return the generateType
     */
    public String getGenerateType() {
        return generateType;
    }

    /**
     * @param generateType 要设置的generateType
     */
    public void setGenerateType(String generateType) {
        this.generateType = generateType;
    }

    /**
     * @return the configJson
     */
    public String getConfigJson() {
        return configJson;
    }

    /**
     * @param configJson 要设置的configJson
     */
    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

}
