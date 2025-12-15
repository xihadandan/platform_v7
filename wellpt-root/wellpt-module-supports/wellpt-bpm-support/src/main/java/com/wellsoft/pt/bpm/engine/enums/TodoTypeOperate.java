/*
 * @(#)7/10/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/10/24.1	    zhulh		7/10/24		    Create
 * </pre>
 * @date 7/10/24
 */
public enum TodoTypeOperate {
    DEFAULT, NONE, SUBMIT;

    /**
     * @param operateRight
     * @return
     */
    public static TodoTypeOperate from(String operateRight) {
        if (StringUtils.isBlank(operateRight)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("default", operateRight)) {
            return DEFAULT;
        } else if (StringUtils.equalsIgnoreCase("none", operateRight)) {
            return NONE;
        } else if (StringUtils.equalsIgnoreCase("submit", operateRight)) {
            return SUBMIT;
        }
        return null;
    }

}
