/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.workflow.work.bean.FlowLoadingRules;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 工作流待办
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 26, 2016.1	zhulh		Dec 26, 2016		Create
 * </pre>
 * @date Dec 26, 2016
 */
@Component
public class WorkFlowTodoDataStore extends WorkFlowDataStoreQuery {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_待办";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("taskDelegationUuid", "t4.uuid", "委托数据UUID", String.class);
        criteriaMetadata.add("taskIdentityUuid", "t4.task_identity_uuid", "待办标识UUID", String.class);
        criteriaMetadata.add("taskArrivalTime", "ti_.task_arrival_time", "待办到达时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * 低优先级查询列
     *
     * @return
     */
    private String getLowPriorityProjection() {
        return "case when t5.low_priority_ is null then 0 else t5.low_priority_ end as low_priority_";
    }

    /**
     * 默认的左关联查询SQL
     *
     * @return
     */
    private StringBuilder getDefaultJoinClause(FlowLoadingRules loadingRules) {
        // 左关联
        StringBuilder sb = new StringBuilder();
        sb.append("left join wf_task_delegation t4 ");
        sb.append("on t1.uuid = t4.task_inst_uuid and t4.trustee = :userId and t4.completion_state in(0, 1)");
        sb.append(" left join (");
        if (loadingRules != null) {
            if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
                sb.append("select current_timestamp() as task_arrival_time, null as task_inst_uuid from dual");
            } else {
                sb.append("select sysdate as task_arrival_time, null as task_inst_uuid from dual");
            }
        } else {
            if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
                sb.append("	select max(t.create_time) as task_arrival_time, t.task_inst_uuid as task_inst_uuid");
                sb.append("		from wf_task_identity t");
                sb.append("		where t.user_id in(:userSids) and t.suspension_state <> 2");
                sb.append("		group by t.task_inst_uuid");
            } else {
                sb.append("	select * from (");
                sb.append("	    select nvl(t.create_time, t_.start_time) as task_arrival_time, t.task_inst_uuid as task_inst_uuid, ");
                sb.append("	        row_number() over(partition by t.task_inst_uuid order by t.create_time desc) as row_num");
                sb.append("	    from wf_task_identity t left join wf_task_instance t_ on t.task_inst_uuid = t_.uuid where t.suspension_state <> 2 and t.user_id in(:userSids)");
                sb.append(" ) where row_num = 1");
            }
        }
        sb.append("	) ti_");
        sb.append("	on ti_.task_inst_uuid = t1.uuid");
        return sb;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#preQuery(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    protected TaskQuery preQuery(QueryContext context) {
        FlowLoadingRules loadingRules = getFlowLoadingRules(context);
        // 按默认方式查询并增加稍后处理5次以上的数据排序规则
        if (loadingRules == null || FlowLoadingRules.MODE_LIST_VIEW == loadingRules.getMode()) {
            return preDefaultListViewQuery(context, null);
        }
        // 按流程加载规则查询
        return preQueryWithFlowLoadingRules(context, loadingRules);
    }

    /**
     * @param context
     * @return
     */
    private TaskQuery preDefaultListViewQuery(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = super.preQuery(context);
        WorkFlowSettings settings = flowSettingService.getWorkFlowSettings();
        if (!settings.isEnabledContinuousWorkView()) {
            // 默认查询列
            String projection = getCriteriaMetadataProjection(context, loadingRules);
            // 查询列
            taskQuery.projection(projection);
            // 默认的左关联查询语句
            StringBuilder joinClause = getDefaultJoinClause(loadingRules);
            taskQuery.join(joinClause.toString());
            return taskQuery;
        }

        // 默认查询列
        String projection = getCriteriaMetadataProjection(context, loadingRules);
        // 低优先级查询列
        String lowPriorityProjection = getLowPriorityProjection();
        // 查询列
        taskQuery.projection(projection + ", " + lowPriorityProjection);
        // 默认的左关联查询语句
        StringBuilder sb = getJoinClause(context, loadingRules);
        taskQuery.join(sb.toString());
        // 视图列表模式，左关联wf_task_instance_topping，计算稍后处理5次以上的数据排序规则
        String taskInstanceToppingSelectClause = "select t4.task_inst_uuid, t4.is_topping, case when t4.low_priority >= 5 then t4.low_priority - 4 else 0 end as low_priority_ from wf_task_instance_topping t4";
        context.getQueryParams().put("taskInstanceToppingSelectClause", taskInstanceToppingSelectClause);
        // 更新参数
        taskQuery.setProperties(context.getQueryParams());
        return taskQuery;
    }

    /**
     * @param context
     * @param loadingRules
     * @return
     */
    private String getCriteriaMetadataProjection(QueryContext context, FlowLoadingRules loadingRules) {
        String projection = super.getCriteriaMetadataProjection(context);
        // 今日到达
        if (loadingRules != null && loadingRules.isArriveToday()) {
            projection += ", case when to_char(t1.start_time, 'yyyy-MM-dd') = '" + DateUtils.formatDate(Calendar.getInstance().getTime()) + "' then 0 else 1 end as arrive_today_order";
        }
        return projection;
    }

    /**
     * @param context
     * @param loadingRules
     * @return
     */
    private StringBuilder getJoinClause(QueryContext context, FlowLoadingRules loadingRules) {
        StringBuilder joinClause = getDefaultJoinClause(loadingRules);
        if (loadingRules == null || FlowLoadingRules.MODE_LIST_VIEW == loadingRules.getMode()) {
            return joinClause;
        }
        // 优先加载的排序字段
        List<String> priorityOrderColumns = Lists.newArrayListWithExpectedSize(0);
        // 优先加载的流程分类UUID列表
        List<String> flowCategoryUuids = loadingRules.getFlowCategoryUuids();
        if (CollectionUtils.isNotEmpty(flowCategoryUuids)) {
            joinClause.append(" left join ( ");
            joinClause.append(" select	c.uuid, ");
            joinClause.append(" case	c.uuid ");
            for (int index = 0; index < flowCategoryUuids.size(); index++) {
                joinClause.append(" when '" + flowCategoryUuids.get(index) + "' then " + 0);
            }
            joinClause.append(" else " + flowCategoryUuids.size());
            joinClause.append(" end as wf_category_order ");
            joinClause.append(" from	wf_def_category c ");
            joinClause.append(" ) wf_category_ on wf_category_.uuid = t3.category ");
            priorityOrderColumns.add("wf_category_order");
        }
        // 优先加载的流程定义ID列表
        List<String> flowDefIds = loadingRules.getFlowDefIds();
        if (CollectionUtils.isNotEmpty(flowDefIds)) {
            joinClause.append(" left join ( ");
            joinClause.append(" select	d.uuid, ");
            joinClause.append(" case	d.id ");
            for (int index = 0; index < flowDefIds.size(); index++) {
                joinClause.append(" when '" + flowDefIds.get(index) + "' then " + 0);
            }
            joinClause.append(" else " + flowDefIds.size());
            joinClause.append(" end as wf_definition_order ");
            joinClause.append(" from	wf_flow_definition d ");
            joinClause.append(" ) wf_definition_ on wf_definition_.uuid = t3.uuid ");
            priorityOrderColumns.add("wf_definition_order");
        }
        context.getQueryParams().put("priorityOrderColumns", priorityOrderColumns);
        return joinClause;
    }

    /**
     * @param context
     * @param loadingRules
     * @return
     */
    private TaskQuery preQueryWithFlowLoadingRules(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = null;
        // 加载规则处理
        int rule = loadingRules.getRule();
        switch (rule) {
            case FlowLoadingRules.RULE_LIST_VIEW:
                // 1、按列表顺序加载
                taskQuery = preQueryByListView(context, loadingRules);
                break;
            case FlowLoadingRules.RULE_LIMIT_TIME:
                // 2、按办理时限加载
                taskQuery = preQueryByLimitTime(context, loadingRules);
                break;
            case FlowLoadingRules.RULE_INTELLIGENT:
                // 3、智能加载
                taskQuery = preQueryByIntelligent(context, loadingRules);
                break;
            default:
                taskQuery = preQueryByListView(context, loadingRules);
                break;
        }
        return taskQuery;
    }

    /**
     * @param context
     * @param loadingRules
     * @return
     */
    private TaskQuery preQueryByListView(QueryContext context, FlowLoadingRules loadingRules) {
        return preDefaultListViewQuery(context, loadingRules);
    }

    /**
     * @param context
     * @param loadingRules
     * @return
     */
    private TaskQuery preQueryByLimitTime(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = super.preQuery(context);
        // 默认查询列
        String projection = getCriteriaMetadataProjection(context, loadingRules);
        // 加入到期时间排序列
        // projection += ", case when t1.due_time is null then to_date('1900-01-01', 'yyyy-MM-dd') else t1.due_time end as due_time_order";
        // 查询列
        taskQuery.projection(projection);
        // 默认的左关联查询语句
        StringBuilder sb = getJoinClause(context, loadingRules);
        taskQuery.join(sb.toString());
        return taskQuery;
    }

    /**
     * @param context
     */
    private TaskQuery preQueryByIntelligent(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = super.preQuery(context);
        // 默认查询列
        String projection = getCriteriaMetadataProjection(context, loadingRules);
        // 低优先级查询列
        String lowPriorityProjection = getLowPriorityProjection();
        // 查询列
        taskQuery.projection(projection + ", " + lowPriorityProjection);
        // 默认的左关联查询语句
        StringBuilder sb = getJoinClause(context, loadingRules);
        taskQuery.join(sb.toString());
        return taskQuery;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        List<QueryItem> queryItems = null;
        FlowLoadingRules loadingRules = getFlowLoadingRules(context);
        // 按默认方式查询并增加稍后处理5次以上的数据排序规则
        if (loadingRules == null || FlowLoadingRules.MODE_LIST_VIEW == loadingRules.getMode()) {
            queryItems = doDefaultQuery(context);
        } else {
            // 按流程加载规则查询
            queryItems = doQueryWithFlowLoadingRules(context, loadingRules);
        }
        Set<String> flowStartDepartmentIds = queryItems.stream().map(item -> item.getString("flowStartDepartmentId")).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(flowStartDepartmentIds) && StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            Map<String, String> departmentNames = workflowOrgService.getNamesByIds(Lists.newArrayList(flowStartDepartmentIds));
            queryItems.forEach(item -> item.put("flowStartDepartmentName", departmentNames.get(item.getString("flowStartDepartmentId"))));
        }
        return queryItems;
    }

    /**
     * 默认视图列表查询方式
     *
     * @param context
     * @return
     */
    private List<QueryItem> doDefaultQuery(QueryContext context) {
        TaskQuery taskQuery = preQuery(context);

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isEnabledContinuousWorkView()) {
            // 优先加载的排序
            List<Order> newOrders = addPriorityOrders(context, getFlowLoadingRules(context));

            boolean isExistIsTopping = false;
            for (Order order : context.getOrders()) {
                if (IS_TOPPING_FILED.equals(order.getColumnIndex())) {
                    isExistIsTopping = true;
                    break;
                }
            }
            // 稍后处理5次以上的数据排序
            newOrders.add(Order.asc("low_priority_"));
            // 原有功能置顶排序
            if (!isExistIsTopping) {
                newOrders.add(Order.asc(IS_TOPPING_FILED));
            }
            // 原有查询条件的排序
            newOrders.addAll(context.getOrders());
            // 重置排序
            context.getOrders().clear();
            context.getOrders().addAll(newOrders);
        }

        taskQuery.order(context.getOrderString());
        taskQuery.setFirstResult(context.getPagingInfo().getFirst());
        taskQuery.setMaxResults(context.getPagingInfo().getPageSize());
        return taskQuery.list(QueryItem.class);
    }

    /**
     * 按流程加载规则查询
     *
     * @param context
     * @param loadingRules
     * @return
     */
    private List<QueryItem> doQueryWithFlowLoadingRules(QueryContext context, FlowLoadingRules loadingRules) {
        List<QueryItem> queryItems = null;
        // 加载规则处理
        int rule = loadingRules.getRule();
        switch (rule) {
            case FlowLoadingRules.RULE_LIST_VIEW:
                // 1、按列表顺序加载
                queryItems = doQueryByListView(context, loadingRules);
                break;
            case FlowLoadingRules.RULE_LIMIT_TIME:
                // 2、按办理时限加载
                queryItems = doQueryByLimitTime(context, loadingRules);
                break;
            case FlowLoadingRules.RULE_INTELLIGENT:
                // 3、智能加载
                queryItems = doQueryByIntelligent(context, loadingRules);
                break;
            default:
                queryItems = doQueryByListView(context, loadingRules);
                break;
        }
        return queryItems;
    }

    /**
     * 按列表顺序加载
     *
     * @param context
     * @return
     */
    private List<QueryItem> doQueryByListView(QueryContext context, FlowLoadingRules loadingRules) {
        // 使用默认视图列表查询，在该查询方法中处理优先加载的排序
        return doDefaultQuery(context);
    }

    /**
     * 按办理时限加载
     *
     * @param context
     * @return
     */
    private List<QueryItem> doQueryByLimitTime(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = preQuery(context);

        List<Order> newOrders = Lists.newArrayList();
        // 优先加载的排序
        newOrders.addAll(addPriorityOrders(context, loadingRules));
        // 按环节到期时间降序(t1.due_time desc)
        newOrders.add(Order.desc("t1.timing_state"));
        // 原有查询条件的排序
        newOrders.addAll(context.getOrders());
        // 重置排序
        context.getOrders().clear();
        context.getOrders().addAll(newOrders);

        taskQuery.order(context.getOrderString());
        taskQuery.setFirstResult(context.getPagingInfo().getFirst());
        taskQuery.setMaxResults(context.getPagingInfo().getPageSize());
        return taskQuery.list(QueryItem.class);
    }

    /**
     * 智能加载
     *
     * @param context
     * @return
     */
    private List<QueryItem> doQueryByIntelligent(QueryContext context, FlowLoadingRules loadingRules) {
        TaskQuery taskQuery = preQuery(context);

        List<Order> newOrders = Lists.newArrayList();
        // 优先加载的排序
        newOrders.addAll(addPriorityOrders(context, loadingRules));
        // 专注模式，左关联查询wf_task_instance_topping，直接根据low_priority_字段排序
        newOrders.add(Order.asc("low_priority_"));
        // 原有查询条件的排序
        newOrders.addAll(context.getOrders());
        // 重置排序
        context.getOrders().clear();
        context.getOrders().addAll(newOrders);

        taskQuery.order(context.getOrderString());
        taskQuery.setFirstResult(context.getPagingInfo().getFirst());
        taskQuery.setMaxResults(context.getPagingInfo().getPageSize());
        return taskQuery.list(QueryItem.class);
    }

    /**
     * 获取加载规则
     *
     * @param context
     * @return
     */
    private FlowLoadingRules getFlowLoadingRules(QueryContext context) {
        return (FlowLoadingRules) context.getQueryParams().get("flowLoadingRules");
    }

    /**
     * 添加优先加载的排序
     *
     * @param context
     * @param loadingRules
     * @return
     */
    private List<Order> addPriorityOrders(QueryContext context, FlowLoadingRules loadingRules) {
        List<Order> newOrders = Lists.newArrayList();
        if (loadingRules == null || FlowLoadingRules.MODE_LIST_VIEW == loadingRules.getMode()) {
            return newOrders;
        }
        // 优先加载的排序
        List<String> priorityOrderColumns = getPriorityOrderColumns(context);
        if (CollectionUtils.isNotEmpty(priorityOrderColumns)) {
            for (String priorityOrderColumn : priorityOrderColumns) {
                newOrders.add(Order.asc(priorityOrderColumn));
            }
        }
        // 今日到达
        if (loadingRules.isArriveToday()) {
            newOrders.add(Order.asc("arrive_today_order"));
        }
        return newOrders;
    }

    /**
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getPriorityOrderColumns(QueryContext context) {
        return (List<String>) context.getQueryParams().get("priorityOrderColumns");
    }

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.TODO);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
