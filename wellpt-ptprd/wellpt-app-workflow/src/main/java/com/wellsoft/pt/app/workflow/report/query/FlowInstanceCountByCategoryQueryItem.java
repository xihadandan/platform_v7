/*
 * @(#)6/27/25 V1.0
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
 * 6/27/25.1	    zhulh		6/27/25		    Create
 * </pre>
 * @date 6/27/25
 */
public class FlowInstanceCountByCategoryQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = 1206679636942393851L;
    
    private String categoryName;
    private String categoryUuid;
    private Long count;
    private Boolean completed;

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

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
