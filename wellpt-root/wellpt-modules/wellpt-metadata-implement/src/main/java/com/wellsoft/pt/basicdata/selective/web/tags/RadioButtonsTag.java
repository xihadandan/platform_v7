/*
 * @(#)2015-9-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.web.tags;

import com.wellsoft.pt.basicdata.selective.support.WformTagUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-26.1	zhulh		2015-9-26		Create
 * </pre>
 * @date 2015-9-26
 */
public class RadioButtonsTag extends org.springframework.web.servlet.tags.form.RadioButtonsTag {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2468324123995863787L;

    private String configKey;

    /**
     * @return the configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * @param configKey 要设置的configKey
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.form.OptionsTag#getItems()
     */
    @Override
    protected Object getItems() {
        if (StringUtils.isNotBlank(this.configKey)) {
            return WformTagUtils.getItems(configKey);
        }

        return super.getItems();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.form.SelectTag#getItemLabel()
     */
    @Override
    protected String getItemLabel() {
        String itemLabel = super.getItemLabel();
        return WformTagUtils.getItemLabel(itemLabel, this.configKey);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.form.SelectTag#getItemValue()
     */
    @Override
    protected String getItemValue() {
        String itemValue = super.getItemValue();
        return WformTagUtils.getItemValue(itemValue, this.configKey);
    }

}
