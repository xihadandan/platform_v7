/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.acl.support.AclPermission;
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
public class OneWorkFlowOverDataStore extends OneWorkFlowDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_我的办结(已过时，不再维护)";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        return super.initCriteriaMetadata(context);
    }

    @Override
    public Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> params = super.getQueryParams(context);
        // 流程实例有结束时间，代表办结状态
        StringBuilder whereSql = new StringBuilder(context.getWhereSqlString());
        whereSql.append(" and (t2.is_active = 0 and t2.end_time is not null)");
        params.put("whereSql", whereSql);
        params.put("reqUserId", SpringSecurityUtils.getCurrentUserId());
        return params;
    }

    @Override
    public int getOrder() {
        return 40;
    }

    // 办结是属于已办状态的一种特殊情况
    @Override
    protected List<Integer> getPermissionMasks() {
        List<Integer> masks = Lists.newArrayList();
        masks.add(AclPermission.DONE.getMask());
        return masks;
    }

}
