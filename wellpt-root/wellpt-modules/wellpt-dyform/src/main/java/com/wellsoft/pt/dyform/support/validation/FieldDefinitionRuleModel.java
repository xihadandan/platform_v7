/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
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
public class FieldDefinitionRuleModel implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 9216524397255409897L;

    private String name;

    private String length;

    private List<Map<String, String>> fieldCheckRules = Collections.emptyList();

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
     * @return the length
     */
    public String getLength() {
        return length;
    }

    /**
     * @param length 要设置的length
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * @return the fieldCheckRules
     */
    public List<Map<String, String>> getFieldCheckRules() {
        return fieldCheckRules;
    }

    /**
     * @param fieldCheckRules 要设置的fieldCheckRules
     */
    public void setFieldCheckRules(List<Map<String, String>> fieldCheckRules) {
        this.fieldCheckRules = fieldCheckRules;
    }

}
