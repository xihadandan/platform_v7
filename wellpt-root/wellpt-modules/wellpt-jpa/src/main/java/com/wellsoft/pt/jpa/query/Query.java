/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.query;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 查询接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
public interface Query<T extends Query<?, ?>, U extends Object> {

    int getFirstResult();

    T setFirstResult(int firstResult);

    int getMaxResults();

    T setMaxResults(int maxResults);

    void setProperties(Map<String, Object> values);

    long count();

    U uniqueResult();

    List<U> list();

    <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass);

}
