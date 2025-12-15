/*
 * @(#)2016年5月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.js;

import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.support.RequireJsHelper;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月17日.1	zhulh		2016年5月17日		Create
 * </pre>
 * @date 2016年5月17日
 */
public class RequireJSJavaScriptModule implements JavaScriptModule {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8582473021833970062L;

    // JS模块ID
    private String id;
    // JS模块名称
    private String name;
    // JS文件路径
    private String path;
    // 加载的CSS文件
    private Set<CssFile> cssFiles = new HashSet<CssFile>(0);
    // 依赖的模块
    private Set<String> dependencies = new HashSet<String>(0);
    // 导出的名称
    private String exports;
    @ApiModelProperty("混淆")
    private boolean confuse;
    @ApiModelProperty("JS模块顺序")
    private int order;

    /**
     *
     */
    public RequireJSJavaScriptModule() {
        super();
    }

    /**
     * @param id
     * @param path
     */
    public RequireJSJavaScriptModule(String id, String path) {
        this.id = id;
        this.path = path;
        this.confuse = true;
    }

    /**
     * @param id
     * @param name
     * @param path
     * @param dependencies
     * @param exports
     */
    public RequireJSJavaScriptModule(String id, String name, String path, Set<CssFile> cssFiles,
                                     Set<String> dependencies, String exports) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.cssFiles = cssFiles;
        this.dependencies = dependencies;
        this.exports = exports;
        this.confuse = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequireJSJavaScriptModule that = (RequireJSJavaScriptModule) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(path, that.path) &&
                Objects.equals(exports, that.exports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, path, cssFiles, dependencies, exports, confuse, order);
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.js.JavaScriptModule#getConfusePath()
     */
    @Override
    public String getConfusePath() {
        return confuse ? RequireJsHelper.createConfusePath(path) : path;
    }

    /**
     * @return the cssFiles
     */
    public Set<CssFile> getCssFiles() {
        return cssFiles;
    }

    /**
     * @param cssFiles 要设置的cssFiles
     */
    public void setCssFiles(Set<CssFile> cssFiles) {
        this.cssFiles = cssFiles;
    }

    /**
     * @return the dependencies
     */
    public Set<String> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies 要设置的dependencies
     */
    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * @return the exports
     */
    public String getExports() {
        if (exports == null) {
            return id;
        }
        return exports;
    }

    /**
     * @param exports 要设置的exports
     */
    public void setExports(String exports) {
        this.exports = exports;
    }

    /**
     * @return the confuse
     */
    public boolean isConfuse() {
        return confuse;
    }

    /**
     * @param confuse 要设置的confuse
     */
    public void setConfuse(boolean confuse) {
        this.confuse = confuse;
    }

    /**
     * @return the order
     */
    @ApiModelProperty("JS模块顺序")
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
