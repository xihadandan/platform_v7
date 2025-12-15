/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
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
public abstract class AbstractCdFieldRender implements ICdFieldRender, ICdFieldDictionary {

    protected ICdFieldDefinition define;

    public AbstractCdFieldRender(ICdFieldDefinition define) {
        this.define = define;
    }

    @Override
    public Object getValue(Object data, Object value) {
        return data == null ? "" : data;
    }

    @Override
    public String getOptionalAttributes() {
        return "";
    }

    @Override
    public String getNoDataShow() {
        return MsgUtils.getMessage("fdext.nodata2show.message");
    }

    @Override
    public String getLabel() {
        if (isRequire()) {
            return getName() + REMARK_REQUIRE;
        }
        return getName();
    }

    @Override
    public boolean isRequire() {
        if (StringUtils.isBlank(getValidationRule())) {
            return false;
        }
        return getValidationRule().contains(VALIDATION_REQUIRE);
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public Collection<DataItem> getDataItems() {
        return Collections.emptyList();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getCreator()
     */
    @Override
    public final String getCreator() {
        return define.getCreator();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getCreateTime()
     */
    @Override
    public final Date getCreateTime() {
        return define.getCreateTime();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getModifier()
     */
    @Override
    public final String getModifier() {
        return define.getModifier();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getModifyTime()
     */
    @Override
    public final Date getModifyTime() {
        return define.getModifyTime();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getName()
     */
    @Override
    public final String getName() {
        return define.getName();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getFieldName()
     */
    @Override
    public final String getFieldName() {
        return define.getFieldName();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getDefaultValue()
     */
    @Override
    public final String getDefaultValue() {
        return define.getDefaultValue();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getSortOrder()
     */
    @Override
    public final Integer getSortOrder() {
        return define.getSortOrder();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getEnabled()
     */
    @Override
    public final Integer getEnabled() {
        return define.getEnabled();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getGroupCode()
     */
    @Override
    public final String getGroupCode() {
        return define.getGroupCode();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getInputType()
     */
    @Override
    public final String getInputType() {
        return define.getInputType();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getCfgKey()
     */
    @Override
    public final String getCfgKey() {
        return define.getCfgKey();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getCfgKeyName()
     */
    @Override
    public final String getCfgKeyName() {
        return define.getCfgKeyName();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getDateFormat()
     */
    @Override
    public final String getDateFormat() {
        return define.getDateFormat();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getValidationRule()
     */
    @Override
    public final String getValidationRule() {
        return define.getValidationRule();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getConstraintValue()
     */
    @Override
    public final String getConstraintValue() {
        return define.getConstraintValue();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getTenantId()
     */
    @Override
    public final String getTenantId() {
        return define.getTenantId();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.support.ICdFieldDefinition#getRemark()
     */
    @Override
    public final String getRemark() {
        return define.getRemark();
    }
}
