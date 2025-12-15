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
import org.springframework.stereotype.Component;

import java.util.Date;
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
public class OneWorkFlowCopyToMeDataStore extends OneWorkFlowDataStore {

    @Override
    public String getQueryName() {
        return "标准工作流程_抄送给我(已过时，不再维护)";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata c = super.initCriteriaMetadata(context);
        c.add("copyToTime", "t4.copy_to_time", "抄送时间", Date.class);
        return c;
    }

    @Override
    public Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> params = super.getQueryParams(context);
        params.put("isCopyTo", "1");
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
        masks.add(AclPermission.FLAG_READ.getMask());
        masks.add(AclPermission.UNREAD.getMask());
        return masks;
    }

}
