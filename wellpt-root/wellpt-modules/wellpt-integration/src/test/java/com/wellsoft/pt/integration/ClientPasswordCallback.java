/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: 如何描述该类
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
public class ClientPasswordCallback implements CallbackHandler {
    private Map<String, String> passwords = new HashMap<String, String>();

    public ClientPasswordCallback() {
        passwords.put("server", "serverPassword");
        passwords.put("client", "clientPassword");
        passwords.put("oa_dev\\oa_dev", "oa_dev");
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
            URL url = ClassLoaderUtils.getResource("ca/keypass.properties", this.getClass());
            if (url != null) {
                Properties props = new Properties();
                InputStream in = url.openStream();
                props.load(in);
                in.close();
                String pwd = StringUtils.trim(props.getProperty(pc.getIdentifier(), ""));
                pc.setPassword(pwd);
            }
        }
    }

}
