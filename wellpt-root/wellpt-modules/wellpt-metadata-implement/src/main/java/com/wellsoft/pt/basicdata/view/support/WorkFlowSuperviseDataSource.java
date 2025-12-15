package com.wellsoft.pt.basicdata.view.support;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description: 查询具有任何权限的流程实例数据，包括已办，待办，草稿，办结，只读，关注，待阅等各种流程数据
 *
 * @author huanglinchuan
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-29.1	Administrator		2014-10-29		Create
 * </pre>
 * @date 2014-10-29
 */
@Component
public class WorkFlowSuperviseDataSource extends AbstractDataSourceProvider {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    @Override
    public String getModuleName() {
        return "工作流督办(实时)";
    }

    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumns, String whereSql, Map<String, Object> queryParams, String orderBy,
                                 PagingInfo pagingInfo) {
        // 1、设置权限
        List<Permission> aclPermissions = new ArrayList<Permission>();
        aclPermissions.add(AclPermission.DONE);
        aclPermissions.add(AclPermission.DRAFT);
        aclPermissions.add(AclPermission.FLAG_READ);
        aclPermissions.add(AclPermission.UNREAD);
        aclPermissions.add(AclPermission.SUPERVISE);
        aclPermissions.add(AclPermission.MONITOR);
        aclPermissions.add(AclPermission.ATTENTION);
        aclPermissions.add(AclPermission.DELEGATION);
        aclPermissions.add(AclPermission.TODO);
        aclPermissions.add(BasePermission.READ);
        String masks_str = "";
        for (int i = 0; i < aclPermissions.size(); i++) {
            masks_str += String.valueOf((aclPermissions.get(i)).getMask());
            if (i < aclPermissions.size() - 1) {
                masks_str += ",";
            }
        }

        // 2.设置sid集合
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<String> sids = new ArrayList<String>();
        sids.add(currentUserId);
        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(currentUserId);
        List<String> deletes = new ArrayList<String>();
        for (String orgId : userOrgIds) {
            if (orgId.startsWith(IdPrefix.USER.getValue())) {
                deletes.add(orgId);
            }
        }
        userOrgIds.removeAll(deletes);
        userOrgIds.add(currentUserId);

        // 5.替换查询条件SQL
        whereSql = replaceString(whereSql);
        if (!whereSql.trim().startsWith("and")) {
            whereSql = " and " + whereSql;
        }
        if (whereSql.trim().equals("()") || whereSql.trim().equals("and ()")) {
            whereSql = "";
        }

        // 7.组装自定义的排序SQL
        String customOrderBySql = "";
        if (StringUtils.isNotBlank(orderBy)) {
            orderBy = replaceString(orderBy);
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            if (orderBys != null && orderBys.length > 0) {
                customOrderBySql = " order by ";
                for (int i = 0; i < orderBys.length; i++) {
                    customOrderBySql += orderBys[i];
                    if (i < orderBys.length - 1) {
                        customOrderBySql += ",";
                    }
                }
            }
        }

        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("masks", masks_str);
        values.put("defaultConditionSql", "");
        values.put("customConditionSql", whereSql);
        values.put("customOrderBySql", customOrderBySql);
        values.put("whereSql", whereSql);
        values.put("sid", userId);
        values.put("orderBy", customOrderBySql);
        values.put("orgIds", userOrgIds);
        values.putAll(queryParams);

        values.put("orgIds", userOrgIds);
        boolean autoCount = pagingInfo.isAutoCount();
        pagingInfo.setAutoCount(false);
        List<QueryItem> queryItems = nativeDao.namedQuery("superviseTaskInstanceQuery", values, QueryItem.class, pagingInfo);

        // 查询总数
        if (autoCount) {
            List<QueryItem> count = nativeDao.namedQuery("countSuperviseTaskInstanceQuery", values, QueryItem.class);
            pagingInfo.setTotalCount(((BigDecimal) count.get(0).get("cnt")).longValue());
        }

        for (QueryItem queryItem : queryItems) {
            if (queryItem.get("endTime") != null) {
                queryItem.put("flowInstanceEnded", "是");
            } else {
                queryItem.put("flowInstanceEnded", "否");
            }
        }

        return queryItems;
    }

    private String replaceString(String whereSql) {
        whereSql = StringUtils.replace(whereSql, " flow_instance_name ", " t2.name ");
        whereSql = StringUtils.replace(whereSql, " task_name ", " t1.name ");
        whereSql = StringUtils.replace(whereSql, " todo_user_name ", " t1.todo_user_name ");
        whereSql = StringUtils.replace(whereSql, " todo_user_id ", " t1.todo_user_id ");
        whereSql = StringUtils.replace(whereSql, " serial_no ", " t1.serial_no ");
        whereSql = StringUtils.replace(whereSql, " title ", " t2.title ");
        whereSql = StringUtils.replace(whereSql, " task_inst_uuid ", " t1.uuid ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_creator ", " t2.creator ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_create_time ", " t2.create_time ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_uuid ", " t2.uuid ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_due_time ", " t1.due_time ");
        whereSql = StringUtils.replace(whereSql, " start_time", " t1.start_time ");
        whereSql = StringUtils.replace(whereSql, " flowInstanceEndTime ", " t2.end_time ");
        whereSql = StringUtils.replace(whereSql, " flowInstanceEnded ", " t2.end_time ");
        return whereSql;
    }

    /* modified by huanglinchuan 2014.12.2 end */

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();

        DataSourceColumn flowInstUuid = new DataSourceColumn();
        flowInstUuid.setFieldName("flowInstanceUuid");
        flowInstUuid.setColumnName("flow_instance_uuid");
        flowInstUuid.setColumnAliase("flow_instance_uuid");
        flowInstUuid.setTitleName("流程.UUID");
        dataSourceColumns.add(flowInstUuid);

        DataSourceColumn isActive = new DataSourceColumn();
        isActive.setFieldName("flowInstanceIsActive");
        isActive.setColumnName("flow_instance_is_active");
        isActive.setColumnAliase("flow_instance_is_active");
        isActive.setTitleName("流程是否激活");
        dataSourceColumns.add(isActive);

        DataSourceColumn flowInstanceTitle = new DataSourceColumn();
        flowInstanceTitle.setFieldName("flowInstanceTitle");
        flowInstanceTitle.setColumnName("title");
        flowInstanceTitle.setColumnAliase("title");
        flowInstanceTitle.setTitleName("流程.标题");
        dataSourceColumns.add(flowInstanceTitle);

        DataSourceColumn flowInstanceName = new DataSourceColumn();
        flowInstanceName.setFieldName("flowInstanceName");
        flowInstanceName.setColumnName("flow_instance_name");
        flowInstanceName.setColumnAliase("flow_instance_name");
        flowInstanceName.setTitleName("流程.名称");
        dataSourceColumns.add(flowInstanceName);

        DataSourceColumn flowInstanceCreateTime = new DataSourceColumn();
        flowInstanceCreateTime.setFieldName("flowInstanceCreateTime");
        flowInstanceCreateTime.setColumnName("flow_instance_create_time");
        flowInstanceCreateTime.setColumnAliase("flow_instance_create_time");
        flowInstanceCreateTime.setTitleName("流程.创建时间");
        dataSourceColumns.add(flowInstanceCreateTime);

        DataSourceColumn flowInstanceCreator = new DataSourceColumn();
        flowInstanceCreator.setFieldName("flowInstanceCreator");
        flowInstanceCreator.setColumnName("flow_instance_creator");
        flowInstanceCreator.setColumnAliase("flow_instance_creator");
        flowInstanceCreator.setTitleName("流程.创建人");
        dataSourceColumns.add(flowInstanceCreator);

        DataSourceColumn taskUuid = new DataSourceColumn();
        taskUuid.setFieldName("uuid");
        taskUuid.setColumnName("task_inst_uuid");
        taskUuid.setColumnAliase("task_inst_uuid");
        taskUuid.setTitleName("环节.UUID");
        dataSourceColumns.add(taskUuid);

        DataSourceColumn taskName = new DataSourceColumn();
        taskName.setFieldName("name");
        taskName.setColumnName("task_name");
        taskName.setColumnAliase("task_name");
        taskName.setTitleName("环节.名称");
        dataSourceColumns.add(taskName);

        DataSourceColumn startTime = new DataSourceColumn();
        startTime.setFieldName("startTime");
        startTime.setColumnName("start_time");
        startTime.setColumnAliase("start_time");
        startTime.setTitleName("环节.开始时间");
        dataSourceColumns.add(startTime);

        DataSourceColumn serialNo = new DataSourceColumn();
        serialNo.setFieldName("serialNo");
        serialNo.setColumnName("serial_no");
        serialNo.setColumnAliase("serial_no");
        serialNo.setTitleName("环节.流水号");
        dataSourceColumns.add(serialNo);

        DataSourceColumn todoUserId = new DataSourceColumn();
        todoUserId.setFieldName("todoUserId");
        todoUserId.setColumnName("todo_user_id");
        todoUserId.setColumnAliase("todo_user_id");
        todoUserId.setTitleName("当前办理人ID");
        dataSourceColumns.add(todoUserId);

        DataSourceColumn todoUserName = new DataSourceColumn();
        todoUserName.setFieldName("todoUserName");
        todoUserName.setColumnName("todo_user_name");
        todoUserName.setColumnAliase("todo_user_name");
        todoUserName.setTitleName("当前办理人名称");
        dataSourceColumns.add(todoUserName);

        DataSourceColumn dueTime = new DataSourceColumn();
        dueTime.setFieldName("flowInstanceDueTime");
        dueTime.setColumnName("flow_instance_due_time");
        dueTime.setColumnAliase("flow_instance_due_time");
        dueTime.setTitleName("流程.到期时间");
        dataSourceColumns.add(dueTime);

        DataSourceColumn isMaking = new DataSourceColumn();
        isMaking.setFieldName("flowInstanceEnded");
        isMaking.setColumnName("flowInstanceEnded");
        isMaking.setColumnAliase("flowInstanceEnded");
        isMaking.setTitleName("流程是否办结");
        dataSourceColumns.add(isMaking);

        return dataSourceColumns;
    }

}
