/*
 * @(#)2013-12-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.UserPropertyDao;
import com.wellsoft.pt.org.entity.UserProperty;

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
 * 2013-12-19.1	zhulh		2013-12-19		Create
 * </pre>
 * @date 2013-12-19
 */
public interface UserPropertyService extends JpaService<UserProperty, UserPropertyDao, String> {

    List<UserProperty> findByExample(UserProperty example);

    /**
     * 设置当前用户的配置数据
     *
     * @param userProperty
     */
    void saveUserProperty(UserProperty userProperty);

    /**
     * 获取当前用户的配置数据
     *
     * @param propertyName
     * @return
     */
    UserProperty getUserProperty(String propertyName);

    String getUserPropertyValue(String prop, String userUuid);

    String getCurrentUserPropertyValue(String prop);


    void saveUserPropertyValue(String prop, String value);

    void deleteByUser(String userUuid);

    public abstract void saveUserPropertyValue(String userId, String prop, String value);

    public abstract String getCurrentUserPropertyValue(String userId, String prop);
}
