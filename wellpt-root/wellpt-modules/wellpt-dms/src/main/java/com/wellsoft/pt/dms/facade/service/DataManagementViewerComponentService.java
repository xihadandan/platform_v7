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
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
public interface DataManagementViewerComponentService extends Select2QueryApi, BaseService {

    /**
     * 操作处理拦截器
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getInterceptorSelectData(Select2QueryInfo queryInfo);

    /**
     * 数据类型
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataTypeSelectData(Select2QueryInfo queryInfo);

    /**
     * 动态表单
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataTypeOfDyFormSelectData(Select2QueryInfo queryInfo);

    /**
     * 动态表单选中
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData getDataTypeOfDyFormSelectDataByIds(Select2QueryInfo select2QueryInfo);

    /**
     * 根据表单定义UUID，获取表单列定义
     *
     * @param formUuid
     * @return
     */
    Select2QueryData getColumnsOfDyFormSelectData(Select2QueryInfo select2QueryInfo);

    /**
     * 数据展示
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo);

    /**
     * 数据预览模板
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataPreviewTemplateSelectData(Select2QueryInfo queryInfo);

}
