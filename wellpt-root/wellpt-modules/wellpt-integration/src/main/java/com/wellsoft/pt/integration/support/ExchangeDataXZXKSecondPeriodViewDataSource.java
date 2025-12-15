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

import java.util.*;

/**
 * Description: 商事管理-行政许可-发件-二期 （二期）
 *
 * @author wangdj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-12.1	wangdj		2016-05-12		Create
 * </pre>
 * @date 2016-05-12
 */
@Component
public class ExchangeDataXZXKSecondPeriodViewDataSource extends AbstractDataExchangeViewDataSource {

    private final String MAJOR_DATA_VIEW_NAME = "o"; // 主要列信息查询视图的别名定义
    private final String IS_EXCHANGE_DATA_TABLE_NAME = "d"; // is_exchange_data表的别名定义
    private final String IS_EXCHANGE_DATA_TYPE_TABLE_NAME = "y";// IS_EXCHANGE_DATA_TYPE表名
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private ExchangeDataClientService exchangeDataClientService;

    /**
     * 提供配置需要的列信息
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getAllViewColumns()
     */
    @Override
    public Collection<ViewColumn> getAllViewColumns() {
        Collection<ViewColumn> viewColumns = new ArrayList<ViewColumn>();

        viewColumns.add(createViewColumn("uuid", "uuid", "uuid", ViewColumnType.STRING));
        viewColumns.add(createViewColumn("fromId", "fromId", "发送单位", ViewColumnType.STRING));
        viewColumns.add(createViewColumn("sendUser", "sendUser", "发送人", ViewColumnType.STRING));
        viewColumns.add(createViewColumn("sendTime", "sendTime", "发送时间", ViewColumnType.DATE));
        viewColumns.add(createViewColumn("limitTime", "limitTime", "到期时间", ViewColumnType.DATE));
        viewColumns.add(createViewColumn("sendNode", "sendNode", "发送状态", ViewColumnType.STRING));
        viewColumns.add(createViewColumn("modifyTime", "modifyTime", "修改时间", ViewColumnType.DATE));

        /*******************关联表的字段*****************************/
        viewColumns.add(createViewColumn(getRelativeColumnName("uploadLimitNum"), "uploadLimitNum", "逾期天数",
                ViewColumnType.INTEGER));
        viewColumns.add(createViewColumn(getRelativeColumnName("dataId"), "dataId", "统一查询号", ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("dataRecVer"), "dataRecVer", "版本号",
                ViewColumnType.INTEGER));
        viewColumns.add(createViewColumn(getRelativeColumnName("reservedText1"), "reservedText1", "标题",
                ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("reservedText2"), "reservedText2", "企业名称",
                ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("reservedText3"), "reservedText3", "登记时间",
                ViewColumnType.DATE));
        viewColumns.add(createViewColumn(getRelativeColumnName("reservedNumber1"), "reservedNumber1", "成立时间",
                ViewColumnType.DATE));
        viewColumns.add(createViewColumn(getRelativeColumnName("reservedNumber2"), "reservedNumber2", "注册号",
                ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("drafter"), "drafter", "起草人", ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("draftTime"), "draftTime", "起草时间", ViewColumnType.DATE));
        viewColumns.add(createViewColumn(getRelativeColumnName("sender"), "sender", "发布人", ViewColumnType.STRING));
        viewColumns.add(createViewColumn(getRelativeColumnName("releaseTime"), "releaseTime", "发布时间",
                ViewColumnType.DATE));

        /*******************特殊解析的字段*****************************/
        viewColumns.add(createViewColumn(getSpecialColumnName("name"), "businessType", "业务类", ViewColumnType.INVALID));
        return viewColumns;
    }

    /**
     * 通过表列名，获取查询中加上表识别的形式的字符串
     *
     * @param tableColumn
     * @return
     */
    private String getRelativeColumnName(String tableColumn) {
        return IS_EXCHANGE_DATA_TABLE_NAME + "." + tableColumn;
    }

    /**
     * 通过表列名，获取查询中加上表识别的形式的字符串
     *
     * @param tableColumn
     * @return
     */
    private String getSpecialColumnName(String tableColumn) {
        return IS_EXCHANGE_DATA_TYPE_TABLE_NAME + "." + tableColumn;
    }

    /**
     * 获取模块ID
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return ModuleID.DATA_EXCHANGE.getValue();
    }

    /**
     * 提供配置的模块名称
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "商事管理-行政许可-发件-二期";
    }

    /**
     * 查询获取数据列表
     *
     * @param viewColumns 列集合
     * @param whereHql    配置中的默认条件
     * @param queryParams 配置的查询条件参数映射
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {

        // 判断预查询参数是否合法
        PreQueryResult preQueryResult = getPreQueryResult();
        if (!preQueryResult.isQueryValid()) {
            return new ArrayList<QueryItem>();
        }

        // 分页信息
        int beginInt = (pagingInfo.getCurrentPage() - 1) * pagingInfo.getPageSize() + 1;
        int endInt = pagingInfo.getCurrentPage() * pagingInfo.getPageSize();

        // 获取选择部分语句
        String selectStr = getColumnsSqlStrFromViewCollection(viewColumns);

        // 条件语句
        String whereStr = " where %s.from_id = '" + preQueryResult.getCommonUnit().getId()
                + "' and %s.send_node != 'verificaFail'";
        whereStr = String.format(whereStr, MAJOR_DATA_VIEW_NAME, MAJOR_DATA_VIEW_NAME);

        // 条件语句转成成对应于数据库中的语句
        if (!StringUtils.isBlank(whereHql)) {
            whereHql = translateToSqlInDB(whereHql, true);
            whereStr += " and " + whereHql;
        }

        // 排序语句转换
        String orderByStr = "";//
        if (!StringUtils.isBlank(orderBy)) {
            orderBy = translateToSqlInDB(orderBy, false);
            orderByStr += " order by " + orderBy;
        } else {
            orderBy = "send_time desc";
            orderByStr += " order by " + orderBy;
        }

        // 查询结果
        List<Map<String, Object>> queryItems = exchangeDataClientService.selectQueryItemDataBySql(createSql(selectStr,
                whereStr, orderByStr, endInt, beginInt));
        /*************************对查询结果的处理********************************/
        return processQueryResult(queryItems);

    }

    /**
     * 拼装SQL语句
     *
     * @param selectStr
     * @param whereStr
     * @param orderByStr
     * @param endInt
     * @param beginInt
     * @return
     */
    private String createSql(String selectStr, String whereStr, String orderByStr, Integer endInt, Integer beginInt) {
        String sql = "";
        if (whereStr.indexOf(" like ") < 0) {// 首次
            sql += selectStr + " from (select m2.*" + " from is_exchange_send_monitor m2," + " (select nums"
                    + " from (select nums, rownum ro1" + " from (select rowid nums"
                    + " from is_exchange_send_monitor o " + whereStr + orderByStr;
            sql += ") where rownum <= " + endInt + ")" + " where ro1 >= " + beginInt + ") l1"
                    + " where m2.rowid = l1.nums)  " + MAJOR_DATA_VIEW_NAME;
            sql += " left outer join is_exchange_data " + IS_EXCHANGE_DATA_TABLE_NAME + " on  " + MAJOR_DATA_VIEW_NAME
                    + ".data_uuid = " + IS_EXCHANGE_DATA_TABLE_NAME + ".uuid"
                    + " left outer join is_exchange_data_batch b" + " on " + IS_EXCHANGE_DATA_TABLE_NAME
                    + ".batch_id = b.uuid,is_exchange_data_type " + IS_EXCHANGE_DATA_TYPE_TABLE_NAME
                    + " where b.type_id = " + IS_EXCHANGE_DATA_TYPE_TABLE_NAME + ".id " + orderByStr;
        } else {// 搜索
            sql += "select * from (select list.*, rownum beginint from (";
            sql += selectStr + " from is_exchange_send_monitor o ";
            sql += "left outer join is_exchange_data d" + " on o.data_uuid = d.uuid"
                    + " left outer join is_exchange_data_batch b" + " on d.batch_id = b.uuid,is_exchange_data_type y ";
            sql += whereStr + " and b.type_id = y.id " + orderByStr;
            sql += ") list where rownum <= " + endInt + ") where beginint >= " + beginInt + "";
        }
        return sql;
    }

    /**
     * 增加视图列
     *
     * @param viewColumns
     * @param attributeName
     * @param columnAlias
     * @param columnName
     * @param columnType
     */
    private ViewColumn createViewColumn(String attributeName, String columnAlias, String columnName,
                                        ViewColumnType columnType) {
        ViewColumn vc = new ViewColumn();
        vc.setAttributeName(attributeName);
        vc.setColumnAlias(columnAlias);
        vc.setColumnName(columnName);
        vc.setColumnType(columnType);
        return vc;
    }

    /**
     * 统计记录数
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource#count(java.util.Collection, java.lang.String, java.util.Map)
     */
    @Override
    public Long count(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams) {

        // 判断预查询参数是否合法
        PreQueryResult preQueryResult = getPreQueryResult();
        if (!preQueryResult.isQueryValid()) {
            return (long) 0;
        }

        StringBuffer hql = new StringBuffer(
                "select count(*) from ExchangeDataSendMonitor o left join o.exchangeData d where o.sendNode != 'verificaFail' and o.fromId = '"
                        + preQueryResult.getCommonUnit().getId() + "'");
        if (!StringUtils.isBlank(whereHql)) {
            hql.append(" and " + whereHql);
        }
        long num = exchangeDataClientService.queryExchangeQueryItemCount(hql.toString(), queryParams);
        return num;
    }

    /**
     * 根据配置的列集合，获取查询语句中select部分
     * 如何描述该方法
     *
     * @param viewColumns
     * @return
     */
    private String getColumnsSqlStrFromViewCollection(Collection<ViewColumn> viewColumns) {
        String selectStr = "";
        Iterator<ViewColumn> it = null;
        if (viewColumns.isEmpty()) {
            it = getAllViewColumns().iterator();
        } else {
            it = viewColumns.iterator();
        }
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            if (!viewColumn.getColumnAlias().equals("reservedText2")) {
                String newField = this.changeField(viewColumn.getAttributeName(), false);
                selectStr += "," + newField + " as " + viewColumn.getColumnAlias();
            }
        }
        // 标题的解析 商事主体名称(事项名称) 加入查询事项名称
        selectStr += "," + this.changeField(getRelativeColumnName("reservedText2"), false) + " as reservedText2";
        selectStr = "select " + selectStr.replaceFirst(",", "");
        return selectStr;
    }

    /**
     * 转成数据库中查询语句（对应于特定数据库表结构列的查询）
     *
     * @param orderByTemp
     * @return
     */
    protected String translateToSqlInDB(String sql, boolean hasFieldValue) {

        String sqlSelect = sql;
        sqlSelect = replaceAllCharToSpaceOrEmpty(sqlSelect);

        String[] tempArr = sqlSelect.split(" ");
        for (int ti = 0; ti < tempArr.length; ti++) {
            if (!StringUtils.isBlank(tempArr[ti])) {
                String strTemp = tempArr[ti];
                boolean isFieldValue = false;
                if (hasFieldValue) { // 有值域的情况
                    if (strTemp.indexOf(".") != -1) { // 如果包含有.符号，认为是表名.列名形式
                        isFieldValue = false;
                    } else {
                        isFieldValue = true;
                    }
                }
                String newField = this.changeField(strTemp, isFieldValue);
                sql = sql.replaceAll(strTemp, newField);
            }
        }
        return sql;
    }

    /**
     * 将特定相关字符替换成空格或者空串
     *
     * @param originStr
     * @return
     */
    protected String replaceAllCharToSpaceOrEmpty(String originStr) {

        // 排序词替换
        originStr = originStr.replaceAll("desc", "").replaceAll("asc", "").replaceAll(",", " ");
        // 查询字符过滤替换
        originStr = originStr.replaceAll("\\>", " ").replaceAll("\\<", " ").replaceAll("\\(", " ")
                .replaceAll("\\)", " ").replaceAll("=", " ").replaceAll(" and ", " ").replaceAll(" or ", " ");

        while (originStr.indexOf("  ") > 0) {
            originStr = originStr.replaceAll("  ", " ");
        }
        return originStr;
    }

    /**
     * 改变field值，遇到大写字母的，改成用_连接对应的小写字母
     * 保证与数据库中表结果字段一致
     *
     * @param oldField     传入字段
     * @param isFieldValue 是否是域的值
     * @return
     */
    protected String changeField(String oldField, boolean isFieldValue) {
        String field = "";
        for (int ni = 0; ni < oldField.length(); ni++) {
            char c = oldField.charAt(ni);
            if ((c >= 'A') && (c <= 'Z')) {
                field += "_" + String.valueOf(c).toLowerCase();
            } else {
                field += c + "";
            }
        }
        if (!isFieldValue) {
            if (field.indexOf(".") < 0) {
                field = MAJOR_DATA_VIEW_NAME + "." + field;
            }
        }
        return field;
    }

    /**
     * 对配置的列信息别名处理
     * 以列名全小写作为键，别名作为值
     * 生成Map数据对象
     *
     * @return
     */
    protected Map<String, String> getColumnAliasMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (ViewColumn viewColumn : this.getAllViewColumns()) {
            map.put(viewColumn.getColumnAlias().toLowerCase(), viewColumn.getColumnAlias());
        }
        return map;
    }

    /**
     * 获取预处理查询结果
     *
     * @return
     */
    protected PreQueryResult getPreQueryResult() {

        PreQueryResult result = new PreQueryResult();

        // 判断当前用户是否有公共库部门实体信息
        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (commonUnits == null || commonUnits.size() == 0) {
            result.setQueryValid(false);
        } else {
            CommonUnit commonUnit = commonUnits.get(0);
            result.setCommonUnit(commonUnit);
            BusinessManage businessManage = unitApiFacade.getBusinessManage(ExchangeConfig.EXCHANGE_BUSINESS_TYPE,
                    commonUnit.getId(), SpringSecurityUtils.getCurrentUserId());
            if (!businessManage.isBusinessSender() && !businessManage.isBusinessManager()) {
                result.setQueryValid(false);
            }
        }

        return result;
    }

    /**
     * 对查询结果的处理
     *
     * @param queryItems
     * @return
     */
    protected List<QueryItem> processQueryResult(List<Map<String, Object>> queryItems) {
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
     * 预查询结果类
     */
    class PreQueryResult {

        private boolean isQueryValid = true; // 预处理是否是合理的

        private CommonUnit commonUnit = null;

        /**
         * @return the isQueryValid
         */
        public boolean isQueryValid() {
            return isQueryValid;
        }

        /**
         * @param isQueryValid 要设置的isQueryValid
         */
        public void setQueryValid(boolean isQueryValid) {
            this.isQueryValid = isQueryValid;
        }

        /**
         * @return the commonUnit
         */
        public CommonUnit getCommonUnit() {
            return commonUnit;
        }

        /**
         * @param commonUnit 要设置的commonUnit
         */
        public void setCommonUnit(CommonUnit commonUnit) {
            this.commonUnit = commonUnit;
        }

    }
}
