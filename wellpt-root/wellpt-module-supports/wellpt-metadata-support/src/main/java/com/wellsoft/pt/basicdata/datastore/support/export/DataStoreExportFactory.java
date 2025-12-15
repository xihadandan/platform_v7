package com.wellsoft.pt.basicdata.datastore.support.export;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.util.reflection.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class DataStoreExportFactory {
    @Autowired
    private List<DataStoreExport> dataStoreExport;

    private Map<String, DataStoreExport> dataStoreExports;

    public DataStoreExport get(String type) {
        return getDataStoreExports().get(type);
    }

    public Select2QueryData getSelectData(Select2QueryInfo queryInfo) {
        return new Select2QueryData(dataStoreExport, "type", "name");
    }

    public List<DataStoreExport> getAllDataStoreExport() {
        return this.dataStoreExport;
    }

    private Map<String, DataStoreExport> getDataStoreExports() {
        if (dataStoreExports == null) {
            dataStoreExports = ConvertUtils.convertElementToMap(dataStoreExport, "type");
        }
        return dataStoreExports;
    }
}
