/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.dyview.bean.ViewAndDataBean;
import com.wellsoft.pt.basicdata.dyview.bean.ViewDefinitionBean;
import com.wellsoft.pt.basicdata.dyview.entity.*;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource;
import com.wellsoft.pt.basicdata.dyview.service.GetViewDataService;
import com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService;
import com.wellsoft.pt.basicdata.dyview.support.DateUtil;
import com.wellsoft.pt.basicdata.dyview.support.DyViewQueryInfo;
import com.wellsoft.pt.basicdata.dyview.support.DyviewConfig;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import ognl.Ognl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.sql.Clob;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 视图自定义的控制器
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Controller
@RequestMapping("/basicdata/dyview")
public class DyViewController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DyViewController.class);

    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ViewDefinitionService viewDefinitionService;
    @Autowired
    private GetViewDataService getViewDataService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired(required = true)
    private Map<String, ViewDataSource> viewDataSourceMap;

    /**
     * 获取路径
     *
     * @param request
     * @return
     */
    private static String getRequestPath(HttpServletRequest request) {
        String ctx = request.getContextPath();
        return "/".equals(ctx) ? "" : ctx;
    }

    /**
     * 视图自定义入口地址
     *
     * @return
     */
    @RequestMapping("")
    public String viewDefinitionIndex(Model model) {
        //视图自定的编号(存放在数据字典中)
        String code = "003006";
        List<Resource> resources = securityApiFacade.getDynamicButtonResourcesByCode(code);
        model.addAttribute("resources", resources);
        return forward("/basicdata/dyview/view_definition");
    }

/*	@RequestMapping(value = "/get_select_column", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<DyformFieldDefinition> getSelectColumn(@RequestParam("formUuid") String formUuid) {
		return getViewDataService.getFieldByForm(formUuid);
	}*/

    @RequestMapping(value = "/column_definition_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<DyformFieldDefinition> getColumnList(@RequestParam("formUuid") String formUuid) {
        List<DyformFieldDefinition> fieldDefinitions = dyFormApiFacade.getFormDefinition(formUuid).doGetFieldDefintions();
        return fieldDefinitions;
    }

    @RequestMapping(value = "/get_select_column2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SystemTableAttribute> getSelectColumn2(@RequestParam("formUuid") String formUuid) {
        return getViewDataService.getSystemTableColumns(formUuid);
    }

    @RequestMapping(value = "/get_select_column3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ViewColumn> getSelectColumn3(@RequestParam("formUuid") String formUuid) {
        return getViewDataService.getViewColumns(formUuid);
    }

    @RequestMapping(value = "/getDataDictionaryByCode")
    @ResponseBody
    public List<CdDataDictionaryItemDto> getDataDictionaryByCode(@RequestParam(value = "code", required = false) String code) {
        List<CdDataDictionaryItemDto> ddList = basicDataApiFacade.getDataDictionariesByType(code);
        return ddList;
    }

    /**
     * 获取弹出框的中的数据
     *
     * @param formUuid
     * @param fieldName
     * @param currentPage
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getSelectData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JqGridQueryData getSelectData(@RequestParam("tableType") String tableType,
                                         @RequestParam("formUuid") String formUuid, @RequestParam("fieldName") String fieldName,
                                         @RequestParam("page") int currentPage, @RequestParam("rows") int pageSize,
                                         @RequestParam("defaultCondition") String defaultCondition) throws Exception {
        PagingInfo page = new PagingInfo();
        page.setCurrentPage(currentPage);
        page.setPageSize(pageSize);
        JqGridQueryData data = new JqGridQueryData();
        if (tableType.equals("1")) {
            List<QueryItem> queryItems = viewDefinitionService.getSelectData(formUuid, fieldName, page);
            data.setDataList(queryItems);
            data.setCurrentPage(page.getCurrentPage());
            data.setTotalPages((long) Math.ceil(queryItems.size() / page.getPageSize()));
        } else if (tableType.equals("2")) {
            List<Map<String, Object>> queryItems = viewDefinitionService.getSelectData2(formUuid, fieldName, page);
            data.setDataList(queryItems);
            data.setCurrentPage(page.getCurrentPage());
            data.setTotalPages((long) Math.ceil(queryItems.size() / page.getPageSize()));
        } else if (tableType.equals("3")) {
            List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
            Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(formUuid)
                    .getAllViewColumns(), "columnName");
            ViewColumn v = map.get(fieldName);
            viewColumns.add(v);
            List<QueryItem> data_ = viewDataSourceMap.get(formUuid).query(viewColumns, defaultCondition, new HashMap(),
                    "", page);
            List<QueryItem> newdata = new ArrayList<QueryItem>();
            for (QueryItem q : data_) {
                QueryItem newq = new QueryItem();
                newq.put("value", q.get(v.getColumnAlias()));
                newdata.add(newq);
            }
            data.setDataList(newdata);
            data.setCurrentPage(page.getCurrentPage());
            data.setTotalPages((long) Math.ceil(newdata.size() / page.getPageSize()));
        }
        return data;
    }

    @RequestMapping(value = "/view/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewDataByViewId(@PathVariable(value = "id") String id, HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        ViewDefinition viewDefinition = this.viewDefinitionService.getByViewId(id);
        redirectAttributes.addAttribute("viewUuid", viewDefinition.getUuid());
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String paramName = e.nextElement();
            redirectAttributes.addAttribute(paramName, request.getParameter(paramName));
        }
        return redirect("/basicdata/dyview/view_show");
    }

    /**
     * 视图的控制层调用入口
     *
     * @param model
     * @param viewUuid
     * @param dyViewQueryInfo
     * @param expandParams
     * @param openBy
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/view_show", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewData(Model model, @RequestParam(value = "viewUuid", required = false) String viewUuid,
                              @RequestParam(value = "count", required = false) String count,
                              @RequestParam(value = "viewName", required = false) String viewName,
                              @RequestParam(value = "openBy", required = false) String openBy,
                              @RequestParam(value = "relationDataDefiantion", required = false) String relationDataDefiantion,
                              PagingInfo page, HttpServletRequest request) throws Exception {
        DyViewQueryInfo dyViewQueryInfo = new DyViewQueryInfo();
        /************************获取视图的定义bean开始***********************/
        ViewAndDataBean viewAndDataBean = new ViewAndDataBean();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        if (viewUuid != null && viewUuid != "") {
            viewAndDataBean.setViewUuid(viewUuid);
            viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        } else {
            ViewDefinition viewDefinition = viewDefinitionService.getByViewId(viewName);
            viewUuid = viewDefinition.getUuid();
            viewAndDataBean.setViewUuid(viewUuid);
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
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentId}",
                SpringSecurityUtils.getCurrentUserDepartmentId());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentName}",
                SpringSecurityUtils.getCurrentUserDepartmentName());
        defaultCondition = defaultCondition.replace("=${nowDate}", " between :preDate and :nextDate").replace(
                "= ${nowDate}", " between :preDate and :nextDate");
        //解析默认条件里定义的参数，来源url链接
        String parmStr = "";
        String htmlParmStr = "";
        Map<String, String[]> pMap = request.getParameterMap();
        for (String pkey : pMap.keySet()) {
            String pvalue = pMap.get(pkey)[0];
            if (defaultCondition != null) {
                defaultCondition = defaultCondition.replace("${" + pkey + "}", pvalue);
            }
            if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                parmStr += "&" + pkey + "=" + pvalue;
                htmlParmStr += "<input type='hidden' id='view_param_" + pkey + "' value='" + pvalue + "' />";
            }
        }
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
        //获取有权限的自定义按钮
        getViewDataService.setCustomButtonRights(viewDefinitionBean, buttonBeans);
        //查询模板
        StringBuilder selectTemplate = new StringBuilder();
        //按条件查询模板
        StringBuilder condSelect = new StringBuilder();
        //按关键字查询模板
        StringBuilder keySelect = new StringBuilder();
        if (selectDefinition != null && selectDefinition.getSelectShow() != null
                && selectDefinition.getSelectShow() == true) {
            selectTemplate.append("<table class='view_search'>");
            if (selectDefinition.getForCondition() == true) {
                //				condSelect.append("<tr><td width='30%'>按条件查询</td>");
                Set<ConditionType> conditionTypes = selectDefinition.getConditionType();
                model.addAttribute("conditionTypes", conditionTypes);
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
        //		if (relationDataDefiantion != null && !relationDataDefiantion.equals("")) {//关联数据——指定字段显示
        //			Set<ColumnDefinitionNew> columnDefinitionsTemp = viewDefinitionBean.getColumnDefinitions();
        //			for (ColumnDefinitionNew columnDefinition : columnDefinitionsTemp) {
        //				if (columnDefinition.getColumnAliase() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getColumnAliase()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				} else if (columnDefinition.getFieldName() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getFieldName()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				}
        //			}
        //		} else {//关联数据——配置的字段全部显示
        columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        //		}

        //获取表的总记录数
        Long totalCount = null;
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            totalCount = dyFormApiFacade.queryTotalCountOfFormDataOfMainform(tableName, "");
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
        StringBuilder buttonTemplate2 = new StringBuilder();
        String buttonGroup = "";
        String buttonGroup2 = "";
        if (viewbutton.size() > 0 || selectDefinition.getForKeySelect() != null
                && selectDefinition.getForKeySelect() == true) {
            if (viewDefinitionBean.getButtonPlace() != null && viewDefinitionBean.getButtonPlace() == true) {
                buttonTemplate2.append("<div class='view_tool_bottom'>");
                SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
                if (viewbutton.size() > 0) {
                    buttonTemplate2.append("<div class='customButton customButton_top'>");
                    for (CustomButton cbb : viewbutton) {
                        //加按钮权限SecurityApiFacade.isGranted
                        if (securityAuditFacadeService.isGranted(cbb.getCode())) {
                            if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                    && buttonGroup2.indexOf(cbb.getButtonGroup()) < 0) {
                                buttonGroup2 += "," + cbb.getButtonGroup();
                            }
                            String place = cbb.getPlace();
                            if (place != null && place.indexOf("头部") > -1 && cbb.getButtonGroup() == null) {
                                String buttonFunction = cbb.getJsContent();
                                String buttonName = cbb.getName();
                                String buttonCode = cbb.getCode();
                                if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                                    buttonFunction = "";
                                }
                                buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                buttonTemplate2.append("<button place=\"place_top\" type=\"button\" value=\""
                                        + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName
                                        + "</button>");
                            }
                        }
                    }
                    buttonTemplate2.append("</div>");
                }

                buttonGroup2 = buttonGroup2.replaceFirst(",", "");
                String[] buttonGroupArray2 = buttonGroup2.split(",");
                if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                    buttonTemplate2.append("<div class='customButton_group'>");
                }
                for (int j = 0; j < buttonGroupArray2.length; j++) {
                    if (!"".equals(buttonGroupArray2[j])) {
                        buttonTemplate2.append("<div class='customButton_group_item'>");
                        buttonTemplate2
                                .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                        + buttonGroupArray2[j] + "</div><div class='select_icon'></div></div>");
                        buttonTemplate2.append("<div class='customButton_group_buttons_bottom'>");
                        for (CustomButton cbb2 : viewbutton) {
                            if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                    && cbb2.getButtonGroup().equals(buttonGroupArray2[j])) {
                                String buttonFunction = cbb2.getJsContent();
                                String buttonName = cbb2.getName();
                                String buttonCode = cbb2.getCode();
                                buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                buttonTemplate2
                                        .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                                + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName);
                                buttonTemplate2.append("</div>");
                            }
                        }
                        buttonTemplate2.append("</div>");
                        buttonTemplate2.append("</div>");
                    }
                }
                if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                    buttonTemplate2.append("</div>");
                }
            }

            buttonTemplate.append("<div class='view_tool2'>");
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
            if (viewbutton.size() > 0) {
                int i = 0;
                for (CustomButton cbb : viewbutton) {
                    //加按钮权限SecurityApiFacade.isGranted
                    if (securityAuditFacadeService.isGranted(cbb.getCode()) && cbb.getPlace() != null
                            && cbb.getPlace().indexOf("头部") > -1) {
                        if (i == 0) {
                            buttonTemplate.append("<div class='customButton customButton_top'>");
                            i++;
                        }
                        if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                && buttonGroup.indexOf(cbb.getButtonGroup()) < 0) {
                            buttonGroup += "," + cbb.getButtonGroup();
                        }
                        String place = cbb.getPlace();
                        if (place != null && place.indexOf("头部") > -1 && cbb.getButtonGroup() == null) {
                            String buttonFunction = cbb.getJsContent();
                            String buttonName = cbb.getName();
                            String buttonCode = cbb.getCode();
                            if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                                buttonFunction = "";
                            }
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate.append("<button place=\"place_top\" type=\"button\" value=\"" + buttonCode
                                    + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                        }
                    }

                }
                if (i == 1) {
                    buttonTemplate.append("</div>");
                }
            }

            buttonGroup = buttonGroup.replaceFirst(",", "");
            String[] buttonGroupArray = buttonGroup.split(",");
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("<div class='customButton_group'>");
            }
            for (int j = 0; j < buttonGroupArray.length; j++) {
                if (!"".equals(buttonGroupArray[j])) {
                    buttonTemplate.append("<div class='customButton_group_item'>");
                    buttonTemplate
                            .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                    + buttonGroupArray[j] + "</div><div class='select_icon'></div></div>");
                    buttonTemplate.append("<div class='customButton_group_buttons'>");
                    for (CustomButton cbb2 : viewbutton) {
                        if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                && cbb2.getButtonGroup().equals(buttonGroupArray[j])) {
                            String buttonFunction = cbb2.getJsContent();
                            String buttonName = cbb2.getName();
                            String buttonCode = cbb2.getCode();
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate
                                    .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                            + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName);
                            buttonTemplate.append("</div>");
                        }
                    }
                    buttonTemplate.append("</div>");
                    buttonTemplate.append("</div>");
                }
            }
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("</div>");
            }

            if (selectDefinition.getForTimeSolt() != null && selectDefinition.getForTimeSolt() == true
                    && !StringUtils.isBlank(selectDefinition.getSearchField())) {
                buttonTemplate.append("<div class='view_timeSolt_div'>");
                buttonTemplate.append("<select class='searchField'>");
                String[] searchFieldArray = selectDefinition.getSearchField().split(";");
                for (int ij = 0; ij < searchFieldArray.length; ij++) {
                    buttonTemplate.append("<option value='" + searchFieldArray[ij].split(":")[1] + "'>"
                            + searchFieldArray[ij].split(":")[0] + "</option>");
                }
                buttonTemplate.append("</select>");
                buttonTemplate
                        .append("<input type='text' value='开始时间' name='beginTime' id='beginTime' autocomplete='off' class='Wdate'/>");
                buttonTemplate.append("<span class='toLine'>--</span>");
                buttonTemplate
                        .append("<input type='text' value='结束时间' name='endTime' id='endTime' autocomplete='off' class='Wdate'/>");
                buttonTemplate.append("</div>");
            }

            if (BooleanUtils.isTrue(selectDefinition.getForKeySelect())
                    && BooleanUtils.isTrue(selectDefinition.getSelectShow())
                    && selectDefinition.getVagueKeySelect() != null && selectDefinition.getExactKeySelect() != null) {
                if (selectDefinition.getVagueKeySelect() == true && selectDefinition.getExactKeySelect() == false) {
                    buttonTemplate
                            .append("<div class='view_keyword_div'><input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button></div>");
                } else if (selectDefinition.getExactKeySelect() == true
                        && selectDefinition.getVagueKeySelect() == false) {
                    buttonTemplate.append("<div class='selectKeyTableDiv'>");
                    String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' >";
                    Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                    int i = 0;
                    for (ExactKeySelectCol ekc : exactKeySelectCols) {
                        String keyName = ekc.getKeyName();
                        String keyValue = ekc.getKeyValue();
                        if (i % 2 != 0) {
                            tableHtml += "<td>" + keyName + ":</td><td>"
                                    + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  field='"
                                    + keyValue + "'></td></tr>";
                        } else {
                            if (i % 4 == 0) {
                                tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            } else {
                                tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            }
                        }
                        i++;
                    }
                    if (i == exactKeySelectCols.size()) {
                        if (exactKeySelectCols.size() % 2 != 0) {
                            tableHtml += "<td></td><td></td>";
                        }
                        tableHtml += "</tr>";
                    }
                    tableHtml += "</table>";
                    buttonTemplate.append(tableHtml);
                    buttonTemplate
                            .append("<button id='keySelect' type='button' style='margin: 5px;float: right;'>查询</button></div>");
                } else if (selectDefinition.getVagueKeySelect() == true && selectDefinition.getExactKeySelect() == true) {
                    buttonTemplate.append("<div class='selectKeyTableDiv'>");
                    String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' style='display:none'>";
                    Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                    int i = 0;
                    for (ExactKeySelectCol ekc : exactKeySelectCols) {
                        String keyName = ekc.getKeyName();
                        String keyValue = ekc.getKeyValue();
                        if (i % 2 != 0) {
                            tableHtml += "<td>" + keyName + ":</td><td>"
                                    + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  field='"
                                    + keyValue + "'></td></tr>";
                        } else {
                            if (i % 4 == 0) {
                                tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            } else {
                                tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            }
                        }
                        i++;
                    }
                    if (i == exactKeySelectCols.size()) {
                        if (exactKeySelectCols.size() % 2 != 0) {
                            tableHtml += "<td></td><td></td>";
                        }
                        tableHtml += "</tr>";
                    }
                    tableHtml += "</table></div>";
                    buttonTemplate.append(tableHtml);
                    buttonTemplate
                            .append("<div class='view_keyword_div'><input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button>");
                    buttonTemplate.append("<button id='showButton' type='button'>↑</button>");
                }
            }
            //			buttonTemplate2.append("</div>");
            buttonTemplate.append("</div>");
        }
        long queryItemCount = 0;
        //获取列标题模板
        Map<String, ColumnDefinition> columnFields = new LinkedHashMap<String, ColumnDefinition>();
        boolean showTitle = viewDefinitionBean.getShowTitle();
        String titleSource = getTitleSource(showTitle, viewDefinitionBean.getShowCheckBox() == null ? false
                : viewDefinitionBean.getShowCheckBox(), columnDefinitions, columnFields, datascope);
        String templateAll = "";

        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            viewAndDataBean.setData(queryItems);

        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData2(tableUuid, defaultCondition, columnDefinitions, rowIdKey,
                    roleType, roleValue, pageDefinition, dyViewQueryInfo, count);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
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
            if (!sb_.toString().equals("@")) {
                for (QueryItem queryItem : queryItems) {
                    for (String key : queryItem.keySet()) {
                        if (sb_.toString().toLowerCase().indexOf("@" + key.toLowerCase() + "@") > -1
                                && (Clob) queryItem.get(key) != null) {
                            String temp_ = IOUtils.toString(((Clob) queryItem.get(key)).getCharacterStream());
                            queryItem.put(key, temp_);
                        }
                    }
                }
            }
            //获取行模板
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            //						viewAndDataBean.setData(queryItems);
        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
            dyViewQueryInfo.setPageInfo(page);
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData3(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo, count);
            queryItemCount = dyViewQueryInfo.getPageInfo().getTotalCount();
            viewAndDataBean.setData(queryItems);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
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
        //			model.addAttribute("mark", "clickSort");
        //			model.addAttribute("mark", "condSelect");
        //			model.addAttribute("mark", "keyWord");
        model.addAttribute("mark", "viewShow");
        model.addAttribute("selectTemplate", selectTemplate);
        model.addAttribute("condSelect", condSelect);
        model.addAttribute("keySelect", keySelect);
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinition);
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("buttonTemplate", buttonTemplate);
        model.addAttribute("buttonTemplate2", buttonTemplate2);
        model.addAttribute("template", templateAll);
        model.addAttribute("openBy", openBy);
        model.addAttribute("columnDefinitions", columnDefinitions);
        model.addAttribute("viewDefinitionBean", viewDefinitionBean);
        model.addAttribute("selectDefinition", selectDefinition);
        model.addAttribute("viewAndDataBean", viewAndDataBean);
        if (queryItemCount == -1) {
            queryItemCount = 0;
        }
        model.addAttribute("queryItemCount", queryItemCount);
        model.addAttribute("srcList", srcList);
        model.addAttribute("parmStr", parmStr);
        model.addAttribute("htmlParmStr", htmlParmStr);
        return forward("/basicdata/dyview/view_explain");
    }

    /**
     * 视图的控制层调用入口(直接预览后的分页控制层入口)
     *
     * @param model
     * @param viewUuid
     * @param dyViewQueryInfo
     * @param expandParams
     * @param openBy
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/view_show_forpage", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewDataForPage(Model model, @RequestBody DyViewQueryInfo dyViewQueryInfo,
                                     HttpServletRequest request) throws Exception {
        if (dyViewQueryInfo == null) {
            dyViewQueryInfo = new DyViewQueryInfo();
        }
        Map<String, String> expandParams = new HashMap<String, String>();
        if (dyViewQueryInfo.getExpandParams() != null) {
            expandParams = dyViewQueryInfo.getExpandParams();
        }
        String viewUuid = "";
        if (dyViewQueryInfo.getViewUuid() != null) {
            viewUuid = dyViewQueryInfo.getViewUuid();
        }

        //视图名称
        String viewName = dyViewQueryInfo.getViewName();
        String orderTitle = "";
        String orderbyArr = "";
        if (dyViewQueryInfo.getCondSelect() != null) {
            orderTitle = dyViewQueryInfo.getCondSelect().getOrderTitle();
            orderbyArr = dyViewQueryInfo.getCondSelect().getOrderbyArr();
        }

        //视图的条数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String count = expandParams.get("count");
        //关联数据所用的参数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String relationDataDefiantion = expandParams.get("relationDataDefiantion");
        /************************获取视图的定义bean开始***********************/
        ViewAndDataBean viewAndDataBean = new ViewAndDataBean();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        if (viewUuid != null && viewUuid != "") {
            viewAndDataBean.setViewUuid(viewUuid);
            viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        } else {
            ViewDefinition viewDefinition = viewDefinitionService.getByViewId(viewName);
            viewUuid = viewDefinition.getUuid();
            viewAndDataBean.setViewUuid(viewUuid);
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
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserName}",
                SpringSecurityUtils.getCurrentUserName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentLoginName}",
                SpringSecurityUtils.getCurrentLoginName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserId}",
                SpringSecurityUtils.getCurrentUserId());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentId}",
                SpringSecurityUtils.getCurrentUserDepartmentId());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentName}",
                SpringSecurityUtils.getCurrentUserDepartmentName());
        defaultCondition = StringUtils.replace(defaultCondition, "${nowDate}", "date(t.create_time) = :nowDate");
        //解析默认条件里定义的参数，来源url链接
        String parmStr = "";
        String htmlParmStr = "";
        Map<String, String> pMap = dyViewQueryInfo.getExpandParams();
        if (pMap != null) {
            for (String pkey : pMap.keySet()) {
                String pvalue = pMap.get(pkey);
                if (defaultCondition != null) {
                    defaultCondition = defaultCondition.replace("${" + pkey + "}", pvalue);
                }
                if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                    parmStr += "&" + pkey + "=" + pvalue;
                    htmlParmStr += "<input type='hidden' id='view_param_" + pkey + "' value='" + pvalue + "' />";
                }
            }
        }
        if (!StringUtils.isBlank(dyViewQueryInfo.getOpenBy())) {
            parmStr += "&openBy=" + dyViewQueryInfo.getOpenBy();
        }
        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        //获取视图的分页信息
        PageDefinition pageDefinition = new PageDefinition();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfo.getPageInfo() != null) {
            page = dyViewQueryInfo.getPageInfo();
        } else {
            if (0 == (page.getCurrentPage())) {
                page.setCurrentPage(1);
            }
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
        //获取有权限的自定义按钮
        getViewDataService.setCustomButtonRights(viewDefinitionBean, buttonBeans);
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
                model.addAttribute("conditionTypes", conditionTypes);
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
        //		if (relationDataDefiantion != null && !relationDataDefiantion.equals("")) {//关联数据——指定字段显示
        //			Set<ColumnDefinitionNew> columnDefinitionsTemp = viewDefinitionBean.getColumnDefinitions();
        //			for (ColumnDefinitionNew columnDefinition : columnDefinitionsTemp) {
        //				if (columnDefinition.getColumnAliase() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getColumnAliase()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				} else if (columnDefinition.getFieldName() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getFieldName()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				}
        //			}
        //		} else {//关联数据——配置的字段全部显示
        columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        //		}

        //获取表的总记录数
        Long totalCount = null;
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            totalCount = dyFormApiFacade.queryTotalCountOfFormDataOfMainform(tableName, "");
            //如果分页存在,设置分页信息
            if (pageDefinition.getIsPaging() == true) {
                page.setTotalCount(totalCount);
                if (page.getPageSize() == 0) {
                    page.setPageSize(pageDefinition.getPageNum());
                }
            }
        } else if ((datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY || datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE)
                && pageDefinition.getIsPaging() == true) {
            //如果分页存在,设置分页信息
            if (pageDefinition.getPageNum() == null) {
                pageDefinition.setPageNum(10);
            }
            if (page.getPageSize() == 0) {
                page.setPageSize(pageDefinition.getPageNum());
            }
        }
        //获取视图自定义按钮
        Set<CustomButton> viewbutton = viewDefinitionBean.getCustomButtons();
        //点击事件跳转的url
        String lineType = viewDefinitionBean.getLineType();
        StringBuilder buttonTemplate = new StringBuilder();
        StringBuilder buttonTemplate2 = new StringBuilder();
        String buttonGroup = "";
        String buttonGroup2 = "";
        if (viewbutton.size() > 0 || selectDefinition.getForKeySelect() == true) {
            buttonTemplate.append("<div class='view_tool2'>");
            if (viewbutton.size() > 0) {
                if (viewDefinitionBean.getButtonPlace() != null && viewDefinitionBean.getButtonPlace() == true) {
                    buttonTemplate2.append("<div class='view_tool_bottom'>");
                    if (viewbutton.size() > 0) {
                        int i = 0;
                        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
                        for (CustomButton cbb : viewbutton) {
                            //加按钮权限SecurityApiFacade.isGranted
                            if (securityAuditFacadeService.isGranted(cbb.getCode()) && cbb.getPlace() != null
                                    && cbb.getPlace().indexOf("头部") > -1) {
                                if (i == 0) {
                                    buttonTemplate2.append("<div class='customButton customButton_top'>");
                                    i++;
                                }
                                if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                        && buttonGroup2.indexOf(cbb.getButtonGroup()) < 0) {
                                    buttonGroup2 += "," + cbb.getButtonGroup();
                                }
                                String place = cbb.getPlace();
                                if (place != null && place.indexOf("头部") > -1 && cbb.getButtonGroup() == null) {
                                    String buttonFunction = cbb.getJsContent();
                                    String buttonName = cbb.getName();
                                    String buttonCode = cbb.getCode();
                                    if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                                        buttonFunction = "";
                                    }
                                    buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                    buttonTemplate2.append("<button place=\"place_top\" type=\"button\" value=\""
                                            + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName
                                            + "</button>");
                                }
                            }
                        }
                        if (i == 1) {
                            buttonTemplate2.append("</div>");
                        }
                    }

                    buttonGroup2 = buttonGroup2.replaceFirst(",", "");
                    String[] buttonGroupArray2 = buttonGroup2.split(",");
                    if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                        buttonTemplate2.append("<div class='customButton_group'>");
                    }
                    for (int j = 0; j < buttonGroupArray2.length; j++) {
                        if (!"".equals(buttonGroupArray2[j])) {
                            buttonTemplate2.append("<div class='customButton_group_item'>");
                            buttonTemplate2
                                    .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                            + buttonGroupArray2[j] + "</div><div class='select_icon'></div></div>");
                            buttonTemplate2.append("<div class='customButton_group_buttons_bottom'>");
                            for (CustomButton cbb2 : viewbutton) {
                                if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                        && cbb2.getButtonGroup().equals(buttonGroupArray2[j])) {
                                    String buttonFunction = cbb2.getJsContent();
                                    String buttonName = cbb2.getName();
                                    String buttonCode = cbb2.getCode();
                                    buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                    buttonTemplate2
                                            .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                                    + buttonCode
                                                    + "\"  onclick=\""
                                                    + buttonFunction
                                                    + "\">"
                                                    + buttonName);
                                    buttonTemplate2.append("</div>");
                                }
                            }
                            buttonTemplate2.append("</div>");
                            buttonTemplate2.append("</div>");
                        }
                    }
                    if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                        buttonTemplate2.append("</div>");
                    }
                }

                int j = 0;
                SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
                for (CustomButton cbb : viewbutton) {
                    //加按钮权限SecurityApiFacade.isGranted
                    if (securityAuditFacadeService.isGranted(cbb.getCode()) && cbb.getPlace() != null
                            && cbb.getPlace().indexOf("头部") > -1) {
                        if (j == 0) {
                            buttonTemplate.append("<div class='customButton customButton_top'>");
                            j++;
                        }
                        if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                && buttonGroup.indexOf(cbb.getButtonGroup()) < 0) {
                            buttonGroup += "," + cbb.getButtonGroup();
                        }
                        String place = cbb.getPlace();
                        if (place != null && place.indexOf("头部") > -1) {
                            String buttonFunction = cbb.getJsContent();
                            String buttonName = cbb.getName();
                            String buttonCode = cbb.getCode();
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate.append("<button place=\"place_top\" type=\"button\" value=\"" + buttonCode
                                    + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                        } else if (place != null && place.indexOf("头尾部") > -1 && cbb.getButtonGroup() == null) {
                            String buttonFunction = cbb.getJsContent();
                            String buttonName = cbb.getName();
                            String buttonCode = cbb.getCode();
                            if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                                buttonFunction = "";
                            }
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate.append("<button place=\"place_top\" type=\"button\" value=\"" + buttonCode
                                    + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                            buttonTemplate2.append("<button place=\"place_top\" type=\"button\" value=\"" + buttonCode
                                    + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                        }
                    }
                }
                if (j == 1) {
                    buttonTemplate.append("</div>");
                }
            }

            buttonGroup = buttonGroup.replaceFirst(",", "");
            String[] buttonGroupArray = buttonGroup.split(",");
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("<div class='customButton_group'>");
            }
            for (int j = 0; j < buttonGroupArray.length; j++) {
                if (!"".equals(buttonGroupArray[j])) {
                    buttonTemplate.append("<div class='customButton_group_item'>");
                    buttonTemplate
                            .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                    + buttonGroupArray[j] + "</div><div class='select_icon'></div></div>");
                    buttonTemplate.append("<div class='customButton_group_buttons'>");
                    for (CustomButton cbb2 : viewbutton) {
                        if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                && cbb2.getButtonGroup().equals(buttonGroupArray[j])) {
                            String buttonFunction = cbb2.getJsContent();
                            String buttonName = cbb2.getName();
                            String buttonCode = cbb2.getCode();
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate
                                    .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                            + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName);
                            buttonTemplate.append("</div>");
                        }
                    }
                    buttonTemplate.append("</div>");
                    buttonTemplate.append("</div>");
                }
            }
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("</div>");
            }

            if (selectDefinition.getForTimeSolt() != null && selectDefinition.getForTimeSolt() == true
                    && !StringUtils.isBlank(selectDefinition.getSearchField())) {
                buttonTemplate.append("<div class='view_timeSolt_div'>");
                buttonTemplate.append("<select class='searchField'>");
                String[] searchFieldArray = selectDefinition.getSearchField().split(";");
                for (int ij = 0; ij < searchFieldArray.length; ij++) {
                    buttonTemplate.append("<option value='" + searchFieldArray[ij].split(":")[1] + "'>"
                            + searchFieldArray[ij].split(":")[0] + "</option>");
                }
                buttonTemplate.append("</select>");
                buttonTemplate
                        .append("<input type='text' value='开始时间' name='beginTime' id='beginTime' autocomplete='off' class='Wdate'/>");
                buttonTemplate.append("<span class='toLine'>--</span>");
                buttonTemplate
                        .append("<input type='text' value='结束时间' name='endTime' id='endTime' autocomplete='off' class='Wdate'/>");
                buttonTemplate.append("</div>");
            }
            if (BooleanUtils.isTrue(selectDefinition.getForKeySelect()) && selectDefinition.getVagueKeySelect() != null
                    && selectDefinition.getExactKeySelect() != null) {
                if (selectDefinition.getVagueKeySelect() == true && selectDefinition.getExactKeySelect() == false) {
                    buttonTemplate
                            .append("<div class='view_keyword_div'><input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button></div>");
                } else if (selectDefinition.getExactKeySelect() == true
                        && selectDefinition.getVagueKeySelect() == false) {
                    buttonTemplate.append("<div class='selectKeyTableDiv'>");
                    String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' >";
                    Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                    int i = 0;
                    for (ExactKeySelectCol ekc : exactKeySelectCols) {
                        String keyName = ekc.getKeyName();
                        String keyValue = ekc.getKeyValue();
                        if (i % 2 != 0) {
                            tableHtml += "<td>" + keyName + ":</td><td>"
                                    + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  field='"
                                    + keyValue + "'></td></tr>";
                        } else {
                            if (i % 4 == 0) {
                                tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            } else {
                                tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            }
                        }
                        i++;
                    }
                    if (i == exactKeySelectCols.size()) {
                        if (exactKeySelectCols.size() % 2 != 0) {
                            tableHtml += "<td></td><td></td>";
                        }
                        tableHtml += "</tr>";
                    }
                    tableHtml += "</table>";
                    buttonTemplate.append(tableHtml);
                    buttonTemplate
                            .append("<button id='keySelect' type='button' style='margin: 5px;float: right;'>查询</button></div>");
                } else if (selectDefinition.getVagueKeySelect() == true && selectDefinition.getExactKeySelect() == true) {
                    buttonTemplate.append("<div class='selectKeyTableDiv'>");
                    String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' style='display:none'>";
                    Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                    int i = 0;
                    for (ExactKeySelectCol ekc : exactKeySelectCols) {
                        String keyName = ekc.getKeyName();
                        String keyValue = ekc.getKeyValue();
                        if (i % 2 != 0) {
                            tableHtml += "<td>" + keyName + ":</td><td>"
                                    + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  field='"
                                    + keyValue + "'></td></tr>";
                        } else {
                            if (i % 4 == 0) {
                                tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            } else {
                                tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                        + keyValue + "'></td>";
                            }
                        }
                        i++;
                    }
                    if (i == exactKeySelectCols.size()) {
                        if (exactKeySelectCols.size() % 2 != 0) {
                            tableHtml += "<td></td><td></td>";
                        }
                        tableHtml += "</tr>";
                    }
                    tableHtml += "</table></div>";
                    buttonTemplate.append(tableHtml);
                    buttonTemplate
                            .append("<div class='view_keyword_div'><input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button>");
                    buttonTemplate.append("<button id='showButton' type='button'>↑</button>");
                }
            }

            buttonTemplate.append("</div>");
        }
        long queryItemCount = 0;
        //获取列标题模板
        Map<String, ColumnDefinition> columnFields = new LinkedHashMap<String, ColumnDefinition>();
        boolean showTitle = viewDefinitionBean.getShowTitle();
        String titleSource = getTitleSource(showTitle, viewDefinitionBean.getShowCheckBox() == null ? false
                : viewDefinitionBean.getShowCheckBox(), columnDefinitions, columnFields, datascope);
        String templateAll = "";
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo);
            queryItemCount = page.getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            viewAndDataBean.setData(queryItems);

        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData2(tableUuid, defaultCondition, columnDefinitions, rowIdKey,
                    roleType, roleValue, pageDefinition, dyViewQueryInfo, count);
            queryItemCount = page.getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
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
            if (!sb_.toString().equals("@")) {
                for (QueryItem queryItem : queryItems) {
                    for (String key : queryItem.keySet()) {
                        if (sb_.toString().toLowerCase().indexOf("@" + key.toLowerCase() + "@") > -1
                                && (Clob) queryItem.get(key) != null) {
                            String temp_ = IOUtils.toString(((Clob) queryItem.get(key)).getCharacterStream());
                            queryItem.put(key, temp_);
                        }
                    }
                }
            }
            //获取行模板
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            //						viewAndDataBean.setData(queryItems);
        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData3(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo, count);
            queryItemCount = page.getTotalCount();
            viewAndDataBean.setData(queryItems);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
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
        model.addAttribute("orderbyArr", orderbyArr);
        model.addAttribute("title", orderTitle);
        //			model.addAttribute("mark", "clickSort");
        //			model.addAttribute("mark", "condSelect");
        //			model.addAttribute("mark", "keyWord");
        model.addAttribute("mark", "viewShow");
        model.addAttribute("selectTemplate", selectTemplate);
        model.addAttribute("condSelect", condSelect);
        model.addAttribute("keySelect", keySelect);
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinition);
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("buttonTemplate", buttonTemplate);
        model.addAttribute("buttonTemplate2", buttonTemplate2);
        model.addAttribute("template", templateAll);
        model.addAttribute("columnDefinitions", columnDefinitions);
        model.addAttribute("viewDefinitionBean", viewDefinitionBean);
        model.addAttribute("viewAndDataBean", viewAndDataBean);
        model.addAttribute("queryItemCount", queryItemCount);
        model.addAttribute("srcList", srcList);
        model.addAttribute("parmStr", parmStr);
        model.addAttribute("htmlParmStr", htmlParmStr);
        return forward("/basicdata/dyview/view_explain");
    }

    /**
     * 视图的控制层调用入口(进来后做了点击排序、关键字查询等操作后的)
     *
     * @param model
     * @param viewUuid
     * @param dyViewQueryInfo
     * @param expandParams
     * @param openBy
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/view_show_param", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewDataByParam(Model model, @RequestBody DyViewQueryInfo dyViewQueryInfo,
                                     HttpServletRequest request) throws Exception {
        if (dyViewQueryInfo == null) {
            dyViewQueryInfo = new DyViewQueryInfo();
        }
        Map<String, String> expandParams = new HashMap<String, String>();
        if (dyViewQueryInfo.getExpandParams() != null) {
            expandParams = dyViewQueryInfo.getExpandParams();
        }
        String viewUuid = "";
        if (dyViewQueryInfo.getViewUuid() != null) {
            viewUuid = dyViewQueryInfo.getViewUuid();
        }

        //视图名称
        String viewName = dyViewQueryInfo.getViewName();
        String orderTitle = "";
        String orderbyArr = "";
        if (dyViewQueryInfo.getCondSelect() != null) {
            if (StringUtils.isNotEmpty(dyViewQueryInfo.getCondSelect().getOrderTitle())) {
                orderTitle = dyViewQueryInfo.getCondSelect().getOrderTitle();
            }
            if (StringUtils.isNotEmpty(dyViewQueryInfo.getCondSelect().getOrderbyArr())) {
                orderbyArr = dyViewQueryInfo.getCondSelect().getOrderbyArr();
            }
        }

        //视图的条数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String count = expandParams.get("count");
        //关联数据所用的参数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String relationDataDefiantion = expandParams.get("relationDataDefiantion");
        /************************获取视图的定义bean开始***********************/
        ViewAndDataBean viewAndDataBean = new ViewAndDataBean();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        if (viewUuid != null && viewUuid != "") {
            viewAndDataBean.setViewUuid(viewUuid);
            viewDefinitionBean = viewDefinitionService.getBeanByUuid(viewUuid);
        } else {
            ViewDefinition viewDefinition = viewDefinitionService.getByViewId(viewName);
            viewUuid = viewDefinition.getUuid();
            viewAndDataBean.setViewUuid(viewUuid);
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
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserName}",
                SpringSecurityUtils.getCurrentUserName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentLoginName}",
                SpringSecurityUtils.getCurrentLoginName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserId}",
                SpringSecurityUtils.getCurrentUserId());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentId}",
                SpringSecurityUtils.getCurrentUserDepartmentId());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserDepartmentName}",
                SpringSecurityUtils.getCurrentUserDepartmentName());
        defaultCondition = StringUtils.replace(defaultCondition, "${nowDate}", "date(t.create_time) = :nowDate");
        //解析默认条件里定义的参数，来源url链接
        String parmStr = "";
        String htmlParmStr = "";
        Map<String, String> pMap = dyViewQueryInfo.getExpandParams();
        if (pMap != null) {
            for (String pkey : pMap.keySet()) {
                String pvalue = pMap.get(pkey);
                if (defaultCondition != null) {
                    defaultCondition = defaultCondition.replace("${" + pkey + "}", pvalue);
                }
                if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                    parmStr += "&" + pkey + "=" + pvalue;
                    htmlParmStr += "<input type='hidden' id='view_param_" + pkey + "' value='" + pvalue + "' />";
                }
            }
        }
        if (!StringUtils.isBlank(dyViewQueryInfo.getOpenBy())) {
            parmStr += "&openBy=" + dyViewQueryInfo.getOpenBy();
        }

        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        //获取视图的分页信息
        PageDefinition pageDefinition = new PageDefinition();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfo.getPageInfo() != null) {
            page = dyViewQueryInfo.getPageInfo();
        } else {
            if (0 == (page.getCurrentPage())) {
                page.setCurrentPage(1);
            }
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
        //获取有权限的自定义按钮
        getViewDataService.setCustomButtonRights(viewDefinitionBean, buttonBeans);
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
                model.addAttribute("conditionTypes", conditionTypes);
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
        //		if (relationDataDefiantion != null && !relationDataDefiantion.equals("")) {//关联数据——指定字段显示
        //			Set<ColumnDefinitionNew> columnDefinitionsTemp = viewDefinitionBean.getColumnDefinitions();
        //			for (ColumnDefinitionNew columnDefinition : columnDefinitionsTemp) {
        //				if (columnDefinition.getColumnAliase() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getColumnAliase()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				} else if (columnDefinition.getFieldName() != null
        //						&& relationDataDefiantion.indexOf(columnDefinition.getFieldName()) > -1) {
        //					columnDefinitions.add(columnDefinition);
        //				}
        //			}
        //		} else {//关联数据——配置的字段全部显示
        columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        //		}

        //获取表的总记录数
        Long totalCount = null;
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            totalCount = dyFormApiFacade.queryTotalCountOfFormDataOfMainform(tableName, "");
            //如果分页存在,设置分页信息
            if (pageDefinition.getIsPaging() == true) {
                page.setTotalCount(totalCount);
                if (dyViewQueryInfo.getPageInfo().getPageSize() == 0) {
                    page.setPageSize(pageDefinition.getPageNum());
                }
            }
        } else if ((datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY || datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE)
                && pageDefinition.getIsPaging() == true) {
            //如果分页存在,设置分页信息
            if (pageDefinition.getPageNum() == null) {
                pageDefinition.setPageNum(10);
            }
            if (dyViewQueryInfo.getPageInfo().getPageSize() == 0) {
                page.setPageSize(pageDefinition.getPageNum());
            }
        }
        //获取视图自定义按钮
        Set<CustomButton> viewbutton = viewDefinitionBean.getCustomButtons();
        //点击事件跳转的url
        String lineType = viewDefinitionBean.getLineType();
        StringBuilder buttonTemplate = new StringBuilder();
        StringBuilder buttonTemplate2 = new StringBuilder();
        String buttonGroup = "";
        String buttonGroup2 = "";
        if (viewbutton.size() > 0 || selectDefinition.getForKeySelect() == true) {
            buttonTemplate.append("<div class='view_tool2'>");
            if (viewbutton.size() > 0) {
                if (viewDefinitionBean.getButtonPlace() != null && viewDefinitionBean.getButtonPlace() == true) {
                    buttonTemplate2.append("<div class='view_tool_bottom'>");
                    if (viewbutton.size() > 0) {
                        int i = 0;
                        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
                        for (CustomButton cbb : viewbutton) {
                            //加按钮权限SecurityApiFacade.isGranted
                            if (securityAuditFacadeService.isGranted(cbb.getCode()) && cbb.getPlace() != null
                                    && cbb.getPlace().indexOf("头部") > -1) {
                                if (i == 0) {
                                    buttonTemplate2.append("<div class='customButton customButton_top'>");
                                    i++;
                                }
                                if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                        && buttonGroup2.indexOf(cbb.getButtonGroup()) < 0) {
                                    buttonGroup2 += "," + cbb.getButtonGroup();
                                }
                                String place = cbb.getPlace();
                                if (place != null && place.indexOf("头部") > -1 && cbb.getButtonGroup() == null) {
                                    String buttonFunction = cbb.getJsContent();
                                    String buttonName = cbb.getName();
                                    String buttonCode = cbb.getCode();
                                    if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                                        buttonFunction = "";
                                    }
                                    buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                    buttonTemplate2.append("<button place=\"place_top\" type=\"button\" value=\""
                                            + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName
                                            + "</button>");
                                }
                            }
                        }
                        if (i == 1) {
                            buttonTemplate2.append("</div>");
                        }
                    }

                    buttonGroup2 = buttonGroup2.replaceFirst(",", "");
                    String[] buttonGroupArray2 = buttonGroup2.split(",");
                    if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                        buttonTemplate2.append("<div class='customButton_group'>");
                    }
                    for (int j = 0; j < buttonGroupArray2.length; j++) {
                        if (!"".equals(buttonGroupArray2[j])) {
                            buttonTemplate2.append("<div class='customButton_group_item'>");
                            buttonTemplate2
                                    .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                            + buttonGroupArray2[j] + "</div><div class='select_icon'></div></div>");
                            buttonTemplate2.append("<div class='customButton_group_buttons_bottom'>");
                            for (CustomButton cbb2 : viewbutton) {
                                if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                        && cbb2.getButtonGroup().equals(buttonGroupArray2[j])) {
                                    String buttonFunction = cbb2.getJsContent();
                                    String buttonName = cbb2.getName();
                                    String buttonCode = cbb2.getCode();
                                    buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                                    buttonTemplate2
                                            .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                                    + buttonCode
                                                    + "\"  onclick=\""
                                                    + buttonFunction
                                                    + "\">"
                                                    + buttonName);
                                    buttonTemplate2.append("</div>");
                                }
                            }
                            buttonTemplate2.append("</div>");
                            buttonTemplate2.append("</div>");
                        }
                    }
                    if (!"".equals(buttonGroup2) && buttonGroupArray2.length > 0) {
                        buttonTemplate2.append("</div>");
                    }
                }
                SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
                int j = 0;
                for (CustomButton cbb : viewbutton) {
                    //加按钮权限SecurityApiFacade.isGranted
                    if (securityAuditFacadeService.isGranted(cbb.getCode()) && cbb.getPlace() != null
                            && cbb.getPlace().indexOf("头部") > -1) {
                        if (j == 0) {
                            buttonTemplate.append("<div class='customButton customButton_top'>");
                            j++;
                        }
                        if (cbb.getButtonGroup() != null && !"".equals(cbb.getButtonGroup())
                                && buttonGroup.indexOf(cbb.getButtonGroup()) < 0) {
                            buttonGroup += "," + cbb.getButtonGroup();
                        }
                        String place = cbb.getPlace();
                        if (place != null && place.indexOf("头部") > -1 && cbb.getButtonGroup() == null) {
                            String buttonFunction = cbb.getJsContent();
                            String buttonName = cbb.getName();
                            String buttonCode = cbb.getCode();
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate.append("<button place=\"place_top\" type=\"button\" value=\"" + buttonCode
                                    + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                        }
                    }
                }
                if (j == 1) {
                    buttonTemplate.append("</div>");
                }
            }

            buttonGroup = buttonGroup.replaceFirst(",", "");
            String[] buttonGroupArray = buttonGroup.split(",");
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("<div class='customButton_group'>");
            }
            for (int j = 0; j < buttonGroupArray.length; j++) {
                if (!"".equals(buttonGroupArray[j])) {
                    buttonTemplate.append("<div class='customButton_group_item'>");
                    buttonTemplate
                            .append("<div class='customButton_group_name'><div class='customButton_group_name_text'>"
                                    + buttonGroupArray[j] + "</div><div class='select_icon'></div></div>");
                    buttonTemplate.append("<div class='customButton_group_buttons'>");
                    for (CustomButton cbb2 : viewbutton) {
                        if (cbb2.getButtonGroup() != null && !"".equals(cbb2.getButtonGroup())
                                && cbb2.getButtonGroup().equals(buttonGroupArray[j])) {
                            String buttonFunction = cbb2.getJsContent();
                            String buttonName = cbb2.getName();
                            String buttonCode = cbb2.getCode();
                            buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                            buttonTemplate
                                    .append("<div class=\"customButton_group_button\" place=\"place_top\" value=\""
                                            + buttonCode + "\"  onclick=\"" + buttonFunction + "\">" + buttonName);
                            buttonTemplate.append("</div>");
                        }
                    }
                    buttonTemplate.append("</div>");
                    buttonTemplate.append("</div>");
                }
            }
            if (!"".equals(buttonGroup) && buttonGroupArray.length > 0) {
                buttonTemplate.append("</div>");
            }

            if (selectDefinition.getForTimeSolt() != null && selectDefinition.getForTimeSolt() == true
                    && !StringUtils.isBlank(selectDefinition.getSearchField())) {
                buttonTemplate.append("<div class='view_timeSolt_div'>");
                buttonTemplate.append("<select class='searchField'>");
                String[] searchFieldArray = selectDefinition.getSearchField().split(";");
                for (int ij = 0; ij < searchFieldArray.length; ij++) {
                    buttonTemplate.append("<option value='" + searchFieldArray[ij].split(":")[1] + "'>"
                            + searchFieldArray[ij].split(":")[0] + "</option>");
                }
                buttonTemplate.append("</select>");
                if (StringUtils.isNotBlank(dyViewQueryInfo.getCondSelect().getBeginTime())) {
                    buttonTemplate.append("<input type='text' name='beginTime' id='beginTime' value='"
                            + dyViewQueryInfo.getCondSelect().getBeginTime() + "' class='Wdate'/>");
                } else {
                    buttonTemplate
                            .append("<input type='text' name='beginTime' id='beginTime' value='开始时间' class='Wdate'/>");
                }

                buttonTemplate.append("<span class='toLine'>--</span>");

                if (StringUtils.isNotBlank(dyViewQueryInfo.getCondSelect().getEndTime())) {
                    buttonTemplate.append("<input type='text' name='endTime' id='endTime' value='"
                            + dyViewQueryInfo.getCondSelect().getEndTime() + "' class='Wdate'/>");
                } else {
                    buttonTemplate
                            .append("<input type='text' name='endTime' id='endTime' value='结束时间' class='Wdate'/>");
                }
                buttonTemplate.append("</div>");
            }
            if (selectDefinition.getForKeySelect() == true && selectDefinition.getSelectShow() == true) {
                List<Map<String, String>> keyWords = dyViewQueryInfo.getCondSelect().getKeyWords();
                if (selectDefinition.getVagueKeySelect() != null && selectDefinition.getExactKeySelect() != null) {
                    if (selectDefinition.getVagueKeySelect() == true && selectDefinition.getExactKeySelect() == false) {
                        StringBuilder selectKeyTextAll = new StringBuilder();
                        if (keyWords != null && keyWords.size() != 0) {
                            for (int j = 0; j < keyWords.size(); j++) {
                                Map<String, String> keyWord = keyWords.get(j);
                                for (String key : keyWord.keySet()) {
                                    selectKeyTextAll.append(keyWord.get("all"));
                                }
                            }
                        }
                        if (StringUtils.isNotEmpty(selectKeyTextAll.toString())) {
                            buttonTemplate
                                    .append("<div class='view_keyword_div'><input type='text' value='"
                                            + selectKeyTextAll.toString()
                                            + "' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button></div>");
                        } else {
                            buttonTemplate
                                    .append("<div class='view_keyword_div'><input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/><button id='keySelect' type='button'>查询</button></div>");
                        }
                    } else if (selectDefinition.getExactKeySelect() == true
                            && selectDefinition.getVagueKeySelect() == false) {

                        buttonTemplate.append("<div class='selectKeyTableDiv'>");
                        String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' >";
                        Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                        int i = 0;
                        for (ExactKeySelectCol ekc : exactKeySelectCols) {
                            String keyName = ekc.getKeyName();
                            String keyValue = ekc.getKeyValue();
                            StringBuilder selectKeyText = new StringBuilder();
                            if (keyWords != null && keyWords.size() != 0) {
                                for (int j = 0; j < keyWords.size(); j++) {
                                    Map<String, String> keyWord = keyWords.get(j);
                                    for (String key : keyWord.keySet()) {
                                        if (key.equals(keyValue)) {
                                            selectKeyText.append(keyWord.get(keyValue));
                                        }
                                    }
                                }
                            }
                            if (i % 2 != 0) {
                                tableHtml += "<td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  value='"
                                        + selectKeyText.toString() + "' field='" + keyValue + "''></td></tr>";
                            } else {
                                if (i % 4 == 0) {
                                    tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                            + "<input type='text' class='selectKeyText' id='selectKeyTextId'  value='"
                                            + selectKeyText.toString() + "' field='" + keyValue + "''></td>";
                                } else {
                                    tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                            + "<input type='text' class='selectKeyText' id='selectKeyTextId'  value='"
                                            + selectKeyText.toString() + "' field='" + keyValue + "''></td>";
                                }
                            }
                            i++;
                        }
                        if (i == exactKeySelectCols.size()) {
                            if (exactKeySelectCols.size() % 2 != 0) {
                                tableHtml += "<td></td><td></td>";
                            }
                            tableHtml += "</tr>";
                        }

                        //						for (ExactKeySelectColNew ekc : exactKeySelectCols) {
                        //							String keyName = ekc.getKeyName();
                        //							String keyValue = ekc.getKeyValue();
                        //							StringBuilder selectKeyText = new StringBuilder();
                        //							if (keyWords != null && keyWords.size() != 0) {
                        //								for (int j = 0; j < keyWords.size(); j++) {
                        //									Map<String, String> keyWord = keyWords.get(j);
                        //									for (String key : keyWord.keySet()) {
                        //										if (key.equals(keyValue)) {
                        //											selectKeyText.append(keyWord.get(keyValue));
                        //										}
                        //									}
                        //								}
                        //							}
                        //							tableHtml += "<tr>";
                        //							tableHtml += "<td>" + keyName + "<input type='text' class='selectKeyText' value='"
                        //									+ selectKeyText.toString() + "' field='" + keyValue + "''></td></tr>";
                        //						}
                        tableHtml += "</table>";
                        buttonTemplate.append(tableHtml);
                        buttonTemplate
                                .append("<button id='keySelect' type='button' style='margin: 5px;float: right;'>查询</button></div>");
                    } else if (selectDefinition.getVagueKeySelect() == true
                            && selectDefinition.getExactKeySelect() == true) {
                        buttonTemplate.append("<div class='selectKeyTableDiv'>");
                        String tableHtml = "<table id='selectKeyTableId' class='selectKeyTableClass' style='display:none'>";
                        Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
                        int i = 0;

                        for (ExactKeySelectCol ekc : exactKeySelectCols) {
                            String keyName = ekc.getKeyName();
                            String keyValue = ekc.getKeyValue();
                            StringBuilder selectKeyText = new StringBuilder();
                            if (keyWords != null && keyWords.size() != 0) {
                                for (int j = 0; j < keyWords.size(); j++) {
                                    Map<String, String> keyWord = keyWords.get(j);
                                    for (String key : keyWord.keySet()) {
                                        if (key.equals(keyValue)) {
                                            selectKeyText.append(keyWord.get(keyValue));
                                        }
                                    }
                                }
                            }
                            if (i % 2 != 0) {
                                tableHtml += "<td>" + keyName + ":</td><td>"
                                        + "<input type='text' class='selectKeyText'  id='selectKeyTextId'  field='"
                                        + keyValue + "' value='" + selectKeyText.toString() + "'></td></tr>";
                            } else {
                                if (i % 4 == 0) {
                                    tableHtml += "<tr class='pdd'><td>" + keyName + ":</td><td>"
                                            + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                            + keyValue + "' value='" + selectKeyText.toString() + "'></td>";
                                } else {
                                    tableHtml += "<tr class='odd'><td>" + keyName + ":</td><td>"
                                            + "<input type='text' class='selectKeyText' id='selectKeyTextId'  field='"
                                            + keyValue + "' value='" + selectKeyText.toString() + "'></td>";
                                }
                            }
                            i++;
                        }
                        if (i == exactKeySelectCols.size()) {
                            if (exactKeySelectCols.size() % 2 != 0) {
                                tableHtml += "<td></td><td></td>";
                            }
                            tableHtml += "</tr>";
                        }
                        tableHtml += "</table></div>";
                        buttonTemplate.append(tableHtml);
                        StringBuilder selectKeyAllText = new StringBuilder();
                        if (keyWords != null && keyWords.size() != 0) {
                            for (int j = 0; j < keyWords.size(); j++) {
                                Map<String, String> keyWord = keyWords.get(j);
                                for (String key : keyWord.keySet()) {
                                    if (key.equals("all")) {
                                        selectKeyAllText.append(keyWord.get("all"));
                                    }
                                }
                            }
                        }
                        buttonTemplate
                                .append("<div class='view_keyword_div'><input type='text' value='"
                                        + selectKeyAllText.toString()
                                        + "' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off' /><button id='keySelect' type='button'>查询</button>");
                        buttonTemplate.append("<button id='showButton' type='button'>↑</button>");
                    }
                }
            }

            buttonTemplate.append("</div>");
        }
        long queryItemCount = 0;
        //获取列标题模板
        Map<String, ColumnDefinition> columnFields = new LinkedHashMap<String, ColumnDefinition>();
        boolean showTitle = viewDefinitionBean.getShowTitle();
        String titleSource = getTitleSource(showTitle, viewDefinitionBean.getShowCheckBox() == null ? false
                : viewDefinitionBean.getShowCheckBox(), columnDefinitions, columnFields, datascope);
        String templateAll = "";
        if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo);
            queryItemCount = page.getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            viewAndDataBean.setData(queryItems);

        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData2(tableUuid, defaultCondition, columnDefinitions, rowIdKey,
                    roleType, roleValue, pageDefinition, dyViewQueryInfo, count);
            queryItemCount = page.getTotalCount();

            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
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
            if (!sb_.toString().equals("@")) {
                for (QueryItem queryItem : queryItems) {
                    for (String key : queryItem.keySet()) {
                        if (sb_.toString().toLowerCase().indexOf("@" + key.toLowerCase() + "@") > -1
                                && (Clob) queryItem.get(key) != null) {
                            String temp_ = IOUtils.toString(((Clob) queryItem.get(key)).getCharacterStream());
                            queryItem.put(key, temp_);
                        }
                    }
                }
            }
            //获取行模板
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
            //						viewAndDataBean.setData(queryItems);
        } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
            List<QueryItem> queryItems = new ArrayList<QueryItem>();
            queryItems = viewDefinitionService.getColumnData3(defaultCondition, tableName, columnDefinitions,
                    pageDefinition, dyViewQueryInfo, count);
            queryItemCount = page.getTotalCount();
            viewAndDataBean.setData(queryItems);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
            templateAll = getColumnTemplate(viewDefinitionBean, columnDefinitions, queryItems, columnFields, request,
                    datascope);
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
        model.addAttribute("orderbyArr", orderbyArr);
        model.addAttribute("title", orderTitle);
        //			model.addAttribute("mark", "clickSort");
        //			model.addAttribute("mark", "condSelect");
        //			model.addAttribute("mark", "keyWord");
        model.addAttribute("mark", "viewSelect");
        model.addAttribute("selectTemplate", selectTemplate);
        model.addAttribute("condSelect", condSelect);
        model.addAttribute("keySelect", keySelect);
        if (page.getTotalCount() == 0) {
            page.setCurrentPage(0);
        }
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinition);
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("buttonTemplate", buttonTemplate);
        model.addAttribute("buttonTemplate2", buttonTemplate2);
        model.addAttribute("template", templateAll);
        model.addAttribute("columnDefinitions", columnDefinitions);
        model.addAttribute("viewDefinitionBean", viewDefinitionBean);
        model.addAttribute("viewAndDataBean", viewAndDataBean);
        model.addAttribute("queryItemCount", queryItemCount);
        model.addAttribute("srcList", srcList);
        model.addAttribute("parmStr", parmStr);
        model.addAttribute("htmlParmStr", htmlParmStr);
        return forward("/basicdata/dyview/view_explain");
    }

    /**
     * 获取行模板
     */
    public String getColumnTemplate(ViewDefinitionBean viewDefinitionBean, Set<ColumnDefinition> columnDefinitions,
                                    List<QueryItem> queryItems, Map<String, ColumnDefinition> columnFields, HttpServletRequest request,
                                    int datascope) throws Exception {
        String lineType = viewDefinitionBean.getLineType();
        StringBuilder template = new StringBuilder();
        String cssValue = "";
        Map<String, Object> root = new HashMap<String, Object>();
        for (int i = 0; i < queryItems.size(); i++) {
            //是否已读
            String isread = "";
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                boolean isflag = queryItems.get(i).get("readFlag") == null ? false : (Boolean) queryItems.get(i).get(
                        "readFlag");
                if (isflag) {
                    isread = "readed";
                } else {
                    isread = "noread";
                }
            }
            //获取行模板
            String keyName = "list" + i;
            root.put(keyName, queryItems.get(i));
            //			String jsonStr = queryItems.get(i).toString();
            JSONObject jsonStr = new JSONObject();
            QueryItem item = queryItems.get(i);
            Iterator<String> it = item.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = item.get(key);
                jsonStr.put(key, value);
            }

            /**获取视图自定义按钮开始**/
            Set<CustomButton> viewbutton = viewDefinitionBean.getCustomButtons();
            StringBuilder buttonTemplate1 = new StringBuilder();
            StringBuilder buttonTemplate2 = new StringBuilder();
            if (viewbutton.size() > 0) {
                buttonTemplate1.append("<div class='customButton1'>");
                buttonTemplate2.append("<div class='customButton2'>");
            }
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
            for (CustomButton cbb : viewbutton) {
                //加按钮权限SecurityApiFacade.isGranted
                if (securityAuditFacadeService.isGranted(cbb.getCode())) {
                    String place = cbb.getPlace();
                    String buttonName = cbb.getName();
                    String buttonCode = cbb.getCode();
                    String buttonFunction = cbb.getJsContent();
                    //按钮事件的传参处理
                    Pattern p1 = Pattern.compile("\\{.*?\\}");
                    if (!(!StringUtils.isBlank(buttonFunction) && !"null".equals(buttonFunction))) {
                        buttonFunction = "";
                    }
                    Matcher m1 = p1.matcher(buttonFunction);
                    while (m1.find()) {
                        String afild = m1.group().replace("{", "").replace("}", "");
                        for (String key : columnFields.keySet()) {
                            ColumnDefinition columnDefinition = columnFields.get(key);
                            if (afild.equals(columnDefinition.getTitleName())) {
                                String fieldName = "";
                                if (columnDefinition.getColumnAliase() == null) {
                                    fieldName = columnDefinition.getFieldName();
                                } else {
                                    fieldName = columnDefinition.getColumnAliase();
                                }

                                String fieldNameTurn = QueryItem.getKey(fieldName);
                                String replaceData = "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
                                buttonFunction = buttonFunction.replace("${" + afild + "}", replaceData);
                            }
                        }
                    }
                    buttonFunction = buttonFunction.replace("${ctx}", request.getContextPath());
                    if (place != null && place.indexOf("第一行") > -1) {
                        buttonTemplate1.append("<button place=\"place_1stline\" type=\"button\" value=\"" + buttonCode
                                + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                    } else if (place != null && place.indexOf("第二行") > -1) {
                        buttonTemplate2.append("<button place=\"place_2ndline\" type=\"button\" value=\"" + buttonCode
                                + "\"  onclick=\"" + buttonFunction + "\">" + buttonName + "</button>");
                    }
                }
            }
            if (viewbutton.size() > 0) {
                buttonTemplate1.append("</div>");
                buttonTemplate2.append("</div>");
            }
            /**获取视图自定义按钮开始结束**/

            String url = viewDefinitionBean.getUrl();
            if (url == null) {
                url = "";
            }
            //获取视图的行样式信息
            Set<ColumnCssDefinition> columnCssDefinitions = viewDefinitionBean.getColumnCssDefinition();
            for (ColumnCssDefinition columnCssDefinition : columnCssDefinitions) {
                String viewColumn = columnCssDefinition.getViewColumn();//列
                String columnCondition = columnCssDefinition.getColumnCondition();//列条件
                String columnConditionValue = columnCssDefinition.getColumnConditionValue();//列条件值
                String fontColor = columnCssDefinition.getFontColor();//字体颜色
                String fontWide = columnCssDefinition.getFontWide();//字体是否加粗
                Iterator<?> objkey = jsonStr.keys();
                while (objkey.hasNext()) {// 遍历JSONObject
                    String json_key = (String) objkey.next().toString();
                    Object value = jsonStr.has(json_key) ? jsonStr.get(json_key) : null;
                    String json_value = value != null ? value.toString() : "";

                    if (json_key.equals(viewColumn)) {
                        String condition = DyviewConfig.getDyviewColumncssCondtion().get(columnCondition);
                        if (condition.equals("包含")) {
                            if (json_value.contains(columnConditionValue)) {
                                if (fontWide.equals("1")) {
                                    cssValue = "color:" + fontColor + ";" + " font-weight: bold;";
                                } else {
                                    cssValue = "color:" + fontColor + ";";
                                }
                            }
                        } else if (condition.equals("不包含")) {
                            if (!json_value.contains(columnConditionValue)) {
                                if (fontWide.equals("1")) {
                                    cssValue = "color:" + fontColor + ";" + " font-weight: bold;";
                                } else {
                                    cssValue = "color:" + fontColor + ";";
                                }
                            }
                        } else {
                            String expression = "'" + json_value + "' " + condition + " '" + columnConditionValue + "'";
                            boolean result = (Boolean) Ognl.getValue(expression, null);
                            if (result == true) {
                                if ("1".equals(fontWide)) {
                                    cssValue = "color:" + fontColor + ";" + " font-weight: bold;";
                                } else {
                                    cssValue = "color:" + fontColor + ";";
                                }
                            }
                        }
                    }
                }
            }
            String NewUrl = "";
            String tr1 = "";
            String tr2 = "";
            NewUrl = url;
            Pattern p1 = Pattern.compile("\\{.*?\\}");
            Matcher m1 = p1.matcher(url);
            int flag = 0;
            while (m1.find()) {
                String afild = m1.group().replace("{", "").replace("}", "");
                for (String key : columnFields.keySet()) {
                    ColumnDefinition columnDefinition = columnFields.get(key);
                    if (columnDefinition.getShowLine() != null && columnDefinition.getShowLine().equals("第二行")) {
                        flag = 1;
                    }
                    if (afild.equals(columnDefinition.getTitleName())) {
                        String fieldName = "";
                        if (columnDefinition.getColumnAliase() == null) {
                            fieldName = columnDefinition.getFieldName();
                        } else {
                            fieldName = columnDefinition.getColumnAliase();
                        }

                        String fieldNameTurn = QueryItem.getKey(fieldName);
                        String replaceData = "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
                        NewUrl = NewUrl.replace("${" + afild + "}", replaceData);
                    }
                }
            }

            String xyz = jsonStr.toString();
            xyz = URLEncoder.encode(xyz, "utf-8");
            if (i % 2 == 1) {
                if (NewUrl != null && !NewUrl.equals("")) {
                    if (i == 0) {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='dataTr odd first tr_bg1 "
                                + isread + "' src='" + getRequestPath(request) + NewUrl.toString() + "'>";
                        cssValue = "";
                    } else {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='dataTr odd  tr_bg1 " + isread
                                + "' src='" + getRequestPath(request) + NewUrl.toString() + "'>";
                        cssValue = "";
                    }
                } else if (NewUrl.indexOf("http://") != -1) {
                    if (i == 0) {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='dataTr odd first tr_bg1 "
                                + isread + "' src='" + NewUrl.toString() + "'>";
                        cssValue = "";
                    } else {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='dataTr odd  tr_bg1 " + isread
                                + "' src='" + NewUrl.toString() + "'>";
                        cssValue = "";
                    }
                } else {
                    if (i == 0) {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='odd dataTr first  tr_bg1 "
                                + isread + "'>";
                        cssValue = "";
                    } else {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='odd  dataTr tr_bg1 " + isread
                                + "'>";
                        cssValue = "";
                    }
                }
                if (flag == 1) {
                    tr2 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='even  tr_bg1 " + isread + "'>";
                    cssValue = "";
                }
            } else {
                if (NewUrl != null && !NewUrl.equals("")) {
                    if (i == 0) {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='tr_bg2 dataTr odd first "
                                + isread + "' src='" + getRequestPath(request) + NewUrl.toString() + "'>";
                        cssValue = "";
                    } else {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='tr_bg2 dataTr odd " + isread
                                + "' src='" + getRequestPath(request) + NewUrl.toString() + "'>";
                        cssValue = "";
                    }
                } else {
                    if (i == 0) {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='tr_bg2 dataTr odd first "
                                + isread + "'>";
                        cssValue = "";
                    } else {
                        tr1 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='tr_bg2 dataTr odd " + isread
                                + "'>";
                        cssValue = "";
                    }
                }
                if (flag == 1) {
                    tr2 = "<tr jsonStr='" + xyz + "' style='" + cssValue + "' class='tr_bg2 even tr_bg2even " + isread
                            + "'>";
                    cssValue = "";
                }
            }
            StringBuilder source1 = new StringBuilder();
            StringBuilder source2 = new StringBuilder();
            //是否显示复选框
            if (viewDefinitionBean.getShowCheckBox() != null && viewDefinitionBean.getShowCheckBox()) {
                String getCheckKey = viewDefinitionBean.getCheckKey();
                if (getCheckKey == null || (getCheckKey != null && getCheckKey.equals(""))) {
                    getCheckKey = "uuid";
                }
                source1.append("<td width='15px'><input type='checkbox' class='checkeds' ");
                for (String key : columnFields.keySet()) {
                    String replaceData = "";
                    ColumnDefinition columnDefinition = columnFields.get(key);
                    if (columnDefinition.getFieldName().equals(getCheckKey)) {
                        String fieldName = "";
                        if (columnDefinition.getColumnAliase() == null) {
                            fieldName = columnDefinition.getFieldName();
                        } else {
                            fieldName = columnDefinition.getColumnAliase();
                        }
                        String fieldNameTurn = QueryItem.getKey(fieldName);
                        replaceData = "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
                        source1.append(" value='" + replaceData + "'");
                    }
                }
                source1.append("/></td>");
            }
            for (String key : columnFields.keySet()) {

                ColumnDefinition columnDefinition = columnFields.get(key);
                if (columnDefinition.getShowLine() == null || columnDefinition.getShowLine().equals("")
                        || columnDefinition.getShowLine().equals("第一行")) {
                    if (columnDefinition.getHidden() == false) {
                        if (columnDefinition.getValueType().equals("1")) {
                            //基础列值设置
                            String columnValue = columnDefinition.getValue();
                            String fieldNameTurn = "";
                            if (columnValue.equals(columnDefinition.getTitleName())) {
                                String fieldName = "";
                                if (datascope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
                                    fieldName = columnDefinition.getFieldName();
                                    String[] fieldNames = fieldName.split("_");
                                    fieldName = "";
                                    for (int n = 0; n < fieldNames.length; n++) {
                                        if (n == 0) {
                                            fieldName += fieldNames[n];
                                        } else {
                                            char fieldNameOld = fieldNames[n].charAt(0);
                                            char fieldNameNew = (fieldNameOld + "").toUpperCase().charAt(0);
                                            String newField = fieldNames[n].replace(fieldNameOld, fieldNameNew);
                                            fieldName += newField;
                                        }
                                    }
                                    fieldNameTurn = QueryItem.getKey(fieldName);
                                } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
                                    fieldName = columnDefinition.getFieldName();
                                    fieldNameTurn = QueryItem.getKey(fieldName);
                                } else if (datascope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
                                    fieldName = columnDefinition.getColumnAliase();
                                    fieldNameTurn = QueryItem.getKey(fieldName);
                                }

                                source1.append("<td field='" + fieldNameTurn + "' title=\"${" + keyName + "['"
                                        + fieldNameTurn + "']!}\" width=" + columnDefinition.getWidth() + ">");
                                source1.append("${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!" + "}</td>");
                            } else {
                                source1.append("<td field='" + key + "' width=" + columnDefinition.getWidth() + ">");
                                source1.append("${" + keyName + "[" + "'" + key + "'" + "]!" + "}</td>");
                            }
                        } else if (columnDefinition.getValueType().equals("2")) {
                            //高级列值设置
                            String columnAliase = QueryItem.getKey(columnDefinition.getColumnAliase());
                            source1.append("<td field='" + columnAliase + "' width=" + columnDefinition.getWidth()
                                    + ">");
                            String columnValue = columnDefinition.getValue();
                            Pattern p = Pattern.compile("\\{.*?\\}");
                            Matcher m = p.matcher(columnValue);
                            while (m.find()) {
                                String result = m.group().replace("{", "").replace("}", "");
                                for (String key1 : columnFields.keySet()) {
                                    ColumnDefinition columnDefinition1 = columnFields.get(key1);
                                    if (result.equals(columnDefinition1.getTitleName())) {
                                        String fieldName = "";
                                        if (datascope == 1 && datascope == 3) {
                                            fieldName = columnDefinition1.getFieldName();
                                        } else {
                                            fieldName = columnDefinition1.getColumnAliase();
                                        }
                                        String fieldNameTurn = QueryItem.getKey(fieldName);
                                        String replaceData = "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
                                        columnValue = columnValue.replace("${" + result + "}", replaceData);

                                    }
                                }
                            }
                            source1.append(columnValue);
                            source1.append("</td>");
                        }
                    }
                } else {
                    if (columnDefinition.getHidden() == false) {
                        if (columnDefinition.getValueType().equals("1")) {
                            //基础列值设置
                            String columnValue = columnDefinition.getValue();
                            if (columnValue.equals(columnDefinition.getTitleName())) {
                                String fieldName = "";
                                if (datascope == 1 || datascope == 3) {
                                    fieldName = columnDefinition.getFieldName();
                                } else {
                                    fieldName = columnDefinition.getColumnAliase();
                                }
                                String fieldNameTurn = QueryItem.getKey(fieldName);
                                source2.append("<td field='" + fieldNameTurn + "' title=\"${" + keyName + "['"
                                        + fieldNameTurn + "']!}\" width=" + columnDefinition.getWidth() + ">");
                                source2.append("${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!" + "}</td>");
                            }
                        } else if (columnDefinition.getValueType().equals("2")) {
                            //高级列值设置
                            String columnAliase = QueryItem.getKey(columnDefinition.getColumnAliase());
                            source2.append("<td field='" + columnAliase + "' width=" + columnDefinition.getWidth()
                                    + ">");
                            String columnValue = columnDefinition.getValue();
                            Pattern p = Pattern.compile("\\{.*?\\}");
                            Matcher m = p.matcher(columnValue);
                            while (m.find()) {
                                String result = m.group().replace("{", "").replace("}", "");
                                for (String key1 : columnFields.keySet()) {
                                    ColumnDefinition columnDefinition1 = columnFields.get(key1);
                                    if (result.equals(columnDefinition1.getTitleName())) {
                                        String fieldName = "";
                                        if (datascope == 1 || datascope == 3) {
                                            fieldName = columnDefinition.getFieldName();
                                        } else {
                                            fieldName = columnDefinition.getColumnAliase();
                                        }
                                        String fieldNameTurn = QueryItem.getKey(fieldName);
                                        String replaceData = "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
                                        columnValue = columnValue.replace("${" + result + "}", replaceData);

                                    }
                                }
                            }
                            source2.append(columnValue);
                            source2.append("</td>");
                        }
                    }
                }
            }
            //没有行按钮
            if (buttonTemplate1.toString().indexOf("place_1stline") < 0
                    && buttonTemplate2.toString().indexOf("place_2ndline") < 0) {
                tr1 += source1.toString() + "</tr>";
                //第二行有内容
                if (!tr2.equals("")) {
                    //有复选框时
                    if (tr1.indexOf("checkbox") > -1) {
                        tr2 += "<td width='15px'></td>" + source2.toString() + "</tr>";
                    } else {
                        tr2 += source2.toString() + "</tr>";
                    }
                }
            }
            //有第一行按钮，无第二行按钮
            else if (buttonTemplate1.toString().indexOf("place_1stline") > -1
                    && buttonTemplate2.toString().indexOf("place_2ndline") < 0) {
                tr1 += source1.toString() + "<td class='tr_td_button' style='text-align: right;'>"
                        + buttonTemplate1.toString() + "</td>" + "</tr>";
                //第二行有内容
                if (!tr2.equals("")) {
                    //有复选框时
                    if (tr1.indexOf("checkbox") > -1) {
                        tr2 += "<td width='15px'></td>" + source2.toString() + "</tr>";
                    } else {
                        tr2 += source2.toString() + "</tr>";
                    }
                }
            }
            //有第二行按钮，无第一行按钮
            else if (buttonTemplate1.toString().indexOf("place_1stline") < 0
                    && buttonTemplate2.toString().indexOf("place_2ndline") > -1) {
                tr1 += source1.toString() + "</tr>";
                if (!tr2.equals("")) {
                    //有复选框时
                    if (tr1.indexOf("checkbox") > -1) {
                        tr2 += "<td width='15px'></td>" + source2.toString();
                    } else {
                        tr2 += source2.toString();
                    }
                }
                if (tr2.equals("")) {
                    tr2 = "<tr class='tr_bg2 even tr_bg2even '>";
                }
                tr2 += "<td class='tr_td_button' style='text-align: right;'>" + buttonTemplate2.toString() + "</td>";
                tr2 += "</tr>";
            }
            //两行按钮都有
            else if (buttonTemplate1.toString().indexOf("place_1stline") > -1
                    && buttonTemplate2.toString().indexOf("place_2ndline") > -1) {
                tr1 += source1.toString() + "<td class='tr_td_button' style='text-align: right;'>"
                        + buttonTemplate1.toString() + "</td>" + "</tr>";
                if (!tr2.equals("")) {
                    //有复选框时
                    if (tr1.indexOf("checkbox") > -1) {
                        tr2 += "<td width='15px'></td>" + source2.toString();
                    } else {
                        tr2 += source2.toString();
                    }
                }
                if (tr2.equals("")) {
                    tr2 = "<tr class='tr_bg2 even tr_bg2even '>";
                }
                tr2 += "<td class='tr_td_button' style='text-align: right;'>" + buttonTemplate2.toString() + "</td>";
                tr2 += "</tr>";
            }
            template.append(tr1 + tr2);
        }
        template.append("<tr class='dataTr' style='height: 2px;display: block;'></tr>");
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String templateAll = templateEngine.process(template.toString(), root);
        return templateAll;
    }

    /**
     * 获取列标题模板
     *
     * @return
     */
    public String getTitleSource(@RequestParam("showTitle") boolean showTitle,
                                 @RequestParam("showCheckBox") boolean showCheckBox, Set<ColumnDefinition> columnDefinitions,
                                 Map<String, ColumnDefinition> columnFields, @RequestParam("datascope") int datascope) {
        StringBuilder titleSource = new StringBuilder();
        String fieldName = null;
        titleSource.append("<tr class='thead_tr'>");
        //是否显示复选框
        if (showCheckBox) {
            titleSource.append("<td width='15px' class='checks_td'><input type='checkbox' class='checkall'/></td>");
        }
        int j = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getHidden() == false
                    && !(columnDefinition.getShowLine() != null && columnDefinition.getShowLine().equals("第二行"))) {
                j++;
            }
        }
        int i = 1;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String titleName;
            if (columnDefinition.getOtherName() != null && !columnDefinition.getOtherName().equals("")) {
                titleName = columnDefinition.getOtherName();
            } else {
                titleName = columnDefinition.getTitleName();
            }
            if (datascope == 2) {
                fieldName = columnDefinition.getColumnAliase();
            } else {
                fieldName = columnDefinition.getFieldName();
            }
            boolean hidden = columnDefinition.getHidden();
            boolean sortAble = columnDefinition.getSortAble();
            columnFields.put(fieldName, columnDefinition);
            if (hidden == false
                    && showTitle == true
                    && (columnDefinition.getShowLine() == null || !(columnDefinition.getShowLine() != null && columnDefinition
                    .getShowLine().equals("第二行")))) {
                if (sortAble == true) {
                    if (i == j) {
                        titleSource.append("<td class='sortAble last' orderby='asc' width='"
                                + columnDefinition.getWidth() + "'>" + titleName + "</td>");
                    } else {
                        titleSource.append("<td class='sortAble' orderby='asc' width='" + columnDefinition.getWidth()
                                + "'>" + titleName + "</td>");
                    }
                } else {
                    if (i == j) {
                        titleSource.append("<td class='last' width='" + columnDefinition.getWidth() + "'>" + titleName
                                + "</td>");
                    } else {
                        titleSource.append("<td width='" + columnDefinition.getWidth() + "'>" + titleName + "</td>");
                    }
                }
                i++;
            }
        }
        titleSource.append("</tr>");
        return titleSource.toString();
    }

    @RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<QueryItem> test(@RequestParam("viewUuid") String viewUuid) {
        return viewDefinitionService.getViewDataByKey(viewUuid, "ID");
    }

    @RequestMapping(value = "/getModuleData")
    @ResponseBody
    public ResultMessage getModuleData() {
        ResultMessage resultMessage = new ResultMessage();
        //模块分类信息
        StringBuilder sb = new StringBuilder();
        List<CdDataDictionaryItemDto> dataSet = basicDataApiFacade.getDataDictionariesByType("MODULE_CATEGORY");
        for (CdDataDictionaryItemDto dataDictionary : dataSet) {
            sb.append("<option value='" + dataDictionary.getUuid() + "'>");
            sb.append(dataDictionary.getLabel());
            sb.append("</option>");
        }
        resultMessage.setData(sb.toString());
        return resultMessage;
    }

}