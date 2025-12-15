/*
 * @(#)2015-9-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-29.1	zhulh		2015-9-29		Create
 * </pre>
 * @date 2015-9-29
 */
public class DyformFieldError implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8049125290449825418L;

    private final String fieldName;

    private final Object rejectedValue;

    private final String checkedRule;

    private final String message;

    private final String formId;

    private final String dataUuid;

    private final boolean isSubformField;

    /**
     * 如何描述该构造方法
     *
     * @param fieldName
     * @param rejectedValue
     * @param checkedRule
     * @param message
     * @param formId
     * @param dataUuid
     * @param isSubformField
     */
    public DyformFieldError(String fieldName, Object rejectedValue, String checkedRule, String message, String formId,
                            String dataUuid, boolean isSubformField) {
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
        this.checkedRule = checkedRule;
        this.message = message;
        this.formId = formId;
        this.dataUuid = dataUuid;
        this.isSubformField = isSubformField;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the rejectedValue
     */
    public Object getRejectedValue() {
        return rejectedValue;
    }

    /**
     * @return the checkedRule
     */
    public String getCheckedRule() {
        return checkedRule;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @return the isSubformField
     */
    public boolean isSubformField() {
        return isSubformField;
    }

}
