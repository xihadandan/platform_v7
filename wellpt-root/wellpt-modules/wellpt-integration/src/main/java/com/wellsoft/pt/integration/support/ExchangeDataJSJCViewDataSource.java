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
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 接收监察
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
public class ExchangeDataJSJCViewDataSource extends AbstractDataExchangeViewDataSource {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
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

        ViewColumn receiveTime = new ViewColumn();
        receiveTime.setAttributeName("receiveTime");
        receiveTime.setColumnAlias("receiveTime");
        receiveTime.setColumnName("到达时间");
        receiveTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(receiveTime);

        ViewColumn replyTime = new ViewColumn();
        replyTime.setAttributeName("replyTime");
        replyTime.setColumnAlias("replyTime");
        replyTime.setColumnName("签收时间");
        replyTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(replyTime);

        ViewColumn unitId = new ViewColumn();
        unitId.setAttributeName("unitId");
        unitId.setColumnAlias("unitId");
        unitId.setColumnName("接收单位");
        unitId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(unitId);

        ViewColumn receiveNode = new ViewColumn();
        receiveNode.setAttributeName("receiveNode");
        receiveNode.setColumnAlias("receiveNode");
        receiveNode.setColumnName("接收状态");
        receiveNode.setColumnType(ViewColumnType.STRING);
        viewColumns.add(receiveNode);

        ViewColumn replyLimitNum = new ViewColumn();
        replyLimitNum.setAttributeName("replyLimitNum");
        replyLimitNum.setColumnAlias("replyLimitNum");
        replyLimitNum.setColumnName("逾期天数");
        replyLimitNum.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(replyLimitNum);

        ViewColumn replyUserMsg = new ViewColumn();
        replyUserMsg.setAttributeName("replyUser");
        replyUserMsg.setColumnAlias("replyUser");
        replyUserMsg.setColumnName("签收人");
        replyUserMsg.setColumnType(ViewColumnType.STRING);
        viewColumns.add(replyUserMsg);

        /*********************相关表（exchangedata）的字段*****************************/

        ViewColumn sendUser = new ViewColumn();
        sendUser.setAttributeName("s.sendUser");
        sendUser.setColumnAlias("sendUser");
        sendUser.setColumnName("发件人");
        sendUser.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sendUser);

        ViewColumn sendTime = new ViewColumn();
        sendTime.setAttributeName("s.sendTime");
        sendTime.setColumnAlias("sendTime");
        sendTime.setColumnName("发送时间");
        sendTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(sendTime);

        ViewColumn fromId = new ViewColumn();
        fromId.setAttributeName("s.fromId");
        fromId.setColumnAlias("fromId");
        fromId.setColumnName("发送单位");
        fromId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(fromId);

        ViewColumn limitTime = new ViewColumn();
        limitTime.setAttributeName("s.limitTime");
        limitTime.setColumnAlias("limitTime");
        limitTime.setColumnName("到期时间");
        limitTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(limitTime);

        /*********************相关表（exchangedata）的字段*****************************/

        ViewColumn reservedText1 = new ViewColumn();
        reservedText1.setAttributeName("d.reservedText1");
        reservedText1.setColumnAlias("reservedText1");
        reservedText1.setColumnName("标题");
        reservedText1.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText1);

        ViewColumn reservedText3 = new ViewColumn();
        reservedText3.setAttributeName("d.reservedText3");
        reservedText3.setColumnAlias("reservedText3");
        reservedText3.setColumnName("登记时间");
        reservedText3.setColumnType(ViewColumnType.DATE);
        viewColumns.add(reservedText3);

        ViewColumn reservedNumber1 = new ViewColumn();
        reservedNumber1.setAttributeName("d.reservedNumber1");
        reservedNumber1.setColumnAlias("reservedNumber1");
        reservedNumber1.setColumnName("成立时间");
        reservedNumber1.setColumnType(ViewColumnType.DATE);
        viewColumns.add(reservedNumber1);

        ViewColumn reservedNumber2 = new ViewColumn();
        reservedNumber2.setAttributeName("d.reservedNumber2");
        reservedNumber2.setColumnAlias("reservedNumber2");
        reservedNumber2.setColumnName("注册号");
        reservedNumber2.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedNumber2);

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
        return "接收监察";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        int isLimitTime = 0;// 是否有查询到期时间
        int isreplyLimitNum = 0;// 是否有查询接收逾期天数
        if (whereHql.indexOf(" like ") < 0) {
            String hql1 = "select o.uuid as uuid from ExchangeDataMonitor o ";
            if (!StringUtils.isBlank(whereHql)) {
                hql1 += " where " + whereHql;
            }
            if (!StringUtils.isBlank(orderBy)) {
                hql1 += " order by " + orderBy;
            }
            List<QueryItem> queryItemsTemp = exchangeDataClientService.queryExchangeQueryItemData(hql1, queryParams,
                    pagingInfo);
            String inStr = "";
            for (QueryItem q : queryItemsTemp) {
                inStr += ",'" + q.get("uuid") + "'";
            }
            inStr = inStr.replaceFirst(",", "");
            if (StringUtils.isBlank(inStr)) {
                inStr = "''";
            }
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
                if (viewColumn.getAttributeName().indexOf(".") > -1) {
                    sb.append(viewColumn.getAttributeName());
                } else {
                    sb.append("o." + viewColumn.getAttributeName());
                }
                sb.append(" as ");
                sb.append(viewColumn.getColumnAlias());
                if (viewColumn.getColumnAlias().equals("limitTime")) {
                    isLimitTime = 1;
                }
                if (viewColumn.getColumnAlias().equals("replyLimitNum")) {
                    isreplyLimitNum = 1;
                }
            }
            // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
            sb.append(Separator.COMMA.getValue());
            sb.append("d.reservedText2");
            sb.append(" as ");
            sb.append("reservedText2");
            // 签收状态
            sb.append(Separator.COMMA.getValue());
            sb.append("o." + "replyStatus");
            sb.append(" as ");
            sb.append("replyStatus");
            // 到期时间
            if (isLimitTime == 0) {
                sb.append(Separator.COMMA.getValue());
                sb.append("s.limitTime");
                sb.append(" as ");
                sb.append("limitTime");
            }
            String sbStr = sb.toString().replaceFirst(Separator.COMMA.getValue(), "");

            StringBuffer hql = new StringBuffer("select " + sbStr
                    + " from ExchangeDataMonitor o left join o.exchangeDataSendMonitor s left join s.exchangeData d ");
            if (!StringUtils.isBlank(whereHql)) {
                hql.append("where o.uuid in (" + inStr + ")");
            }
            if (!StringUtils.isBlank(orderBy)) {
                hql.append(" order by ");
                hql.append(orderBy);
            }
            if (whereHql.indexOf("nowDate") > -1) {
                queryParams.put("nowDate", new Date());
            }
            pagingInfo.setAutoCount(false);
            int currentPage = pagingInfo.getCurrentPage();
            pagingInfo.setCurrentPage(1);
            queryItems = exchangeDataClientService.queryExchangeQueryItemData(hql.toString(), queryParams, pagingInfo);
            pagingInfo.setCurrentPage(currentPage);
        } else {
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
                if (viewColumn.getAttributeName().indexOf(".") > -1) {
                    sb.append(viewColumn.getAttributeName());
                } else {
                    sb.append("o." + viewColumn.getAttributeName());
                }
                sb.append(" as ");
                sb.append(viewColumn.getColumnAlias());
                if (viewColumn.getColumnAlias().equals("limitTime")) {
                    isLimitTime = 1;
                }
                if (viewColumn.getColumnAlias().equals("replyLimitNum")) {
                    isreplyLimitNum = 1;
                }
            }
            // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
            sb.append(Separator.COMMA.getValue());
            sb.append("d.reservedText2");
            sb.append(" as ");
            sb.append("reservedText2");
            // 签收状态
            sb.append(Separator.COMMA.getValue());
            sb.append("o." + "replyStatus");
            sb.append(" as ");
            sb.append("replyStatus");
            // 到期时间
            if (isLimitTime == 0) {
                sb.append(Separator.COMMA.getValue());
                sb.append("s.limitTime");
                sb.append(" as ");
                sb.append("limitTime");
            }
            String sbStr = sb.toString().replaceFirst(Separator.COMMA.getValue(), "");

            StringBuffer hql = new StringBuffer(
                    "select "
                            + sbStr
                            + " from ExchangeDataMonitor o left join o.exchangeDataSendMonitor s left join s.exchangeData d where 1=1");
            if (!StringUtils.isBlank(whereHql)) {
                hql.append(" and " + whereHql);
            }
            if (!StringUtils.isBlank(orderBy)) {
                hql.append(" order by ");
                hql.append(orderBy);
            }
            if (whereHql.indexOf("nowDate") > -1) {
                queryParams.put("nowDate", new Date());
            }
            pagingInfo.setAutoCount(false);
            queryItems = exchangeDataClientService.queryExchangeQueryItemData(hql.toString(), queryParams, pagingInfo);
        }

        /*************************对查询结果的处理********************************/
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
                } else if (key.equals("reservedText2") || key.equals("replyStatus")
                        || (key.equals("limitTime") && isLimitTime == 0)) {

                } else if (key.equals("receiveNode")) {
                    if ("wait".equals(queryItem.get(key).toString())) {
                        queryItem1.put(key, "待收");
                    } else if ("sign".equals(queryItem.get(key).toString())) {
                        queryItem1.put(key, "已签收");
                    } else if ("back".equals(queryItem.get(key).toString())) {
                        queryItem1.put(key, "已拒收");
                    }
                } else {
                    queryItem1.put(key, queryItem.get(key));
                }
            }
            if ("default".equals(queryItem.get("replyStatus")) && isreplyLimitNum == 1) {// 未签收且有查询逾期天数
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date begin = new Date();
                try {
                    begin = format.parse(queryItem.get("limitTime").toString());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }// 登记时间
                Date end = new Date();
                if (begin.before(end)) {
                    WorkPeriod WorkPeriod = basicDataApiFacade.getWorkPeriod(begin, end);
                    if ((WorkPeriod.getDays() - 1) > 0) {
                        queryItem1.put("replyLimitNum", WorkPeriod.getDays() - 1);
                    }
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
        // whereHql = strq + " (d.reservedText1 like '%" + keyWorld +
        // "%' or d.reservedText2 like '%" + keyWorld
        // + "%' or d.reservedNumber2 like '%" + keyWorld + "%')";
        // //单位搜索（现在还没接口）
        // }
        StringBuffer hql = new StringBuffer(
                "select count(*) from ExchangeDataMonitor o left join o.exchangeDataSendMonitor s left join s.exchangeData d");
        if (!StringUtils.isBlank(whereHql)) {
            hql.append(" where " + whereHql);
        }
        if (whereHql.indexOf("nowDate") > -1) {
            queryParams.put("nowDate", new Date());
        }
        return exchangeDataClientService.queryExchangeQueryItemCount(hql.toString(), queryParams);

    }
}
