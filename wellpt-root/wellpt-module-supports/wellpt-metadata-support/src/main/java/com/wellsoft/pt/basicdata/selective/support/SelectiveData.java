/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class SelectiveData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 529778616538839474L;

    private String configKey;

    // 数据提供者(数据字典，其他等)
    private String provider;

    private Object items;

    private String itemLabel = DataItemConstants.LABEL;

    private String itemValue = DataItemConstants.VALUE;

    // 是否可缓存
    private boolean cacheable = true;
    // 可受权的
    private boolean authorise = false;

    private String cacheName = ModuleID.BASIC_DATA.getName();

    /**
     * @return the configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * @param configKey 要设置的configKey
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider 要设置的provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return the items
     */
    public Object getItems() {
        if (authorise) {
            return doSecurityFilter(items);
        }
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(Object items) {
        this.items = items;
    }

    /**
     * 权限过滤
     *
     * @param items
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Object doSecurityFilter(Object itemsObject) {
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
        // 数据列表
        if (itemsObject.getClass().isArray()) {
            // 判断是否为DataItem
            boolean isDataItem = false;
            Object[] itemsArray = (Object[]) itemsObject;
            for (Object item : itemsArray) {
                if (DataItem.class.isAssignableFrom(item.getClass())) {
                    isDataItem = true;
                    break;
                } else {
                    isDataItem = false;
                    break;
                }
            }
            // DataItem权限过滤
            if (isDataItem) {

                List<DataItem> dataItems = new ArrayList<DataItem>();
                for (Object item : itemsArray) {
                    DataItem dataItem = (DataItem) item;
                    if (StringUtils.isBlank(dataItem.getAceId()) || securityAuditFacadeService.isGranted(dataItem.getAceId())) {
                        dataItems.add(dataItem);
                    }
                }
                return dataItems;
            }
        } else if (itemsObject instanceof Collection) {
            // 集合列表
            // 判断是否为DataItem
            boolean isDataItem = false;
            final Collection optionCollection = (Collection) itemsObject;
            for (Object item : optionCollection) {
                if (DataItem.class.isAssignableFrom(item.getClass())) {
                    isDataItem = true;
                    break;
                } else {
                    isDataItem = false;
                    break;
                }
            }
            // DataItem权限过滤
            if (isDataItem) {
                List<DataItem> dataItems = new ArrayList<DataItem>();
                for (Object item : optionCollection) {
                    DataItem dataItem = (DataItem) item;
                    if (StringUtils.isBlank(dataItem.getAceId()) || securityAuditFacadeService.isGranted(dataItem.getAceId())) {
                        dataItems.add(dataItem);
                    }
                }
                return dataItems;
            }
        } else if (itemsObject instanceof Map) {
            // Map对象
        }
        return itemsObject;
    }

    /**
     * @return the itemLabel
     */
    public String getItemLabel() {
        return itemLabel;
    }

    /**
     * @param itemLabel 要设置的itemLabel
     */
    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    /**
     * @return the itemValue
     */
    public String getItemValue() {
        return itemValue;
    }

    /**
     * @param itemValue 要设置的itemValue
     */
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    /**
     * @return the cacheable
     */
    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * @param cacheable 要设置的cacheable
     */
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * @return the authorise
     */
    public boolean isAuthorise() {
        return authorise;
    }

    /**
     * @param authorise 要设置的authorise
     */
    public void setAuthorise(boolean authorise) {
        this.authorise = authorise;
    }

    /**
     * @return the cacheName
     */
    public String getCacheName() {
        return cacheName;
    }

    /**
     * @param cacheName 要设置的cacheName
     */
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

}
