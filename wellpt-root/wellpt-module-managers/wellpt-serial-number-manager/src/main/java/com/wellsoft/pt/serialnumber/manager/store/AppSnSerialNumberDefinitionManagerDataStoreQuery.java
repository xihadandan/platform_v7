/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.serialnumber.manager.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 流水号定义数据仓库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
@Component
public class AppSnSerialNumberDefinitionManagerDataStoreQuery extends AbstractDataStoreQueryInterface {


    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_新版流水号定义";
    }

    /**
     * 返回查询信息元数据
     *
     * @param context
     * @return
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t1.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t1.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t1.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t1.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t1.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t1.name", "名称", String.class);
        criteriaMetadata.add("id", "t1.id", "ID", String.class);
        criteriaMetadata.add("code", "t1.code", "编号", String.class);
        criteriaMetadata.add("moduleId", "t1.module_id", "模块ID", String.class);
        criteriaMetadata.add("systemUnitId", "t1.system_unit_id", "归属系统单位ID", String.class);
        return criteriaMetadata;
    }

    /**
     * @param context
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        List<QueryItem> queryItems = context.getNativeDao().namedQuery(
                "appModuleSnSerialNumberDefinitionManagerQuery",
                getQueryParams(context), QueryItem.class, context.getPagingInfo());
        return queryItems;
    }

    /**
     * 返回查询的条数
     *
     * @param context
     * @return
     */
    @Override
    public long count(QueryContext context) {
        Long total = context.getPagingInfo().getTotalCount();
        return total != -1 ? total : context.getNativeDao().countByNamedQuery(
                "appModuleSnSerialNumberDefinitionManagerQuery", getQueryParams(context));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("moduleId", queryContext.getQueryParams().get("moduleId"));
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            queryParams.put("systemIds", Lists.newArrayList(RequestSystemContextPathResolver.system()));
        }
        queryParams.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return queryParams;
    }
}
