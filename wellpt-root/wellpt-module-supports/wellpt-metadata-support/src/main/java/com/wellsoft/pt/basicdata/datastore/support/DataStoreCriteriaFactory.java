/*
 * @(#)2017年5月3日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceCriteria;
import com.wellsoft.pt.jpa.criteria.QueryInterface;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月3日.1	zhulh		2017年5月3日		Create
 * </pre>
 * @date 2017年5月3日
 */
public class DataStoreCriteriaFactory {
    private static Logger logger = LoggerFactory.getLogger(DataStoreCriteriaFactory.class);

    /**
     * @param dao
     * @param universalDao
     * @param params
     * @param configuration
     */
    public static Criteria getCriteria(NativeDao dao, UniversalDao universalDao, DataStoreParams params,
                                       DataStoreConfiguration configuration) {
        DataStoreProxy proxy = params.getProxy();
        if (proxy != null) {
            String proxyType = proxy.getType();
            if (StringUtils.isNotBlank(proxyType) && DataStoreType.TABLE.getType().equals(configuration.getType())) {
                Object[] initargs = new Object[]{dao, params, configuration};
                try {
                    Class<?> criteriaClass = Class.forName(proxyType);
                    Criteria criteria = (Criteria) (criteriaClass.getConstructor(NativeDao.class,
                            DataStoreParams.class, DataStoreConfiguration.class).newInstance(initargs));
                    initCriteriaMetadata(criteria, configuration);
                    return criteria;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return getCriteria(dao, universalDao, configuration);
    }

    /**
     * 如何描述该方法
     *
     * @param dao
     * @param configuration
     * @return
     */
    public static final Criteria getCriteria(NativeDao dao, UniversalDao universalDao,
                                             DataStoreConfiguration configuration) {
        Criteria criteria = null;
        String configType = configuration.getType();
        boolean initCriteriaMetadata = false;
        if (DataStoreType.TABLE.getType().equals(configType)) {
            criteria = dao.createTableCriteria(configuration.getTableName());
            initCriteriaMetadata = true;
        } else if (DataStoreType.VIEW.getType().equals(configType)) {
            criteria = dao.createTableCriteria(configuration.getViewName());
            initCriteriaMetadata = true;
        } else if (DataStoreType.ENTITY.getType().equals(configType)) {
            try {
                criteria = universalDao.createEntityCriteria(Class.forName(configuration.getEntityName()));
            } catch (ClassNotFoundException e) {
                logger.error(ExceptionUtils.getFullStackTrace(e));
                throw new RuntimeException("实体[" + configuration.getEntityName() + "]不存在！");
            }
        } else if (DataStoreType.SQL.getType().equals(configType)) {
            criteria = dao.createSqlCriteria(configuration.getSqlStatement());
            initCriteriaMetadata = true;
        } else if (DataStoreType.NAME_QUERY.getType().equals(configType)) {
            criteria = dao.createNamedQueryCriteria(configuration.getSqlName());
            initCriteriaMetadata = true;
        } else if (DataStoreType.DATA_INTERFACE.getType().equals(configType)) {
            try {
                String dataInterfaceName = configuration.getDataInterfaceName();
                String dataInterfaceParam = configuration.getDataInterfaceParam();
                QueryInterface queryInterface = null;
                try {
                    String bean = StringUtils.uncapitalize(dataInterfaceName.substring(dataInterfaceName
                            .lastIndexOf(".") + 1));
                    queryInterface = (QueryInterface) ApplicationContextHolder.getBean(bean,
                            Class.forName(dataInterfaceName));
                } catch (Exception ex) {
                    queryInterface = (QueryInterface) ApplicationContextHolder
                            .getBean(Class.forName(dataInterfaceName));
                }
                criteria = dao.createInterfaceCriteria(queryInterface, dataInterfaceParam);
                ((InterfaceCriteria) criteria).getContext().put("dataStoreConfiguration", configuration);
            } catch (ClassNotFoundException ex) {
                logger.error("查询接口[" + configuration.getDataInterfaceName() + "]不存在！", ex);
                throw new RuntimeException("查询接口[" + configuration.getDataInterfaceName() + "]不存在！");
            }

        }
        // 使用配置数据初始化当前条件查询的数据库信息
        if (initCriteriaMetadata) {
            initCriteriaMetadata(criteria, configuration);
        }

        return criteria;
    }

    /**
     * @param criteria
     * @param configuration
     */
    private static void initCriteriaMetadata(Criteria criteria, DataStoreConfiguration configuration) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Collection<DataStoreColumn> dataStoreColumns = configuration.getColumnMap().values();
        for (DataStoreColumn dataStoreColumn : dataStoreColumns) {
            criteriaMetadata.add(dataStoreColumn.getColumnIndex(), dataStoreColumn.getColumnName(),
                    dataStoreColumn.getTitle(), dataStoreColumn.getDataType(), dataStoreColumn.getColumnType());
        }
        criteria.setCriteriaMetadata(criteriaMetadata);
    }

}
