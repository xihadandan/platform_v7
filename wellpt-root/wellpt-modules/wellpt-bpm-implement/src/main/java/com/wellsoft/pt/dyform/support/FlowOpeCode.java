/*
 * @(#)2017年6月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support;

import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年6月13日.1	zhongzh		2017年6月13日		Create
 * </pre>
 * @date 2017年6月13日
 */
public class FlowOpeCode {

    private static final Map<String, String> mapope = new HashMap<String, String>();

    static {
        for (WorkFlowPrivilege value : WorkFlowPrivilege.values()) {
            String eName = value.toString();
            mapope.put(value.getCode(), eName.toLowerCase());
        }
    }

    private FlowOpeCode() {
    }

    public static String mapope(String code) {
        String ope = mapope.get(code);
        return ope == null ? "primary" : ope;
    }

    public static Map<String, String> getMapping() {
        return Collections.unmodifiableMap(mapope);
    }
}
