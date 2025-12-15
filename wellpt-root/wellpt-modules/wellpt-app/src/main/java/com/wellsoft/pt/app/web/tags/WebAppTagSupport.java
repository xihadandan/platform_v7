/*
 * @(#)Oct 20, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.ThemeHelper;
import com.wellsoft.pt.app.theme.Theme;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author huanglc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Oct 20, 2016.1	huanglc		Oct 20, 2016		Create
 * </pre>
 * @date Oct 20, 2016
 */
public abstract class WebAppTagSupport extends HtmlEscapingAwareTag {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3795232635223446855L;

    /**
     * 通过Key加载CssFile
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected static CssFile getCssFile(String key, String defaultValue) {
        String cssId = Config.getValue(key, defaultValue);
        return AppContextHolder.getContext().getCssFile(cssId);
    }

    /**
     * 获取当前主题
     *
     * @return
     */
    protected Theme getTheme() {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        return ThemeHelper.getTheme(request, AppContextHolder.getContext());
    }

    /**
     * 通过Key加载JavaScriptModule
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected JavaScriptModule getJavaScriptModule(String key, String defaultValue) {
        String id = Config.getValue(key, defaultValue);
        return AppContextHolder.getContext().getJavaScriptModule(id);
    }

}
