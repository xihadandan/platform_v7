/*
 * @(#)2016年3月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import org.springframework.core.Ordered;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月14日.1	zhongzh		2016年3月14日		Create
 * </pre>
 * @date 2016年3月14日
 */
public interface WellOrdered extends Ordered {

    /**
     * Useful constant for the normal precedence value.
     *
     * @see java.lang.Integer#0
     */
    int NORMAL_PRECEDENCE = 0;

    /**
     * Useful constant for the highest precedence value.
     *
     * @see instead java.lang.Integer#MIN_VALUE不方便运算
     */
    int HIGHEST_PRECEDENCE = -4096;

    /**
     * Useful constant for the lowest precedence value.
     *
     * @see instead java.lang.Integer#MAX_VALUE不方便运算
     */
    int LOWEST_PRECEDENCE = 4096;

}
