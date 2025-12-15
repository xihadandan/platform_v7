package com.wellsoft.pt.basicdata.view.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
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
public class WorkFlowDraftDataSource extends AbstractDataSourceProvider {

    @Autowired
    private AclService aclService;

    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    @Override
    public String getModuleName() {
        return "工作流草稿";
    }

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();

        DataSourceColumn flowInstUuid = new DataSourceColumn();
        flowInstUuid.setFieldName("uuid");
        flowInstUuid.setColumnName("uuid");
        flowInstUuid.setColumnAliase("uuid");
        flowInstUuid.setTitleName("流程.UUID");
        dataSourceColumns.add(flowInstUuid);

        DataSourceColumn flowInstanceTitle = new DataSourceColumn();
        flowInstanceTitle.setFieldName("title");
        flowInstanceTitle.setColumnName("title");
        flowInstanceTitle.setColumnAliase("title");
        flowInstanceTitle.setTitleName("流程.标题");
        dataSourceColumns.add(flowInstanceTitle);

        DataSourceColumn flowInstanceName = new DataSourceColumn();
        flowInstanceName.setFieldName("name");
        flowInstanceName.setColumnName("name");
        flowInstanceName.setColumnAliase("name");
        flowInstanceName.setTitleName("流程.名称");
        dataSourceColumns.add(flowInstanceName);

        DataSourceColumn flowInstanceCreateTime = new DataSourceColumn();
        flowInstanceCreateTime.setFieldName("createTime");
        flowInstanceCreateTime.setColumnName("createTime");
        flowInstanceCreateTime.setColumnAliase("createTime");
        flowInstanceCreateTime.setTitleName("流程.创建时间");
        dataSourceColumns.add(flowInstanceCreateTime);

        DataSourceColumn flowInstanceCreator = new DataSourceColumn();
        flowInstanceCreator.setFieldName("creator");
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

        return dataSourceColumns;
    }

    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumn, String whereHql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
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
        QueryInfo<FlowInstance> aclQueryInfo = new QueryInfo<FlowInstance>();
        aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
        aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        aclQueryInfo.setSelectionHql(sb.toString());
        if (StringUtils.isNotBlank(orderBy)) {
            aclQueryInfo.addOrderby(orderBy);
        }

        whereHql = StringUtils.replace(whereHql, " name ", " o.name ");
        whereHql = StringUtils.replace(whereHql, " title", " o.title ");
        whereHql = StringUtils.replace(whereHql, " creator ", " o.creator ");
        whereHql = StringUtils.replace(whereHql, " flowInstUuid ", " o.uuid ");
        whereHql = StringUtils.replace(whereHql, " dueTime ", " o.dueTime ");
        whereHql = StringUtils.replace(whereHql, " createTime ", " o.createTime ");
        whereHql = StringUtils.replace(whereHql, " isActive ", " o.isActive ");

        aclQueryInfo.setWhereHql(whereHql);
        for (String key : queryParams.keySet()) {
            aclQueryInfo.addQueryParams(key, queryParams.get(key));
        }

        List<QueryItem> queryItems = aclService.queryForItem(FlowInstance.class, aclQueryInfo, AclPermission.DRAFT,
                SpringSecurityUtils.getCurrentUserId());
        pagingInfo.setTotalCount(aclQueryInfo.getPage().getTotalCount());

        return queryItems;
    }
}
