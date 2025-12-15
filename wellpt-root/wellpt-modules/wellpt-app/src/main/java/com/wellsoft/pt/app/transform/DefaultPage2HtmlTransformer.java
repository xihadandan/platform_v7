/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.transform;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.ui.Page;
import com.wellsoft.pt.app.ui.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public class DefaultPage2HtmlTransformer implements Page2HtmlTransformer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.transform.Page2HtmlTransformer#transform(com.wellsoft.pt.app.ui.Page)
     */
    @Override
    public String transform(Page page, AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(page.getDocType());
        sb.append(Tag.HTML_START);
        // meta
        sb.append(Tag.HEAD_START);
        for (String meta : page.getMetas()) {
            sb.append(meta);
        }
        // title
        sb.append(Tag.TITLE_START);
        sb.append(page.getTitle());
        sb.append(Tag.TITLE_END);

        // 页面主题
        Theme theme = page.getTheme(appContext, request, response);

        // 加载CSS
        String ctx = request.getContextPath();
        for (CssFile cssFie : page.getRenderCssFiles(appContext, request, response)) {
            sb.append("<link rel=\"stylesheet\" href=\"" + ctx + cssFie.getPath() + "\">");
        }
        if (theme != null) {
            for (CssFile cssFie : theme.getCssFiles()) {
                sb.append("<link rel=\"stylesheet\" href=\"" + ctx + cssFie.getPath() + "\">");
            }
        }

        // 加载require js
        sb.append("<script data-main=\"" + ctx + "/resources/pt/js/cms/main\" src=\"" + ctx
                + appContext.getJavaScriptModule(AppConstants.REQUIRE_JS_MODULE).getConfusePath() + ".js\"></script>");
        sb.append(Tag.HEAD_END);

        sb.append(Tag.BODY_START);
        // 加载页面组件
        sb.append("<div id='" + page.getId() + "'>");
        for (View view : page.getViews()) {
            sb.append(view.render(AppContextHolder.getContext(), request, response));
        }
        sb.append(Tag.DIV_END);
        // 加载JS
        sb.append(Tag.SCRIPT_START);
        sb.append("var WebApp = WebApp || {};");
        sb.append("WebApp.pageDefinition = " + page.getDefinitionJson() + ";");
        sb.append(Tag.SCRIPT_END);
        sb.append(Tag.BODY_END);

        sb.append(Tag.HTML_END);
        return sb.toString();
    }

}
