/*
 * @(#)2020年2月16日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月16日.1	wangrf		2020年2月16日		Create
 * </pre>
 * @date 2020年2月16日
 */
@Entity
@Table(name = "dyform_field_value_rel")
@DynamicInsert
@DynamicUpdate
public class DyformFieldValueRel extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3310273786448626529L;

    private String formId;

    // 字段显示值
    private String fieldText;

    // 字段真实值
    private String fieldValue;

    // 字段名称
    private String fieldCode;

    //
    private String dataUuid;

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
     * @return the fieldText
     */
    public String getFieldText() {
        return fieldText;
    }

    /**
     * @param fieldText 要设置的fieldText
     */
    public void setFieldText(String fieldText) {
        this.fieldText = fieldText;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue 要设置的fieldValue
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the fieldCode
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * @param fieldCode 要设置的fieldCode
     */
    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

}
