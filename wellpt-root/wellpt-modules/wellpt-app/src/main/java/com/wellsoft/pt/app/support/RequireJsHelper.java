/*
 * @(#)2016年5月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.version.Version;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.js.RequireJSJavaScriptModule;
import com.wellsoft.pt.cache.FrontCacheUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
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
 * 2016年5月4日.1	zhulh		2016年5月4日		Create
 * </pre>
 * @date 2016年5月4日
 */
public class RequireJsHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RequireJsHelper.class);

    /**
     * 返回main.js的requirejs.config脚本
     *
     * @param request
     * @param javaScriptModules
     * @return
     */
    public static String getConfigScript(HttpServletRequest request, Collection<JavaScriptModule> javaScriptModules) {
        return getConfigScript(request, javaScriptModules, true);
    }

    /**
     * 返回main.js的requirejs.config脚本
     *
     * @param request
     * @param javaScriptModules
     * @param resolveDependency
     * @return
     */
    public static String getConfigScript(HttpServletRequest request, Collection<JavaScriptModule> javaScriptModules,
                                         boolean resolveDependency) {
        Collection<JavaScriptModule> modules = javaScriptModules;
        if (resolveDependency) {
            modules = sortWithResolveDependency(javaScriptModules);
        }
        Map<String, Object> root = new HashMap<String, Object>();
        // 添加APP的JS缓存时间戳
        root.put("jsTimestamp", FrontCacheUtils.get(ModuleID.APP.getName()) + "");
        // 平台运行时版本
        root.put("version", Version.getRuntimeVersion());
        root.put("ctx", Config.getValue("contextPath"));
        root.put("modules", javaScriptModuleDependenciesUseExports(modules));
        try {
            return generateScript("requirejs_config.ftl", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 依赖名称转化为使用导出的名称
     *
     * @param javaScriptModules
     * @return
     */
    public static Collection<JavaScriptModule> javaScriptModuleDependenciesUseExports(
            Collection<JavaScriptModule> javaScriptModules) {
        AppContext appContext = AppContextHolder.getContext();
        List<JavaScriptModule> modules = new ArrayList<JavaScriptModule>();
        for (JavaScriptModule javaScriptModule : javaScriptModules) {
            RequireJSJavaScriptModule module = new RequireJSJavaScriptModule();
            BeanUtils.copyProperties(javaScriptModule, module);
            Set<String> dependencies = javaScriptModule.getDependencies();
            Set<String> newDependencies = new LinkedHashSet<String>();
            for (String dependency : dependencies) {
                try {
                    JavaScriptModule dependencyScriptModule = appContext.getJavaScriptModule(dependency);
                    if (dependencyScriptModule != null) {
                        newDependencies.add(dependencyScriptModule.getExports());
                    } else {
                        newDependencies.add(dependency);
                    }
                } catch (Exception e) {
                    LOG.error("JS module [" + dependency + "] is not found!");
                    throw new RuntimeException(e);
                }
            }
            module.setDependencies(newDependencies);
            modules.add(module);
        }
        return modules;
    }

    /**
     * 返回app.js的require脚本 + callbackScript
     *
     * @param javaScriptModules
     * @param callbackScript
     * @return
     */
    public static String getRequireScript(Collection<JavaScriptModule> javaScriptModules, String callbackScript) {
        return getRequireScript(javaScriptModules, callbackScript, true);
    }

    /**
     * 返回app.js的require脚本 + callbackScript
     *
     * @param javaScriptModules
     * @param callbackScript
     * @param resolveDependency
     * @return
     */
    public static String getRequireScript(Collection<JavaScriptModule> javaScriptModules, String callbackScript,
                                          boolean resolveDependency) {
        Collection<JavaScriptModule> modules = javaScriptModules;
        if (resolveDependency) {
            modules = sortWithResolveDependency(javaScriptModules);
        }
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("modules", javaScriptModuleDependenciesUseExports(modules));
        root.put("callbackScript", callbackScript);
        try {
            return generateScript("requirejs_require.ftl", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 返回app.js的define脚本 + callbackScript
     *
     * @param javaScriptModules
     * @param callbackScript
     * @return
     */
    public static String getDefineScript(List<JavaScriptModule> javaScriptModules, String callbackScript) {
        List<JavaScriptModule> modules = sortWithResolveDependency(javaScriptModules);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("modules", modules);
        root.put("callbackScript", callbackScript);
        try {
            return generateScript("requirejs_define.ftl", root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param javaScriptModules
     * @return
     */
    public static List<JavaScriptModule> sortWithResolveDependency(Collection<JavaScriptModule> javaScriptModules) {
        List<String> ids = new ArrayList<String>();
        List<JavaScriptModule> list = new ArrayList<JavaScriptModule>();
        for (JavaScriptModule javaScriptModule : javaScriptModules) {
            if (javaScriptModule == null) {
                continue;
            }
            resolveDependency(javaScriptModule, ids);
            list.add(javaScriptModule);
        }

        List<JavaScriptModule> tmpList = new ArrayList<JavaScriptModule>();
        Map<String, JavaScriptModule> idMap = ConvertUtils.convertElementToMap(list, AppConstants.KEY_ID);
        for (String id : ids) {
            JavaScriptModule module = idMap.get(id);
            if (module == null) {
                module = AppContextHolder.getContext().getJavaScriptModule(id);
            }
            if (module != null) {
                tmpList.add(module);
            }
        }
        return tmpList;
    }

    /**
     * 如何描述该方法
     *
     * @param javaScriptModule
     * @param ids
     */
    private static void resolveDependency(JavaScriptModule javaScriptModule, List<String> ids) {
        if (javaScriptModule == null) {
            return;
        }

        String id = javaScriptModule.getId();
        String exports = javaScriptModule.getExports();
        // 已存在模块直接返回
        if (ids.contains(id) || ids.contains(exports)) {
            return;
        }

        Set<String> dependencies = javaScriptModule.getDependencies();
        // JS模块
        if (dependencies != null && !dependencies.isEmpty()) {
            AppContext appContext = AppContextHolder.getContext();
            for (String dependency : dependencies) {
                JavaScriptModule dependencyModule = appContext.getJavaScriptModule(dependency);
                resolveDependency(dependencyModule, ids);
            }
        }

        ids.add(id);
        // ids.add(exports);
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

    /**
     * @param piFunctions
     * @return
     */
    public static List<JavaScriptModule> getJavaScriptModules(List<PiFunction> piFunctions) {
        AppContext appContext = AppContextHolder.getContext();
        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        for (PiFunction piFunction : piFunctions) {
            JavaScriptModule piJavaScriptModule = appContext.getJavaScriptModule(piFunction.getId());
            if (piJavaScriptModule != null) {
                javaScriptModules.add(piJavaScriptModule);
            }
        }
        return javaScriptModules;
    }

    /**
     * @param piFunctions
     * @return
     */
    public static List<JavaScriptTemplate> getJavaScriptTemplates(List<PiFunction> piFunctions) {
        AppContext appContext = AppContextHolder.getContext();
        List<JavaScriptTemplate> javaScriptTemplates = new ArrayList<JavaScriptTemplate>();
        for (PiFunction piFunction : piFunctions) {
            JavaScriptTemplate javaScriptTemplate = appContext.getJavaScriptTemplate(piFunction.getId());
            if (javaScriptTemplate != null) {
                javaScriptTemplates.add(javaScriptTemplate);
            }
        }
        return javaScriptTemplates;
    }

    public static String createConfusePath(String path) {
        // 开发环境直接返回
        if (Config.getAppEnv().equalsIgnoreCase(Config.ENV_DEV)
                || "false".equalsIgnoreCase(Config.getValue("confuseJsPath"))) {
            return path;
        }

        String rawPath = path;
        String confusePath = path;
        if (StringUtils.isNotBlank(rawPath)) {
            //			// My97DatePicker、ckeditor会在JS中加载相对路径的样式，不处理
            //			if (rawPath.endsWith("WdatePicker") || rawPath.endsWith("ckeditor")) {
            //				return rawPath;
            //			}
            String realPath = rawPath + ".js";
            if (AppCacheUtils.hasRealPath(realPath)) {
                return AppCacheUtils.getConfusePath(realPath);
            }
            String key = confusePath(rawPath);
            AppCacheUtils.addConfusePath(key + ".js", realPath);
            if (rawPath.startsWith("/resources/")) {
                confusePath = "/resources/" + key;
            } else if (rawPath.startsWith("/web/res/")) {
                confusePath = "/web/res/" + key;
            }
            AppCacheUtils.addRealPath(realPath, confusePath);
        }
        return confusePath;
    }

    /**
     * @param rawPath
     * @return
     */
    private static String confusePath(String rawPath) {
        String path = DigestUtils.md5Hex(rawPath);
        Random random = new Random();
        int splitCount = random.nextInt(1000) % 4 + 1;
        for (int i = 0; i < splitCount; i++) {
            int countLenth = random.nextInt(1000) % 8 + 1;
            int position = splitCount * countLenth;
            if (path.length() > position) {
                String startPart = path.substring(0, position);
                String endPart = path.substring(position);
                String slash = Separator.SLASH.getValue();
                if (!startPart.endsWith(slash) && !endPart.startsWith(slash)) {
                    path = startPart + slash + endPart;
                }
            }
        }
        return path;
    }

    /**
     * @param javaScriptModule
     * @return
     */
    public static JavaScriptModule clone(JavaScriptModule javaScriptModule) {
        JavaScriptModule retJavaScriptModule = null;
        if (javaScriptModule instanceof Cloneable) {
            try {
                retJavaScriptModule = (JavaScriptModule) javaScriptModule.getClass().getMethod("clone")
                        .invoke(javaScriptModule);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (javaScriptModule instanceof RequireJSJavaScriptModule) {
            RequireJSJavaScriptModule source = (RequireJSJavaScriptModule) javaScriptModule;
            RequireJSJavaScriptModule target = new RequireJSJavaScriptModule();
            target.setId(source.getId());
            target.setName(source.getName());
            target.setPath(source.getPath());
            target.setConfuse(source.isConfuse());
            target.getCssFiles().addAll(source.getCssFiles());
            target.getDependencies().addAll(source.getDependencies());
            target.setExports(source.getExports());
            retJavaScriptModule = target;
        } else {
            throw new RuntimeException("javaScriptModule " + javaScriptModule.getId() + " not support clone");
        }
        return retJavaScriptModule;
    }

    /**
     * @param javaScriptModules
     * @return
     */
    public static Collection<? extends String> getJavaScriptModuleIds(List<JavaScriptModule> javaScriptModules) {
        if (CollectionUtils.isEmpty(javaScriptModules)) {
            return Collections.emptyList();
        }
        List<String> ids = Lists.newArrayList();
        for (JavaScriptModule javaScriptModule : javaScriptModules) {
            ids.add(javaScriptModule.getId());
        }
        return ids;
    }

}
