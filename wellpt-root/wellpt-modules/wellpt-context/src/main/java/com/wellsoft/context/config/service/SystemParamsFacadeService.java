/*
 * @(#)2018年1月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.config.service;

import com.wellsoft.context.service.BaseService;

/**
 * 系统配置参数服务(具体实现在wellpt-basicdata模块下)
 */
public interface SystemParamsFacadeService extends BaseService {

    String getValue(String key);

    String getValue(String key, String defaultValue);
}
