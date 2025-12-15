package com.wellsoft.pt.di.component.datastore;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutProducer;
import com.wellsoft.pt.di.enums.EdpParameterType;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/15    chenq		2019/7/15		Create
 * </pre>
 */
public class DataStoreEndpoint extends
        AbstractEndpoint<DataStoreDIComponent, WithoutProducer, DataStoreConsumer> {

    @EndpointParameter(name = "数据仓库", type = EdpParameterType.CONSUMER)
    private String dataStoreId;//数据仓库ID
    @EndpointParameter(name = "唯一值字段", type = EdpParameterType.CONSUMER)
    private String uuidColumn;
    @EndpointParameter(name = "数据时间字段", type = EdpParameterType.CONSUMER)
    private String timeColumn;
    @EndpointParameter(name = "批次数据上限", type = EdpParameterType.CONSUMER)
    private Integer limit = 1000;


    public String getDataStoreId() {
        return dataStoreId;
    }

    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    public String getUuidColumn() {
        return uuidColumn;
    }

    public void setUuidColumn(String uuidColumn) {
        this.uuidColumn = uuidColumn;
    }

    public String getTimeColumn() {
        return timeColumn;
    }

    public void setTimeColumn(String timeColumn) {
        this.timeColumn = timeColumn;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String endpointPrefix() {
        return "datastore";
    }

    @Override
    public String endpointName() {
        return "数据交换-数据仓库端点";
    }


}
