/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 工作流待办
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-06.1	zyguo		2017-01-06		Create
 * </pre>
 * @date 2017-01-06
 */
@Component
public class OneWorkFlowAllRequestDataStore extends OneWorkFlowDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_全部申请(已过时，不再维护)";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        return super.initCriteriaMetadata(context);
    }

    @Override
    public Map<String, Object> getQueryParams(QueryContext context) {
        return super.getQueryParams(context);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 40;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.store.OneWorkFlowDataStore#getPermissionMasks()
     */
    @Override
    protected List<Integer> getPermissionMasks() {
        return null;
    }

}
