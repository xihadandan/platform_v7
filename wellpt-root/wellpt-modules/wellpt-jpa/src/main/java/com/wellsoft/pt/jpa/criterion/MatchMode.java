/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月25日.1	xiem		2016年10月25日		Create
 * </pre>
 * @date 2016年10月25日
 */
public enum MatchMode {

    /**
     * Match the entire string to the pattern
     */
    EXACT {
        @Override
        public String toMatchString(String pattern) {
            return pattern;
        }
    },

    /**
     * Match the start of the string to the pattern
     */
    START {
        @Override
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    },

    /**
     * Match the end of the string to the pattern
     */
    END {
        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern;
        }
    },

    /**
     * Match the pattern anywhere in the string
     */
    ANYWHERE {
        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    };

    /**
     * Convert the pattern, by appending/prepending "%"
     *
     * @param pattern The pattern for convert according to the mode
     * @return The converted pattern
     */
    public abstract String toMatchString(String pattern);

}
