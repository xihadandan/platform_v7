package com.wellsoft.pt.basicdata.view.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
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
 * Description: 工作流已阅接口
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
public class WorkFlowYYDataSource extends AbstractDataSourceProvider {

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
        return "工作流已阅";
    }

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();

        DataSourceColumn flowInstUuid = new DataSourceColumn();
        flowInstUuid.setFieldName("flowInstance.uuid");
        flowInstUuid.setColumnName("flowInstUuid");
        flowInstUuid.setColumnAliase("flowInstUuid");
        flowInstUuid.setTitleName("流程.uuid");
        dataSourceColumns.add(flowInstUuid);

        DataSourceColumn isActive = new DataSourceColumn();
        isActive.setFieldName("flowInstance.isActive");
        isActive.setColumnName("isActive");
        isActive.setColumnAliase("isActive");
        dataSourceColumns.add(isActive);

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

        DataSourceColumn taskUuid = new DataSourceColumn();
        taskUuid.setFieldName("uuid");
        taskUuid.setColumnName("taskUuid");
        taskUuid.setColumnAliase("taskUuid");
        taskUuid.setTitleName("环节.uuid");
        dataSourceColumns.add(taskUuid);

        DataSourceColumn taskName = new DataSourceColumn();
        taskName.setFieldName("name");
        taskName.setColumnName("taskName");
        taskName.setColumnAliase("taskName");
        taskName.setTitleName("环节.名称");
        dataSourceColumns.add(taskName);

        DataSourceColumn startTime = new DataSourceColumn();
        startTime.setFieldName("startTime");
        startTime.setColumnName("lastTime");
        startTime.setColumnAliase("lastTime");
        startTime.setTitleName("环节.开始时间");
        dataSourceColumns.add(startTime);

        DataSourceColumn flowName = new DataSourceColumn();
        flowName.setFieldName("flowDefinition.name");
        flowName.setColumnName("flowName");
        flowName.setColumnAliase("flowName");
        flowName.setTitleName("流程定义的名称");
        dataSourceColumns.add(flowName);

        DataSourceColumn category = new DataSourceColumn();
        category.setFieldName("flowDefinition.category");
        category.setColumnName("category");
        category.setColumnAliase("category");
        category.setTitleName("流程定义分类");
        dataSourceColumns.add(category);

        DataSourceColumn dueTime = new DataSourceColumn();
        dueTime.setFieldName("dueTime");
        dueTime.setColumnName("dueTime");
        dueTime.setColumnAliase("dueTime");
        dueTime.setTitleName("流程.到期时间");
        dataSourceColumns.add(dueTime);

        DataSourceColumn assignee = new DataSourceColumn();
        assignee.setFieldName("assignee");
        assignee.setColumnName("assignee");
        assignee.setColumnAliase("assignee");
        assignee.setTitleName("当前办理人");
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

        DataSourceColumn endTime = new DataSourceColumn();
        endTime.setFieldName("flowInstance.endTime");
        endTime.setColumnName("endTime");
        endTime.setColumnAliase("endTime");
        endTime.setTitleName("流程.结束时间");
        dataSourceColumns.add(endTime);

        DataSourceColumn isMaking = new DataSourceColumn();
        isMaking.setFieldName("isMaking");
        isMaking.setColumnName("isMaking");
        isMaking.setColumnAliase("isMaking");
        isMaking.setTitleName("流程是否办结");
        dataSourceColumns.add(isMaking);

        DataSourceColumn serialNo = new DataSourceColumn();
        serialNo.setFieldName("serialNo");
        serialNo.setColumnName("serialNo");
        serialNo.setColumnAliase("serialNo");
        serialNo.setTitleName("流水号");
        dataSourceColumns.add(serialNo);
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
        for (DataSourceColumn dc : dataSourceColumn) {
            if (dc.getFieldName().equals("taskId")) {
                continue;
            } else if (dc.getFieldName().equals("taskNameBL")) {
                continue;
            } else if (dc.getFieldName().equals("assignee")) {
                continue;
            } else if (dc.getFieldName().equals("isMaking")) {
                continue;
            } else {
                sb.append(",");
                sb.append(dc.getFieldName());
                sb.append(" as ");
                sb.append(dc.getColumnAliase());
            }
        }
        QueryInfo<TaskInstance> aclQueryInfo = new QueryInfo<TaskInstance>();
        aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
        aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        aclQueryInfo.setSelectionHql(sb.toString().replaceFirst(",", ""));

        whereHql = StringUtils.replace(whereHql, " name ", " o.flowInstance.name ");
        whereHql = StringUtils.replace(whereHql, " title ", " o.flowInstance.title ");
        whereHql = StringUtils.replace(whereHql, " todoUserName ", " o.todoUserName ");
        whereHql = StringUtils.replace(whereHql, " creator ", " o.flowInstance.creator ");
        whereHql = StringUtils.replace(whereHql, " flowInstance.isActive ", " o.flowInstance.isActive ");
        whereHql = StringUtils.replace(whereHql, " isActive ", " o.flowInstance.isActive ");
        whereHql = StringUtils.replace(whereHql, " flowInstUuid ", " o.flowInstance.uuid ");
        whereHql = StringUtils.replace(whereHql, " dueTime ", " o.dueTime ");
        whereHql = StringUtils.replace(whereHql, " createTime ", " o.flowInstance.createTime ");
        whereHql = StringUtils.replace(whereHql, " todoUserId ", " o.todoUserId ");
        whereHql = StringUtils.replace(whereHql, " taskUuid ", " o.uuid ");
        whereHql = StringUtils.replace(whereHql, " createTimeBL ", " o.todoUserId ");
        whereHql = StringUtils.replace(whereHql, " startTime ", " o.startTime ");
        whereHql = StringUtils.replace(whereHql, " lastTime ", " o.startTime ");
        whereHql = StringUtils.replace(whereHql, " taskNameBL ", " o.todoUserId ");
        whereHql = StringUtils.replace(whereHql, " taskName ", " o.name ");
        whereHql = StringUtils.replace(whereHql, " lastTime ", " o.startTime ");
        whereHql = StringUtils.replace(whereHql, " assignee ", " o.assignee ");
        whereHql = StringUtils.replace(whereHql, " serialNo ", " o.serialNo ");
        whereHql = StringUtils.replace(whereHql, " isMaking ", " o.todoUserId ");
        whereHql = StringUtils.replace(whereHql, " flowName ", " o.flowDefinition.name ");
        whereHql = StringUtils.replace(whereHql, " category ", " o.flowDefinition.category ");

        aclQueryInfo.setWhereHql(whereHql);
        for (String key : queryParams.keySet()) {
            aclQueryInfo.addQueryParams(key, queryParams.get(key));
        }
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            for (String string : orderBys) {

                if (string.indexOf("lastTime") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("startTime desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("startTime asc");
                    }
                }
                if (string.indexOf("isActive") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.isActive desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.isActive asc");
                    }
                }
                if (string.indexOf("title") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.title desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.title asc");
                    }
                }
                if (string.indexOf("name") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.name desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.name asc");
                    }
                }
                if (string.indexOf("createTime") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.createTime desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.createTime asc");
                    }
                }
                if (string.indexOf("creator") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.creator desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.creator asc");
                    }
                }
                if (string.indexOf("taskName") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("name desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("name asc");
                    }
                }
                if (string.indexOf("flowName") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowDefinition.name desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowDefinition.name asc");
                    }
                }
                if (string.indexOf("dueTime") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("dueTime desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("dueTime asc");
                    }
                }

                if (string.indexOf("endTime") > -1) {
                    if (string.indexOf("desc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.endTime desc");
                    } else if (string.indexOf("asc") > -1) {
                        aclQueryInfo.addOrderby("flowInstance.endTime asc");
                    }
                }
            }
        }
        List<QueryItem> queryItems = aclService.queryForItem(TaskInstance.class, aclQueryInfo, AclPermission.FLAG_READ,
                SpringSecurityUtils.getCurrentUserId());
        decorateGzWorkData(queryItems);
        pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());

        for (QueryItem queryItem : queryItems) {
            if (queryItem.get("endTime") != null) {
                queryItem.put("isMaking", "是");
            } else {
                queryItem.put("isMaking", "否");
            }
        }
        return queryItems;
    }



    /**
     * 如何描述该方法
     *
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
