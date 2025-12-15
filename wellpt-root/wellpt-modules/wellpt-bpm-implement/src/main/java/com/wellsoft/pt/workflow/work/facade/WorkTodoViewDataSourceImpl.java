/*
 * @(#)2013-4-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.facade;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 待办列表视图自定义数据列
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	zhulh		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
@Component
public class WorkTodoViewDataSourceImpl extends AbstractViewDataSource {
    @Autowired
    private AclService aclService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return ModuleID.WORKFLOW.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getViewColumns()
     */
    @Override
    public Collection<ViewColumn> getAllViewColumns() {
        Collection<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        ViewColumn flowInstUuid = new ViewColumn();
        flowInstUuid.setAttributeName("uuid");
        flowInstUuid.setColumnAlias("flowInstUuid");
        flowInstUuid.setColumnName("flowInstUuid");
        viewColumns.add(flowInstUuid);

        ViewColumn title = new ViewColumn();
        title.setAttributeName("title");
        title.setColumnAlias("title");
        title.setColumnName("title");
        viewColumns.add(title);

        ViewColumn createTime = new ViewColumn();
        createTime.setAttributeName("createTime");
        createTime.setColumnAlias("createTime");
        createTime.setColumnName("createTime");
        viewColumns.add(createTime);

        ViewColumn taskUuid = new ViewColumn();
        taskUuid.setAttributeName("currentTaskInstance.uuid");
        taskUuid.setColumnAlias("taskUuid");
        taskUuid.setColumnName("taskUuid");
        viewColumns.add(taskUuid);

        ViewColumn taskName = new ViewColumn();
        taskName.setAttributeName("currentTaskInstance.name");
        taskName.setColumnAlias("taskName");
        taskName.setColumnName("taskName");
        viewColumns.add(taskName);

        ViewColumn previousAssignee = new ViewColumn();
        previousAssignee.setAttributeName("currentTaskInstance.assignee");
        previousAssignee.setColumnAlias("previousAssignee");
        previousAssignee.setColumnName("previousAssignee");
        viewColumns.add(previousAssignee);

        ViewColumn startTime = new ViewColumn();
        startTime.setAttributeName("currentTaskInstance.startTime");
        startTime.setColumnAlias("arrivalTime");
        startTime.setColumnName("arrivalTime");
        viewColumns.add(startTime);

        ViewColumn dueTime = new ViewColumn();
        dueTime.setAttributeName("currentTaskInstance.endTime");
        dueTime.setColumnAlias("dueTime");
        dueTime.setColumnName("dueTime");
        viewColumns.add(dueTime);

        ViewColumn flowName = new ViewColumn();
        flowName.setAttributeName("flowDefinition.name");
        flowName.setColumnAlias("flowName");
        flowName.setColumnName("flowName");
        viewColumns.add(flowName);
        return viewColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.lang.Object[], java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {
        Iterator<ViewColumn> it = viewColumns.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            sb.append(viewColumn.getAttributeName());
            sb.append(" as ");
            sb.append(viewColumn.getColumnAlias());
            if (it.hasNext()) {
                sb.append(Separator.COMMA.getValue());
            }
        }
        QueryInfo<FlowInstance> aclQueryInfo = new QueryInfo<FlowInstance>();
        aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
        aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        aclQueryInfo.setSelectionHql(sb.toString());
        aclQueryInfo.setWhereHql(whereHql);
        for (String key : queryParams.keySet()) {
            aclQueryInfo.addQueryParams(key, queryParams.get(key));
        }
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            for (String string : orderBys) {
                aclQueryInfo.addOrderby(string);
            }
        }
        return aclService.queryForItem(FlowInstance.class, aclQueryInfo, AclPermission.TODO,
                SpringSecurityUtils.getCurrentUserId());
    }
}
