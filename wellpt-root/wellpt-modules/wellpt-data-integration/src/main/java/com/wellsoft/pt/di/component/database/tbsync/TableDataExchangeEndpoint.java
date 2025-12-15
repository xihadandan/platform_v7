package com.wellsoft.pt.di.component.database.tbsync;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.enums.DIParameterDomType;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class TableDataExchangeEndpoint extends
        AbstractEndpoint<TableDataExchangeDIComponent, TableDataExchangeProducer, TableDataExchangeConsumer> {
    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "表名")
    private String tableName;
    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "每次获取数据量")
    private int limit = 1;

    @Override
    public String endpointPrefix() {
        return "table-data-change";
    }

    @Override
    public String endpointName() {
        return "数据表变更记录";
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
