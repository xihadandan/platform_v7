/*
 * @(#)2015年9月22日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.suport;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.web.ServletUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
 * 2015年9月22日.1	zhulh		2015年9月22日		Create
 * </pre>
 * @date 2015年9月22日
 */
public class RequestUtils {

    private static final String KEY_REQUEST_AUTH = "api.restful.webservice.auth";
    private static final String KEY_IGNORE_LOGIN_IP = "api.restful.webservice.ignore_login.ip";
    private static final String FALSE = "false";

    public static final boolean isRequestAuth() {
        return Boolean.valueOf(Config.getValue(KEY_REQUEST_AUTH, FALSE));
    }

    public static final boolean isAllowIgnoreLogin(HttpServletRequest request) {
        // 忽略登录IP地址，为空则允许忽略登录
        String ignoreLoginIp = Config.getValue(KEY_IGNORE_LOGIN_IP);
        if (StringUtils.isBlank(ignoreLoginIp)) {
            return true;
        }

        List<String> ignoreLoginIps = Arrays.asList(StringUtils.split(ignoreLoginIp, Separator.COMMA.getValue()));
        // 获得客户端真实的IP地址
        String clientIp = ServletUtils.getRemoteAddr(request);
        if (ignoreLoginIps.contains(clientIp)) {
            return true;
        }

        return false;
    }

}
