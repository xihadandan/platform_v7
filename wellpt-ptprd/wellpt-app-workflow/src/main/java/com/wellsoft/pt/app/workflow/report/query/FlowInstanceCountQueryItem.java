/*
 * @(#)6/30/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/30/25.1	    zhulh		6/30/25		    Create
 * </pre>
 * @date 6/30/25
 */
public class FlowInstanceCountQueryItem implements BaseQueryItem {
    private Long count;
    private Boolean completed;

    /**
     * @return the count
     */
    public Long getCount() {
        return count;
    }

    /**
     * @param count 要设置的count
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * @return the completed
     */
    public Boolean getCompleted() {
        return completed;
    }

    /**
     * @param completed 要设置的completed
     */
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
