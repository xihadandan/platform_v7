/*
 * @(#)2016-10-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.RequireJsHelper;
import com.wellsoft.pt.app.support.WebAppScriptHelper;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
 * 2016-10-20.1	zhulh		2016-10-20		Create
 * </pre>
 * @date 2016-10-20
 */
public class RequireDyformJavaScriptModuleTag extends DyformTagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8246766460469626471L;

    // 程序入口模块
    private String dataMain;

    // 表单定义UUID
    private String formUuid;

    // 需要加载的模块，多个逗号隔开
    private String requireModules;

    /**
     * @return the dataMain
     */
    public String getDataMain() {
        return dataMain;
    }

    /**
     * @param dataMain 要设置的dataMain
     */
    public void setDataMain(String dataMain) {
        this.dataMain = dataMain;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the requireModules
     */
    public String getRequireModules() {
        return requireModules;
    }

    /**
     * @param requireModules 要设置的requireModules
     */
    public void setRequireModules(String requireModules) {
        this.requireModules = requireModules;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
     */
    @Override
    protected int doStartTagInternal() throws Exception {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AppContext appContext = AppContextHolder.getContext();
            JavaScriptModule dataMainModule = appContext.getJavaScriptModule(this.dataMain);

            Set<JavaScriptModule> javaScriptModules = Sets.newHashSet();
            javaScriptModules.addAll(getDependencies(dataMainModule));
            Object dyformJsModuleCache = request.getSession().getAttribute(getFormUuid() + "_jsModules");
            javaScriptModules.addAll(dyformJsModuleCache != null ? (Collection) dyformJsModuleCache : getRequireDyformJsModules());
            javaScriptModules.addAll(getRequireJsModules());
            request.getSession().removeAttribute(getFormUuid() + "_jsModules");

            // 主题配置的JS模块
            Theme theme = getTheme();
            if (theme != null) {
                javaScriptModules.addAll(theme.getJavaScriptModules());
            }


            String callbackScript = "require([\"pt/js/app/app\"], function(){require([\"" + this.dataMain + "\"]);"
                    + "});";
            String requireScript = RequireJsHelper.getRequireScript(javaScriptModules, callbackScript);

            StringBuilder sb = new StringBuilder();
            sb.append(Tag.SCRIPT_START);
            String webAppInitScript = WebAppScriptHelper.generateInitScript(request, null, null, null, null,
                    appContext.getCurrentUserAppData());
            sb.append(webAppInitScript);
            sb.append(requireScript);
            sb.append(Tag.SCRIPT_END);

            pageContext.getOut().write(sb.toString());
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return SKIP_BODY;
    }

    /**
     * @param dataMainModule
     * @return
     */
    private Collection<? extends JavaScriptModule> getDependencies(JavaScriptModule dataMainModule) {
        if (dataMainModule == null) {
            return Collections.emptyList();
        }

        Set<String> dependencies = dataMainModule.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return Collections.emptyList();
        }

        Set<JavaScriptModule> javaScriptModules = Sets.newHashSet();
        for (String javaScriptModule : dependencies) {
            javaScriptModules.add(AppContextHolder.getContext().getJavaScriptModule(javaScriptModule));
        }
        return javaScriptModules;
    }

    /**
     * @return
     */
    private Collection<? extends JavaScriptModule> getRequireDyformJsModules() {
        return getDyformJavaScriptModules(this.getFormUuid());
    }

    /**
     * @return
     */
    private Collection<? extends JavaScriptModule> getRequireJsModules() {
        if (StringUtils.isBlank(requireModules)) {
            return Collections.emptyList();
        }

        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        String[] jsModules = StringUtils.split(requireModules, Separator.COMMA.getValue());
        for (String jsModule : jsModules) {
            javaScriptModules.add(AppContextHolder.getContext().getJavaScriptModule(StringUtils.trim(jsModule)));
        }
        return javaScriptModules;
    }

}
