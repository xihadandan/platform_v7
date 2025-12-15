/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.parse;

import com.wellsoft.pt.app.context.AppContextParser;
import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.LinkedHashMap;
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
 * 2017-01-24.1	zhulh		2017-01-24		Create
 * </pre>
 * @date 2017-01-24
 */
public abstract class AbstractAppContextParser implements AppContextParser {

    protected Logger logger = LoggerFactory.getLogger(CssFileParser.class);

    /**
     * @param configurationSupport
     * @return
     */
    protected Map<String, String> extractValues(AppContextPropertiesConfigurationSupport configurationSupport,
                                                String... keyPrefixs) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        Enumeration<?> keys = configurationSupport.keys();
        while (keys.hasMoreElements()) {
            String key = StringUtils.trim(keys.nextElement().toString());
            for (String keyPrefix : keyPrefixs) {
                if (key.startsWith(keyPrefix) || key.startsWith(keyPrefix)) {
                    values.put(key, StringUtils.trim(configurationSupport.getProperty(key)));
                }
            }
        }
        return values;
    }
}
