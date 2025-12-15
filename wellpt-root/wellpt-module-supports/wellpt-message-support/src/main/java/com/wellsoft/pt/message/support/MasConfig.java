/*
 * @(#)2013-12-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.support;

import com.wellsoft.context.util.PropertiesUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
 * 2013-12-11.1	zhulh		2013-12-11		Create
 * </pre>
 * @date 2013-12-11
 */
public class MasConfig {
    public static final String KEY_SERVER_IP = "mas.server.ip";
    public static final String KEY_API_USER = "mas.api.user";
    public static final String KEY_API_PASSWORD = "mas.api.password";
    public static final String KEY_API_CODE = "mas.api.code";
    public static final String KEY_DATABASE_NAME = "mas.database.name";
    private static Logger LOGGER = LoggerFactory.getLogger(MasConfig.class);
    private Properties masProp = new Properties();

    /**
     * @param apiCode
     */
    public MasConfig() {
        super();
        try {
            masProp = PropertiesUtils.loadProperties("mas/mas.properties");
        } catch (IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * @return the serverIp
     */
    public String getServerIp() {
        String serverIp = masProp.getProperty(KEY_SERVER_IP);
        return serverIp;
    }

    /**
     * @return the apiUser
     */
    public String getApiUser() {
        String apiUser = masProp.getProperty(KEY_API_USER);
        return apiUser;
    }

    /**
     * @return the apiPassword
     */
    public String getApiPassword() {
        String apiPassword = masProp.getProperty(KEY_API_PASSWORD);
        return apiPassword;
    }

    /**
     * @return the apiCode
     */
    public String getApiCode() {
        String apiCode = masProp.getProperty(KEY_API_CODE);
        return apiCode;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        String databaseName = masProp.getProperty(KEY_DATABASE_NAME);
        return databaseName;
    }

}
