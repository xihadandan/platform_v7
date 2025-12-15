/*
 * @(#)27 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.wellsoft.context.component.tree.TreeNode;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 27 Feb 2017.1	Xiem		27 Feb 2017		Create
 * </pre>
 * @date 27 Feb 2017
 */
public interface TreeComponentDataProvider {
    /**
     * 获取节点类型
     *
     * @return
     */
    List<TreeType> getNodeTypes();

    /**
     * 名称
     *
     * @return
     */
    String getName();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<TreeNode> loadTreeData(TreeComponentRequestParam reqParams);

    /**
     * 返回过滤参数使用说明
     *
     * @return
     */
    String getFilterHint();

}
