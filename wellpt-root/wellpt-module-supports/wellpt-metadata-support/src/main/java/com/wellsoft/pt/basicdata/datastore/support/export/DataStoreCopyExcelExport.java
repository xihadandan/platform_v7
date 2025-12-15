package com.wellsoft.pt.basicdata.datastore.support.export;

//@Component
@Deprecated
public class DataStoreCopyExcelExport extends DataStoreExcelExport {

    @Override
    public String getType() {
        return "CopyExcel";
    }

    @Override
    public String getName() {
        return "CopyExcel";
    }

}
