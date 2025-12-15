/*
 * @(#)2017年7月10日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.workflow.facade.service.FlowFacade;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional
public class FlowFacadeImpl implements FlowFacade {

    @Autowired
    private FlowDefineService flowDefineSercice;

    @Autowired
    private FlowSchemeService flowSchemeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowFacade#getMaxVerFlowDefByCatagoryCode(java.lang.String)
     */
    @Override
    public List<QueryItem> getMaxVerFlowDefByCatagoryCode(String categoryCode) {
        return flowDefineSercice.getMaxVerFlowDefByCatagoryCode(categoryCode);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowFacade#getFlowTasks(java.lang.String)
     */
    @Override
    public List<KeyValuePair> getFlowTasks(String flowDefId) {
        return flowSchemeService.getFlowTasks(flowDefId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowFacade#getSubForms(java.lang.String, java.lang.String)
     */
    @Override
    public List<com.wellsoft.context.component.tree.TreeNode> getSubForms(String treeNodeId, String formUuid) {
        return flowSchemeService.getSubForms(treeNodeId, formUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowFacade#getFlowDefinition(java.lang.String)
     */
    @Override
    public FlowDefinition getFlowDefinition(String uuid) {
        return flowDefineSercice.get(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowFacade#getFormFields(java.lang.String, java.lang.String)
     */
    @Override
    public List<com.wellsoft.context.component.tree.TreeNode> getFormFields(String treeNodeId, String formUuid) {
        return flowSchemeService.getFormFields(treeNodeId, formUuid);
    }


}
