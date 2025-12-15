/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 工作流已办
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-06.1	zhulh		2017-01-06		Create
 * </pre>
 * @date 2017-01-06
 */
@Component
public class WorkFlowDraftDataStore extends AbstractDataStoreQueryInterface {

    private static String SELECTION_HQL = "o.uuid as uuid, o.title as title, o.name as name, o.id as id, o.createTime as createTime, o.creator as creator,"
            + " o.modifyTime as modifyTime, o.modifier as modifier, o.formUuid as formUuid, o.dataUuid as dataUuid, o.reservedText1 as reservedText1,"
            + " o.reservedText2 as reservedText2, o.reservedText3 as reservedText3, o.reservedText4 as reservedText4, o.reservedText5 as reservedText5,"
            + " o.reservedText6 as reservedText6, o.reservedText7 as reservedText7, o.reservedText8 as reservedText8, o.reservedText9 as reservedText9,"
            + " o.reservedText10 as reservedText10, o.reservedText11 as reservedText11, o.reservedText12 as reservedText12, o.reservedNumber1 as reservedNumber1,"
            + " o.reservedNumber2 as reservedNumber2, o.reservedNumber3 as reservedNumber3, o.reservedDate1 as reservedDate1, o.reservedDate2 as reservedDate2";
    @Autowired
    private AclService aclService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        QueryInfo<FlowInstance> aclQueryInfo = createAclQueryInfo(queryContext);

        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.DRAFT);
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());

        return aclService.count(FlowInstance.class, aclQueryInfo, permissions, sids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_草稿";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext arg0) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "o.uuid", "流程实例UUID", String.class);
        criteriaMetadata.add("title", "o.title", "流程标题", String.class);
        criteriaMetadata.add("name", "o.name", "流程名称", DataType.S);
        criteriaMetadata.add("id", "o.id", "流程定义ID", DataType.S);
        criteriaMetadata.add("createTime", "o.createTime", "创建时间", Date.class);
        criteriaMetadata.add("creator", "o.creator", "创建人", DataType.S);
        criteriaMetadata.add("modifyTime", "o.modifyTime", "修改时间", Date.class);
        criteriaMetadata.add("modifier", "o.modifier", "修改人", DataType.S);
        criteriaMetadata.add("formUuid", "o.formUuid", "表单定义UUID", String.class);
        criteriaMetadata.add("dataUuid", "o.dataUuid", "表单数据UUID", String.class);
        criteriaMetadata.add("reservedText1", "o.reservedText1", "预留文本字段1", String.class);
        criteriaMetadata.add("reservedText2", "o.reservedText2", "预留文本字段2", String.class);
        criteriaMetadata.add("reservedText3", "o.reservedText3", "预留文本字段3", String.class);
        criteriaMetadata.add("reservedText4", "o.reservedText4", "预留文本字段4", String.class);
        criteriaMetadata.add("reservedText5", "o.reservedText5", "预留文本字段5", String.class);
        criteriaMetadata.add("reservedText6", "o.reservedText6", "预留文本字段6", String.class);
        criteriaMetadata.add("reservedText7", "o.reservedText7", "预留文本字段7", String.class);
        criteriaMetadata.add("reservedText8", "o.reservedText8", "预留文本字段8", String.class);
        criteriaMetadata.add("reservedText9", "o.reservedText9", "预留文本字段9", String.class);
        criteriaMetadata.add("reservedText10", "o.reservedText10", "预留文本字段10", String.class);
        criteriaMetadata.add("reservedText11", "o.reservedText11", "预留文本字段11", String.class);
        criteriaMetadata.add("reservedText12", "o.reservedText12", "预留文本字段12", String.class);
        criteriaMetadata.add("reservedNumber1", "o.reservedNumber1", "预留数字字段1", DataType.D);
        criteriaMetadata.add("reservedNumber2", "o.reservedNumber2", "预留数字字段2", DataType.D);
        criteriaMetadata.add("reservedNumber3", "o.reservedNumber3", "预留数字字段3", DataType.D);
        criteriaMetadata.add("reservedDate1", "o.reservedDate1", "预留日期字段1", DataType.T);
        criteriaMetadata.add("reservedDate2", "o.reservedDate2", "预留日期字段2", DataType.T);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        QueryInfo<FlowInstance> aclQueryInfo = createAclQueryInfo(queryContext);

        List<QueryItem> queryItems = aclService.queryForItem(FlowInstance.class, aclQueryInfo, AclPermission.DRAFT,
                SpringSecurityUtils.getCurrentUserId());
        queryContext.getPagingInfo().setTotalCount(aclQueryInfo.getPage().getTotalCount());
        return queryItems;
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private QueryInfo<FlowInstance> createAclQueryInfo(QueryContext queryContext) {
        PagingInfo pagingInfo = queryContext.getPagingInfo();

        QueryInfo<FlowInstance> aclQueryInfo = new QueryInfo<FlowInstance>();
        // 大小
        aclQueryInfo.getPage().setPageNo(pagingInfo.getCurrentPage());
        aclQueryInfo.getPage().setPageSize(pagingInfo.getPageSize());
        aclQueryInfo.getPage().setAutoCount(pagingInfo.isAutoCount());
        // 选择列
        aclQueryInfo.setSelectionHql(SELECTION_HQL);
        // 查询
        String whereHql = queryContext.getSqlString();
        if (StringUtils.isNotBlank(whereHql)) {
            try {
                whereHql = TemplateEngineFactory.getDefaultTemplateEngine().process(whereHql, queryContext.getQueryParams());
            } catch (Exception e) {
            }
        }
        aclQueryInfo.setWhereHql(whereHql);
        // 查询条件
        Map<String, Object> params = queryContext.getQueryParams();
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.contains(whereHql, ":" + key)) {
                aclQueryInfo.addQueryParams(key, entry.getValue());
            }
        }
        // 排序
        Iterator<Order> orderIterator = queryContext.getOrders().iterator();
        while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            if (order.isAscending()) {
                aclQueryInfo.addOrderby(order.getColumnIndex(), "asc");
            } else {
                aclQueryInfo.addOrderby(order.getColumnIndex(), "desc");
            }
        }
        return aclQueryInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 40;
    }

}
