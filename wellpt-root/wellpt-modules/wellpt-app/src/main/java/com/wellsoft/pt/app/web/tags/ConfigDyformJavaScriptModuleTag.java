/*
 * @(#)2016-10-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.google.common.base.Throwables;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.RequireJsHelper;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class ConfigDyformJavaScriptModuleTag extends DyformTagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8246766460469626471L;

    // 表单定义UUID
    private String formUuid;

    private String formUuids;

    // 额外的CSS模块，多个逗号隔开
    private String extraModules;

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
     * @return the formUuids
     */
    public String getFormUuids() {
        return formUuids;
    }

    /**
     * @param formUuids 要设置的formUuids
     */
    public void setFormUuids(String formUuids) {
        this.formUuids = formUuids;
    }

    /**
     * @return the extraModules
     */
    public String getExtraModules() {
        return extraModules;
    }

    /**
     * @param extraModules 要设置的extraModules
     */
    public void setExtraModules(String extraModules) {
        this.extraModules = extraModules;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
     */
    @Override
    protected int doStartTagInternal() throws Exception {
        try {
            ArrayList<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
            Collection dyformJavascriptModules = getDyformJavaScriptModules();
            javaScriptModules.addAll(dyformJavascriptModules);
            javaScriptModules.addAll(getExtraJavaScriptModules());

            // 主题配置的JS模块
            Theme theme = getTheme();
            if (theme != null) {
                javaScriptModules.addAll(theme.getJavaScriptModules());
            }

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            request.getSession().setAttribute(getFormUuid() + "_jsModules", dyformJavascriptModules);//缓存表单解析的脚本，RequireDyformJavaScriptModuleTag使用
            String configScript = RequireJsHelper.getConfigScript(request, javaScriptModules);

            Writer writer = pageContext.getOut();
            writer.write("<script src=\""
                    + request.getContextPath()
                    + AppContextHolder.getContext().getJavaScriptModule(AppConstants.REQUIRE_JS_MODULE)
                    .getConfusePath() + ".js\"></script>");
            writer.write(Tag.SCRIPT_START);
            writer.write(configScript);
            writer.write(Tag.SCRIPT_END);
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
        return SKIP_BODY;
    }

    /**
     * @return
     */
    private Collection<? extends JavaScriptModule> getDyformJavaScriptModules() {
        if (StringUtils.isNotBlank(getFormUuids())) {
            String[] formUuids = getFormUuids().split(Separator.SEMICOLON.getValue());
            List<JavaScriptModule> list = new ArrayList<JavaScriptModule>();
            for (String formUuid : formUuids) {
                list.addAll(getDyformJavaScriptModules(formUuid));
            }
            return list;
        }
        return getDyformJavaScriptModules(this.getFormUuid());
    }

    /**
     * @return
     */
    private Collection<? extends JavaScriptModule> getExtraJavaScriptModules() {
        if (StringUtils.isBlank(extraModules)) {
            return Collections.emptyList();
        }

        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        String[] jsModules = StringUtils.split(extraModules, Separator.COMMA.getValue());
        for (String jsModule : jsModules) {
            javaScriptModules.add(AppContextHolder.getContext().getJavaScriptModule(StringUtils.trim(jsModule)));
        }
        return javaScriptModules;
    }

}
