package com.wellpt.code.builder.support;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/2    chenq		2018/8/2		Create
 * </pre>
 */
public class MySqlDialect extends AbstractDialect {

    @Override
    public Map<String, Class<?>> types() {
        Map<String, Class<?>> types = new HashMap<String, Class<?>>();
        types.put("VARCHAR", String.class);
        types.put("CHAR", String.class);
        types.put("TINYBLOB", String.class);
        types.put("TEXT", String.class);
        types.put("CLOB", String.class);
        types.put("BLOB", Blob.class);
        types.put("MEDIUMTEXT", String.class);
        types.put("LONGBLOB", Blob.class);
        types.put("TINYTEXT", String.class);
        types.put("TIMESTAMP", Date.class);
        types.put("TINYINT", Byte.class);
        types.put("SMALLINT", Short.class);
        types.put("INGEGER", Integer.class);
        types.put("INT", Integer.class);
        types.put("FLOAT", Float.class);
        types.put("BIGINT", Long.class);
        types.put("DOUBLE", Double.class);
        types.put("DECIMAL", BigDecimal.class);
        types.put("BIT", Boolean.class);
        types.put("DATE", Date.class);
        types.put("TIME", Date.class);
        return Collections.unmodifiableMap(types);
    }
}
