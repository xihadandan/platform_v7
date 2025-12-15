/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceDefinitionBean;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceProfileBean;
import com.wellsoft.pt.basicdata.datasource.dao.DataSourceDefinitionDao;
import com.wellsoft.pt.basicdata.datasource.dao.DataSourceProfileDao;
import com.wellsoft.pt.basicdata.datasource.dao.DynamicDataSourceHibernateDao;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceProfile;
import com.wellsoft.pt.basicdata.datasource.facade.service.impl.DataSourceApiFacadeImpl;
import com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDefinitionService;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService;
import com.wellsoft.pt.basicdata.datasource.support.DataSourceConfig;
import com.wellsoft.pt.basicdata.datasource.support.JdbcConnection;
import com.wellsoft.pt.basicdata.datasource.support.JdbcSupports;
import com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource;
import com.wellsoft.pt.basicdata.dyview.support.DyviewConfig;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.service.impl.GetViewDataNewServiceImpl;
import com.wellsoft.pt.basicdata.view.support.CondSelectAskInfoNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.basicdata.view.support.DyviewConfigNew;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * Description: 数据源的服务类的实现
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * <p>
 * <p>
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 * //          佛曰:
 * //                  写字楼里写字间，写字间里程序员；
 * //                  程序人员写程序，又拿程序换酒钱。
 * //                  酒醒只在网上坐，酒醉还来网下眠；
 * //                  酒醉酒醒日复日，网上网下年复年。
 * //                  但愿老死电脑间，不愿鞠躬老板前；
 * //                  奔驰宝马贵者趣，公交自行程序员。
 * //                  别人笑我忒疯癫，我笑自己命太贱；
 * //                  不见满街漂亮妹，哪个归得程序员？
 * <p>
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 佛祖保佑       永无BUG
 * @date 2014-7-31
 */
@Service
@Transactional
public class DataSourceDataServiceImpl extends BaseServiceImpl implements DataSourceDataService {
    private Logger logger = LoggerFactory.getLogger(DataSourceDataServiceImpl.class);
    @Autowired
    private DynamicDataSourceHibernateDao dynamicDataSourceHibernateDao;

    @Autowired
    private DataSourceProfileDao dataSourceProfileDao;

    @Autowired
    private DataSourceDefinitionDao dataSourceDefinitionDao;

    @Autowired(required = false)
    private Map<String, DataSourceProvider> dataSourceProviderMap;

    @Autowired
    private Map<String, ViewDataSource> viewDataSourceMap;

    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private DataSourceProfileService dataSourceProfileService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DataSourceApiFacadeImpl dataSourceApiFacade;

    @Autowired
    private GetViewDataNewServiceImpl getViewDataNewServiceImpl;

    @Autowired
    private AclService aclService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDefinitionService#getDataBaseTable(java.lang.String, java.lang.String)
     */
    @Override
    public List getDataBaseTable(String s, String id) {
        List dataBaseTable = dynamicDataSourceHibernateDao.getAllDataBaseTables();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Iterator iterator = dataBaseTable.iterator(); iterator.hasNext(); ) {
            List children = new ArrayList();
            // Map<String, String> map = (Map<String, String>) iterator.next();
            Object[] object = (Object[]) iterator.next();
            TreeNode treeNode = new TreeNode();
            treeNode.setId(((BigDecimal) object[1]).toString());
            treeNode.setName((String) object[0]);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 获得数据视图
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getDataBaseView(java.lang.String, java.lang.String)
     */
    @Override
    public List getDataBaseView(String s, String id) {
        List dataBaseView = dynamicDataSourceHibernateDao.getAllDataBaseViews();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Iterator iterator = dataBaseView.iterator(); iterator.hasNext(); ) {
            List children = new ArrayList();
            // Map<String, String> map = (Map<String, String>) iterator.next();
            Object[] object = (Object[]) iterator.next();
            TreeNode treeNode = new TreeNode();
            treeNode.setId(((BigDecimal) object[1]).toString());
            treeNode.setName((String) object[0]);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getColumnsByTable(java.lang.String)
     */
    @Override
    public List getColumnsByTable(String tableName) {
        List coulumns = dynamicDataSourceHibernateDao.getColumnsByTables(tableName);
        return coulumns;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getDataBaseInfoByOut(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List getDataBaseInfoByOut(String s, String id, String sourceType) {
        JdbcSupports jt = new JdbcSupports();
        DataSourceProfile dp = dataSourceProfileDao.getByUuid(id);
        String databaseType = dp.getDatabaseType();
        String userName = dp.getUserName();
        String passWord = dp.getPassWord();
        String host = dp.getHost();
        String port = dp.getPort();
        String databaseSid = dp.getDatabaseSid();
        String url = "";
        if (databaseType.equals("1")) {
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
        }
        List dataBaseInfo = jt.getDataByOut(databaseType, userName, passWord, url, sourceType);
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Iterator iterator = dataBaseInfo.iterator(); iterator.hasNext(); ) {
            List children = new ArrayList();
            // Map<String, String> map = (Map<String, String>) iterator.next();
            Map<String, String> map = (Map<String, String>) iterator.next();
            TreeNode treeNode = new TreeNode();
            treeNode.setId(map.get("id"));
            treeNode.setName(map.get("name"));
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getColumnsByOutTable(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List getColumnsByOutTable(String id, String tableName, String sourceType) {
        JdbcSupports jt = new JdbcSupports();
        DataSourceProfile dp = dataSourceProfileDao.getByUuid(id);
        String databaseType = dp.getDatabaseType();
        String owner = dp.getOwner();
        String userName = dp.getUserName();
        String passWord = dp.getPassWord();
        String host = dp.getHost();
        String port = dp.getPort();
        String databaseSid = dp.getDatabaseSid();
        String url = "";
        if (databaseType.equals("1")) {
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
        }
        List columnInfo = jt.getColumnByOut(databaseType, owner, userName, passWord, url, sourceType, tableName);
        return columnInfo;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#query(java.lang.String)
     */
    @Override
    public List<QueryItem> query(String sql, Map<String, Object> selectionArgs, int firstResult, int maxResults) {
        SQLQuery sqlQuery = dynamicDataSourceHibernateDao.getSession().createSQLQuery(sql);
        sqlQuery.setProperties(selectionArgs);
        List<QueryItem> queryItems = sqlQuery.setResultTransformer(QueryItemResultTransformer.INSTANCE)
                .setFirstResult(firstResult).setMaxResults(maxResults).list();
        return queryItems;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryByHql(java.lang.String)
     */
    @Override
    public List<QueryItem> queryByHql(String hql) {
        return dataSourceDefinitionDao.getByHql(hql);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getTotalByOut(java.lang.String, java.lang.String, java.lang.String, java.util.Map, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public Long getTotalByOut(String id, String sourceType, String sqlText, Map<String, Object> queryParams,
                              PagingInfo pagingInfo) {
        Long value = 0L;
        JdbcSupports jt = new JdbcSupports();
        DataSourceProfile dp = dataSourceProfileDao.getByUuid(id);
        String databaseType = dp.getDatabaseType();
        String userName = dp.getUserName();
        String passWord = dp.getPassWord();
        String host = dp.getHost();
        String port = dp.getPort();
        String databaseSid = dp.getDatabaseSid();
        String url = "";
        if (databaseType.equals("1")) {
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
        }

        value = jt.getTotalBySql(databaseType, userName, passWord, url, sourceType, sqlText, queryParams, pagingInfo);
        return value;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<QueryItem> getDataByOut(String id, String sourceType, String sqlText, Map<String, Object> queryParams,
                                        PagingInfo pagingInfo) {
        List<QueryItem> items = new ArrayList<QueryItem>();
        JdbcSupports jt = new JdbcSupports();
        DataSourceProfile dp = dataSourceProfileDao.getByUuid(id);
        String databaseType = dp.getDatabaseType();
        String userName = dp.getUserName();
        String passWord = dp.getPassWord();
        String host = dp.getHost();
        String port = dp.getPort();
        String databaseSid = dp.getDatabaseSid();
        String characterSet = dp.getCharacterSet();
        String url = "";
        if (databaseType.equals("1")) {
            if (StringUtils.isNotBlank(characterSet)) {
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid + "?characterEncoding="
                        + characterSet;
            } else {
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
            }
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            if (StringUtils.isNotBlank(characterSet)) {
                url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid + "?characterEncoding=" + characterSet;
            } else {
                url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
            }
        }

        items = jt.getDataBySql(databaseType, userName, passWord, url, sourceType, sqlText, queryParams, pagingInfo);
        return items;
    }

    /**
     * 获得所有的接口(旧版)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getDataSourceList(java.lang.String, java.lang.String)
     */
    @Override
    public List getDataSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : dataSourceProviderMap.keySet()) {
            DataSourceProvider source = dataSourceProviderMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getModuleName());
            treeNode.setId(key);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getSourceList(java.lang.String, java.lang.String)
     */
    @Override
    public List getSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : dataSourceProviderMap.keySet()) {
            DataSourceProvider source = dataSourceProviderMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getModuleName());
            treeNode.setId(key);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getDataBySource(com.wellsoft.pt.basicdata.datasource.bean.DataSourceDefinitionBean)
     */
    @Override
    public List<QueryItem> getDataBySource(DataSourceDefinitionBean bean, String whereClause,
                                           Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        List<QueryItem> data = new ArrayList<QueryItem>();
        String sourceName = bean.getChooseDataId();
        Set<DataSourceColumn> dataSourceColumns = bean.getDataSourceColumns();
        String defaultSql = bean.getSearchCondition();
        defaultSql = StringUtils.replace(defaultSql, "${currentUserName}", SpringSecurityUtils.getCurrentUserName());
        defaultSql = StringUtils.replace(defaultSql, "${currentLoginName}", SpringSecurityUtils.getCurrentLoginName());
        defaultSql = StringUtils.replace(defaultSql, "${currentUserId}", SpringSecurityUtils.getCurrentUserId());
        defaultSql = StringUtils.replace(defaultSql, "${nowDate}", "date(t.create_time) = :nowDate");
        String whereSql = "";
        if (defaultSql != null) {
            if (StringUtils.isNotBlank(whereClause)) {
                whereSql = defaultSql + " and " + whereClause;
            } else {
                whereSql = defaultSql;
            }

        } else {
            whereSql = whereClause;
        }
        if (StringUtils.isNotBlank(whereSql)) {
            data = dataSourceProviderMap.get(sourceName).query(dataSourceColumns, "(" + whereSql + ")", queryParams,
                    orderBy, pagingInfo);
        } else {
            data = dataSourceProviderMap.get(sourceName).query(dataSourceColumns, "(1=1)", queryParams, orderBy,
                    pagingInfo);
        }
        return data;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#query(java.lang.String, java.lang.String, java.util.Map, java.lang.String, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> query(String dataSourceDefId, String whereClause, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {
        int firstResult = 0;
        int maxResult = 0;
        if (pagingInfo != null) {
            if (pagingInfo.getCurrentPage() <= 0) {
                pagingInfo.setCurrentPage(1);
            }
            firstResult = (pagingInfo.getCurrentPage() - 1) * pagingInfo.getPageSize();
            maxResult = pagingInfo.getPageSize();
        }
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        DataSourceDefinitionBean bean = new DataSourceDefinitionBean();
        bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        // 获得数据源的类型
        String dataSourceTypeId = bean.getDataSourceTypeId();
        String condition = bean.getSearchCondition();
        /*******************************解析默认搜索条件中特定的变量********************************/
        condition = StringUtils.replace(condition, "${currentUserName}", SpringSecurityUtils.getCurrentUserName());
        condition = StringUtils.replace(condition, "${currentLoginName}", SpringSecurityUtils.getCurrentLoginName());
        condition = StringUtils.replace(condition, "${currentUserId}", SpringSecurityUtils.getCurrentUserId());
        condition = StringUtils.replace(condition, "${nowDate}", "date(t.create_time) = :nowDate");
        if (DataSourceConfig.DATA_SOURCE_TYPE_IN.equals(dataSourceTypeId)) {
            // 内部数据源取数据的处理
            // 获取内部数据源的类别
            String inDataScope = bean.getInDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.IN_DATA_SCOPE_TABLE.equals(inDataScope)
                    || DataSourceConfig.IN_DATA_SCOPE_VIEW.equals(inDataScope)) {
                // 数据库表或数据视图
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }
                if (StringUtils.isNotBlank(condition)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where (1=1) and ").append("(" + condition + ")");
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlText.append(" and (" + whereClause + ")");
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlText.append(" order by " + orderBy);
                    }
                } else {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where (1=1)");
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlText.append(" and (" + whereClause + ")");
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlText.append(" order by " + orderBy);
                    }
                }
                queryItems = this.query(sqlText.toString(), queryParams, firstResult, maxResult);
                if (StringUtils.isNotBlank(condition)) {
                    pagingInfo
                            .setTotalCount(this.queryTotal(tableName, condition
                                    + (StringUtils.isNotBlank(whereClause) ? " and " + whereClause : ""), queryParams));
                } else {
                    pagingInfo.setTotalCount(this.queryTotal(tableName, whereClause, queryParams));
                }
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITY.equals(inDataScope)) {

                // 分页请求参数
                StringBuilder selectionHql = new StringBuilder();
                StringBuilder orderBy_ = new StringBuilder();
                Class<IdEntity> Entityname = null;
                // 获取选取的实体类表
                String tableUuid = bean.getChooseDataId();
                List<SystemTableRelationship> relationShip = getViewDataNewServiceImpl
                        .getAttributesByrelationship(tableUuid);
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String moduleId = st.getModuleName();// 模块id
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                // 多表时
                if (relationShip.size() != 0) {
                    for (int index = 0; index < relationShip.size(); index++) {
                        String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                        try {
                            Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                        } catch (ClassNotFoundException e) {
                            logger.error(e.getMessage(), e);
                        }
                        String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                        String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                        String tableRelationShip = relationShip.get(index).getTableRelationship();

                        String[] mainTNs = mainTableName.split("entity.");
                        String[] secondaryTNs = SecondaryTableName.split("entity.");

                        for (DataSourceColumn dataSourceColumn : columns) {
                            String field = dataSourceColumn.getFieldName();
                            String entityName = dataSourceColumn.getEntityName();
                            String columnName = dataSourceColumn.getColumnName();
                            String columnAlias = dataSourceColumn.getColumnAliase();
                            String titleName = dataSourceColumn.getTitleName();
                            String defaultSort = dataSourceColumn.getDefaultSort();
                            String idExport = dataSourceColumn.getIsExport();
                            /**************排序*************/
                            if (defaultSort == null) {
                                defaultSort = "";
                            }

                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1].toUpperCase();
                            if (idExport.equals("true")) {
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append(",o." + columnName + " as " + columnAlias);
                                    // 默认排序
                                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                        orderBy_.append("," + columnAlias + " asc ");
                                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                        orderBy_.append("," + columnAlias + " desc ");
                                    }
                                } else if (secondaryTN.equals(entityN)) {
                                    selectionHql.append(",o." + associateAttribute + "." + columnName + " as "
                                            + columnAlias);
                                    // 默认排序
                                    if (defaultSort != null) {
                                        if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                            orderBy_.append("," + associateAttribute + "." + columnName + " asc ");
                                        } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                            orderBy_.append("," + associateAttribute + "." + columnName + " desc ");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 单表时
                else {
                    String mainTableName = st.getFullEntityName();// 主表名
                    try {
                        Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        logger.error(e.getMessage(), e);
                    }
                    for (DataSourceColumn dataSourceColumn : columns) {
                        String field = dataSourceColumn.getFieldName();
                        String columnName = dataSourceColumn.getColumnName();
                        String columnAlias = dataSourceColumn.getColumnAliase();
                        String defaultSort = dataSourceColumn.getDefaultSort();
                        String titleName = dataSourceColumn.getTitleName();
                        // if (!field.equals("uuid")) {
                        selectionHql.append(",o." + columnName + " as " + columnAlias);

                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                orderBy_.append("," + columnName + " asc ");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                orderBy_.append("," + columnName + " desc ");
                            }
                        }
                    }
                }
                selectionHql.append(" order by " + orderBy_.toString().replaceFirst(",", ""));
                queryItems = this.query(selectionHql.toString(), queryParams, firstResult, maxResult);
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITYBYACL.equals(inDataScope)) {
                String roleType = bean.getRoleType();
                String roleValue = bean.getRoleValue();
                // 实体(带acl权限)
                com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
                aclQueryInfo.addOrderby(orderBy);
                aclQueryInfo.setWhereHql(whereClause);
                // 分页请求参数
                if (pagingInfo != null) {
                    aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
                    aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
                } else {
                    // 设置当前页
                    aclQueryInfo.getPage().setPageNo(1);
                    // 设置每页的页数
                    aclQueryInfo.getPage().setPageSize(10);
                }
                StringBuilder selectionHql = new StringBuilder();
                Class<IdEntity> Entityname = null;
                // 获取选取的实体类表
                String tableUuid = bean.getChooseDataId();
                List<SystemTableRelationship> relationShip = getViewDataNewServiceImpl
                        .getAttributesByrelationship(tableUuid);
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String moduleId = st.getModuleName();// 模块id
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                // 多表时
                if (relationShip.size() != 0) {
                    for (int index = 0; index < relationShip.size(); index++) {
                        String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                        try {
                            Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                        } catch (ClassNotFoundException e) {
                            logger.error(e.getMessage(), e);
                        }
                        String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                        String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                        String tableRelationShip = relationShip.get(index).getTableRelationship();

                        String[] mainTNs = mainTableName.split("entity.");
                        String[] secondaryTNs = SecondaryTableName.split("entity.");

                        for (DataSourceColumn dataSourceColumn : columns) {
                            String field = dataSourceColumn.getFieldName();
                            String entityName = dataSourceColumn.getEntityName();
                            String columnName = dataSourceColumn.getColumnName();
                            String columnAlias = dataSourceColumn.getColumnAliase();
                            String titleName = dataSourceColumn.getTitleName();
                            String defaultSort = dataSourceColumn.getDefaultSort();
                            String idExport = dataSourceColumn.getIsExport();
                            /**************排序*************/
                            if (defaultSort == null) {
                                defaultSort = "";
                            }

                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1].toUpperCase();
                            if (idExport.equals("true")) {
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append(",o." + columnName + " as " + columnAlias);
                                    // 默认排序
                                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                        aclQueryInfo.addOrderby(columnAlias, "asc");
                                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                        aclQueryInfo.addOrderby(columnAlias, "desc");
                                    }
                                } else if (secondaryTN.equals(entityN)) {
                                    selectionHql.append(",o." + associateAttribute + "." + columnName + " as "
                                            + columnAlias);
                                    // 默认排序
                                    if (defaultSort != null) {
                                        if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "asc");
                                        } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "desc");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 单表时
                else {
                    String mainTableName = st.getFullEntityName();// 主表名
                    try {
                        Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        logger.error(e.getMessage(), e);
                    }
                    for (DataSourceColumn dataSourceColumn : columns) {
                        String field = dataSourceColumn.getFieldName();
                        String columnName = dataSourceColumn.getColumnName();
                        String columnAlias = dataSourceColumn.getColumnAliase();
                        String defaultSort = dataSourceColumn.getDefaultSort();
                        String titleName = dataSourceColumn.getTitleName();
                        // if (!field.equals("uuid")) {
                        selectionHql.append(",o." + columnName + " as " + columnAlias);

                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                aclQueryInfo.addOrderby(columnName, "asc");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                aclQueryInfo.addOrderby(columnName, "desc");
                            }
                        }
                    }
                }
                aclQueryInfo.setSelectionHql(selectionHql.toString().replaceFirst(",", ""));
                // 处理权限角色值
                List<Permission> roTypeList = new ArrayList<Permission>();
                if (StringUtils.isNotBlank(roleType)) {
                    String[] roTypeArray = roleType.split(";");
                    for (int i = 0; i < roTypeArray.length; i++) {
                        roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
                    }
                }
                List<String> sids = new ArrayList<String>();
                sids.add(SpringSecurityUtils.getCurrentUserId());
                // 个人权限
                if (roleValue.equals("person")) {
                    queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
                    pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
                }
                // 群组权限
                else if (roleValue.equals("group")) {
                    queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                            SpringSecurityUtils.getCurrentUserId(), moduleId);
                    pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
                } else if (roleValue.equals("all")) {
                    queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                            SpringSecurityUtils.getCurrentUserId(), moduleId);
                    pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
                }

            } else if (DataSourceConfig.IN_DATA_SCOPE_SQL.equals(inDataScope)) {
                StringBuilder sqlNew = new StringBuilder();
                // sql
                if (StringUtils.isNotBlank(condition)) {

                    sqlNew.append(" where (1=1) and ").append("(" + condition + ")");
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlNew.append(" and (" + whereClause + ")");
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlNew.append(" order by " + orderBy);
                    }
                } else {
                    sqlNew.append(" where (1=1)");
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlNew.append(" and (" + whereClause + ")");
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlNew.append(" order by " + orderBy);
                    }
                }
                queryItems = this
                        .query(bean.getSqlOrHqlText() + sqlNew.toString(), queryParams, firstResult, maxResult);
                StringBuilder queryByView = new StringBuilder();
                if (StringUtils.isNotBlank(condition)) {
                    queryByView.append("(1=1) and (" + condition + ")");
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" and (" + whereClause + ")");
                    }
                } else {
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" (" + whereClause + ")");
                    }
                }
                pagingInfo.setTotalCount(this.queryTotalByView(dataSourceDefId, queryByView.toString(), queryParams));
            } else if (DataSourceConfig.IN_DATA_SCOPE_HQL.equals(inDataScope)) {
                // hql
            } else if (DataSourceConfig.IN_DATA_SCOPE_ACLHQL.equals(inDataScope)) {
                // aclHql
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_OUT.equals(dataSourceTypeId)) {
            // 外部数据源取数据的处理
            // 获取数据源的类别
            String outDataScope = bean.getOutDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.OUT_DATA_SCOPE_TABLE.equals(outDataScope)
                    || DataSourceConfig.OUT_DATA_SCOPE_VIEW.equals(outDataScope)) {
                // 数据库表
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }
                if (StringUtils.isNotBlank(condition)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(condition);
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlText.append(" and " + whereClause);
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlText.append(" order by " + orderBy);
                    }
                } else {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where 1=1");
                    if (StringUtils.isNotBlank(whereClause)) {
                        sqlText.append(" and " + whereClause);
                    }
                    if (StringUtils.isNotBlank(orderBy)) {
                        sqlText.append(" order by " + orderBy);
                    }
                }
                String outDataSourceId = bean.getOutDataSourceId();
                queryItems = this.getDataByOut(outDataSourceId, outDataScope, sqlText.toString(), queryParams,
                        pagingInfo);
                StringBuilder queryByView = new StringBuilder();
                if (StringUtils.isNotBlank(condition)) {
                    queryByView.append("(1=1) and (" + condition + ")");
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" and (" + whereClause + ")");
                    }
                } else {
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" (" + whereClause + ")");
                    }
                }
                pagingInfo.setTotalCount(this.queryTotalByView(dataSourceDefId, queryByView.toString(), queryParams));
            } else if (DataSourceConfig.OUT_DATA_SCOPE_SQL.equals(outDataScope)) {
                // sql
                String outDataSourceId = bean.getOutDataSourceId();
                String defaultSql = bean.getSearchCondition();
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                StringBuilder querySql = new StringBuilder();
                if (StringUtils.isNotBlank(defaultSql)) {
                    querySql.append(bean.getSqlOrHqlText()).append(" where " + defaultSql);
                    if (StringUtils.isNotBlank(whereClause)) {
                        querySql.append(" and " + whereClause);
                    }
                } else if (StringUtils.isNotBlank(whereClause)) {
                    querySql.append(bean.getSqlOrHqlText()).append(" where " + whereClause);
                } else {
                    querySql.append(bean.getSqlOrHqlText());
                }
                if (StringUtils.isNotBlank(orderBy)) {
                    querySql.append(" order by " + orderBy);
                }
                queryItems = this.getDataByOut(outDataSourceId, outDataScope, querySql.toString(), queryParams,
                        pagingInfo);
                StringBuilder queryByView = new StringBuilder();
                if (StringUtils.isNotBlank(condition)) {
                    queryByView.append("(1=1) and (" + condition + ")");
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" and (" + whereClause + ")");
                    }
                } else {
                    if (StringUtils.isNotBlank(whereClause)) {
                        queryByView.append(" (" + whereClause + ")");
                    }
                }
                pagingInfo.setTotalCount(this.queryTotalByView(dataSourceDefId, queryByView.toString(), queryParams));
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_PROVIDER.equals(dataSourceTypeId)) {
            // 数据接口
            // 暂时设置成当前页 每页20条
            // pagingInfo.setCurrentPage(1);
            // pagingInfo.setPageSize(20);
            queryItems = this.getDataBySource(bean, whereClause, queryParams, orderBy, pagingInfo);
        }
        return queryItems;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#dataSourceInterpreter(java.lang.String)
     */
    @Override
    public List<QueryItem> dataSourceInterpreter(String dataSourceDefId) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        /*****************获取数据源的定义********************/
        DataSourceDefinitionBean bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        // 获得数据源的类型
        String dataSourceTypeId = bean.getDataSourceTypeId();
        String condition = bean.getSearchCondition();
        if (condition == null) {
            condition = "";
        }
        /*******************************解析默认搜索条件中特定的变量********************************/
        condition = StringUtils.replace(condition, "${currentUserName}", SpringSecurityUtils.getCurrentUserName());
        condition = StringUtils.replace(condition, "${currentLoginName}", SpringSecurityUtils.getCurrentLoginName());
        condition = StringUtils.replace(condition, "${currentUserId}", SpringSecurityUtils.getCurrentUserId());
        condition = StringUtils.replace(condition, "${currentUserDepartmentId}",
                SpringSecurityUtils.getCurrentUserDepartmentId());
        condition = StringUtils.replace(condition, "${currentUserDepartmentName}",
                SpringSecurityUtils.getCurrentUserDepartmentName());
        condition = StringUtils.replace(condition, "${nowDate}", "date(t.create_time) = :nowDate");
        if (DataSourceConfig.DATA_SOURCE_TYPE_IN.equals(dataSourceTypeId)) {
            // 内部数据源取数据的处理
            // 获取内部数据源的类别
            String inDataScope = bean.getInDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.IN_DATA_SCOPE_TABLE.equals(inDataScope)) {
                // 数据库表
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }
                if (StringUtils.isNotBlank(condition)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(condition);
                } else {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where 1=1");
                }
                queryItems = this.query(sqlText.toString(), null, 0, 0);
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITY.equals(inDataScope)) {
                // 分页请求参数
                StringBuilder selectionHql = new StringBuilder();
                StringBuilder selectionHql2 = new StringBuilder();
                selectionHql.append("select ");
                selectionHql2.append(" from ");
                StringBuilder orderBy_ = new StringBuilder();
                Class<IdEntity> Entityname = null;
                // 获取选取的实体类表
                String tableUuid = bean.getChooseDataId();
                List<SystemTableRelationship> relationShip = getViewDataNewServiceImpl
                        .getAttributesByrelationship(tableUuid);
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String moduleId = st.getModuleName();// 模块id
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                // 多表时
                if (relationShip.size() != 0) {
                    for (int index = 0; index < relationShip.size(); index++) {
                        String mainTableName = relationShip.get(index).getMainTableName();// 主表名

                        try {
                            Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                        } catch (ClassNotFoundException e) {
                            logger.error(e.getMessage(), e);
                        }
                        String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                        String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                        String tableRelationShip = relationShip.get(index).getTableRelationship();
                        selectionHql2.append(mainTableName + " o inner join o." + associateAttribute);
                        String[] mainTNs = mainTableName.split("entity.");
                        String[] secondaryTNs = SecondaryTableName.split("entity.");

                        for (DataSourceColumn dataSourceColumn : columns) {
                            String field = dataSourceColumn.getFieldName();
                            String entityName = dataSourceColumn.getEntityName();
                            String columnName = dataSourceColumn.getColumnName();
                            String columnAlias = dataSourceColumn.getColumnAliase();
                            String titleName = dataSourceColumn.getTitleName();
                            String defaultSort = dataSourceColumn.getDefaultSort();
                            String idExport = dataSourceColumn.getIsExport();
                            /**************排序*************/
                            if (defaultSort == null) {
                                defaultSort = "";
                            }

                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1].toUpperCase();
                            if (idExport.equals("true")) {
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append(",o." + columnName + " as " + columnAlias);
                                    // 默认排序
                                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                        orderBy_.append("," + columnAlias + " asc ");
                                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                        orderBy_.append("," + columnAlias + " desc ");
                                    }
                                } else if (secondaryTN.equals(entityN)) {
                                    selectionHql.append(",o." + associateAttribute + "." + columnName + " as "
                                            + columnAlias);
                                    // 默认排序
                                    if (defaultSort != null) {
                                        if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                            orderBy_.append("," + associateAttribute + "." + columnName + " asc ");
                                        } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                            orderBy_.append("," + associateAttribute + "." + columnName + " desc ");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 单表时
                else {
                    String mainTableName = st.getFullEntityName();// 主表名
                    selectionHql2.append(mainTableName + " o ");
                    try {
                        Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        logger.error(e.getMessage(), e);
                    }
                    for (DataSourceColumn dataSourceColumn : columns) {
                        String field = dataSourceColumn.getFieldName();
                        String columnName = dataSourceColumn.getColumnName();
                        String columnAlias = dataSourceColumn.getColumnAliase();
                        String defaultSort = dataSourceColumn.getDefaultSort();
                        String titleName = dataSourceColumn.getTitleName();
                        // if (!field.equals("uuid")) {
                        selectionHql.append(",o." + columnName + " as " + columnAlias);

                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                orderBy_.append("," + columnName + " asc ");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                orderBy_.append("," + columnName + " desc ");
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(orderBy_.toString().replaceFirst(",", ""))) {
                    selectionHql.append(" order by " + orderBy_.toString().replaceFirst(",", ""));
                }
                selectionHql.append(selectionHql2);
                queryItems = this.query(selectionHql.toString().replaceFirst(",", ""), null, 0, 0);
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITYBYACL.equals(inDataScope)) {
                String roleType = bean.getRoleType();
                String roleValue = bean.getRoleValue();
                // 实体(带acl权限)
                com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
                StringBuilder selectionHql = new StringBuilder();
                Class<IdEntity> Entityname = null;
                // 获取选取的实体类表
                String tableUuid = bean.getChooseDataId();
                List<SystemTableRelationship> relationShip = getViewDataNewServiceImpl
                        .getAttributesByrelationship(tableUuid);
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String moduleId = st.getModuleName();// 模块id
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                // 多表时
                if (relationShip.size() != 0) {
                    for (int index = 0; index < relationShip.size(); index++) {
                        String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                        try {
                            Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                        } catch (ClassNotFoundException e) {
                            logger.error(e.getMessage(), e);
                        }
                        String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                        String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                        String tableRelationShip = relationShip.get(index).getTableRelationship();

                        String[] mainTNs = mainTableName.split("entity.");
                        String[] secondaryTNs = SecondaryTableName.split("entity.");

                        for (DataSourceColumn dataSourceColumn : columns) {
                            String field = dataSourceColumn.getFieldName();
                            String entityName = dataSourceColumn.getEntityName();
                            String columnName = dataSourceColumn.getColumnName();
                            String columnAlias = dataSourceColumn.getColumnAliase();
                            String titleName = dataSourceColumn.getTitleName();
                            String defaultSort = dataSourceColumn.getDefaultSort();
                            String idExport = dataSourceColumn.getIsExport();
                            /**************排序*************/
                            if (defaultSort == null) {
                                defaultSort = "";
                            }

                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1].toUpperCase();
                            if (idExport.equals("true")) {
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append(",o." + columnName + " as " + columnAlias);
                                    // 默认排序
                                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                        aclQueryInfo.addOrderby(columnAlias, "asc");
                                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                        aclQueryInfo.addOrderby(columnAlias, "desc");
                                    }
                                } else if (secondaryTN.equals(entityN)) {
                                    selectionHql.append(",o." + associateAttribute + "." + columnName + " as "
                                            + columnAlias);
                                    // 默认排序
                                    if (defaultSort != null) {
                                        if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "asc");
                                        } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "desc");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 单表时
                else {
                    String mainTableName = st.getFullEntityName();// 主表名
                    try {
                        Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        logger.error(e.getMessage(), e);
                    }
                    for (DataSourceColumn dataSourceColumn : columns) {
                        String field = dataSourceColumn.getFieldName();
                        String columnName = dataSourceColumn.getColumnName();
                        String columnAlias = dataSourceColumn.getColumnAliase();
                        String defaultSort = dataSourceColumn.getDefaultSort();
                        String titleName = dataSourceColumn.getTitleName();
                        String idExport = dataSourceColumn.getIsExport();
                        // if (!field.equals("uuid")) {
                        if (idExport.equals("true")) {
                            selectionHql.append(",o." + columnName + " as " + columnAlias);
                        }
                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                aclQueryInfo.addOrderby(columnName, "asc");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                aclQueryInfo.addOrderby(columnName, "desc");
                            }
                        }
                    }
                }
                aclQueryInfo.setSelectionHql(selectionHql.toString().replaceFirst(",", ""));
                // 处理权限角色值
                List<Permission> roTypeList = new ArrayList<Permission>();
                if (StringUtils.isNotBlank(roleType)) {
                    String[] roTypeArray = roleType.split(";");
                    for (int i = 0; i < roTypeArray.length; i++) {
                        roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
                    }
                }
                List<String> sids = new ArrayList<String>();
                sids.add(SpringSecurityUtils.getCurrentUserId());
                // 个人权限
                if (roleValue.equals("person")) {
                    queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
                }
                // 群组权限
                else if (roleValue.equals("group")) {
                    queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                            SpringSecurityUtils.getCurrentUserId(), moduleId);
                } else if (roleValue.equals("all")) {
                    queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                            SpringSecurityUtils.getCurrentUserId(), moduleId);
                }

            } else if (DataSourceConfig.IN_DATA_SCOPE_VIEW.equals(inDataScope)) {
                // 数据视图
            } else if (DataSourceConfig.IN_DATA_SCOPE_SQL.equals(inDataScope)) {
                // sql
                String doSql = bean.getSqlOrHqlText();
                if (StringUtils.isNotBlank(condition)) {
                    doSql = bean.getSqlOrHqlText() + " where " + condition;
                }
                queryItems = this.query(doSql, null, 0, 0);
            } else if (DataSourceConfig.IN_DATA_SCOPE_HQL.equals(inDataScope)) {
                // hql
            } else if (DataSourceConfig.IN_DATA_SCOPE_ACLHQL.equals(inDataScope)) {
                // aclHql
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_OUT.equals(dataSourceTypeId)) {
            PagingInfo pagingInfo = new PagingInfo();
            // 外部数据源取数据的处理
            // 获取内部数据源的类别
            String outDataScope = bean.getOutDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.OUT_DATA_SCOPE_TABLE.equals(outDataScope)
                    || DataSourceConfig.OUT_DATA_SCOPE_VIEW.equals(outDataScope)) {
                // 数据库表
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }

                if (StringUtils.isNotBlank(condition)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(condition);
                } else {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where 1=1");
                }
                String outDataSourceId = bean.getOutDataSourceId();
                queryItems = this.getDataByOut(outDataSourceId, outDataScope, sqlText.toString(), null, pagingInfo);
            } else if (DataSourceConfig.OUT_DATA_SCOPE_SQL.equals(outDataScope)) {
                // sql
                String outDataSourceId = bean.getOutDataSourceId();
                String searchWhereSql = bean.getSearchCondition();
                String sql = "";
                if (StringUtils.isNotBlank(searchWhereSql)) {
                    sql = bean.getSqlOrHqlText() + " where " + searchWhereSql;
                } else {
                    sql = bean.getSqlOrHqlText();
                }

                queryItems = this.getDataByOut(outDataSourceId, outDataScope, sql, null, pagingInfo);
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_PROVIDER.equals(dataSourceTypeId)) {
            // 数据接口取数据的处理
            PagingInfo pagingInfo = new PagingInfo();
            Map<String, Object> map = new HashMap<String, Object>();
            queryItems = this.getDataBySource(bean, "", map, "", pagingInfo);
        }
        return queryItems;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryByTotal(java.lang.String)
     */
    @Override
    public Long queryByTotal(String dataSourceDefId) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        Long total = 0L;
        /*****************获取数据源的定义********************/
        DataSourceDefinitionBean bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        // 获得数据源的类型
        String dataSourceTypeId = bean.getDataSourceTypeId();
        if (DataSourceConfig.DATA_SOURCE_TYPE_IN.equals(dataSourceTypeId)) {
            // 内部数据源取数据的处理
            // 获取内部数据源的类别
            String inDataScope = bean.getInDataScope();
            if (DataSourceConfig.IN_DATA_SCOPE_TABLE.equals(inDataScope)) {
                // 数据库表名
                String tableName = bean.getChooseDataText();
                total = this.queryTotal(tableName, "", null);
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITY.equals(inDataScope)) {
                // 实体(不考虑acl权限)
                String tableUuid = bean.getChooseDataId();
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String tableName = st.getTableName();
                total = this.queryTotal(tableName, "", null);
            } else if (DataSourceConfig.IN_DATA_SCOPE_VIEW.equals(inDataScope)) {
                // 数据视图
            } else if (DataSourceConfig.IN_DATA_SCOPE_SQL.equals(inDataScope)) {
                // sql
                queryItems = this.query(bean.getSqlOrHqlText(), null, 0, 0);
                total = Long.valueOf(queryItems.size());
            } else if (DataSourceConfig.IN_DATA_SCOPE_HQL.equals(inDataScope)) {
                // hql
            } else if (DataSourceConfig.IN_DATA_SCOPE_ACLHQL.equals(inDataScope)) {
                // aclHql
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_OUT.equals(dataSourceTypeId)) {
            // 外部数据源取数据的处理
            // 获取内部数据源的类别
            String outDataScope = bean.getOutDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.OUT_DATA_SCOPE_TABLE.equals(outDataScope)
                    || DataSourceConfig.OUT_DATA_SCOPE_VIEW.equals(outDataScope)) {
                PagingInfo pagingInfo = new PagingInfo();
                // 数据库表
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }
                String defaultSort = bean.getSearchCondition();
                if (StringUtils.isNotBlank(defaultSort)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(defaultSort);
                } else {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where 1=1");
                }
                String outDataSourceId = bean.getOutDataSourceId();
                queryItems = this.getDataByOut(outDataSourceId, outDataScope, sqlText.toString(), null, pagingInfo);
                total = Long.valueOf(queryItems.size());
            } else if (DataSourceConfig.OUT_DATA_SCOPE_SQL.equals(outDataScope)) {
                // sql
                PagingInfo pagingInfo = new PagingInfo();
                String outDataSourceId = bean.getOutDataSourceId();
                total = this.getTotalByOut(outDataSourceId, outDataScope, bean.getSqlOrHqlText(), null, pagingInfo);
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_PROVIDER.equals(dataSourceTypeId)) {
        }
        return total;

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryTotalBySelect(java.lang.String, java.lang.String, com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew)
     */
    @Override
    public Long queryTotalBySelect(String dataSourceDefId, String whereSql, DyViewQueryInfoNew dyViewQueryInfoNew,
                                   Set<ColumnDefinitionNew> columnDefinitionNews) {
        Map paraMap = new HashMap();
        StringBuilder selection = new StringBuilder();
        selection.append(" (1=1)");
        StringBuilder selectionTemp = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();
        // 时间请求sql
        String timeSql = "";
        // 前端所有的请求参数类
        List<CondSelectAskInfoNew> askInfo = dyViewQueryInfoNew.getCondSelectList();
        StringBuilder selectionFields = new StringBuilder();
        if (askInfo != null) {
            for (int i = 0; i < askInfo.size(); i++) {
                StringBuilder selectionField = new StringBuilder();
                String optionTitle = "";
                String optionValue = "";
                String appointColumn = "";
                String beginTime = "";
                String endTime = "";
                String searchField = "";
                String searchFieldTypeId = "";
                Boolean isArea = false;
                Boolean isExact = false;
                Boolean isLike = false;
                String exactValue = "";
                String searchValue = "";
                String orderbyArr = "";
                String orderTitle = "";
                List<Map<String, String>> keyWords = new ArrayList<Map<String, String>>();
                if (askInfo != null) {
                    // 备选项的标题
                    if (StringUtils.isNotEmpty(askInfo.get(i).getOptionTitle())) {
                        optionTitle = askInfo.get(i).getOptionTitle();
                    }
                    // 备选项的真实值
                    if (StringUtils.isNotEmpty(askInfo.get(i).getOptionValue())) {
                        optionValue = askInfo.get(i).getOptionValue();
                    }
                    // 备选项对应的列名称
                    if (StringUtils.isNotEmpty(askInfo.get(i).getAppointColumn())) {
                        appointColumn = askInfo.get(i).getAppointColumn();
                    }
                    // 日期查询开始时间
                    if (StringUtils.isNotEmpty(askInfo.get(i).getBeginTime())) {
                        beginTime = askInfo.get(i).getBeginTime();
                    }
                    // 日期查询结束时间
                    if (StringUtils.isNotEmpty(askInfo.get(i).getEndTime())) {
                        endTime = askInfo.get(i).getEndTime();
                    }
                    // 查询的字段
                    if (StringUtils.isNotEmpty(askInfo.get(i).getSearchField())) {
                        searchField = askInfo.get(i).getSearchField();
                    }
                    // 查询的字段的类型Id
                    if (StringUtils.isNotEmpty(askInfo.get(i).getSearchFieldTypeId())) {
                        searchFieldTypeId = askInfo.get(i).getSearchFieldTypeId();
                    }
                    if (askInfo.get(i).getIsArea() != null) {
                        isArea = askInfo.get(i).getIsArea();
                    }
                    if (askInfo.get(i).getIsExact() != null) {
                        isExact = askInfo.get(i).getIsExact();
                    }
                    if (askInfo.get(i).getIsLike() != null) {
                        isLike = askInfo.get(i).getIsLike();
                    }

                    if (StringUtils.isNotEmpty(askInfo.get(i).getExactValue())) {
                        exactValue = askInfo.get(i).getExactValue();

                    }

                    if (StringUtils.isNotEmpty(askInfo.get(i).getSearchValue())) {
                        searchValue = askInfo.get(i).getSearchValue();
                    }
                    // 关键字
                    if (askInfo.get(i).getKeyWords() != null && askInfo.get(i).getKeyWords().size() != 0) {
                        keyWords = askInfo.get(i).getKeyWords();
                    }
                    // 点击排序
                    if (StringUtils.isNotEmpty(askInfo.get(i).getOrderTitle())) {
                        orderTitle = askInfo.get(i).getOrderTitle();
                    }
                    if (StringUtils.isNotEmpty(askInfo.get(i).getOrderbyArr())) {
                        orderbyArr = askInfo.get(i).getOrderbyArr();
                    }
                }
                if (StringUtils.isNotBlank(searchField)) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    if (searchFieldTypeId.equals("SELECT") || searchFieldTypeId.equals("RADIO")
                            || searchFieldTypeId.equals("CHECKBOX")) {
                        if (StringUtils.isNotBlank(searchValue)) {
                            selectionField.append(searchField + " = '" + searchValue + "'");
                        }
                    } else if (searchFieldTypeId.equals("ORG")) {
                        if (StringUtils.isNotBlank(searchValue)) {
                            String[] searchValues = searchValue.split(";");
                            for (int k = 0; k < searchValues.length; k++) {
                                if (k == 0) {
                                    selectionField.append(searchField + " = '" + searchValues[k] + "'");
                                } else {
                                    selectionField.append(" or " + searchField + " = '" + searchValues[k] + "'");
                                }
                            }
                        }
                    } else if (searchFieldTypeId.equals("DATE")) {
                        if (StringUtils.isNotBlank(beginTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                timeSql += searchField + ">:startDate";
                                paraMap.put("startDate", startDate);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                logger.error(e.getMessage(), e);
                            }
                        }
                        if (StringUtils.isNotBlank(endTime)) {
                            try {
                                Date endDate = format.parse(endTime);
                                if (!StringUtils.isBlank(timeSql)) {
                                    timeSql += " and " + searchField + "<:endDate";
                                } else {
                                    timeSql += searchField + "< :endDate";
                                }
                                paraMap.put("endDate", endDate);
                            } catch (ParseException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        if (!StringUtils.isBlank(timeSql)) {
                            selectionField.append(timeSql);
                        }
                    } else if (searchFieldTypeId.equals("TEXT")) {
                        if (isArea == true) {

                        }
                        if (isExact == true) {
                            if (StringUtils.isNotBlank(searchValue)) {
                                if (exactValue == "1") {
                                    selectionField.append(searchField + " = " + searchValue);
                                } else if (exactValue == "2") {
                                    selectionField.append(searchField + " in( '" + searchValue + "')");
                                } else if (exactValue == "3") {
                                    selectionField.append(searchField + " != " + searchValue);
                                } else if (exactValue == "4") {
                                    selectionField.append(searchField + " not in( '" + searchValue + "')");
                                }
                            }
                        }
                        if (isLike == true) {
                            if (StringUtils.isNotBlank(searchValue)) {
                                selectionField.append(searchField + " like '%" + searchValue + "%'");
                            }
                        }
                    }

                    if (!StringUtils.isBlank(selectionField.toString())) {
                        selectionFields.append(" and " + selectionField.toString());
                    }
                }

                // 计数器
                // int i = 0;
                for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                    String field = columnDefinitionNew.getFieldName();
                    String titleName = columnDefinitionNew.getTitleName();
                    /**************排序*************/
                    String defaultSort = columnDefinitionNew.getDefaultSort();
                    // 点击排序
                    if (orderTitle != null && !orderTitle.equals("")) {
                        if (orderTitle.equals(titleName)) {
                            orderBy.append("," + field).append(" " + orderbyArr);
                        }
                    }
                    // 默认排序
                    if (defaultSort != null) {
                        if (defaultSort.equals(DyviewConfigNew.DYVIEW_SORT_ASC)) {
                            orderBy.append("," + field).append(" asc");
                        } else if (defaultSort.equals(DyviewConfigNew.DYVIEW_SORT_DESC)) {
                            orderBy.append("," + field).append(" desc");
                        }
                    }
                    /*********************搜索条件*************************/
                    String keyWordType = "all";
                    String keyWordValue = "";
                    for (int j = 0; j < keyWords.size(); j++) {
                        Map<String, String> keyWord = keyWords.get(j);
                        for (String key : keyWord.keySet()) {
                            if (key.equals("all")) {
                                keyWordType = "all";
                                keyWordValue = keyWord.get("all").toString();
                                break;
                            }
                        }

                    }
                    String columnDataType = columnDefinitionNew.getColumnDataType();
                    if (keyWords.size() != 0) {
                        if (field != null && !"".equals(field) && columnDataType != "2") {
                            // if (i < 1) {
                            if (keyWordType.equals("all") && !field.equals("uuid")) {// 全部列模糊查询
                                String[] keyValues = keyWordValue.split(",");
                                for (int k = 0; k < keyValues.length; k++) {
                                    if ("20".equals(columnDataType)) {
                                        List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                                        if (!users.isEmpty()) {
                                            StringBuilder sb = new StringBuilder();
                                            for (int m = 0; m < users.size(); m++) {
                                                sb.append(",'" + users.get(m) + "'");
                                            }
                                            selectionTemp.append(" or " + field + " in ("
                                                    + sb.toString().replaceFirst(",", "") + ")");
                                        }
                                    } else {
                                        selectionTemp.append(" or " + field + " like " + "'%" + keyValues[k] + "%'");
                                    }
                                }
                            }
                        }
                    }

                    if (appointColumn != null && !appointColumn.equals("")) {
                        if (titleName.equals(appointColumn)) {
                            if (field != null && !"".equals(field)) {
                                selectionTemp.append(field + " like " + "'%" + optionValue + "%'");
                            }
                        }
                    }

                    if (columnDefinitionNew.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinitionNew.getFieldName());
                    }
                    if (columnDefinitionNew.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinitionNew.getFieldName());
                    }

                }
            }
        }
        if (StringUtils.isNotEmpty(whereSql)) {
            selection.append(" and (" + whereSql + ")");
        }
        if (StringUtils.isNotEmpty(selectionTemp.toString())) {
            selection.append(" and (").append(selectionTemp.toString().replaceFirst(" or", "") + ")");
        }
        if (StringUtils.isNotEmpty(selectionFields.toString())) {
            DataSourceDefinitionBean dataSourceBean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
            if (dataSourceBean.getDataSourceTypeId().equals("3")) {
                String selectWhereSql = dataSourceProviderMap.get(dataSourceBean.getChooseDataId()).getWhereSql(
                        dyViewQueryInfoNew);
                if (StringUtils.isNotBlank(selectWhereSql)) {
                    selection.append(" and (").append(selectWhereSql + ")");
                } else {
                    selection.append(" and (").append(selectionFields.toString().replaceFirst("and", "") + ")");
                }
            } else {
                selection.append(" and (").append(selectionFields.toString().replaceFirst("and", "") + ")");
            }

        }
        List<QueryItem> queryItem = dataSourceApiFacade.query(dataSourceDefId, selection.toString(), paraMap,
                orderBy.toString(), null);
        return (long) queryItem.size();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryTotalByView(java.lang.String, java.lang.String)
     */
    @Override
    public Long queryTotalByView(String dataSourceDefId, String whereSql, Map<String, Object> queryParams) {

        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        Long total = 0L;
        /*****************获取数据源的定义********************/
        DataSourceDefinitionBean bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        // 获得数据源的类型
        String dataSourceTypeId = bean.getDataSourceTypeId();
        if (DataSourceConfig.DATA_SOURCE_TYPE_IN.equals(dataSourceTypeId)) {
            // 内部数据源取数据的处理
            // 获取内部数据源的类别
            String inDataScope = bean.getInDataScope();
            if (DataSourceConfig.IN_DATA_SCOPE_TABLE.equals(inDataScope)) {
                // 数据库表名
                String tableName = bean.getChooseDataText();
                total = this.queryTotal(tableName, whereSql, null);
            } else if (DataSourceConfig.IN_DATA_SCOPE_ENTITY.equals(inDataScope)) {
                // 实体(不考虑acl权限)
                String tableUuid = bean.getChooseDataId();
                SystemTable st = basicDataApiFacade.getTable(tableUuid);
                String tableName = st.getTableName();
                total = this.queryTotal(tableName, whereSql, null);
            } else if (DataSourceConfig.IN_DATA_SCOPE_VIEW.equals(inDataScope)) {
                // 数据视图
            } else if (DataSourceConfig.IN_DATA_SCOPE_SQL.equals(inDataScope)) {
                // sql
                if (StringUtils.isNotBlank(whereSql)) {
                    queryItems = this.query(bean.getSqlOrHqlText() + " where " + whereSql, queryParams, 0, 0);
                    total = Long.valueOf(queryItems.size());
                } else {
                    queryItems = this.query(bean.getSqlOrHqlText(), null, 0, 0);
                    total = Long.valueOf(queryItems.size());
                }
            } else if (DataSourceConfig.IN_DATA_SCOPE_HQL.equals(inDataScope)) {
                // hql
            } else if (DataSourceConfig.IN_DATA_SCOPE_ACLHQL.equals(inDataScope)) {
                // aclHql
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_OUT.equals(dataSourceTypeId)) {
            // 外部数据源取数据的处理
            // 获取内部数据源的类别
            String outDataScope = bean.getOutDataScope();
            StringBuilder sqlText = new StringBuilder();
            if (DataSourceConfig.OUT_DATA_SCOPE_TABLE.equals(outDataScope)
                    || DataSourceConfig.OUT_DATA_SCOPE_VIEW.equals(outDataScope)) {
                PagingInfo pagingInfo = new PagingInfo();
                // 数据库表
                sqlText.append("select ");
                StringBuilder sqlColumns = new StringBuilder();
                // 数据库表名
                String tableName = bean.getChooseDataText();
                // 数据库表的定义列
                Set<DataSourceColumn> columns = bean.getDataSourceColumns();
                for (DataSourceColumn dcb : columns) {
                    String columnName = dcb.getColumnName();
                    String isExport = dcb.getIsExport();
                    if (DataSourceConfig.DATA_SOURCE_COLUMN_ISEXPORT.equals(isExport)) {
                        sqlColumns.append("," + "t." + columnName);
                    }
                }
                String defaultSort = bean.getSearchCondition();

                if (StringUtils.isNotBlank(whereSql)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(whereSql);
                    if (StringUtils.isNotBlank(defaultSort)) {
                        sqlText.append(" and " + defaultSort);
                    }
                }

                if (StringUtils.isNotBlank(defaultSort)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where ").append(defaultSort);
                }

                if (!StringUtils.isNotBlank(defaultSort) && !StringUtils.isNotBlank(whereSql)) {
                    sqlText.append(sqlColumns.toString().replaceFirst(",", "")).append(" from ")
                            .append(tableName + " t ").append(" where 1=1");
                }

                String outDataSourceId = bean.getOutDataSourceId();
                queryItems = this.getDataByOut(outDataSourceId, outDataScope, sqlText.toString(), null, pagingInfo);
                total = Long.valueOf(queryItems.size());
            } else if (DataSourceConfig.OUT_DATA_SCOPE_SQL.equals(outDataScope)) {
                // sql
                PagingInfo pagingInfo = new PagingInfo();
                String outDataSourceId = bean.getOutDataSourceId();
                if (StringUtils.isEmpty(whereSql) || "".equals(whereSql.trim())) {
                    queryItems = this.getDataByOut(outDataSourceId, outDataScope, bean.getSqlOrHqlText()
                            + " where 1=1 ", null, pagingInfo);
                } else {
                    queryItems = this.getDataByOut(outDataSourceId, outDataScope, bean.getSqlOrHqlText()
                            + " where 1=1 and " + whereSql, null, pagingInfo);
                }
                total = Long.valueOf(queryItems.size());
            }
        } else if (DataSourceConfig.DATA_SOURCE_TYPE_PROVIDER.equals(dataSourceTypeId)) {
            // PagingInfo pagingInfo = new PagingInfo();
            // Map<String, Object> map = new HashMap<String, Object>();
            // //数据接口取数据的处理
            // queryItems = this.getDataBySource(bean, "", map, "", pagingInfo);
            // total = Long.valueOf(queryItems.size());
        }
        return total;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @param queryParams
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryTotal(java.lang.String)
     */
    @Override
    public Long queryTotal(String tableName, String whereSql, Map<String, Object> queryParams) {
        String countString = "";
        if (StringUtils.isNotBlank(whereSql)) {
            countString = "select count(*) from " + tableName + " where " + whereSql;
        } else {
            countString = "select count(*) from " + tableName;
        }
        SQLQuery countQuery = dynamicDataSourceHibernateDao.getSession().createSQLQuery(countString);
        if (countQuery == null) {
            queryParams = new HashMap<String, Object>();
        }
        countQuery.setProperties(queryParams);
        Object count = countQuery.uniqueResult();
        return Long.valueOf(count.toString());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#getSourceColumns(java.lang.String)
     */
    @Override
    public List getSourceColumns(String id) {
        DataSourceProvider dp = dataSourceProviderMap.get(id);
        Collection<DataSourceColumn> dataSourceColumns = dp.getAllDataSourceColumns();
        List<DataSourceColumn> lists = new ArrayList<DataSourceColumn>();
        for (DataSourceColumn dataSourceColumn : dataSourceColumns) {
            lists.add(dataSourceColumn);
        }
        return lists;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#custom(java.lang.String, java.lang.Object[])
     */
    @Override
    public Object[] custom(String dataSourceDefId, Object[] obj) {

        DataSourceDefinitionBean bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        // 获得数据源的类型
        String dataSourceTypeId = bean.getDataSourceTypeId();
        if (DataSourceConfig.DATA_SOURCE_TYPE_PROVIDER.equals(dataSourceTypeId)) {
            String dataInterfaceId = bean.getChooseDataId();
            return dataSourceProviderMap.get(dataInterfaceId).custom(obj);
        } else {
            return null;
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryByValidate(java.lang.String, java.util.Map)
     */
    @Override
    public List queryByValidate(String dataComeType, String dataComeId, String sql, Map<String, Object> selectionArgs) {
        List list = new ArrayList();
        JdbcConnection jc = new JdbcConnection();
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        if (dataComeType.equals("2")) {
            DataSourceProfileBean bean = dataSourceProfileService.getBeanByUuid(dataComeId);
            String databaseType = bean.getDatabaseType();
            String userName = bean.getUserName();
            String passWord = bean.getPassWord();
            String host = bean.getHost();
            String port = bean.getPort();
            String databaseSid = bean.getDatabaseSid();
            String url = "";
            if (databaseType.equals("1")) {
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
            } else if (databaseType.equals("2")) {

            } else if (databaseType.equals("3")) {
                url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
            }
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(sql);
                // util.fillParameters(pState, pMap);
                rs = pState.executeQuery();

                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                for (int i = 1; i <= numberOfColumns; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String columnName = rsmd.getColumnName(i);
                    String columnType = rsmd.getColumnTypeName(i);
                    map.put("columnName", columnName);
                    map.put("columnType", columnType);
                    map.put("titleName", columnName);
                    list.add(map);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        } else {
            try {
                connection = jc.openConnection();
                pState = connection.prepareStatement(sql);
                // util.fillParameters(pState, pMap);
                rs = pState.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                for (int i = 1; i <= numberOfColumns; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String columnName = rsmd.getColumnName(i);
                    String columnType = rsmd.getColumnTypeName(i);
                    map.put("columnName", columnName);
                    map.put("columnType", columnType);
                    map.put("titleName", columnName);
                    list.add(map);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        }

        return list;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService#queryForAcl(java.lang.String, com.wellsoft.pt.security.acl.support.QueryInfo, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> queryForAcl(String dataSourceDefId, QueryInfo aclQueryInfo, PagingInfo pagingInfo) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        DataSourceDefinitionBean bean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        String roleType = bean.getRoleType();
        String roleValue = bean.getRoleValue();
        // 分页请求参数
        if (pagingInfo != null) {
            aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
            aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        } else {
            // 设置当前页
            aclQueryInfo.getPage().setPageNo(1);
            // 设置每页的页数
            aclQueryInfo.getPage().setPageSize(10);
        }
        StringBuilder selectionHql = new StringBuilder();
        Class<IdEntity> Entityname = null;
        // 获取选取的实体类表
        String tableUuid = bean.getChooseDataId();
        List<SystemTableRelationship> relationShip = getViewDataNewServiceImpl.getAttributesByrelationship(tableUuid);
        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        Set<DataSourceColumn> columns = bean.getDataSourceColumns();
        // 多表时
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");

                for (DataSourceColumn dataSourceColumn : columns) {
                    String field = dataSourceColumn.getFieldName();
                    String entityName = dataSourceColumn.getEntityName();
                    String columnName = dataSourceColumn.getColumnName();
                    String columnAlias = dataSourceColumn.getColumnAliase();
                    String titleName = dataSourceColumn.getTitleName();
                    String defaultSort = dataSourceColumn.getDefaultSort();
                    String idExport = dataSourceColumn.getIsExport();
                    /**************排序*************/
                    if (defaultSort == null) {
                        defaultSort = "";
                    }

                    String mainTN = mainTNs[1].toUpperCase();
                    String entityN = entityName.toUpperCase();
                    String secondaryTN = secondaryTNs[1].toUpperCase();
                    if (idExport.equals("true")) {
                        if (mainTN.equals(entityN)) {
                            selectionHql.append(",o." + columnName + " as " + columnAlias);
                            // 默认排序
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                aclQueryInfo.addOrderby(columnAlias, "asc");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                aclQueryInfo.addOrderby(columnAlias, "desc");
                            }
                        } else if (secondaryTN.equals(entityN)) {
                            selectionHql.append(",o." + associateAttribute + "." + columnName + " as " + columnAlias);
                            // 默认排序
                            if (defaultSort != null) {
                                if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "asc");
                                } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + columnName, "desc");
                                }
                            }
                        }
                    }
                }
            }
        }
        // 单表时
        else {
            String mainTableName = st.getFullEntityName();// 主表名
            try {
                Entityname = (Class<IdEntity>) Class.forName(mainTableName);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            }
            for (DataSourceColumn dataSourceColumn : columns) {
                String field = dataSourceColumn.getFieldName();
                String columnName = dataSourceColumn.getColumnName();
                String columnAlias = dataSourceColumn.getColumnAliase();
                String defaultSort = dataSourceColumn.getDefaultSort();
                String titleName = dataSourceColumn.getTitleName();
                // if (!field.equals("uuid")) {
                selectionHql.append(",o." + columnName + " as " + columnAlias);

                // 默认排序
                if (defaultSort != null) {
                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                        aclQueryInfo.addOrderby(columnName, "asc");
                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                        aclQueryInfo.addOrderby(columnName, "desc");
                    }
                }
            }
        }
        aclQueryInfo.setSelectionHql(selectionHql.toString().replaceFirst(",", ""));
        // 处理权限角色值
        List<Permission> roTypeList = new ArrayList<Permission>();
        if (StringUtils.isNotBlank(roleType)) {
            String[] roTypeArray = roleType.split(";");
            for (int i = 0; i < roTypeArray.length; i++) {
                roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
            }
        }
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());
        // 个人权限
        if (roleValue.equals("person")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
            pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
        }
        // 群组权限
        else if (roleValue.equals("group")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
        } else if (roleValue.equals("all")) {
            queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());
        }
        return queryItems;
    }
}
