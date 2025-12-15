/*
 * @(#)2013-6-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 收件
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-27.1	ruanhg		2013-12-27		Create
 * </pre>
 * @date 2013-12-27
 */
@Component
public class ExchangeDataSJViewDataSource extends AbstractDataExchangeViewDataSource {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UnitApiFacade unitApiFacade;
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

        ViewColumn replyStatus = new ViewColumn();
        replyStatus.setAttributeName("replyStatus");
        replyStatus.setColumnAlias("replyStatus");
        replyStatus.setColumnName("签收状态");
        replyStatus.setColumnType(ViewColumnType.STRING);
        viewColumns.add(replyStatus);

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

        ViewColumn matterId = new ViewColumn();
        matterId.setAttributeName("matterId");
        matterId.setColumnAlias("matterId");
        matterId.setColumnName("事项Id");
        matterId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(matterId);

        ViewColumn matter = new ViewColumn();
        matter.setAttributeName("matter");
        matter.setColumnAlias("matter");
        matter.setColumnName("事项名称");
        matter.setColumnType(ViewColumnType.STRING);
        viewColumns.add(matter);

        ViewColumn createTime = new ViewColumn();
        createTime.setAttributeName("createTime");
        createTime.setColumnAlias("createTime");
        createTime.setColumnName("发送时间");
        createTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(createTime);

        /*********************相关表（exchangeDataSendMonitor）的字段*****************************/

        ViewColumn sendUser = new ViewColumn();
        sendUser.setAttributeName("s.sendUser");
        sendUser.setColumnAlias("sendUser");
        sendUser.setColumnName("发件人");
        sendUser.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sendUser);

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

        ViewColumn reservedText2 = new ViewColumn();
        reservedText2.setAttributeName("d.reservedText2");
        reservedText2.setColumnAlias("reservedText2");
        reservedText2.setColumnName("企业名称");
        reservedText2.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText2);

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

        ViewColumn reservedText4 = new ViewColumn();
        reservedText4.setAttributeName("d.reservedText4");
        reservedText4.setColumnAlias("reservedText4");
        reservedText4.setColumnName("企业类型");
        reservedText4.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText4);

        ViewColumn reservedText5 = new ViewColumn();
        reservedText5.setAttributeName("d.reservedText5");
        reservedText5.setColumnAlias("reservedText5");
        reservedText5.setColumnName("法定代表人");
        reservedText5.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText5);

        ViewColumn reservedText6 = new ViewColumn();
        reservedText6.setAttributeName("d.reservedText6");
        reservedText6.setColumnAlias("reservedText6");
        reservedText6.setColumnName("主体状态");
        reservedText6.setColumnType(ViewColumnType.STRING);
        viewColumns.add(reservedText6);

        return viewColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return ModuleID.EXCHANGE.getValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "数据交换-收件";
    }

    public Map<String, String> getColumnAliasMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (ViewColumn viewColumn : this.getAllViewColumns()) {
            map.put(viewColumn.getColumnAlias().toLowerCase(), viewColumn.getColumnAlias());
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {

        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (commonUnits == null || commonUnits.size() == 0) {
            return new ArrayList<QueryItem>();
        }
        CommonUnit commonUnit = commonUnits.get(0);
        BusinessManage businessManage = unitApiFacade.getBusinessManage(ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
                commonUnit.getId(), SpringSecurityUtils.getCurrentUserId());
        if (!businessManage.isBusinessReceiver() && !businessManage.isBusinessManager()) {
            return new ArrayList<QueryItem>();
        }
        String selectStr = "";
        int beginInt = (pagingInfo.getCurrentPage() - 1) * pagingInfo.getPageSize() + 1;
        int endInt = pagingInfo.getCurrentPage() * pagingInfo.getPageSize();

        Iterator<ViewColumn> it = null;
        if (viewColumns.isEmpty()) {
            it = getAllViewColumns().iterator();
        } else {
            it = viewColumns.iterator();
        }
        int isLimitTime = 0;// 是否有查询到期时间
        int isreplyLimitNum = 0;// 是否有查询接收逾期天数
        int isreplyStatus = 0;// 是否有查询接收逾期天数
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            if (!viewColumn.getColumnAlias().equals("reservedText2")) {
                String newField = this.changeField(viewColumn.getAttributeName());
                selectStr += "," + newField + " as " + viewColumn.getColumnAlias();
            }
            if (viewColumn.getColumnAlias().equals("limitTime")) {
                isLimitTime = 1;
            }
            if (viewColumn.getColumnAlias().equals("replyLimitNum")) {
                isreplyLimitNum = 1;
            }
            if (viewColumn.getColumnAlias().equals("replyStatus")) {
                isreplyStatus = 1;
            }
        }
        // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
        selectStr += "," + this.changeField("d.reservedText2") + " as reservedText2";
        if (isreplyStatus == 0) {
            // 签收状态
            selectStr += "," + this.changeField("replyStatus") + " as replyStatus";
        }
        // 到期时间
        if (isLimitTime == 0) {
            selectStr += "," + this.changeField("s.limitTime") + " as limitTime";
        }
        selectStr = "select " + selectStr.replaceFirst(",", "");

        String whereStr = " where o.unit_id = '" + commonUnits.get(0).getId() + "'";
        String defaultCondition = queryParams.get("defaultCondition") == null ? "" : queryParams
                .get("defaultCondition").toString();
        if (!StringUtils.isBlank(defaultCondition)) {
            String whereHqlTemp = defaultCondition;
            whereHqlTemp = whereHqlTemp.replaceAll("\\>", " ").replaceAll("\\<", " ").replaceAll("\\(", " ")
                    .replaceAll("\\)", " ").replaceAll("=", " ").replaceAll(" and ", " ").replaceAll(" or ", " ");
            while (whereHqlTemp.indexOf("  ") > 0) {
                whereHqlTemp = whereHqlTemp.replaceAll("  ", " ");
            }
            String[] whereHqlTempArr = whereHqlTemp.split(" ");
            for (int ti = 0; ti < whereHqlTempArr.length; ti++) {
                if (!StringUtils.isBlank(whereHqlTempArr[ti]) && whereHqlTempArr[ti].indexOf(".") > -1) {
                    String fileTemp = whereHqlTempArr[ti];
                    String whereHqlfield = this.changeField(fileTemp);
                    defaultCondition = defaultCondition.replaceAll(fileTemp, whereHqlfield);
                }
            }
            whereStr += " and " + defaultCondition;
        }

        String orderByStr = "";
        String orderByTemp = orderBy;
        if (!StringUtils.isBlank(orderByTemp)) {
            orderByTemp = orderByTemp.replaceAll("desc", "").replaceAll("asc", "").replaceAll(",", " ");
            while (orderByTemp.indexOf("  ") > 0) {
                orderByTemp = orderByTemp.replaceAll("  ", " ");
            }
            String[] orderByTempArr = orderByTemp.split(" ");
            for (int ti = 0; ti < orderByTempArr.length; ti++) {
                if (!StringUtils.isBlank(orderByTempArr[ti])) {
                    String fileTemp = orderByTempArr[ti];
                    String orderByfield = this.changeField(fileTemp);
                    orderBy = orderBy.replaceAll(fileTemp, orderByfield);
                }
            }
            orderByStr += " order by " + orderBy;
        }

        String sql = "";
        if (whereHql.indexOf(" like ") < 0) {// 首次
            sql += selectStr + " from (select m2.*" + " from is_exchange_data_monitor m2," + " (select nums"
                    + " from (select nums, rownum ro1" + " from (select rowid nums"
                    + " from is_exchange_data_monitor o" + whereStr + orderByStr;
            sql += ") where rownum <= " + endInt + ")" + " where ro1 >= " + beginInt + ") l1"
                    + " where m2.rowid = l1.nums) o";
            sql += " left outer join is_exchange_send_monitor s" + " on o.send_id = s.uuid"
                    + " left outer join is_exchange_data d" + " on s.data_uuid = d.uuid";
        } else {// 搜索
            whereStr += "  and lower(d.reserved_text1) like '%' || lower('" + queryParams.get("keyWord") + "') || '%'";
            sql += "select * from (select list.*, rownum beginint from (";
            sql += selectStr + " from is_exchange_data_monitor o";
            sql += " left outer join is_exchange_send_monitor s" + " on o.send_id = s.uuid"
                    + " left outer join is_exchange_data d" + " on s.data_uuid = d.uuid ";
            sql += whereStr;
            sql += ") list where rownum <= " + endInt + ") where beginint >= " + beginInt + "";
        }

        List<Map<String, Object>> queryItems = exchangeDataClientService.selectQueryItemDataBySql(sql);
        /*************************对查询结果的处理********************************/
        Map<String, String> map = this.getColumnAliasMap();
        List<QueryItem> queryItems1 = new ArrayList<QueryItem>();
        for (Map<String, Object> queryItem : queryItems) {
            QueryItem queryItem1 = new QueryItem();
            for (String key : queryItem.keySet()) {
                if (!key.equals("beginint")) {
                    String key2 = "";
                    if (key.equals("reservedtext2")) {
                        key2 = "reservedText2";
                    } else {
                        key2 = map.get(key);
                    }
                    if (key2.equals("reservedText1")) {
                        String reservedText1 = "";
                        String reservedText2 = "";
                        if (queryItem.get("reservedText1".toLowerCase()) != null) {
                            reservedText1 = queryItem.get("reservedText1".toLowerCase()).toString();
                        }
                        if (queryItem.get("reservedText2".toLowerCase()) != null) {
                            reservedText2 = queryItem.get("reservedText2".toLowerCase()).toString();
                        }
                        if (!reservedText1.equals("") && !reservedText2.equals("")) {
                            reservedText1 = reservedText2 + "(" + reservedText1 + ")";
                        } else if (!reservedText2.equals("") && reservedText1.equals("")) {
                            reservedText1 = reservedText2;
                        } else if (reservedText2.equals("") && !reservedText1.equals("")) {
                        } else {
                            reservedText1 = "";
                        }
                        queryItem1.put(key2, reservedText1);
                    } else if (key2.equals("reservedText2") || key2.equals("replyStatus")
                            || (key2.equals("limitTime") && isLimitTime == 0)) {

                    } else if (key2.equals("receiveNode")) {
                        if ("wait".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "待收");
                        } else if ("sign".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "已签收");
                        } else if ("back".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "已拒收");
                        } else if ("reply".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "已出证");
                        } else if ("transfer".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "已转发");
                        }
                    } else {
                        queryItem1.put(key2, queryItem.get(key));
                    }
                }
            }
            if ("default".equals(queryItem.get("replyStatus".toLowerCase())) && isreplyLimitNum == 1
                    && queryItem.get("limitTime".toLowerCase()) != null) {// 未签收且有查询逾期天数
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date begin = new Date();
                try {
                    begin = format.parse(queryItem.get("limitTime".toLowerCase()).toString());
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

        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (commonUnits == null || commonUnits.size() == 0) {
            return (long) 0;
        }
        CommonUnit commonUnit = commonUnits.get(0);
        BusinessManage businessManage = unitApiFacade.getBusinessManage(ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
                commonUnit.getId(), SpringSecurityUtils.getCurrentUserId());
        if (!businessManage.isBusinessReceiver() && !businessManage.isBusinessManager()) {
            return (long) 0;
        }
        String selectStr = "select count(*) as count_";

        String whereStr = " where o.unit_id = '" + commonUnits.get(0).getId() + "'";
        String defaultCondition = queryParams.get("defaultCondition") == null ? "" : queryParams
                .get("defaultCondition").toString();
        if (!StringUtils.isBlank(defaultCondition)) {
            String whereHqlTemp = defaultCondition;
            whereHqlTemp = whereHqlTemp.replaceAll("\\>", " ").replaceAll("\\<", " ").replaceAll("\\(", " ")
                    .replaceAll("\\)", " ").replaceAll("=", " ").replaceAll(" and ", " ").replaceAll(" or ", " ");
            while (whereHqlTemp.indexOf("  ") > 0) {
                whereHqlTemp = whereHqlTemp.replaceAll("  ", " ");
            }
            String[] whereHqlTempArr = whereHqlTemp.split(" ");
            for (int ti = 0; ti < whereHqlTempArr.length; ti++) {
                if (!StringUtils.isBlank(whereHqlTempArr[ti]) && whereHqlTempArr[ti].indexOf(".") > -1) {
                    String fileTemp = whereHqlTempArr[ti];
                    String whereHqlfield = this.changeField(fileTemp);
                    defaultCondition = defaultCondition.replaceAll(fileTemp, whereHqlfield);
                }
            }
            whereStr += " and " + defaultCondition;
        }

        String sql = "";
        if (whereHql.indexOf(" like ") < 0) {// 首次
            sql += selectStr + " from is_exchange_data_monitor o" + whereStr;
        } else {// 搜索
            whereStr += "  and lower(d.reserved_text1) like '%' || lower('" + queryParams.get("keyWord") + "') || '%'";
            sql += selectStr + " from is_exchange_data_monitor o";
            sql += " left outer join is_exchange_send_monitor s" + " on o.send_id = s.uuid"
                    + " left outer join is_exchange_data d" + " on s.data_uuid = d.uuid";
            sql += whereStr;
        }

        List<Map<String, Object>> list = exchangeDataClientService.selectQueryItemDataBySql(sql);
        long num = ((BigDecimal) list.get(0).get("count_")).longValue();
        return num;
    }

    public String changeField(String oldField) {
        // Map<String, ViewColumn> map = this.getViewDataSourceMap();
        // String attributeName = map.get(oldField).getAttributeName();
        String orderByfield = "";
        for (int ni = 0; ni < oldField.length(); ni++) {
            char c = oldField.charAt(ni);
            if ((c >= 'A') && (c <= 'Z')) {
                orderByfield += "_" + String.valueOf(c).toLowerCase();
            } else {
                orderByfield += c + "";
            }
        }
        if (orderByfield.indexOf(".") < 0) {
            orderByfield = "o." + orderByfield;
        }
        return orderByfield;
    }
}
