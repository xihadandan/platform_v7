/*
 * @(#)10/10/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.biz.facade.service.BizItemDefinitionFacadeService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/10/22.1	zhulh		10/10/22		Create
 * </pre>
 * @date 10/10/22
 */
@Component
public class BizProcessItemDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private BizItemDefinitionFacadeService bizItemDefinitionFacadeService;

    /**
     * @return
     */
    @Override
    public String getQueryName() {
        return "业务流程管理_业务事项数据";
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
        criteriaMetadata.add("uuid", "t.uuid", "事项UUID", String.class);
        criteriaMetadata.add("rowId", "t.uuid", "数据行标识", String.class);
        criteriaMetadata.add("itemName", "t.item_name", "事项名称", String.class);
        criteriaMetadata.add("itemCode", "t.item_code", "事项编码", String.class);
        criteriaMetadata.add("itemType", "t.item_type", "事项类型", String.class);
        criteriaMetadata.add("itemDefName", "t.item_def_name", "事项定义名称", String.class);
        criteriaMetadata.add("itemDefId", "t.item_def_id", "事项定义ID", String.class);
        criteriaMetadata.add("itemDefUuid", "t.item_def_uuid", "事项定义ID", String.class);
        return criteriaMetadata;
    }

    /**
     * @param context
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        String processDefUuid = Objects.toString(context.getQueryParams().get("processDefUuid"), StringUtils.EMPTY);
        if (StringUtils.isBlank(processDefUuid)) {
            return Collections.emptyList();
        }
        List<QueryItem> queryItems = bizItemDefinitionFacadeService.queryItemData(processDefUuid, context);
        queryItems.forEach(item -> {
            item.put("rowId", item.get("uuid") + "_" + item.get("itemDefUuid"));
        });
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
        return context.getPagingInfo().getTotalCount();
    }
}
