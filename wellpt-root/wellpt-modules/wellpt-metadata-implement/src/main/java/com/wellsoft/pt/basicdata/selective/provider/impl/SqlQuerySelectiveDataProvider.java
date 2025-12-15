/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.provider.impl;

import com.wellsoft.pt.basicdata.selective.provider.AbstractSelectiveDataProvider;
import com.wellsoft.pt.basicdata.selective.service.SqlQuerySelectiveDataService;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
@Component
public class SqlQuerySelectiveDataProvider extends AbstractSelectiveDataProvider {

    @Autowired
    private SqlQuerySelectiveDataService sqlQuerySelectiveDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.selective.provider.SelectiveDataProvider#get(java.lang.String)
     */
    @Override
    public SelectiveData get(String configKey) {
        return sqlQuerySelectiveDataService.get(configKey);
    }

}
