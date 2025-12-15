/*
 * @(#)2021年7月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.support;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月1日.1	zhongzh		2021年7月1日		Create
 * </pre>
 * @date 2021年7月1日
 */
public class AllowXFrameOptionsHeaderWriter extends RuleXFrameOptionsHeaderWriter {

    private static final String DEFAULT_ORIGIN_REFERER = "Referer";

    private static final String DEFAULT_ORIGIN_REQUEST_PARAMETER = "x-frames-allow-from";

    private final Collection<String> allowed;

    private String allowFromParameterName = DEFAULT_ORIGIN_REQUEST_PARAMETER;

    /**
     * @param allowed
     */
    public AllowXFrameOptionsHeaderWriter(Collection<String> allowed) {
        this(XFrameOptionsMode.SAMEORIGIN, allowed);
    }

    /**
     * @param frameOptionsMode
     * @param allowed
     */
    public AllowXFrameOptionsHeaderWriter(XFrameOptionsMode frameOptionsMode, Collection<String> allowed) {
        super(frameOptionsMode);
        this.allowed = allowed;
    }

    /**
     *
     */
    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        String allowFromOrigin = request.getParameter(allowFromParameterName);
        if (StringUtils.isBlank(allowFromOrigin)) {
            allowFromOrigin = request.getHeader(DEFAULT_ORIGIN_REFERER);
        }
        if (StringUtils.isNotBlank(allowFromOrigin) && allowed.contains(allowFromOrigin)) {
            response.setHeader(XFRAME_OPTIONS_HEADER, XFrameOptionsMode.ALLOW_FROM.getMode() + " " + allowFromOrigin);
        } else {
            super.writeHeaders(request, response);
        }
    }

    public void setAllowFromParameterName(String allowFromParameterName) {
        this.allowFromParameterName = allowFromParameterName;
    }

}
