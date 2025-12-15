/*
 * @(#)2020年1月20日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.ext.enumcls.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月20日.1	zhulh		2020年1月20日		Create
 * </pre>
 * @date 2020年1月20日
 */
public interface EnumClassSelect2QueryService {

    /**
     * 加载所有枚举类列表
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData queryAll4SelectOptions(Select2QueryInfo queryInfo);

    /**
     * 根据枚举类名加载对应的枚举类属性列表
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData loadEnumClassFieldsSelectData(Select2QueryInfo queryInfo);

}
