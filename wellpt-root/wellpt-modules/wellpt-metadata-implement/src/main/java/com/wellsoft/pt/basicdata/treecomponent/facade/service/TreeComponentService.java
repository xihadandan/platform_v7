/*
 * @(#)27 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 27 Feb 2017.1	Xiem		27 Feb 2017		Create
 * </pre>
 * @date 27 Feb 2017
 */
public interface TreeComponentService {
    Select2QueryData loadTreeComponent(Select2QueryInfo query);

    List<TreeType> getTreeTypes(String dataProviderClz);

    boolean isAsync(String dataProviderClz);

    /**
     * 如何描述该方法
     *
     * @param dataProviderClz
     * @return
     */
    String getFilterHint(String dataProviderClz);
}
