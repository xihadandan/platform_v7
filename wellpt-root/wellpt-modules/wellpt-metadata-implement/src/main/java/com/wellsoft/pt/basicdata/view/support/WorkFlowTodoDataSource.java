package com.wellsoft.pt.basicdata.view.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.gz.entity.UfWfGzWorkData;
import com.wellsoft.pt.workflow.gz.facade.UfWfGzWorkDataFacade;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 工作流待办接口
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-22.1	wubin		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
@Component
public class WorkFlowTodoDataSource extends AbstractDataSourceProvider {

    @Autowired
    private AclService aclService;

    @Autowired
    private UfWfGzWorkDataFacade ufWfGzWorkDataFacade;

    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    @Override
    public String getModuleName() {
        return "工作流待办";
    }

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();

        DataSourceColumn flowInstUuid = new DataSourceColumn();
        flowInstUuid.setFieldName("flowInstance.uuid");
        flowInstUuid.setColumnName("flowInstUuid");
        flowInstUuid.setColumnAliase("flowInstUuid");
        flowInstUuid.setTitleName("流程.UUID");
        dataSourceColumns.add(flowInstUuid);

        DataSourceColumn flowInstanceTitle = new DataSourceColumn();
        flowInstanceTitle.setFieldName("flowInstance.title");
        flowInstanceTitle.setColumnName("title");
        flowInstanceTitle.setColumnAliase("title");
        flowInstanceTitle.setTitleName("流程.标题");
        dataSourceColumns.add(flowInstanceTitle);

        DataSourceColumn flowInstanceName = new DataSourceColumn();
        flowInstanceName.setFieldName("flowInstance.name");
        flowInstanceName.setColumnName("name");
        flowInstanceName.setColumnAliase("name");
        flowInstanceName.setTitleName("流程.名称");
        dataSourceColumns.add(flowInstanceName);

        DataSourceColumn flowInstanceCreateTime = new DataSourceColumn();
        flowInstanceCreateTime.setFieldName("flowInstance.createTime");
        flowInstanceCreateTime.setColumnName("createTime");
        flowInstanceCreateTime.setColumnAliase("createTime");
        flowInstanceCreateTime.setTitleName("流程.创建时间");
        dataSourceColumns.add(flowInstanceCreateTime);

        DataSourceColumn flowInstanceCreator = new DataSourceColumn();
        flowInstanceCreator.setFieldName("flowInstance.creator");
        flowInstanceCreator.setColumnName("creator");
        flowInstanceCreator.setColumnAliase("creator");
        flowInstanceCreator.setTitleName("流程.创建人");
        dataSourceColumns.add(flowInstanceCreator);

        DataSourceColumn endTime = new DataSourceColumn();
        endTime.setFieldName("dueTime");
        endTime.setColumnName("dueTime");
        endTime.setColumnAliase("dueTime");
        endTime.setTitleName("流程.到期时间");
        dataSourceColumns.add(endTime);

        DataSourceColumn alarmState = new DataSourceColumn();
        alarmState.setFieldName("alarmState");
        alarmState.setColumnName("alarmState");
        alarmState.setColumnAliase("alarmState");
        alarmState.setTitleName("预警状态");
        dataSourceColumns.add(alarmState);

        DataSourceColumn overDueState = new DataSourceColumn();
        overDueState.setFieldName("overDueState");
        overDueState.setColumnName("overDueState");
        overDueState.setColumnAliase("overDueState");
        overDueState.setTitleName("逾期状态");
        dataSourceColumns.add(overDueState);

        DataSourceColumn taskUuid = new DataSourceColumn();
        taskUuid.setFieldName("uuid");
        taskUuid.setColumnName("taskInstUuid");
        taskUuid.setColumnAliase("taskInstUuid");
        taskUuid.setTitleName("环节.UUID");
        dataSourceColumns.add(taskUuid);

        DataSourceColumn taskId = new DataSourceColumn();
        taskId.setFieldName("id");
        taskId.setColumnName("taskId");
        taskId.setColumnAliase("taskId");
        taskId.setTitleName("环节ID");
        dataSourceColumns.add(taskId);

        DataSourceColumn taskName = new DataSourceColumn();
        taskName.setFieldName("name");
        taskName.setColumnName("taskName");
        taskName.setColumnAliase("taskName");
        taskName.setTitleName("环节.名称");
        dataSourceColumns.add(taskName);

        DataSourceColumn serialNo = new DataSourceColumn();
        serialNo.setFieldName("serialNo");
        serialNo.setColumnName("serialNo");
        serialNo.setColumnAliase("serialNo");
        serialNo.setTitleName("流水号");
        dataSourceColumns.add(serialNo);

        DataSourceColumn startTime = new DataSourceColumn();
        startTime.setFieldName("startTime");
        startTime.setColumnName("startTime");
        startTime.setColumnAliase("startTime");
        startTime.setTitleName("环节.开始时间");
        dataSourceColumns.add(startTime);

        DataSourceColumn assignee = new DataSourceColumn();
        assignee.setFieldName("assignee");
        assignee.setColumnName("assignee");
        assignee.setColumnAliase("assignee");
        assignee.setTitleName("前办理人");
        dataSourceColumns.add(assignee);

        DataSourceColumn todoUserId = new DataSourceColumn();
        todoUserId.setFieldName("todoUserId");
        todoUserId.setColumnName("todoUserId");
        todoUserId.setColumnAliase("todoUserId");
        todoUserId.setTitleName("当前办理人ID");
        dataSourceColumns.add(todoUserId);

        DataSourceColumn todoUserName = new DataSourceColumn();
        todoUserName.setFieldName("todoUserName");
        todoUserName.setColumnName("todoUserName");
        todoUserName.setColumnAliase("todoUserName");
        todoUserName.setTitleName("当前办理人名称");
        dataSourceColumns.add(todoUserName);

        return dataSourceColumns;
    }

    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumn, String whereHql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        DataSourceColumn dataUuid = new DataSourceColumn();
        dataUuid.setFieldName("dataUuid");
        dataUuid.setColumnName("dataUuid");
        dataUuid.setColumnAliase("dataUuid");
        dataUuid.setTitleName("dataUuid");
        dataSourceColumn.add(dataUuid);
        DataSourceColumn flowDefId = new DataSourceColumn();
        flowDefId.setFieldName("flowInstance.id");
        flowDefId.setColumnName("flowDefId");
        flowDefId.setColumnAliase("flowDefId");
        flowDefId.setTitleName("flowDefId");
        dataSourceColumn.add(flowDefId);

        StringBuilder sb = new StringBuilder();
        Iterator<DataSourceColumn> it = dataSourceColumn.iterator();
        while (it.hasNext()) {
            DataSourceColumn dc = it.next();
            sb.append(dc.getFieldName());
            sb.append(" as ");
            sb.append(dc.getColumnAliase());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        QueryInfo<TaskInstance> aclQueryInfo = new QueryInfo<TaskInstance>();
        aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
        aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        aclQueryInfo.setSelectionHql(sb.toString());
        if (StringUtils.isNotBlank(orderBy)) {
            if (!orderBy.contains("overDueState")) {
                aclQueryInfo.addOrderby("overDueState desc");
            }
            if (!orderBy.contains("alarmState")) {
                aclQueryInfo.addOrderby("alarmState desc");
            }
            aclQueryInfo.addOrderby(orderBy);
        } else {
            aclQueryInfo.addOrderby("overDueState desc");
            aclQueryInfo.addOrderby("alarmState desc");
        }

        whereHql = StringUtils.replace(whereHql, " name ", " o.flowInstance.name ");
        whereHql = StringUtils.replace(whereHql, " title", " o.flowInstance.title ");
        whereHql = StringUtils.replace(whereHql, " creator ", " o.flowInstance.creator ");
        whereHql = StringUtils.replace(whereHql, " flowInstUuid ", " o.flowInstance.uuid ");
        whereHql = StringUtils.replace(whereHql, " dueTime ", " o.dueTime ");
        whereHql = StringUtils.replace(whereHql, " createTime ", " o.flowInstance.createTime ");
        whereHql = StringUtils.replace(whereHql, " flowName ", " o.flowInstance.name ");
        whereHql = StringUtils.replace(whereHql, " isActive ", " o.flowInstance.isActive ");
        whereHql = StringUtils.replace(whereHql, " taskInstUuid ", " o.uuid ");
        whereHql = StringUtils.replace(whereHql, " todoUserName ", " o.todoUserName ");
        whereHql = StringUtils.replace(whereHql, " todoUserId ", " o.todoUserId ");
        whereHql = StringUtils.replace(whereHql, " startTime ", " o.startTime ");
        whereHql = StringUtils.replace(whereHql, " taskName ", " o.name ");
        whereHql = StringUtils.replace(whereHql, " taskId ", " o.id ");
        whereHql = StringUtils.replace(whereHql, " assignee ", " o.assignee ");
        whereHql = StringUtils.replace(whereHql, " serialNo ", " o.serialNo ");

        aclQueryInfo.setWhereHql(whereHql);
        for (String key : queryParams.keySet()) {
            aclQueryInfo.addQueryParams(key, queryParams.get(key));
        }

        List<QueryItem> queryItems = aclService.queryForItem(TaskInstance.class, aclQueryInfo, AclPermission.TODO,
                SpringSecurityUtils.getCurrentUserId());
        decorateGzWorkData(queryItems);
        pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());

        return queryItems;
    }

    /**
     * @param queryItems
     */
    private void decorateGzWorkData(List<QueryItem> queryItems) {
        List<String> dataUuids = new ArrayList<String>();
        Map<String, List<QueryItem>> dataUuidMap = new HashMap<String, List<QueryItem>>();
        for (QueryItem queryItem : queryItems) {
            String flowDefId = queryItem.getString("flowDefId");
            if (WfGzDataConstant.FLOW_DEF_ID.equals(flowDefId)) {
                String dataUuid = queryItem.getString("dataUuid");
                if (StringUtils.isBlank(dataUuid)) {
                    continue;
                }
                dataUuids.add(dataUuid);
                if (!dataUuidMap.containsKey(dataUuid)) {
                    dataUuidMap.put(dataUuid, new ArrayList<QueryItem>());
                }
                dataUuidMap.get(dataUuid).add(queryItem);
            }
        }

        if (dataUuids.isEmpty()) {
            return;
        }

        List<UfWfGzWorkData> ufWfGzWorkDatas = ufWfGzWorkDataFacade.getAllByPk(dataUuids);
        for (UfWfGzWorkData ufWfGzWorkData : ufWfGzWorkDatas) {
            String dataUuid = ufWfGzWorkData.getUuid();
            List<QueryItem> items = dataUuidMap.get(dataUuid);
            for (QueryItem queryItem : items) {
                queryItem.put("todoUserName", ufWfGzWorkData.getTodoUserName());
                queryItem.put("startTime", ufWfGzWorkData.getArriveTime());
                queryItem.put("dueTime", ufWfGzWorkData.getDueTime());
                queryItem.put("name", ufWfGzWorkData.getSourceFlowName());
                queryItem.put("assignee", ufWfGzWorkData.getPreviousOperatorName());
                queryItem.put("serialNo", ufWfGzWorkData.getSourceSerialNo());
                queryItem.put("taskName", ufWfGzWorkData.getCurrentTaskName());
            }
        }
    }

}
