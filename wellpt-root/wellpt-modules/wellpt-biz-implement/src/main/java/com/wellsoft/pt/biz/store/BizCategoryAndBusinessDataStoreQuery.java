/*
 * @(#)5/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/12/23.1	zhulh		5/12/23		Create
 * </pre>
 * @date 5/12/23
 */
@Component
public class BizCategoryAndBusinessDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "业务流程管理_业务分类及业务树";
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
        criteriaMetadata.add("parentUuid", "t.parent_uuid", "上级UUID", String.class);
        criteriaMetadata.add("remark", "t.remark", "备注", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人ID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", Date.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人ID", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "版本号", Integer.class);
        return criteriaMetadata;
    }

    /**
     * 如何描述该方法
     *
     * @param queryContext
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("bizCategoryAndBusinessQuery",
                queryContext.getQueryParams(), QueryItem.class, queryContext.getPagingInfo());
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
}
