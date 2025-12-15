package com.wellsoft.pt.repository.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FieldConstants {
    public static final int FIELD_SYS_TYPE = 0;
    public static final int FIELD_USER_TYPE = 1;
    public static final int FIELD_DYNAMIC_TYPE = 2;
    private static final Map<String, String> typeMap = new HashMap<String, String>() {
        /** 如何描述serialVersionUID */
        private static final long serialVersionUID = -816860976271698772L;

        {
            put("java.lang.String", "string");
            put("int", "int");
            put("long", "long");
            put("java.lang.Integer", "int");
            put("java.lang.Long", "int");
            put("boolean", "boolean");
            put("java.lang.Boolean", "boolean");
        }
    };

    public static Map<String, String> getTypeMap() {
        return Collections.unmodifiableMap(typeMap);
    }
}
