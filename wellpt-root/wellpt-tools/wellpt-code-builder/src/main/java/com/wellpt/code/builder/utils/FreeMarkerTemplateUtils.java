package com.wellpt.code.builder.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.SoftCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Description: freemarker模板加载工具类
 *
 * @author chenq
 * @date 2018/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/31    chenq		2018/7/31		Create
 * </pre>
 */
public class FreeMarkerTemplateUtils {


    private static final String TEMPLATE_PATH = "/com/wellpt/code/builder/ft/";

    private static final Configuration CONFIGURATION = new Configuration();

    private static final SoftCacheStorage CACHE = new SoftCacheStorage();

    static {
        CONFIGURATION.setTemplateLoader(
                new ClassTemplateLoader(FreeMarkerTemplateUtils.class,
                        TEMPLATE_PATH));
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(CACHE);
    }


    public static Template getTemplate(String templateName) throws Exception {
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (Exception e) {
            throw e;
        }
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }
}
