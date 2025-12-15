/*
 * @(#)2018年1月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.config;

import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.util.ApplicationContextHolder;

/**
 * Description: 系统参数配置工具类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月16日.1	chenqiong		2018年1月16日		Create
 * </pre>
 * @date 2018年1月16日
 */
public class SystemParamsUtils {

    private static final String SYSTEMPARAM_ID = "SystemParam";

    public static String getCacheKey(String key) {
        return SYSTEMPARAM_ID + "." + key;
    }

    public static String getValue(String key) {
        return ApplicationContextHolder.getBean(SystemParamsFacadeService.class).getValue(key);
    }

    public static String getValue(String key, String defaultValue) {
        return ApplicationContextHolder.getBean(SystemParamsFacadeService.class).getValue(key, defaultValue);
    }

}
