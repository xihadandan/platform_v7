/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-1.1	zhulh		2013-2-1		Create
 * </pre>
 * @date 2013-2-1
 */
public interface CommonQueryService {
    QueryData query(QueryInfo queryInfo);
}
