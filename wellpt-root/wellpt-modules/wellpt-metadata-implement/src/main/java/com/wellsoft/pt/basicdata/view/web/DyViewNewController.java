/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.facade.service.impl.DataSourceApiFacadeImpl;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.view.bean.ViewAndDataNewBean;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.*;
import com.wellsoft.pt.basicdata.view.provider.AssemblyHtmlUtil;
import com.wellsoft.pt.basicdata.view.provider.ViewColumnNew;
import com.wellsoft.pt.basicdata.view.provider.ViewDataSourceNew;
import com.wellsoft.pt.basicdata.view.service.GetViewDataNewService;
import com.wellsoft.pt.basicdata.view.service.ViewDefinitionNewService;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Clob;
import java.util.*;

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
@RequestMapping("/basicdata/view")
public class DyViewNewController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DyViewNewController.class);

    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ViewDefinitionNewService viewDefinitionNewService;
    @Autowired
    private GetViewDataNewService getViewDataNewService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired(required = false)
    private Map<String, ViewDataSourceNew> viewDataSourceNewMap;
    @Autowired
    private DataSourceApiFacadeImpl dataSourceApiFacade;

    public static boolean showFirstLine(String showLine) {
        return showLine == null || !(showLine != null && showLine.equals("第二行"));
    }

    @RequestMapping("")
    public String viewDefinitionZtree() {
        return forward("/basicdata/view/view_definition_new_ztree");
    }

    /**
     * 视图自定义入口地址
     *
     * @return
     */
    @RequestMapping("/list")
    public String viewDefinitionIndex(Model model) {
        // 视图自定的编号(存放在数据字典中)
        String code = "003006";
        List<Resource> resources = securityApiFacade.getDynamicButtonResourcesByCode(code);
        model.addAttribute("resources", resources);
        return forward("/basicdata/view/view_definition_new");
    }

    @RequestMapping(value = "/column_definition_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<DyformFieldDefinition> getColumnList(@RequestParam("formUuid") String formUuid) {
        List<DyformFieldDefinition> fieldDefinitions = dyFormApiFacade.getFormDefinition(formUuid).doGetFieldDefintions();
        return fieldDefinitions;
    }

    @RequestMapping(value = "/get_select_column", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<DataSourceColumn> getSelectColumn(@RequestParam("formUuid") String formUuid) {
        return dataSourceApiFacade.getDataSourceFieldsById(formUuid);
    }

    @RequestMapping(value = "/get_select_column2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SystemTableAttribute> getSelectColumn2(@RequestParam("formUuid") String formUuid) {
        return getViewDataNewService.getSystemTableColumns(formUuid);
    }

    @RequestMapping(value = "/get_select_column3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ViewColumnNew> getSelectColumn3(@RequestParam("formUuid") String formUuid) {
        return getViewDataNewService.getViewColumns(formUuid);
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
            List<QueryItem> queryItems = viewDefinitionNewService.getSelectData(formUuid, fieldName, page);
            data.setDataList(queryItems);
            data.setCurrentPage(page.getCurrentPage());
            data.setTotalPages((long) Math.ceil(queryItems.size() / page.getPageSize()));
        } else if (tableType.equals("2")) {
            List<Map<String, Object>> queryItems = viewDefinitionNewService.getSelectData2(formUuid, fieldName, page);
            data.setDataList(queryItems);
            data.setCurrentPage(page.getCurrentPage());
            data.setTotalPages((long) Math.ceil(queryItems.size() / page.getPageSize()));
        } else if (tableType.equals("3")) {
            List<ViewColumnNew> viewColumnNews = new ArrayList<ViewColumnNew>();
            Map<String, ViewColumnNew> map = ConvertUtils.convertElementToMap(viewDataSourceNewMap.get(formUuid)
                    .getAllViewColumns(), "columnName");
            ViewColumnNew v = map.get(fieldName);
            viewColumnNews.add(v);
            List<QueryItem> data_ = viewDataSourceNewMap.get(formUuid).query(viewColumnNews, defaultCondition,
                    new HashMap<String, Object>(), "", page);
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
        ViewDefinitionNew viewDefinitionNew = this.viewDefinitionNewService.getByViewId(id);
        redirectAttributes.addAttribute("viewUuid", viewDefinitionNew.getUuid());

        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String paramName = e.nextElement();
            redirectAttributes.addAttribute(paramName, request.getParameter(paramName));
        }
        return redirect("/basicdata/view/view_show");
    }

    /**
     * 解析视图的默认搜索条件中特定的变量
     */
    private String dispsoseDefaultCondition(String condition, HttpServletRequest request, Map<String, String> otherpMap) {
        if (condition == null) {
            return "";
        }
        String relCondition = condition;
        relCondition = relCondition.replaceAll(" +", " ");
        relCondition = StringUtils
                .replace(relCondition, "${currentUserName}", SpringSecurityUtils.getCurrentUserName());
        relCondition = StringUtils.replace(relCondition, "${currentLoginName}",
                SpringSecurityUtils.getCurrentLoginName());
        relCondition = StringUtils.replace(relCondition, "${currentUserId}", SpringSecurityUtils.getCurrentUserId());
        relCondition = StringUtils.replace(relCondition, "${currentUserDepartmentId}",
                SpringSecurityUtils.getCurrentUserDepartmentId());
        relCondition = StringUtils.replace(relCondition, "${currentUserDepartmentName}",
                SpringSecurityUtils.getCurrentUserDepartmentName());
        relCondition = StringUtils.replace(relCondition, "=${nowDate}", " date(t.create_time) = :nowDate");
        Map<String, String[]> pMap = request.getParameterMap();
        for (String pkey : pMap.keySet()) {
            relCondition = relCondition.replace("${" + pkey + "}", pMap.get(pkey)[0]);
        }
        if (otherpMap != null) {
            for (String pkey : otherpMap.keySet()) {
                relCondition = relCondition.replace("${" + pkey + "}", otherpMap.get(pkey));
            }
        }

        return relCondition;
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
        String whereSql = request.getParameter("whereSql");
        String pageCurrentPage = request.getParameter("pageCurrentPage");
        DyViewQueryInfoNew dyViewQueryInfoNew = new DyViewQueryInfoNew();
        /************************获取视图的定义bean开始***********************/
        ViewAndDataNewBean viewAndDataNewBean = new ViewAndDataNewBean();
        if (viewUuid != null && viewUuid != "") {
            viewAndDataNewBean.setViewUuid(viewUuid);
        } else {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewService.getByViewId(viewName);
            viewUuid = viewDefinitionNew.getUuid();
            viewAndDataNewBean.setViewUuid(viewUuid);
        }
        ViewDefinitionNewBean viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);

        // 解析默认条件里定义的参数，来源url链接
        // 获取视图的分页信息
        PageDefinitionNew pageDefinitionNew = new PageDefinitionNew();
        if (0 == (page.getCurrentPage())) {
            page.setCurrentPage(1);
        }

        // ??
        if (count == null) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
            if (pageDefinitionNew.getIsPaging() == true) {
                if (pageDefinitionNew.getPageNum() == null) {
                    pageDefinitionNew.setPageNum(10);
                }
                if (StringUtils.isNotBlank(pageCurrentPage)) {
                    page.setCurrentPage(Integer.valueOf(pageCurrentPage));
                }
                page.setPageSize(pageDefinitionNew.getPageNum());
            }
        } else {
            pageDefinitionNew.setIsPaging(false);
            pageDefinitionNew.setPageNum(Integer.valueOf(count));
            page.setCurrentPage(-1);
        }

        // 视图是否分页
        viewDefinitionNewBean.setPageAble(viewDefinitionNewBean.getPageDefinitionNews().getIsPaging());
        // 获取视图的查询定义信息
        SelectDefinitionNew selectDefinitionNew = viewDefinitionNewBean.getSelectDefinitionNews();
        // 获取视图的自定义按钮信息
        Set<CustomButtonNew> buttonBeans = viewDefinitionNewBean.getCustomButtonNews();
        // 获取有权限的自定义按钮
        getViewDataNewService.setCustomButtonRights(viewDefinitionNewBean, buttonBeans);
        // 组合查询按钮HTML工具类
        AssemblyHtmlUtil assemblyHtmlUtil = new AssemblyHtmlUtil(viewDefinitionNewBean, request);
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();
        // 获取表的总记录数

        // 根据视图的条件获取数据源的数据

        dyViewQueryInfoNew.setPageInfo(page);
        // 复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionNewBean.getCheckKey()) ? "uuid" : viewDefinitionNewBean
                .getCheckKey();
        // 获取视图的默认搜索条件
        String defaultCondition = dispsoseDefaultCondition(viewDefinitionNewBean.getDefaultCondition(), request, null);
        List<QueryItem> queryItems = viewDefinitionNewService.getViewData(defaultCondition, whereSql,
                viewDefinitionNewBean.getTableDefinitionId(), columnDefinitionNews, pageDefinitionNew,
                dyViewQueryInfoNew, rowIdKey);
        long queryItemCount = dyViewQueryInfoNew.getPageInfo().getTotalCount();
        page.setTotalCount(queryItemCount);
        if (viewDefinitionNewBean.getIsRead() != null && viewDefinitionNewBean.getIsRead()) {
            // 启用已读未读属性
            String readKey = StringUtils.isBlank(viewDefinitionNewBean.getReadKey()) ? "uuid" : viewDefinitionNewBean
                    .getReadKey();
            readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
        }
        viewAndDataNewBean.setData(queryItems);
        viewAndDataNewBean.setViewDefinitionBean(viewDefinitionNewBean);

        model.addAttribute("count", count);
        model.addAttribute("absoluteWidth", viewDefinitionNewBean.getAbsoluteWidth());
        model.addAttribute("fieldSelects", selectDefinitionNew.getFieldSelects());
        model.addAttribute("mark", "viewShow");
        model.addAttribute("selectTemplate", assemblyHtmlUtil.assemblySelectTemplateHtml());
        model.addAttribute("condSelect", assemblyHtmlUtil.assemblyConditionSelectHtml());
        model.addAttribute("keySelect", assemblyHtmlUtil.assemblyKeySelectHtml());
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinitionNew);
        String titleSource = assemblyHtmlUtil.assemblyColumntTitleHtml();
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("colSource", StringUtils.isNotBlank(titleSource) ? assemblyHtmlUtil.assemblyColHtml() : "");
        model.addAttribute("buttonTemplate", assemblyHtmlUtil.assemblyButtonTemplateHtmlOne());
        model.addAttribute("buttonTemplate2", assemblyHtmlUtil.assemblyButtonTemplateHtmlTwo());
        model.addAttribute("template", assemblyHtmlUtil.assemblyColumnTemplateHtml(queryItems));
        model.addAttribute("openBy", openBy);
        model.addAttribute("columnDefinitions", columnDefinitionNews);
        model.addAttribute("viewDefinitionBean", viewDefinitionNewBean);
        model.addAttribute("selectDefinition", selectDefinitionNew);
        model.addAttribute("viewAndDataBean", viewAndDataNewBean);
        model.addAttribute("queryItemCount", queryItemCount < 0 ? 0 : queryItemCount);
        model.addAttribute("srcList", assemblyHtmlUtil.getSrcList());
        model.addAttribute("parmStr", assemblyHtmlUtil.assemblyParamsStr(openBy, dyViewQueryInfoNew.getExpandParams()));
        model.addAttribute("htmlParmStr", assemblyHtmlUtil.assemblyParamsHtmlStr(dyViewQueryInfoNew.getExpandParams()));
        model.addAttribute("extendsParams", extendsParams(dyViewQueryInfoNew.getExpandParams()));
        return forward("/basicdata/view/view_explain_new");
    }

    /**
     * 视图的控制层调用入口(直接预览后的分页控制层入口)
     *
     * @param model
     * @param viewUuid
     * @param dyViewQueryInfoNew
     * @param expandParams
     * @param openBy
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/view_show_forpage", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewDataForPage(Model model, @RequestBody DyViewQueryInfoNew dyViewQueryInfoNew,
                                     HttpServletRequest request) throws Exception {
        String whereSql = request.getParameter("whereSql");
        if (dyViewQueryInfoNew == null) {
            dyViewQueryInfoNew = new DyViewQueryInfoNew();
        }
        Map<String, String> expandParams = dyViewQueryInfoNew.getExpandParams();
        if (expandParams == null) {
            expandParams = new HashMap<String, String>();
        }
        String viewUuid = StringUtils.trimToEmpty(dyViewQueryInfoNew.getViewUuid());

        // 视图名称
        String viewName = dyViewQueryInfoNew.getViewName();

        // 视图的条数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String count = expandParams.get("count");
        // 关联数据所用的参数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        /************************获取视图的定义bean开始***********************/
        ViewAndDataNewBean viewAndDataNewBean = new ViewAndDataNewBean();
        if (StringUtils.isNotBlank(viewUuid)) {
            viewAndDataNewBean.setViewUuid(viewUuid);
        } else {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewService.getByViewId(viewName);
            viewUuid = viewDefinitionNew.getUuid();
            viewAndDataNewBean.setViewUuid(viewUuid);
        }
        ViewDefinitionNewBean viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        /************************获取视图的定义信息结束***********************/
        // 数据源的ID
        // 启用已读未读属性
        String readKey = StringUtils.isBlank(viewDefinitionNewBean.getReadKey()) ? "uuid" : viewDefinitionNewBean
                .getReadKey();
        // 复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionNewBean.getCheckKey()) ? "uuid" : viewDefinitionNewBean
                .getCheckKey();
        // 获取视图的默认搜索条件
        /*******************************解析视图的默认搜索条件中特定的变量********************************/
        String defaultCondition = dispsoseDefaultCondition(viewDefinitionNewBean.getDefaultCondition(), request,
                dyViewQueryInfoNew.getExpandParams());

        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        // 获取视图的分页信息
        PageDefinitionNew pageDefinitionNew = new PageDefinitionNew();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfoNew.getPageInfo() != null) {
            page = dyViewQueryInfoNew.getPageInfo();
        } else {
            if (0 == (page.getCurrentPage())) {
                page.setCurrentPage(1);
            }
        }

        // ??
        if (count == null) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
        } else if (Integer.valueOf(count) == 000) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
            pageDefinitionNew.setPageNum(5);
        } else {
            pageDefinitionNew.setIsPaging(false);
        }
        // 视图是否分页
        viewDefinitionNewBean.setPageAble(viewDefinitionNewBean.getPageDefinitionNews().getIsPaging());
        // 获取视图的自定义按钮信息
        Set<CustomButtonNew> buttonBeans = viewDefinitionNewBean.getCustomButtonNews();
        // 获取有权限的自定义按钮
        getViewDataNewService.setCustomButtonRights(viewDefinitionNewBean, buttonBeans);
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();
        // 组合查询按钮HTML工具类
        AssemblyHtmlUtil assemblyHtmlUtil = new AssemblyHtmlUtil(viewDefinitionNewBean, request);
        // 获取表的总记录数
        Long totalCount = -1L;
        totalCount = dataSourceApiFacade.countForView(viewDefinitionNewBean.getTableDefinitionId(), defaultCondition,
                new HashMap<String, Object>());
        if (totalCount != -1L) {
            dyViewQueryInfoNew.getPageInfo().setTotalCount(totalCount);
        }
        // 获取视图自定义按钮
        // 点击事件跳转的url
        long queryItemCount = 0;
        // 获取列标题模板
        // 根据视图的条件获取数据源的数据
        dyViewQueryInfoNew.setPageInfo(page);
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        queryItems = viewDefinitionNewService.getViewData(defaultCondition, whereSql,
                viewDefinitionNewBean.getTableDefinitionId(), columnDefinitionNews, pageDefinitionNew,
                dyViewQueryInfoNew, rowIdKey);
        queryItemCount = dyViewQueryInfoNew.getPageInfo().getTotalCount();
        page.setTotalCount(queryItemCount);
        if (viewDefinitionNewBean.getIsRead() != null && viewDefinitionNewBean.getIsRead()) {
            readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
        }
        viewAndDataNewBean.setData(queryItems);
        viewAndDataNewBean.setViewDefinitionBean(viewDefinitionNewBean);

        if (dyViewQueryInfoNew.getExpandParams() != null) {
            model.addAttribute("viewAskType", dyViewQueryInfoNew.getExpandParams().get("viewAskType"));
        }
        model.addAttribute("absoluteWidth", viewDefinitionNewBean.getAbsoluteWidth());
        model.addAttribute("orderbyArr", "");
        model.addAttribute("title", "");
        model.addAttribute("mark", "viewShow");
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinitionNew);

        String titleSource = assemblyHtmlUtil.assemblyColumntTitleHtml();
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("colSource", StringUtils.isNotBlank(titleSource) ? assemblyHtmlUtil.assemblyColHtml() : "");
        model.addAttribute("template", assemblyHtmlUtil.assemblyColumnTemplateHtml(queryItems));
        model.addAttribute("columnDefinitions", columnDefinitionNews);
        model.addAttribute("viewDefinitionBean", viewDefinitionNewBean);
        model.addAttribute("viewAndDataBean", viewAndDataNewBean);
        model.addAttribute("queryItemCount", queryItemCount);
        model.addAttribute("srcList", assemblyHtmlUtil.getSrcList());
        model.addAttribute("parmStr", assemblyHtmlUtil.assemblyParamsStr(dyViewQueryInfoNew.getOpenBy(),
                dyViewQueryInfoNew.getExpandParams()));
        model.addAttribute("extendsParams", extendsParams(dyViewQueryInfoNew.getExpandParams()));
        // model.addAttribute("htmlParmStr", htmlParmStr);
        return forward("/basicdata/view/view_update_new");
    }

    /**
     * 视图的控制层调用入口(进来后做了点击排序、关键字查询等操作后的)
     *
     * @param model
     * @param viewUuid
     * @param dyViewQueryInfoNew
     * @param expandParams
     * @param openBy
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/view_show_param", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getViewDataByParam(Model model, @RequestBody DyViewQueryInfoNew dyViewQueryInfoNew,
                                     HttpServletRequest request) throws Exception {
        String whereSql = request.getParameter("whereSql");
        if (dyViewQueryInfoNew == null) {
            dyViewQueryInfoNew = new DyViewQueryInfoNew();
        }
        Map<String, String> expandParams = dyViewQueryInfoNew.getExpandParams();
        if (dyViewQueryInfoNew.getExpandParams() == null) {
            expandParams = new HashMap<String, String>();
        }
        String viewUuid = StringUtils.trimToEmpty(dyViewQueryInfoNew.getViewUuid());

        // 视图的条数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String count = expandParams.get("count");
        /************************获取视图的定义bean开始***********************/
        ViewAndDataNewBean viewAndDataNewBean = new ViewAndDataNewBean();
        ViewDefinitionNewBean viewDefinitionNewBean = new ViewDefinitionNewBean();
        if (StringUtils.isNotBlank(viewUuid)) {
            viewAndDataNewBean.setViewUuid(viewUuid);
            viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        } else {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewService
                    .getByViewId(dyViewQueryInfoNew.getViewName());
            viewUuid = viewDefinitionNew.getUuid();
            viewAndDataNewBean.setViewUuid(viewUuid);
            viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        }
        /************************获取视图的定义信息结束***********************/
        // 数据源的ID
        // 启用已读未读属性
        String readKey = StringUtils.isBlank(viewDefinitionNewBean.getReadKey()) ? "uuid" : viewDefinitionNewBean
                .getReadKey();
        // 复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionNewBean.getCheckKey()) ? "uuid" : viewDefinitionNewBean
                .getCheckKey();
        // 获取视图的默认搜索条件
        String defaultCondition = dispsoseDefaultCondition(viewDefinitionNewBean.getDefaultCondition(), request,
                dyViewQueryInfoNew.getExpandParams());
        // 解析默认条件里定义的参数，来源url链接

        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        // 获取视图的分页信息
        PageDefinitionNew pageDefinitionNew = new PageDefinitionNew();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfoNew.getPageInfo() != null) {
            page = dyViewQueryInfoNew.getPageInfo();
        } else {
            if (0 == (page.getCurrentPage())) {
                page.setCurrentPage(1);
            }
        }
        // ??
        if (count == null) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
        } else if (Integer.valueOf(count) == 0) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
            pageDefinitionNew.setPageNum(5);
        } else {
            pageDefinitionNew.setIsPaging(false);
        }
        // 视图是否分页
        viewDefinitionNewBean.setPageAble(viewDefinitionNewBean.getPageDefinitionNews().getIsPaging());
        // 获取视图的自定义按钮信息
        Set<CustomButtonNew> buttonBeans = viewDefinitionNewBean.getCustomButtonNews();
        // 获取有权限的自定义按钮
        getViewDataNewService.setCustomButtonRights(viewDefinitionNewBean, buttonBeans);
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionNews = new HashSet<ColumnDefinitionNew>();
        // 组合查询按钮HTML工具类
        AssemblyHtmlUtil assemblyHtmlUtil = new AssemblyHtmlUtil(viewDefinitionNewBean, request);
        columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();

        // 如果分页存在,设置分页信息
        if (pageDefinitionNew.getIsPaging() == true) {
            if (pageDefinitionNew.getPageNum() == null) {
                pageDefinitionNew.setPageNum(10);
            }
            page.setTotalCount(0);
            if (page.getPageSize() == 0) {
                page.setPageSize(pageDefinitionNew.getPageNum());
            }
        } else {
            page.setCurrentPage(-1);
        }
        // 点击事件跳转的url
        // 获取列标题模板
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        queryItems = viewDefinitionNewService.getViewData(defaultCondition, whereSql,
                viewDefinitionNewBean.getTableDefinitionId(), columnDefinitionNews, pageDefinitionNew,
                dyViewQueryInfoNew, rowIdKey);
        page.setTotalCount(dyViewQueryInfoNew.getPageInfo().getTotalCount());
        if (viewDefinitionNewBean.getIsRead() != null && viewDefinitionNewBean.getIsRead()) {
            readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
        }
        viewAndDataNewBean.setData(queryItems);
        viewAndDataNewBean.setViewDefinitionBean(viewDefinitionNewBean);
        if (dyViewQueryInfoNew.getExpandParams() != null) {
            model.addAttribute("viewAskType", dyViewQueryInfoNew.getExpandParams().get("viewAskType"));
        }
        model.addAttribute("absoluteWidth", viewDefinitionNewBean.getAbsoluteWidth());
        model.addAttribute("loadJs", "false");
        model.addAttribute("orderbyArr", "");
        model.addAttribute("title", "");
        model.addAttribute("mark", "viewSelect");
        if (page.getTotalCount() == 0) {
            page.setCurrentPage(0);
        }
        model.addAttribute("page", page);
        model.addAttribute("pageDefinition", pageDefinitionNew);
        String titleSource = assemblyHtmlUtil.assemblyColumntTitleHtml();
        model.addAttribute("titleSource", titleSource);
        model.addAttribute("colSource", StringUtils.isNotBlank(titleSource) ? assemblyHtmlUtil.assemblyColHtml() : "");
        model.addAttribute("template", assemblyHtmlUtil.assemblyColumnTemplateHtml(queryItems));
        model.addAttribute("columnDefinitions", columnDefinitionNews);
        model.addAttribute("viewDefinitionBean", viewDefinitionNewBean);
        model.addAttribute("viewAndDataBean", viewAndDataNewBean);
        model.addAttribute("queryItemCount", dyViewQueryInfoNew.getPageInfo().getTotalCount());
        model.addAttribute("srcList", assemblyHtmlUtil.getSrcList());
        model.addAttribute("parmStr", assemblyHtmlUtil.assemblyParamsStr(dyViewQueryInfoNew.getOpenBy(),
                dyViewQueryInfoNew.getExpandParams()));
        model.addAttribute("extendsParams", extendsParams(dyViewQueryInfoNew.getExpandParams()));
        return forward("/basicdata/view/view_update_new");
    }

    /**
     * 视图的控制层调用入口(进来后做了点击排序、关键字查询等操作后的)
     *
     * @param model
     * @param dyViewQueryInfo
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/loadViewData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ViewAndDataNewBean loadViewData(Model model, @RequestBody DyViewQueryInfoNew dyViewQueryInfoNew,
                                           HttpServletRequest request) throws Exception {
        String whereSql = request.getParameter("whereSql");
        if (dyViewQueryInfoNew == null) {
            dyViewQueryInfoNew = new DyViewQueryInfoNew();
        }
        Map<String, String> expandParams = dyViewQueryInfoNew.getExpandParams();
        if (dyViewQueryInfoNew.getExpandParams() == null) {
            expandParams = new HashMap<String, String>();
        }
        String viewUuid = StringUtils.trimToEmpty(dyViewQueryInfoNew.getViewUuid());

        // 视图的条数，由前台传递过来，传递的格式是必须放在组装的map里面，且key为count
        String count = expandParams.get("count");
        /************************获取视图的定义bean开始***********************/
        ViewAndDataNewBean viewAndDataNewBean = new ViewAndDataNewBean();
        ViewDefinitionNewBean viewDefinitionNewBean = new ViewDefinitionNewBean();
        if (StringUtils.isNotBlank(viewUuid)) {
            viewAndDataNewBean.setViewUuid(viewUuid);
            viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        } else {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewService
                    .getByViewId(dyViewQueryInfoNew.getViewName());
            viewUuid = viewDefinitionNew.getUuid();
            viewAndDataNewBean.setViewUuid(viewUuid);
            viewDefinitionNewBean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        }
        /************************获取视图的定义信息结束***********************/
        // 数据源的ID
        // 启用已读未读属性
        String readKey = StringUtils.isBlank(viewDefinitionNewBean.getReadKey()) ? "uuid" : viewDefinitionNewBean
                .getReadKey();
        // 复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionNewBean.getCheckKey()) ? "uuid" : viewDefinitionNewBean
                .getCheckKey();
        // 获取视图的默认搜索条件
        String defaultCondition = dispsoseDefaultCondition(viewDefinitionNewBean.getDefaultCondition(), request,
                dyViewQueryInfoNew.getExpandParams());
        // 解析默认条件里定义的参数，来源url链接

        /*******************************解析视图的默认搜索条件中特定的变量结束********************************/
        // 获取视图的分页信息
        PageDefinitionNew pageDefinitionNew = new PageDefinitionNew();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfoNew.getPageInfo() != null) {
            page = dyViewQueryInfoNew.getPageInfo();
        } else {
            if (0 == (page.getCurrentPage())) {
                page.setCurrentPage(1);
            }
        }
        // ??
        if (count == null) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
        } else if (Integer.valueOf(count) == 0) {
            pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
            pageDefinitionNew.setPageNum(5);
        } else {
            pageDefinitionNew.setIsPaging(false);
        }
        // 视图是否分页
        viewDefinitionNewBean.setPageAble(viewDefinitionNewBean.getPageDefinitionNews().getIsPaging());
        // 获取视图的自定义按钮信息
        Set<CustomButtonNew> buttonBeans = viewDefinitionNewBean.getCustomButtonNews();
        // 获取有权限的自定义按钮
        getViewDataNewService.setCustomButtonRights(viewDefinitionNewBean, buttonBeans);
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionNews = new HashSet<ColumnDefinitionNew>();
        // 组合查询按钮HTML工具类
        columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();

        // 如果分页存在,设置分页信息
        if (pageDefinitionNew.getIsPaging() == true) {
            if (pageDefinitionNew.getPageNum() == null) {
                pageDefinitionNew.setPageNum(10);
            }
            page.setTotalCount(0);
            if (page.getPageSize() == 0) {
                page.setPageSize(pageDefinitionNew.getPageNum());
            }
        } else {
            page.setCurrentPage(-1);
        }
        // 点击事件跳转的url
        // 获取列标题模板
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        queryItems = viewDefinitionNewService.getViewData(defaultCondition, whereSql,
                viewDefinitionNewBean.getTableDefinitionId(), columnDefinitionNews, pageDefinitionNew,
                dyViewQueryInfoNew, rowIdKey);
        page.setTotalCount(dyViewQueryInfoNew.getPageInfo().getTotalCount());
        if (viewDefinitionNewBean.getIsRead() != null && viewDefinitionNewBean.getIsRead()) {
            readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (QueryItem item : queryItems) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (ColumnDefinitionNew column : viewDefinitionNewBean.getColumnDefinitionNews()) {
                Object value = item.get(QueryItem.getKey(column.getColumnAliase()));
                if (value instanceof Clob) {
                    value = ClobUtils.ClobToString((Clob) value);
                }
                map.put(column.getColumnAliase(), value);
            }
            result.add(map);
        }
        viewAndDataNewBean.setData(result);
        viewAndDataNewBean.setTotal(page.getTotalCount());
        viewAndDataNewBean.setViewDefinitionBean(null);
        return viewAndDataNewBean;
    }

    public String getColSource(@RequestParam("showCheckBox") boolean showCheckBox,
                               Set<ColumnDefinitionNew> columnDefinitionNews) {
        StringBuilder colSource = new StringBuilder();
        colSource.append("<colgroup>");
        String widthType = "1";
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String cWidth = columnDefinitionNew.getWidth();
            if (columnDefinitionNew.getHidden() != true && StringUtils.isNotBlank(cWidth) && cWidth.indexOf("%") > -1) {
                widthType = "2";
                break;
            }
        }
        // 是否显示复选框
        if (showCheckBox) {
            if (widthType.equals("2")) {
                colSource.append("<col style='width:5%'>");
            } else {
                colSource.append("<col style='width:15px'>");
            }
        }
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if (columnDefinitionNew.getHidden() != true) {
                String width = columnDefinitionNew.getWidth();
                colSource.append("<col style='width:" + width + "'>");
            }
        }
        colSource.append("</colgroup>");
        return colSource.toString();
    }

    /**
     * 获取列标题模板
     *
     * @return
     */
    public String getTitleSource(@RequestParam("showTitle") boolean showTitle,
                                 @RequestParam("showCheckBox") boolean showCheckBox, Set<ColumnDefinitionNew> columnDefinitionNews,
                                 Map<String, ColumnDefinitionNew> columnFields, @RequestParam("datascope") String viewUuid) {

        StringBuilder titleSource = new StringBuilder();
        String fieldName = null;
        if (showTitle == true) {
            titleSource.append("<tr class='thead_tr'>");
        }
        String widthType = "1";
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String cWidth = columnDefinitionNew.getWidth();
            if (columnDefinitionNew.getHidden() != true && StringUtils.isNotBlank(cWidth) && cWidth.indexOf("%") > -1) {
                widthType = "2";
                break;
            }
        }
        // 是否显示复选框
        if (showCheckBox) {
            if (widthType.equals("2")) {
                titleSource.append("<td width='5%' class='checks_td'><input type='checkbox' class='checkall'/></td>");
            } else {
                titleSource.append("<td width='15px' class='checks_td'><input type='checkbox' class='checkall'/></td>");
            }
        }
        int j = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if (columnDefinitionNew.getHidden() == false
                    && !(columnDefinitionNew.getShowLine() != null && columnDefinitionNew.getShowLine().equals("第二行"))) {
                j++;
            }
        }
        int i = 1;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String titleName;
            if (columnDefinitionNew.getOtherName() != null && !columnDefinitionNew.getOtherName().equals("")) {
                titleName = columnDefinitionNew.getOtherName();
            } else {
                titleName = columnDefinitionNew.getTitleName();
            }
            if (columnDefinitionNew.getEntityName() != null) {
                fieldName = columnDefinitionNew.getColumnAliase();
            } else {
                fieldName = columnDefinitionNew.getFieldName();
            }

            boolean hidden = columnDefinitionNew.getHidden();
            boolean sortAble = columnDefinitionNew.getSortAble();
            columnFields.put(fieldName, columnDefinitionNew);
            if (hidden == false && showTitle == true && showFirstLine(columnDefinitionNew.getShowLine())) {
                if (sortAble == true) {
                    if (i == j) {
                        titleSource.append("<td class='sortAble last' orderby='asc' >" + titleName + "</td>");
                    } else {
                        titleSource.append("<td class='sortAble' orderby='asc' >" + titleName + "</td>");
                    }
                } else {
                    if (i == j) {
                        titleSource.append("<td class='last' >" + titleName + "</td>");
                    } else {
                        titleSource.append("<td >" + titleName + "</td>");
                    }
                }
                i++;
            }
        }
        if (showTitle == true) {
            titleSource.append("</tr>");
        }
        return titleSource.toString();
    }

    private String extendsParams(Map<String, String> expandParamsMap) throws JSONException {
        JSONObject json = new JSONObject();
        if (!CollectionUtils.isEmpty(expandParamsMap)) {
            for (String key : expandParamsMap.keySet()) {
                if (StringUtils.isNotBlank(key) && key.startsWith("expandParams_")) {
                    json.put(key.replaceFirst("expandParams_", ""), expandParamsMap.get(key));
                }
            }
        }
        return json.toString();
    }

    @RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<QueryItem> test(@RequestParam("viewUuid") String viewUuid) {
        return viewDefinitionNewService.getViewDataByKey(viewUuid, "ID");
    }

}