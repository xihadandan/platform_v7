/*
 * @(#)Jan 10, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.jpa.criteria.QueryContext;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 10, 2018.1	zhulh		Jan 10, 2018		Create
 * </pre>
 * @date Jan 10, 2018
 */
public interface DmsFileQueryService extends BaseService {

    /**
     * @param context
     * @return
     */
    List<QueryItem> query(QueryContext context);

    /**
     * @param context
     * @return
     */
    long count(QueryContext context);

}
