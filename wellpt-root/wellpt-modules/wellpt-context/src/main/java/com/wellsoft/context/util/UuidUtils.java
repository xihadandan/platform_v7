package com.wellsoft.context.util;

import com.wellsoft.context.util.groovy.GroovyUseable;

@GroovyUseable
public class UuidUtils {
    /**
     * 创建uuid
     *
     * @return uuid
     */
    public static String createUuid() {
        return SnowFlake.getId() + "";
    }
}
