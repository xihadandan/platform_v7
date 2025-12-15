/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
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
public class OneWorkFlowDoneDataStore extends OneWorkFlowDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_我的已办(已过时，不再维护)";
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
        Map<String, Object> params = super.getQueryParams(context);
        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> namedParams = Maps.newHashMap();
        namedParams.put("userId", userId);
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(context.getNativeDao().getSessionFactory(), "flowInstQuery", namedParams);
//		StringBuilder flowInstSql = new StringBuilder();
// 		flowInstSql.append("select * from( ");
//		/* 通过分组过滤找出用户办理过的流程实例 */
//
//
//
//
//
//		flowInstSql.append("	select f1.*, row_number() over( partition by o.flow_inst_uuid, o.assignee order by o.create_time desc ) as row_num ");
//		flowInstSql.append("	from wf_task_operation o, wf_flow_instance f1 ");
//		flowInstSql.append("	where o.flow_inst_uuid = f1.uuid and f1.flow_def_uuid = :flowDefUuid  ");
//		flowInstSql.append("	and o.assignee ='" + userId + "' ");
//		/* 剔除拟稿环节, 即剔除第一环节 */
//		flowInstSql.append("	and exists ( ");
//		flowInstSql.append("		select * from(");
//		flowInstSql.append("			select t.*, row_number() over( partition by t.flow_inst_uuid order by t.create_time asc ) as row_num ");
//		flowInstSql.append("			from wf_task_instance t where t.flow_def_uuid = :flowDefUuid ");
//		flowInstSql.append("		) a where row_num > 1 and o.task_inst_uuid = a.uuid  ");
//		flowInstSql.append("	)");
//		/* 剔除已办结数据 */
//		flowInstSql.append("	and f1.end_time is null ");
//		flowInstSql.append(") where row_num = 1");

        params.put("flowInstSql", sql);
        return params;
    }

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
        List<Integer> masks = Lists.newArrayList();
        masks.add(AclPermission.DONE.getMask());
        return masks;
    }

}
