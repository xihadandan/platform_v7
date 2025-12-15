/*
 * @(#)2016年2月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.validation.support;

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
 * 2016年2月1日.1	zhulh		2016年2月1日		Create
 * </pre>
 * @date 2016年2月1日
 */
public class FieldValidationMetaData {

    private List<FieldMetaData> fieldMetaDatas;

    /**
     * @return the fieldMetaDatas
     */
    public List<FieldMetaData> getFieldMetaDatas() {
        return fieldMetaDatas;
    }

    /**
     * @param fieldMetaDatas 要设置的fieldMetaDatas
     */
    public void setFieldMetaDatas(List<FieldMetaData> fieldMetaDatas) {
        this.fieldMetaDatas = fieldMetaDatas;
    }

}
