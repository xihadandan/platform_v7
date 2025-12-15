/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.model;

import com.wellsoft.pt.app.feishu.support.AbstractMessage;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/21/25.1	    zhulh		3/21/25		    Create
 * </pre>
 * @date 3/21/25
 */
public class TextMessage extends AbstractMessage {
    private String subject;
    private String title;
    private String url;
    private String text;

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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    @JsonIgnore
    public String getTextMessage() {
        return this.text;
    }

}
