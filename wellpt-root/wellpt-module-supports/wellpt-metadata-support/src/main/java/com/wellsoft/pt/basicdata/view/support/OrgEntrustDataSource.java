package com.wellsoft.pt.basicdata.view.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 工作委托接口
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
public class OrgEntrustDataSource extends AbstractDataSourceProvider {

    @Override
    public String getModuleId() {
        return ModuleID.WORKFLOW.getValue();
    }

    @Override
    public String getModuleName() {
        return "工作委托";
    }

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();

        DataSourceColumn uuid = new DataSourceColumn();
        uuid.setFieldName("uuid");
        uuid.setColumnName("uuid");
        uuid.setColumnAliase("uuid");
        uuid.setTitleName("uuid");
        dataSourceColumns.add(uuid);

        DataSourceColumn consignorName = new DataSourceColumn();
        consignorName.setFieldName("consignorName");
        consignorName.setColumnName("consignorName");
        consignorName.setColumnAliase("consignorName");
        consignorName.setTitleName("委托人");
        dataSourceColumns.add(consignorName);

        DataSourceColumn consignor = new DataSourceColumn();
        consignor.setFieldName("consignor");
        consignor.setColumnName("consignor");
        consignor.setColumnAliase("consignor");
        consignor.setTitleName("委托人ID");
        dataSourceColumns.add(consignor);

        DataSourceColumn content = new DataSourceColumn();
        content.setFieldName("contentName");
        content.setColumnName("contentName");
        content.setColumnAliase("contentName");
        content.setTitleName("内容");
        dataSourceColumns.add(content);

        DataSourceColumn trusteeName = new DataSourceColumn();
        trusteeName.setFieldName("trusteeName");
        trusteeName.setColumnName("trusteeName");
        trusteeName.setColumnAliase("trusteeName");
        trusteeName.setTitleName("受托人");
        dataSourceColumns.add(trusteeName);

        DataSourceColumn trustee = new DataSourceColumn();
        trustee.setFieldName("trustee");
        trustee.setColumnName("trustee");
        trustee.setColumnAliase("trustee");
        trustee.setTitleName("受托人ID");
        dataSourceColumns.add(trustee);

        DataSourceColumn fromTime = new DataSourceColumn();
        fromTime.setFieldName("fromTime");
        fromTime.setColumnName("fromTime");
        fromTime.setColumnAliase("fromTime");
        fromTime.setTitleName("开始时间");
        dataSourceColumns.add(fromTime);

        DataSourceColumn toTime = new DataSourceColumn();
        toTime.setFieldName("toTime");
        toTime.setColumnName("toTime");
        toTime.setColumnAliase("toTime");
        toTime.setTitleName("结束时间");
        dataSourceColumns.add(toTime);

        DataSourceColumn status = new DataSourceColumn();
        status.setFieldName("status");
        status.setColumnName("status");
        status.setColumnAliase("status");
        status.setTitleName("状态");
        dataSourceColumns.add(status);

        return dataSourceColumns;
    }

    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumn, String whereHql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        StringBuilder hql = new StringBuilder("from DutyAgent where 1 = 1 ");
        if (!userDetails.isSuperAdmin()) {
            hql.append(" and consignor = :consignor ");
        }
        if (StringUtils.isNotBlank(whereHql)) {
            hql.append(" and " + whereHql + " ");
        }
        if (StringUtils.isNotBlank(orderBy)) {
            hql.append(orderBy);
        }

        List<QueryItem> queryItems = null;
        if (userDetails.isSuperAdmin()) {
            queryItems = this.dao.query(hql.toString(), queryParams, QueryItem.class, pagingInfo);
        } else {
            queryParams.put("consignor", userDetails.getUserId());
            queryItems = this.dao.query(hql.toString(), queryParams, QueryItem.class, pagingInfo);
        }

        return queryItems;
    }
}
