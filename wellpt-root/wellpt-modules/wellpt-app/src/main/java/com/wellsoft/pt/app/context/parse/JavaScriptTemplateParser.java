/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.parse;

import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import com.wellsoft.pt.app.context.AppParserContext;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.js.JuicerTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description: 配置文件JS模板解析
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
public class JavaScriptTemplateParser extends AbstractAppContextParser {
    private static final String PT_JS_TEMPLATE_PREFIX = "pt.js.template.";
    private static final String APP_JS_TEMPLATE_PREFIX = "app.js.template.";

    private static final String JS_TEMPLATE_ID_PREFIX = "id";
    private static final String JS_TEMPLATE_NAME_PREFIX = "name";
    private static final String JS_TEMPLATE_SOURCE_PREFIX = "source";
    private static final String JS_TEMPLATE_ORDER_PREFIX = "order";
    private static final String JS_TEMPLATE_CLASS_PREFIX = "class";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextParser#parse(com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport, com.wellsoft.pt.app.context.AppParserContext)
     */
    @Override
    public void parse(AppContextPropertiesConfigurationSupport configurationSupport, AppParserContext parserContext) {
        Map<String, String> jsTemplateValues = extractValues(configurationSupport, PT_JS_TEMPLATE_PREFIX,
                APP_JS_TEMPLATE_PREFIX);
        Set<String> jsTemplateSet = new HashSet<String>();
        for (String key : jsTemplateValues.keySet()) {
            String[] keyArray = key.split("\\.");
            String jsTemplate = StringUtils.trim(keyArray[3]);
            String jsTemplatePrefix = keyArray[0] + "." + keyArray[1] + "." + keyArray[2] + ".";
            if (jsTemplateSet.contains(jsTemplate)) {
                continue;
            } else {
                jsTemplateSet.add(jsTemplate);
                String idKey = jsTemplatePrefix + jsTemplate + "." + JS_TEMPLATE_ID_PREFIX;
                String nameKey = jsTemplatePrefix + jsTemplate + "." + JS_TEMPLATE_NAME_PREFIX;
                String sourceKey = jsTemplatePrefix + jsTemplate + "." + JS_TEMPLATE_SOURCE_PREFIX;
                String orderKey = jsTemplatePrefix + jsTemplate + "." + JS_TEMPLATE_ORDER_PREFIX;
                String classNameKey = jsTemplatePrefix + jsTemplate + "." + JS_TEMPLATE_CLASS_PREFIX;

                String id = StringUtils.trim(jsTemplateValues.get(idKey));
                String name = jsTemplateValues.get(nameKey);
                String packageName = configurationSupport.getAppContextConfigurer().getClass().getPackage().getName();
                String packagePath = StringUtils.replace(packageName, ".", "/");
                String configPath = jsTemplateValues.get(sourceKey);
                String path = null;
                if (StringUtils.isNotBlank(configPath) && configPath.startsWith("/")) {
                    path = configPath;
                } else {
                    path = "/" + packagePath + "/" + configPath;
                }
                int order = StringUtils.isBlank(jsTemplateValues.get(orderKey)) ? 1 : Integer.valueOf(jsTemplateValues
                        .get(orderKey));
                String className = jsTemplateValues.get(classNameKey);

                // 确保JS模板与ID一致
                if (!StringUtils.equals(jsTemplate, id)) {
                    throw new RuntimeException("JS模板配置的属性键与ID不匹配: " + id + " != " + jsTemplate);
                }

                if (StringUtils.isBlank(className)) {
                    // 默认JuicerTemplate
                    JavaScriptTemplate javaScriptTemplate = new JuicerTemplate(id, name, path, order);
                    parserContext.registerJavaScriptTemplate(javaScriptTemplate);
                } else {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Class<?> paramClazz[] = {String.class, String.class, String.class, int.class};
                        Constructor<?> cons = clazz.getConstructor(paramClazz);
                        JavaScriptTemplate cssFile = (JavaScriptTemplate) cons.newInstance(id, name, path, order);
                        parserContext.registerJavaScriptTemplate(cssFile);
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }
    }
}
