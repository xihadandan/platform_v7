package com.wellsoft.pt.basicdata.datastore.support.export;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Description: 导出Excel 2007
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月26日.1	zhulh		2020年2月26日		Create
 * </pre>
 * @date 2020年2月26日
 */
@Component
@Order(3)
public class DataStoreExcelOfOoxmlColumnExport extends DataStoreExcelOfOoxmlExport {


    @Override
    public String getType() {
        return "excel_ooxml_column";
    }

    @Override
    public String getName() {
        return "按当前列表的字段导出Excel(2007)";
    }

}
