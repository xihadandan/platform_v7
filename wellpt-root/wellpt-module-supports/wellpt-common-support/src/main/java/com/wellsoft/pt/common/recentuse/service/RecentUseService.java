/*
 * @(#)2018年6月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.recentuse.service;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

import java.util.Collection;
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
 * 2018年6月15日.1	zhulh		2018年6月15日		Create
 * </pre>
 * @date 2018年6月15日
 */
public interface RecentUseService {

    /**
     * 使用对象
     *
     * @param objectIdIdentity
     * @param moduleId
     */
    void use(String objectIdIdentity, String moduleId);

    /**
     * 查询最近使用的实体对象，按最近使用时间降序，最多返回20条
     *
     * @param userId
     * @param moduleId
     * @param itmeClass
     * @param objectIdInItemProperty
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId, Class<ITEM> itmeClass,
                                                               String objectIdInItemProperty);

    /**
     * 查询最近使用的实体对象，按最近使用时间降序，最多返回20条
     *
     * @param userId
     * @param moduleId
     * @param itmeClass
     * @param objectIdInItemProperty
     * @param ignoreProperties
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId, Class<ITEM> itmeClass,
                                                               String objectIdInItemProperty, Collection<String> ignoreProperties);

    /**
     * 查询最近使用的实体对象，按最近使用时间降序，最多返回20条
     *
     * @param userId
     * @param moduleId
     * @param itmeClass
     * @param propertyNames
     * @param objectIdInItemProperty
     * @param ignoreProperties
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId, Class<ITEM> itmeClass,
                                                               List<String> propertyNames, String objectIdInItemProperty, Collection<String> ignoreProperties);

}
