/*
 * @(#)2017年7月10日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年7月10日.1	zhongzh		2017年7月10日		Create
 * </pre>
 * @date 2017年7月10日
 */
public interface FlowFacade {
    /**
     * 根据流程分类编号，获取该分类下所有最高版本的流程定义
     *
     * @param catagoryCode 流程分类
     * @return
     */
    List<QueryItem> getMaxVerFlowDefByCatagoryCode(String categoryCode);

    /**
     * 根据流程定义ID，获取最高版本的流程定义的环节map<id, name>
     *
     * @return
     */
    List<KeyValuePair> getFlowTasks(String flowDefId);

    /**
     * 根据动态表单UUID，获取从表信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getSubForms(String treeNodeId, String formUuid);

    /**
     * 获取指定的流程定义
     *
     * @param uuid
     */
    FlowDefinition getFlowDefinition(String uuid);

    /**
     * 根据动态表单UUID，获取表单字段信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getFormFields(String treeNodeId, String formUuid);

}
