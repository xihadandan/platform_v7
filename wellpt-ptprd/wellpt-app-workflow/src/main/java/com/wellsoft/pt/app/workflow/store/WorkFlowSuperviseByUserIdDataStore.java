/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流程_督办(运行时)__指定用户
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/4/2.1	    zenghw		2022/4/2		    Create
 * </pre>
 * @date 2022/4/2
 */
@Component
public class WorkFlowSuperviseByUserIdDataStore extends WorkFlowSuperviseDataStore {

    private String handoverUserId;

    /**
     * (non-Javadoc)
     */
    @Override
    public String getQueryName() {
        return "工作流程_督办(运行时)__指定用户";
    }

    @Override
    protected List<String> getSids() {
        List<String> sids = new ArrayList<String>();
        sids.add(this.handoverUserId);
        return sids;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {

        this.handoverUserId = (String) context.getQueryParams().get("handoverUserId");
        return super.query(context);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 90;
    }

}
