/*
 * @(#)2019年4月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordService;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecorderService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import com.wellsoft.pt.basicdata.iexport.suport.InsertedRecords.InsertedRecord;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
 * 2019年4月7日.1	zhulh		2019年4月7日		Create
 * </pre>
 * @date 2019年4月7日
 */
@Service
@Transactional(readOnly = true)
public class IexportDataRecordServiceImpl extends BaseServiceImpl implements IexportDataRecordService {

    @Autowired
    private IexportDataRecorderService iexportDataRecorderService;

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordService#getRecord(IexportData)
     */
    @Override
    public IexportDataRecord getRecord(IexportData iexportData) {
        //		IexportDataMetaData metaData = IexportDataRelationalMappingUtils.getMetaData(iexportData.getType(),
        //				);
        String uuid = iexportData.getUuid();
        String tableName = TableMetaData.getTableName(iexportData.getType());
        IexportDataRecord record = extractRecord(JpaEntity.UUID, uuid, tableName, iexportData.getType(),
                iexportData.getMetaData());

        return record;
    }

    /**
     * @param pkName
     * @param pkValue
     * @param extractColumnValue
     * @param metaData
     * @return
     */
    @SuppressWarnings("unchecked")
    private IexportDataRecord extractRecord(String pkName, String pkValue, String tableName, String iexportType,
                                            IexportMetaData iexportMetaData) {
        IexportTableMetaData tableMetaData = iexportMetaData.getTableMetaData(tableName);
        String queryString = IexportDataRelationalMappingUtils.getSelectSql(pkName, pkValue, tableMetaData);
        Query query = this.dao.getSession().createSQLQuery(queryString);
        query.setString(pkName, pkValue);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> listObject = query.list();
        if (listObject.size() > 1) {
            throw new RuntimeException("data error, exists more than one recoed for main record！");
        }

        Map<String, Object> object = new CaseInsensitiveMap();
        object.putAll(listObject.get(0));
        // 1、主表
        IexportDataRecord dataRecord = convert2DataRecord(object, pkName, tableMetaData, iexportMetaData);

        // 2、从表
        Map<String, List<IexportDataRecord>> relationalDataMap = extractRelationRecord(pkValue, iexportType,
                tableMetaData, iexportMetaData);
        dataRecord.setRelationalDataMap(relationalDataMap);
        return dataRecord;
    }

    /**
     * @param relationDataUuid
     * @param mainMetaData
     * @return
     */
    private Map<String, List<IexportDataRecord>> extractRelationRecord(String relationDataUuid, String iexportType,
                                                                       IexportTableMetaData mainTableMetaData, IexportMetaData iexportMetaData) {
        String mainGroup = iexportType;
        String mainTableName = mainTableMetaData.getTableName();
        Map<String, List<IexportDataRecord>> relationalDataMap = Maps.newHashMap();

        List<TableRelation> slaveTables = TableMetaData.getRelationTables(mainGroup, mainTableName);
        for (TableRelation relation : slaveTables) {
            String slaveTableName = relation.getSlaveTableName();
            IexportTableMetaData slaveTableMetaData = IexportDataRelationalMappingUtils.getMetaData(slaveTableName);
            List<IexportDataRecord> list = extractRelationRecord(relation, slaveTableMetaData, iexportMetaData,
                    relationDataUuid);
            relationalDataMap.put(slaveTableName, list);
            // 从表的从表
            for (IexportDataRecord slaveRecord : list) {
                slaveRecord.setRelationalDataMap(extractRelationRecord(slaveRecord.getPrimaryKeyValue(), iexportType,
                        slaveTableMetaData, iexportMetaData));
            }
        }
        return relationalDataMap;
    }

    /**
     * @param relation
     * @param metaData
     * @param mainDataRecord
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<IexportDataRecord> extractRelationRecord(TableRelation relation,
                                                          IexportTableMetaData slaveTableMetaData, IexportMetaData iexportMetaData, String relationDataUuid) {
        String slaveTableName = relation.getSlaveTableName();
        String slaveTablePkName = TableMetaData.getPk(slaveTableName);
        String relationColumnName = relation.getRelationColumnName();

        String queryString = IexportDataRelationalMappingUtils.getSelectSql(relationColumnName, relationDataUuid,
                slaveTableMetaData);
        Query query = this.dao.getSession().createSQLQuery(queryString);
        query.setString(relationColumnName, relationDataUuid);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> listObject = query.list();
        List<IexportDataRecord> dataRecords = Lists.newArrayList();
        for (Map<String, Object> data : listObject) {
            IexportDataRecord record = convert2DataRecord(data, slaveTablePkName, slaveTableMetaData, iexportMetaData,
                    relation);
            record.setTableName(slaveTableName);
            dataRecords.add(record);
        }
        return dataRecords;
    }

    /**
     * @param object
     * @param pkName
     * @param metaData
     * @return
     */
    private IexportDataRecord convert2DataRecord(Map<String, Object> object, String pkName,
                                                 IexportTableMetaData tableMetaData, IexportMetaData metaData) {
        return convert2DataRecord(object, pkName, tableMetaData, metaData, null);
    }

    /**
     * @param object
     * @param pkName
     * @param metaData
     * @return
     */
    private IexportDataRecord convert2DataRecord(Map<String, Object> object, String pkName,
                                                 IexportTableMetaData tableMetaData, IexportMetaData metaData, TableRelation relation) {
        IexportDataRecord dataRecord = new IexportDataRecord();
        for (Entry<String, Object> entry : object.entrySet()) {
            Object value = entry.getValue();
            // Clob转成字符串
            if (value instanceof Clob) {
                try {
                    value = IOUtils.toString(((Clob) value).getCharacterStream());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            IexportDataColumn dataColumn = new IexportDataColumn();
            dataColumn.setName(StringUtils.lowerCase(entry.getKey()));
            dataColumn.setValue(value);
            dataRecord.getDataColumns().add(dataColumn);
        }

        // 表名
        dataRecord.setTableName(tableMetaData.getTableName());
        // 主键
        dataRecord.setPrimaryKeyName(pkName);
        StringBuilder sb = new StringBuilder();
        String[] pkNames = StringUtils.split(pkName, Separator.SEMICOLON.getValue());
        for (int i = 0; i < pkNames.length; i++) {
            sb.append(object.get(pkNames[i]).toString());
            if (i + 1 < pkNames.length) {
                sb.append(Separator.SEMICOLON.getValue());
            }
        }
        // dataRecord.setPrimaryKeyValue(sb.toString());
        dataRecord.setMetaData(metaData);
        dataRecord.setTableRelation(relation);

        return dataRecord;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordService#storeRecord(IexportData)
     */
    @Override
    @Transactional
    public void storeRecord(IexportData iexportData) {
        IexportDataRecord record = iexportData.getRecord();
        restoreData(record, iexportData.getType());
    }

    /**
     * @param record
     */
    private void restoreData(IexportDataRecord record, String iexportType) {
        IexportMetaData iexportMetaData = record.getMetaData();
        // 主表
        String pkName = record.getPrimaryKeyName();
        String pkValue = record.getPrimaryKeyValue();
        String type = iexportType;

        // 从缓存获取是否已经还原
        String cacheKey = "IexportDataRecordSet" + pkName + pkValue + type;
        if (Boolean.TRUE.equals(IexportDataRecordSetCacheUtils.getValue(cacheKey))) {
            return;
        }
        // 添加单位系统字段，以支持新旧系统导入
        // addOrRemoveSystemUnitFieldIfNeed(iexportDataResultSet);
        Map<String, Object> data = record.getData();
        if (data.isEmpty()) {
            return;
        }

        String queryString = IexportDataRelationalMappingUtils.getCountSql(record);
        Query query = this.dao.getSession().createSQLQuery(queryString);
        IexportDataRelationalMappingUtils.setPkParam(query, pkName, pkValue);
        Long count = ((BigDecimal) query.uniqueResult()).longValue();

        String insertString = null;
        if (count > 0) {
            // 更新
            insertString = IexportDataRelationalMappingUtils.getUpdateSql(record);
            iexportDataRecorderService.recordUpdate(record);
        } else {
            // 插入
            insertString = IexportDataRelationalMappingUtils.getInsertSql(record);
            iexportDataRecorderService.recordInsert(record);
        }
        Query insertQuery = this.dao.getSession().createSQLQuery(insertString);
        List<ColumnMetaData> columnMetaDatas = iexportMetaData.getTableMetaData(record.getTableName())
                .getColumnMetaDatas();
        Map<String, ColumnMetaData> columnMap = ConvertUtils.convertElementToMap(columnMetaDatas, "name");
        boolean isSuperAdmin = MultiOrgSystemUnit.PT_ID.equals(SpringSecurityUtils.getCurrentUserUnitId());
        for (Entry<String, Object> entry : data.entrySet()) {
            String name = entry.getKey();
            Object val = entry.getValue();
            /**
             * 从超级管理后台配置导入，配置项的单位字段按配置数据原始值导入
             * 从单位管理后台配置导入，配置项的单位字段按当前登录用户的单位ID保存
             */
            if (!isSuperAdmin && StringUtils.equalsIgnoreCase(name, TenantEntity.SYSTEM_UNIT_ID4DB)) {
                // 设置当前系统单位
                insertQuery.setString(name, SpringSecurityUtils.getCurrentUserUnitId());
            } else if (columnMap.containsKey(name)) {
                ColumnMetaData columnMetaData = columnMap.get(name);
                String dataType = columnMetaData.getDataType();
                if (dataType.equalsIgnoreCase("LONG")) {
                    insertQuery.setLong(name, (Long) val);
                } else if (dataType.equalsIgnoreCase("TIMESTAMP")) {
                    insertQuery.setTimestamp(name, (Date) val);
                } else if (dataType.equalsIgnoreCase("NVARCHAR2")) {
                    insertQuery.setString(name, (String) val);
                } else if (dataType.equalsIgnoreCase("FLOAT")) {
                    insertQuery.setFloat(name, (Float) val);
                } else if (dataType.equalsIgnoreCase("NUMBER")) {
                    insertQuery.setBigDecimal(name, (BigDecimal) val);
                } else if (dataType.equalsIgnoreCase("CLOB")) {
                    insertQuery.setText(name, (String) val);
                } else if (dataType.equalsIgnoreCase("DATE")) {
                    insertQuery.setDate(name, (Date) val);
                } else if (dataType.equalsIgnoreCase("VARCHAR2")) {
                    insertQuery.setString(name, (String) val);
                } else if (dataType.equalsIgnoreCase("BLOB")) {
                    throw new RuntimeException("data type [BLOB] is not support");
                }
            } else {
                if (val != null) {
                    insertQuery.setParameter(name, val);
                }
            }
        }
        if (count > 0) {
            IexportDataRelationalMappingUtils.setPkParam(insertQuery, pkName, pkValue);
        }
        insertQuery.executeUpdate();

        // 从表
        Map<String, List<IexportDataRecord>> relationalDataMap = record.getRelationalDataMap();
        for (Entry<String, List<IexportDataRecord>> entry : relationalDataMap.entrySet()) {
            List<IexportDataRecord> relationalRecords = entry.getValue();
            for (IexportDataRecord relationalRecord : relationalRecords) {
                // 确保外键关系的值存在
                if (isFkValueExists(relationalRecord)) {
                    restoreData(relationalRecord, iexportType);
                }
            }
        }

        IexportDataRecordSetCacheUtils.put(cacheKey, true);
    }

    /**
     * @param record
     * @return
     */
    private boolean isFkValueExists(IexportDataRecord dataRecord) {
        List<TableRelation> tableRelations = TableMetaData.getTableRelations(dataRecord.getTableName());
        for (TableRelation tableRelation : tableRelations) {
            // 从缓存获取是否已经还原
            String primaryTable = tableRelation.getPrimaryTableName();
            String primaryTablePkName = TableMetaData.getPk(primaryTable);
            String primaryPkValue = ObjectUtils.toString(dataRecord.getData()
                    .get(tableRelation.getRelationColumnName()));
            String cacheKey = "IexportDataRecordSet" + primaryTablePkName + primaryPkValue + tableRelation.getGroup();
            if (Boolean.TRUE.equals(IexportDataRecordSetCacheUtils.getValue(cacheKey))) {
                return false;
            }

            String queryString = IexportDataRelationalMappingUtils.getPkCountSql(primaryTable, primaryTablePkName,
                    primaryPkValue);
            Query query = this.dao.getSession().createSQLQuery(queryString);
            IexportDataRelationalMappingUtils.setPkParam(query, primaryTablePkName, primaryPkValue);
            Long count = ((BigDecimal) query.uniqueResult()).longValue();
            if (Long.valueOf(0l).equals(count)) {
                return false;
            }
        }

        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecordService#deleteInsertedRecords(List)
     */
    @Override
    @Transactional
    public void deleteInsertedRecords(List<InsertedRecord> insertedRecords) {
        for (InsertedRecord insertedRecord : insertedRecords) {
            String tableName = insertedRecord.getTableName();
            String pkName = insertedRecord.getPrimaryKeyName();
            String pkValue = insertedRecord.getPrimaryKeyValue();
            String queryString = IexportDataRelationalMappingUtils.getPkCountSql(tableName, pkName, pkValue);
            Query query = this.dao.getSession().createSQLQuery(queryString);
            IexportDataRelationalMappingUtils.setPkParam(query, pkName, pkValue);
            long count = ((BigDecimal) query.uniqueResult()).longValue();
            if (count > 1) {
                throw new RuntimeException("要删除的数据存在多条重复的主键记录！");
            }
            String deleteString = IexportDataRelationalMappingUtils.getDeleteSql(tableName, pkName, pkValue);
            query = this.dao.getSession().createSQLQuery(deleteString);
            IexportDataRelationalMappingUtils.setPkParam(query, pkName, pkValue);
            query.executeUpdate();
        }
    }
}
