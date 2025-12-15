/*
 * @(#)2013-2-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-9.1	Administrator		2014-4-9		Create
 * </pre>
 * @date 2014-4-9
 */
public class ExchangeDataResultTransformer extends AliasedTupleSubsetResultTransformer {

    public static final ExchangeDataResultTransformer INSTANCE = new ExchangeDataResultTransformer();
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3272319594489983122L;

    private ExchangeDataResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                result.put(alias.toLowerCase(), tuple[i]);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.transform.TupleSubsetResultTransformer#
     * isTransformedValueATupleElement(java.lang.String[], int)
     */
    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
