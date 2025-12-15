/*
 * @(#)2013-2-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web.tags;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.service.ResourceService;
import com.wellsoft.pt.security.support.DynamicButton;
import com.wellsoft.pt.security.support.DynamicButtonInterceptor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.ArrayList;
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
 * 2013-2-19.1	zhulh		2013-2-19		Create
 * </pre>
 * @date 2013-2-19
 */
public class DynamicButtonPrivilegeTag extends TagSupport {

    private static final long serialVersionUID = 2496426734712050387L;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String authority;

    private String cssClass;

    private String interceptor;

    /**
     * @return the authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * @param authority 要设置的authority
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * @return the cssClass
     */
    public String getCssClass() {
        return cssClass;
    }

    /**
     * @param cssClass 要设置的cssClass
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * @return the interceptor
     */
    public String getInterceptor() {
        return interceptor;
    }

    /**
     * @param interceptor 要设置的interceptor
     */
    public void setInterceptor(String interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        return writeTagContent(getTagWriter());
    }

    /**
     * @param tagWriter
     * @return
     */
    private int writeTagContent(JspWriter tagWriter) {
        try {
            tagWriter.write(buildButtons());
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return SKIP_BODY;
    }

    /**
     * @return
     */
    private String buildButtons() {
        StringBuilder sb = new StringBuilder();
        // 获取模块的动态按钮
        ResourceService resourceService = ApplicationContextHolder.getBean(ResourceService.class);
        List<Resource> resources = resourceService.getDynamicButtonResourcesByCode(authority);

        // 动态按钮权限过滤
        List<DynamicButton> buttons = new ArrayList<DynamicButton>();
        for (Resource resource : resources) {
            if (ButtonPrivilegeTag.isGranted(resource.getCode())) {
                DynamicButton dynamicButton = new DynamicButton();
                dynamicButton.setCode(resource.getCode());
                dynamicButton.setName(resource.getName());
                dynamicButton.setScript(resource.getTarget());
                buttons.add(dynamicButton);
            }
        }

        // 动态按钮展示前过滤拦截
        if (StringUtils.isNotBlank(interceptor)) {
            DynamicButtonInterceptor dynamicButtonInterceptor = ApplicationContextHolder.getBean(interceptor,
                    DynamicButtonInterceptor.class);
            buttons = dynamicButtonInterceptor.prepare(buttons);
            if (buttons == null) {
                buttons = new ArrayList<DynamicButton>();
            }
        }

        // 输出动态按钮
        for (DynamicButton button : buttons) {
            // String temp =
            // "<button id='btn_add' type='button' class='btn'>新增</button>";
            String code = button.getCode();
            String name = button.getName();
            String script = button.getScript();
            sb.append("<button");
            sb.append(" ");
            sb.append("id=\"" + code + "\"");
            sb.append(" ");
            sb.append("type=\"button\"");
            sb.append(" ");
            if (StringUtils.isNotBlank(cssClass)) {
                sb.append("class=\"" + cssClass + "\"");
                sb.append(" ");
            }
            if (StringUtils.isNotBlank(script)) {
                sb.append("onclick=\"" + script + "\"");
                sb.append(" ");
            }
            sb.append(">");
            sb.append(name);
            sb.append("</button>");
            sb.append(Separator.LINE.getValue());
        }
        return sb.toString();
    }

    /**
     * @return
     */
    private JspWriter getTagWriter() {
        return this.pageContext.getOut();
    }

}
