package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月14日   chenq	 Create
 * </pre>
 */
public class IExportTable implements Serializable {

    private String table;

    private List<String> dataLookupColumn = Lists.newArrayList("uuid"); // 数据定位列：用于判断后续导入的sql数据是 update 还是 insert

    private String exportSql;

    public IExportTable() {
    }

    public IExportTable(String exportSql) {
        this.exportSql = exportSql;
    }

    public IExportTable(String table, String exportSql) {
        this.table = table;
        this.exportSql = exportSql;
    }

    public IExportTable(String table, List<String> dataLookupColumn, String exportSql) {
        this.table = table;
        this.dataLookupColumn = dataLookupColumn;
        this.exportSql = exportSql;
    }

    public IExportTable(List<String> dataLookupColumn, String exportSql) {
        this.dataLookupColumn = dataLookupColumn;
        this.exportSql = exportSql;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getDataLookupColumn() {
        return dataLookupColumn;
    }

    public void setDataLookupColumn(List<String> dataLookupColumn) {
        this.dataLookupColumn = dataLookupColumn;
    }

    public String getExportSql() {
        return exportSql;
    }

    public void setExportSql(String exportSql) {
        this.exportSql = exportSql;
    }
}
