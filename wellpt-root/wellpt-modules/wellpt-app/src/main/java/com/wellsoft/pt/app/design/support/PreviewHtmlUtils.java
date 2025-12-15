/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.support;

import com.wellsoft.context.enums.Encoding;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;

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
 * 2016年5月10日.1	zhulh		2016年5月10日		Create
 * </pre>
 * @date 2016年5月10日
 */
public class PreviewHtmlUtils {

    public static String getPreviewHtml(String ftl, Map<String, Object> root) {
        try {
            return generateHtml(ftl, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String generateHtml(String name, Map<String, Object> root) throws Exception {
        Configuration cfg = new Configuration();
        loadTemplate(name, cfg);
        cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

        Template template = cfg.getTemplate(name, Encoding.UTF8.getValue());

        Writer writer = new StringWriter();
        template.process(root, writer);
        writer.flush();
        writer.close();
        return writer.toString();
    }

    private static final void loadTemplate(String templateName, Configuration cfg) throws IOException {
        String path = "/com/wellsoft/pt/app/design/ftl/" + templateName;
        URL ftlRes = PreviewHtmlUtils.class.getResource(path);
        // 加载字符串模板
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate(templateName, IOUtils.toString(ftlRes.openStream()));
        cfg.setTemplateLoader(loader);
    }

}
