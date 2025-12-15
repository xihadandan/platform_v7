/*
 * @(#)4/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.model;

import com.wellsoft.context.base.BaseObject;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/24/25.1	    zhulh		4/24/25		    Create
 * </pre>
 * @date 4/24/25
 */
public class ActionCardMessage extends BaseObject {
    private static final long serialVersionUID = -8010959310595055175L;

    private String subject;

    private String markdown;

    private String singleTitle;

    private String singleUrl;

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
     * @return the markdown
     */
    public String getMarkdown() {
        return markdown;
    }

    /**
     * @param markdown 要设置的markdown
     */
    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    /**
     * @return the singleTitle
     */
    public String getSingleTitle() {
        return singleTitle;
    }

    /**
     * @param singleTitle 要设置的singleTitle
     */
    public void setSingleTitle(String singleTitle) {
        this.singleTitle = singleTitle;
    }

    /**
     * @return the singleUrl
     */
    public String getSingleUrl() {
        return singleUrl;
    }

    /**
     * @param singleUrl 要设置的singleUrl
     */
    public void setSingleUrl(String singleUrl) {
        this.singleUrl = singleUrl;
    }

}
