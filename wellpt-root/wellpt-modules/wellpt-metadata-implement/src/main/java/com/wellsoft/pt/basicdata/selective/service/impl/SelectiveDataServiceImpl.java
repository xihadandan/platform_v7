/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.selective.provider.SelectiveDataProvider;
import com.wellsoft.pt.basicdata.selective.service.SelectiveDataService;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import com.wellsoft.pt.basicdata.selective.support.SelectiveDataCacheUtils;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeNodeDataProvider;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
@Service
@Transactional(readOnly = true)
public class SelectiveDataServiceImpl extends BaseServiceImpl implements SelectiveDataService {


    @Autowired
    private Map<String, SelectiveDataProvider> providerMap = new HashMap<String, SelectiveDataProvider>();

    private Map<String, String> configKeyProvider = new HashMap<String, String>();


    @Autowired
    private List<TreeNodeDataProvider> treeNodeDataProviders;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.selective.service.SelectiveDataService#get(java.lang.String)
     */
    @Override
    public SelectiveData get(String configKey) {
        String cacheKey = SelectiveDataCacheUtils.getCacheKey(configKey);

        if (SelectiveDataCacheUtils.containsKey(configKey)) {
            CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
            Cache cache = (Cache) cacheManager.getCache(SelectiveDataCacheUtils.get(configKey));
            SelectiveData selectiveData = (SelectiveData) cache.getValue(cacheKey);
            if (selectiveData != null) {
                return selectiveData;
            }
        }

        String providerName = configKeyProvider.get(configKey);
        SelectiveData selectiveData = null;
        if (StringUtils.isNotBlank(providerName)) {
            SelectiveDataProvider selectiveDataProvider = providerMap.get(providerName);
            selectiveData = selectiveDataProvider.get(configKey);
        } else {
            for (String key : providerMap.keySet()) {
                SelectiveDataProvider selectiveDataProvider = providerMap.get(key);
                selectiveData = selectiveDataProvider.get(configKey);
                if (selectiveData != null && selectiveData.getItems() != null) {
                    configKeyProvider.put(configKey, key);
                    break;
                }
            }
        }
        if (selectiveData != null && selectiveData.isCacheable()) {
            CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
            Cache cache = (Cache) cacheManager.getCache(selectiveData.getCacheName());
            cache.put(cacheKey, selectiveData);
            SelectiveDataCacheUtils.put(configKey, selectiveData.getCacheName());
        }
        return selectiveData;
    }

    @Override
    public Select2QueryData getTreeNodeDataProviderSelect(Select2QueryInfo queryInfo) {
        Select2QueryData select2QueryData = new Select2QueryData();
        if (CollectionUtils.isNotEmpty(treeNodeDataProviders)) {
            for (TreeNodeDataProvider provider : treeNodeDataProviders) {
                select2QueryData.getResults().add(new Select2DataBean(StringUtils.uncapitalize(AopUtils.getTargetClass(provider).getSimpleName()), provider.name()));
            }
        }
        return select2QueryData;
    }


}
