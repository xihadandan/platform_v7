/*
 * @(#)2020年1月20日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.ext.enumcls.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.enumtool.EnumClassUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map.Entry;

/**
 * Description: 枚举类数据仓库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月20日.1	zhulh		2020年1月20日		Create
 * </pre>
 * @date 2020年1月20日
 */
@Component
public class EnumClassDataStoreQuery extends AbstractDataStoreQueryInterface {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台公共功能_枚举类_数据仓库查询";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Class<?> enumClass = this.getEnumClass(queryContext);
        if (enumClass != null) {
            List<String> propertyNames = EnumClassUtils.getPropertyNames(enumClass);
            for (String propertyName : propertyNames) {
                criteriaMetadata.add(propertyName, propertyName, String.class);
            }
        }
        return criteriaMetadata;
    }

    /**
     * @param queryContext
     * @return
     */
    private Class<?> getEnumClass(QueryContext queryContext) {
        EnumClassDataStoreInterfaceParam param = queryContext.interfaceParam(EnumClassDataStoreInterfaceParam.class);
        String className = null;
        if (param == null) {
            className = queryContext.getInterfaceParam();//兼容旧的参数文本模式
        } else {
            className = param.getClassName();
        }
        try {
            if (StringUtils.isNotBlank(className)) {
                return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = Lists.newArrayList();
        Class<?> enumClass = getEnumClass(queryContext);
        if (enumClass == null) {
            return queryItems;
        }
        try {
            Object[] enumObjects = EnumClassUtils.getEnumObjects(enumClass);
            List<String> propertyNames = EnumClassUtils.getPropertyNames(enumClass);
            for (Object object : enumObjects) {
                BeanWrapper beanWrapper = new BeanWrapperImpl(object);
                QueryItem queryItem = new QueryItem();
                for (String propertyName : propertyNames) {
                    queryItem.put(propertyName, beanWrapper.getPropertyValue(propertyName));
                }
                queryItems.add(queryItem);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        queryItems = filter(queryItems, queryContext);
        queryContext.getPagingInfo().setTotalCount(queryItems.size());
        return queryItems;
    }

    /**
     * @param queryItems
     * @param queryContext
     * @return
     */
    private List<QueryItem> filter(List<QueryItem> queryItems, QueryContext queryContext) {
        String keyword = queryContext.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            return queryItems;
        }
        List<QueryItem> retItems = Lists.newArrayList();
        for (QueryItem queryItem : queryItems) {
            for (Entry<String, Object> entry : queryItem.entrySet()) {
                if (StringUtils.contains(ObjectUtils.toString(entry.getValue()), keyword)) {
                    retItems.add(queryItem);
                    break;
                }
            }
        }
        return retItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getPagingInfo().getTotalCount();
    }

    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return EnumClassDataStoreInterfaceParam.class;
    }

}
