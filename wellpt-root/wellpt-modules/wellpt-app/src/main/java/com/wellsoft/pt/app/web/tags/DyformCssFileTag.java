/*
 * @(#)2016-10-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.support.CssHelper;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
public class DyformCssFileTag extends DyformTagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4044596213953421592L;

    // 表单定义UUID
    private String formUuid;

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
            ArrayList<CssFile> cssFiles = new ArrayList<CssFile>();
            // 平台基础
            cssFiles.addAll(CssHelper.getWebAppBaseCssFiles());
            // 表单样式
            cssFiles.addAll(getDyformCssFiles());
            // 附加样式
            cssFiles.addAll(getExtraModuleCssFiles());
            // 主题样式
            Theme theme = getTheme();
            if (theme != null) {
                cssFiles.addAll(theme.getCssFiles());
            }

            String cssImport = CssHelper.getCssImport((HttpServletRequest) pageContext.getRequest(),
                    cssFiles);

            pageContext.getOut().write(cssImport);
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return SKIP_BODY;
    }

    /**
     * @return
     */
    private Collection<CssFile> getDyformCssFiles() {
        return getDyformCssFiles(null);
    }

    /**
     * @return
     */
    private Collection<? extends CssFile> getExtraModuleCssFiles() {
        if (StringUtils.isBlank(extraModules)) {
            return Collections.emptyList();
        }

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        String[] cssModules = StringUtils.split(extraModules, Separator.COMMA.getValue());
        for (String cssModule : cssModules) {
            cssFiles.add(AppContextHolder.getContext().getCssFile(StringUtils.trim(cssModule)));
        }
        return cssFiles;
    }
}
