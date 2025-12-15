/*
 * @(#)21 Dec 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mobilelist.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.mobilelist.support.MobileListTemplate;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 21 Dec 2016.1	Xiem		21 Dec 2016		Create
 * </pre>
 * @date 21 Dec 2016
 */
public interface MobileListService {

    /**
     * 如何描述该方法
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getMobileListTemplate(Select2QueryInfo queryInfo);

    /**
     * 如何描述该方法
     *
     * @param beanName
     * @return
     */
    MobileListTemplate getMobileListTemplateByBeanName(String beanName);

}
