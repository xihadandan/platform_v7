/*
 * @(#)2018年6月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.recentuse.service.impl;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.common.recentuse.dao.impl.RecentUseDaoImpl;
import com.wellsoft.pt.common.recentuse.entity.RecentUseEntity;
import com.wellsoft.pt.common.recentuse.service.RecentUseService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.mapping.Property;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.util.*;

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
public class RecentUseServiceImpl extends AbstractJpaServiceImpl<RecentUseEntity, RecentUseDaoImpl, String> implements
        RecentUseService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.service.RecentUseService#use(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void use(String objectIdIdentity, String moduleId) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        RecentUseEntity entity = new RecentUseEntity();
        entity.setObjectIdIdentity(objectIdIdentity);
        entity.setUserId(userId);
        entity.setModuleId(moduleId);
        List<RecentUseEntity> recentUseEntities = this.dao.listByEntity(entity);
        if (recentUseEntities.isEmpty()) {
            RecentUseEntity recentUseEntity = new RecentUseEntity();
            recentUseEntity.setObjectIdIdentity(objectIdIdentity);
            recentUseEntity.setUserId(userId);
            recentUseEntity.setUseTime(Calendar.getInstance().getTime());
            recentUseEntity.setModuleId(moduleId);
            this.dao.save(recentUseEntity);
        } else {
            for (RecentUseEntity recentUseEntity : recentUseEntities) {
                recentUseEntity.setUseTime(Calendar.getInstance().getTime());
                this.dao.save(recentUseEntity);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.service.RecentUseService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, String objectIdInItemProperty) {
        Collection<String> ignoreProperties = Collections.emptyList();
        return this.queryRecentUseList(userId, moduleId, itmeClass, objectIdInItemProperty, ignoreProperties);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.service.RecentUseService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.String, java.lang.Class, java.util.Collection)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, String objectIdInItemProperty, Collection<String> ignoreProperties) {
        List<String> propertyNames = Collections.emptyList();
        return this.queryRecentUseList(userId, moduleId, itmeClass, propertyNames, objectIdInItemProperty,
                ignoreProperties);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.recentuse.service.RecentUseService#queryRecentUseList(java.lang.String, java.lang.String, java.lang.Class, java.util.List, java.lang.String, java.util.Collection)
     */
    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> queryRecentUseList(String userId, String moduleId,
                                                                      Class<ITEM> itmeClass, List<String> propertyNames, String objectIdInItemProperty,
                                                                      Collection<String> ignoreProperties) {
        // 获取查询的属性
        List<Property> properties = getQueryProperties(itmeClass, propertyNames, ignoreProperties);
        String tableName = itmeClass.getAnnotation(Table.class).name();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tableName", tableName);
        values.put("userId", userId);
        values.put("moduleId", moduleId);
        values.put("objectIdInItemProperty", objectIdInItemProperty);
        values.put("properties", properties);
        return this.listItemByNameSQLQuery("queryRecentUseList", itmeClass, values, new PagingInfo(1, 20, false));
    }

    /**
     * 如何描述该方法
     *
     * @param itmeClass
     * @param propertyNames
     * @param ignoreProperties
     * @return
     */
    private <ITEM extends BaseQueryItem> List<Property> getQueryProperties(Class<ITEM> itmeClass,
                                                                           List<String> propertyNames, Collection<String> ignoreProperties) {
        List<Property> properties = new ArrayList<Property>();
        if (CollectionUtils.isEmpty(propertyNames)) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(itmeClass);
            PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                String columnName = ImprovedNamingStrategy.INSTANCE.propertyToColumnName(propertyName);
                if (StringUtils.equals(propertyName, "class")) {
                    continue;
                }
                if (ignoreProperties.contains(propertyName)) {
                    continue;
                }
                Property property = new Property();
                property.setName(propertyName);
                property.setPropertyAccessorName(columnName);
                properties.add(property);
            }
        } else {
            for (String propertyName : propertyNames) {
                Property property = new Property();
                property.setName(propertyName);
                property.setPropertyAccessorName(ImprovedNamingStrategy.INSTANCE.propertyToColumnName(propertyName));
                properties.add(property);
            }
        }
        return properties;
    }

}
