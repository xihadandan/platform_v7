/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.component.select2.facade.service;

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
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public interface Select2DataStoreQueryService extends BaseService, Select2QueryApi {


    /**
     * 获取实体类下拉选择，可传递参数superEntityClass指定查询继承该类的所有实体类
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    Select2QueryData loadEntityData(Select2QueryInfo queryInfo) throws Exception;

}
