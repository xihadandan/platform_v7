/*
 * @(#)2016年6月21日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.theme;

import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import io.swagger.annotations.ApiModelProperty;

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
 * 2016年6月21日.1	zhulh		2016年6月21日		Create
 * </pre>
 * @date 2016年6月21日
 */
public class SimpleTheme implements Theme {
    public final static String DEFAULT_THEME_ID = "default";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6831436018282780754L;
    private String id;

    private String name;

    private List<CssFile> cssFiles;

    private List<JavaScriptModule> javaScriptModules;
    @ApiModelProperty("排序号")
    private int order;

    public SimpleTheme() {
        super();
    }

    public SimpleTheme(String id, String name, List<CssFile> cssFiles, List<JavaScriptModule> javaScriptModules,
                       int order) {
        super();
        this.id = id;
        this.name = name;
        this.cssFiles = cssFiles;
        this.javaScriptModules = javaScriptModules;
        this.order = order;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cssFiles
     */
    public List<CssFile> getCssFiles() {
        return cssFiles;
    }

    /**
     * @param cssFiles 要设置的cssFiles
     */
    public void setCssFiles(List<CssFile> cssFiles) {
        this.cssFiles = cssFiles;
    }

    /**
     * @return the javaScriptModules
     */
    public List<JavaScriptModule> getJavaScriptModules() {
        return javaScriptModules;
    }

    /**
     * @param javaScriptModules 要设置的javaScriptModules
     */
    public void setJavaScriptModules(List<JavaScriptModule> javaScriptModules) {
        this.javaScriptModules = javaScriptModules;
    }

    /**
     * @return the order
     */
    @ApiModelProperty("排序号")
    public int getOrder() {
        return order;
    }

    /**
     * @param order 要设置的order
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
