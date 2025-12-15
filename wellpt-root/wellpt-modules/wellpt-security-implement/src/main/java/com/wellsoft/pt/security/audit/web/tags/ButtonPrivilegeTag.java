/*
 * @(#)2013-2-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web.tags;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.tags.form.ButtonTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-4.1	zhulh		2013-2-4		Create
 * </pre>
 * @date 2013-2-4
 */
public class ButtonPrivilegeTag extends ButtonTag {

    private static final long serialVersionUID = -7460202391087473464L;

    private String authority;

    public static final boolean isGranted(String btnAuthority) {
        Authentication authentication = SpringSecurityUtils.getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (SpringSecurityUtils.getCurrentUser() instanceof SuperAdminDetails) {
            return true;
        }

        SecurityMetadataSourceService securityMetadataSourceService = ApplicationContextHolder
                .getBean(SecurityMetadataSourceService.class);
        Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(
                btnAuthority,
                com.wellsoft.pt.security.enums.ResourceType.BUTTON.getValue());
        if (configAttributes == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (ConfigAttribute attribute : configAttributes) {
            // Attempt to find a matching granted authority
            for (GrantedAuthority auth : authorities) {
                if (attribute.getAttribute().equals(auth.getAuthority())) {
                    return true;
                }
            }
        }

        return false;
    }

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

    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        super.writeTagContent(tagWriter);
        try {
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                    .getBean(SecurityAuditFacadeService.class);
            Resource resource = securityAuditFacadeService.getResourceByCode(authority);
            // 设置按钮名称
            if (resource != null) {
                pageContext.getOut().write(resource.getName());
            } else {
                return EVAL_BODY_INCLUDE;
            }
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return SKIP_BODY;
    }

    @Override
    protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
        super.writeDefaultAttributes(tagWriter);
        // 若无权限不可用且隐藏
        if (!isGranted(authority)) {
            tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
            // tagWriter.writeAttribute(STYLE_ATTRIBUTE, "display:none;");
        } else {
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                    .getBean(SecurityAuditFacadeService.class);
            Resource resource = securityAuditFacadeService.getButtonByCode(authority);
            // 按钮资源的target属性存放JS代码
            if (resource != null && StringUtils.isNotBlank(resource.getTarget())) {
                tagWriter.writeAttribute(ONCLICK_ATTRIBUTE, resource.getTarget());
            }
        }
    }

    /**
     * Return the default value.
     *
     * @return The default value if none supplied.
     */
    @Override
    protected String getDefaultValue() {
        return "";
    }

    /**
     * Get the value of the '<code>type</code>' attribute. Subclasses
     * can override this to change the type of '<code>input</code>' element
     * rendered. Default value is '<code>submit</code>'.
     */
    @Override
    protected String getType() {
        return "button";
    }

    @Override
    protected String resolveCssClass() throws JspException {
        return ObjectUtils.getDisplayString(evaluate("cssClass", getCssClass()));
    }

}
