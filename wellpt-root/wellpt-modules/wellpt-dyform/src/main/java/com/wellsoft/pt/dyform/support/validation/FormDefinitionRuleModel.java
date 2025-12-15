/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-28.1	zhulh		2015-9-28		Create
 * </pre>
 * @date 2015-9-28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDefinitionRuleModel implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 173384583720391504L;

    @JsonProperty(value = "name")
    private String formId;

    @JsonProperty(value = "outerId")
    private String tableName;

    private Map<String, FieldDefinitionRuleModel> fields = Collections.emptyMap();

    private Map<String, Object> subforms = Collections.emptyMap();

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

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
     * @return the fields
     */
    public Map<String, FieldDefinitionRuleModel> getFields() {
        return fields;
    }

    /**
     * @param fields 要设置的fields
     */
    public void setFields(Map<String, FieldDefinitionRuleModel> fields) {
        this.fields = fields;
    }

    /**
     * @return the subforms
     */
    public Map<String, Object> getSubforms() {
        return subforms;
    }

    /**
     * @param subforms 要设置的subforms
     */
    public void setSubforms(Map<String, Object> subforms) {
        this.subforms = subforms;
    }

}
