/*
 * @(#)2016年5月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

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
 * 2016年5月18日.1	zhulh		2016年5月18日		Create
 * </pre>
 * @date 2016年5月18日
 */
public class AppType {
    // 应用类型0、产品、1、系统；2、模块；3、应用；4、功能
    public static final Integer PRODUCT = 0;

    public static final Integer SYSTEM = 1;

    public static final Integer MODULE = 2;

    public static final Integer APPLICATION = 3;

    public static final Integer FUNCTION = 4;

    private static Map<Integer, String> typeMap = new HashMap<Integer, String>();

    static {
        typeMap.put(PRODUCT, "产品");
        typeMap.put(SYSTEM, "系统");
        typeMap.put(MODULE, "模块");
        typeMap.put(APPLICATION, "应用");
        typeMap.put(FUNCTION, "功能");
    }

    public static String getName(Integer appType) {
        return typeMap.get(appType);
    }
}
