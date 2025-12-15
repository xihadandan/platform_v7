/*
 * @(#)2018年6月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.recentuse.facade.service.impl;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService;
import com.wellsoft.pt.common.recentuse.service.RecentUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class RecentUseFacadeServiceImpl extends AbstractApiFacade implements RecentUseFacadeService {

    @Autowired
    private RecentUseService recentUseService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService#use(java.lang.String, java.lang.String)
     */
    public void use(String objectIdIdentity, String moduleId) {
        recentUseService.use(objectIdIdentity, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, String objectIdInItemProperty) {
        return recentUseService.queryRecentUseList(userId, moduleId, itmeClass, objectIdInItemProperty);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.Class, java.util.Collection, java.lang.String)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, String objectIdInItemProperty, Collection<String> ignoreProperties) {
        return recentUseService.queryRecentUseList(userId, moduleId, itmeClass, objectIdInItemProperty,
                ignoreProperties);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.Class, java.util.List, java.lang.String, java.util.List)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, List<String> queryProperties, String objectIdInItemProperty,
                                                                      List<String> ignoreProperties) {
        return recentUseService.queryRecentUseList(userId, moduleId, itmeClass, queryProperties,
                objectIdInItemProperty, ignoreProperties);
    }

}
