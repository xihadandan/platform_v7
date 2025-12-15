/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.service.impl;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.basicdata.datasource.dao.DynamicDataSourceHibernateDao;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.dao.CdDataStoreDefinitionDao;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryInterface;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.NamedQueryRepository;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月19日.1	xiem		2016年10月19日		Create
 * </pre>
 * @date 2016年10月19日
 */
@Service
@Transactional
public class CdDataStoreDefinitionServiceImpl extends BaseServiceImpl implements CdDataStoreDefinitionService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CdDataStoreDefinitionDao cdDataStoreDefinitionDao;
    @Autowired
    private DynamicDataSourceHibernateDao dynamicDataSourceHibernateDao;
    @Autowired
    private List<AbstractDataStoreQueryInterface> queryInterfaces;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private DbLinkConfigService dbLinkConfigService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#getBeanById(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public CdDataStoreDefinitionBean getBeanById(String id) {
        CdDataStoreDefinition dataStoreDefinition = cdDataStoreDefinitionDao.findById(id);
        if (dataStoreDefinition == null)
            return null;
        CdDataStoreDefinitionBean bean = new CdDataStoreDefinitionBean();
        BeanUtils.copyProperties(dataStoreDefinition, bean);
        return bean;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#saveBean(com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String saveBean(CdDataStoreDefinitionBean bean) {
        CdDataStoreDefinition definition = new CdDataStoreDefinition();
        if (cdDataStoreDefinitionDao.idIsExists(bean.getId(), bean.getUuid())) {
            throw new RuntimeException("ID已经存在！");
        }
        if (StringUtils.isNotBlank(bean.getUuid())) {
            definition = cdDataStoreDefinitionDao.getOne(bean.getUuid());
        } else {
            bean.setUuid(null);
        }
        BeanUtils.copyProperties(bean, definition);
        this.cdDataStoreDefinitionDao.save(definition);
        return definition.getUuid();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#deleteByIds(java.lang.String[])
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByUuids(String[] uuids) {
        for (String uuid : uuids) {
            this.cdDataStoreDefinitionDao.delete(uuid);
        }
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByIds(String[] ids) {
        this.cdDataStoreDefinitionDao.deleteByIds(ids);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<CdDataStoreDefinition> dataStore = Lists.newArrayList();
        String currrentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (queryInfo.getParams().containsKey("id")) {
            dataStore.addAll(cdDataStoreDefinitionDao.listSimpleEntityByIds(new String[]{queryInfo.getParams().get("id").toString()}));
        }
        if (!MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currrentUserUnitId)) {//查询当前单位的数据仓库
            if (StringUtils.isNotBlank(currrentUserUnitId)) {
                List<CdDataStoreDefinition> list = cdDataStoreDefinitionDao.listSimpleEntityBySystemUnitId(currrentUserUnitId);
                dataStore.addAll(cdDataStoreDefinitionDao.listSimpleEntityBySystemUnitId(currrentUserUnitId));
            }
        }
        dataStore.addAll(cdDataStoreDefinitionDao.listSimpleEntityBySystemUnitId(MultiOrgSystemUnit.PT_ID));
        return new Select2QueryData(Maps.uniqueIndex(dataStore, cdDataStoreDefinition -> {
            return cdDataStoreDefinition.getId();
        }).values().asList(), "id", "name");
    }

    @Override
    public Select2QueryData loadSelectData2(Select2QueryInfo queryInfo) {
        List<String> systemUnitIds = Lists.newArrayList();
        String currrentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        // 查询当前单位的数据仓库
        if (!MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currrentUserUnitId)
                && StringUtils.isNotBlank(currrentUserUnitId)) {
            systemUnitIds.add(currrentUserUnitId);
        }
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitIds", systemUnitIds);
        params.put("name", queryInfo.getSearchValue());
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        pagingInfo.setAutoCount(true);
        List<CdDataStoreDefinition> list = cdDataStoreDefinitionDao.listByNameSQLQuery(
                "cdDataStoreDefinitionSelect2Query2", params, pagingInfo);
        return new Select2QueryData(list, "id", "name", pagingInfo);
    }

    @Override
    public Select2QueryData loadSelectData2ByIds(Select2QueryInfo queryInfo) {
        List<CdDataStoreDefinition> dataStore = cdDataStoreDefinitionDao.listSimpleEntityByIds(queryInfo.getIds());
        return new Select2QueryData(dataStore, "id", "name");
    }

    @Override
    public Select2QueryData loadColumnsSelectData(Select2QueryInfo queryInfo) {
        String id = queryInfo.getOtherParams("dataStoreId");
        return new Select2QueryData(getColumnsById(id), "columnIndex", "title");
    }

    @Override
    public List<CdDataStoreColumnBean> getColumnsById(String id) {
        CdDataStoreDefinition definition = this.getBeanById(id);
        if (definition == null) {
            return Collections.emptyList();
        }
        return definition.getColumnDefinitionBeans();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSelectDataByTable(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByTable(Select2QueryInfo queryInfo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", queryInfo.getSearchValue());
        params.put("tableName", queryInfo.getOtherParams("tableName"));
        params.put("tableSchema", Config.getValue("multi.tenancy.tenant.database_name"));
        NativeDao dao = this.nativeDao;
        if (queryInfo.getParams().containsKey("dbLinkConfUuid") && StringUtils.isNotBlank(queryInfo.getParams().get("dbLinkConfUuid").toString())) {
            SessionFactory sessionFactory = dbLinkConfigService.createLocalSessionFactory(Long.parseLong(queryInfo.getParams().get("dbLinkConfUuid").toString()));
            if (sessionFactory != null) {
                String tableSchema = ((SessionFactoryImpl) sessionFactory).getProperties().getProperty("tableSchema");
                if (StringUtils.isNotBlank(tableSchema)) {
                    params.put("tableSchema", tableSchema);
                }
                dao = ApplicationContextHolder.getBean(NativeDao.class);
                dao.setSessionFactory(sessionFactory, null);
            }

        }
        List<?> dataBaseTable = dao.namedQuery("queryAllDatabaseTables", params, queryInfo.getPagingInfo());
        return new Select2QueryData(dataBaseTable, 0, 0);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSelectDataByEntity(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByEntity(Select2QueryInfo queryInfo) {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Select2QueryData result = new Select2QueryData();
        for (String simpleName : entityClasses.keySet()) {
            result.addResultData(new Select2DataBean(entityClasses.get(simpleName).getName(), simpleName));
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSelectDataBySqlName(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataBySqlName(Select2QueryInfo queryInfo) {
        Select2QueryData result = new Select2QueryData();
        NamedQueryRepository namedQueryRepository = ((SessionFactoryImpl) (this.nativeDao.getSession()
                .getSessionFactory())).getNamedQueryRepository();
        try {
            Field namedSqlQueryDefinitionMapField = NamedQueryRepository.class
                    .getDeclaredField("namedSqlQueryDefinitionMap");
            namedSqlQueryDefinitionMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap = (Map<String, NamedSQLQueryDefinition>) namedSqlQueryDefinitionMapField
                    .get(namedQueryRepository);
            Set<String> addDone = Sets.newHashSet();
            for (String name : namedSqlQueryDefinitionMap.keySet()) {
                if (name.startsWith("mysql#") || name.startsWith("oracle#")) {
                    name = name.replaceFirst("mysql#", "");
                    name = name.replaceFirst("oracle#", "");
                    if (addDone.contains(name)) {
                        continue;
                    }
                }
                result.addResultData(new Select2DataBean(name, name));
                addDone.add(name);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSelectDataByDataInterface(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByDataInterface(Select2QueryInfo queryInfo) {
        Select2QueryData result = new Select2QueryData();
        OrderComparator.sort(queryInterfaces);
        for (QueryInterface query : queryInterfaces) {
            result.addResultData(new Select2DataBean(query.getClass().getName(), query.getQueryName()));
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadTableColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName) {
        Criteria criteria = this.nativeDao.createTableCriteria(tableName);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            SessionFactory sessionFactory = dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid);
            dao.setSessionFactory(sessionFactory, null);
        }
        Criteria criteria = dao.createTableCriteria(tableName);
        return loadColumns(criteria);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadTableColumns(java.lang.String, boolean)
     */
    @Override
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, boolean camelColumnIndex) {
        Criteria criteria = this.nativeDao.createTableCriteria(tableName);
        return loadColumns(criteria, camelColumnIndex);
    }

    @Override
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, boolean camelColumnIndex, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = dao.createTableCriteria(tableName);
        return loadColumns(criteria, camelColumnIndex);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSelectDataByView(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByView(Select2QueryInfo queryInfo) {
        List<?> dataBaseTable = dynamicDataSourceHibernateDao.getAllDataBaseViews();
        return new Select2QueryData(dataBaseTable, 0, 0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadViewColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName) {
        Criteria criteria = this.nativeDao.createTableCriteria(viewName);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = this.nativeDao.createTableCriteria(viewName);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, boolean camelColumnIndex, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = this.nativeDao.createTableCriteria(viewName);
        return loadColumns(criteria, camelColumnIndex);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadViewColumns(java.lang.String, boolean)
     */
    @Override
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, boolean camelColumnIndex) {
        Criteria criteria = this.nativeDao.createTableCriteria(viewName);
        return loadColumns(criteria, camelColumnIndex);
    }

    private List<CdDataStoreColumnBean> loadColumns(Criteria criteria) {
        return loadColumns(criteria, false);
    }

    private List<CdDataStoreColumnBean> loadColumns(Criteria criteria, boolean camelColumnIndex) {
        List<CdDataStoreColumnBean> columns = new ArrayList<CdDataStoreColumnBean>();
        if (criteria != null) {
            criteria.addQueryParams("currentUserName", SpringSecurityUtils.getCurrentUserName());
            criteria.addQueryParams("currentLoginName", SpringSecurityUtils.getCurrentLoginName());
            criteria.addQueryParams("currentUserId", SpringSecurityUtils.getCurrentUserId());
            criteria.addQueryParams("currentUserDepartmentId", SpringSecurityUtils.getCurrentUserDepartmentId());
            criteria.addQueryParams("currentUserDepartmentName", SpringSecurityUtils.getCurrentUserDepartmentName());
            criteria.addQueryParams("currentUserUnitId", SpringSecurityUtils.getCurrentUserUnitId());
            criteria.addQueryParams("currentTenantId", SpringSecurityUtils.getCurrentTenantId());
            if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
                criteria.addQueryParams("currentSystem", RequestSystemContextPathResolver.system());
            }
            criteria.addQueryParams("sysdate", new Date());

            CriteriaMetadata metadata = criteria.getCriteriaMetadata();
            for (int i = 0; i < metadata.length(); i++) {
                String columnIndex = metadata.getColumnIndex(i);
                String columnName = metadata.getMapColumnIndex(columnIndex);
                // 使用驼峰风格列索引
                if (camelColumnIndex) {
                    columnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
                }
                CdDataStoreColumnBean column = new CdDataStoreColumnBean(metadata.getComment(i), columnIndex,
                        columnName, metadata.getDataType(i).getType(), metadata.getColumnType(i));
                columns.add(column);
            }
        }
        return columns;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadEntityColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadEntityColumns(String entityName) {
        Criteria criteria = null;
        try {
            criteria = this.dao.createEntityCriteria(Class.forName(entityName));
        } catch (ClassNotFoundException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return loadColumns(criteria);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSqlNameColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName) {
        Criteria criteria = this.nativeDao.createNamedQueryCriteria(sqlName);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = dao.createNamedQueryCriteria(sqlName);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, boolean camelColumnIndex, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = dao.createNamedQueryCriteria(sqlName);
        return loadColumns(criteria, camelColumnIndex);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSqlNameColumns(java.lang.String, boolean)
     */
    @Override
    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, boolean camelColumnIndex) {
        Criteria criteria = this.nativeDao.createNamedQueryCriteria(sqlName);
        return loadColumns(criteria, camelColumnIndex);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadDataInterfaceColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadDataInterfaceColumns(String dataInterfaceName, String dataInterfaceParam) {
        Criteria criteria = null;
        if (StringUtils.isBlank(dataInterfaceName)) {
            return new ArrayList<>();
        }
        try {
            QueryInterface queryInterface = getByBeanName(extractBeanName(dataInterfaceName));
            if (queryInterface == null) {
                queryInterface = (QueryInterface) ApplicationContextHolder.getBean(Class.forName(dataInterfaceName));
            }
            criteria = this.nativeDao.createInterfaceCriteria(queryInterface, dataInterfaceParam);
        } catch (ClassNotFoundException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return loadColumns(criteria);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSqlColumns(java.lang.String)
     */
    @Override
    public List<CdDataStoreColumnBean> loadSqlColumns(String sql) {
        Criteria criteria = this.nativeDao.createSqlCriteria(sql);
        return loadColumns(criteria);
    }

    @Override
    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = dao.createSqlCriteria(sql);
        return loadColumns(criteria);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService#loadSqlColumns(java.lang.String, boolean)
     */
    @Override
    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, boolean camelColumnIndex) {

        Criteria criteria = this.nativeDao.createSqlCriteria(sql);
        return loadColumns(criteria, camelColumnIndex);
    }

    @Override
    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, boolean camelColumnIndex, Long dbLinkConfUuid) {
        NativeDao dao = this.nativeDao;
        if (dbLinkConfUuid != null) {
            dao = ApplicationContextHolder.getBean(NativeDao.class);
            dao.setSessionFactory(dbLinkConfigService.createLocalSessionFactory(dbLinkConfUuid), null);
        }
        Criteria criteria = dao.createSqlCriteria(sql);
        return loadColumns(criteria, camelColumnIndex);
    }

    // 获取接口使用说明
    @Override
    public String getInterfaceDesc(String dataInterfaceName) {
        if (StringUtils.isNotBlank(dataInterfaceName)) {
            try {
                QueryInterface queryInterface = getByBeanName(extractBeanName(dataInterfaceName));
                if (queryInterface == null) {
                    queryInterface = (QueryInterface) ApplicationContextHolder
                            .getBean(Class.forName(dataInterfaceName));
                }
                return queryInterface.getInterfaceDesc();
            } catch (Exception e) {
                logger.warn("无法找到对应的数据接口={}", dataInterfaceName);
            }
        }
        return null;
    }

    /**
     * @param beanName
     * @return
     */
    private QueryInterface getByBeanName(String beanName) {
        return (QueryInterface) ApplicationContextHolder.getBean(beanName);
    }

    /**
     * @param dataInterfaceName
     * @return
     */
    private String extractBeanName(String dataInterfaceName) {
        String[] beanNames = StringUtils.split(dataInterfaceName, Separator.DOT.getValue());
        String beanName = beanNames[beanNames.length - 1];
        beanName = StringUtils.uncapitalize(beanName);
        return beanName.endsWith("Impl") ? beanName.substring(0, beanName.length() - 4) : beanName;
    }

    @Override
    public Select2QueryData loadSelectDefinitionByModule(Select2QueryInfo select2QueryInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String moduleId = select2QueryInfo.getOtherParams("moduleId");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String excludeModuleIds = select2QueryInfo.getOtherParams("excludeModuleIds");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(excludeModuleIds)) {
            params.put("excludeModuleIds", Arrays.asList(excludeModuleIds.split(Separator.SEMICOLON.getValue())));
        }
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
        } else {
            List<String> systemUnitIdList = Lists.newArrayList(MultiOrgSystemUnit.PT_ID,
                    SpringSecurityUtils.getCurrentUserUnitId());
            params.put("systemUnitIds", systemUnitIdList);
        }
        List<CdDataStoreDefinition> formDefinitions = this.cdDataStoreDefinitionDao.listByNameHQLQuery(
                "queryAllCdDataStoreDefinition", params);
        return new Select2QueryData(formDefinitions, idProperty, "name");
    }
}
