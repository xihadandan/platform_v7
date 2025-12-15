/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criteria.*;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;

import java.util.*;
import java.util.stream.Collectors;

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
public abstract class OneWorkFlowDataStore extends AbstractDataStoreQueryInterface {
    @Autowired
    private FlowService flowService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private AclTaskService aclTaskService;

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metedata = this.initCriteriaMetadataOfFlow(context);

        OneWorkFlowInterfaceParam param = context.interfaceParam(OneWorkFlowInterfaceParam.class);
        // 添加流程表单的查询列
        this.addDyformCriteriaMetadata(metedata, param, context);

        // 添加左连接的查询表
        this.addJoinDmCriteriaMetadata(metedata, param, context);

        return metedata;
    }

    // 初始化流程相关的字段信息
    public CriteriaMetadata initCriteriaMetadataOfFlow(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        this.addFlowInstanceCriteriaMetadata(criteriaMetadata);
        this.addTaskInstanceCriteriaMetadata(criteriaMetadata);
        this.addFlowDefinitionCriteriaMetadata(criteriaMetadata);
        return criteriaMetadata;
    }

    /**
     * 添加流程表单的查询列
     *
     * @param metedata
     * @param context
     */
    protected void addDyformCriteriaMetadata(CriteriaMetadata metedata, OneWorkFlowInterfaceParam param, QueryContext context) {
        // 开始添加表单相关的字段信息
        String formTable = this.getDyfromTableName(param, null, context);
        if (StringUtils.isNotBlank(formTable)) {
            Criteria formC = context.getNativeDao().createTableCriteria(formTable);
            CriteriaMetadata formMetadata = formC.getCriteriaMetadata();
            for (int i = 0; i < formMetadata.length(); i++) {
                String columnIndex = formMetadata.getColumnIndex(i);
                columnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
                String columnName = "d1." + formMetadata.getColumnIndex(i);
                // 存在跟流程一样的冗余字段，直接跳过
                if (ArrayUtils.contains(metedata.getColumnIndexs(), columnIndex)) {
                    continue;
                }
                metedata.add(columnIndex, columnName, formMetadata.getComment(i), formMetadata.getDataType(i));
            }
        }
    }

    /**
     * 添加左连接的查询表
     *
     * @param metedata
     * @param context
     */
    protected void addJoinDmCriteriaMetadata(CriteriaMetadata metedata, OneWorkFlowInterfaceParam param, QueryContext context) {
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

    // 添加流程实例字段
    public void addFlowInstanceCriteriaMetadata(CriteriaMetadata criteriaMetadata) {
        criteriaMetadata.add("flowInstUuid", "t2.uuid", "流程实例UUID", String.class);
        criteriaMetadata.add("flowTitle", "t2.title", "流程标题", DataType.S);
        criteriaMetadata.add("flowStartUserId", "t2.start_user_id", "流程发起人ID", String.class);
        criteriaMetadata.add("flowStartTime", "t2.start_time", "流程发起时间", Date.class);
        criteriaMetadata.add("flowEndTime", "t2.end_time", "流程结束时间", Date.class);
        criteriaMetadata.add("isActive", "t2.is_active", "流程是否激活", DataType.B);

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
    }

    // 添加环节实例子弟
    public void addTaskInstanceCriteriaMetadata(CriteriaMetadata criteriaMetadata) {
        criteriaMetadata.add("taskInstUuid", "t1.uuid", "环节实例UUID", String.class);
        criteriaMetadata.add("taskId", "t1.id", "当前环节ID", String.class);
        criteriaMetadata.add("taskName", "t1.name", "当前环节名称", DataType.S);
        criteriaMetadata.add("serialNo", "t1.serial_no", "流水号", DataType.S);
        criteriaMetadata.add("todoUserId", "t1.todo_user_id", "当前办理人ID", DataType.S);
        criteriaMetadata.add("todoUserName", "t1.todo_user_name", "当前办理人名称", DataType.S);
        criteriaMetadata.add("preOperatorId", "t1.assignee", "前办理人ID", String.class);
        criteriaMetadata.add("taskStartTime", "t1.start_time", "环节开始时间", Date.class);
        criteriaMetadata.add("taskEndTime", "t1.end_time", "环节结束时间", Date.class);
        criteriaMetadata.add("taskModifyTime", "t1.modify_time", "环节最后修改时间", Date.class);
        criteriaMetadata.add("timingState", "t1.timing_state", "计时状态", DataType.I);
        criteriaMetadata.add("alarmTime", "t1.alarm_time", "预警时间", Date.class);
        criteriaMetadata.add("dueTime", "t1.due_time", "逾期时间", Date.class);
        criteriaMetadata.add("alarmState", "t1.alarm_state", "预警状态", DataType.I);
        criteriaMetadata.add("overDueState", "t1.over_due_state", "逾期状态", DataType.I);
        criteriaMetadata.add("formUuid", "t1.form_uuid", "表单定义UUID", String.class);
        criteriaMetadata.add("dataUuid", "t1.data_uuid", "表单数据UUID", String.class);
    }

    // 添加流程定义字段
    public void addFlowDefinitionCriteriaMetadata(CriteriaMetadata criteriaMetadata) {
        criteriaMetadata.add("flowName", "t3.name", "流程名称", String.class);
        criteriaMetadata.add("flowId", "t3.id", "流程ID", String.class);
        criteriaMetadata.add("flowCategory", "t3.category", "流程归属分类", String.class);
    }

    @Override
    public long count(QueryContext context) {
        if (context.getPagingInfo() == null || context.getPagingInfo().getTotalCount() <= 0) {
            if (context.getPagingInfo() != null) {
                context.getPagingInfo().setPageSize(1);
                context.getPagingInfo().setAutoCount(true);
            }
            this.query(context);
        }
        return context.getPagingInfo().getTotalCount();
    }

    // 获取流程定义
    public FlowDefinition getFlowDefinition(OneWorkFlowInterfaceParam param, QueryContext context) {
        String flowId = null;
        if (param != null) {
            flowId = param.getFlowId();
        } else {
            flowId = context.getInterfaceParam();
        }
        if (StringUtils.isBlank(flowId)) {
            return null;
        }
        return flowService.getFlowDefinitionById(flowId);
    }

    // 获取对应的表单表名
    public String getDyfromTableName(OneWorkFlowInterfaceParam param, FlowDefinition flowDefinition, QueryContext context) {
        FlowDefinition flow = flowDefinition;
        if (flow == null) {
            flow = this.getFlowDefinition(param, context);
        }
        if (flow != null) {
            DyFormFormDefinition dyForm = dyFormFacade.getFormDefinition(flow.getFormUuid());
            if (dyForm != null) {
                return dyForm.getTableName();
            }
        }
        return null;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = this.getQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery("listTaskQueryOfOneFlowWithDM", params,
                QueryItem.class, queryContext.getPagingInfo());
        return items;
    }

    /**
     * @param queryContext
     * @return
     */
    public Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        OneWorkFlowInterfaceParam param = queryContext.interfaceParam(OneWorkFlowInterfaceParam.class);
        FlowDefinition flowDefinition = this.getFlowDefinition(param, queryContext);
        String dyformTableName = this.getDyfromTableName(param, flowDefinition, queryContext);
        String dyformFields = this.getDyformFields(dyformTableName, param, flowDefinition, queryContext);
        String joinDmSelectionClause = this.getJoinDmTableProjectionClause(param);
        String joinDmTableClause = this.getJoinDmTableClause(param);
        params.put("flowDefUuid", flowDefinition.getUuid());
        params.put("formUuid", flowDefinition.getFormUuid());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("dyformTableName", dyformTableName);
        // 表单字段，需要扣除这些已有的重复字段
        params.put("dyformFields", dyformFields);
        params.put("joinProjectionClause", joinDmSelectionClause);
        params.put("joinTableClause", joinDmTableClause);
        params.put("orderBy", queryContext.getOrderString());
        List<Integer> permissionMasks = this.getPermissionMasks();
        if (CollectionUtils.isNotEmpty(permissionMasks)) {
            params.put("masks", permissionMasks);
            params.put("sidUser", SpringSecurityUtils.getCurrentUserId());
            params.put("userSids", PermissionGranularityUtils.getCurrentUserSids());
            List<Permission> permissions = permissionMasks.stream().map(mask -> {
                if (mask == null) {
                    return AclPermission.READ;
                }
                if (mask.equals(AclPermission.READ.getMask())) {
                    return AclPermission.READ;
                } else if (mask.equals(AclPermission.TODO.getMask())) {
                    return AclPermission.TODO;
                } else if (mask.equals(AclPermission.DONE.getMask())) {
                    return AclPermission.DONE;
                } else if (mask.equals(AclPermission.ATTENTION.getMask())) {
                    return AclPermission.ATTENTION;
                } else if (mask.equals(AclPermission.UNREAD.getMask())) {
                    return AclPermission.UNREAD;
                } else if (mask.equals(AclPermission.FLAG_READ.getMask())) {
                    return AclPermission.FLAG_READ;
                } else if (mask.equals(AclPermission.SUPERVISE.getMask())) {
                    return AclPermission.SUPERVISE;
                } else if (mask.equals(AclPermission.MONITOR.getMask())) {
                    return AclPermission.MONITOR;
                }
                return AclPermission.READ;
            }).collect(Collectors.toList());
            String authWhere = aclTaskService.getAuthWhere(permissions);
            params.put("authWhere", authWhere);
        }
        params.putAll(queryContext.getQueryParams());
        params.putAll(getJoinDmTableSqlParamaters(param));
        return params;
    }

    /**
     * @return
     */
    private String getJoinDmTableProjectionClause(OneWorkFlowInterfaceParam param) {
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
    private String getJoinDmTableClause(OneWorkFlowInterfaceParam param) {
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
    private Map<String, Object> getJoinDmTableSqlParamaters(OneWorkFlowInterfaceParam param) {
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


    protected abstract List<Integer> getPermissionMasks();

    /**
     * @param dyformTableName
     * @param flowDefinition
     * @param context
     * @return
     */
    private String getDyformFields(String dyformTableName, OneWorkFlowInterfaceParam param, FlowDefinition flowDefinition, QueryContext context) {
        String dyformFields = "";
        DataStoreConfiguration dataStoreConfiguration = (DataStoreConfiguration) context.get("dataStoreConfiguration");
        if (dataStoreConfiguration != null) {
            Collection<DataStoreColumn> dataStoreColumns = dataStoreConfiguration.getColumnMap().values();
            dyformFields = dataStoreColumns.stream().filter(column -> StringUtils.startsWith(column.getColumnName(), "d1."))
                    .map(column -> column.getColumnName().toLowerCase() + " as " + column.getColumnIndex())
                    .collect(Collectors.joining(", "));
        }

        if (StringUtils.isBlank(dyformFields)) {
            CriteriaMetadata metedata = this.initCriteriaMetadataOfFlow(context);
            // 开始添加表单相关的字段信息
            String formTable = dyformTableName;
            if (StringUtils.isBlank(formTable)) {
                formTable = this.getDyfromTableName(param, flowDefinition, context);
            }
            List<String> fields = Lists.newArrayList();
            if (StringUtils.isNotBlank(formTable)) {
                Criteria formC = context.getNativeDao().createTableCriteria(formTable);
                CriteriaMetadata formMetadata = formC.getCriteriaMetadata();
                for (int i = 0; i < formMetadata.length(); i++) {
                    String columnIndex = formMetadata.getColumnIndex(i);
                    columnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
                    String columnName = "d1." + formMetadata.getColumnIndex(i).toLowerCase();
                    // 存在跟流程一样的冗余字段，直接跳过
                    if (ArrayUtils.contains(metedata.getColumnIndexs(), columnIndex)) {
                        continue;
                    }
                    fields.add(columnName);
                }
            }
            dyformFields = StringUtils.join(fields, ",");
        }
        return dyformFields;
    }

    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return OneWorkFlowInterfaceParam.class;
    }

}
