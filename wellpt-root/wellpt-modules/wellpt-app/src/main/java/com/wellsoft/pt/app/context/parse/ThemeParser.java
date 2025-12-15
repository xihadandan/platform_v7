/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.parse;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import com.wellsoft.pt.app.context.AppParserContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.theme.SimpleTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Description: 配置文件主题解析
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-24.1	zhulh		2017-01-24		Create
 * </pre>
 * @date 2017-01-24
 */
public class ThemeParser extends AbstractAppContextParser {
    private static final String PT_THEME_PREFIX = "pt.theme.";
    private static final String APP_THEME_PREFIX = "app.theme.";

    private static final String THEME_ID_PREFIX = "id";
    private static final String THEME_NAME_PREFIX = "name";
    private static final String THEME_ORDER_PREFIX = "order";
    private static final String THEME_CLASS_PREFIX = "class";
    private static final String THEME_CSSFILES_PREFIX = "cssFiles";
    private static final String THEME_JAVASCRIPTMODULES_PREFIX = "javaScriptModules";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextParser#parse(com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport, com.wellsoft.pt.app.context.AppParserContext)
     */
    @Override
    public void parse(AppContextPropertiesConfigurationSupport configurationSupport, AppParserContext parserContext) {
        Map<String, String> themeValues = extractValues(configurationSupport, PT_THEME_PREFIX, APP_THEME_PREFIX);
        Set<String> themesSet = new HashSet<String>();
        for (String key : themeValues.keySet()) {
            String[] keyArray = key.split("\\.");
            String theme = StringUtils.trim(keyArray[2]);
            String themePrefix = keyArray[0] + "." + keyArray[1] + ".";
            if (themesSet.contains(theme)) {
                continue;
            } else {
                themesSet.add(theme);
                String idKey = themePrefix + theme + "." + THEME_ID_PREFIX;
                String nameKey = themePrefix + theme + "." + THEME_NAME_PREFIX;
                String orderKey = themePrefix + theme + "." + THEME_ORDER_PREFIX;
                String classNameKey = themePrefix + theme + "." + THEME_CLASS_PREFIX;
                String cssFilesKey = themePrefix + theme + "." + THEME_CSSFILES_PREFIX;
                String javaScriptModulesKey = themePrefix + theme + "." + THEME_JAVASCRIPTMODULES_PREFIX;

                String id = StringUtils.trim(themeValues.get(idKey));
                String name = themeValues.get(nameKey);
                int order = StringUtils.isBlank(themeValues.get(orderKey)) ? 1 : Integer.valueOf(themeValues
                        .get(orderKey));
                String className = themeValues.get(classNameKey);
                String cssFiles = themeValues.get(cssFilesKey);
                String javaScriptModules = themeValues.get(javaScriptModulesKey);

                // 确保主题模块与ID一致
                if (!StringUtils.equals(theme, id)) {
                    throw new RuntimeException("主题模块配置的属性键与ID不匹配: " + id + " != " + theme);
                }

                List<CssFile> cssFileList = new ArrayList<CssFile>();
                if (StringUtils.isNotBlank(cssFiles)) {
                    String[] cssFileArray = cssFiles.split(Separator.COMMA.getValue());
                    for (String cssId : cssFileArray) {
                        CssFile cssFile = parserContext.getCssFile(StringUtils.trim(cssId));
                        if (cssFile != null) {
                            cssFileList.add(cssFile);
                        } else {
                            parserContext.registerPendingCssFileForTheme(StringUtils.trim(cssId), id);
                        }
                    }
                }
                List<JavaScriptModule> javaScriptModuleList = new ArrayList<JavaScriptModule>();
                if (StringUtils.isNotBlank(javaScriptModules)) {
                    String[] javaScriptArray = javaScriptModules.split(Separator.COMMA.getValue());
                    for (String jsModuleId : javaScriptArray) {
                        JavaScriptModule jsModule = parserContext.getJavaScriptModule(StringUtils.trim(jsModuleId));
                        if (jsModule != null) {
                            javaScriptModuleList.add(jsModule);
                        } else {
                            parserContext.registerPendingJavaScriptModuleForTheme(StringUtils.trim(jsModuleId), id);
                        }
                    }
                }

                if (StringUtils.isBlank(className)) {
                    // 默认SimpleTheme
                    SimpleTheme simpleTheme = new SimpleTheme();
                    simpleTheme.setId(id);
                    simpleTheme.setName(name);
                    simpleTheme.setOrder(order);
                    simpleTheme.setCssFiles(cssFileList);
                    simpleTheme.setJavaScriptModules(javaScriptModuleList);
                    parserContext.registerTheme(simpleTheme);
                } else {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Class<?> paramClazz[] = {String.class, String.class, List.class, List.class, int.class};
                        Constructor<?> cons = clazz.getConstructor(paramClazz);
                        SimpleTheme simpleTheme = (SimpleTheme) cons.newInstance(id, name, cssFileList,
                                javaScriptModuleList, order);
                        parserContext.registerTheme(simpleTheme);
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }

    }

}
