/*
 * @(#)2013-12-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.web;

import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
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
 * 2013-12-15.1	zhulh		2013-12-15		Create
 * </pre>
 * @date 2013-12-15
 */
public class AjaxUtils {
    public static final String X_REQUESTED_WIDTH = "X-Requested-With";

    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    public static final String AJAX_UPLOAD = "ajaxUpload";

    private AjaxUtils() {
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader(X_REQUESTED_WIDTH);
        return requestedWith != null ? XML_HTTP_REQUEST.equals(requestedWith) : false;
    }

    public static boolean isAjaxRequest(WebRequest webRequest) {
        String requestedWith = webRequest.getHeader(X_REQUESTED_WIDTH);
        return requestedWith != null ? XML_HTTP_REQUEST.equals(requestedWith) : false;
    }

    public static boolean isAjaxUploadRequest(WebRequest webRequest) {
        return webRequest.getParameter(AJAX_UPLOAD) != null;
    }

    public static boolean isAjaxRequest(SavedRequest savedRequest) {
        List<String> requestedWiths = savedRequest.getHeaderValues(X_REQUESTED_WIDTH);
        if (requestedWiths != null && !requestedWiths.isEmpty()) {
            for (String requestedWith : requestedWiths) {
                return requestedWith != null ? XML_HTTP_REQUEST.equals(requestedWith) : false;
            }
        }
        return false;
    }
}
