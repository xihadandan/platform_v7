package com.wellsoft.pt.basicdata.viewcomponent.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;

public interface CalendarComponentService {

    /**
     * 获取实现代理接口的实例
     *
     * @param query
     * @return
     */
    public Select2QueryData loadCalendarComponent(Select2QueryInfo query);

    /**
     * 获取列字段
     *
     * @param dataProviderClz
     * @return
     */
    public Select2QueryData loadColumnsSelectData(String dataProviderId);

}
