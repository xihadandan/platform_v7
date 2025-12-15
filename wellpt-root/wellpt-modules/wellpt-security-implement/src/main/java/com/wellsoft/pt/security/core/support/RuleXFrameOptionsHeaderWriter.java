/*
 * @(#)2021年7月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.support;

import org.springframework.security.web.header.HeaderWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class RuleXFrameOptionsHeaderWriter implements HeaderWriter {

    public static final String XFRAME_OPTIONS_HEADER = "X-Frame-Options";

    private XFrameOptionsMode frameOptionsMode = XFrameOptionsMode.SAMEORIGIN;

    /**
     * @param frameOptionsMode
     */
    public RuleXFrameOptionsHeaderWriter(XFrameOptionsMode frameOptionsMode) {
        this.frameOptionsMode = frameOptionsMode;
    }

    /**
     *
     */
    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(XFRAME_OPTIONS_HEADER, frameOptionsMode.getMode());
    }

    public void setFrameOptionsMode(XFrameOptionsMode frameOptionsMode) {
        this.frameOptionsMode = frameOptionsMode;
    }

    public enum XFrameOptionsMode {
        DENY("DENY"), SAMEORIGIN("SAMEORIGIN"), ALLOW_FROM("ALLOW-FROM"), ALLOWALL("ALLOWALL");

        private String mode;

        private XFrameOptionsMode(String mode) {
            this.mode = mode;
        }

        /**
         * Gets the mode for the X-Frame-Options header value. For example, DENY,
         * SAMEORIGIN, ALLOW-FROM. Cannot be null.
         *
         * @return the mode for the X-Frame-Options header value.
         */
        public String getMode() {
            return mode;
        }
    }

}
