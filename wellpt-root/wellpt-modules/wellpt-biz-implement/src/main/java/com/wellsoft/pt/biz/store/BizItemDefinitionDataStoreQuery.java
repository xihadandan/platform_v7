/*
 * @(#)10/8/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 业务事项定义数据仓库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/8/22.1	zhulh		10/8/22		Create
 * </pre>
 * @date 10/8/22
 */
@Component
public class BizItemDefinitionDataStoreQuery extends AbstractDataStoreQueryInterface {
    @Autowired
    private BizBusinessService bizBusinessService;

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override

    public String getQueryName() {
        return "业务流程管理_业务事项定义";
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
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("name", "t.name", "名称", String.class);
        criteriaMetadata.add("id", "t.id", "ID", String.class);
        criteriaMetadata.add("code", "t.code", "编号", String.class);
        criteriaMetadata.add("type", "t.type", "类型", String.class);
        criteriaMetadata.add("businessId", "t.business_id", "业务ID", String.class);
        criteriaMetadata.add("formId", "t.form_id", "使用表单ID", String.class);
        criteriaMetadata.add("remark", "t.remark", "备注", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人ID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", Date.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人ID", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "版本号", Integer.class);
        return criteriaMetadata;
    }

    /**
     * @param queryContext
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("bizItemDefinitionQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
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

    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        String categoryUuid = Objects.toString(queryParams.get("categoryUuid"), StringUtils.EMPTY);
        // 获取业务分类下的业务ID
        if (StringUtils.isNotBlank(categoryUuid)) {
            List<String> businessIds = bizBusinessService.listIdByCategoryUuid(categoryUuid);
            if (CollectionUtils.isNotEmpty(businessIds)) {
                queryParams.put("businessIds", businessIds);
            } else {
                queryParams.put("businessIds", Lists.<String>newArrayList(TreeNode.ROOT_ID));
            }
        }
        return queryParams;
    }

}
