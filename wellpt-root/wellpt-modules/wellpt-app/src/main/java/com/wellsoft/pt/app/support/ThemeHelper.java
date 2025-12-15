/*
 * @(#)2016-12-21 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Description: 主题铺助类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-12-21.1	zhulh		2016-12-21		Create
 * </pre>
 * @date 2016-12-21
 */
public class ThemeHelper {

    /**
     * @param request
     * @param appContext
     * @return
     */
    public static Theme getTheme(HttpServletRequest request, AppContext appContext) {
        return getTheme(request, appContext, null);
    }

    /**
     * @param request
     * @param appContext
     * @param defaultTheme
     * @return
     */
    public static Theme getTheme(HttpServletRequest request, AppContext appContext, String defaultTheme) {
        // 使用门户主题
        String pageUuid = ObjectUtils.toString(WebUtils.getSessionAttribute(request, "portalPageUuid"));
        Theme portalTheme = appContext.getTheme(ObjectUtils.toString(
                WebUtils.getSessionAttribute(request, "portalTheme_" + pageUuid), StringUtils.EMPTY));
        if (portalTheme != null) {
            return portalTheme;
        }
        // 1、从Cookie中取
        Cookie cookie = WebUtils.getCookie(request, AppConstants.KEY_APP_THEME);
        if (cookie != null) {
            return appContext.getTheme(cookie.getValue());
        }
        // 2、返回指定的默认主题
        Theme returnDefaultTheme = null;
        if (StringUtils.isNotBlank(defaultTheme)) {
            returnDefaultTheme = appContext.getTheme(defaultTheme);
        }
        if (returnDefaultTheme != null) {
            // 默认主题设置到session中
            WebUtils.setSessionAttribute(request, AppConstants.KEY_APP_THEME, returnDefaultTheme.getId());
            return returnDefaultTheme;
        }
        // 3、从当前Session属性中取
        String theme = (String) WebUtils.getSessionAttribute(request, AppConstants.KEY_APP_THEME);
        if (StringUtils.isNotBlank(theme)) {
            return appContext.getTheme(theme);
        }
        // 4、返回系统默认主题
        return appContext.getTheme(AppConstants.THEME_DEFAULT);
    }

}
