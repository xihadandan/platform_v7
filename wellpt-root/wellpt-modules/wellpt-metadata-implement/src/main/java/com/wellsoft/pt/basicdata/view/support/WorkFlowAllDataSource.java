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
import com.wellsoft.pt.security.core.userdetails.UserDetails;
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
public class WorkFlowAllDataSource extends AbstractDataSourceProvider {

    /* modified by huanglinchuan 2014.12.2 begin */
    /*
     * @Autowired private AclService aclService;
     */
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private NativeDao nativeDao;

    /*
     * @Override public List<QueryItem> query(Set<DataSourceColumn>
     * dataSourceColumns, String whereSql, Map<String, Object> queryParams,
     * String orderBy, PagingInfo pagingInfo) { StringBuilder sb = new
     * StringBuilder(); for (DataSourceColumn dc : dataSourceColumns) { if
     * (dc.getFieldName().equals("taskId")) { continue; } else if
     * (dc.getFieldName().equals("taskNameBL")) { continue; }else if
     * (dc.getFieldName().equals("isMaking")) { continue; } else {
     * sb.append(","); sb.append(dc.getFieldName()); sb.append(" as ");
     * sb.append(dc.getColumnAliase()); } }
     *
     * QueryInfo<TaskInstance> aclQueryInfo = new QueryInfo<TaskInstance>();
     * aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
     * aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
     * aclQueryInfo.setSelectionHql(sb.toString().replaceFirst(",", ""));
     *
     * whereSql = StringUtils.replace(whereSql, "name", "o.flowInstance.name");
     * whereSql = StringUtils.replace(whereSql, "title",
     * "o.flowInstance.title"); whereSql = StringUtils.replace(whereSql,
     * "todoUserName", "o.todoUserName"); whereSql =
     * StringUtils.replace(whereSql, "creator", "o.flowInstance.creator");
     * whereSql = StringUtils.replace(whereSql, "todoUserId", "o.todoUserId");
     * whereSql = StringUtils.replace(whereSql, "isActive", "o.todoUserId");
     * whereSql = StringUtils.replace(whereSql, "taskUuid", "o.uuid"); whereSql
     * = StringUtils.replace(whereSql, "flowInstUuid", "o.flowInstance.uuid");
     * whereSql = StringUtils .replace(whereSql, "createTimeBL",
     * "o.todoUserId"); whereSql = StringUtils.replace(whereSql, "dueTime",
     * "o.flowInstance.dueTime"); whereSql = StringUtils.replace(whereSql,
     * "createTime", "o.flowInstance.createTime"); whereSql =
     * StringUtils.replace(whereSql, "startTime", "o.startTime"); whereSql =
     * StringUtils.replace(whereSql, "taskNameBL", "o.todoUserId"); whereSql =
     * StringUtils.replace(whereSql, "taskName", "o.name"); whereSql =
     * StringUtils.replace(whereSql, "lastTime", "o.startTime"); whereSql =
     * StringUtils.replace(whereSql, "isMaking", "o.todoUserId"); whereSql =
     * StringUtils.replace(whereSql, "flowName", "o.todoUserId"); whereSql =
     * StringUtils.replace(whereSql, "category", "o.todoUserId");
     *
     * aclQueryInfo.setWhereHql(whereSql); for (String key :
     * queryParams.keySet()) { aclQueryInfo.addQueryParams(key,
     * queryParams.get(key)); } if (StringUtils.isNotBlank(orderBy)) { String[]
     * orderBys = orderBy.split(Separator.COMMA.getValue()); for (String string
     * : orderBys) {
     *
     * if (string.indexOf("lastTime") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("startTime desc"); } else if
     * (string.indexOf("asc") > -1) { aclQueryInfo.addOrderby("startTime asc");
     * } } if (string.indexOf("isActive") > -1) { if (string.indexOf("desc") >
     * -1) { aclQueryInfo.addOrderby("flowInstance.isActive desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.isActive asc"); } } if
     * (string.indexOf("title") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.title desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.title asc"); } } if
     * (string.indexOf("name") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.name desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.name asc"); } } if
     * (string.indexOf("createTime") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.createTime desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.createTime asc"); } } if
     * (string.indexOf("creator") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.creator desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.creator asc"); } } if
     * (string.indexOf("taskName") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("name desc"); } else if (string.indexOf("asc") >
     * -1) { aclQueryInfo.addOrderby("name asc"); } } if
     * (string.indexOf("flowName") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowDefinition.name desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowDefinition.name asc"); } } if
     * (string.indexOf("dueTime") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.dueTime desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.dueTime asc"); } }
     *
     * if (string.indexOf("endTime") > -1) { if (string.indexOf("desc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.endTime desc"); } else if
     * (string.indexOf("asc") > -1) {
     * aclQueryInfo.addOrderby("flowInstance.endTime asc"); } } } }
     *
     * //获取基于流程办结，草稿，已读，未读，监督，管理，关注，委托，待办权限的任务实例 List<Permission>
     * aclPermissions=new ArrayList<Permission>();
     * aclPermissions.add(AclPermission.DONE);
     * aclPermissions.add(AclPermission.DRAFT);
     * aclPermissions.add(AclPermission.FLAG_READ);
     * aclPermissions.add(AclPermission.UNREAD);
     * aclPermissions.add(AclPermission.SUPERVISE);
     * aclPermissions.add(AclPermission.MONITOR);
     * aclPermissions.add(AclPermission.ATTENTION);
     * aclPermissions.add(AclPermission.DELEGATION);
     * aclPermissions.add(AclPermission.TODO);
     *
     * String hqlForAcl="select acl_entry.uuid " + "from AclEntry acl_entry " +
     * "where (o.flowDefinition.uuid = acl_entry.objectIdIdentity " +
     * "and (acl_entry.granting = :granting and acl_entry.mask in (:masks) " +
     * "and (exists (select id from AclSid all_acl_sid where all_acl_sid = acl_entry.aclSid and ((all_acl_sid.sid = :sid and all_acl_sid.principal = :principal) or all_acl_sid.sid like 'ROLE_%')) "
     * +
     * "or exists (select id from AclSidMember acl_sid_member where acl_sid_member.aclSid = acl_entry.aclSid "
     * +
     * "and acl_sid_member.member = :member and acl_sid_member.moduleId = :moduleId))))"
     * +
     * " or (o.uuid=acl_entry.objectIdIdentity and acl_entry.granting = :granting and acl_entry.mask in (:masks2) "
     * +
     * "and (exists (select id from AclSid all_acl_sid where all_acl_sid=acl_entry.aclSid and all_acl_sid.sid = :sid and all_acl_sid.principal = :principal)))"
     * ;
     *
     * Map<String,Object> paras=new HashMap<String,Object>(); paras.put("sid",
     * SpringSecurityUtils.getCurrentUserId()); paras.put("member",
     * SpringSecurityUtils.getCurrentUserId()); paras.put("principal",
     * SpringSecurityUtils
     * .getCurrentUserId().startsWith(AclService.PREFIX_USERNAME));
     * paras.put("moduleId", ModuleID.WORKFLOW.getValue());
     * paras.put("granting", true);
     *
     * List<Integer> masks=new ArrayList<Integer>();
     * masks.add(AclPermission.READ.getMask()); paras.put("masks",
     * masks.toArray());
     *
     * List<Integer> masks2 = new ArrayList<Integer>(); for (Permission
     * permission : aclPermissions) { masks2.add(permission.getMask()); }
     * paras.put("masks2", masks2.toArray());
     *
     * List<QueryItem> queryItems = aclService.queryForItemByCustomHql(
     * TaskInstance.class, aclQueryInfo,hqlForAcl, paras);
     *
     * pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount()); for
     * (QueryItem queryItem : queryItems) { if (queryItem.get("endTime") !=
     * null) { queryItem.put("isMaking", "否"); } else {
     * queryItem.put("isMaking", "是"); } } return queryItems; }
     */

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

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 2.设置sid集合
        String currentUserId = userDetails.getUserId();
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

        String sids_str = "'" + currentUserId + "'";
        for (String userOrgId : userOrgIds) {
            String sid = userOrgId;
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                continue;
            } else {
                sid = "GROUP_" + sid;
            }
            sids.add(sid);
            sids_str += ",'" + sid + "'";
        }

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

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("sids", sids_str);
        values.put("masks", masks_str);
        values.put("defaultConditionSql", "");
        values.put("customConditionSql", whereSql);
        values.put("customOrderBySql", customOrderBySql);
        values.put("whereSql", whereSql);
        values.put("sid", currentUserId);
        values.put("orderBy", customOrderBySql);
        values.put("orgIds", userOrgIds);
        values.putAll(queryParams);

        String queryName = "viewTaskInstanceQuery";
        String countQueryName = "countViewTaskInstanceQuery";
        if (userDetails.isAdmin()) {
            queryName = "adminViewTaskInstanceQuery";
            countQueryName = "adminCountViewTaskInstanceQuery";
        }

        if (pagingInfo.isAutoCount()) {
            List<QueryItem> count = nativeDao.namedQuery(countQueryName, values, QueryItem.class);
            pagingInfo.setTotalCount(((BigDecimal) count.get(0).get("cnt")).longValue());
            pagingInfo.setAutoCount(false);
        }
        // List<QueryItem>
        // queryItems=nativeDao.namedQuery("myTaskInstanceQuery", values,
        // QueryItem.class, pagingInfo);
        List<QueryItem> queryItems = nativeDao.namedQuery(queryName, values, QueryItem.class, pagingInfo);

        for (QueryItem queryItem : queryItems) {
            if (queryItem.get("flowInstanceEndTime") != null) {
                queryItem.put("flowInstanceEnded", "是");
                // 办结的视图环节及当前办理人清空
                queryItem.put("taskName", "已归档");
                queryItem.put("todoUserName", "");
            } else {
                queryItem.put("flowInstanceEnded", "否");
            }
        }

        return queryItems;
    }

    private String replaceString(String whereSql) {
        whereSql = StringUtils.replace(whereSql, " flow_instance_name ", " t2.name ");
        whereSql = StringUtils.replace(whereSql, " name ", " t1.name ");
        whereSql = StringUtils.replace(whereSql, " todo_user_name ", " t1.todo_user_name ");
        whereSql = StringUtils.replace(whereSql, " todo_user_id ", " t1.todo_user_id ");
        whereSql = StringUtils.replace(whereSql, " serial_no ", " t1.serial_no ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_title ", " t2.title ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_creator ", " t2.creator ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_create_time ", " t2.create_time ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_uuid ", " t2.uuid ");
        whereSql = StringUtils.replace(whereSql, " flow_instance_due_time ", " t2.due_time ");
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
        flowInstUuid.setTitleName("流程.uuid");
        dataSourceColumns.add(flowInstUuid);

        DataSourceColumn isActive = new DataSourceColumn();
        isActive.setFieldName("flowInstanceIsActive");
        isActive.setColumnName("flow_instance_is_active");
        isActive.setColumnAliase("flow_instance_is_active");
        dataSourceColumns.add(isActive);

        DataSourceColumn flowInstanceTitle = new DataSourceColumn();
        flowInstanceTitle.setFieldName("flowInstanceTitle");
        flowInstanceTitle.setColumnName("flow_instance_title");
        flowInstanceTitle.setColumnAliase("flow_instance_title");
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
        taskUuid.setColumnName("uuid");
        taskUuid.setColumnAliase("uuid");
        taskUuid.setTitleName("环节.uuid");
        dataSourceColumns.add(taskUuid);

        DataSourceColumn taskName = new DataSourceColumn();
        taskName.setFieldName("name");
        taskName.setColumnName("name");
        taskName.setColumnAliase("name");
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

    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    @Override
    public String getModuleName() {
        return "工作流查询";
    }
}
