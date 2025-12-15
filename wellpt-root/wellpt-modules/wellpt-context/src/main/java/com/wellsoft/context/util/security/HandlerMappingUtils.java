/*
 * @(#)2016-09-7 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.security;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;

/**
 * Description: URL请求路径映射转换
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-7.1	zhulh		2016-09-7		Create
 * </pre>
 * @date 2016-09-7
 */
public class HandlerMappingUtils {

    /**
     * 如何描述STRING
     */
    private static final String DOT_ASTERISK = ".*";

    /**
     * 以"/"结尾不处理，包含"."号不处理，其他添加.*
     *
     * @param url
     * @return
     */
    public static String getRequestMappingUrl(String url) {
        String tmpUrl = StringUtils.trim(url);
        // 以"/"结尾不处理，包含"."号不处理，其他添加.*
        if (tmpUrl.endsWith(Separator.SLASH.getValue())) {
        } else if (tmpUrl.indexOf(Separator.DOT.getValue()) != -1) {
        } else {
            // 添加原URL
            tmpUrl += DOT_ASTERISK;
        }
        return tmpUrl;
    }

}
