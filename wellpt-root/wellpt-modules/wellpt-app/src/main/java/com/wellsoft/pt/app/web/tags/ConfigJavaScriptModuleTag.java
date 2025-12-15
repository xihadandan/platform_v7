/*
 * @(#)2016-10-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.RequireJsHelper;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

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
public class ConfigJavaScriptModuleTag extends WebAppTagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8246766460469626471L;

    // JS模块ID
    private String id;

    // 额外的JS模块，多个逗号隔开
    private String extraModules;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param extraModules 要设置的extraModules
     */
    public void setExtraModules(String extraModules) {
        this.extraModules = extraModules;
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

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
     */
    @Override
    protected int doStartTagInternal() throws Exception {
        AppContext appContext = AppContextHolder.getContext();
        boolean isMobileApp = appContext.isMobileApp();
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            appContext.setMobileApp(StringUtils.equals("true", request.getParameter("isMobileApp")));
            JavaScriptModule javaScriptModule = appContext.getJavaScriptModule(this.id);
            ArrayList<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
            javaScriptModules.addAll(getExtraJavaScriptModules());
            javaScriptModules.add(javaScriptModule);

            // 主题配置的JS模块
            Theme theme = getTheme();
            if (theme != null) {
                javaScriptModules.addAll(theme.getJavaScriptModules());
            }

            String configScript = RequireJsHelper.getConfigScript(request, javaScriptModules);

            Writer writer = pageContext.getOut();
            writer.write("<script type=\"text/javascript\" src=\""
                    + request.getContextPath()
                    + AppContextHolder.getContext().getJavaScriptModule(AppConstants.REQUIRE_JS_MODULE)
                    .getConfusePath() + ".js\"></script>");
            writer.write(Tag.SCRIPT_START);
            writer.write(configScript);
            writer.write(Tag.SCRIPT_END);
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            appContext.setMobileApp(isMobileApp);
        }
        return SKIP_BODY;
    }
}
