/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.visitor;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;

/**
 * Description: 生成前台要显示的树结点
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class TreeNodeVisitor implements Visitor {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.visitor.Visitor#visit(com.wellsoft.pt.basicdata.iexport.acceptor.Acceptor)
     */
    @Override
    public void visit(IexportData iexportData) {
        System.out.println(iexportData.getName());
    }

    public TreeNode getTreeNode() {
        return new TreeNode();
    }

}
