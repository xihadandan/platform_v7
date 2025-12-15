package com.wellsoft.pt.basicdata.iexport.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月07日   chenq	 Create
 * </pre>
 */
public class ExportTable implements Serializable {

    private String table;

    private String where; // 条件筛选

    private HashMap<String, Object> params = Maps.newHashMap(); // 参数

    private List<ExportTable> subExportTables = Lists.newArrayList();

    public ExportTable() {
    }

    public ExportTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<ExportTable> getSubExportTables() {
        return subExportTables;
    }

    public void setSubExportTables(List<ExportTable> subExportTables) {
        this.subExportTables = subExportTables;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    private DescriptionMetadata descriptionMetadata;

    public DescriptionMetadata getDescriptionMetadata() {
        return descriptionMetadata;
    }

    public void setDescriptionMetadata(DescriptionMetadata descriptionMetadata) {
        this.descriptionMetadata = descriptionMetadata;
    }

    public static class DescriptionMetadata implements Serializable {
        private String typeName;
        private String titleColumn;
        private String idColumn;


        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTitleColumn() {
            return titleColumn;
        }

        public void setTitleColumn(String titleColumn) {
            this.titleColumn = titleColumn;
        }

        public String getIdColumn() {
            return idColumn;
        }

        public void setIdColumn(String idColumn) {
            this.idColumn = idColumn;
        }
    }
}
