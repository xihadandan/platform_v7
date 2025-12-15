/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.parse;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import com.wellsoft.pt.app.context.AppParserContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.RequireJSJavaScriptModule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 配置文件JS模块解析
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
public class JavaScriptModuleParser extends AbstractAppContextParser {
    private static final String JS_MODULE_PATTERN = ".js.module.*?(?=\\.id)";
    private static final String JS_MODULE_FLAG = ".js.module.";
    private static final String PT_JS_MODULE_PREFIX = "pt.js.module.";
    private static final String APP_JS_MODULE_PREFIX = "app.js.module.";

    private static final String JS_MODULE_ID_PREFIX = "id";
    private static final String JS_MODULE_NAME_PREFIX = "name";
    private static final String JS_MODULE_PATH_PREFIX = "path";
    private static final String JS_MODULE_CSSFILES_PREFIX = "cssFiles";
    private static final String JS_MODULE_CLASS_PREFIX = "class";
    private static final String JS_MODULE_ABSTRACT_PREFIX = "abstract";
    private static final String JS_MODULE_PARENT_PREFIX = "parent";
    private static final String JS_MODULE_DEPENDENCIES_PREFIX = "dependencies";
    private static final String JS_MODULE_EXPORTS_PREFIX = "exports";
    private static final String JS_MODULE_CONFUSE_PREFIX = "confuse";
    private static final String JS_MODULE_ORDER_PREFIX = "order";

    @Autowired(required = false)
    private List<JavaScriptModule> customJavaScriptModules;
    private Map<String, JavaScriptModule> customJavaScriptModuleMap = new HashMap<String, JavaScriptModule>();

    /**
     * @param expression
     * @return
     */
    private String getJsModule(String expression) {
        // 使用正则表达式的最小匹配、零宽断言
        Pattern pattern = Pattern.compile(JS_MODULE_PATTERN);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String group = matcher.group();
            return group.substring(JS_MODULE_FLAG.length());
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextParser#parse(com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport, com.wellsoft.pt.app.context.AppParserContext)
     */
    @Override
    public void parse(AppContextPropertiesConfigurationSupport configurationSupport, AppParserContext parserContext) {
        Map<String, String> jsModuleValues = extractValues(configurationSupport, PT_JS_MODULE_PREFIX,
                APP_JS_MODULE_PREFIX);
        Set<String> jsModulesSet = new HashSet<String>();
        for (String key : jsModuleValues.keySet()) {
            String jsModule = getJsModule(key);// StringUtils.trim(keyArray[3]);
            if (StringUtils.isBlank(jsModule)) {
                continue;
            }
            String jsModulePrefix = PT_JS_MODULE_PREFIX;
            if (key.startsWith(APP_JS_MODULE_PREFIX)) {
                jsModulePrefix = APP_JS_MODULE_PREFIX;
            }
            if (jsModulesSet.contains(jsModule)) {
                continue;
            } else {
                jsModulesSet.add(jsModule);
                String idKey = jsModulePrefix + jsModule + "." + JS_MODULE_ID_PREFIX;
                String nameKey = jsModulePrefix + jsModule + "." + JS_MODULE_NAME_PREFIX;
                String pathKey = jsModulePrefix + jsModule + "." + JS_MODULE_PATH_PREFIX;
                String cssFilesKey = jsModulePrefix + jsModule + "." + JS_MODULE_CSSFILES_PREFIX;
                String classNameKey = jsModulePrefix + jsModule + "." + JS_MODULE_CLASS_PREFIX;
                String abstractKey = jsModulePrefix + jsModule + "." + JS_MODULE_ABSTRACT_PREFIX;
                String parentKey = jsModulePrefix + jsModule + "." + JS_MODULE_PARENT_PREFIX;
                String dependenciesKey = jsModulePrefix + jsModule + "." + JS_MODULE_DEPENDENCIES_PREFIX;
                String exportsKey = jsModulePrefix + jsModule + "." + JS_MODULE_EXPORTS_PREFIX;
                String confuseKey = jsModulePrefix + jsModule + "." + JS_MODULE_CONFUSE_PREFIX;
                String orderKey = jsModulePrefix + jsModule + "." + JS_MODULE_ORDER_PREFIX;

                String id = StringUtils.trim(jsModuleValues.get(idKey));
                String name = jsModuleValues.get(nameKey);
                String path = configurationSupport.createRelative(jsModuleValues.get(pathKey), "js");
                path = getCustomJavaScriptModulePath(id, path);
                String cssFiles = jsModuleValues.get(cssFilesKey);
                String className = jsModuleValues.get(classNameKey);
                String abst = jsModuleValues.get(abstractKey);
                String parentIds = jsModuleValues.get(parentKey);
                String dependencies = jsModuleValues.get(dependenciesKey);
                String exports = jsModuleValues.get(exportsKey);
                String confuse = jsModuleValues.get(confuseKey);
                boolean isConfuse = StringUtils.isBlank(confuse) ? true : Boolean.valueOf(confuse);
                int order = NumberUtils.toInt(jsModuleValues.get(orderKey), 0);

                // 确保JS模块与ID一致
                if (!StringUtils.equals(jsModule, id)) {
                    throw new RuntimeException("JS模块配置的属性键与ID不匹配: " + id + " != " + jsModule);
                }

                Set<CssFile> cssFileList = new HashSet<CssFile>();
                if (StringUtils.isNotBlank(cssFiles)) {
                    String[] cssFileArray = cssFiles.split(Separator.COMMA.getValue());
                    for (String cssId : cssFileArray) {
                        CssFile cssFile = parserContext.getCssFile(StringUtils.trim(cssId));
                        if (cssFile != null) {
                            cssFileList.add(cssFile);
                        } else {
                            parserContext.registerPendingCssFileForJavaScriptModule(StringUtils.trim(cssId), id);
                        }
                    }
                }

                Set<String> dependencyList = new LinkedHashSet<String>(); // 后续调用list等方法会报错，如dependencyList.addAll等
                if (StringUtils.isNotBlank(dependencies)) {
                    String[] dependencyArray = dependencies.split(Separator.COMMA.getValue());
                    for (String dependency : dependencyArray) {
                        dependencyList.add(StringUtils.trim(dependency));
                    }
                }
                if (StringUtils.isNotBlank(parentIds)) {
                    String[] parentIdArray = parentIds.split(Separator.COMMA.getValue());
                    for (String parentId : parentIdArray) {
                        JavaScriptModule parentJSModule = parserContext.getJavaScriptModule(StringUtils.trim(parentId));
                        if (parentJSModule != null) {
                            // 父类不是抽象类，直接依赖
                            if (StringUtils.isNotBlank(parentJSModule.getPath())) {
                                dependencyList.add(parentJSModule.getId());
                            }

                            // 获取父类的依赖
                            Set<String> paretnDependencyList = parentJSModule.getDependencies();
                            if (paretnDependencyList != null && paretnDependencyList.size() > 0) {
                                dependencyList.addAll(paretnDependencyList);
                            }

                            // 获取父类的基类
                            String parentClassNameKey = PT_JS_MODULE_PREFIX + "." + parentId + "."
                                    + JS_MODULE_CLASS_PREFIX;
                            String parentClassName = jsModuleValues.get(parentClassNameKey);
                            if (StringUtils.isBlank(parentClassName)) {
                                parentClassNameKey = APP_JS_MODULE_PREFIX + "." + parentId + "."
                                        + JS_MODULE_CLASS_PREFIX;
                                parentClassName = jsModuleValues.get(parentClassNameKey);
                            }
                            className = parentClassName;
                        } else {
                            parserContext.registerPendingJavaScriptModule(parentId, id);
                        }
                    }
                }
                if (StringUtils.isBlank(className)) {
                    // 默认RequireJSJavaScriptModule
                    RequireJSJavaScriptModule requireJSJavaScriptModule = new RequireJSJavaScriptModule();
                    requireJSJavaScriptModule.setId(id);
                    requireJSJavaScriptModule.setName(name);
                    requireJSJavaScriptModule.setPath(path);
                    requireJSJavaScriptModule.setCssFiles(cssFileList);
                    requireJSJavaScriptModule.setDependencies(dependencyList);
                    requireJSJavaScriptModule.setExports(exports);
                    requireJSJavaScriptModule.setConfuse(isConfuse);
                    requireJSJavaScriptModule.setOrder(order);

                    if (Config.TRUE.equalsIgnoreCase(abst)) {
                        parserContext.registerAbstractJavaScriptModule(requireJSJavaScriptModule);
                    } else {
                        parserContext.registerJavaScriptModule(requireJSJavaScriptModule);
                    }
                } else {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Class<?> paramClazz[] = {String.class, String.class, String.class, List.class};
                        Constructor<?> cons = clazz.getConstructor(paramClazz);
                        if (Config.TRUE.equalsIgnoreCase(abst)) {
                            JavaScriptModule javaScriptModule = (JavaScriptModule) cons.newInstance(id, name, path,
                                    dependencyList, exports);
                            parserContext.registerAbstractJavaScriptModule(javaScriptModule);
                        } else {
                            JavaScriptModule javaScriptModule = (JavaScriptModule) cons.newInstance(id, name, path,
                                    dependencyList, exports);
                            parserContext.registerJavaScriptModule(javaScriptModule);
                        }
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }

    }

    /**
     * 返回自定义的JS路径
     *
     * @param id
     * @param path
     * @return
     */
    private String getCustomJavaScriptModulePath(String id, final String path) {
        String tmpPath = path;
        if (customJavaScriptModules == null || customJavaScriptModules.isEmpty()) {
            return tmpPath;
        }

        if (customJavaScriptModuleMap.isEmpty()) {
            for (JavaScriptModule javaScriptModule : customJavaScriptModules) {
                customJavaScriptModuleMap.put(javaScriptModule.getId(), javaScriptModule);
            }
        }
        if (customJavaScriptModuleMap.containsKey(id)) {
            tmpPath = customJavaScriptModuleMap.get(id).getPath();
            logger.info("use custom javascript module path [{}] for {}", tmpPath, id);
        }
        return tmpPath;
    }

}
