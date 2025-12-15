/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.support;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
public class ConfigJson implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3086544803943899100L;

    private String tableName;

    private List<String> tableOutputType;

    private String formDefUuid;

    private String formDefName;

    private List<String> formOutputType;

    private String navigationTemplateName;

    private String navigationTemplateUuid;

    private String parentNavigationName;

    private String parentNavigationUuid;

    private String resourceTemplateName;

    private String resourceTemplateUuid;

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName 要设置的tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the tableOutputType
     */
    public List<String> getTableOutputType() {
        return tableOutputType;
    }

    /**
     * @param tableOutputType 要设置的tableOutputType
     */
    public void setTableOutputType(List<String> tableOutputType) {
        this.tableOutputType = tableOutputType;
    }

    /**
     * @return the formDefUuid
     */
    public String getFormDefUuid() {
        return formDefUuid;
    }

    /**
     * @param formDefUuid 要设置的formDefUuid
     */
    public void setFormDefUuid(String formDefUuid) {
        this.formDefUuid = formDefUuid;
    }

    /**
     * @return the formDefName
     */
    public String getFormDefName() {
        return formDefName;
    }

    /**
     * @param formDefName 要设置的formDefName
     */
    public void setFormDefName(String formDefName) {
        this.formDefName = formDefName;
    }

    /**
     * @return the formOutputType
     */
    public List<String> getFormOutputType() {
        return formOutputType;
    }

    /**
     * @param formOutputType 要设置的formOutputType
     */
    public void setFormOutputType(List<String> formOutputType) {
        this.formOutputType = formOutputType;
    }

    /**
     * @return the navigationTemplateName
     */
    public String getNavigationTemplateName() {
        return navigationTemplateName;
    }

    /**
     * @param navigationTemplateName 要设置的navigationTemplateName
     */
    public void setNavigationTemplateName(String navigationTemplateName) {
        this.navigationTemplateName = navigationTemplateName;
    }

    /**
     * @return the navigationTemplateUuid
     */
    public String getNavigationTemplateUuid() {
        return navigationTemplateUuid;
    }

    /**
     * @param navigationTemplateUuid 要设置的navigationTemplateUuid
     */
    public void setNavigationTemplateUuid(String navigationTemplateUuid) {
        this.navigationTemplateUuid = navigationTemplateUuid;
    }

    /**
     * @return the parentNavigationName
     */
    public String getParentNavigationName() {
        return parentNavigationName;
    }

    /**
     * @param parentNavigationName 要设置的parentNavigationName
     */
    public void setParentNavigationName(String parentNavigationName) {
        this.parentNavigationName = parentNavigationName;
    }

    /**
     * @return the parentNavigationUuid
     */
    public String getParentNavigationUuid() {
        return parentNavigationUuid;
    }

    /**
     * @param parentNavigationUuid 要设置的parentNavigationUuid
     */
    public void setParentNavigationUuid(String parentNavigationUuid) {
        this.parentNavigationUuid = parentNavigationUuid;
    }

    /**
     * @return the resourceTemplateName
     */
    public String getResourceTemplateName() {
        return resourceTemplateName;
    }

    /**
     * @param resourceTemplateName 要设置的resourceTemplateName
     */
    public void setResourceTemplateName(String resourceTemplateName) {
        this.resourceTemplateName = resourceTemplateName;
    }

    /**
     * @return the resourceTemplateUuid
     */
    public String getResourceTemplateUuid() {
        return resourceTemplateUuid;
    }

    /**
     * @param resourceTemplateUuid 要设置的resourceTemplateUuid
     */
    public void setResourceTemplateUuid(String resourceTemplateUuid) {
        this.resourceTemplateUuid = resourceTemplateUuid;
    }

}
