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
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 上传监察
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
public class ExchangeDataSBJCViewDataSource extends AbstractDataExchangeViewDataSource {

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

        ViewColumn dataId = new ViewColumn();
        dataId.setAttributeName("dataId");
        dataId.setColumnAlias("dataId");
        dataId.setColumnName("统一查询号");
        dataId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(dataId);

        ViewColumn recVer = new ViewColumn();
        recVer.setAttributeName("dataRecVer");
        recVer.setColumnAlias("dataRecVer");
        recVer.setColumnName("版本号");
        recVer.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(recVer);

        ViewColumn correlationDataId = new ViewColumn();
        correlationDataId.setAttributeName("correlationDataId");
        correlationDataId.setColumnAlias("correlationDataId");
        correlationDataId.setColumnName("相关统一查询号");
        correlationDataId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(correlationDataId);

        ViewColumn correlationRecver = new ViewColumn();
        correlationRecver.setAttributeName("correlationRecver");
        correlationRecver.setColumnAlias("correlationRecver");
        correlationRecver.setColumnName("相关版本号");
        correlationRecver.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(correlationRecver);

        ViewColumn batchId = new ViewColumn();
        batchId.setAttributeName("exchangeDataBatch.id");
        batchId.setColumnAlias("batchId");
        batchId.setColumnName("批次号");
        batchId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(batchId);

        ViewColumn typeId = new ViewColumn();
        typeId.setAttributeName("exchangeDataBatch.typeId");
        typeId.setColumnAlias("typeId");
        typeId.setColumnName("数据类型");
        typeId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(typeId);

        ViewColumn drafter = new ViewColumn();
        drafter.setAttributeName("drafter");
        drafter.setColumnAlias("drafter");
        drafter.setColumnName("起草人");
        drafter.setColumnType(ViewColumnType.STRING);
        viewColumns.add(drafter);

        ViewColumn draftTime = new ViewColumn();
        draftTime.setAttributeName("draftTime");
        draftTime.setColumnAlias("draftTime");
        draftTime.setColumnName("起草时间");
        draftTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(draftTime);

        ViewColumn sender = new ViewColumn();
        sender.setAttributeName("sender");
        sender.setColumnAlias("sender");
        sender.setColumnName("发布人");
        sender.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sender);

        ViewColumn releaseTime = new ViewColumn();
        releaseTime.setAttributeName("releaseTime");
        releaseTime.setColumnAlias("releaseTime");
        releaseTime.setColumnName("发布时间");
        releaseTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(releaseTime);

        ViewColumn fromId = new ViewColumn();
        fromId.setAttributeName("exchangeDataBatch.fromId");
        fromId.setColumnAlias("fromId");
        fromId.setColumnName("发送");
        fromId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(fromId);

        ViewColumn toId = new ViewColumn();
        toId.setAttributeName("exchangeDataBatch.toId");
        toId.setColumnAlias("toId");
        toId.setColumnName("接收");
        toId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(toId);

        ViewColumn cc = new ViewColumn();
        cc.setAttributeName("exchangeDataBatch.cc");
        cc.setColumnAlias("cc");
        cc.setColumnName("抄送");
        cc.setColumnType(ViewColumnType.STRING);
        viewColumns.add(cc);

        ViewColumn bcc = new ViewColumn();
        bcc.setAttributeName("exchangeDataBatch.bcc");
        bcc.setColumnAlias("bcc");
        bcc.setColumnName("密送");
        bcc.setColumnType(ViewColumnType.STRING);
        viewColumns.add(bcc);

        ViewColumn businessType = new ViewColumn();
        businessType.setAttributeName("exchangeDataBatch.typeId");
        businessType.setColumnAlias("businessType");
        businessType.setColumnName("业务类");
        businessType.setColumnType(ViewColumnType.STRING);
        viewColumns.add(businessType);

        ViewColumn operateSource = new ViewColumn();
        operateSource.setAttributeName("exchangeDataBatch.operateSource");
        operateSource.setColumnAlias("operateSource");
        operateSource.setColumnName("来源");
        operateSource.setColumnType(ViewColumnType.STRING);
        viewColumns.add(operateSource);

        ViewColumn reservedText1 = new ViewColumn();
        reservedText1.setAttributeName("reservedText1");
        reservedText1.setColumnAlias("reservedText1");
        reservedText1.setColumnName("标题");
        reservedText1.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText1);

        ViewColumn reservedText3 = new ViewColumn();
        reservedText3.setAttributeName("reservedText3");
        reservedText3.setColumnAlias("reservedText3");
        reservedText3.setColumnName("登记时间");
        reservedText3.setColumnType(ViewColumnType.DATE);
        viewColumns.add(reservedText3);

        ViewColumn reservedNumber1 = new ViewColumn();
        reservedNumber1.setAttributeName("reservedNumber1");
        reservedNumber1.setColumnAlias("reservedNumber1");
        reservedNumber1.setColumnName("成立时间");
        reservedNumber1.setColumnType(ViewColumnType.DATE);
        viewColumns.add(reservedNumber1);

        ViewColumn reservedNumber2 = new ViewColumn();
        reservedNumber2.setAttributeName("reservedNumber2");
        reservedNumber2.setColumnAlias("reservedNumber2");
        reservedNumber2.setColumnName("注册号");
        reservedNumber2.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedNumber2);

        ViewColumn uploadLimitNum = new ViewColumn();
        uploadLimitNum.setAttributeName("uploadLimitNum");
        uploadLimitNum.setColumnAlias("uploadLimitNum");
        uploadLimitNum.setColumnName("逾期天数");
        uploadLimitNum.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(uploadLimitNum);

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
        return "上传监察";
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
        // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
        sb.append(Separator.COMMA.getValue());
        sb.append("o." + "reservedText2");
        sb.append(" as ");
        sb.append("reservedText2");
        String sbStr = sb.toString().replaceFirst(Separator.COMMA.getValue(), "");

        // if (whereHql != null && whereHql.indexOf("(  o.") > -1) {//搜索
        // String keyWorld = whereHql.substring(whereHql.indexOf("'%") + 2,
        // whereHql.indexOf("%'"));
        // String strq = whereHql.substring(0, whereHql.indexOf("(  o."));
        // whereHql = strq + " (o.reservedText1 like '%" + keyWorld +
        // "%' or o.reservedText2 like '%" + keyWorld
        // + "%' or o.reservedNumber2 like '%" + keyWorld + "%')";
        // //单位搜索（现在还没接口）
        // }

        StringBuffer hql = new StringBuffer("select " + sbStr + " from ExchangeData o where o.validData='yes'");
        if (!StringUtils.isBlank(whereHql)) {
            hql.append(" and " + whereHql);
        }
        if (!StringUtils.isBlank(orderBy)) {
            hql.append(" order by ");
            hql.append(orderBy);
        }
        pagingInfo.setAutoCount(false);
        List<QueryItem> queryItems = exchangeDataClientService.queryExchangeQueryItemData(hql.toString(), queryParams,
                pagingInfo);
        List<QueryItem> queryItems1 = new ArrayList<QueryItem>();
        for (QueryItem queryItem : queryItems) {
            QueryItem queryItem1 = new QueryItem();
            for (String key : queryItem.keySet()) {
                if (key.equals("reservedText1")) {
                    String reservedText1 = "";
                    String reservedText2 = "";
                    if (queryItem.get("reservedText1") != null) {
                        reservedText1 = queryItem.get("reservedText1").toString();
                    }
                    if (queryItem.get("reservedText2") != null) {
                        reservedText2 = queryItem.get("reservedText2").toString();
                    }
                    if (!reservedText1.equals("") && !reservedText2.equals("")) {
                        reservedText1 = reservedText2 + "(" + reservedText1 + ")";
                    } else if (!reservedText2.equals("") && reservedText1.equals("")) {
                        reservedText1 = reservedText2;
                    } else if (reservedText2.equals("") && !reservedText1.equals("")) {
                    } else {
                        reservedText1 = "";
                    }
                    queryItem1.put(key, reservedText1);
                } else if (key.equals("reservedText2")) {

                } else {
                    queryItem1.put(key, queryItem.get(key));
                }
            }
            queryItems1.add(queryItem1);
        }
        return queryItems1;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource#count(java.util.Collection, java.lang.String, java.util.Map)
     */
    @Override
    public Long count(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams) {

        // if (whereHql != null && whereHql.indexOf("(  o.") > -1) {//搜索
        // String keyWorld = whereHql.substring(whereHql.indexOf("'%") + 2,
        // whereHql.indexOf("%'"));
        // String strq = whereHql.substring(0, whereHql.indexOf("(  o."));
        // whereHql = strq + " (o.reservedText1 like '%" + keyWorld +
        // "%' or o.reservedText2 like '%" + keyWorld
        // + "%' or o.reservedNumber2 like '%" + keyWorld + "%')";
        // //单位搜索（现在还没接口）
        // }
        StringBuffer hql = new StringBuffer("select count(*) from ExchangeData o where o.validData='yes'");
        if (!StringUtils.isBlank(whereHql)) {
            hql.append(" and " + whereHql);
        }
        return exchangeDataClientService.queryExchangeQueryItemCount(hql.toString(), queryParams);
    }
}
