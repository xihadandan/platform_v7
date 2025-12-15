/*
 * @(#)2013-6-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 批次信息
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-28.1	Administrator		2013-11-28		Create
 * </pre>
 * @date 2013-11-28
 */
@Component
public class ExchangeDataBatchViewDataSource extends AbstractDataExchangeViewDataSource {

    @Autowired
    private ExchangeDataClientService exchangeDataClientService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getAllViewColumns()
     */
    @Override
    public Collection<ViewColumn> getAllViewColumns() {
        Collection<ViewColumn> viewColumns = new ArrayList<ViewColumn>();

        ViewColumn uuid = new ViewColumn();
        uuid.setAttributeName("uuid");
        uuid.setColumnAlias("uuid");
        uuid.setColumnName("uuid");
        uuid.setColumnType(ViewColumnType.STRING);
        viewColumns.add(uuid);

        ViewColumn id = new ViewColumn();
        id.setAttributeName("id");
        id.setColumnAlias("id");
        id.setColumnName("批次号");
        id.setColumnType(ViewColumnType.STRING);
        viewColumns.add(id);

        ViewColumn typeId = new ViewColumn();
        typeId.setAttributeName("typeId");
        typeId.setColumnAlias("typeId");
        typeId.setColumnName("业务类");
        typeId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(typeId);

        ViewColumn operateSource = new ViewColumn();
        operateSource.setAttributeName("operateSource");
        operateSource.setColumnAlias("operateSource");
        operateSource.setColumnName("数据来源");
        operateSource.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(operateSource);

        ViewColumn createTime = new ViewColumn();
        createTime.setAttributeName("createTime");
        createTime.setColumnAlias("createTime");
        createTime.setColumnName("上传日期");
        createTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(createTime);

        return viewColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return ModuleID.DATA_EXCHANGE.getValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "批次信息";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {

        Iterator<ViewColumn> it = null;
        if (viewColumns.isEmpty()) {
            it = getAllViewColumns().iterator();
        } else {
            it = viewColumns.iterator();
        }
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            sb.append(Separator.COMMA.getValue());
            sb.append("o." + viewColumn.getAttributeName());
            sb.append(" as ");
            sb.append(viewColumn.getColumnAlias());
        }
        String sbStr = sb.toString().replaceFirst(Separator.COMMA.getValue(), "");
        StringBuffer hql = new StringBuffer("select " + sbStr + " from ExchangeDataBatch o where 1=1 " + whereHql);
        Map<String, Object> values = new HashMap<String, Object>();
        pagingInfo.setAutoCount(false);
        List<QueryItem> queryItems = exchangeDataClientService.queryExchangeQueryItemData(hql.toString(), values,
                pagingInfo);
        for (QueryItem queryItem : queryItems) {
            for (String key : queryItem.keySet()) {
                if (key != null && key.equals("typeId")) {
                    // 解析业务类型
                    ExchangeDataClientService exchangeDataClientService = ApplicationContextHolder
                            .getBean(ExchangeDataClientService.class);
                    ExchangeDataType exchangeDataType = exchangeDataClientService.getExchangeDataTypeById(queryItem
                            .get(key).toString());
                    if (exchangeDataType != null) {
                        queryItem.put(key, exchangeDataType.getName());
                    } else {
                        queryItem.put(key, queryItem.get("typeId"));
                    }
                }
            }
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource#count(java.util.Collection, java.lang.String, java.util.Map)
     */
    @Override
    public Long count(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams) {
        StringBuffer hql = new StringBuffer("select count(*) from ExchangeDataBatch o where 1=1 " + whereHql);
        Map<String, Object> values = new HashMap<String, Object>();
        return exchangeDataClientService.queryExchangeQueryItemCount(hql.toString(), values);
    }
}
