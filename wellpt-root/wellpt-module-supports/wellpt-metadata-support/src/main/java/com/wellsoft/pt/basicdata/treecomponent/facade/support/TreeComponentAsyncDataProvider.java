/*
 * @(#)6 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.wellsoft.context.component.tree.TreeNode;

import java.util.List;

/**
 * Description: 树控件的异步加载子节点接口。
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6 Mar 2017.1	Xiem		6 Mar 2017		Create
 * </pre>
 * @date 6 Mar 2017
 */
public interface TreeComponentAsyncDataProvider extends TreeComponentDataProvider {
    List<TreeNode> asyncLoadTreeData(TreeComponentRequestParam params);

    List<TreeNode> search(TreeComponentRequestParam params);
}
