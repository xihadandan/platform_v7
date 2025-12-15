/*
 * @(#)2015年9月21日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.interceptor;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.web.ServletUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Description: IP地址限制拦截器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月21日.1	zhulh		2015年9月21日		Create
 * </pre>
 * @date 2015年9月21日
 */
public class IpAddressLimitInInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final String KEY_BIND_IP = "api.restful.webservice.bind.ip";
    private static final String KEY_LIMIT_IP = "api.restful.webservice.limit.ip";

    /**
     *
     */
    public IpAddressLimitInInterceptor() {
        super(Phase.RECEIVE);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(Message message) throws Fault {
        // 获取客户端的HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        // 1、IP绑定
        String bindIp = Config.getValue(KEY_BIND_IP);
        if (StringUtils.isNotBlank(bindIp)) {
            List<String> bindIps = Arrays.asList(StringUtils.split(bindIp, Separator.COMMA.getValue()));
            // 获得客户端访问的IP地址
            String serverName = request.getServerName();
            // IP绑定异常
            if (!bindIps.contains(serverName)) {
                throw new Fault(new IllegalAccessException("IP address " + serverName + " is not bind"));
            }
        }

        // 2、IP限制
        String limitIp = Config.getValue(KEY_LIMIT_IP);
        if (StringUtils.isBlank(limitIp)) {
            return;
        }
        List<String> limitIps = Arrays.asList(StringUtils.split(limitIp, Separator.COMMA.getValue()));
        // 获得客户端真实的IP地址
        String clientIp = ServletUtils.getRemoteAddr(request);
        if (limitIps.contains(clientIp)) {
            return;
        }
        // IP限制异常
        throw new Fault(new IllegalAccessException("IP address " + clientIp + " is limited"));
    }

}
