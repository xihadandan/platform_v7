/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;

/**
 * Description: QueryParameter
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月13日.1	zhongzh		2017年10月13日		Create
 * </pre>
 * @date 2017年10月13日
 */
public class QueryParameter extends LinkedHashMap<String, String> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public QueryParameter(String query) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(query) && query.contains("=")) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                put(key, value);
            }
        }
    }

    public String getString(String param) {
        return get(param);
    }

    public Boolean getBoolean(String param) {
        return Boolean.valueOf(get(param));
    }
}
