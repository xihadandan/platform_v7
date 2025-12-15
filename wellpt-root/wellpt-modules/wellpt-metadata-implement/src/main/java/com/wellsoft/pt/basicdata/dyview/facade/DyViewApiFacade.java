package com.wellsoft.pt.basicdata.dyview.facade;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.dyview.bean.ViewAndDataBean;
import com.wellsoft.pt.basicdata.dyview.bean.ViewDefinitionBean;
import com.wellsoft.pt.basicdata.dyview.entity.*;
import com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService;
import com.wellsoft.pt.basicdata.dyview.support.DateUtil;
import com.wellsoft.pt.basicdata.dyview.support.DyViewQueryInfo;
import com.wellsoft.pt.basicdata.dyview.support.DyviewConfig;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-20.1	Administrator		2013-5-20		Create
 * </pre>
 * @date 2013-5-20
 */
@Component
public class DyViewApiFacade extends AbstractApiFacade {
    @Autowired
    private ViewDefinitionService viewDefinitionService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 根据视图uuid获得所有的列定义
     *
     * @param viewUuid
     * @return
     */
    public Set<ColumnDefinition> getColumnDefinitions(String viewUuid) {
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        Set<ColumnDefinition> columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        return columnDefinitions;
    }

    public long getViewDataCount(@RequestParam("viewUuid") String viewUuid, PagingInfo page,
                                 @RequestParam(value = "count", required = false) String count) {

        DyViewQueryInfo dyViewQueryInfo = new DyViewQueryInfo();
        /************************获取视图的定义bean开始***********************/
        ViewAndDataBean viewAndDataBean = new ViewAndDataBean();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        if (viewUuid != null && viewUuid != "") {
            viewAndDataBean.setViewUuid(viewUuid);
            viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        } else {
            viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        }
        /************************获取视图的定义信息结束***********************/
        int datascope = viewDefinitionBean.getDataScope();
        //获取视图对应的表uuid
        String tableUuid = viewDefinitionBean.getFormuuid();
        //获取数据源范围下的表名
        String tableName = viewDefinitionBean.getTableDefinitionName();
        //启用已读未读属性
        String readKey = viewDefinitionBean.getReadKey();
        if (readKey == null || (readKey != null && readKey.equals(""))) {
            readKey = "uuid";
        }
        //复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionBean.getCheckKey()) ? "uuid" : viewDefinitionBean
                .getCheckKey();
        //获取视图的默认搜索条件
        String defaultCondition = viewDefinitionBean.getDefaultCondition();
        if (defaultCondition == null) {
            defaultCondition = "";
        }
        /*******************************解析视图的默认搜索条件中特定的变量********************************/
        while (defaultCondition.contains("  ")) {
            defaultCondition = defaultCondition.replaceAll("  ", " ");
        }
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserName}",
                SpringSecurityUtils.getCurrentUserName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentLoginName}",
                SpringSecurityUtils.getCurrentLoginName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserId}",
                SpringSecurityUtils.getCurrentUserId());
        defaultCondition = defaultCondition.replace("=${nowDate}", " between :preDate and :nextDate").replace(
                "= ${nowDate}", " between :preDate and :nextDate");
        //解析默认条件里定义的参数，来源url链接
        String parmStr = "";
        String htmlParmStr = "";
        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        //获取视图的分页信息
        PageDefinition pageDefinition = new PageDefinition();
        if (0 == (page.getCurrentPage())) {
            page.setCurrentPage(1);
        }

        //??
        if (count == null) {
            pageDefinition = viewDefinitionBean.getPageDefinitions();
        } else {
            pageDefinition.setIsPaging(false);
        }
        //视图是否分页
        viewDefinitionBean.setPageAble(viewDefinitionBean.getPageDefinitions().getIsPaging());
        //获取视图的行样式信息
        Set<ColumnCssDefinition> columnCssDefinitions = viewDefinitionBean.getColumnCssDefinition();
        //获取视图的查询定义信息
        SelectDefinition selectDefinition = viewDefinitionBean.getSelectDefinitions();
        //获取视图的自定义按钮信息
        Set<CustomButton> buttonBeans = viewDefinitionBean.getCustomButtons();
        //获取视图的角色权限信息
        String roleType = viewDefinitionBean.getRoleType();
        //获取视图的角色类型
        String roleValue = viewDefinitionBean.getRoleValue();
        //查询模板
        StringBuilder selectTemplate = new StringBuilder();
        //按条件查询模板
        StringBuilder condSelect = new StringBuilder();
        //按关键字查询模板
        StringBuilder keySelect = new StringBuilder();
        if (selectDefinition.getSelectShow() == true) {
            selectTemplate.append("<table class='view_search'>");
            if (selectDefinition.getForCondition() == true) {
                //				condSelect.append("<tr><td width='30%'>按条件查询</td>");
                Set<ConditionType> conditionTypes = selectDefinition.getConditionType();
                int flag = 0;
                //按条件查询的前台页面展示
                for (ConditionType conditionType : conditionTypes) {
                    String ShowName = conditionType.getName(); //备选项的显示值
                    String ShowValue = conditionType.getConditionValue();//备选项的真实值
                    String ConditionName = conditionType.getConditionName();//条件名称
                    String appointColumn = conditionType.getAppointColumn();//条件对应的列
                    String appointColumnType = conditionType.getAppointColumnType();
                    if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
                        if ("1".equals(appointColumnType)) {
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left'>" + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            String[] ShowNames = ShowName.split(";");
                            String[] ShowValues = ShowValue.split(";");
                            for (int i = 0; i < ShowNames.length; i++) {
                                condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i
                                        + "_" + conditionType.getUuid() + "_cond" + "' value='" + ShowValues[i]
                                        + "' appointColumn='" + appointColumn + "' appointColumnType = '"
                                        + appointColumnType + "'><a>" + ShowNames[i] + "</a></div></td>");
                            }

                            condSelect.append("</tr>");
                        } else if ("2".equals(appointColumnType) || "DATE".equals(appointColumnType)) {
                            DateUtil dateUtil = new DateUtil();
                            String today = dateUtil.getPreDate(0);
                            String yesterday = dateUtil.getPreDate(-1);
                            //获取上周星期一的日期
                            String lastWeekFirstDay = dateUtil.getPreviousMonday();
                            //获取上周星期日的日期
                            String lastWeekSunday = dateUtil.getSunday();
                            //获取上个月的第一天
                            String lastMonthFirstDay = dateUtil.getLastMonthFirstDay();
                            //获取上个月的最后一天
                            String lastMonthLastDay = dateUtil.getLastMonthLastDay();
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left' " + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            condSelect.append("<td class='view_search_right'><div id='allDay'><a>不限</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='today' today='" + today
                                    + "' appointColumn='" + appointColumn + "' ><a>今天</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='yesterday' yesterday='"
                                    + yesterday + "' appointColumn='" + appointColumn + "'><a>昨天</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='lastWeek' lastWeekFirstDay='"
                                    + lastWeekFirstDay + "' lastWeekSunday='" + lastWeekSunday + "' appointColumn='"
                                    + appointColumn + "'><a>上星期</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='lastMonth' lastMonthFirstDay='"
                                    + lastMonthFirstDay + "' lastMonthLastDay='" + lastMonthLastDay
                                    + "' appointColumn='" + appointColumn + "'><a>上个月</a></div></td>");
                            condSelect
                                    .append("<td  class='view_search_right'><div id='chooseDate'><a>选择日期</a></div><div id='dateInput' style='display:none'><input type='text' id='datepicker' appointColumn='"
                                            + appointColumn + "'></div></td></tr>");
                        } else if ("3".equals(appointColumnType)) {
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left'>" + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            condSelect.append("<td class='view_search_right'><div><a>不限</a></div></td>");
                            condSelect
                                    .append("<td class='view_search_right'><div><input type='text' id='' name=''/></div></td>");
                            condSelect.append("<td class='view_search_right'><div>'--'</div></td>");
                            condSelect
                                    .append("<td class='view_search_right'><div><input type='text' id='' name='' appointColumn='"
                                            + appointColumn + "'/></div></td></tr>");
                        }
                    } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY
                            || datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
                        if ("STRING".equals(appointColumnType)) {
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left'>" + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            String[] ShowNames = ShowName.split(";");
                            String[] ShowValues = ShowValue.split(";");
                            for (int i = 0; i < ShowNames.length; i++) {
                                condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i
                                        + "_" + conditionType.getUuid() + "_cond" + "' value='" + ShowValues[i]
                                        + "' appointColumn='" + appointColumn + "' appointColumnType = '"
                                        + appointColumnType + "'><a>" + ShowNames[i] + "</a></div></td>");
                            }

                            condSelect.append("</tr>");
                        } else if ("DATE".equals(appointColumnType)) {
                            DateUtil dateUtil = new DateUtil();
                            String today = dateUtil.getPreDate(0);
                            String yesterday = dateUtil.getPreDate(-1);
                            //获取上周星期一的日期
                            String lastWeekFirstDay = dateUtil.getPreviousMonday();
                            //获取上周星期日的日期
                            String lastWeekSunday = dateUtil.getSunday();
                            //获取上个月的第一天
                            String lastMonthFirstDay = dateUtil.getLastMonthFirstDay();
                            //获取上个月的最后一天
                            String lastMonthLastDay = dateUtil.getLastMonthLastDay();
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left'>" + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            condSelect.append("<td class='view_search_right'><div id='allDay'><a>不限</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='today' today='" + today
                                    + "' appointColumn='" + appointColumn + "' ><a>今天</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='yesterday' yesterday='"
                                    + yesterday + "' appointColumn='" + appointColumn + "'><a>昨天</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='lastWeek' lastWeekFirstDay='"
                                    + lastWeekFirstDay + "' lastWeekSunday='" + lastWeekSunday + "' appointColumn='"
                                    + appointColumn + "'><a>上星期</a></div></td>");
                            condSelect.append("<td class='view_search_right'><div id='lastMonth' lastMonthFirstDay='"
                                    + lastMonthFirstDay + "' lastMonthLastDay='" + lastMonthLastDay
                                    + "' appointColumn='" + appointColumn + "'><a>上个月</a></div></td>");
                            condSelect
                                    .append("<td class='view_search_right'><div id='chooseDate'><a>选择日期</a></div><div id='dateInput' style='display:none'><input type='text' id='datepicker' appointColumn='"
                                            + appointColumn + "'></div></td></tr>");
                        } else {
                            if (flag % 2 == 1) {
                                condSelect.append("<tr class='view_seach_tr_odd'>");
                            } else {
                                condSelect.append("<tr class='view_seach_tr_even'>");
                            }
                            condSelect.append("<td class='view_search_left'>" + ConditionName
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
                            String[] ShowNames = ShowName.split(";");
                            String[] ShowValues = ShowValue.split(";");
                            for (int i = 0; i < ShowNames.length; i++) {
                                condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i
                                        + "_" + conditionType.getUuid() + "_cond" + "' value='" + ShowValues[i]
                                        + "' appointColumn='" + appointColumn + "' appointColumnType = '"
                                        + appointColumnType + "'><a>" + ShowNames[i] + "</a></div></td>");
                            }
                        }
                    }
                    flag++;
                }
                condSelect.append("</tr>");
            }
        }
        selectTemplate.append(condSelect.toString());
        selectTemplate.append(keySelect.toString());
        selectTemplate.append("</table>");
        //获取视图下所有的列字段的数据
        Set<ColumnDefinition> columnDefinitions = new HashSet<ColumnDefinition>();
        columnDefinitions = viewDefinitionBean.getColumnDefinitions();

        //获取表的总记录数
        Long totalCount = null;
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            totalCount = dyFormApiFacade.queryTotalCountOfFormDataOfMainform(tableName, defaultCondition);
            //如果分页存在,设置分页信息
            if (pageDefinition.getIsPaging() == true) {
                page.setTotalCount(totalCount);
                page.setPageSize(pageDefinition.getPageNum());
            }
        } else if ((datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY || datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE)
                && pageDefinition.getIsPaging() == true) {
            //如果分页存在,设置分页信息
            if (pageDefinition.getPageNum() == null) {
                pageDefinition.setPageNum(10);
            }
            page.setPageSize(pageDefinition.getPageNum());
        }
        //获取视图自定义按钮
        Set<CustomButton> viewbutton = viewDefinitionBean.getCustomButtons();
        //点击事件跳转的url
        String lineType = viewDefinitionBean.getLineType();
        StringBuilder buttonTemplate = new StringBuilder();
        String buttonGroup = "";
        long queryItemCount = 0;
        //获取列标题模板
        Map<String, ColumnDefinition> columnFields = new LinkedHashMap<String, ColumnDefinition>();
        boolean showTitle = viewDefinitionBean.getShowTitle();
        String templateAll = "";

        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();

            viewAndDataBean.setData(queryItems);

        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData2(tableUuid, defaultCondition, columnDefinitions, rowIdKey,
                    roleType, roleValue, pageDefinition, dyViewQueryInfo, count);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();

            //处理clob对象
            StringBuilder sb_ = new StringBuilder();
            sb_.append("@");
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                if (columnDefinition.getColumnDataType() != null && "CLOB".equals(columnDefinition.getColumnDataType())) {
                    String columnAliase_ = columnDefinition.getColumnAliase();
                    if (columnAliase_.indexOf("_") > -1) {
                        int atTemp_ = columnAliase_.indexOf("_");
                        columnAliase_ = columnAliase_.substring(0, atTemp_)
                                + columnAliase_.substring(atTemp_ + 1, atTemp_ + 2).toUpperCase()
                                + columnAliase_.substring(atTemp_ + 2);
                    }
                    sb_.append(columnAliase_ + "@");
                }
            }
        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData3(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo, count);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();
            viewAndDataBean.setData(queryItems);
        }

        viewAndDataBean.setViewDefinitionBean(viewDefinitionBean);
        List srcList = new ArrayList();
        if (viewDefinitionBean.getJsSrc() == null) {
            viewDefinitionBean.setJsSrc("");
        }
        String[] srcTemp = viewDefinitionBean.getJsSrc().split(",");
        for (int j = 0; j < srcTemp.length; j++) {
            Map srcMap = new HashMap();
            if (!srcTemp[j].equals("")) {
                srcMap.put("src", srcTemp[j]);
                srcList.add(srcMap);
            }
        }
        return queryItemCount;
    }

    /**
     * 获得视图按钮
     *
     * @param moudleId
     * @return
     */
    public List<Map<String, Object>> getViewButtons(String viewUuid, HttpServletRequest request) {
        List<Map<String, Object>> list_ = new ArrayList<Map<String, Object>>();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        Set<CustomButton> viewbutton = viewDefinitionBean.getCustomButtons();
        for (CustomButton cbb : viewbutton) {
            Map<String, Object> map_ = new HashMap<String, Object>();
            String buttonName = cbb.getName();
            String buttonCode = cbb.getCode();
            String buttonFunction = cbb.getJsContent();
            map_.put("name", buttonName);
            map_.put("code", buttonCode);
            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
            map_.put("function", buttonFunction);
            list_.add(map_);
        }
        return list_;
    }

    /**
     * 通过字段名称key及对应的值查询视图数据（当key为null默认uuid列）
     *
     * @param val
     * @param key
     * @return
     */
    public List<QueryItem> getViewDataByKey(String val, String key) {
        return viewDefinitionService.getViewDataByKey(val, key);
    }

    /**
     * 根据视图的ID获取视图的uuid
     *
     * @param viewId
     * @return
     */
    public String getViewUuid(String viewId) {
        ViewDefinition viewDefinition = viewDefinitionService.getByViewId(viewId);
        return viewDefinition.getUuid();
    }
}
