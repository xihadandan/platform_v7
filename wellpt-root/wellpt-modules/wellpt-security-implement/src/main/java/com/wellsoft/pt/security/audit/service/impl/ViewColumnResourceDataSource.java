/*
 * @(#)2015年9月24日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.dyview.entity.ColumnDefinition;
import com.wellsoft.pt.basicdata.dyview.entity.ViewDefinition;
import com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 视图列资源
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月24日.1	zhulh		2015年9月24日		Create
 * </pre>
 * @date 2015年9月24日
 */
// @Component
public class ViewColumnResourceDataSource extends AbstractResourceDataSource {

    @Autowired
    private ViewDefinitionService viewDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getName()
     */
    @Override
    public String getName() {
        return "视图列资源";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getId()
     */
    @Override
    public String getId() {
        return "VIEW_COLUMN";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getOrder()
     */
    @Override
    public int getOrder() {
        return 3;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getData()
     */
    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        List<ViewDefinition> viewDefinitions = viewDefinitionService.searchViewDefinition();
        TreeNode treeNode = new TreeNode();
        treeNode.setName("视图资源");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        buildViewResourceTree(treeNode, viewDefinitions);
        return treeNode.getChildren();
    }

    /**
     * 构建视图列的资源树
     *
     * @param treeNode
     * @param resources
     * @param checkedResources
     */
    private void buildViewResourceTree(TreeNode treeNode, List<ViewDefinition> viewDefinitions) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (ViewDefinition viewDefinition : viewDefinitions) {
            List<TreeNode> children1 = new ArrayList<TreeNode>();
            TreeNode child = new TreeNode();
            child.setId(viewDefinition.getUuid());
            child.setName(viewDefinition.getViewName());
            children.add(child);

            Set<ColumnDefinition> columnDefinition1s = viewDefinition.getColumnDefinitions();
            for (ColumnDefinition columnDefinition1 : columnDefinition1s) {
                TreeNode child1 = new TreeNode();
                child1.setId(columnDefinition1.getUuid());
                child1.setName(columnDefinition1.getTitleName());
                children1.add(child1);
            }
            child.setChildren(children1);
        }
        treeNode.setChildren(children);
    }

}
