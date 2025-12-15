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
 * Description:  工作流已办(包含办结)
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年9月1日.1	zhongzh		2020年9月1日		Create
 * </pre>
 * @date 2020年9月1日
 */
@Component
public class OneWorkFlowDoneIncludeOverDataStore extends OneWorkFlowDoneDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_我的已办(包含办结)(已过时，不再维护)";
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
        namedParams.put("includeOver", userId);// 包含办结
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(context.getNativeDao().getSessionFactory(),
                "flowInstQuery", namedParams);
        params.put("flowInstSql", sql);
        return params;
    }

    @Override
    public int getOrder() {
        return 40;
    }

    /**
     *
     */
    @Override
    protected List<Integer> getPermissionMasks() {
        List<Integer> masks = Lists.newArrayList();
        masks.add(AclPermission.DONE.getMask());
        return masks;
    }

}
