/*
 * @(#)27 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractLeftSidebarDataProvider extends AbstractAsyncTreeComponentDataProvider {

    @Autowired
    protected SecurityApiFacade securityApiFacade;

    @Override
    public List<TreeType> getNodeTypes() {
        List<TreeType> types = new ArrayList<TreeType>();
        return types;
    }

    public List<TreeNode> loadTreeData(Map<String, String> otherParams) {
        List<TreeNode> treeNodes = null;
        return treeNodes;
    }

    @Override
    public List<TreeNode> asyncLoadTreeData(TreeComponentRequestParam params) {
        List<TreeNode> treeNodes = null;
        return treeNodes;
    }

    @Override
    public List<TreeNode> search(TreeComponentRequestParam params) {
        List<TreeNode> treeNodes = null;
        return treeNodes;
    }
}
