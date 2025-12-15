/*
 * @(#)28 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

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
public class TreeType {
    private String type;
    private String name;

    public TreeType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static TreeType createTreeType(String type, String name) {
        return new TreeType(type, name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
    }

}
