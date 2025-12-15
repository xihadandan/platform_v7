/*
 * @(#)2019年4月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import org.apache.commons.lang.StringUtils;

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
 * 2019年4月11日.1	zhulh		2019年4月11日		Create
 * </pre>
 * @date 2019年4月11日
 */
public class IexportBundle extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -489493950663230500L;

    // 元数据
    private Map<String, IexportMetaData> metaDataMap = Maps.newHashMap();

    // 数据
    private List<IexportData> datas = Lists.newArrayList();

    // 列映射
    private Map<IexportMappingColumn, IexportMappingColumn> mappingColumnMap = Maps.newHashMap();

    /**
     * @return the metaDataMap
     */
    public Map<String, IexportMetaData> getMetaDataMap() {
        return metaDataMap;
    }

    /**
     * @param metaDataMap 要设置的metaDataMap
     */
    public void setMetaDataMap(Map<String, IexportMetaData> metaDataMap) {
        this.metaDataMap = metaDataMap;
    }

    /**
     * @return the datas
     */
    public List<IexportData> getDatas() {
        return datas;
    }

    /**
     * @param datas 要设置的datas
     */
    public void setDatas(List<IexportData> datas) {
        this.datas = datas;
    }

    /**
     * @param newMappingColumn
     * @param oldColumn
     */
    public void putMappingColumn(IexportMappingColumn newMappingColumn, IexportMappingColumn oldMappingColumn) {
        IexportMappingColumn currentOldMappingColumn = mappingColumnMap.get(oldMappingColumn);
        if (currentOldMappingColumn != null) {
            //throw new RuntimeException("column mapping error");
            mappingColumnMap.put(newMappingColumn, currentOldMappingColumn);
        } else {
            mappingColumnMap.put(newMappingColumn, oldMappingColumn);
        }
    }

    /**
     * @param newMappingColumn
     * @return
     */
    public boolean hasMappingColumn(IexportMappingColumn newMappingColumn) {
        return mappingColumnMap.containsKey(newMappingColumn);
    }

    /**
     * @param newMappingColumn
     * @return
     */
    public IexportMappingColumn getMappingColumn(IexportMappingColumn newMappingColumn) {
        //		for (Entry<IexportMappingColumn, IexportMappingColumn> entry : mappingColumnMap.entrySet()) {
        //			System.out.println(entry.getKey().getTableName() + ": " + entry.getKey().getName() + " "
        //					+ entry.getKey().getValue() + ", " + entry.getValue().getValue());
        //		}
        return mappingColumnMap.get(newMappingColumn);
    }

    /**
     * @param tableName
     * @param dataColumn
     * @return
     */
    public IexportMappingColumn getMappingColumn(String tableName, IexportDataColumn dataColumn) {
        IexportMappingColumn newMappingColumn = new IexportMappingColumn(tableName, dataColumn.getName(),
                dataColumn.getValue());
        return getMappingColumn(newMappingColumn);
    }

    /**
     * @param tableNames
     * @return
     */
    public Map<Object, Object> getMappingColumnValues(String... tableNames) {
        return getMappingColumnValues(StringUtils.EMPTY, tableNames);
    }

    /**
     * @param columnName
     * @param tableNames
     * @return
     */
    public Map<Object, Object> getMappingColumnValues(String columnName, String... tableNames) {
        List<String> filterTables = Lists.newArrayList();
        for (String tableName : tableNames) {
            filterTables.add(tableName);
        }
        Map<Object, Object> mappingColumnValues = Maps.newHashMap();
        for (Entry<IexportMappingColumn, IexportMappingColumn> entry : mappingColumnMap.entrySet()) {
            IexportMappingColumn key = entry.getKey();
            IexportMappingColumn value = entry.getValue();
            // 匹配任意列的表
            if (StringUtils.isEmpty(columnName)) {
                if (filterTables.isEmpty() || filterTables.contains(key.getTableName())) {
                    mappingColumnValues.put(value.getValue(), key.getValue());
                }
            } else if (StringUtils.equals(key.getName(), columnName)) {
                // 匹配指定列的表
                if (filterTables.isEmpty() || filterTables.contains(key.getTableName())) {
                    mappingColumnValues.put(value.getValue(), key.getValue());
                }
            }

        }
        return mappingColumnValues;
    }

}
