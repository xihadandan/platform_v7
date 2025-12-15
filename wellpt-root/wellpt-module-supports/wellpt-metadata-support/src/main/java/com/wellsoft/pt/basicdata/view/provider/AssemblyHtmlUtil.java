package com.wellsoft.pt.basicdata.view.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.*;
import com.wellsoft.pt.basicdata.view.support.DyviewConfigNew;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import ognl.Ognl;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Clob;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AssemblyHtmlUtil {

    private static final String TR_BEGIN_STR = "<tr class='fieldSelectTr'>";
    private static final String TR_END_STR = "</tr>";
    private static final String TD_BEGIN_STR = "<td class='fieldSelectTd'>";
    private static final String TD_END_STR = "</td>";
    private static final String FIELD_COND_TITLE_STR = "<td class='fieldSelectTitleTd' width='10%'>${titleName}</td>";
    private static final String TOOL_BOTTOM_DIV_BEGIN = "<div class='view_tool_bottom'>";
    private static final String TOOL_CUSTOM_BOTTOM_DIV_BEGIN = "<div class='customButton customButton_top'>";
    private static final String TOOL_BOTTOM_GROUP_DIV_BEGIN = "<div class='customButton_group'>";
    private static final String TOOL_BOTTOM_GROUP_ITEM_DIV_BEGIN = "<div class='customButton_group_item'>";
    private static final String TOOL_BOTTOM_GROUP_BUTTONS_BOTTOM_DIV_BEGIN = "<div class='customButton_group_buttons_bottom'>";
    private static final String TOOL_BOTTOM_GROUP_BUTTONS_DIV_BEGIN = "<div class='customButton_group_buttons'>";
    private static final String TOOL_BUTTON_GROUP_NAME_STR = "<div class='customButton_group_name'><div class='customButton_group_name_text'>${buttonGroupName}</div><div class='select_icon'></div></div>";
    private static final String TOOL_BUTTON_GROUP_BUTTON_STR = "<div class=\"customButton_group_button\" place=\"place_top\" value=\"${cbbCode}\"  onclick=\"${jsFunction}\">${cbbName}</div>";
    private static final String TOOL_PLACE_TOP_STR = "<button place=\"place_top\" type=\"button\" value=\"${cbbCode}\"  onclick=\"${jsFunction}\">${cbbName}</button>";
    private static final String DIV_END = "</div>";
    private static final String VIEW_TOOL2_DIV_BEGIN = "<div class='view_tool2'>";
    private static final String VIEW_KEYWORD_INPUT = "<input type='text' value='关键字搜索' onblur=\"if(this.value =='') this.value = '关键字搜索'\" onfocus=\"if(this.value == '关键字搜索') this.value = ''\" name='keyWord' id='keyWord' autocomplete='off'/>";
    private static final String KEY_SELECT_QUERY_BTN = "<button id='keySelect' type='button'>查询</button>";
    private static final String QUERY_HIDDEN_BTN = "<button id='showButton' type='button'>↑</button>";
    private static final String FIELD_SELECT_QUERY_BTN_HIDE = "<button id='fieldSelect' type='button' style='display:none;'>查询</button>";
    private static final String VIEW_KEYWORD_DIV_BEGIN = "<div class='view_keyword_div'>";
    private static final String FIELD_SELECT_QUERY_BTN = "<button id='fieldSelect' type='button'>查询</button>";
    private static final String VIEW_SEARCH_TABLE_HIDE_BEGIN = "<table class='view_search' style='width:100%;display:none;'>";
    private static final String VIEW_SEARCH_TABLE_BEGIN = "<table class='view_search' style='width:100%;'>";
    private static final String COMLUMN_HTML_1 = "<td field='${field}' title=\"${content}\" width='${width}' >${content}</td>";
    private static final String COMLUMN_HTML_2 = "<td field='${field}' width='${width}' >${content}</td>";
    private static final String BUTTON_TEMPLATE_STR = "<button place=\"${place}\" type=\"button\" value=\"${cbbCode}\"  onclick=\"${jsFunction}\">${cbbName}</button>";
    private static final String PLACE = "place";
    private static final String FIELD = "field";
    private static final String TITLE = "title";
    private static final String WIDTH = "width";
    private static final String CONTENT = "content";
    private static final String TABLE_END = "</table>";
    private static final String TITLE_NAME = "titleName";
    private static final String FIELD_NAME = "fieldName";
    private static final String SELECT_TYPE_ID = "selectTypeId";
    private static final String DATA_SOURCE_ID = "dataSourceId";
    private static final String SELECT_NAME_COLUMN = "selectNameColumn";
    private static final String SELECT_VALUE_COLUMN = "selectValueColumn";
    private static final String OPTION_DATA_SOURCE = "optionDataSource";
    private static final String EXACT_VALUE = "exactValue";
    private static final String CONTENT_FORMAT = "contentFormat";
    private static final String ORG_INPUT_MODE = "orgInputMode";
    private static final String OPT_DATA = "optdata";
    private static final String CBB_CODE = "cbbCode";
    private static final String CBB_NAME = "cbbName";
    private static final String JS_FUNCTION = "jsFunction";
    private static final String SUPPLEMENT_STR = "<TD></TD><TD></TD></TR>";
    private static final Map<String, String> fieldCondHtmlMap = new HashMap<String, String>();

    static {
        fieldCondHtmlMap
                .put("TEXT_AREA",
                        "<div class='fieldSelectDiv'><input class='inputClass' type='text' id='${fieldName}_first' selectTypeId='${selectTypeId}' isArea='true' fieldName='${fieldName}'/>至<input class='inputClass' type='text' id='${fieldName}_last' selectTypeId='${selectTypeId}' isArea='true' fieldName='${fieldName}'/></div>");
        fieldCondHtmlMap
                .put("TEXT_EXACT",
                        "<input class='inputClass' type='text' id='${fieldName}' exactValue='${exactValue}' selectTypeId='${selectTypeId}' isExact='true'/>");
        fieldCondHtmlMap
                .put("TEXT_LIKE",
                        "<input class='inputClass' type='text' id='${fieldName}' isLike='true' id='${fieldName}' selectTypeId='${selectTypeId}'/>");
        fieldCondHtmlMap
                .put("DATE",
                        "<div class='fieldSelectDiv'><input class='inputClass' type='hidden' name='${fieldName}_begin' id='${fieldName}_begin' contentFormat='${contentFormat}' selectTypeId='${selectTypeId}' searchField='${fieldName}'/>至<input class='inputClass' type='hidden' name='${fieldName}_end' id='${fieldName}_end' contentFormat='${contentFormat}' selectTypeId='${selectTypeId}'/></div>");
        fieldCondHtmlMap
                .put("ORG",
                        "<input class='inputClass' type='hidden' id='${fieldName}' name='${fieldName}' orgInputMode='${orgInputMode}' selectTypeId='${selectTypeId}'/>");
        fieldCondHtmlMap
                .put("SELECT",
                        "<input class='inputClass' type='hidden' id='${fieldName}' name='${fieldName}' selectTypeId='${selectTypeId}' dataSourceId='${dataSourceId}' selectNameColumn='${selectNameColumn}'  selectValueColumn='${selectValueColumn}' optionDataSource='${optionDataSource}' optdata='${optdata}'>");
        fieldCondHtmlMap
                .put("RADIO",
                        "<input class='inputClass' type='hidden' id='${fieldName}' name='${fieldName}' selectTypeId='${selectTypeId}' dataSourceId='${dataSourceId}' selectNameColumn='${selectNameColumn}'  selectValueColumn='${selectValueColumn}' optionDataSource='${optionDataSource}' optdata='${optdata}'>");
        fieldCondHtmlMap
                .put("CHECKBOX",
                        "<input class='inputClass' type='hidden' id='${fieldName}' name='${fieldName}' selectTypeId='${selectTypeId}' dataSourceId='${dataSourceId}' selectNameColumn='${selectNameColumn}'  selectValueColumn='${selectValueColumn}' optionDataSource='${optionDataSource}' optdata='${optdata}'>");
        fieldCondHtmlMap
                .put("DIAlOG",
                        "<input class='inputClass' type='hidden' id='${fieldName}' name='${fieldName}' selectTypeId='${selectTypeId}'></td>");
    }

    private SelectDefinitionNew selectDefinitionNew;
    private Set<CustomButtonNew> viewbutton;
    private ViewDefinitionNewBean viewDefinitionNewBean;
    private Set<ColumnDefinitionNew> columnDefinitionNews;
    private HttpServletRequest request;
    private Map<String, ColumnDefinitionNew> columnFields = new LinkedHashMap<String, ColumnDefinitionNew>();

    public AssemblyHtmlUtil(ViewDefinitionNewBean viewDefinitionNewBean, HttpServletRequest request) {
        this.viewDefinitionNewBean = viewDefinitionNewBean;
        this.selectDefinitionNew = viewDefinitionNewBean.getSelectDefinitionNews();
        this.columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();
        this.viewbutton = viewDefinitionNewBean.getCustomButtonNews();
        this.request = request;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            columnFields.put(getFieldName(columnDefinitionNew), columnDefinitionNew);
        }
    }

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

    public static boolean isFieldSelect(SelectDefinitionNew selectDefinitionNew) {
        return selectDefinitionNew.getForFieldSelect() == true && selectDefinitionNew.getFieldSelects().size() > 0;
    }

    public StringBuilder assemblySelectTemplateHtml() throws Exception {
        StringBuilder selectTemplate = new StringBuilder();
        selectTemplate.append(selectDefinitionNew.getForKeySelect() ? VIEW_SEARCH_TABLE_HIDE_BEGIN
                : VIEW_SEARCH_TABLE_BEGIN);
        selectTemplate.append(assemblyFieldSelectHtml());
        selectTemplate.append(assemblyConditionSelectHtml());
        selectTemplate.append(assemblyKeySelectHtml());
        selectTemplate.append(TABLE_END);
        return selectTemplate;
    }

    /**
     * 获取参数URL拼串
     *
     * @param defaultCondition
     * @return
     */
    public String assemblyParamsStr(String openBy, Map<String, String> otherpMap) {
        StringBuilder parmStr = new StringBuilder();
        Map<String, String[]> pMap = request.getParameterMap();
        for (String pkey : pMap.keySet()) {
            if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                parmStr.append("&").append(pkey).append("=").append(pMap.get(pkey)[0]);
            }
        }
        if (otherpMap != null) {
            for (String pkey : otherpMap.keySet()) {
                if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                    parmStr.append("&").append(pkey).append("=").append(otherpMap.get(pkey));
                }
            }
        }
        if (StringUtils.isNotBlank(openBy)) {
            parmStr.append("&openBy=").append(openBy);
        }

        return parmStr.toString();
    }

    public List<Map<String, String>> getSrcList() {
        List<Map<String, String>> srcList = new ArrayList<Map<String, String>>();
        if (StringUtils.isNotBlank(viewDefinitionNewBean.getJsSrc())) {
            String[] srcTemp = viewDefinitionNewBean.getJsSrc().split(",");
            for (int j = 0; j < srcTemp.length; j++) {
                if (StringUtils.isNotBlank(srcTemp[j])) {
                    Map<String, String> srcMap = new HashMap<String, String>();
                    srcMap.put("src", srcTemp[j]);
                    srcList.add(srcMap);
                }
            }
        }
        return srcList;
    }

    /**
     * 获取参数HTML拼串
     *
     * @param defaultCondition
     * @return
     */
    public String assemblyParamsHtmlStr(Map<String, String> otherpMap) {
        StringBuilder htmlParmStr = new StringBuilder();
        Map<String, String[]> pMap = request.getParameterMap();
        for (String pkey : pMap.keySet()) {
            if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                htmlParmStr.append("<input type='hidden' id='view_param_").append(pkey).append("' value='")
                        .append(pMap.get(pkey)[0]).append("' />");
            }
        }
        if (otherpMap != null) {
            for (String pkey : otherpMap.keySet()) {
                if (!pkey.equals("viewUuid") && !pkey.equals("currentPage")) {
                    htmlParmStr.append("<input type='hidden' id='view_param_").append(pkey).append("' value='")
                            .append(otherpMap.get(pkey)).append("' />");
                }
            }
        }

        return htmlParmStr.toString();
    }

    public String assemblyColHtml() {
        StringBuilder colSource = new StringBuilder();
        colSource.append("<colgroup>");
        //是否显示复选框
        if (isTrue(viewDefinitionNewBean.getShowCheckBox())) {
            colSource.append("<col style='width:").append(widthType() == 2 ? "5%" : "15px").append("'>");
        }
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if (!isTrue(columnDefinitionNew.getHidden())) {
                colSource.append("<col style='width:" + columnDefinitionNew.getWidth() + "'>");
            }
        }
        colSource.append("</colgroup>");
        return colSource.toString();
    }

    public String assemblyColumntTitleHtml() {

        StringBuilder titleSource = new StringBuilder();
        if (isTrue(viewDefinitionNewBean.getShowTitle())) {
            titleSource.append("<tr class='thead_tr'>");
        }
        //是否显示复选框
        if (isTrue(viewDefinitionNewBean.getShowCheckBox())) {
            titleSource.append("<td width='").append(widthType() == 2 ? "5%" : "15px")
                    .append("' class='checks_td'><input type='checkbox' class='checkall'/></td>");
        }
        int j = getNeedShowTitleNum();
        int i = 1;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String titleName = StringUtils.isNotBlank(columnDefinitionNew.getOtherName()) ? columnDefinitionNew
                    .getOtherName() : columnDefinitionNew.getTitleName();
            if (!isTrue(columnDefinitionNew.getHidden())
                    && isTrue(viewDefinitionNewBean.getShowTitle())
                    && (StringUtils.isBlank(columnDefinitionNew.getShowLine()) || columnDefinitionNew.getShowLine()
                    .equals("第一行"))) {
                titleSource.append("<td ").append(getTitleCssStyle(columnDefinitionNew.getSortAble(), j == i++))
                        .append(isTrue(columnDefinitionNew.getSortAble()) ? "orderby='asc'" : "")
                        .append(" >" + titleName + "</td>");
            }
        }
        if (isTrue(viewDefinitionNewBean.getShowTitle())) {
            titleSource.append("</tr>");
        }
        return titleSource.toString();

    }

    public StringBuilder getColumnHtml() {

        StringBuilder template = new StringBuilder();
        String keyName = "dateInfo";
        //获取行模板
        String newClickEvent = replaceColumnFieldParams(viewDefinitionNewBean.getClickEvent(), keyName);
        String newUrl = replaceColumnFieldParams(viewDefinitionNewBean.getUrl(), keyName);
        //第一行按钮HTML
        StringBuilder buttonTemplate1 = getButtonHtml(keyName, "第一行", "customButton1", "place_1stline");
        //第二行按钮HTML
        StringBuilder buttonTemplate2 = getButtonHtml(keyName, "第二行", "customButton2", "place_2ndline");
        //复选框HTML
        StringBuilder checkBoxTemplate = getCheckBoxHtml(keyName);
        //显示行为第一行的HTML
        StringBuilder firstColumnHtml = getColumnHtml(keyName, "第一行");
        //显示行为第二行的HTML
        StringBuilder secondColumnHtml = getColumnHtml(keyName, "第二行");

        StringBuilder tr1 = new StringBuilder();
        tr1.append("<tr jsonStr='${jsonStr}' style='${cssStyle}' class='${classStr}' ");

        if (StringUtils.isNotBlank(newClickEvent)) {
            tr1.append(" clickjs='").append(newClickEvent).append("' ");
        }
        if (StringUtils.isNotBlank(newUrl)) {
            tr1.append(" src='").append(wrapNewUrl(newUrl)).append("' ");
        }
        tr1.append(" >");
        tr1.append(checkBoxTemplate).append(firstColumnHtml);
        if (buttonTemplate1.length() > 0) {
            tr1.append("<td class='tr_td_button' style='text-align: right;'>").append(buttonTemplate1).append("</td>");
        }
        tr1.append("</tr>");
        template.append(tr1);
        StringBuilder tr2 = new StringBuilder();
        if (hasSecondColumn() || buttonTemplate2.length() > 0) {
            if (hasSecondColumn()) {
                tr2.append("<tr jsonStr='${jsonStr}' style='${cssStyle}' class='${classStr2}'> ");
                if (checkBoxTemplate.length() > 0) {
                    tr2.append("<td width='15px'></td>");
                }
                tr2.append(secondColumnHtml);
            }
            if (buttonTemplate2.length() > 0) {
                if (tr2.length() == 0) {
                    tr2.append("<tr class='tr_bg2 even tr_bg2even '>");
                }
                tr2.append("<td class='tr_td_button' style='text-align: right;'>").append(buttonTemplate2)
                        .append("</td>");
            }
            tr2.append("</tr>");
            template.append(tr2);
        }
        return template;

    }

    public String assemblyColumnTemplateHtml(List<QueryItem> queryItems) throws Exception {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        StringBuilder template1 = new StringBuilder();
        StringBuilder template = getColumnHtml();
        /**获取视图自定义按钮开始结束**/
        if (queryItems != null) {
            for (int i = 0; i < queryItems.size(); i++) {
                Map<String, Object> root = new HashMap<String, Object>();

                QueryItem item = queryItems.get(i);
                //是否已读
                String isread = getIsReadString(item);
                JSONObject jsonStr = queryItemToJson(item);
                //获取行模板
                root.put("dateInfo", item);
                root.put("jsonStr", URLEncoder.encode(jsonStr.toString(), "utf-8"));
                root.put("cssStyle", getCssValue(item));
                root.put("classStr", getStyleString(i, isread));
                root.put("classStr2", getStyleString2(i, isread));
                template1.append(templateEngine.process(template.toString(), root));
            }
        }
        return template1.toString();
    }

    /**
     * 组合按字段查询模版
     *
     * @return HTML代码
     * @throws UnsupportedEncodingException
     */
    public StringBuilder assemblyFieldSelectHtml() throws UnsupportedEncodingException {
        StringBuilder fieldSelect = new StringBuilder();
        if (selectDefinitionNew.getForFieldSelect()) {
            int i = 0;
            //查询条件一行两列
            for (FieldSelect fs : selectDefinitionNew.getFieldSelects()) {
                if (i % 2 == 0) {
                    fieldSelect.append(TR_BEGIN_STR);
                }

                fieldSelect.append(setParamsValue(FIELD_COND_TITLE_STR, TITLE_NAME, fs.getShowName()));//名称
                fieldSelect.append(TD_BEGIN_STR);
                fieldSelect.append(initParams(fieldCondHtmlMap.get(getHtmlKey(fs)), fs));
                fieldSelect.append(TD_END_STR);
                if (i % 2 != 0) {
                    fieldSelect.append(TR_END_STR);
                }
                i++;
            }
            if (selectDefinitionNew.getFieldSelects().size() % 2 != 0) {
                fieldSelect.append(SUPPLEMENT_STR);
            }
        }
        return fieldSelect;
    }

    private void assemblyCustomBottomHtml(StringBuilder buttonTemplate, boolean buttonPlace) {
        if (viewbutton.size() > 0) {
            Set<String> buttonGroupSet = new HashSet<String>();
            buttonTemplate.append(TOOL_CUSTOM_BOTTOM_DIV_BEGIN);
            for (CustomButtonNew cbb : viewbutton) {
                //加按钮权限SecurityApiFacade.isGranted
                if (isGranted(cbb.getCode())) {
                    if (StringUtils.isNotBlank(cbb.getButtonGroup())) {
                        buttonGroupSet.add(cbb.getButtonGroup());
                    } else {
                        if (StringUtils.isNotBlank(cbb.getPlace()) && cbb.getPlace().indexOf("头部") > -1) {
                            buttonTemplate.append(initParams(TOOL_PLACE_TOP_STR, cbb));
                        }
                    }
                }
            }
            buttonTemplate.append(DIV_END);
            if (buttonGroupSet.size() > 0) {
                buttonTemplate.append(TOOL_BOTTOM_GROUP_DIV_BEGIN);
                for (String buttonGroup : buttonGroupSet) {
                    if (StringUtils.isNotBlank(buttonGroup)) {
                        buttonTemplate.append(TOOL_BOTTOM_GROUP_ITEM_DIV_BEGIN).append(
                                setParamsValue(TOOL_BUTTON_GROUP_NAME_STR, "buttonGroupName", buttonGroup));
                        if (buttonPlace) {
                            buttonTemplate.append(TOOL_BOTTOM_GROUP_BUTTONS_BOTTOM_DIV_BEGIN);
                        } else {
                            buttonTemplate.append(TOOL_BOTTOM_GROUP_BUTTONS_DIV_BEGIN);
                        }
                        for (CustomButtonNew cbb : viewbutton) {
                            if (buttonGroup.equals(cbb.getButtonGroup())) {
                                buttonTemplate.append(initParams(TOOL_BUTTON_GROUP_BUTTON_STR, cbb));
                            }
                        }
                        buttonTemplate.append(DIV_END).append(DIV_END);
                    }
                }
                buttonTemplate.append(DIV_END);
            }
        }
    }

    private boolean isButtonPalce() {
        return viewDefinitionNewBean.getButtonPlace() != null && viewDefinitionNewBean.getButtonPlace() == true;
    }

    public StringBuilder assemblyButtonTemplateHtmlOne() {
        StringBuilder buttonTemplate = new StringBuilder();
        if (viewbutton.size() > 0 || selectDefinitionNew.getForKeySelect() == true
                || selectDefinitionNew.getForFieldSelect() == true) {
            buttonTemplate.append(VIEW_TOOL2_DIV_BEGIN);
            assemblyCustomBottomHtml(buttonTemplate, false);
            if (isFieldSelect(selectDefinitionNew)) {
                if (selectDefinitionNew.getForKeySelect() == true) {
                    buttonTemplate.append(VIEW_KEYWORD_DIV_BEGIN).append(VIEW_KEYWORD_INPUT)
                            .append(FIELD_SELECT_QUERY_BTN_HIDE).append(KEY_SELECT_QUERY_BTN).append(QUERY_HIDDEN_BTN)
                            .append(DIV_END);
                } else {
                    buttonTemplate.append(VIEW_KEYWORD_DIV_BEGIN).append(FIELD_SELECT_QUERY_BTN).append(DIV_END);
                }
            }
            if (selectDefinitionNew.getForKeySelect() == true && !isFieldSelect(selectDefinitionNew)) {
                buttonTemplate.append(VIEW_KEYWORD_DIV_BEGIN).append(VIEW_KEYWORD_INPUT).append(KEY_SELECT_QUERY_BTN)
                        .append(DIV_END);
            }
            buttonTemplate.append(DIV_END);
        }
        return buttonTemplate;
    }

    public StringBuilder assemblyButtonTemplateHtmlTwo() {
        StringBuilder buttonTemplate = new StringBuilder();
        if (viewbutton.size() > 0 || selectDefinitionNew.getForKeySelect() == true && isButtonPalce()) {
            buttonTemplate.append(TOOL_BOTTOM_DIV_BEGIN);
            assemblyCustomBottomHtml(buttonTemplate, true);
            buttonTemplate.append(DIV_END);
        }
        return buttonTemplate;
    }

    /**
     * 拼接行样式
     *
     * @param fontWide  字体加粗
     * @param fontColor 字体颜色
     * @return
     */
    private String getCssStyle(String fontWide, String fontColor) {
        StringBuilder cssValue = new StringBuilder();
        cssValue.append("color:").append(fontColor).append(";");
        if (fontWide.equals("1")) {
            cssValue.append(" font-weight: bold;");
        }
        return cssValue.toString();
    }

    /**
     * 校验是否满足行样式添加规则
     *
     * @param columnCondition      条件类型
     * @param columnConditionValue 条件对比值
     * @param jsonValue            字段值
     * @return 是否样式添加规则
     * @throws Exception
     */
    private boolean cssStyleConditionVerify(String columnCondition, String columnConditionValue, String jsonValue)
            throws Exception {
        String condition = DyviewConfigNew.getDyviewColumncssCondtion().get(columnCondition);
        if (condition.equals("包含")) {
            return jsonValue.contains(columnConditionValue);
        } else if (condition.equals("不包含")) {
            return !jsonValue.contains(columnConditionValue);
        } else {
            String expression = "'" + jsonValue + "' " + condition + " '" + columnConditionValue + "'";
            return (Boolean) Ognl.getValue(expression, null);
        }
    }

    private String getStyleString(int i, String isread) {
        //公共样式
        StringBuilder rel = new StringBuilder("dataTr odd");
        //首行样式
        if (i == 0) {
            rel.append(" first");
        }
        //单双行样式
        if (i % 2 == 1) {
            rel.append(" tr_bg1");
        } else {
            rel.append(" tr_bg2");
        }
        if (StringUtils.isNotBlank(isread)) {
            rel.append(" ").append(isread);
        }
        return rel.toString();
    }

    private String getStyleString2(int i, String isread) {
        //公共样式
        StringBuilder rel = new StringBuilder("dataTr even");
        //单双行样式
        if (i % 2 == 1) {
            rel.append(" tr_bg1");
        } else {
            rel.append(" tr_bg2 tr_bg2even");
        }
        if (StringUtils.isNotBlank(isread)) {
            rel.append(" ").append(isread);
        }
        return rel.toString();
    }

    private String wrapNewUrl(String NewUrl) {
        if (!StringUtils.isBlank(NewUrl) && NewUrl.indexOf("http://") < 0) {
            NewUrl = getRequestPath(request) + NewUrl;
        }
        return NewUrl;
    }

    /**
     * 替换${params} 为对应的值
     *
     * @param text    替换的字符串
     * @param keyName 数据Key
     * @return 替换后的字符串
     */
    private String replaceColumnFieldParams(String text, String keyName) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        Pattern p1 = Pattern.compile("\\{.*?\\}");
        Matcher m1 = p1.matcher(text);
        while (m1.find()) {
            String afild = m1.group().replace("{", "").replace("}", "");
            for (String key : columnFields.keySet()) {
                ColumnDefinitionNew columnDefinitionNew = columnFields.get(key);
                if (afild.equals(columnDefinitionNew.getTitleName())) {
                    String fieldName = getFieldName(columnDefinitionNew);
                    text = text.replace("${" + afild + "}", getReplaceData(keyName, fieldName));
                    break;
                }
            }
        }
        return text;
    }

    private String getFieldName(ColumnDefinitionNew columnDefinitionNew) {
        String fieldName = columnDefinitionNew.getColumnAliase();
        if (StringUtils.isBlank(fieldName)) {
            fieldName = columnDefinitionNew.getFieldName();
        }
        return fieldName;
    }

    private String getReplaceData(String keyName, String fieldName) {
        String fieldNameTurn = QueryItem.getKey(fieldName);
        return "${" + keyName + "[" + "'" + fieldNameTurn + "'" + "]!}";
    }

    private StringBuilder getCheckBoxHtml(String keyName) {
        StringBuilder sBuffer = new StringBuilder();
        if (viewDefinitionNewBean.getShowCheckBox() != null && viewDefinitionNewBean.getShowCheckBox()) {
            String checkKey = StringUtils.isNotBlank(viewDefinitionNewBean.getCheckKey()) ? viewDefinitionNewBean
                    .getCheckKey() : "uuid";
            sBuffer.append("<td width='15px'><input type='checkbox' class='checkeds' ");
            for (String key : columnFields.keySet()) {
                ColumnDefinitionNew columnDefinitionNew = columnFields.get(key);
                if (checkKey.equals(columnDefinitionNew.getColumnAliase())) {
                    sBuffer.append(" value='" + getReplaceData(keyName, columnDefinitionNew.getColumnAliase()) + "'");
                }
            }
            sBuffer.append("/></td>");
        }
        return sBuffer;
    }

    private StringBuilder getColumnHtml(String keyName, String rowPlace) {
        StringBuilder rel = new StringBuilder();
        for (String key : columnFields.keySet()) {
            ColumnDefinitionNew columnDefinitionNew = columnFields.get(key);
            if (columnDefinitionNew.getHidden() == false
                    && (rowPlace.equals(columnDefinitionNew.getShowLine()) || (StringUtils.isBlank(columnDefinitionNew
                    .getShowLine()) && rowPlace.equals("第一行")))) {
                if (columnDefinitionNew.getValueType().equals("1")) {
                    String fieldName = key;
                    if (rowPlace.equals("第二行")
                            || columnDefinitionNew.getValue().equals(columnDefinitionNew.getTitleName())) {
                        fieldName = getFieldName(columnDefinitionNew);
                    }
                    String field = QueryItem.getKey(key);
                    String content = getReplaceData(keyName, fieldName);
                    rel.append(initParams(COMLUMN_HTML_1, field, "", columnDefinitionNew.getWidth(), content));
                } else if (columnDefinitionNew.getValueType().equals("2")) {
                    //高级列值设置
                    String columnAliase = QueryItem.getKey(columnDefinitionNew.getColumnAliase());
                    String content = replaceColumnFieldParams(columnDefinitionNew.getValue(), keyName);
                    rel.append(initParams(COMLUMN_HTML_2, columnAliase, "", columnDefinitionNew.getWidth(), content));
                }
            }
        }
        return rel;
    }

    private StringBuilder getButtonHtml(String keyName, String btnPlace, String divClass, String btnClasss) {
        StringBuilder buttonTemplate = new StringBuilder();
        for (CustomButtonNew cbb : viewbutton) {
            if (isNotBlank(cbb.getCode()) && isGranted(cbb.getCode()) && StringUtils.isNotBlank(cbb.getPlace())
                    && cbb.getPlace().indexOf(btnPlace) > -1) {
                if (buttonTemplate.length() == 0) {
                    buttonTemplate.append("<div class='").append(divClass).append("'>");
                }
                buttonTemplate.append(initParams(BUTTON_TEMPLATE_STR, btnClasss, keyName, cbb));
            }
        }
        if (buttonTemplate.length() > 0) {
            buttonTemplate.append("</div>");
        }
        return buttonTemplate;
    }

    private boolean hasSecondColumn() {
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if ("第二行".equals(columnDefinitionNew.getShowLine())) {
                return true;
            }
        }
        return false;
    }

    private String getIsReadString(QueryItem queryItem) {
        String isread = "";
        if (viewDefinitionNewBean.getIsRead() != null && viewDefinitionNewBean.getIsRead()) {
            boolean isflag = queryItem.get("readFlag") == null ? false : (Boolean) queryItem.get("readFlag");
            if (isflag) {
                isread = "readed";
            } else {
                isread = "noread";
            }
        }
        return isread;
    }

    private String getCssValue(QueryItem item) throws Exception {
        String cssValue = "";
        Set<ColumnCssDefinitionNew> columnCssDefinitionNews = viewDefinitionNewBean.getColumnCssDefinitionNew();
        for (ColumnCssDefinitionNew columnCssDefinitionNew : columnCssDefinitionNews) {
            String viewColumn = columnCssDefinitionNew.getViewColumn();//列
            Iterator<String> objkey = item.keySet().iterator();
            while (objkey.hasNext()) {// 遍历JSONObject
                String key = objkey.next();
                if (key.equalsIgnoreCase(viewColumn)
                        && cssStyleConditionVerify(columnCssDefinitionNew.getColumnCondition(),
                        columnCssDefinitionNew.getColumnConditionValue(), item.getString(key))) {
                    cssValue = getCssStyle(columnCssDefinitionNew.getFontWide(), columnCssDefinitionNew.getFontColor());
                }
            }
        }
        return cssValue;
    }

    private JSONObject queryItemToJson(QueryItem item) throws Exception {
        JSONObject jsonStr = new JSONObject();

        Iterator<String> it = item.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = item.get(key) == null ? "" : item.get(key);
            if (Clob.class.isAssignableFrom(value.getClass())) {
                String valueString = ClobUtils.ClobToString((Clob) value);
                jsonStr.put(key, valueString);
                item.put(key, valueString);
            } else {
                jsonStr.put(key, value);
            }
        }
        return jsonStr;
    }

    private String initParams(String text, CustomButtonNew cbb) {
        text = setParamsValue(text, CBB_CODE, cbb.getCode());
        text = setParamsValue(text, CBB_NAME, cbb.getName());
        text = setParamsValue(text, JS_FUNCTION, setParamsValue(cbb.getJsContent(), "ctx", request.getContextPath()));
        return text;
    }

    private String initParams(String text, FieldSelect fs) throws UnsupportedEncodingException {
        text = setParamsValue(text, TITLE_NAME, fs.getField());
        text = setParamsValue(text, FIELD_NAME, fs.getField());
        text = setParamsValue(text, SELECT_TYPE_ID, fs.getSelectTypeId());
        text = setParamsValue(text, DATA_SOURCE_ID, fs.getDataSourceId());
        text = setParamsValue(text, SELECT_NAME_COLUMN, fs.getSelectNameColumn());
        text = setParamsValue(text, SELECT_VALUE_COLUMN, fs.getSelectValueColumn());
        text = setParamsValue(text, OPTION_DATA_SOURCE, fs.getOptionDataSource());
        text = setParamsValue(text, EXACT_VALUE, fs.getExactValue());
        text = setParamsValue(text, CONTENT_FORMAT, fs.getContentFormat());
        text = setParamsValue(text, ORG_INPUT_MODE, fs.getOrgInputMode());
        String opData = StringUtils.isNotBlank(fs.getOptdata()) ? URLEncoder.encode(fs.getOptdata(), "utf-8") : "";
        text = setParamsValue(text, OPT_DATA, opData);
        fs.setOptdata(opData);
        return text;
    }

    private String initParams(String text, String field, String title, String width, String content) {
        text = setParamsValue(text, FIELD, field);
        text = setParamsValue(text, TITLE, title);
        text = setParamsValue(text, WIDTH, width);
        text = setParamsValue(text, CONTENT, content);
        return text;
    }

    private boolean isNotBlank(String text) {
        return StringUtils.isNotBlank(text) && !text.equals("null");
    }

    private boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    private String initParams(String text, String palce, String keyName, CustomButtonNew cbb) {
        text = setParamsValue(text, PLACE, palce);
        text = setParamsValue(text, CBB_CODE, cbb.getCode());
        text = setParamsValue(text, CBB_NAME, cbb.getName());
        String jsFunction = setParamsValue(cbb.getJsContent(), "ctx", request.getContextPath());
        text = setParamsValue(text, JS_FUNCTION, replaceColumnFieldParams(jsFunction, keyName));
        return text;
    }

    private String setParamsValue(String text, String paramName, String paramValue) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return StringUtils.replace(text, "${" + paramName + "}", paramValue);
    }

    private String getHtmlKey(FieldSelect fs) {
        String htmlKey = fs.getSelectTypeId();
        if (fs.getSelectTypeId().equals("TEXT")) {
            if (fs.getIsArea() == true) {
                htmlKey = htmlKey + "_AREA";
            } else if (fs.getIsExact() == true) {
                htmlKey = htmlKey + "_EXACT";
            } else if (fs.getIsLike()) {
                htmlKey = htmlKey + "_LIKE";
            }
        }
        return htmlKey;
    }

    private int widthType() {
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if (columnDefinitionNew.getHidden() != true && columnDefinitionNew.getWidth() != null
                    && columnDefinitionNew.getWidth().indexOf("%") > -1) {
                return 2;
            }
        }
        return 1;
    }

    private int getNeedShowTitleNum() {
        int j = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            if (columnDefinitionNew.getHidden() == false && !"第二行".equals(columnDefinitionNew.getShowLine())) {
                j++;
            }
        }
        return j;
    }

    private String getTitleCssStyle(Boolean showAble, Boolean isLast) {
        StringBuilder cssStyle = new StringBuilder();
        if (isTrue(showAble) || isTrue(isLast)) {
            cssStyle.append(" class = '");
            if (isTrue(showAble)) {
                cssStyle.append(" sortAble");
            }
            if (isTrue(isLast)) {
                cssStyle.append(" last");
            }
            cssStyle.append(" '");
        }
        return cssStyle.toString();
    }

    /**
     * 组合按关键字查询模板(占时放空，预留)
     *
     * @return html代码
     */
    public StringBuilder assemblyKeySelectHtml() {
        return new StringBuilder();
    }

    /**
     * 组合按条件查出模板(代码未正确实现)
     *
     * @return html代码
     */
    public StringBuilder assemblyConditionSelectHtml() {
        return new StringBuilder();
        //	StringBuilder condSelect = new StringBuilder();
        //	int datascope = 0;
        //	Set<ConditionTypeNew> conditionTypeNews = selectDefinitionNew.getConditionTypeNew();
        //	int flag = 0;
        //	//按条件查询的前台页面展示
        //	for (ConditionTypeNew conditionTypeNew : conditionTypeNews) {
        //		String ShowName = conditionTypeNew.getName(); //备选项的显示值
        //		String ShowValue = conditionTypeNew.getConditionValue();//备选项的真实值
        //		String ConditionName = conditionTypeNew.getConditionName();//条件名称
        //		String appointColumn = conditionTypeNew.getAppointColumn();//条件对应的列
        //		String appointColumnType = conditionTypeNew.getAppointColumnType();
        //		if (datascope == DyviewConfigNew.DYVIEW_DATASCOPE_DYTABLE) {
        //			if ("1".equals(appointColumnType)) {
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left'>" + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				String[] ShowNames = ShowName.split(";");
        //				String[] ShowValues = ShowValue.split(";");
        //				for (int i = 0; i < ShowNames.length; i++) {
        //					condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i + "_"
        //							+ conditionTypeNew.getUuid() + "_cond" + "' value='" + ShowValues[i]
        //							+ "' appointColumn='" + appointColumn + "' appointColumnType = '" + appointColumnType
        //							+ "'><a>" + ShowNames[i] + "</a></div></td>");
        //				}
        //
        //				condSelect.append("</tr>");
        //			} else if ("2".equals(appointColumnType) || "DATE".equals(appointColumnType)) {
        //				DateUtil dateUtil = new DateUtil();
        //				String today = dateUtil.getPreDate(0);
        //				String yesterday = dateUtil.getPreDate(-1);
        //				//获取上周星期一的日期
        //				String lastWeekFirstDay = dateUtil.getPreviousMonday();
        //				//获取上周星期日的日期
        //				String lastWeekSunday = dateUtil.getSunday();
        //				//获取上个月的第一天
        //				String lastMonthFirstDay = dateUtil.getLastMonthFirstDay();
        //				//获取上个月的最后一天
        //				String lastMonthLastDay = dateUtil.getLastMonthLastDay();
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left' " + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				condSelect.append("<td class='view_search_right'><div id='allDay'><a>不限</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='today' today='" + today
        //						+ "' appointColumn='" + appointColumn + "' ><a>今天</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='yesterday' yesterday='" + yesterday
        //						+ "' appointColumn='" + appointColumn + "'><a>昨天</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='lastWeek' lastWeekFirstDay='"
        //						+ lastWeekFirstDay + "' lastWeekSunday='" + lastWeekSunday + "' appointColumn='"
        //						+ appointColumn + "'><a>上星期</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='lastMonth' lastMonthFirstDay='"
        //						+ lastMonthFirstDay + "' lastMonthLastDay='" + lastMonthLastDay + "' appointColumn='"
        //						+ appointColumn + "'><a>上个月</a></div></td>");
        //				condSelect
        //						.append("<td  class='view_search_right'><div id='chooseDate'><a>选择日期</a></div><div id='dateInput' style='display:none'><input type='text' id='datepicker' appointColumn='"
        //								+ appointColumn + "'></div></td></tr>");
        //			} else if ("3".equals(appointColumnType)) {
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left'>" + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				condSelect.append("<td class='view_search_right'><div><a>不限</a></div></td>");
        //				condSelect
        //						.append("<td class='view_search_right'><div><input type='text' id='' name=''/></div></td>");
        //				condSelect.append("<td class='view_search_right'><div>'--'</div></td>");
        //				condSelect
        //						.append("<td class='view_search_right'><div><input type='text' id='' name='' appointColumn='"
        //								+ appointColumn + "'/></div></td></tr>");
        //			}
        //		} else if (datascope == DyviewConfigNew.DYVIEW_DATASCOPE_ENTITY
        //				|| datascope == DyviewConfigNew.DYVIEW_DATASCOPE_MOUDLE) {
        //			if ("STRING".equals(appointColumnType)) {
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left'>" + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				String[] ShowNames = ShowName.split(";");
        //				String[] ShowValues = ShowValue.split(";");
        //				for (int i = 0; i < ShowNames.length; i++) {
        //					condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i + "_"
        //							+ conditionTypeNew.getUuid() + "_cond" + "' value='" + ShowValues[i]
        //							+ "' appointColumn='" + appointColumn + "' appointColumnType = '" + appointColumnType
        //							+ "'><a>" + ShowNames[i] + "</a></div></td>");
        //				}
        //
        //				condSelect.append("</tr>");
        //			} else if ("DATE".equals(appointColumnType)) {
        //				DateUtil dateUtil = new DateUtil();
        //				String today = dateUtil.getPreDate(0);
        //				String yesterday = dateUtil.getPreDate(-1);
        //				//获取上周星期一的日期
        //				String lastWeekFirstDay = dateUtil.getPreviousMonday();
        //				//获取上周星期日的日期
        //				String lastWeekSunday = dateUtil.getSunday();
        //				//获取上个月的第一天
        //				String lastMonthFirstDay = dateUtil.getLastMonthFirstDay();
        //				//获取上个月的最后一天
        //				String lastMonthLastDay = dateUtil.getLastMonthLastDay();
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left'>" + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				condSelect.append("<td class='view_search_right'><div id='allDay'><a>不限</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='today' today='" + today
        //						+ "' appointColumn='" + appointColumn + "' ><a>今天</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='yesterday' yesterday='" + yesterday
        //						+ "' appointColumn='" + appointColumn + "'><a>昨天</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='lastWeek' lastWeekFirstDay='"
        //						+ lastWeekFirstDay + "' lastWeekSunday='" + lastWeekSunday + "' appointColumn='"
        //						+ appointColumn + "'><a>上星期</a></div></td>");
        //				condSelect.append("<td class='view_search_right'><div id='lastMonth' lastMonthFirstDay='"
        //						+ lastMonthFirstDay + "' lastMonthLastDay='" + lastMonthLastDay + "' appointColumn='"
        //						+ appointColumn + "'><a>上个月</a></div></td>");
        //				condSelect
        //						.append("<td class='view_search_right'><div id='chooseDate'><a>选择日期</a></div><div id='dateInput' style='display:none'><input type='text' id='datepicker' appointColumn='"
        //								+ appointColumn + "'></div></td></tr>");
        //			} else {
        //				if (flag % 2 == 1) {
        //					condSelect.append("<tr class='view_seach_tr_odd'>");
        //				} else {
        //					condSelect.append("<tr class='view_seach_tr_even'>");
        //				}
        //				condSelect.append("<td class='view_search_left'>" + ConditionName
        //						+ "&nbsp;&nbsp;&nbsp;&nbsp;|</td>");
        //				String[] ShowNames = ShowName.split(";");
        //				String[] ShowValues = ShowValue.split(";");
        //				for (int i = 0; i < ShowNames.length; i++) {
        //					condSelect.append("<td class='view_search_right'><div class='cond_class' id='" + i + "_"
        //							+ conditionTypeNew.getUuid() + "_cond" + "' value='" + ShowValues[i]
        //							+ "' appointColumn='" + appointColumn + "' appointColumnType = '" + appointColumnType
        //							+ "'><a>" + ShowNames[i] + "</a></div></td>");
        //				}
        //			}
        //		}
        //		flag++;
        //	}
        //	condSelect.append("</tr>");
    }

    private boolean isGranted(String codes) {
        if (StringUtils.isBlank(codes)) {
            return false;
        }

        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(SecurityAuditFacadeService.class);
        String[] codestmp = codes.split(Separator.SEMICOLON.getValue());
        for (String code : codestmp) {
            if (StringUtils.isBlank(code)) {
                continue;
            } else {
                if (securityAuditFacadeService.isGranted(code)) {
                    return true;
                }
            }
        }

        return false;
    }
}
