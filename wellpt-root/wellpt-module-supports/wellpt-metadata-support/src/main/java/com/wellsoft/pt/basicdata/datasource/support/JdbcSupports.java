/*
 * @(#)2014-8-14 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.support;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-14.1	wubin		2014-8-14		Create
 * 2016-8-8.2	zhongzh		2016-8-8		Update
 * </pre>
 * @date 2014-8-14
 */
public class JdbcSupports {
    private Logger logger = LoggerFactory.getLogger(JdbcSupports.class);
    private JdbcConnection jc = new JdbcConnection();

    /**
     * 测试外部数据连接是否成功
     *
     * @param databaseType
     * @param userName
     * @param passWord
     * @param url
     */
    public void test(String databaseType, String userName, String passWord, String url) {
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        try {
            if (databaseType.equals("1")) {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection
                        .prepareStatement("select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='TABLE'");
                rs = pState.executeQuery();
                while (rs.next()) {
                    // System.out.println(rs.getString("OBJECT_NAME"));
                }
            } else if (databaseType.equals("2")) {

            } else if (databaseType.equals("3")) {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement("show tables");
                rs = pState.executeQuery();
                while (rs.next()) {
                    // System.out.println(rs.getString("OBJECT_NAME"));
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pState);
            JdbcUtils.closeConnection(connection);
        }
    }

    /**
     * 获得外部数据配置的数据(数据库表、数据视图 等)
     *
     * @param databaseType
     * @param userName
     * @param passWord
     * @param url
     * @param sourceType
     * @return
     */
    public List getDataByOut(String databaseType, String userName, String passWord, String url, String sourceType) {
        List list = new ArrayList();
        StringBuilder psSql = new StringBuilder();
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        if (databaseType.equals("1")) {// oracle数据库
            if (sourceType.equals("1")) {
                // 数据库表
                psSql.append("select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='TABLE' order by t.OBJECT_NAME");
            } else if (sourceType.equals("2")) {
                // 数据视图
                psSql.append("select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='VIEW' order by t.OBJECT_NAME");
            }

            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(psSql.toString());
                rs = pState.executeQuery();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    // System.out.println(rs.getString("OBJECT_NAME"));
                    map.put("name", rs.getString("OBJECT_NAME"));
                    map.put("id", rs.getString("OBJECT_ID"));
                    list.add(map);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {// mysql数据库
            if (sourceType.equals("1")) {
                // 数据库表
                psSql.append("select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='TABLE' order by t.OBJECT_NAME");
            } else if (sourceType.equals("2")) {
                // 数据视图
                psSql.append("select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='VIEW' order by t.OBJECT_NAME");
            }

            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                java.sql.DatabaseMetaData metadata = connection.getMetaData();
                // System.out.println("数据库已知的用户: " + metadata.getUserName());
                // System.out.println("数据库的系统函数的逗号分隔列表: " +
                // metadata.getSystemFunctions());
                // System.out.println("数据库的时间和日期函数的逗号分隔列表: " +
                // metadata.getTimeDateFunctions());
                // System.out.println("数据库的字符串函数的逗号分隔列表: " +
                // metadata.getStringFunctions());
                // System.out.println("数据库供应商用于 'schema' 的首选术语: " +
                // metadata.getSchemaTerm());
                // System.out.println("数据库URL: " + metadata.getURL());
                // System.out.println("是否允许只读:" + metadata.isReadOnly());
                // System.out.println("数据库的产品名称:" +
                // metadata.getDatabaseProductName());
                // System.out.println("数据库的版本:" +
                // metadata.getDatabaseProductVersion());
                // System.out.println("驱动程序的名称:" + metadata.getDriverName());
                // System.out.println("驱动程序的版本:" + metadata.getDriverVersion());

                // System.out.println("获取指定的数据库的所有表的类型");
                ResultSet rs1 = metadata.getTables(url.split("/")[1], null, null, null);
                while (rs1.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    // System.out.println();
                    // System.out.println("数据库名:" + rs1.getString(1));
                    // System.out.println("表名: " + rs1.getString(3));
                    // System.out.println("类型: " + rs1.getString(4));
                    map.put("name", rs1.getString(3));
                    map.put("id", rs1.getString(4));
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
     * 获得外部数据源的一张数据库表(视图)的字段信息
     *
     * @param databaseType
     * @param userName
     * @param passWord
     * @param url
     * @param sourceType
     * @return
     */
    public List getColumnByOut(String databaseType, String owner, String userName, String passWord, String url,
                               String sourceType, String tableName) {
        List list = new ArrayList();
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        if (databaseType.equals("1")) {
            StringBuilder psSql = new StringBuilder();
            if (sourceType.equals("1")) {
                // 数据库表
                // t.mandt这个条件是暂时性加上
                psSql.append("select t.COLUMN_NAME,t.DATA_TYPE from dba_tab_columns t where  owner = '" + owner
                        + "' and t.TABLE_NAME = '" + tableName + "'");
            }

            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(psSql.toString());
                rs = pState.executeQuery();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", rs.getString("COLUMN_NAME"));
                    map.put("dataType", rs.getString("DATA_TYPE"));
                    list.add(map);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            StringBuilder psSql = new StringBuilder();
            psSql.append("select * from " + tableName);
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(psSql.toString());
                rs = pState.executeQuery();
                ResultSetMetaData rsme = rs.getMetaData();
                int columnCount = rsme.getColumnCount();
                // System.out.println("ResultSet对象中的列数" + columnCount);
                for (int i = 1; i < columnCount; i++) {
                    Map<String, String> map = new HashMap<String, String>();
                    // System.out.println();
                    // System.out.println("列名称: " + rsme.getColumnName(i));
                    // System.out.println("列类型(DB): " +
                    // rsme.getColumnTypeName(i));
                    // System.out.println("长度: " + rsme.getPrecision(i));
                    // System.out.println("是否自动编号: " + rsme.isAutoIncrement(i));
                    // System.out.println("是否可以为空: " + rsme.isNullable(i));
                    // System.out.println("是否可以写入: " + rsme.isReadOnly(i));
                    map.put("name", rsme.getColumnName(i));
                    map.put("dataType", rsme.getColumnTypeName(i));
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
     * 根据传入的sql查询总数
     *
     * @param databaseType
     * @param userName
     * @param passWord
     * @param url
     * @param sourceType
     * @param sqlText
     * @param pMap
     * @param pagingInfo
     * @return
     */
    public Long getTotalBySql(String databaseType, String userName, String passWord, String url, String sourceType,
                              String sqlText, Map<String, Object> pMap, PagingInfo pagingInfo) {
        Long value = 0L;
        NamedParamSqlUtil util = new NamedParamSqlUtil();
        String sqlNew = util.parseSql(sqlText);
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        if (databaseType.equals("1")) {
            String sqlByTotal = "select count(*) from (" + sqlNew + ") A";
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(sqlByTotal);
                util.fillParameters(pState, pMap);
                rs = pState.executeQuery();
                while (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columName = rs.getMetaData().getColumnLabel(i);
                        Object obj = rs.getString(columName);
                        value = Long.valueOf(String.valueOf(obj));
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            String sqlByTotal = "select count(*) from (" + sqlNew + ") A";
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(sqlByTotal);
                util.fillParameters(pState, pMap);
                rs = pState.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int count = rsmd.getColumnCount();// 得到表里字段的总数
                while (rs.next()) {
                    for (int i = 0; i < count; i++) {
                        value = Long.valueOf(String.valueOf(rs.getObject(i + 1)));
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        }
        return value;
    }

    /**
     * 根据传入的sql查询数据
     *
     * @param databaseType
     * @param userName
     * @param passWord
     * @param url
     * @param sourceType
     * @param tableName
     * @param sqlText
     * @return
     */
    public List<QueryItem> getDataBySql(String databaseType, String userName, String passWord, String url,
                                        String sourceType, String sqlText, Map<String, Object> pMap, PagingInfo pagingInfo) {
        List<QueryItem> list = new ArrayList<QueryItem>();
        NamedParamSqlUtil util = new NamedParamSqlUtil();
        String sqlNew = util.parseSql(sqlText);
        int firstSize = 0;
        int maxSize = 0;
        Connection connection = null;
        PreparedStatement pState = null;
        ResultSet rs = null;
        if (pagingInfo != null) {
            if (pagingInfo.getCurrentPage() == 0) {
                pagingInfo.setCurrentPage(1);
            }
            firstSize = (pagingInfo.getCurrentPage() - 1) * pagingInfo.getPageSize();
            maxSize = pagingInfo.getPageSize() + firstSize;
        }
        if (databaseType.equals("1")) {
            String sqlByPage = "";
            if (firstSize == 0 && maxSize == 0) {
                sqlByPage = sqlNew;
            } else {
                sqlByPage = "select * from (select A.*,rownum r from (" + sqlNew + ") A where rownum <= '" + maxSize
                        + "' ) where r>= '" + firstSize + "'";
            }
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(sqlByPage);
                util.fillParameters(pState, pMap);
                rs = pState.executeQuery();
                while (rs.next()) {
                    QueryItem map = new QueryItem();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columName = rs.getMetaData().getColumnLabel(i);
                        Object obj = rs.getString(columName);
                        map.put(columName, obj);
                    }
                    list.add(map);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pState);
                JdbcUtils.closeConnection(connection);
            }
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            String sqlByPage = "";
            if (pagingInfo.getPageSize() != 0) {
                sqlByPage = sqlNew + " limit " + firstSize + "," + pagingInfo.getPageSize();
            } else {
                sqlByPage = sqlNew;
            }
            try {
                connection = jc.createConnection(databaseType, userName, passWord, url);
                pState = connection.prepareStatement(sqlByPage);
                util.fillParameters(pState, pMap);
                rs = pState.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int count = rsmd.getColumnCount();// 得到表里字段的总数
                while (rs.next()) {
                    QueryItem map = new QueryItem();
                    for (int i = 0; i < count; i++) {
                        map.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));// 名字和值
                    }
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
}
