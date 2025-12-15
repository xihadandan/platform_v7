/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.template.freemarker.FreeMarkerTemplateEngine;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
public class TemplateEngineFactory {

    public static TemplateEngine getDefaultTemplateEngine() {
        return ApplicationContextHolder.getBean(FreeMarkerTemplateEngine.class);
    }

    /**
     * 获取可解析的顶级数据模型，包含常用的一些参数值
     *
     * @return
     */
    public static Map<String, Object> getExplainRootModel() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sysdate", new Date());
        if (SpringSecurityUtils.getCurrentUser() == null) {
            return params;
        }
        params.put("currentUserName", SpringSecurityUtils.getCurrentUserName());
        params.put("currentLoginName", SpringSecurityUtils.getCurrentLoginName());
        String userId = SpringSecurityUtils.getCurrentUserId();
        params.put("currentUserId", userId);
        params.put("currentTenantId", SpringSecurityUtils.getCurrentTenantId());
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            params.put("currentSystem", RequestSystemContextPathResolver.system());
        }
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.pageId())) {
            params.put("currentPageId", RequestSystemContextPathResolver.pageId());
        }
        if (SpringSecurityUtils.getCurrentUser() instanceof InternetUserDetails) {
            return params;
        }
        // 当前登录单位ID
        params.put("currentUserUnitId", StringUtils.trimToEmpty(SpringSecurityUtils.getCurrentUserUnitId()));
        params.put("currentUserUnitName", StringUtils.trimToEmpty(SpringSecurityUtils.getCurrentUserUnitName()));
        params.put("currentUserDepartmentId", StringUtils.trimToEmpty(SpringSecurityUtils.getCurrentUserDepartmentId()));
        params.put("currentUserDepartmentName", StringUtils.trimToEmpty(SpringSecurityUtils.getCurrentUserDepartmentName()));
        params.put("currentUserMainBusinessUnitId", StringUtils.trimToEmpty(SpringSecurityUtils.getUserMainBusinessUnitId()));
        params.put("currentUserOtherBusinessUnitIds", SpringSecurityUtils.getUserOtherBusinessUnitIds());
        return params;
    }
}
