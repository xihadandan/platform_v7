/*
 * @(#)Jan 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.pt.app.context.support.OrderedProperties;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;

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
 * Jan 22, 2017.1	zhulh		Jan 22, 2017		Create
 * </pre>
 * @date Jan 22, 2017
 */
public class AppParserContext {

    private Map<String, String> values = new TreeMap<String, String>();

    private Map<String, String> themeValues = new LinkedHashMap<String, String>();
    private Map<String, String> cssValues = new LinkedHashMap<String, String>();
    private Map<String, String> jsModuleValues = new LinkedHashMap<String, String>();

    private Map<String, CssFile> cssFileMap = new HashMap<String, CssFile>();
    private Map<String, JavaScriptModule> javaScriptModuleMap = new HashMap<String, JavaScriptModule>();
    private Map<String, JavaScriptModule> abstractJavaScriptModuleMap = new HashMap<String, JavaScriptModule>();
    private Map<String, JavaScriptTemplate> javaScriptTemplateMap = new HashMap<String, JavaScriptTemplate>();
    private Map<String, Theme> themeMap = new HashMap<String, Theme>();

    private Map<String, List<String>> pendingJsModuleMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> pendingJsModuleForThemeMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> pendingCssForJsModuleMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> pendingCssForThemeMap = new HashMap<String, List<String>>();

    /**
     * @param appContextConfigurerParser
     */
    public AppParserContext() {
    }

    /**
     * @param orderedProperties
     */
    public void addAll(List<OrderedProperties> orderedProperties) {
        for (OrderedProperties config : orderedProperties) {
            Enumeration<?> keys = config.keys();
            while (keys.hasMoreElements()) {
                String key = StringUtils.trim(keys.nextElement().toString());
                if (values.containsKey(key)) {
                    throw new RuntimeException("CMS配置文件中，存在重复key【" + key + "】，请检查");
                }
                values.put(key, key);
            }
        }
    }

    /**
     * @return the themeValues
     */
    public Map<String, String> getThemeValues() {
        return themeValues;
    }

    /**
     * @return the cssValues
     */
    public Map<String, String> getCssValues() {
        return cssValues;
    }

    /**
     * @return the jsModuleValues
     */
    public Map<String, String> getJsModuleValues() {
        return jsModuleValues;
    }

    /**
     * @param cssFile
     */
    public void registerCssFile(CssFile cssFile) {
        String cssId = cssFile.getId();
        cssFileMap.put(cssId, cssFile);
        // 更新JS模块要加载的样式
        if (pendingCssForJsModuleMap.containsKey(cssId)) {
            List<String> jsModuleIds = pendingCssForJsModuleMap.get(cssId);
            for (String jsModuleId : jsModuleIds) {
                JavaScriptModule javaScriptModule = javaScriptModuleMap.get(jsModuleId);
                if (javaScriptModule != null && !javaScriptModule.getCssFiles().contains(cssFile)) {
                    javaScriptModule.getCssFiles().add(cssFile);
                }
            }
        }

        // 更新主题依赖的样式
        if (pendingCssForThemeMap.containsKey(cssId)) {
            List<String> themeIds = pendingCssForThemeMap.get(cssId);
            for (String themeId : themeIds) {
                Theme theme = themeMap.get(themeId);
                if (theme != null && !theme.getCssFiles().contains(cssFile)) {
                    theme.getCssFiles().add(cssFile);
                }
            }
        }
    }

    /**
     * @param javaScriptModule
     */
    public void registerJavaScriptModule(JavaScriptModule javaScriptModule) {
        String jsModuleId = javaScriptModule.getId();
        javaScriptModuleMap.put(jsModuleId, javaScriptModule);
        // 更新主题依赖的JS模块
        if (pendingJsModuleForThemeMap.containsKey(jsModuleId)) {
            List<String> themeIds = pendingJsModuleForThemeMap.get(jsModuleId);
            for (String themeId : themeIds) {
                Theme theme = themeMap.get(themeId);
                if (theme != null && !theme.getJavaScriptModules().contains(javaScriptModule)) {
                    theme.getJavaScriptModules().add(javaScriptModule);
                }
            }
        }
    }

    /**
     * @param javaScriptModule
     */
    public void registerAbstractJavaScriptModule(JavaScriptModule javaScriptModule) {
        String jsModuleId = javaScriptModule.getId();
        abstractJavaScriptModuleMap.put(javaScriptModule.getId(), javaScriptModule);
        // 更新JS模块依赖的JS模块，抽象类的继承依赖不支持
        if (pendingJsModuleMap.containsKey(jsModuleId)) {
            List<String> jsModuleIds = pendingJsModuleMap.get(jsModuleId);
            for (String id : jsModuleIds) {
                JavaScriptModule jsModule = javaScriptModuleMap.get(id);
                if (jsModule != null) {
                    Set<String> parentDependencies = javaScriptModule.getDependencies();
                    jsModule.getDependencies().addAll(parentDependencies);
                }
            }
        }
    }

    /**
     * @param javaScriptTemplate
     */
    public void registerJavaScriptTemplate(JavaScriptTemplate javaScriptTemplate) {
        String jsTemplateId = javaScriptTemplate.getId();
        javaScriptTemplateMap.put(jsTemplateId, javaScriptTemplate);
    }

    /**
     * @param theme
     */
    public void registerTheme(Theme theme) {
        themeMap.put(theme.getId(), theme);
    }

    /**
     * @return the cssFileMap
     */
    public Map<String, CssFile> getCssFileMap() {
        return cssFileMap;
    }

    /**
     * @return the javaScriptModuleMap
     */
    public Map<String, JavaScriptModule> getJavaScriptModuleMap() {
        return javaScriptModuleMap;
    }

    /**
     * @return the abstractJavaScriptModuleMap
     */
    public Map<String, JavaScriptModule> getAbstractJavaScriptModuleMap() {
        return abstractJavaScriptModuleMap;
    }

    /**
     * @return the javaScriptTemplateMap
     */
    public Map<String, JavaScriptTemplate> getJavaScriptTemplateMap() {
        return javaScriptTemplateMap;
    }

    /**
     * @return the themeMap
     */
    public Map<String, Theme> getThemeMap() {
        return themeMap;
    }

    /**
     * @param id
     * @return
     */
    public CssFile getCssFile(String id) {
        return this.cssFileMap.get(id);
    }

    /**
     * @param id
     * @return
     */
    public JavaScriptModule getJavaScriptModule(String id) {
        if (javaScriptModuleMap.containsKey(id)) {
            return javaScriptModuleMap.get(id);
        }
        return abstractJavaScriptModuleMap.get(id);
    }

    /**
     * @param jsModuleId
     */
    public void registerPendingJavaScriptModule(String dependencyJsModuleId, String jsModuleId) {
        if (!pendingJsModuleMap.containsKey(dependencyJsModuleId)) {
            pendingJsModuleMap.put(dependencyJsModuleId, new ArrayList<String>());
        }
        pendingJsModuleMap.get(dependencyJsModuleId).add(jsModuleId);
    }

    /**
     * @param trim
     * @param id
     */
    public void registerPendingJavaScriptModuleForTheme(String jsModuleId, String themeId) {
        if (!pendingJsModuleForThemeMap.containsKey(jsModuleId)) {
            pendingJsModuleForThemeMap.put(jsModuleId, new ArrayList<String>());
        }
        pendingJsModuleForThemeMap.get(jsModuleId).add(themeId);
    }

    /**
     * @param cssFileId
     * @param jsModuleId
     */
    public void registerPendingCssFileForJavaScriptModule(String cssFileId, String jsModuleId) {
        if (!pendingCssForJsModuleMap.containsKey(cssFileId)) {
            pendingCssForJsModuleMap.put(cssFileId, new ArrayList<String>());
        }
        pendingCssForJsModuleMap.get(cssFileId).add(jsModuleId);
    }

    /**
     * @param trim
     * @param id
     */
    public void registerPendingCssFileForTheme(String cssFileId, String themeId) {
        if (!pendingCssForThemeMap.containsKey(cssFileId)) {
            pendingCssForThemeMap.put(cssFileId, new ArrayList<String>());
        }
        pendingCssForThemeMap.get(cssFileId).add(themeId);
    }

}
