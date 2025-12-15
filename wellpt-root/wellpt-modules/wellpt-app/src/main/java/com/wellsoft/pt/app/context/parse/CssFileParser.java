/*
 * @(#)2017-01-24 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.parse;

import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import com.wellsoft.pt.app.context.AppParserContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.css.SimpleCssFile;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Description: 配置文件CSS信息解析
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
public class CssFileParser extends AbstractAppContextParser {
    private static final String PT_CSS_PREFIX = "pt.css.";
    private static final String APP_CSS_PREFIX = "app.css.";

    private static final String CSS_ID_PREFIX = "id";
    private static final String CSS_NAME_PREFIX = "name";
    private static final String CSS_PATH_PREFIX = "path";
    private static final String CSS_ORDER_PREFIX = "order";
    private static final String CSS_CLASS_PREFIX = "class";

    @Autowired(required = false)
    private List<CssFile> customCssFiles;
    private Map<String, CssFile> customCssFileMap = new HashMap<String, CssFile>();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextParser#parse(com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport, com.wellsoft.pt.app.context.AppParserContext)
     */
    public void parse(AppContextPropertiesConfigurationSupport configurationSupport, AppParserContext parserContext) {
        Map<String, String> cssValues = extractValues(configurationSupport, PT_CSS_PREFIX, APP_CSS_PREFIX);
        Set<String> cssSet = new HashSet<String>();
        for (String key : cssValues.keySet()) {
            String[] keyArray = key.split("\\.");
            String css = StringUtils.trim(keyArray[2]);
            String cssPrefix = keyArray[0] + "." + keyArray[1] + ".";
            if (cssSet.contains(css)) {
                continue;
            } else {
                cssSet.add(keyArray[2]);
                String idKey = cssPrefix + css + "." + CSS_ID_PREFIX;
                String nameKey = cssPrefix + css + "." + CSS_NAME_PREFIX;
                String pathKey = cssPrefix + css + "." + CSS_PATH_PREFIX;
                String orderKey = cssPrefix + css + "." + CSS_ORDER_PREFIX;
                String classNameKey = cssPrefix + css + "." + CSS_CLASS_PREFIX;

                String id = StringUtils.trim(cssValues.get(idKey));
                String name = cssValues.get(nameKey);
                String path = configurationSupport.createRelative(cssValues.get(pathKey), "css");
                path = getCustomCssFilePath(id, path);
                int order = StringUtils.isBlank(cssValues.get(orderKey)) ? 1 : Integer.valueOf(cssValues.get(orderKey));
                String className = cssValues.get(classNameKey);

                // 确保CSS模块与ID一致
                if (!StringUtils.equals(css, id)) {
                    throw new RuntimeException("CSS模块配置的属性键与ID不匹配: " + id + " != " + css);
                }

                if (StringUtils.isBlank(className)) {
                    // 默认SimpleCssFile
                    CssFile cssFile = new SimpleCssFile(id, name, path, order);
                    parserContext.registerCssFile(cssFile);
                } else {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Class<?> paramClazz[] = {String.class, String.class, String.class, int.class};
                        Constructor<?> cons = clazz.getConstructor(paramClazz);
                        CssFile cssFile = (CssFile) cons.newInstance(id, name, path, order);
                        parserContext.registerCssFile(cssFile);
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }
    }

    /**
     * @param id
     * @param path
     * @return
     */
    private String getCustomCssFilePath(String id, final String path) {
        String tmpPath = path;
        if (customCssFiles == null || customCssFiles.isEmpty()) {
            return path;
        }

        if (customCssFileMap.isEmpty()) {
            for (CssFile cssFile : customCssFiles) {
                customCssFileMap.put(cssFile.getId(), cssFile);
            }
        }
        if (customCssFileMap.containsKey(id)) {
            tmpPath = customCssFileMap.get(id).getPath();
            logger.info("use custom css file path [{}] for {}", tmpPath, id);
        }
        return tmpPath;
    }

}
