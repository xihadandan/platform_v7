/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.bean;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppProduct;

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
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
public class AppProductBean extends AppProduct {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1469340749176L;

    private List<Long> tagUuids = Lists.newArrayList();

    private TreeNode piTreeNode;

    /**
     * @return the piTreeNode
     */
    public TreeNode getPiTreeNode() {
        return piTreeNode;
    }

    /**
     * @param piTreeNode 要设置的piTreeNode
     */
    public void setPiTreeNode(TreeNode piTreeNode) {
        this.piTreeNode = piTreeNode;
    }

    public List<Long> getTagUuids() {
        return tagUuids;
    }

    public void setTagUuids(List<Long> tagUuids) {
        this.tagUuids = tagUuids;
    }
}
