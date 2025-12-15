/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
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
 * 2019年10月12日.1	zhulh		2019年10月12日		Create
 * </pre>
 * @date 2019年10月12日
 */
public interface DmsDataPermissionQueryService {

    /**
     * 数据权限查询
     *
     * @param dpDefId
     * @param queryContext
     * @return
     */
    List<QueryItem> query(String dpDefId, QueryContext queryContext);

    /**
     * 数据权限查询总数
     *
     * @param dpDefId
     * @param queryContext
     * @return
     */
    long count(String dpDefId, QueryContext queryContext);

    /**
     * 数据权限最新版本数据查询
     *
     * @param dpDefId
     * @param queryContext
     * @return
     */
    List<QueryItem> queryLatestVersion(String dpDefId, QueryContext queryContext);

    /**
     * 数据权限最新版本数据查询总数
     *
     * @param dpDefId
     * @param queryContext
     * @return
     */
    long countLatestVersion(String dpDefId, QueryContext queryContext);

    Select2QueryData queryAll4SelectOptions(Select2QueryInfo queryInfo);
}
