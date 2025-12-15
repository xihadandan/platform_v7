package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description: 工作流程_监控(管理)__指定用户
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/4/3.1	    zenghw		2022/4/3		    Create
 * </pre>
 * @date 2022/4/3
 */
@Component
public class WorkFlowMonitorManagementByUserIdDataStore extends WorkFlowMonitorManagementDataStoreQuery {

    private String handoverUserId;

    /**
     * (non-Javadoc)
     */
    @Override
    public String getQueryName() {
        return "工作流程_监控(管理)__指定用户";
    }

    @Override
    protected List<String> getSids() {
        List<String> sids = new ArrayList<String>();
        // 用户相关的组织ID
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        String userId = this.handoverUserId;
//        Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
        Set<String> orgIds = workflowOrgService.getUserRelatedIds(userId);
        sids.addAll(orgIds);
        sids.add(this.handoverUserId);
        return sids;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {

        this.handoverUserId = (String) context.getQueryParams().get("handoverUserId");
        return super.query(context);
    }
}
