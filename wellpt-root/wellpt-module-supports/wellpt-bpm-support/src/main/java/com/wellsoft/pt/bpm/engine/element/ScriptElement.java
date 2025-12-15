/*
 * @(#)2018年8月30日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月30日.1	zhulh		2018年8月30日		Create
 * </pre>
 * @date 2018年8月30日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptElement implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3172084062649250817L;

    // 脚本切入点
    private String pointcut;

    // 脚本类型
    private String type;

    // 脚本内容类型1、脚本定义；2、自定义
    private String contentType;

    // 脚本内容
    private String content;

    /**
     * @return the pointcut
     */
    public String getPointcut() {
        return pointcut;
    }

    /**
     * @param pointcut 要设置的pointcut
     */
    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

}
