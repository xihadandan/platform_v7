package com.wellsoft.pt.jpa.support;

import org.apache.commons.io.IOUtils;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
public class NativeAliasToEntityMapResultTransformer extends AliasedTupleSubsetResultTransformer {
    public static final NativeAliasToEntityMapResultTransformer INSTANCE = new NativeAliasToEntityMapResultTransformer();

    /**
     * Disallow instantiation of AliasToEntityMapResultTransformer.
     */
    private NativeAliasToEntityMapResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map result = new HashMap(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                Object o = tuple[i];
                if (tuple[i] instanceof java.sql.Clob) {
                    try {
                        Clob sc = (java.sql.Clob) tuple[i];
                        o = IOUtils.toString(sc.getCharacterStream());
                    } catch (Exception e) {
                        continue;
                    }
                }

                if (tuple[i] instanceof java.sql.Blob) {
                    continue;
                }
                result.put(alias, o);
            }
        }
        return result;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    /**
     * Serialization hook for ensuring singleton uniqueing.
     *
     * @return The singleton instance : {@link #INSTANCE}
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
