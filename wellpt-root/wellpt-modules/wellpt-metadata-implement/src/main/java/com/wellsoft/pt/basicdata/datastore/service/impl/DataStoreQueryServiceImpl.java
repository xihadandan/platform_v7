/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.service.impl;

import cn.hutool.core.convert.Convert;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import com.wellsoft.pt.basicdata.datastore.support.*;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
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
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
@Service
@Transactional(readOnly = true)
public class DataStoreQueryServiceImpl extends BaseServiceImpl implements DataStoreQueryService {

    public static final String DB_LINK_CONF_UUID = "dbLinkConfUuid";
    @Autowired
    private DbLinkConfigService dbLinkConfigService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService#createCriteriaQuery(com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams, com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration)
     */
    @Override
    public Criteria createCriteriaQuery(DataStoreParams params, DataStoreConfiguration configuration) {
        UniversalDao dao = this.dao;
        NativeDao nativeDao = this.nativeDao;
        if (DataStoreType.ENTITY.getType().equals(configuration.getType())
                && StringUtils.isNotBlank(configuration.getEntityName())) {
            try {
                Class<?> clazz = Class.forName(configuration.getEntityName());
                CommonEntity commonEntity = clazz.getAnnotation(CommonEntity.class);
                if (commonEntity != null) {
                    dao = getDao(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
                    nativeDao = getNativeDao(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        //切换数据源
        if(params.getParams().containsKey(DB_LINK_CONF_UUID))
        {
            Long dbLinkConfUuid = Convert.toLong(params.getParams().get(DB_LINK_CONF_UUID)) ;
            if(dbLinkConfUuid!=null)
            {
                configuration.setDbLinkConfUuid(dbLinkConfUuid);
            }
        }
        if (configuration.getDbLinkConfUuid() != null) {
            // 通过数据库连接创建
            SessionFactory sessionFactory = dbLinkConfigService.createLocalSessionFactory(configuration.getDbLinkConfUuid());
            dao = ApplicationContextHolder.getBean(UniversalDao.class);
            dao.setSessionFactory(sessionFactory, null);
            nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
            nativeDao.setSessionFactory(sessionFactory, null);
        }

        return DataStoreCriteriaFactory.getCriteria(nativeDao, dao, params, configuration);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService#query(com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryInfo)
     */
    @Override
    public DataStoreQueryData query(DataStoreQueryInfo queryInfo) {
        // 数据源配置
        String dataStoreId = queryInfo.getDataStoreId();
        DataStoreConfiguration dataStoreConfiguration = DataStoreConfigurationBuilder.buildFromDataStoreId(dataStoreId);
        if (dataStoreConfiguration == null) {
            logger.error("DataStore configuration is empty, return empty query data");
            DataStoreQueryData emptyQueryData = new DataStoreQueryData();
            emptyQueryData.setDataList(new ArrayList<Object>(0));
            emptyQueryData.setPagingInfo(queryInfo.getPagingInfo());
            return emptyQueryData;
        }
        UniversalDao dao = this.dao;
        NativeDao nativeDao = this.nativeDao;
        if (DataStoreType.ENTITY.getType().equals(dataStoreConfiguration.getType())
                && StringUtils.isNotBlank(dataStoreConfiguration.getEntityName())) {
            try {
                Class<?> clazz = Class.forName(dataStoreConfiguration.getEntityName());
                CommonEntity commonEntity = clazz.getAnnotation(CommonEntity.class);
                if (commonEntity != null) {
                    dao = getDao(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
                    nativeDao = getNativeDao(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        DataStoreQuery dataStoreQuery = new DataStoreQueryImpl(nativeDao, dao, dataStoreConfiguration);

        Map<String, Object> queryParams = queryInfo.getQueryParams();
        if (queryParams == null) {
            queryParams = new HashMap<String, Object>();
        }
        queryParams.putAll(TemplateEngineFactory.getExplainRootModel());

        // 是否去重
        dataStoreQuery.setDistinct(queryInfo.isDistinct());
        // 设置查询的列
        dataStoreQuery.setProjection(queryInfo.getProjection());
        // 设置查询字段条件
        dataStoreQuery.setConditions(queryInfo.getConditions());
        // 查询参数
        dataStoreQuery.setProperties(queryParams);
        // 排序信息
        dataStoreQuery.setOrders(queryInfo.getOrders());
        // 分页信息
        dataStoreQuery.setPagingInfo(queryInfo.getPagingInfo());

        List<Serializable> dataList = dataStoreQuery.list();

        DataStoreQueryData queryData = new DataStoreQueryData();
        queryData.setDataList(dataList);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService#createQuery(com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryInfo)
     */
    @Override
    public DataStoreQuery createQuery(DataStoreQueryInfo queryInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService#createQuery(java.lang.String)
     */
    @Override
    public DataStoreQuery createQuery(String dataStoreId) {
        // TODO Auto-generated method stub
        return null;
    }

}
