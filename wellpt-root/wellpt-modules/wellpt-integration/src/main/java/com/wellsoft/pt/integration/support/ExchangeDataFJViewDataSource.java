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
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 发件(exchangeDataSendMonitor)
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
public class ExchangeDataFJViewDataSource extends AbstractDataExchangeViewDataSource {

    @Autowired
    private UnitApiFacade unitApiFacade;
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

        ViewColumn fromId = new ViewColumn();
        fromId.setAttributeName("fromId");
        fromId.setColumnAlias("fromId");
        fromId.setColumnName("发送单位");
        fromId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(fromId);

        ViewColumn sendUser = new ViewColumn();
        sendUser.setAttributeName("sendUser");
        sendUser.setColumnAlias("sendUser");
        sendUser.setColumnName("发送人");
        sendUser.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sendUser);

        ViewColumn sendTime = new ViewColumn();
        sendTime.setAttributeName("sendTime");
        sendTime.setColumnAlias("sendTime");
        sendTime.setColumnName("发送时间");
        sendTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(sendTime);

        ViewColumn limitTime = new ViewColumn();
        limitTime.setAttributeName("limitTime");
        limitTime.setColumnAlias("limitTime");
        limitTime.setColumnName("到期时间");
        limitTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(limitTime);

        ViewColumn sendNode = new ViewColumn();
        sendNode.setAttributeName("sendNode");
        sendNode.setColumnAlias("sendNode");
        sendNode.setColumnName("发送状态");
        sendNode.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sendNode);

        ViewColumn modifyTime = new ViewColumn();
        modifyTime.setAttributeName("modifyTime");
        modifyTime.setColumnAlias("modifyTime");
        modifyTime.setColumnName("修改时间");
        modifyTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(modifyTime);

        /*******************关联表的字段*****************************/

        ViewColumn uploadLimitNum = new ViewColumn();
        uploadLimitNum.setAttributeName("d.uploadLimitNum");
        uploadLimitNum.setColumnAlias("uploadLimitNum");
        uploadLimitNum.setColumnName("逾期天数");
        uploadLimitNum.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(uploadLimitNum);

        ViewColumn dataId = new ViewColumn();
        dataId.setAttributeName("d.dataId");
        dataId.setColumnAlias("dataId");
        dataId.setColumnName("统一查询号");
        dataId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(dataId);

        ViewColumn recVer = new ViewColumn();
        recVer.setAttributeName("d.dataRecVer");
        recVer.setColumnAlias("dataRecVer");
        recVer.setColumnName("版本号");
        recVer.setColumnType(ViewColumnType.INTEGER);
        viewColumns.add(recVer);

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

        ViewColumn drafter = new ViewColumn();
        drafter.setAttributeName("d.drafter");
        drafter.setColumnAlias("drafter");
        drafter.setColumnName("起草人");
        drafter.setColumnType(ViewColumnType.STRING);
        viewColumns.add(drafter);

        ViewColumn draftTime = new ViewColumn();
        draftTime.setAttributeName("d.draftTime");
        draftTime.setColumnAlias("draftTime");
        draftTime.setColumnName("起草时间");
        draftTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(draftTime);

        ViewColumn sender = new ViewColumn();
        sender.setAttributeName("d.sender");
        sender.setColumnAlias("sender");
        sender.setColumnName("发布人");
        sender.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sender);

        ViewColumn releaseTime = new ViewColumn();
        releaseTime.setAttributeName("d.releaseTime");
        releaseTime.setColumnAlias("releaseTime");
        releaseTime.setColumnName("发布时间");
        releaseTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(releaseTime);

        /*******************特殊解析的字段*****************************/

        ViewColumn businessType = new ViewColumn();
        businessType.setAttributeName("y.name");
        businessType.setColumnAlias("businessType");
        businessType.setColumnName("业务类");
        businessType.setColumnType(ViewColumnType.INVALID);
        viewColumns.add(businessType);

        // ViewColumn toIds = new ViewColumn();
        // toIds.setAttributeName("toIds");
        // toIds.setColumnAlias("toIds");
        // toIds.setColumnName("接收单位");
        // toIds.setColumnType(ViewColumnType.STRING);
        // viewColumns.add(toIds);
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
        return "数据交换-发件";
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
        if (!businessManage.isBusinessSender() && !businessManage.isBusinessManager()) {
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
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            if (!viewColumn.getColumnAlias().equals("reservedText2")) {
                String newField = this.changeField(viewColumn.getAttributeName());
                selectStr += "," + newField + " as " + viewColumn.getColumnAlias();
            }
        }
        // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
        selectStr += "," + this.changeField("d.reservedText2") + " as reservedText2";
        selectStr = "select " + selectStr.replaceFirst(",", "");

        String whereStr = " where o.from_id = '" + commonUnits.get(0).getId() + "' and o.send_node != 'verificaFail'";
        if (!StringUtils.isBlank(whereHql)) {
            String whereHqlTemp = whereHql;
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
                    whereHql = whereHql.replaceAll(fileTemp, whereHqlfield);
                }
            }
            whereStr += " and " + whereHql;
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
        // if (whereHql != null && whereHql.indexOf("(  o.") > -1) {//搜索
        // String keyWorld = whereHql.substring(whereHql.indexOf("'%") + 2,
        // whereHql.indexOf("%'"));
        // String strq = whereHql.substring(0, whereHql.indexOf("(  o."));
        // whereHql = strq + " (d.reservedText1 like '%" + keyWorld +
        // "%' or d.reservedText2 like '%" + keyWorld
        // + "%' or d.reservedNumber2 like '%" + keyWorld + "%')";
        // //单位搜索（现在还没接口）
        // }

        String sql = "";
        if (whereHql.indexOf(" like ") < 0) {// 首次
            sql += selectStr + " from (select m2.*" + " from is_exchange_send_monitor m2," + " (select nums"
                    + " from (select nums, rownum ro1" + " from (select rowid nums"
                    + " from is_exchange_send_monitor o " + whereStr + orderByStr;
            sql += ") where rownum <= " + endInt + ")" + " where ro1 >= " + beginInt + ") l1"
                    + " where m2.rowid = l1.nums) o";
            sql += " left outer join is_exchange_data d" + " on o.data_uuid = d.uuid"
                    + " left outer join is_exchange_data_batch b"
                    + " on d.batch_id = b.uuid,is_exchange_data_type y where b.type_id = y.id " + orderByStr;
        } else {// 搜索
            sql += "select * from (select list.*, rownum beginint from (";
            sql += selectStr + " from is_exchange_send_monitor o ";
            sql += "left outer join is_exchange_data d" + " on o.data_uuid = d.uuid"
                    + " left outer join is_exchange_data_batch b" + " on d.batch_id = b.uuid,is_exchange_data_type y ";
            sql += whereStr + " and b.type_id = y.id " + orderByStr;
            sql += ") list where rownum <= " + endInt + ") where beginint >= " + beginInt + "";
        }
        List<Map<String, Object>> queryItems = exchangeDataClientService.selectQueryItemDataBySql(sql);
        /*************************对查询结果的处理********************************/
        List<QueryItem> queryItems1 = new ArrayList<QueryItem>();
        Map<String, String> map = this.getColumnAliasMap();
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
                        if (queryItem.get("reservedtext1") != null) {
                            reservedText1 = queryItem.get("reservedtext1").toString();
                        }
                        if (queryItem.get("reservedtext2") != null) {
                            reservedText2 = queryItem.get("reservedtext2").toString();
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
                    } else if (key2.equals("reservedText2")) {

                    } else if (key2.equals("sendNode")) {
                        if ("ing".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "发送中");
                        } else if ("end".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "送达");
                        } else if ("sign".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "已签收");
                        } else if ("abnormal".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "未送达");
                        } else if ("back".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "退回");
                        } else if ("examineIng".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "待审核");
                        } else if ("examineFail".equals(queryItem.get(key).toString())) {
                            queryItem1.put(key2, "审核被退回");
                        }
                    } else {
                        queryItem1.put(key2, queryItem.get(key));
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
        if (!businessManage.isBusinessSender() && !businessManage.isBusinessManager()) {
            return (long) 0;
        }
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
                "select count(*) from ExchangeDataSendMonitor o left join o.exchangeData d where o.sendNode != 'verificaFail' and o.fromId = '"
                        + commonUnits.get(0).getId() + "'");
        if (!StringUtils.isBlank(whereHql)) {
            hql.append(" and " + whereHql);
        }
        long num = exchangeDataClientService.queryExchangeQueryItemCount(hql.toString(), queryParams);
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

    public Map<String, String> getColumnAliasMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (ViewColumn viewColumn : this.getAllViewColumns()) {
            map.put(viewColumn.getColumnAlias().toLowerCase(), viewColumn.getColumnAlias());
        }
        return map;
    }
}
