package com.wellsoft.pt.basicdata.iexport.suport;

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
 * 2023年11月13日   chenq	 Create
 * </pre>
 */
public class IExportTableData implements Serializable {
    private static final long serialVersionUID = 8694142499199504035L;
    // 通过 lookup 定位唯一数据

    private List<String> lookupColumn = Lists.newArrayList();

    private List<Row> rows = Lists.newArrayList();

    public static class Row implements Serializable {

        private List<Column> columns = Lists.newArrayList();

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }


    }

    public static class Column implements Serializable {
        private String code;
        private String data;
        private String javaType;
        private String dbType;

        public Column() {
        }

        public Column(String code, String data, String javaType) {
            this.code = code;
            this.data = data;
            this.javaType = javaType;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

        public String getDbType() {
            return dbType;
        }

        public void setDbType(String dbType) {
            this.dbType = dbType;
        }
    }

    public List<String> getLookupColumn() {
        return lookupColumn;
    }

    public void setLookupColumn(List<String> lookupColumn) {
        this.lookupColumn = lookupColumn;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
