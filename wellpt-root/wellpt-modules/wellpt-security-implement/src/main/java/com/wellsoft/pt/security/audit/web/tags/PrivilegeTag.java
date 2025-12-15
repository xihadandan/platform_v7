/*
 * @(#)2013-1-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web.tags;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.access.intercept.MultiTenantFilterInvocationSecurityMetadataSource;
import com.wellsoft.pt.security.audit.support.ResourceType;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Collection;

/**
 * Description: 按钮权限控制tag
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-25.1	zhulh		2013-1-25		Create
 * </pre>
 * @date 2013-1-25
 */
public class PrivilegeTag extends TagSupport {
    private static final long serialVersionUID = -6973956323615946216L;

    private static final Logger LOG = LoggerFactory.getLogger(PrivilegeTag.class);

    private String ifGranted;
    private String ifNotGranted;
    private String ifAllNotGranted;
    private String ifExsitOneGranted;//存在一个授权即可

    public static final boolean isGranted(String authority) {
        if (StringUtils.isBlank(authority)) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        // 菜单URL
        if (authority.startsWith(ResourceType.URL)) {
            return isGrantedUrl(authority);
        } else if (authority.startsWith(ResourceType.BUTTON)) {
            return ButtonPrivilegeTag.isGranted(authority);
        } else if (authority.startsWith(ResourceType.METHOD)) {
            return isGrantedMethod(authority);
        } else if (authority.startsWith(Separator.SLASH.getValue())) {// url 判断
            MultiTenantFilterInvocationSecurityMetadataSource securityMetadataSource = ApplicationContextHolder
                    .getBean(MultiTenantFilterInvocationSecurityMetadataSource.class);
            Collection<ConfigAttribute> attributes = securityMetadataSource.getRequestURLAttributes(authority);
            if (attributes == null) {
                return false;
            }
            return checkGrantedAuthority(authentication, attributes);

        }
        return isGrantedUrl(authority);
    }

    /**
     * @param authority
     * @return
     */
    public static boolean isGrantedUrl(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        SecurityMetadataSourceService securityMetadataSourceService = ApplicationContextHolder
                .getBean(SecurityMetadataSourceService.class);
        Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(
                authority,
                com.wellsoft.pt.security.enums.ResourceType.MENU.getValue());
        if (configAttributes == null) {
            return false;
        }

        return checkGrantedAuthority(authentication, configAttributes);
    }

    /**
     * @param authority
     * @return
     */
    public static boolean isGrantedButton(String authority) {
        return ButtonPrivilegeTag.isGranted(authority);
    }

    /**
     * @param authority
     * @return
     */
    public static boolean isGrantedMethod(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        SecurityMetadataSourceService securityMetadataSourceService = ApplicationContextHolder
                .getBean(SecurityMetadataSourceService.class);
        Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(
                authority,
                com.wellsoft.pt.security.enums.ResourceType.METHOD.getValue());
        if (configAttributes == null) {
            return false;
        }

        return checkGrantedAuthority(authentication, configAttributes);
    }

    /**
     * @param authentication
     * @param configAttributes
     * @return
     */
    private static boolean checkGrantedAuthority(Authentication authentication,
                                                 Collection<ConfigAttribute> configAttributes) {
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
     * @return the ifGranted
     */
    public String getIfGranted() {
        return ifGranted;
    }

    /**
     * @param ifGranted 要设置的ifGranted
     */
    public void setIfGranted(String ifGranted) {
        this.ifGranted = ifGranted;
    }

    /**
     * @return the ifNotGranted
     */
    public String getIfNotGranted() {
        return ifNotGranted;
    }

    /**
     * @param ifNotGranted 要设置的ifNotGranted
     */
    public void setIfNotGranted(String ifNotGranted) {
        this.ifNotGranted = ifNotGranted;
    }

    /**
     * @return the ifAllNotGranted
     */
    public String getIfAllNotGranted() {
        return ifAllNotGranted;
    }

    /**
     * @param ifAllNotGranted 要设置的ifAllNotGranted
     */
    public void setIfAllNotGranted(String ifAllNotGranted) {
        this.ifAllNotGranted = ifAllNotGranted;
    }

    public String getIfExsitOneGranted() {
        return ifExsitOneGranted;
    }

    public void setIfExsitOneGranted(String ifExsitOneGranted) {
        this.ifExsitOneGranted = ifExsitOneGranted;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        if (ifGranted != null && isGranted(ifGranted)) {
            return EVAL_BODY_INCLUDE;
        }

        if (ifNotGranted != null && isNotGranted(ifNotGranted)) {
            return EVAL_BODY_INCLUDE;
        }

        if (ifAllNotGranted != null && isAllNotGranted(ifAllNotGranted)) {
            return EVAL_BODY_INCLUDE;
        }

        if (ifExsitOneGranted != null && ifExsitOneGranted(ifExsitOneGranted)) {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }

    private boolean ifExsitOneGranted(String ifExsitOneGranted) throws JspException {
        String[] strings = ifExsitOneGranted.split(",");
        for (int index = 0; index < strings.length; index++) {
            if (isGranted(strings[index])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param ifAllNotGranted1
     * @return
     * @throws JspException
     */
    private boolean isAllNotGranted(String ifAllNotGranted1) throws JspException {
        String[] strings = ifAllNotGranted1.split(",");
        for (int index = 0; index < strings.length; index++) {
            boolean granted = isNotGranted(strings[index]);
            if (granted == false)
                return false;
        }
        return true;
    }

    /**
     * @param ifNotGranted2
     * @return
     * @throws JspException
     */
    private boolean isNotGranted(String ifNotGranted2) throws JspException {
        return !isGranted(ifNotGranted2);
    }
}
