/*
 * @(#)Jan 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.workflow.store.WorkFlowLeftJoinConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 流程查询
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 6, 2017.1	zhulh		Jan 6, 2017		Create
 * </pre>
 * @date Jan 6, 2017
 */
public class WorkFlowDataStoreQuery extends AbstractDataStoreQueryInterface {

    public final static String IS_TOPPING_FILED = "isTopping";

    private Object locked = new Object();
    private String projectionColumnClause;
    // private Map<String, String> projectionColumnClauseMap = Maps.newHashMap();

    @Autowired
    protected WfFlowSettingService flowSettingService;

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.TODO);
        return permissions;
    }

    /**
     * @param context
     */
    protected String getCriteriaMetadataProjection(QueryContext context) {
//        DataStoreConfiguration dataStoreConfiguration = (DataStoreConfiguration) context.get("dataStoreConfiguration");
//        String projectionKey = dataStoreConfiguration == null ? "default" : dataStoreConfiguration.getId();
//        String projectionColumnClause = projectionColumnClauseMap.get(projectionKey);

        if (StringUtils.isNotBlank(projectionColumnClause)) {
            return leftJoinProjection(projectionColumnClause, context);
        }
        synchronized (locked) {
            if (StringUtils.isNotBlank(projectionColumnClause)) {
                return leftJoinProjection(projectionColumnClause, context);
            }
            List<String> projection = Lists.newArrayList();
            CriteriaMetadata criteriaMetadata = context.getCriteriaMetadata();
            Iterator<String> columnIndexIt = Arrays.asList(criteriaMetadata.getColumnIndexs()).iterator();
            while (columnIndexIt.hasNext()) {
                String columnIndex = columnIndexIt.next();
                String columnName = criteriaMetadata.getMapColumnIndex(columnIndex);
                // 排除左关联数据模型的列定义
                if (StringUtils.startsWith(columnName, "t")) {
                    projection.add(columnName + " as " + columnIndex);
                }
            }
            projectionColumnClause = StringUtils.join(projection, ", ");
            // projectionColumnClauseMap.put(projectionKey, projectionColumnClause);
        }
        return leftJoinProjection(projectionColumnClause, context);
    }

    private String leftJoinProjection(String defaultProjection, QueryContext context) {
        WorkFlowDataStoreInterfaceParam interfaceParam = (WorkFlowDataStoreInterfaceParam) context.get("interfaceParam");
        if (interfaceParam == null || interfaceParam.getLeftJoinConfig() == null
                || !interfaceParam.getLeftJoinConfig().getEnabled()) {
            return defaultProjection;
        }
        return defaultProjection + ", " + getJoinDmTableProjectionClause(interfaceParam);
    }

    /**
     * @param context
     * @return
     */
    protected TaskQuery preQuery(QueryContext context) {
        List<String> sids = getSids();
        Map<String, Object> queryParams = context.getQueryParams();
        // 添加接口参数查询
        addInterfaceParamQuery(queryParams, context);
        TaskQuery taskQuery = FlowEngine.getInstance().createQuery(TaskQuery.class);
        if (StringUtils.isNotBlank((String) queryParams.get("joinTableClause"))) {
            taskQuery.projection(getCriteriaMetadataProjection(context));
        }
        taskQuery.permission(sids, getPermissions());
        taskQuery.setProperties(queryParams);
        taskQuery.where(context.getWhereSqlString(), context.getQueryParams());
        return taskQuery;
    }

    /**
     * @param queryParams
     * @param context
     */
    protected void addInterfaceParamQuery(Map<String, Object> queryParams, QueryContext context) {
        WorkFlowDataStoreInterfaceParam interfaceParam = context.interfaceParam(WorkFlowDataStoreInterfaceParam.class);
        String JoinTableClause = this.getJoinDmTableClause(interfaceParam);
        queryParams.put("joinTableClause", JoinTableClause);
        if (StringUtils.isNotBlank(JoinTableClause)) {
            queryParams.putAll(this.getJoinDmTableSqlParamaters(interfaceParam));
        }
        if (interfaceParam != null && CollectionUtils.isNotEmpty(interfaceParam.getFlowDefIds())) {
            addFlowDefIdQueryParams(queryParams, interfaceParam);
        }
        context.put("interfaceParam", interfaceParam);
    }

    /**
     * @param queryParams
     * @param interfaceParam
     */
    private void addFlowDefIdQueryParams(Map<String, Object> queryParams, WorkFlowDataStoreInterfaceParam interfaceParam) {
        Object flowDefId = queryParams.get("flowDefId");
        List<String> flowDefIds = Lists.newArrayList(interfaceParam.getFlowDefIds());
        if (flowDefId instanceof String && StringUtils.isNotBlank((String) flowDefId)) {
            if (flowDefIds.contains(flowDefId)) {
                queryParams.put("flowDefId", flowDefId);
            } else {
                queryParams.put("flowDefId", "-1");
            }
        } else if (flowDefId instanceof Collection && CollectionUtils.isNotEmpty((Collection) flowDefId)) {
            queryParams.put("flowDefId", CollectionUtils.intersection(flowDefIds, (Collection) flowDefId));
        } else {
            queryParams.put("flowDefId", flowDefIds);
        }
    }

    /**
     * @return
     */
    protected List<String> getSids() {
        return PermissionGranularityUtils.getCurrentUserSids();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        TaskQuery taskQuery = preQuery(context);

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isEnabledContinuousWorkView()) {
            boolean isExistIsTopping = false;
            for (Order order : context.getOrders()) {
                if (IS_TOPPING_FILED.equals(order.getColumnIndex())) {
                    isExistIsTopping = true;
                    break;
                }
            }
            if (!isExistIsTopping) {
                List<Order> orders = Lists.newArrayList(Order.asc(IS_TOPPING_FILED));
                orders.addAll(context.getOrders());
                context.getOrders().clear();
                context.getOrders().addAll(orders);
            }
        }

        taskQuery.order(context.getOrderString());
        taskQuery.setFirstResult(context.getPagingInfo().getFirst());
        taskQuery.setMaxResults(context.getPagingInfo().getPageSize());
        return taskQuery.list(QueryItem.class);
    }

    /**
     * @return
     */
    private String getJoinDmTableProjectionClause(WorkFlowDataStoreInterfaceParam param) {
        String projectionClause = StringUtils.EMPTY;
        if (param == null) {
            return projectionClause;
        }

        WorkFlowLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return projectionClause;
        }

        List<DataStoreColumn> dataStoreColumns = leftJoinConfig.getSelectionColumns();
        projectionClause = dataStoreColumns.stream().map(column -> column.getColumnName() + " as " + column.getColumnIndex())
                .collect(Collectors.joining(", "));
        return projectionClause;
    }

    /**
     * @return
     */
    private String getJoinDmTableClause(WorkFlowDataStoreInterfaceParam param) {
        String projectionClause = StringUtils.EMPTY;
        if (param == null) {
            return projectionClause;
        }

        WorkFlowLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return projectionClause;
        }

        StringBuilder sb = new StringBuilder();
        String tableName = leftJoinConfig.getTableName();
        // 数据模型为视图时，使用视图查询的sql
        String tableSql = leftJoinConfig.getTableSql();
        if (StringUtils.isNotBlank(tableSql)) {
            tableName = "(" + tableSql + ")";
        }
        sb.append(" left join ").append(tableName).append(" ").append(leftJoinConfig.getTableAlias())
                .append(" ").append(leftJoinConfig.getOnConditionSql());

        return sb.toString();
    }

    /**
     * @param param
     * @return
     */
    private Map<String, Object> getJoinDmTableSqlParamaters(WorkFlowDataStoreInterfaceParam param) {
        if (param == null) {
            return Collections.emptyMap();
        }

        WorkFlowLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return Collections.emptyMap();
        }

        Map<String, Object> sqlParameters = leftJoinConfig.getSqlParameter();
        return sqlParameters != null ? sqlParameters : Collections.emptyMap();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext context) {
        PagingInfo pagingInfo = context.getPagingInfo();
        if (pagingInfo != null && pagingInfo.getTotalCount() > 0) {
            return pagingInfo.getTotalCount();
        }
        return preQuery(context).count();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t1.create_time", "环节创建时间", Date.class);
        criteriaMetadata.add("modifyTime", "t1.modify_time", "环节修改时间", Date.class);
        criteriaMetadata.add("flowInstUuid", "t2.uuid", "流程实例UUID", String.class);
        criteriaMetadata.add("title", "t2.title", "流程标题", DataType.S);
        criteriaMetadata.add("flowStartUserId", "t2.start_user_id", "流程发起人ID", String.class);
        criteriaMetadata.add("flowStartUserName", "t2.start_user_name", "流程发起人名称", String.class);
        criteriaMetadata.add("flowStartDepartmentId", "t2.start_department_id", "流程发起人部门ID", String.class);
        criteriaMetadata.add("flowStartDepartmentName", "t2.start_department_name", "流程发起人部门名称", String.class);
        criteriaMetadata.add("flowStartTime", "t2.start_time", "流程发起时间", Date.class);
        criteriaMetadata.add("flowEndTime", "t2.end_time", "流程结束时间", Date.class);
        criteriaMetadata.add("isActive", "t2.is_active", "流程是否激活", DataType.B);
        criteriaMetadata.add("taskId", "t1.id", "当前环节ID", String.class);
        criteriaMetadata.add("taskName", "t1.name", "当前环节名称", DataType.S);
        criteriaMetadata.add("serialNo", "t1.serial_no", "流水号", DataType.S);
        criteriaMetadata.add("todoUserId", "t1.todo_user_id", "当前办理人ID", DataType.S);
        criteriaMetadata.add("todoUserName", "t1.todo_user_name", "当前办理人名称", DataType.S);
        criteriaMetadata.add("preOperatorId", "t1.assignee", "前办理人ID", String.class);
        criteriaMetadata.add("preOperatorName", "t1.assignee_name", "前办理人名称", String.class);
        criteriaMetadata.add("startTime", "t1.start_time", "环节开始时间", Date.class);
        criteriaMetadata.add("endTime", "t1.end_time", "环节结束时间", Date.class);
        criteriaMetadata.add("timingState", "t1.timing_state", "计时状态", DataType.I);
        criteriaMetadata.add("alarmTime", "t1.alarm_time", "预警时间", Date.class);
        criteriaMetadata.add("dueTime", "t1.due_time", "逾期时间", Date.class);
        criteriaMetadata.add("alarmState", "t1.alarm_state", "预警状态", DataType.I);
        criteriaMetadata.add("overDueState", "t1.over_due_state", "逾期状态", DataType.I);
        criteriaMetadata.add("formUuid", "t1.form_uuid", "表单定义UUID", String.class);
        criteriaMetadata.add("dataUuid", "t1.data_uuid", "表单数据UUID", String.class);
        criteriaMetadata.add("flowName", "t3.name", "流程名称", String.class);
        criteriaMetadata.add("flowDefId", "t3.id", "流程定义ID", String.class);
        criteriaMetadata.add(IS_TOPPING_FILED, "t5.is_topping", "是否置顶", DataType.B);
        criteriaMetadata.add("reservedText1", "t2.reserved_text1", "预留文本字段1", String.class);
        criteriaMetadata.add("reservedText2", "t2.reserved_text2", "预留文本字段2", String.class);
        criteriaMetadata.add("reservedText3", "t2.reserved_text3", "预留文本字段3", String.class);
        criteriaMetadata.add("reservedText4", "t2.reserved_text4", "预留文本字段4", String.class);
        criteriaMetadata.add("reservedText5", "t2.reserved_text5", "预留文本字段5", String.class);
        criteriaMetadata.add("reservedText6", "t2.reserved_text6", "预留文本字段6", String.class);
        criteriaMetadata.add("reservedText7", "t2.reserved_text7", "预留文本字段7", String.class);
        criteriaMetadata.add("reservedText8", "t2.reserved_text8", "预留文本字段8", String.class);
        criteriaMetadata.add("reservedText9", "t2.reserved_text9", "预留文本字段9", String.class);
        criteriaMetadata.add("reservedText10", "t2.reserved_text10", "预留文本字段10", String.class);
        criteriaMetadata.add("reservedText11", "t2.reserved_text11", "预留文本字段11", String.class);
        criteriaMetadata.add("reservedText12", "t2.reserved_text12", "预留文本字段12", String.class);
        criteriaMetadata.add("reservedNumber1", "t2.reserved_number1", "预留数字字段1", DataType.D);
        criteriaMetadata.add("reservedNumber2", "t2.reserved_number2", "预留数字字段2", DataType.D);
        criteriaMetadata.add("reservedNumber3", "t2.reserved_number3", "预留数字字段3", DataType.D);
        criteriaMetadata.add("reservedDate1", "t2.reserved_date1", "预留日期字段1", DataType.T);
        criteriaMetadata.add("reservedDate2", "t2.reserved_date2", "预留日期字段2", DataType.T);

        // 添加左连接的查询表
        WorkFlowDataStoreInterfaceParam interfaceParam = context.interfaceParam(WorkFlowDataStoreInterfaceParam.class);
        this.addJoinDmCriteriaMetadata(criteriaMetadata, interfaceParam, context);
        return criteriaMetadata;
    }

    /**
     * 添加左连接的查询表
     *
     * @param metedata
     * @param context
     */
    protected void addJoinDmCriteriaMetadata(CriteriaMetadata metedata, WorkFlowDataStoreInterfaceParam param, QueryContext context) {
        if (param == null) {
            return;
        }

        WorkFlowLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return;
        }

        List<DataStoreColumn> dataStoreColumns = leftJoinConfig.getSelectionColumns();
        for (DataStoreColumn dataStoreColumn : dataStoreColumns) {
            String columnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, dataStoreColumn.getColumnIndex());
            metedata.add(columnIndex, dataStoreColumn.getColumnName(),
                    dataStoreColumn.getTitle(), dataStoreColumn.getDataType(), dataStoreColumn.getColumnType());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_数据源";
    }


    /**
     * 接口参数定义类
     *
     * @return
     */
    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return WorkFlowDataStoreInterfaceParam.class;
    }

}
