/*
 * @(#)2016年5月11日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.ui.*;
import com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage;
import com.wellsoft.pt.app.ui.client.container.WebAppMobilePageDefinitionProxyPage;
import com.wellsoft.pt.app.ui.client.widget.JsonBuildWidget;
import com.wellsoft.pt.app.ui.client.widget.JsonWidget;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionElement;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月11日.1	zhulh		2016年5月11日		Create
 * </pre>
 * @date 2016年5月11日
 */
public class WidgetDefinitionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetDefinitionUtils.class);

    private static final String KEY_UUID = "uuid";
    private static final String KEY_ID = "id";
    private static final String KEY_WTYPE = "wtype";
    private static final String KEY_TITLE = "title";
    private static final String KEY_HTML = "html";
    private static final String KEY_ITEMS = "items";
    private static final Map<String, Class> widgetTypeViewClass;

    static {
        widgetTypeViewClass = Maps.newHashMap();
        widgetTypeViewClass.put("wPage", WebAppPageDefinitionProxyPage.class);
        widgetTypeViewClass.put("wMobilePage", WebAppMobilePageDefinitionProxyPage.class);
        // 未注册组件类，通过com.wellsoft.pt.app.ui.client.widget.JsonBuildWidget解析组件的json
    }

    /**
     * @param json
     * @return
     */
    public static final Set<String> extractWtypes(JSONObject json) {
        Set<String> wtypes = new HashSet<String>();
        try {
            extractWtype(json, wtypes);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return wtypes;
    }

    /**
     * @param jsonObject
     * @param wtypes
     */
    private static void extractWtype(JSONObject jsonObject, Set<String> wtypes) throws JSONException {
        if (!jsonObject.has(KEY_WTYPE)) {
            return;
        }
        String wtype = jsonObject.getString(KEY_WTYPE);
        wtypes.add(wtype);

        if (!jsonObject.has(KEY_ITEMS)) {
            return;
        }

        JSONArray array = jsonObject.getJSONArray(KEY_ITEMS);
        if (array == null) {
            return;
        }

        for (int index = 0; index < array.length(); index++) {
            extractWtype(array.getJSONObject(index), wtypes);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param definitionJson
     * @return
     */
    private static JSONObject getJSONObject(String definitionJson) throws JSONException {
        JSONObject json = new JSONObject(definitionJson);
        return json;
    }

    /**
     * @param definitionJson
     * @param string
     * @return
     */
    public static String getString(String definitionJson, String key) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        if (json.has(key)) {
            return json.getString(key);
        }
        return null;
    }

    /**
     * @param definitionJson
     * @return
     */
    public static String getUuid(String definitionJson) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        if (json.has(KEY_UUID)) {
            return json.getString(KEY_UUID);
        }
        return null;
    }

    /**
     * @param definitionJson
     * @return
     */
    public static String getId(String definitionJson) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        if (json.has(KEY_ID)) {
            return json.getString(KEY_ID);
        }
        return null;
    }

    /**
     * @param definitionJson
     * @return
     */
    public static String getTitle(String definitionJson) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        if (json.has(KEY_TITLE)) {
            return json.getString(KEY_TITLE);
        }
        return null;
    }

    /**
     * @param definitionJson
     * @return
     */
    public static String getHtml(String definitionJson) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        if (json.has(KEY_HTML)) {
            return json.getString(KEY_HTML);
        }
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param definitionJson
     * @param string
     */
    public static String putString(String definitionJson, String key, String value) throws JSONException {
        JSONObject json = getJSONObject(definitionJson);
        json.put(key, value);
        return json.toString();
    }

    /**
     * @param definitionJson
     * @return
     */
    public static Widget parseWidget(String definitionJson) {
        try {
            JSONObject jsonObject = new JSONObject(definitionJson);
            String wtype = jsonObject.getString(KEY_WTYPE);
            Class viewClass = widgetTypeViewClass.containsKey(wtype) ? widgetTypeViewClass.get(wtype) : JsonBuildWidget.class;
            return WidgetInstantiatorUtils.instantiate(viewClass, definitionJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param definitionJson
     * @return
     */
    public static Widget parseWidget(String definitionJson, Class<?> itemViewCls) {
        try {
            return WidgetInstantiatorUtils.instantiate(itemViewCls, definitionJson, itemViewCls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param pageWidget
     * @return
     */
    public static List<Widget> extractWidgets(Widget pageWidget) {
        List<Widget> list = new ArrayList<Widget>();
        List<Widget> widgets = pageWidget.getItems();
        for (Widget widget : widgets) {
            list.addAll(extractWidgets(widget));
        }
//        list.add(pageWidget);
        return list;
    }

    /**
     * 提取组件对应的JS模块
     *
     * @param pageWidget
     * @return
     */
    public static final Set<String> extractJavaScriptModulesOfWtypes(Widget pageWidget) {
        Set<String> javaScriptModules = new HashSet<String>();
        List<Widget> widgets = extractWidgets(pageWidget);
        for (Widget widget : widgets) {
            javaScriptModules.add(getJavaScriptModuleByWtype(widget.getWtype()));
        }
        return javaScriptModules;
    }

    /**
     * 根据组件类型，获取组件对应的JS模块
     *
     * @param wtype
     * @return
     */
    public static final String getJavaScriptModuleByWtype(String wtype) {
        String javaScriptModule = wtype;
        if (wtype.indexOf(Separator.UNDERLINE.getValue()) != -1) {
            javaScriptModule = StringUtils.split(wtype, Separator.UNDERLINE.getValue())[0];
        }
        return javaScriptModule;
    }

    public static final <C extends WidgetConfiguration> C getWidgetConfigurationById(Class<C> configurationClass,
                                                                                     String widgetId) {
        AppWidgetDefinition appWidgetDefinition = AppCacheUtils.getAppWidgetDefinitionById(widgetId);
        return getWidgetConfigurationByDefinitionJson(configurationClass, appWidgetDefinition.getDefinitionJson());
    }

    public static final <C extends WidgetConfiguration> C getWidgetConfigurationByDefinitionJson(
            Class<C> configurationClass, String definitionJson) {
        try {
            JSONObject jsonObject = new JSONObject(definitionJson);
            String configuration = jsonObject.getJSONObject("configuration").toString();
            C result = JsonUtils.json2Object(configuration, configurationClass);
            if (result instanceof AbstractWidgetConfiguration) {
                ((AbstractWidgetConfiguration) result).setType(jsonObject.optString("wtype"));
            }
            return result;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param widget
     * @return
     */
    public static ModifiableWidgetDefinitionView copyWidget(Widget widget) {
        try {
            return copyWidget(widget.getDefinitionJson());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * @param widget
     * @return
     */
    public static ModifiableWidgetDefinitionView copyWidget(String definitionJson) {
        return copyWidget(definitionJson, StringUtils.EMPTY);
    }

    /**
     * @param widget
     * @return
     */
    public static ModifiableWidgetDefinitionView copyWidget(String definitionJson, String html) {
        return copyWidget(definitionJson, html, StringUtils.EMPTY);
    }

    public static ModifiableWidgetDefinitionView copyWidget(String definitionJson, String html, boolean parseJSON) {
        return copyWidget(definitionJson, html, StringUtils.EMPTY, parseJSON);
    }

    public static ModifiableWidgetDefinitionView copyWidget(String definitionJson, String html, String rootTargetId, boolean parseJSON) {
        String newDefinitionJson = AppConstants.JSON_EMPTY;
        try {
            // 新旧组件ID列表
            List<String> oldWidgetIds = Lists.newArrayList();
            List<String> newWidgetIds = Lists.newArrayList();
            List<String> srcHexWidgetIds = Lists.newArrayList();
            ModifiableWidgetDefinitionView proxyView = parseJSON ? (ModifiableWidgetDefinitionView) WidgetDefinitionUtils
                    .fromJSON(definitionJson, ModifiableWidgetDefinitionView.class) : (ModifiableWidgetDefinitionView) WidgetDefinitionUtils
                    .parseWidget(definitionJson, ModifiableWidgetDefinitionView.class);
            List<Widget> widgets = extractWidgets(proxyView);
            Document sourceDoc = Jsoup.parse(html);
            for (Widget widget : widgets) {
                // 容器组件引用的子组件不进行复制
                if (isReferenceWidgetItem(widgets, widget)) {
                    continue;
                }
                String widgetId = widget.getId();
                String newWidgetId = createWidgetId(widget);
                String srcHexId = DigestUtils.md5Hex(widgetId);
                // 目标组件不为空，使用目标组件的ID
                if (proxyView.equals(widget) && StringUtils.isNotBlank(rootTargetId)) {
                    newWidgetId = rootTargetId;
                }
                // 删除JSON的UUID
                ((ModifiableWidgetDefinitionView) widget).removeAttribute(AppConstants.KEY_UUID);
                // 更新JSON的ID
                ((ModifiableWidgetDefinitionView) widget).setAttribute(AppConstants.KEY_ID, newWidgetId);
                ((ModifiableWidgetDefinitionView) widget).setAttribute("srcId", srcHexId);

                // 更新HTML的ID属性
                Element sourceElement = sourceDoc.getElementById(widgetId);
                if (sourceElement != null) {
                    sourceElement.attr(AppConstants.KEY_ID, newWidgetId);
                }
                // 新旧组件ID列表
                oldWidgetIds.add(widgetId);
                newWidgetIds.add(newWidgetId);
                srcHexWidgetIds.add(srcHexId);
            }
            String targetHtml = sourceDoc.toString();
            proxyView.setAttribute(AppConstants.KEY_HTML, targetHtml);
            // 更新ID内容值
            newDefinitionJson = StringUtils.replaceEach(proxyView.getDefinitionJson(),
                    oldWidgetIds.toArray(new String[0]), newWidgetIds.toArray(new String[0]));
            // 设置源ID
            newDefinitionJson = StringUtils.replaceEach(newDefinitionJson, srcHexWidgetIds.toArray(new String[0]),
                    oldWidgetIds.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (ModifiableWidgetDefinitionView) parseWidget(newDefinitionJson, ModifiableWidgetDefinitionView.class);
    }

    /**
     * @param widget
     * @return
     */
    public static ModifiableWidgetDefinitionView copyWidget(String definitionJson, String html, String rootTargetId) {
        String newDefinitionJson = AppConstants.JSON_EMPTY;
        try {
            // 新旧组件ID列表
            List<String> oldWidgetIds = Lists.newArrayList();
            List<String> newWidgetIds = Lists.newArrayList();
            List<String> srcHexWidgetIds = Lists.newArrayList();
            ModifiableWidgetDefinitionView proxyView = (ModifiableWidgetDefinitionView) WidgetDefinitionUtils
                    .parseWidget(definitionJson, ModifiableWidgetDefinitionView.class);
            List<Widget> widgets = extractWidgets(proxyView);
            Document sourceDoc = Jsoup.parse(html);
            for (Widget widget : widgets) {
                // 容器组件引用的子组件不进行复制
                if (isReferenceWidgetItem(widgets, widget)) {
                    continue;
                }
                String widgetId = widget.getId();
                String newWidgetId = createWidgetId(widget);
                String srcHexId = DigestUtils.md5Hex(widgetId);
                // 目标组件不为空，使用目标组件的ID
                if (proxyView.equals(widget) && StringUtils.isNotBlank(rootTargetId)) {
                    newWidgetId = rootTargetId;
                }
                // 删除JSON的UUID
                ((ModifiableWidgetDefinitionView) widget).removeAttribute(AppConstants.KEY_UUID);
                // 更新JSON的ID
                ((ModifiableWidgetDefinitionView) widget).setAttribute(AppConstants.KEY_ID, newWidgetId);
                ((ModifiableWidgetDefinitionView) widget).setAttribute("srcId", srcHexId);

                // 更新HTML的ID属性
                Element sourceElement = sourceDoc.getElementById(widgetId);
                if (sourceElement != null) {
                    sourceElement.attr(AppConstants.KEY_ID, newWidgetId);
                }
                // 新旧组件ID列表
                oldWidgetIds.add(widgetId);
                newWidgetIds.add(newWidgetId);
                srcHexWidgetIds.add(srcHexId);
            }
            String targetHtml = sourceDoc.toString();
            proxyView.setAttribute(AppConstants.KEY_HTML, targetHtml);
            // 更新ID内容值
            newDefinitionJson = StringUtils.replaceEach(proxyView.getDefinitionJson(),
                    oldWidgetIds.toArray(new String[0]), newWidgetIds.toArray(new String[0]));
            // 设置源ID
            newDefinitionJson = StringUtils.replaceEach(newDefinitionJson, srcHexWidgetIds.toArray(new String[0]),
                    oldWidgetIds.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (ModifiableWidgetDefinitionView) parseWidget(newDefinitionJson, ModifiableWidgetDefinitionView.class);
    }

    /**
     * @param widgets
     * @param widget
     * @return
     */
    public static boolean isReferenceWidgetItem(List<Widget> widgets, Widget item) {
        for (Widget widget : widgets) {
            if (widget.isReferenceWidget() && !widget.equals(item)) {
                List<Widget> itemWidgets = WidgetDefinitionUtils.extractWidgets(widget);
                if (itemWidgets.contains(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param wtype
     * @return
     */
    public static String createWidgetId(Widget widget) {
        return widget.getWtype() + "_" + StringUtils.replace(UUID.randomUUID().toString(), "-", "").toUpperCase();
    }

    public static View resolveWidgetViewJSON(AppContext appContext, HttpServletRequest request) {
        UserAppData userAppData = appContext.getCurrentUserAppData();
        PiSystem piSystem = userAppData.getSystem();
        PiModule piModule = userAppData.getModule();
        PiApplication piApplication = userAppData.getApplication();
        String sysId = piSystem.getId();
        AppSystem appSystem = AppCacheUtils.getAppSystemById(sysId);
        AppPageDefinition appPageDefinition = AppCacheUtils.getUserAppPageDefinition(
                SpringSecurityUtils.getCurrentUserId(), userAppData.getAppPath(), userAppData.getPageUuid());
        if (appPageDefinition instanceof UnauthorizePageDefinition) {
            throw new AccessDeniedException("无权限访问页面");
        } else if (appPageDefinition instanceof NoPageDefinition) {
            throw new NotFoundException("尚未配置或指定默认页面，请先配置页面");
        }
        String pageUuid = appPageDefinition.getUuid();
        String id = appPageDefinition.getId();

        String title = piApplication != null ? piApplication.getTitle() : (piModule != null ? piModule.getTitle()
                : piSystem.getTitle());
        // 门户页面名称
        if (StringUtils.equals(SpringSecurityUtils.getCurrentUserId(), appPageDefinition.getUserId())) {
            title = appPageDefinition.getName();
        }

        WebUtils.setSessionAttribute(request, "portalPageUuid", pageUuid);
        String jsModule = appSystem.getJsModule();
        String definitionJson = appPageDefinition.getDefinitionJson();
        String wtype = appPageDefinition.getWtype();
        Class<?> viewClass = widgetTypeViewClass.get(wtype);
        Class<?>[] parameterTypes = new Class[]{String.class, String.class, String.class, boolean.class, String.class};
        try {
            Constructor<View> constructor = (Constructor<View>) viewClass.getConstructor(parameterTypes);
            View pageView = constructor.newInstance(id, title, null, true, definitionJson);
            return pageView;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Widget fromJSON(String definitionJson) {
        try {
            JSONObject jsonObject = new JSONObject(definitionJson);
            String wtype = jsonObject.getString(KEY_WTYPE);
            Class viewClass = widgetTypeViewClass.containsKey(wtype) ? widgetTypeViewClass.get(wtype) : JsonBuildWidget.class;
            return WidgetInstantiatorUtils.instantiate(viewClass, definitionJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Widget fromJSON(String definitionJson, Class<?> itemViewCls) {
        try {
            return WidgetInstantiatorUtils.instantiate(itemViewCls, definitionJson, true, itemViewCls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Widget> parseWidgets(List<AppWidgetDefinitionElement> appWidgetDefinitionElements) {
        List<Widget> widgets = Lists.newArrayList();
        try {
            if (appWidgetDefinitionElements != null) {
                for (AppWidgetDefinitionElement element : appWidgetDefinitionElements) {
                    JsonWidget jsonWidget = new JsonWidget(element.getDefinitionJson());
                    widgets.add(jsonWidget);
                }
            }
        } catch (Exception e) {
            //TODO:
        }
        return widgets;

    }
}
