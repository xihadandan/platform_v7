package com.wellsoft.pt.dm.factory.ddl;

import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
public class Table implements Serializable {


    public List<Column> columns = Lists.newArrayList();// 列
    public List<Index> indices = Lists.newArrayList(); // 索引
    private String tableName; // 表名
    private String comment;// 注解

    public Table() {
    }

    public Table(final String tableName) {
        this.tableName = tableName;
    }

    public Table(final String tableName, final String comment) {
        this.tableName = tableName;
        this.comment = comment;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumns(final List<Column> columns) {
        this.columns = columns;
    }

    public List<Index> getIndices() {
        return this.indices;
    }

    public void setIndices(final List<Index> indices) {
        this.indices = indices;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public Table addColumn(Column col) {
        this.columns.add(col);
        return this;
    }

    public Table addIndex(Index idx) {
        this.indices.add(idx);
        return this;
    }

    public static class Column implements Serializable {

        private String code; // 编码

        private String dataType;

        private String comment;// 注解

        private Boolean notNull = false;

        private Boolean primaryKey = false;

        private Object defaultValue;


        public Column() {
        }

        public Column(final String code, String dataType) {
            this.code = code;
            this.dataType = dataType;
        }


        public Column(final String code, final String dataType, final String comment, final Boolean notNull, final Boolean primaryKey, final Object defaultValue) {
            this.code = code;
            this.dataType = dataType;
            this.comment = comment;
            this.notNull = notNull;
            this.primaryKey = primaryKey;
            this.defaultValue = defaultValue;
        }


        public Column(final String code, final String dataType, final String comment, final Boolean notNull) {
            this.code = code;
            this.dataType = dataType;
            this.comment = comment;
            this.notNull = notNull;
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        public String getDataType() {
            return this.dataType;
        }

        public void setDataType(final String dataType) {
            this.dataType = dataType;
        }

        public String getComment() {
            return this.comment;
        }

        public void setComment(final String comment) {
            this.comment = comment;
        }

        public Boolean getNotNull() {
            return this.notNull;
        }

        public void setNotNull(final Boolean notNull) {
            this.notNull = notNull;
        }

        public Boolean getPrimaryKey() {
            return this.primaryKey;
        }

        public void setPrimaryKey(final Boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        public Object getDefaultValue() {
            return this.defaultValue;
        }

        public void setDefaultValue(final Object defaultValue) {
            this.defaultValue = defaultValue;
        }


    }

    public static class Index implements Serializable {
        private String id;
        private String[] columns;
        private String using = "BTREE";

        public Index() {
        }

        public Index(final String[] columns) {
            this.columns = columns;
        }

        public String getId() {
            return this.id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String[] getColumns() {
            return this.columns;
        }

        public void setColumns(final String[] columns) {
            this.columns = columns;
        }

        public String getUsing() {
            return this.using;
        }

        public void setUsing(final String using) {
            this.using = using;
        }

    }


}
