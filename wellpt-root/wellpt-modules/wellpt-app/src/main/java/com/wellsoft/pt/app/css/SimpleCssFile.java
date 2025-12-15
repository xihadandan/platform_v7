/*
 * @(#)2016年5月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.css;

import com.wellsoft.context.version.Version;
import io.swagger.annotations.ApiModelProperty;

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
public class SimpleCssFile implements CssFile {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6182676590722547819L;

    // CSS唯一标识
    private String id;
    // CSS名称
    private String name;
    // CSS文件路径
    private String path;
    @ApiModelProperty("CSS加载的循序")
    private int order;

    /**
     *
     */
    public SimpleCssFile() {
        super();
    }

    /**
     * @param path
     * @param order
     */
    public SimpleCssFile(String path, int order) {
        this.path = path;
        this.order = order;
    }

    /**
     * @param id
     * @param path
     */
    public SimpleCssFile(String id, String path) {
        this.id = id;
        this.path = path;
    }

    /**
     * @param id
     * @param name
     * @param path
     * @param order
     */
    public SimpleCssFile(String id, String name, String path, int order) {
        this.id = id;
        this.name = name;
        this.path = path;
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
     * @return the path
     */
    public String getPath() {
        if (path.endsWith(".css")) {
            return path + "?v=" + Version.getRuntimeVersion();
        }
        return path + ".css" + "?v=" + Version.getRuntimeVersion();
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the order
     */
    @ApiModelProperty("CSS加载的循序")
    public int getOrder() {
        return order;
    }

    /**
     * @param order 要设置的order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleCssFile other = (SimpleCssFile) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
