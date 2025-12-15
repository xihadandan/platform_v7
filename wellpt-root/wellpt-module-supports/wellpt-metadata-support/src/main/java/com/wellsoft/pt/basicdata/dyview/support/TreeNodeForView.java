/*
 * @(#)2013-4-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.support;

import com.wellsoft.context.component.tree.TreeNode;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-28.1	wubin		2013-4-28		Create
 * </pre>
 * @date 2013-4-28
 */
public class TreeNodeForView extends TreeNode {

    private String attribute; //其他属性

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute 要设置的attribute
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

}
