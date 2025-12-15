/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataMetaData.ColumnMetaData;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
@Service
@Transactional(readOnly = true)
public class IexportDataRecordSetServiceImpl extends BaseServiceImpl implements
        IexportDataRecordSetService {

    public IexportDataMetaData getMetaData(String group, String tableName) {
        // 从缓存获取
        String cacheKey = "MetaData" + "_" + group + "_" + tableName;
        IexportDataMetaData metaData = (IexportDataMetaData) IexportDataRecordSetCacheUtils.getValue(
                cacheKey);
        if (metaData != null) {
            return metaData;
        }

        metaData = new IexportDataMetaData();
        metaData.setGroup(group);
        metaData.setTableName(tableName);
        List<ColumnMetaData> columnMetaDatas = new ArrayList<ColumnMetaData>();
        try {
            JdbcConnectionAccess jdbcConnectionAccess = ((SessionImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory().getCurrentSession()).getJdbcConnectionAccess();
            Connection connection = jdbcConnectionAccess.obtainConnection();

            // 列信息
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "select * from " + metaData.getTableName() + "  where 1=2");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            // 获得列的信息
            int size = rsmd.getColumnCount();
            for (int i = 1; i <= size; i++) {
                String columnName = rsmd.getColumnName(i);
                String dataType = rsmd.getColumnTypeName(i);

                ColumnMetaData cmd = new ColumnMetaData();
                cmd.setName(columnName);
                cmd.setDataType(dataType);
                columnMetaDatas.add(cmd);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        metaData.setColumnMetaDatas(columnMetaDatas);

        // 放入缓存
        IexportDataRecordSetCacheUtils.put(cacheKey, metaData);
        return metaData;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordSetService#getData(IexportData, IexportDataMetaData)
     */
    @Override
    public IexportDataRecordSet getData(IexportData iexportData) {
        IexportDataMetaData iexportDataMetaData = getMetaData(iexportData.getType(),
                TableMetaData.getTableName(iexportData.getType()));
        String uuid = iexportData.getUuid();

        List<IexportDataRecordSet> results = extractData("UUID", "UUID", uuid, iexportDataMetaData);

        if (results.size() == 1) {
            return results.get(0);
        }

        throw new RuntimeException("data error");
    }

    /**
     * 如何描述该方法
     *
     * @param iexportData
     * @param iexportDataMetaData
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<IexportDataRecordSet> extractData(String pkName, String extractColumnName,
                                                   String extractColumnValue,
                                                   IexportDataMetaData iexportDataMetaData) {
        String queryString = getSelectSql(extractColumnName, extractColumnValue,
                iexportDataMetaData);
        Query query = this.dao.getSession().createSQLQuery(queryString);
        query.setString(extractColumnName, extractColumnValue);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> listObject = query.list();

        List<IexportDataRecordSet> recordSets = new ArrayList<IexportDataRecordSet>();
        for (Map<String, Object> src : listObject) {
            IexportDataRecordSet result = new IexportDataRecordSet();
            recordSets.add(result);

            // 1、主表
            Map<String, Object> map = new HashMap<>();
            map.putAll(src);
            for (Entry<String, Object> entry : src.entrySet()) {
                Object val = entry.getValue();
                // Clob转成字符串
                if (val instanceof Clob) {
                    try {
                        map.put(entry.getKey(),
                                IOUtils.toString(((Clob) val).getCharacterStream()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            result.setIexportDataMetaData(iexportDataMetaData);
            // 主键
            result.setPkName(pkName);
            StringBuilder sb = new StringBuilder();
            String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
            for (int i = 0; i < pkNames.length; i++) {
                Object value = map.get(pkNames[i]) != null ? map.get(pkNames[i]) : map.get(
                        pkNames[i].toLowerCase());
                sb.append(value.toString());
                if (i + 1 < pkNames.length) {
                    sb.append(Separator.SEMICOLON.getValue());
                }
            }
            result.setPkValue(sb.toString());
            // 外键（查询键）
            result.setFkName(extractColumnName);
            StringBuilder sb2 = new StringBuilder();
            String[] fkNames = StringUtils.split(extractColumnName, Separator.SEMICOLON.getValue());
            for (int i = 0; i < fkNames.length; i++) {
                Object value = map.get(fkNames[i]) != null ? map.get(fkNames[i]) : map.get(
                        fkNames[i].toLowerCase());
                if (value == null) {
                    value = map.get(fkNames[i].toUpperCase());
                }
                sb2.append(value.toString());
                if (i + 1 < fkNames.length) {
                    sb2.append(Separator.SEMICOLON.getValue());
                }
            }
            result.setFkValue(sb2.toString());
            //
            result.setData(map);

            // 2、从表
            String tableName = iexportDataMetaData.getTableName();
            List<TableRelation> slaveTables = TableMetaData
                    .getRelationTables(iexportDataMetaData.getGroup(), tableName);
            Map<String, List<IexportDataRecordSet>> mapData = new HashMap<String, List<IexportDataRecordSet>>();
            for (TableRelation relation : slaveTables) {
                String slaveTableName = relation.getSlaveTableName();
                String pk = TableMetaData.getPk(slaveTableName);
                List<IexportDataRecordSet> list = new ArrayList<IexportDataRecordSet>();
                String relationUuid = map.get(pkName) != null ? map.get(
                        pkName).toString() : map.get(pkName.toLowerCase()).toString();
                list.addAll(extractData(pk, relation.getRelationColumnName(), relationUuid,
                        getMetaData(iexportDataMetaData.getGroup(), slaveTableName)));
                mapData.put(slaveTableName, list);
            }
            result.setListData(mapData);
        }
        return recordSets;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordSetService#getIexportRowData(String, String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getIexportRowData(String uuid, String type) {
        IexportDataMetaData iexportDataMetaData = getMetaData(type,
                TableMetaData.getTableName(type));
        String queryString = getSelectSql("uuid", uuid, iexportDataMetaData);
        Query query = this.dao.getSession().createSQLQuery(queryString);
        query.setString("uuid", uuid);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> listObject = query.list();
        if (listObject.size() == 0 || listObject.size() > 1) {
            throw new RuntimeException(
                    "数据错误，表名[" + iexportDataMetaData.getTableName() + "], UUID[" + uuid + "]");
        }

        // 单行数据
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map<String, Object> object : listObject) {
            map.putAll(object);
            for (Entry<String, Object> entry : object.entrySet()) {
                Object val = entry.getValue();
                // Clob转成字符串
                if (val instanceof Clob) {
                    try {
                        map.put(entry.getKey(),
                                IOUtils.toString(((Clob) val).getCharacterStream()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordSetService#save(IexportDataRecordSet)
     */
    @Override
    public void save(IexportDataRecordSet iexportDataResultSet) {
        // 还原数据
        restoreData(iexportDataResultSet);
    }


    /**
     * 如何描述该方法
     *
     * @param iexportDataResultSet
     */
    private void restoreData(IexportDataRecordSet iexportDataResultSet) {
        // 主表
        String pkName = iexportDataResultSet.getPkName();
        String pkValue = iexportDataResultSet.getPkValue();
        String type = iexportDataResultSet.getIexportDataMetaData().getGroup();

        // 从缓存获取是否已经还原
        String cacheKey = "IexportDataRecordSet" + pkName + pkValue + type;
        if (Boolean.TRUE.equals(IexportDataRecordSetCacheUtils.getValue(cacheKey))) {
            return;
        }
        // 添加单位系统字段，以支持新旧系统导入
        addOrRemoveSystemUnitFieldIfNeed(iexportDataResultSet);

        if (buildSQLQuery(iexportDataResultSet, true).executeUpdate() == 0) {//更新
            buildSQLQuery(iexportDataResultSet, false).executeUpdate();//新增
        }

        // 从表
        Map<String, List<IexportDataRecordSet>> listData = iexportDataResultSet.getListData();
        for (Entry<String, List<IexportDataRecordSet>> entry : listData.entrySet()) {
            List<IexportDataRecordSet> list = entry.getValue();
            for (IexportDataRecordSet record : list) {
                // 确保外键关系的值存在
                if (isFkValueExists(record)) {
                    restoreData(record);
                }
            }
        }

        IexportDataRecordSetCacheUtils.put(cacheKey, true);
    }


    private Query buildSQLQuery(IexportDataRecordSet iexportDataResultSet, boolean isUpdate) {
        String sql = isUpdate ? getUpdateSql(iexportDataResultSet) : getInsertSql(
                iexportDataResultSet);
        Query query = this.dao.getSession().createSQLQuery(sql);
        Map<String, Object> data = iexportDataResultSet.getData();
        List<ColumnMetaData> columnMetaDatas = iexportDataResultSet.getIexportDataMetaData().getColumnMetaDatas();
        Map<String, ColumnMetaData> columnMap = ConvertUtils.convertElementToMap(columnMetaDatas,
                "name");
        for (Entry<String, Object> entry : data.entrySet()) {
            String name = entry.getKey();
            Object val = entry.getValue();
            if (val == null) {
                continue;
            }

            if (!isUpdate && StringUtils.equalsIgnoreCase(name,
                    TenantEntity.SYSTEM_UNIT_ID4DB)) {//插入的数据，允许新数据变更为当前用户单位
                query.setString(name, SpringSecurityUtils.getCurrentUserUnitId());
            }

            if (columnMap.containsKey(name)) {
                ColumnMetaData columnMetaData = columnMap.get(name);
                String dataType = columnMetaData.getDataType();
                if (dataType.equalsIgnoreCase("LONG")) {
                    query.setLong(name, (Long) val);
                } else if (dataType.equalsIgnoreCase("INT")) {
                    query.setInteger(name, (Integer) val);
                } else if (dataType.equalsIgnoreCase("BIGINT")) {
                    query.setBigDecimal(name, (BigDecimal) val);
                } else if (dataType.equalsIgnoreCase("TIMESTAMP")) {
                    query.setTimestamp(name, (Date) val);
                } else if (dataType.equalsIgnoreCase("NVARCHAR2")) {
                    query.setString(name, (String) val);
                } else if (dataType.equalsIgnoreCase("DOUBLE")) {
                    query.setDouble(name, (Double) val);
                } else if (dataType.equalsIgnoreCase("FLOAT")) {
                    query.setFloat(name, (Float) val);
                } else if (dataType.equalsIgnoreCase("NUMBER")) {
                    query.setBigDecimal(name, (BigDecimal) val);
                } else if (dataType.equalsIgnoreCase("CLOB")) {
                    query.setText(name, (String) val);
                } else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase(
                        "DATETIME")) {
                    query.setDate(name, (Date) val);
                } else if (dataType.equalsIgnoreCase("VARCHAR2") || dataType.equalsIgnoreCase(
                        "VARCHAR")) {
                    query.setString(name, (String) val);
                } else if (dataType.equalsIgnoreCase("BLOB")) {
                    throw new RuntimeException("data type [BLOB] is not support");
                }
            } else {
                query.setParameter(name, val);
            }
        }
        if (isUpdate) {
            setPkParam(query, iexportDataResultSet.getPkName(), iexportDataResultSet.getPkValue());
        }
        return query;
    }


    /**
     * @param record
     * @return
     */
    private boolean isFkValueExists(IexportDataRecordSet iexportDataResultSet) {
        // 主表
        String pkName = iexportDataResultSet.getFkName();
        if (StringUtils.isBlank(pkName)) {
            pkName = iexportDataResultSet.getPkName();
        }
        String pkValue = iexportDataResultSet.getFkValue();
        if (StringUtils.isBlank(pkValue)) {
            pkValue = iexportDataResultSet.getPkValue();
        }
        String type = iexportDataResultSet.getIexportDataMetaData().getGroup();

        // 从缓存获取是否已经还原
        String cacheKey = "IexportDataRecordSet" + pkName + pkValue + type;
        if (Boolean.TRUE.equals(IexportDataRecordSetCacheUtils.getValue(cacheKey))) {
            return false;
        }
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        for (int i = 0; i < pkNames.length; i++) {
            String name = pkNames[i];
            String value = pkValues[i];
            List<TableRelation> tableRelations = TableMetaData.getTableRelations(
                    iexportDataResultSet
                            .getIexportDataMetaData().getTableName(), name);
            if (tableRelations.isEmpty()) {
                continue;
            }
            for (TableRelation tableRelation : tableRelations) {
                String primaryTable = tableRelation.getPrimaryTableName();
                String primaryTablePkName = TableMetaData.getPk(primaryTable);
                String primaryPkValue = value;
                String queryString = getPkCountSql(primaryTable, primaryTablePkName,
                        primaryPkValue);
                Query query = this.dao.getSession().createSQLQuery(queryString);
                setPkParam(query, primaryTablePkName, primaryPkValue);
                Long count = ((BigDecimal) query.uniqueResult()).longValue();
                if (Long.valueOf(0l).equals(count)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param query
     * @param pkName
     * @param pkValue
     * @return
     */
    private void setPkParam(Query query, String pkName, String pkValue) {
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
    private void setParameter(Query query, String paramName, Integer paramValue) {
        query.setParameter(paramName, paramValue);
    }

    /**
     * 如何描述该方法
     *
     * @param iexportDataResultSet
     * @return
     */
    private String getCountSql(IexportDataRecordSet iexportDataResultSet) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = iexportDataResultSet.getPkName();
        String pkValue = iexportDataResultSet.getPkValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("count.ftl", root, iexportDataResultSet);
    }

    /**
     * @param tableName
     * @param pkName
     * @param pkValue
     * @return
     */
    private String getPkCountSql(String tableName, String pkName, String pkValue) {
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
     * @param iexportDataMetaData
     * @return
     */
    private String getUpdateSql(IexportDataRecordSet iexportDataResultSet) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = iexportDataResultSet.getPkName();
        String pkValue = iexportDataResultSet.getPkValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("update.ftl", root, iexportDataResultSet);
    }

    /**
     * 如何描述该方法
     *
     * @param iexportDataResultSet
     * @return
     */
    private String getInsertSql(IexportDataRecordSet iexportDataResultSet) {
        Map<String, Object> root = new HashMap<String, Object>();
        String pkName = iexportDataResultSet.getPkName();
        String pkValue = iexportDataResultSet.getPkValue();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        String[] pkValues = StringUtils.split(pkValue, Separator.SEMICOLON.getValue());
        root.put("pkNames", pkNames);
        root.put("pkValues", pkValues);
        return generateSql("insert.ftl", root, iexportDataResultSet);
    }

    /**
     * @param iexportDataMetaData
     * @return
     * @throws Exception
     */
    private String getSelectSql(String pkName, String uuid,
                                IexportDataMetaData iexportDataMetaData) {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("pkName", pkName);
        root.put("uuid", uuid);
        return generateSql("select.ftl", root, iexportDataMetaData);
    }

    private String generateSql(String name, Map<String, Object> root,
                               IexportDataMetaData iexportDataMetaData) {
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(IexportDataRecordSet.class, "ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            root.put("tableName", iexportDataMetaData.getTableName());
            root.put("columns", iexportDataMetaData.getColumnMetaDatas());
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
     * 如何描述该方法
     *
     * @param iexportData
     * @param iexportDataMetaData
     * @return
     */
    private String generateSql(String name, Map<String, Object> root,
                               IexportDataRecordSet iexportDataRecordSet) {
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(IexportDataRecordSet.class, "ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            Map<String, Object> datas = iexportDataRecordSet.getData();
            IexportDataMetaData iexportDataMetaData = iexportDataRecordSet.getIexportDataMetaData();
            List<ColumnMetaData> columnMetaData = iexportDataMetaData.getColumnMetaDatas();
            List<ColumnMetaData> renderColumnMetaData = Lists.newArrayList();
            String pkName = iexportDataRecordSet.getPkName();
            String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
            for (ColumnMetaData metaData : columnMetaData) {
                if (datas.get(metaData.getName()) != null) {
                    if ("update.ftl".equalsIgnoreCase(name)) {
                        //更新忽略主键字段
                        if (pkNames.length != columnMetaData.size() && ArrayUtils.indexOf(pkNames, metaData.getName()) != -1) {
                            continue;
                        }
                    }
                    ColumnMetaData copy = new ColumnMetaData();
                    BeanUtils.copyProperties(metaData, copy);
                    renderColumnMetaData.add(copy);
                }
            }
            root.put("tableName", iexportDataRecordSet.getIexportDataMetaData().getTableName());
            root.put("columns", renderColumnMetaData);
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

    @Override
    public boolean isExists(IexportData iexportData) {
        String tableName = TableMetaData.getTableName(iexportData.getType());
        String sql = "select count(*) from " + tableName + " t where t.uuid = :uuid";
        Query query = this.dao.getSession().createSQLQuery(sql);
        setPkParam(query, "uuid", iexportData.getUuid());
        Long cont = ((BigDecimal) query.uniqueResult()).longValue();
        return cont != 0L;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordSetService#hasDifference(IexportData)
     */
    @Override
    public boolean hasDifference(IexportData iexportData) {
        String tableName = TableMetaData.getTableName(iexportData.getType());
        String sql = "select count(*) from " + tableName + " t where t.uuid = :uuid and t.rec_ver <> :rec_ver";
        Query query = this.dao.getSession().createSQLQuery(sql);
        setPkParam(query, "uuid", iexportData.getUuid());
        setParameter(query, "rec_ver", iexportData.getRecVer());
        Long cont = ((BigDecimal) query.uniqueResult()).longValue();
        if (cont == 0L) {
            return false;
        } else {
            return true;
        }
    }

    public ColumnMetaData existSystemUnitField(IexportDataMetaData metaData) {
        for (ColumnMetaData columnMetaData : metaData.getColumnMetaDatas()) {
            if (StringUtils.equalsIgnoreCase(columnMetaData.getName(),
                    TenantEntity.SYSTEM_UNIT_ID4DB)) {
                return columnMetaData;
            }
        }
        return null;
    }

    public ColumnMetaData localExistSystemUnitField(String groupType, String tableName) {
        return existSystemUnitField(getMetaData(groupType, tableName));
    }

    protected void addOrRemoveSystemUnitFieldIfNeed(IexportDataRecordSet iexportDataResultSet) {
        IexportDataMetaData remoteMetaData = iexportDataResultSet.getIexportDataMetaData();
        ColumnMetaData remoteSystemUnitColumn = existSystemUnitField(remoteMetaData);
        ColumnMetaData localSystemUnitColumn = localExistSystemUnitField(remoteMetaData.getGroup(),
                remoteMetaData.getTableName());
        if (null != remoteSystemUnitColumn && null == localSystemUnitColumn) {
            // 删除
            iexportDataResultSet.getData().remove(TenantEntity.SYSTEM_UNIT_ID4DB);
            remoteMetaData.getColumnMetaDatas().remove(remoteSystemUnitColumn);
        } else if (null == remoteSystemUnitColumn && null != localSystemUnitColumn) {
            // 添加
            remoteMetaData.getColumnMetaDatas().add(localSystemUnitColumn);
            iexportDataResultSet.getData().put(TenantEntity.SYSTEM_UNIT_ID4DB,
                    SpringSecurityUtils.getCurrentUserUnitId());
        }
    }

}
