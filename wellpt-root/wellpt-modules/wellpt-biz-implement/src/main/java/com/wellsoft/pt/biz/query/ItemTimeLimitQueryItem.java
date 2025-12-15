/*
 * @(#)10/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/20/22.1	zhulh		10/20/22		Create
 * </pre>
 * @date 10/20/22
 */
public class ItemTimeLimitQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = -7522028775110141115L;

    private Integer timeLimit;

    /**
     * @return the timeLimit
     */
    public Integer getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
}
