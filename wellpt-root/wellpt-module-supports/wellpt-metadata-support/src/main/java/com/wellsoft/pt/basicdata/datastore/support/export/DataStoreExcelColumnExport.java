package com.wellsoft.pt.basicdata.datastore.support.export;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DataStoreExcelColumnExport extends DataStoreExcelExport {

    @Override
    public String getType() {
        return "excel_column";
    }

    @Override
    public String getName() {
        return "按当前列表的字段导出Excel(97-2003)";
    }

}
