/*
 * @(#)5/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.model;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/26/25.1	    zhulh		5/26/25		    Create
 * </pre>
 * @date 5/26/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextCardMessage extends BaseObject {
    private String title;
    private String description;
    private String url;
    @JsonProperty("btntxt")
    private String btnTxt;

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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 要设置的description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return the btnTxt
     */
    public String getBtnTxt() {
        return btnTxt;
    }

    /**
     * @param btnTxt 要设置的btnTxt
     */
    public void setBtnTxt(String btnTxt) {
        this.btnTxt = btnTxt;
    }
}
