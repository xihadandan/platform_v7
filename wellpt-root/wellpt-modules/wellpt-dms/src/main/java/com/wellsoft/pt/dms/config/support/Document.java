/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
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
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6084697893093642805L;

    private String title;

    private boolean enableVersioning;

    // 操作处理拦截器
    private String interceptors;

    // 单据二开模块
    private String jsModule;

    private List<Button> buttons = new ArrayList<Button>(0);

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
     * @return the enableVersioning
     */
    public boolean isEnableVersioning() {
        return enableVersioning;
    }

    /**
     * @param enableVersioning 要设置的enableVersioning
     */
    public void setEnableVersioning(boolean enableVersioning) {
        this.enableVersioning = enableVersioning;
    }

    /**
     * @return the interceptors
     */
    public String getInterceptors() {
        return interceptors;
    }

    /**
     * @param interceptors 要设置的interceptors
     */
    public void setInterceptors(String interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * @return the jsModule
     */
    public String getJsModule() {
        return jsModule;
    }

    /**
     * @param jsModule 要设置的jsModule
     */
    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    /**
     * @return the buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

}
