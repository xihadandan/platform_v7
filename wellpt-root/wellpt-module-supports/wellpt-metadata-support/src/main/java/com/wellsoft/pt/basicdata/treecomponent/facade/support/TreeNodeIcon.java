/*
 * @(#)28 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 28 Feb 2017.1	Xiem		28 Feb 2017		Create
 * </pre>
 * @date 28 Feb 2017
 */
public class TreeNodeIcon implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8623351060800728384L;
    private String className; // 对应的是css的 class
    private String iconPath; // 对应的是url

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the iconPath
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * @param iconPath 要设置的iconPath
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

}
