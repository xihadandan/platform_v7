/*
 * @(#)2016年8月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.*;

/**
 * Description: CSS辅助类
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月16日.1	wujx		2016年8月16日		Create
 * </pre>
 * @date 2016年8月16日
 */
public class CssHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CssHelper.class);

    /**
     * 获取Web应用基础的样式
     *
     * @return
     */
    public static List<CssFile> getWebAppBaseCssFiles() {
        ArrayList<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.bootstrap.id", "bootstrap"));
        cssFiles.add(getCssFile("pt.css.pace.id", "pace"));
        cssFiles.add(getCssFile("pt.css.app-base.id", "app-base"));
        cssFiles.add(getCssFile("pt.css.font-awesome.id", "font-awesome"));
        cssFiles.add(getCssFile("pt.css.iconfont.id", "iconfont"));
        cssFiles.add(getCssFile("pt.css.wellsoft-iconfont.id", "wellsoft-iconfont"));
        return cssFiles;
    }

    private static CssFile getCssFile(String key, String defaultValue) {
        String cssId = Config.getValue(key, defaultValue);
        return AppContextHolder.getContext().getCssFile(cssId);
    }

    /**
     * @param javaScriptModules
     * @param cssFiles
     */
    public static void addJavaScriptModuleCssFiles(List<JavaScriptModule> javaScriptModules,
                                                   List<CssFile> cssFiles) {
        for (JavaScriptModule javaScriptModule : javaScriptModules) {
            cssFiles.addAll(javaScriptModule.getCssFiles());
        }
    }

    /**
     * 返回main.js的requirejs.config脚本
     *
     * @param request
     * @param javaScriptModules
     * @return
     */
    public static String getCssImport(HttpServletRequest request, Collection<CssFile> cssFiles) {
        Collection<CssFile> noDuplCssFiles = removeDuplicate(cssFiles);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("ctx", request.getContextPath());
        root.put("cssFiles", noDuplCssFiles);
        try {
            return generateCss("css_import.ftl", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static Collection<CssFile> removeDuplicate(Collection<CssFile> cssFiles) {
        Map<String, CssFile> noDuplicateMap = new HashMap<String, CssFile>();
        for (CssFile cssFile : cssFiles) {
            if (cssFile == null) {
                continue;
            }
            if (!noDuplicateMap.containsKey(cssFile.getPath())) {
                noDuplicateMap.put(cssFile.getPath(), cssFile);
            }
        }
        List<CssFile> noDuplCssFiles = new ArrayList<CssFile>(noDuplicateMap.values());
        OrderComparator.sort(noDuplCssFiles);
        return noDuplCssFiles;
    }

    private static final String generateCss(String name,
                                            Map<String, Object> root) throws Exception {
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

    private static final void loadTemplate(String templateName,
                                           Configuration cfg) throws IOException {
        String path = "/com/wellsoft/pt/app/support/ftl/" + templateName;
        URL ftlRes = CssHelper.class.getResource(path);
        // 加载字符串模板
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate(templateName, IOUtils.toString(ftlRes.openStream()));
        cfg.setTemplateLoader(loader);
    }

    /**
     * @param piFunctions
     * @return
     */
    public static List<CssFile> getCssFiles(List<PiFunction> piFunctions) {
        AppContext appContext = AppContextHolder.getContext();
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        for (PiFunction piFunction : piFunctions) {
            CssFile cssFile = appContext.getCssFile(piFunction.getId());
            if (cssFile != null) {
                cssFiles.add(cssFile);
            }
        }
        return cssFiles;
    }

}
