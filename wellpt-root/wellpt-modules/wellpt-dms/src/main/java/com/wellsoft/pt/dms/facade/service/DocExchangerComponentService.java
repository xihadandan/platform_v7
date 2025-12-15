/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;

/**
 * 文档交换器组件服务
 */
public interface DocExchangerComponentService extends Select2QueryApi, BaseService {


    Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo);

    /**
     * 数据类型
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataTypeSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取工作流的数据下拉选择项
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getWorkFlowSelectData(Select2QueryInfo queryInfo);

    Select2QueryData getContactBookUnitSelectData(Select2QueryInfo queryInfo);

    /**
     * 动态表单
     *
     * @param queryInfo
     * @return
     */
    //Select2QueryData getDataTypeOfDyFormSelectData(Select2QueryInfo queryInfo);


}
