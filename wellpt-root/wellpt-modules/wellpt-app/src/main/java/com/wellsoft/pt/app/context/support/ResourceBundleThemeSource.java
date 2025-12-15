/*
 * @(#)2016年3月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.support;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月25日.1	zhulh		2016年3月25日		Create
 * </pre>
 * @date 2016年3月25日
 */
public class ResourceBundleThemeSource extends org.springframework.ui.context.support.ResourceBundleThemeSource {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.springframework.ui.context.support.ResourceBundleThemeSource#createMessageSource(java.lang.String)
     */
    @Override
    protected MessageSource createMessageSource(String basename) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

}
