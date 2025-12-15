/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;

import java.io.Serializable;
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
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public class IexportMetaData implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7745701425414931350L;

    //	private String group;

    // 表元数据<表名，表元数据>
    private Map<String, IexportTableMetaData> tableMetaDataMap = Maps.newHashMap();

    // 不可变更的列<表名，表字段>
    //	private Map<String, List<String>> immutableColumnMap = Maps.newHashMap();

    // 模块内表关系
    private List<TableRelation> tableRelations = Lists.newArrayList();

    // 模块间依赖关系
    private List<DataRelation> dataRelations = Lists.newArrayList();

    // 数据可复制性检测器，为空默认可复制
    private transient IexportDataRecordReplicableChecker recordReplicableChecker;

    // 主键值生成方式
    private transient IdentifierGenerator primaryKeyValueGenerator = new UUIDGenerator();

    // 列值处理器<table, <column, process>>
    private transient Map<String, Map<String, ColumnValueProcessor>> columnValueProcessorMap = Maps.newHashMap();

    //	/**
    //	 * @param group
    //	 */
    //	public IexportMetaData(String group) {
    //		super();
    //		this.group = group;
    //	}
    //
    //	/**
    //	 * @return the group
    //	 */
    //	public String getGroup() {
    //		return group;
    //	}

    //	/**
    //	 * @param group 要设置的group
    //	 */
    //	public void setGroup(String group) {
    //		this.group = group;
    //	}

    /**
     * @return the tableMetaDataMap
     */
    public Map<String, IexportTableMetaData> getTableMetaDataMap() {
        return tableMetaDataMap;
    }

    /**
     * @param tableMetaDataMap 要设置的tableMetaDataMap
     */
    public void setTableMetaDataMap(Map<String, IexportTableMetaData> tableMetaDataMap) {
        this.tableMetaDataMap = tableMetaDataMap;
    }

    //	/**
    //	 * @return the immutableColumnMap
    //	 */
    //	public Map<String, List<String>> getImmutableColumnMap() {
    //		return immutableColumnMap;
    //	}
    //
    //	/**
    //	 * @param immutableColumnMap 要设置的immutableColumnMap
    //	 */
    //	public void setImmutableColumnMap(Map<String, List<String>> immutableColumnMap) {
    //		this.immutableColumnMap = immutableColumnMap;
    //	}

    /**
     * @return the recordReplicableChecker
     */
    public IexportDataRecordReplicableChecker getRecordReplicableChecker() {
        return recordReplicableChecker;
    }

    /**
     * @param recordReplicableChecker 要设置的recordReplicableChecker
     */
    public void setRecordReplicableChecker(IexportDataRecordReplicableChecker recordReplicableChecker) {
        this.recordReplicableChecker = recordReplicableChecker;
    }

    /**
     * @return the tableRelations
     */
    public List<TableRelation> getTableRelations() {
        return tableRelations;
    }

    /**
     * @param tableRelations 要设置的tableRelations
     */
    public void setTableRelations(List<TableRelation> tableRelations) {
        this.tableRelations = tableRelations;
    }

    /**
     * @return the dataRelations
     */
    public List<DataRelation> getDataRelations() {
        return dataRelations;
    }

    /**
     * @param dataRelations 要设置的dataRelations
     */
    public void setDataRelations(List<DataRelation> dataRelations) {
        this.dataRelations = dataRelations;
    }

    /**
     * @param tableName
     * @return
     */
    public IexportTableMetaData getTableMetaData(String tableName) {
        if (!tableMetaDataMap.containsKey(tableName)) {
            tableMetaDataMap.put(tableName, IexportDataRelationalMappingUtils.getMetaData(tableName));
        }
        return tableMetaDataMap.get(tableName);
    }

    /**
     * @return the primaryKeyValueGenerator
     */
    public IdentifierGenerator getPrimaryKeyValueGenerator() {
        return primaryKeyValueGenerator;
    }

    /**
     * @param primaryKeyValueGenerator 要设置的primaryKeyValueGenerator
     */
    public void setPrimaryKeyValueGenerator(IdentifierGenerator primaryKeyValueGenerator) {
        this.primaryKeyValueGenerator = primaryKeyValueGenerator;
    }

    /**
     * 注册列值处理器
     *
     * @param tableName
     * @param columnName
     * @param columnValueProcessor
     */
    public void registerColumnValueProcessor(String tableName, String columnName,
                                             ColumnValueProcessor columnValueProcessor) {
        if (!columnValueProcessorMap.containsKey(tableName)) {
            Map<String, ColumnValueProcessor> processorMap = Maps.newLinkedHashMap();
            columnValueProcessorMap.put(tableName, processorMap);
        }
        columnValueProcessorMap.get(tableName).put(columnName, columnValueProcessor);
    }

    /**
     * 获取列值处理器
     *
     * @param tableName
     * @param columnName
     * @param columnValueProcessor
     * @return
     */
    public Map<String, ColumnValueProcessor> getColumnValueProcessor(String tableName) {
        return columnValueProcessorMap.get(tableName);
    }

    /**
     * 获取列值处理器
     *
     * @param tableName
     * @param columnName
     * @param columnValueProcessor
     * @return
     */
    public Map<String, Map<String, ColumnValueProcessor>> getColumnValueProcessor() {
        return columnValueProcessorMap;
    }

    /**
     * 判断是否有列值处理器
     *
     * @return
     */
    public boolean hasColumnValueProcessor() {
        return !columnValueProcessorMap.isEmpty();
    }

    public static class ColumnMetaData implements Serializable {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -9045393092045541956L;
        private String name;
        private String dataType;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the dataType
         */
        public String getDataType() {
            return dataType;
        }

        /**
         * @param dataType 要设置的dataType
         */
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ColumnMetaData other = (ColumnMetaData) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }

}
