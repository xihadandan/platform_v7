/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月18日.1	zyguo		2017年12月18日		Create
 * </pre>
 * @date 2017年12月18日
 */
@Component
public class MyTestOrgTreeDialogDataProvider implements OrgTreeDialogDataProvider {
    public static final String TYPE = "MyTest";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2121385764390354438L;
    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        TreeNode tree1 = new TreeNode();
        tree1.setName("test1");
        tree1.setId("U001");
        tree1.setType(IdPrefix.USER.getValue());

        TreeNode tree2 = new TreeNode();
        tree2.setName("test2");
        tree2.setId("U002");
        tree2.setType(IdPrefix.USER.getValue());
        tree1.getChildren().add(tree2);

        TreeNode tree3 = new TreeNode();
        tree3.setName("test3");
        tree3.setId("U003");
        tree3.setType(IdPrefix.USER.getValue());

        treeNodeList.add(tree1);
        treeNodeList.add(tree3);
        return treeNodeList;
    }
}
