/*
 * @(#)2019年4月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
 * 2019年4月7日.1	zhulh		2019年4月7日		Create
 * </pre>
 * @date 2019年4月7日
 */
public class IexportDataRelationalMappingUtils {

    private static final Logger logger = LoggerFactory.getLogger(IexportDataRelationalMappingUtils.class);

    /**
     * @param tableName
     * @return
     */
    public static IexportTableMetaData getMetaData(String tableName) {
        IexportTableMetaData tableMetaData = new IexportTableMetaData();
        tableMetaData.setTableName(tableName);
        List<ColumnMetaData> columnMetaDatas = new ArrayList<ColumnMetaData>();
        try {
            JdbcConnectionAccess jdbcConnectionAccess = ((SessionImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory().getCurrentSession()).getJdbcConnectionAccess();
            Connection connection = jdbcConnectionAccess.obtainConnection();

            // 列信息
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "select * from " + tableMetaData.getTableName() + "  where 1=2");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            // 获得列的信息
            int size = rsmd.getColumnCount();
            for (int i = 1; i <= size; i++) {
                String columnName = rsmd.getColumnName(i);
                String dataType = rsmd.getColumnTypeName(i);

                ColumnMetaData cmd = new ColumnMetaData();
                cmd.setName(StringUtils.lowerCase(columnName));
                cmd.setDataType(dataType);
                columnMetaDatas.add(cmd);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        tableMetaData.setColumnMetaDatas(columnMetaDatas);
        return tableMetaData;
    }

    /**
     * @param iexportDataMetaData
     * @return
     * @throws Exception
     */
    public static String getSelectSql(String pkName, String uuid, IexportTableMetaData tableMetaData) {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("pkName", pkName);
        root.put("uuid", uuid);
        return generateSql("select.ftl", root, tableMetaData);
    }

    /**
     * @param iexportData
     * @param iexportDataMetaData
     * @return
     */
    private static String generateSql(String name, Map<String, Object> root, IexportTableMetaData tableMetaData) {
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(IexportDataRecordSet.class, "ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

            root.put("tableName", tableMetaData.getTableName());
            root.put("columns", tableMetaData.getColumnMetaDatas());
            Template template = cfg.getTemplate(name, Encoding.UTF8.getValue());

            Writer writer = new StringWriter();
            template.process(root, writer);
            writer.flush();
            writer.close();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param record
     * @return
     */
    public static String getCountSql(IexportDataRecord record) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = record.getPrimaryKeyName();
        String pkValue = record.getPrimaryKeyValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("count.ftl", root, record.getMetaData().getTableMetaData(record.getTableName()));
    }

    /**
     * @param tableName
     * @param pkName
     * @param pkValue
     * @return
     */
    public static String getPkCountSql(String tableName, String pkName, String pkValue) {
        Map<String, Object> root = new HashMap<String, Object>();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(IexportDataRecordSet.class, "ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

            root.put("tableName", tableName);
            Template template = cfg.getTemplate("count.ftl", Encoding.UTF8.getValue());

            Writer writer = new StringWriter();
            template.process(root, writer);
            writer.flush();
            writer.close();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param record
     * @return
     */
    public static String getUpdateSql(IexportDataRecord record) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = record.getPrimaryKeyName();
        String pkValue = record.getPrimaryKeyValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("update.ftl", root, record.getMetaData().getTableMetaData(record.getTableName()));
    }

    /**
     * @param record
     * @return
     */
    public static String getInsertSql(IexportDataRecord record) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = record.getPrimaryKeyName();
        String pkValue = record.getPrimaryKeyValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("insert.ftl", root, record.getMetaData().getTableMetaData(record.getTableName()));
    }

    /**
     * @param pkName
     * @param pkValue
     * @param tableName
     * @return
     */
    public static String getDeleteSql(String tableName, String pkName, String pkValue) {
        Map<String, Object> root = new HashMap<String, Object>();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("delete.ftl", root, getMetaData(tableName));
    }

    /**
     * @param query
     * @param pkName
     * @param pkValue
     * @return
     */
    public static void setPkParam(Query query, String pkName, String pkValue) {
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        for (int i = 0; i < pkNames.length; i++) {
            query.setParameter(pkNames[i], pkValues[i]);
        }
    }

    /**
     * @param query
     * @param paramName
     * @param paramValue
     */
    public static void setParameter(Query query, String paramName, Integer paramValue) {
        query.setParameter(paramName, paramValue);
    }

}
