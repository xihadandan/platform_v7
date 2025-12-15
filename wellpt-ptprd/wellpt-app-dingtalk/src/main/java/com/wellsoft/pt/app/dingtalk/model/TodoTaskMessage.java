/*
 * @(#)4/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.model;

import com.wellsoft.context.base.BaseObject;

import java.util.LinkedHashMap;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/25/25.1	    zhulh		4/25/25		    Create
 * </pre>
 * @date 4/25/25
 */
public class TodoTaskMessage extends BaseObject {
    private static final long serialVersionUID = 3419720296998922131L;

    private String subject;

    private String appUrl;

    private String pcUrl;

    private LinkedHashMap<String, Object> contentFieldList;

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the appUrl
     */
    public String getAppUrl() {
        return appUrl;
    }

    /**
     * @param appUrl 要设置的appUrl
     */
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    /**
     * @return the pcUrl
     */
    public String getPcUrl() {
        return pcUrl;
    }

    /**
     * @param pcUrl 要设置的pcUrl
     */
    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    /**
     * @return the contentFieldList
     */
    public LinkedHashMap<String, Object> getContentFieldList() {
        return contentFieldList;
    }

    /**
     * @param contentFieldList 要设置的contentFieldList
     */
    public void setContentFieldList(LinkedHashMap<String, Object> contentFieldList) {
        this.contentFieldList = contentFieldList;
    }
}
