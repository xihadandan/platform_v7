/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
public class OneWorkFlowDraftDataStore extends OneWorkFlowDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_我的草稿(已过时，不再维护)";
    }

    // 初始化流程相关的字段信息
    public CriteriaMetadata initCriteriaMetadataOfFlow(QueryContext context) {
        // 草稿没有环节实例，所有不需要环节实例字段
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        this.addFlowDefinitionCriteriaMetadata(criteriaMetadata);
        this.addFlowInstanceCriteriaMetadata(criteriaMetadata);
        return criteriaMetadata;
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        return super.initCriteriaMetadata(context);
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = this.getQueryParams(queryContext);
        // 表单的创建者是自己，代表我的申请
        params.put("reqUserId", SpringSecurityUtils.getCurrentUserId());
        return queryContext.getNativeDao().namedQuery("listDraftTaskQueryOfOneFlow", params, QueryItem.class, queryContext.getPagingInfo());
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
