/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppProductIntegrationBean;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;

import java.util.Collection;
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
public interface AppProductIntegrationMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppProductIntegrationBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(AppProductIntegrationBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    /**
     * 根据产品UUID删除集成信息
     *
     * @param appProductUuid
     */
    void removeByAppProductUuid(String appProductUuid);

    /**
     * 保存产品集成信息树
     *
     * @param piTreeNode
     */
    void savePiTreeNode(TreeNode piTreeNode);

    /**
     * 保存产品集成信息树指定结点的子结点
     *
     * @param piTreeNode
     */
    void savePiChildNodes(TreeNode piTreeNode);

    /**
     * 获取产品集成信息树
     *
     * @param productUuid
     * @return
     */
    TreeNode getTree(String productUuid);

    /**
     * 获取产品集成信息树指定结点的子结点
     *
     * @param piTreeNode
     */
    List<TreeNode> getPiChildNodes(TreeNode piTreeNode);

    /**
     * 获取产品集成信息树
     *
     * @param productUuid
     * @param dataTypes
     * @return
     */
    TreeNode getTreeByDataType(String[] dataTypes);

    /**
     * 获取产品集成信息树
     *
     * @param productUuid
     * @param dataType    类型
     * @return
     */
    TreeNode getTreeByProductUuidAndDataType(String productUuid, String[] dataTypes);

    /**
     * 获取产品集成信息树
     *
     * @param productUuid
     * @param dataTypes
     * @param functionTypes
     * @return
     */
    TreeNode getTreeByProductUuidAndDataTypeAndFunctionType(String productUuid, String[] dataTypes,
                                                            String[] functionTypes);

    /**
     * 获取产品集成信息树
     *
     * @param uuid
     * @param dataType 类型
     * @return
     */
    TreeNode getTreeByUuid(String uuid, String[] dataTypes);

    /**
     * 根据产品集成类型及数据，返回对应的树结点列表
     *
     * @param parentPiPath
     * @param piType
     * @param piValue
     * @return
     */
    List<TreeNode> getPiTreeNodes(String parentPiPath, Integer piType, String piValue);

    /**
     * 保存集成信息的页面配置
     *
     * @param piUuid
     * @param pageUuid
     */
    void savePiPageDefinition(String piUuid, String pageUuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param dataTypes
     * @param functionTypes
     * @return
     */
    TreeNode getTreeNodeByUuid(String uuid, String[] dataTypes, String[] functionTypes);

    /**
     * 获取集成信息的所有组件定义
     *
     * @return
     */
    List<AppWidgetDefinition> getAllAppWidgetDefinition();

    /**
     * 返回自己的产品以及和平台的产品
     *
     * @param productUuid
     * @return
     */
    List<TreeNode> getTreeWithPtProduct(String productUuid);

    /**
     * 返回自己的产品以及和平台的产品及指定类型的功能
     *
     * @param productUuid
     * @return
     */
    @Description("返回自己的产品以及和平台的产品及指定类型的功能")
    List<TreeNode> getTreeWithPtProductAndFunctionType(String productUuid, String[] functionTypes);

    public abstract List<TreeNode> getTreeByDataType2(String[] dataTypes, String topName);

    /**
     * @param appSystemUuid
     * @return
     */
    List<TreeNode> getTreeByAppSystemUuid(String appSystemUuid);

    /**
     * @param appProductUuid
     * @return
     */
    long countByAppProductUuid(String appProductUuid);

    /**
     * 获取产品集成树:系统-模块-应用
     *
     * @param appSystemUuid
     * @param includeFunction 是否包含功能节点
     * @return
     */
    List<TreeNode> getSysModuleAppTreeByAppSystemUuid(String appSystemUuid);

    TreeNode getProtectedTree(String productUuid);
}
