/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;

import java.io.Serializable;
import java.util.List;

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
public class IexportDataMetaData implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7745701425414931350L;

    private String group;
    private String tableName;
    private List<ColumnMetaData> columnMetaDatas;

    // 模块内表关系
    private List<TableRelation> tableRelations = Lists.newArrayList();

    // 模块间依赖关系
    private List<DataRelation> dataRelations = Lists.newArrayList();

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group 要设置的group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName 要设置的tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columnMetaDatas
     */
    public List<ColumnMetaData> getColumnMetaDatas() {
        return columnMetaDatas;
    }

    /**
     * @param columnMetaDatas 要设置的columnMetaDatas
     */
    public void setColumnMetaDatas(List<ColumnMetaData> columnMetaDatas) {
        this.columnMetaDatas = columnMetaDatas;
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
