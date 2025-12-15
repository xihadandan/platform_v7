/*
 * @(#)2016-11-10 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.app.support.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 可更改的组件定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-11-10.1	zhulh		2016-11-10		Create
 * </pre>
 * @date 2016-11-10
 */
public class UniAppWidgetDefinitionView extends AbstractWidget {

    // 组件JSON对象
    private JSONObject jsonObject;
    // 包含的组件
    private List<Widget> items;
    // 解析item的类型
    private Class<?> itemViewClass;

    /**
     * @param widgetDefinition
     * @throws JSONException
     */
    public UniAppWidgetDefinitionView(String widgetDefinition, Class<?> itemViewClass) throws Exception {
        super(widgetDefinition);
        this.jsonObject = new JSONObject(widgetDefinition);
        this.itemViewClass = itemViewClass;
        parseItems(false);
    }

    public UniAppWidgetDefinitionView(String widgetDefinition, boolean parseJSON, Class<?> itemViewClass)
            throws Exception {
        super(widgetDefinition);
        this.jsonObject = new JSONObject(widgetDefinition);
        this.itemViewClass = itemViewClass;

        parseItems(parseJSON);
    }

    /**
     * @param parseJSON
     * @throws JSONException
     */
    private void parseItems(boolean parseJSON) throws JSONException {
        List<Widget> tempWidgets = new ArrayList<Widget>();
        if (!jsonObject.has(AppConstants.KEY_ITEMS)) {
            items = Collections.unmodifiableList(new ArrayList<Widget>());
            return;
        }

        JSONArray array = jsonObject.getJSONArray(AppConstants.KEY_ITEMS);
        if (array == null) {
            items = Collections.unmodifiableList(new ArrayList<Widget>());
            return;
        }

        for (int index = 0; index < array.length(); index++) {
            tempWidgets.add(parseJSON
                    ? WidgetDefinitionUtils.fromJSON(array.getJSONObject(index).toString(),
                    UniAppWidgetDefinitionView.class)
                    : WidgetDefinitionUtils.parseWidget(array.getJSONObject(index).toString(), itemViewClass));
        }
        items = Collections.unmodifiableList(tempWidgets);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        if (items.isEmpty()) {
            return convertToUniAppDefinitionJson();
        }

        try {
            List<JSONObject> itemJsons = new ArrayList<JSONObject>();
            for (Widget widget : items) {
                itemJsons.add(new JSONObject(widget.getDefinitionJson()));
            }

            jsonObject.remove(AppConstants.KEY_ITEMS);
            jsonObject.put(AppConstants.KEY_ITEMS, itemJsons);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return convertToUniAppDefinitionJson();
    }

    /**
     * @return
     */
    private String convertToUniAppDefinitionJson() {
        String wtype = getWtype();
        switch (wtype) {
            case "wMobileTabBar":
                convertWMobileTabBarDefinitionJson();
                break;
            case "wMobilePanel":
                convertWMobilePanelDefinitionJson();
                break;
            case "wMobileSlider":
                convertWMobileSliderDefinitionJson();
                break;
            case "wMobileGridView":
                convertWMobileGridDefinitionJson();
                break;
            case "wMobileTiles":
                convertWMobileTilesDefinitionJson();
                break;
            case "wMobileNav":
                convertWMobileNavDefinitionJson();
                break;
            case "wMobileListView":
                convertWMobileListViewDefinitionJson();
                break;
            case "wMobileTabs":
                convertWMobileTabsDefinitionJson();
                break;
            default:
                break;
        }
        return jsonObject.toString();
    }

    private void convertWMobileTabBarDefinitionJson() {
        // 九宫格类型由wMobileTiles改为WidgetUniTiles
        setAttribute("wtype", "WidgetUniTabBar");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("tabItems")) {
            return;
        }
        // 图标转换configuration.columns[i].iconClass
        JSONArray jsonArray = configuration.getJSONArray("tabItems");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tabItemJsonObject = jsonArray.getJSONObject(i);
            convertEventHander2PageUrl(tabItemJsonObject);
        }
    }

    private void convertEventHander2PageUrl(JSONObject itemJsonObject) {
        // 事件处理
        if (itemJsonObject.has("eventHandler")) {
            JSONObject eventHandler = itemJsonObject.getJSONObject("eventHandler");
            String dataPath = eventHandler.getString("path");
            // 发起工作
            if (StringUtils.equals(dataPath, "/pt-mobile/mobile-workflow/mobile-workflow-todo/mobile_new_work")) {
                itemJsonObject.put("pageUrl", "/uni_modules/w-app/pages/workflow/start_new_work");
            } else {
                // 非功能事件处理
                if (!AppType.FUNCTION.equals(eventHandler.getInt("type"))) {
                    String pageUrl = "/uni_modules/w-app/pages/app/app?appPiPath=" + dataPath;
                    itemJsonObject.put("pageUrl", pageUrl);
                } else {
                    // 功能事件处理
                    convertAppFunction2PageUrl(dataPath, itemJsonObject);
                }
            }
        }
    }

    /**
     * 功能事件处理
     *
     * @param dataPath
     * @param itemJsonObject
     */
    private void convertAppFunction2PageUrl(String dataPath, JSONObject itemJsonObject) {
        AppContextService appContextService = ApplicationContextHolder.getBean(AppContextService.class);
        PiFunction piFunction = appContextService.getFunctionByPath(dataPath);
        if (piFunction == null) {
            return;
        }
        // 页面URL
        if (AppFunctionType.URL.equals(piFunction.getType())) {
            JSONObject jsonObject = new JSONObject(piFunction.getDefinitionJson());
            String url = jsonObject.getString("url");
            String[] urlParts = StringUtils.split(url, "?");
            if (urlParts.length > 1) {
                dataPath = StringUtils.replace(urlParts[0], "/web/app", StringUtils.EMPTY);
                dataPath = StringUtils.replace(dataPath, ".html", StringUtils.EMPTY);
                String pageUrl = "/uni_modules/w-app/pages/app/app?appPiPath=" + dataPath + "&"
                        + urlParts[urlParts.length - 1];
                itemJsonObject.put("pageUrl", pageUrl);
            } else {
                String pageUrl = "/uni_modules/w-app/pages/app/app?appPiPath=" + dataPath;
                itemJsonObject.put("pageUrl", pageUrl);
            }
        } else if (StringUtils.equals(AppFunctionType.AppWidgetDefinition, piFunction.getType())) {
            // 组件定义
            String pageUrl = "/uni_modules/w-app/pages/app/app?widgetDefId=" + piFunction.getId();
            itemJsonObject.put("pageUrl", pageUrl);
        } else if (AppFunctionType.FlowDefinition.equals(piFunction.getType())) {
            // 流程定义
            JSONObject jsonObject = new JSONObject(piFunction.getDefinitionJson());
            String newWorkUrl = jsonObject.getString("newWorkUrl");
            String[] urlParts = StringUtils.split(newWorkUrl, "/");
            String flowDefId = urlParts[urlParts.length - 1];
            itemJsonObject.put("pageUrl", "/uni_modules/w-app/pages/workflow/work_view?flowDefId=" + flowDefId);
        } else {
            itemJsonObject.put("appFunction", new JSONObject(piFunction.getDefinitionJson()));
        }
    }

    /**
     *
     */
    private void convertWMobilePanelDefinitionJson() {
        // 面板类型由wMobilePanel改为WidgetUniPanel
        setAttribute("wtype", "WidgetUniPanel");
        if (!jsonObject.has("configuration")) {
            return;
        }
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("menuItems")) {
            return;
        }
        JSONArray jsonArray = configuration.getJSONArray("menuItems");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 事件处理
            convertEventHander2PageUrl(jsonObject);
            // 图标
            String iconClass = jsonObject.getString("iconClass");
            if (StringUtils.contains(iconClass, "xinjiangongzuo")) {
                iconClass = "plusempty";
            }
            jsonObject.put("iconType", iconClass);
        }
    }

    /**
     * 图片滑块
     */
    private void convertWMobileSliderDefinitionJson() {
        // 滑块类型由wMobileSlider改为WidgetUniSwiper
        setAttribute("wtype", "WidgetUniSwiper");
        if (!jsonObject.has("configuration")) {
            return;
        }
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        configuration.put("autoplay", true);
        configuration.put("interval", configuration.getString("time"));
    }

    /**
     * 九宫格
     */
    private void convertWMobileGridDefinitionJson() {
        // 九宫格类型由wMobileGridView改为WidgetUniGridView
        setAttribute("wtype", "WidgetUniGridView");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("columns")) {
            return;
        }
        // 图标转换configuration.columns[i].iconClass
        JSONArray jsonArray = configuration.getJSONArray("columns");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject columnJsonObject = jsonArray.getJSONObject(i);
            String iconClass = columnJsonObject.getString("iconClass");
            iconClass = StringUtils.replace(iconClass, "mui-icon-paperplane", "paperplane");
            iconClass = StringUtils.replace(iconClass, "mui-icon-compose", "compose");
            iconClass = StringUtils.replace(iconClass, "fa fa-sign-out", "cloud-upload");
            iconClass = StringUtils.replace(iconClass, "fa fa-sign-in", "cloud-download");
            iconClass = StringUtils.replace(iconClass, "fa fa-file-text-o", "compose");
            iconClass = StringUtils.replace(iconClass, "mui-icon-mic", "mic");
            iconClass = StringUtils.replace(iconClass, "icon-notice", "notification");
            iconClass = StringUtils.replace(iconClass, "mui-icon-email", "email");
            iconClass = StringUtils.replace(iconClass, "fa fa-calendar", "calendar");
            iconClass = StringUtils.replace(iconClass, "fa fa-book", "contact");
            iconClass = StringUtils.replace(iconClass, "fa fa-fighter-jet", "compose");
            iconClass = StringUtils.replace(iconClass, "mui-icon-chatboxes", "chatboxes");
            iconClass = StringUtils.replace(iconClass, "fa fa-folder-o", "folder");
            iconClass = StringUtils.replace(iconClass, "mui-icon-more", "more");
            iconClass = StringUtils.replace(iconClass, "mui-icon-gear", "gear");
            iconClass = StringUtils.replace(iconClass, "mui-icon-map", "map");
            iconClass = StringUtils.replace(iconClass, "mui-icon-help", "help");
            iconClass = StringUtils.replace(iconClass, "mui-icon-info", "info");
            columnJsonObject.put("iconClass", iconClass);
            columnJsonObject.put("iconType", iconClass);
            columnJsonObject.put("imageUrl", "");
            // 事件处理
            convertEventHander2PageUrl(columnJsonObject);
        }
    }

    private void convertWMobileTilesDefinitionJson() {
        // 九宫格类型由wMobileTiles改为WidgetUniTiles
        setAttribute("wtype", "WidgetUniTiles");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("tiles")) {
            return;
        }
        // 图标转换configuration.columns[i].iconClass
        JSONArray jsonArray = configuration.getJSONArray("tiles");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tileJsonObject = jsonArray.getJSONObject(i);
            tileJsonObject.put("imageUrl", "");
            // 事件处理
            convertEventHander2PageUrl(tileJsonObject);
        }
    }

    /**
     * 手机导航
     */
    private void convertWMobileNavDefinitionJson() {
        // 导航类型由wMobileNav改为WidgetUniNav
        setAttribute("wtype", "WidgetUniNav");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("nav")) {
            return;
        }
        JSONArray jsonArray = configuration.getJSONArray("nav");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject navJsonObject = jsonArray.getJSONObject(i);
            if (!navJsonObject.has("data")) {
                continue;
            }
            JSONObject dataJsonObject = navJsonObject.getJSONObject("data");
            String icon = dataJsonObject.getString("icon");
            String type = "list";
            // 待办
            if (StringUtils.contains(icon, "daibangongzuo") || StringUtils.contains(icon, "icon-waittodo")) {
                type = "list";
            }
            // 已办
            if (StringUtils.contains(icon, "yibangongzuo") || StringUtils.contains(icon, "icon-done")) {
                type = "checkbox";
            }
            // 办结
            if (StringUtils.contains(icon, "icon-over")) {
                type = "checkmarkempty";
            }
            // 已阅
            if (StringUtils.contains(icon, "readed")) {
                type = "mail-open";
            }
            // 未阅
            if (StringUtils.contains(icon, "waittoread")) {
                type = "email-filled";
            }
            // 草稿
            if (StringUtils.contains(icon, "caogaoxiang") || StringUtils.contains(icon, "icon-draft1")) {
                type = "email-filled";
            }
            // 抄送
            if (StringUtils.contains(icon, "icon-oa-chaosongwode") || StringUtils.contains(icon, "icon-biniess")
                    || StringUtils.contains(icon, "icon-copy")) {
                type = "paperplane";
            }
            // 关注
            if (StringUtils.contains(icon, "icon-oa-woguanzhude") || StringUtils.contains(icon, "icon-focus")) {
                type = "star-filled";
            }
            // 委托
            if (StringUtils.contains(icon, "icon-entrust")) {
                type = "cart";
            }
            // 督办
            if (StringUtils.contains(icon, "icon-oa-duban")) {
                type = "eye";
            }
            // 监控
            if (StringUtils.contains(icon, "icon-oa-jiankong") || StringUtils.contains(icon, "icon-monitor")) {
                type = "scan";
            }
            // 查询
            if (StringUtils.contains(icon, "icon-xmch-xiangmuchaxun") || StringUtils.contains(icon, "icon-search")) {
                type = "search";
            }
            // 收件箱
            if (StringUtils.contains(icon, "mui-icon-email")) {
                type = "email";
            }
            // 已发送
            if (StringUtils.contains(icon, "mui-icon-paperplane")) {
                type = "paperplane";
            }
            // 草稿箱
            if (StringUtils.contains(icon, "mui-icon-loop")) {
                type = "loop";
            }
            // 草稿箱
            if (StringUtils.contains(icon, "mui-icon-trash")) {
                type = "trash";
            }
            // 未阅消息
            if (StringUtils.contains(icon, "mui-icon-chat")) {
                type = "email-filled";
            }

            Map<String, String> iconInfo = Maps.newHashMap();
            // iconInfo.put("color", "#4cd964");
            iconInfo.put("size", "22");
            iconInfo.put("type", type);
            navJsonObject.put("extraIcon", iconInfo);

            // 是否隐藏
            navJsonObject.put("hidden", dataJsonObject.getBoolean("hidden"));

            // 事件处理
            String eventHanlderPath = dataJsonObject.getString("eventHanlderPath");
            String eventHanlderType = dataJsonObject.getString("eventHanlderType");
            // 非功能事件处理
            if (!AppType.FUNCTION.equals(Integer.valueOf(eventHanlderType))) {
                String pageUrl = "/uni_modules/w-app/pages/app/app?appPiPath=" + eventHanlderPath;
                navJsonObject.put("pageUrl", pageUrl);
            } else {
                // 功能事件处理
                convertAppFunction2PageUrl(eventHanlderPath, navJsonObject);
            }
        }
    }

    /**
     *
     */
    private void convertWMobileListViewDefinitionJson() {
        // 数据列表类型由wMobileListView改为WidgetUniListView
        setAttribute("wtype", "WidgetUniListView");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        configuration.put("selectable", false);
        configuration.put("multiSelect", false);
        if (configuration.has("jsModule") && (!configuration.has("detailPageUrl")
                || StringUtils.isBlank(configuration.getString("detailPageUrl")))) {
            String jsModule = configuration.getString("jsModule");
            if (StringUtils.equals(jsModule, "mui-WorkFlowTodoMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=TODO");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowDraftMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=DRAFT");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowDoneMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=DONE");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowOverMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=OVER");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowAttentionMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=ATTENTION");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowSuperviseMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=SUPERVISE");
            } else if (StringUtils.equals(jsModule, "mui-WorkFlowMonitorMobileListViewDevelopment")) {
                configuration.put("detailPageUrl", "/uni_modules/w-app/pages/workflow/work_view?aclRole=MONITOR");
            }
        }
        if (!configuration.has("template")) {
            return;
        }
        String templateHtml = configuration.getString("templateHtml");
        templateHtml = StringUtils.replace(templateHtml, "leftCenter", "note");
        templateHtml = StringUtils.replace(templateHtml, "rightTop", "rightText");
        configuration.put("templateHtml", templateHtml);

        JSONArray jsonArray = configuration.getJSONArray("templateProperties");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            if (StringUtils.equals("subtitle", name)) {
                name = "subtitle";
            } else if (StringUtils.equals("leftCenter", name) || StringUtils.equals("subtitle2", name)) {
                name = "note";
            } else if (StringUtils.equals("rightTop", name) || StringUtils.equals("content", name)) {
                name = "rightText";
            }
            jsonObject.put("name", name);
        }

        // 操作按钮
        JSONArray buttonJsonArray = configuration.getJSONArray("buttons");
        for (int i = 0; i < buttonJsonArray.length(); i++) {
            JSONObject buttonJsonObject = buttonJsonArray.getJSONObject(i);
            // 事件处理
            convertEventHander2PageUrl(buttonJsonObject);
        }
    }

    /**
     *
     */
    private void convertWMobileTabsDefinitionJson() {
        // 标签页类型由wMobileTabs改为WidgetUniTab
        setAttribute("wtype", "WidgetUniTab");
        JSONObject configuration = jsonObject.getJSONObject("configuration");
        if (!configuration.has("tabs")) {
            return;
        }
        JSONArray jsonArray = configuration.getJSONArray("tabs");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tabJsonObject = jsonArray.getJSONObject(i);
            JSONObject eventHandler = tabJsonObject.getJSONObject("eventHandler");
            String dataPath = eventHandler.getString("path");
            // 非功能事件处理
            if (!AppType.FUNCTION.equals(eventHandler.getInt("type"))) {
                // TODO
            } else {
                // 功能事件处理
                AppContextService appContextService = ApplicationContextHolder.getBean(AppContextService.class);
                PiFunction piFunction = appContextService.getFunctionByPath(dataPath);
                if (piFunction == null) {
                    continue;
                }
                if (StringUtils.equals(AppFunctionType.AppWidgetDefinition, piFunction.getType())) {
                    AppWidgetDefinition appWidgetDefinition = appContextService
                            .getAppWidgetDefinitionById(piFunction.getId(), true);
                    tabJsonObject.put("itemDefinitionJson", appWidgetDefinition.getDefinitionJson());
                }
            }
        }
    }

    /**
     * @param key
     * @param value
     * @throws JSONException
     */
    public void setAttribute(String key, String value) {
        this.jsonObject.put(key, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getItems()
     */
    @Override
    public List<Widget> getItems() {
        return this.items;
    }

}
