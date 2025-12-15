/*
 * @(#)2019年1月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.tree;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 列表数据构建树工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年1月9日.1	zhulh		2019年1月9日		Create
 * </pre>
 * @date 2019年1月9日
 */
public class TreeUtils {

    /**
     * 根据列表数据构建树结点
     *
     * @param dataList
     * @param idKey
     * @param parentKey
     * @param function
     * @return
     */
    public static <DATA extends Object> TreeNode buildTree(List<DATA> dataList, String idKey, final String parentKey,
                                                           Function<DATA, TreeNode> function) {
        return buildTree(null, dataList, idKey, parentKey, function);
    }

    /**
     * 根据列表数据构建树结点
     *
     * @param dataList
     * @param idKey
     * @param parentKey
     * @param function
     * @param defaultNodeId
     * @param defaultNodeName
     * @return
     */
    public static <DATA extends Object> TreeNode buildTree(List<DATA> dataList, String idKey, final String parentKey,
                                                           Function<DATA, TreeNode> function, String defaultNodeId, String defaultNodeName) {
        return buildTree(TreeNode.ROOT_ID, "根结点", dataList, idKey, parentKey, function);
    }

    /**
     * 根据列表数据构建树结点
     *
     * @param rootNodeId
     * @param dataList
     * @param idKey
     * @param parentKey
     * @param function
     * @return
     */
    public static <DATA extends Object> TreeNode buildTree(String rootNodeId, List<DATA> dataList, String idKey,
                                                           final String parentKey, Function<DATA, TreeNode> function) {
        return buildTree(rootNodeId, null, dataList, idKey, parentKey, function);
    }

    /**
     * 根据列表数据构建树结点
     *
     * @param rootNodeId
     * @param rootNodeName
     * @param dataList
     * @param idKey
     * @param parentKey
     * @param function
     * @return
     */
    public static <DATA extends Object> TreeNode buildTree(String rootNodeId, String rootNodeName, List<DATA> dataList,
                                                           String idKey, final String parentKey, Function<DATA, TreeNode> function) {
        final Map<DATA, Object> dataIdMap = Maps.newHashMap();
        final Map<DATA, Object> dataParentMap = Maps.newHashMap();
        ImmutableListMultimap<Object, DATA> immutableListMultimap = Multimaps.index(dataList.iterator(),
                new Function<DATA, Object>() {

                    @Override
                    public Object apply(DATA input) {
                        Object parent = ReflectionUtils.invokeGetterMethod(input, parentKey);
                        dataParentMap.put(input, parent);
                        if (parent == null || StringUtils.isBlank(ObjectUtils.toString(parent))) {
                            return TreeNode.ROOT_ID;
                        }
                        return parent;
                    }

                });
        // 获取根结点
        List<DATA> rootDataList = Lists.newArrayList();
        if (StringUtils.isNotBlank(rootNodeId)) {
            for (DATA data : dataList) {
                Object id = ReflectionUtils.invokeGetterMethod(data, idKey);
                dataIdMap.put(data, id);
                if (rootNodeId.equals(id)) {
                    rootDataList.add(data);
                }
            }
        } else {
            Map<Object, DATA> idDataMap = Maps.newLinkedHashMap();
            for (DATA data : dataList) {
                Object id = ReflectionUtils.invokeGetterMethod(data, idKey);
                idDataMap.put(id, data);
            }
            for (Entry<Object, DATA> entry : idDataMap.entrySet()) {
                Object id = entry.getKey();
                DATA data = entry.getValue();
                dataIdMap.put(data, id);
                Object parent = dataParentMap.get(data);
                // 存在上级主键标识但数据不存在的作为根结点
                if (!immutableListMultimap.containsKey(parent) || !idDataMap.containsKey(parent)) {
                    rootDataList.add(data);
                }
            }
        }
        List<TreeNode> rootNodes = Lists.newArrayList();
        // 根据根结点构建树
        for (DATA rootData : rootDataList) {
            TreeNode rootNode = function.apply(rootData);
            buildChildren(rootNode, rootData, dataIdMap, immutableListMultimap, idKey, function);
            rootNodes.add(rootNode);
        }
        TreeNode rootNode = null;
        if (rootNodes.size() == 1) {
            rootNode = rootNodes.get(0);
            if (StringUtils.isNotBlank(rootNodeName)) {
                rootNode.setName(rootNodeName);
            }
            return rootNode;
        }
        rootNode = new TreeNode();
        rootNode.setId(rootNodeId);
        rootNode.setName(rootNodeName);
        rootNode.setChildren(rootNodes);
        return rootNode;
    }

    /**
     * 生成子结点
     *
     * @param rootNode
     * @param rootData
     * @param dataIdMap
     * @param immutableListMultimap
     * @param idKey
     * @param function
     */
    static <DATA extends Object> void buildChildren(TreeNode rootNode, DATA rootData, Map<DATA, Object> dataIdMap,
                                                    ImmutableListMultimap<Object, DATA> immutableListMultimap, String idKey, Function<DATA, TreeNode> function) {
        List<TreeNode> children = Lists.newArrayList();
        Object id = dataIdMap.get(rootData);
        ImmutableList<DATA> childDataList = immutableListMultimap.get(id);
        for (DATA childData : childDataList) {
            TreeNode childNode = function.apply(childData);
            children.add(childNode);

            // 递归生成子结点
            buildChildren(childNode, childData, dataIdMap, immutableListMultimap, idKey, function);
        }
        rootNode.setChildren(children);
    }

    /**
     * 获取指定ID的树结点
     *
     * @param treeNode
     * @param id
     * @return
     */
    public static TreeNode getTreeNode(TreeNode treeNode, String id) {
        if (StringUtils.equals(treeNode.getId(), id)) {
            return treeNode;
        }
        List<TreeNode> children = treeNode.getChildren();
        return getTreeNode(children, id);
    }

    /**
     * 获取指定ID的树结点
     *
     * @param treeNodes
     * @param id
     * @return
     */
    public static TreeNode getTreeNode(List<TreeNode> treeNodes, String id) {
        for (TreeNode treeNode : treeNodes) {
            TreeNode searchTreeNode = getTreeNode(treeNode, id);
            if (searchTreeNode != null) {
                return searchTreeNode;
            }
        }
        return null;
    }

    /**
     * 遍历树
     *
     * @param treeNode
     */
    public static void traverseTree(TreeNode treeNode, Function<TreeNode, Void> function) {
        function.apply(treeNode);
        List<TreeNode> children = treeNode.getChildren();
        for (TreeNode child : children) {
            traverseTree(child, function);
        }
    }

    /**
     * 遍历树
     *
     * @param treeNode
     */
    public static void traverseTree(List<TreeNode> treeNodes, Function<TreeNode, Void> function) {
        if (CollectionUtils.isEmpty(treeNodes)) {
            return;
        }
        for (TreeNode treeNode : treeNodes) {
            traverseTree(treeNode, function);
        }
    }

    /**
     * 删除树结点
     *
     * @param treeNode
     * @param toRemoveNode
     */
    public static void removeTreeNode(TreeNode treeNode, final TreeNode toRemoveNode) {
        traverseTree(treeNode, new Function<TreeNode, Void>() {

            @Override
            public Void apply(TreeNode input) {
                List<TreeNode> children = input.getChildren();
                if (children.contains(toRemoveNode)) {
                    children.remove(toRemoveNode);
                }
                return null;
            }
        });

    }

    /**
     * 删除树结点
     *
     * @param treeNode
     * @param toRemoveNodes
     */
    public static void removeTreeNodes(TreeNode treeNode, List<TreeNode> toRemoveNodes) {
        for (TreeNode toRemoveNode : toRemoveNodes) {
            removeTreeNode(treeNode, toRemoveNode);
        }
    }

}
