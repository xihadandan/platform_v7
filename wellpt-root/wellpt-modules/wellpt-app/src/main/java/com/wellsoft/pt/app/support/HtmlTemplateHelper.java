/*
 * @(#)2016-09-23 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.context.enums.Encoding;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-23.1	zhulh		2016-09-23		Create
 * </pre>
 * @date 2016-09-23
 */
public class HtmlTemplateHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RequireJsHelper.class);

    /**
     * @param templateName
     * @param request
     * @return
     */
    public static String getJavaScript(String templateName, Map<String, Object> root, HttpServletRequest request) {
        try {
            return generateScript(templateName + ".js", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param templateName
     * @param request
     * @return
     */
    public static String getHtml(String templateName, Map<String, Object> root, HttpServletRequest request) {
        root.put("ctx", request.getContextPath());
        try {
            return generateScript(templateName + ".html", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static final String generateScript(String name, Map<String, Object> root) throws Exception {
        Configuration cfg = new Configuration();
        loadTemplate(name, cfg);
        cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

        Template template = cfg.getTemplate(name, Encoding.UTF8.getValue());

        Writer writer = new StringWriter();
        try {
            template.process(root, writer);
            writer.flush();
        } finally {
            writer.close();
        }
        return writer.toString();
    }

    private static final void loadTemplate(String templateName, Configuration cfg) throws IOException {
        String path = "/com/wellsoft/pt/app/support/ftl/" + templateName;
        URL ftlRes = RequireJsHelper.class.getResource(path);
        // 加载字符串模板
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate(templateName, IOUtils.toString(ftlRes.openStream()));
        cfg.setTemplateLoader(loader);
    }

}
