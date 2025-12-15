/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public interface IDyFieldDefinition {

    /**
     * @return creator
     */
    public String getCreator();

    /**
     * @return createTime
     */
    public Date getCreateTime();

    /**
     * @return modifier
     */
    public String getModifier();

    /**
     * @return modifyTime
     */
    public Date getModifyTime();

    /**
     * @return the name
     */
    public String getName();

    /**
     * @return the fieldName
     */
    public String getFieldName();

    /**
     * @return the defaultValue
     */
    public String getDefaultValue();

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder();

    /**
     * @return the enabled
     */
    public Integer getEnabled();

    /**
     * @return the groupCode
     */
    public String getGroupCode();

    /**
     * @return the inputType
     */
    public String getInputType();

    /**
     * @return the cfgKey
     */
    public String getCfgKey();

    /**
     * @return the cfgKeyName
     */
    public String getCfgKeyName();

    /**
     * @return the dateFormat
     */
    public String getDateFormat();

    /**
     * @return the validationRule
     */
    public String getValidationRule();

    /**
     * @return the constraintValue
     */
    public String getConstraintValue();

    /**
     * @return the tenantId
     */
    public String getTenantId();

    /**
     * @return the remark
     */
    public String getRemark();

}
