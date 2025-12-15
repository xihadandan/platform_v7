/*
 * @(#)6 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.util.CollectionUtils;

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
 * 修改后版本	修改人		 修改日期			修改内容
 * 6 Mar 2017.1	zyguo		6 Mar 2017		Create
 * </pre>
 * @date 6 Mar 2017
 */
public abstract class AbstractTreeComponentDataProvider implements TreeComponentDataProvider {
    public List<TreeNode> loadTreeData(TreeComponentRequestParam params) {
        List<TreeNode> list = this.loadAllTreeData(params);
        if (!CollectionUtils.isEmpty(list)) {
            ArrayList<TreeNodeConfig> configs = params.getTreeNodeConfigs();
            ArrayList<String> notShowTypes = getNotShowTypeList(configs);
            if (CollectionUtils.isEmpty(notShowTypes)) {
                // 没有需要隐藏的节点，直接返回
                return list;
            } else {
                return removeNotShowNode(list, notShowTypes);
            }
        }
        return list;
    }

    // 加载全部的数据
    protected abstract List<TreeNode> loadAllTreeData(TreeComponentRequestParam params);

    // 将不需要展示的节点数据
    private List<TreeNode> removeNotShowNode(List<TreeNode> treeList, ArrayList<String> notShowTypes) {
        List<TreeNode> newList = new ArrayList<TreeNode>();
        for (TreeNode node : treeList) {
            TreeNode rootNode = new TreeNode();
            BeanUtils.copyProperties(node, rootNode, new String[]{"children"});
            // 根节点不能移除，所以从第二级节点开始算
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                for (TreeNode childNode : node.getChildren()) {
                    removeNotShowNodeFromTreeNode(rootNode, childNode, notShowTypes);
                }
            }
            newList.add(rootNode);
        }
        return newList;
    }

    private void removeNotShowNodeFromTreeNode(TreeNode parent, TreeNode node, ArrayList<String> notShowTypes) {
        String nodeType = node.getType();
        if (notShowTypes.contains(nodeType)) { // 属于需要隐藏的节点,跳过该节点，直接检查node的子节点
            // 所有的子节点的parent也变更成了参数中的parent，不再试原来的node
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                for (TreeNode child : node.getChildren()) {
                    removeNotShowNodeFromTreeNode(parent, child, notShowTypes);
                }
            }
        } else {
            // 保持节点位置不变
            TreeNode cloneNode = new TreeNode();
            BeanUtils.copyProperties(node, cloneNode, new String[]{"children"});
            parent.getChildren().add(cloneNode);
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                for (TreeNode child : node.getChildren()) {
                    removeNotShowNodeFromTreeNode(cloneNode, child, notShowTypes);
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param configs
     * @return
     */
    private ArrayList<String> getNotShowTypeList(ArrayList<TreeNodeConfig> configs) {
        ArrayList<String> list = new ArrayList<String>();
        for (TreeNodeConfig treeNodeConfig : configs) {
            if (treeNodeConfig.getIsShow() == null || treeNodeConfig.getIsShow() == 0) {
                list.add(treeNodeConfig.getType());
            }
        }
        return list;
    }
}
