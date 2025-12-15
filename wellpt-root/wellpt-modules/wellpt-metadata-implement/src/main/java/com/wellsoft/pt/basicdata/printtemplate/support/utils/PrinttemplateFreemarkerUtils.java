package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;

import java.util.Map;

public class PrinttemplateFreemarkerUtils {
    public static String process(PrintTemplate printTemplate, String content, Map<String, Object> data)
            throws Exception {
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        if (printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
            return templateEngine.process(content, data, new CustomXMLPrintTemplateBeanWrapper());
        } else {
            return templateEngine.process(content, data);
        }
    }
}
