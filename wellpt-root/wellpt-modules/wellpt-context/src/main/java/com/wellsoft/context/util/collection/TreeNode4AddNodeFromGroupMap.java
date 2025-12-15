/*
 * @(#)2017年12月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.collection;

import com.wellsoft.context.component.tree.TreeNode;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 将新的数据，挂在指定的树形结构数据下
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月27日.1	zyguo		2017年12月27日		Create
 * </pre>
 * @date 2017年12月27日
 */
public abstract class TreeNode4AddNodeFromGroupMap<T extends Serializable> {
    public TreeNode addNode(TreeNode treeNode, Map<String, List<T>> groupMap) {
        // 递归处理子节点, 必须先处理子节点，然后处理根节点，不然新加进来的数据又变成了根节点的子节点，这样是重复计算的，不合适
        digui(treeNode, groupMap);
        // 处理根节点
        // 该节点含有信息，添加子节点
        String key = getGroupMapKeyFromTreeNode(treeNode);
        if (groupMap.containsKey(key)) {
            List<TreeNode> list = createChildrenNode(groupMap.get(key), treeNode);
            treeNode.getChildren().addAll(list);
        }

        return treeNode;
    }

    // 递归将groupMap中的数据合并到对应的树位置中去
    public void digui(TreeNode treeNode, Map<String, List<T>> groupMap) {
        // 防止死循环
        if (!CollectionUtils.isEmpty(treeNode.getChildren())) {
            for (TreeNode node : treeNode.getChildren()) {
                // 必须先递归，后加节点，如果先加子节点后递归的话，新加的子节点会在递归的范围内
                digui(node, groupMap);
                // 该节点含有信息，添加子节点
                String key = getGroupMapKeyFromTreeNode(node);
                if (groupMap.containsKey(key)) {
                    List<TreeNode> list = createChildrenNode(groupMap.get(key), node);
                    node.getChildren().addAll(list);
                }

            }
        }
    }

    /**
     * 从treeNode 获取 与 groupMap 关联的 KEY 值
     *
     * @param node
     * @return
     */
    protected abstract String getGroupMapKeyFromTreeNode(TreeNode node);

    private List<TreeNode> createChildrenNode(List<T> list, TreeNode parentNode) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<TreeNode>();
        }
        ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
        for (T obj : list) {
            TreeNode node = obj2TreeNode(obj, parentNode);
            if (node != null) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    /**
     * 将对象转成TreeNode对象
     *
     * @param obj
     * @return
     */
    protected abstract TreeNode obj2TreeNode(T obj, TreeNode parentNode);

}
