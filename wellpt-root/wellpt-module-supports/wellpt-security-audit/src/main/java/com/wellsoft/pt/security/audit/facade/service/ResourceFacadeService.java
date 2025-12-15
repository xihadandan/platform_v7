/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.entity.Resource;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface ResourceFacadeService extends Facade {
    /**
     * 根据UUID获取资源
     *
     * @param uuid
     * @return
     */
    ResourceBean getBean(String uuid);

    /**
     * 保存资源
     *
     * @param bean
     */
    String saveBean(ResourceBean bean);

    /**
     * 根据UUID删除资源
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 获取资源的树形结构
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getMenuAsTree(String excludeUuid);

    TreeNode getModuleMenuAsTree(String uuid, String moduleId);

    /**
     * 如何描述该方法
     *
     * @param code
     * @return
     */
    Resource getResourceByCode(String code);

    /**
     * 获取资源按钮树
     *
     * @param treeNodeId
     * @param excludeId
     * @return
     */
    List<TreeNode> getResourceButtonTree(String treeNodeId, String excludeUuid);

    /**
     * 获取资源按钮树
     *
     * @param excludeId
     * @return
     */
    List<TreeNode> getResourceButtonTree(String excludeUuid);

    /**
     * 获取资源的树形结构
     *
     * @param excludeUuid
     * @return
     */
    List<TreeNode> getResourceMenuTree(String treeNodeId, String excludeUuid);

    long coutModuleMenus(String moduleId);

    List<Resource> getModuleMenuResources(String id);
}
