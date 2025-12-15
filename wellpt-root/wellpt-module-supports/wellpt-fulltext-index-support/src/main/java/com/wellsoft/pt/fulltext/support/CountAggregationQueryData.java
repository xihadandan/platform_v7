/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

import com.wellsoft.context.jdbc.support.QueryData;

import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
public class CountAggregationQueryData extends QueryData {
    private Map<String, Long> countMap = null;

    /**
     * @return the countMap
     */
    public Map<String, Long> getCountMap() {
        return countMap;
    }

    /**
     * @param countMap 要设置的countMap
     */
    public void setCountMap(Map<String, Long> countMap) {
        this.countMap = countMap;
    }
}
