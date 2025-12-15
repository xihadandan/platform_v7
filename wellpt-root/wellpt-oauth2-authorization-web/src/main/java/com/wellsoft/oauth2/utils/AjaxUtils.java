package com.wellsoft.oauth2.utils;

import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * The Class AjaxUtils.
 */

public class AjaxUtils {
    // 状态码
    /**
     * The Constant STATUS_CODE_SUCCESS.
     */
    public final static int STATUS_CODE_SUCCESS = 200;

    /**
     * The Constant STATUS_CODE_FAILURE.
     */
    public final static int STATUS_CODE_FAILURE = 500;

    /**
     * The Constant STATUS_CODE_TIMEOUT.
     */
    public final static int STATUS_CODE_TIMEOUT = 301;

    /**
     * The Constant STATUS_CODE_FORBIDDEN.
     */
    public final static int STATUS_CODE_FORBIDDEN = 403;

    /**
     * The status code.
     */
    private int statusCode = STATUS_CODE_SUCCESS;

    /**
     * The message.
     */
    private String message = "";

    /**
     * The params.
     */
    private Object params;

    public AjaxUtils() {
    }

    public AjaxUtils(String message) {
        this.message = message;
    }

    public AjaxUtils(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public AjaxUtils(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * New ok.
     *
     * @param message the message
     * @return the ajax utils
     */
    public static AjaxUtils newOk(Object message) {
        return new AjaxUtils(STATUS_CODE_SUCCESS, message.toString());
    }

    /**
     * New ok.
     *
     * @return the ajax utils
     */
    public static AjaxUtils newOk() {
        return new AjaxUtils(STATUS_CODE_SUCCESS, "");
    }

    /**
     * New error.
     *
     * @param message the message
     * @return the ajax utils
     */
    public static AjaxUtils newError(String message) {
        return new AjaxUtils(STATUS_CODE_FAILURE, message);
    }

    /**
     * New timeout.
     *
     * @param message the message
     * @return the ajax utils
     */
    public static AjaxUtils newTimeout(String message) {
        return new AjaxUtils(STATUS_CODE_TIMEOUT, message);
    }

    /**
     * New forbidden.
     *
     * @param message the message
     * @return the ajax utils
     */
    public static AjaxUtils newForbidden(String message) {
        return new AjaxUtils(STATUS_CODE_TIMEOUT, message);
    }

    /**
     * Checks if is ajax request.
     *
     * @param request the request
     * @return true, if is ajax request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request == null)
            return false;
        return ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }

    /**
     * Gets the status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode the status code
     * @return the ajax utils
     */
    public AjaxUtils setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message
     * @return the ajax utils
     */
    public AjaxUtils setMessage(String message) {
        this.message = message;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Map<String, Object> p = Maps.newHashMap();
        p.put("statusCode", getStatusCode());
        p.put("message", getMessage());
        p.put("ortherParams", getParams());
        return JsonMapper.nonEmptyMapper().toJson(p);
    }

    /**
     * String.
     *
     * @return the string
     */
    public String string() {
        return toString();
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public Object getParams() {
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the params
     * @return the ajax utils
     */
    public AjaxUtils setParams(Object params) {
        this.params = params;
        return this;
    }

}
