/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.facade.service;

import com.wellsoft.context.service.Facade;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface SystemAccessFacadeService extends Facade {
    /**
     * 保存所有可访问系统的用户，以map(userids, usernames)的形式传递数据
     *
     * @param map
     */
    void saveAllFromMap(Map<String, String> map);

    /**
     * 以map(userids, usernames)的形式，返回所有用户ID及用户名
     *
     * @return
     */
    Map<String, String> getAllAsMap();
}
