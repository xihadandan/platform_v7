/*
 * @(#)2019年8月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.service;

import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryInfo;
import org.hibernate.engine.spi.TypedValue;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月28日.1	zhulh		2019年8月28日		Create
 * </pre>
 * @date 2019年8月28日
 */
public interface UserTableFormDataService {

    /**
     * @param sql
     * @param values
     */
    int executeUpdate(String sql, Map<String, TypedValue> values);

    /**
     * @param sql
     * @param values
     * @param maxResults
     * @return
     */
    List<Map<String, Object>> query(String sql, Map<String, Object> values, int maxResults);

    /**
     * 表单数据查询
     *
     * @param queryInfo
     * @return
     */
    FormDataQueryData query(UserTableFormDataQueryInfo queryInfo);

    /**
     * 查询总数
     *
     * @param queryInfo
     * @return
     */
    long count(UserTableFormDataQueryInfo queryInfo);

}
