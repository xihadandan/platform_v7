/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.security;

import com.wellsoft.pt.integration.support.MerlinCrypto;
import org.apache.commons.lang.StringUtils;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Properties;

/**
 * Description: 服务器证书私钥密码回调设置类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-11.1	zhulh		2013-11-11		Create
 * </pre>
 * @date 2013-11-11
 */
public class ServerPasswordCallback implements CallbackHandler {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
            if (StringUtils.isBlank(pc.getIdentifier())) {
                throw new RuntimeException("服务器找不到相应的证书别名!");
            }

            // 签名解密
            Properties properties = MerlinCrypto.getInstace().getKeypassProperties();
            String pwd = StringUtils.trim(properties.getProperty(pc.getIdentifier(), ""));
            pc.setPassword(pwd);
        }
    }

}
