/*
 * @(#)2019年8月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import org.apache.commons.io.IOUtils;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.util.HashMap;
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
public class NativeAliasToLowerCaseEntityMapResultTransformer extends AliasedTupleSubsetResultTransformer {

    public static final NativeAliasToLowerCaseEntityMapResultTransformer INSTANCE = new NativeAliasToLowerCaseEntityMapResultTransformer();
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8087064106321860355L;

    /**
     *
     */
    private NativeAliasToLowerCaseEntityMapResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map result = new HashMap(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                Object val = tuple[i];
                if (tuple[i] instanceof Clob) {
                    try {
                        SerialClob clob = new SerialClob((Clob) tuple[i]);
                        val = IOUtils.toString(clob.getCharacterStream());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                result.put(alias.toLowerCase(), val);
            }
        }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.transform.TupleSubsetResultTransformer#isTransformedValueATupleElement(java.lang.String[], int)
     */
    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    /**
     * @return
     */
    private Object readResolve() {
        return INSTANCE;
    }

}
