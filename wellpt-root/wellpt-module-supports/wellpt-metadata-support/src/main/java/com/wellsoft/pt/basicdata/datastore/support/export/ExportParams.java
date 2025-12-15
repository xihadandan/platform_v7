package com.wellsoft.pt.basicdata.datastore.support.export;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportParams {
    private String fileName;
    private String exportTemplateId;//导出模板文件id
    private List<ExportColumn> columns = new ArrayList<ExportColumn>();
    private DataStoreData data = new DataStoreData();
    private DataStoreParams params = new DataStoreParams();
    private Map<String, Object> searchSnapshot = Maps.newHashMap();//查询条件快照
    private Map<String, Object> extras = Maps.newHashMap();//其他数据参数

    public ExportParams() {

    }

    public ExportParams(String fileName, DataStoreData data, List<ExportColumn> columns,
                        DataStoreParams params) {
        this.fileName = fileName;
        this.columns = columns;
        this.data = data;
        this.params = params;
    }

    public DataStoreData getData() {
        return data;
    }

    public void setData(DataStoreData data) {
        this.data = data;
    }

    public List<ExportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ExportColumn> columns) {
        this.columns = columns;
    }

    public DataStoreParams getParams() {
        return params;
    }

    public void setParams(DataStoreParams params) {
        this.params = params;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExportTemplateId() {
        return exportTemplateId;
    }

    public void setExportTemplateId(String exportTemplateId) {
        this.exportTemplateId = exportTemplateId;
    }

    public Map<String, Object> getSearchSnapshot() {
        return searchSnapshot;
    }

    public void setSearchSnapshot(Map<String, Object> searchSnapshot) {
        this.searchSnapshot = searchSnapshot;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }
}
