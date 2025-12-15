/*
 * @(#)2020年1月2日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月2日.1	wangrf		2020年1月8日		Create
 * </pre>
 * @date 2020年1月8日
 */
@Component
public class BusinessCategoryDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "公共资源_表单应用配置";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("manageDeptValue", "manage_dept_value", "部门value", String.class);
        metadata.add("manageUserValue", "manage_user_value", "管理员value值", String.class);
        metadata.add("applicationUuid", "application_uuid", "应用uuid", String.class);
        return metadata;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * 如何描述该方法
     * 改造方法于
     * com.wellsoft.pt.basicdata.business.service.impl.BusinessCategoryOrgServiceImpl
     * .queryByApplication(JqGridQueryInfo queryInfo, String value)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        // 查询数据库
        List<QueryItem> objs = context.getNativeDao().namedQuery("businessCategoryQuery", getQueryParams(context),
                QueryItem.class, context.getPagingInfo());
        return objs;
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        // 根据业务类别排序
        String whereSql = queryContext.getWhereSqlString();
        if (StringUtils.isNotEmpty(whereSql) && whereSql.contains("application_uuid")) {
            whereSql = whereSql.replace("application_uuid", "a.uuid");
        }
        queryParams.put("whereSql", whereSql);

        String orderBy = queryContext.getOrderString();
        orderBy = orderBy.replace("application_uuid", "a.uuid");
        queryParams.put("orderBy", orderBy);

        queryParams.put("unitId", queryContext.getQueryParams().get("currentUserUnitId"));
        return queryParams;
    }
}
