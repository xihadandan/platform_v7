package com.wellsoft.pt.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月17日.1	wangrf		2020年2月17日		Create
 * </pre>
 * @date 2020年2月17日
 */
@Component
public class WfTaskDelegationDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Override
    public String getQueryName() {
        return "流程管理_工作委托查询(已过时，不再维护)";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();

        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("recVer", "rec_ver", "版本号", Integer.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        metadata.add("attach", "attach", "附件属性", String.class);

        metadata.add("taskInstUuid", "task_inst_uuid", "流程任务实例Uuid", String.class);
        metadata.add("flowInstUuid", "flow_inst_uuid", "流程定义Uuid", String.class);
        metadata.add("delegationSettingsUuid", "delegation_settings_uuid", "委托设置UUID", String.class);
        metadata.add("consignorName", "consignor_name", "委托人名称", String.class);
        metadata.add("consignor", "consignor", "委托人ID", String.class);
        metadata.add("trusteeName", "trustee_name", "受托人名称", String.class);
        metadata.add("trustee", "trustee", "受托人ID", String.class);
        metadata.add("taskIdentityUuid", "task_identity_uuid", "受托人待办信息", String.class);
        metadata.add("dueToTakeBackWork", "due_to_take_back_work", "回收委托期间未处理工作", String.class);
        metadata.add("deactiveToTakeBackWork", "deactive_to_take_back_work", "手动终止时自动收回", String.class);
        metadata.add("completionState", "completion_state", "完成状态 ", String.class);
        metadata.add("fromTime", "from_time", "开始时间", String.class);
        metadata.add("toTime", "to_time", "结束时间", String.class);
        metadata.add("flowTitle", "flow_title", "委托流程", String.class);
        metadata.add("taskName", "task_name", "流程任务实例名称", String.class);

        return metadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("taskDelegataionQueryStore",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        return queryParams;
    }
}
