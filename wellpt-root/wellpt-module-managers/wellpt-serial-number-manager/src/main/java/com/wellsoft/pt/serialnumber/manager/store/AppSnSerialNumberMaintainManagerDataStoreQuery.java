/*
 * @(#)7/26/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.serialnumber.manager.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/26/22.1	zhulh		7/26/22		Create
 * </pre>
 * @date 7/26/22
 */
@Component
public class AppSnSerialNumberMaintainManagerDataStoreQuery extends AbstractDataStoreQueryInterface {
    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_新版流水号维护";
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
        criteriaMetadata.add("serialNumberDefUuid", "t1.serial_number_def_uuid", "流水号定义UUID", String.class);
        criteriaMetadata.add("initialValue", "t1.initial_value", "初始值", String.class);
        criteriaMetadata.add("pointer", "t1.pointer", "指针", String.class);
        criteriaMetadata.add("pointerResetType", "t1.pointer_reset_type", "指针重置类型", String.class);
        criteriaMetadata.add("pointerResetRule", "t1.pointer_reset_rule", "指针重置规则", String.class);
        criteriaMetadata.add("pointerResetRuleValue", "t1.pointer_reset_rule_value", "指针重置规则值", String.class);
        criteriaMetadata.add("systemUnitId", "t1.system_unit_id", "归属系统单位ID", String.class);
        return criteriaMetadata;
    }

    /**
     * 返回查询的条数
     *
     * @param queryContext
     * @return
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getPagingInfo().getTotalCount();
    }

    /**
     * 如何描述该方法
     *
     * @param queryContext
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appModuleSnSerialNumberMaintainManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
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
        queryParams.put("id", queryContext.getQueryParams().get("id"));
        return queryParams;
    }

}
