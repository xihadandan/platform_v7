/*
 * @(#)11/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.WfFlowDefinitionUserDao;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.WfFlowDefinitionUserEntity;
import com.wellsoft.pt.bpm.engine.support.FlowDefinitionUserModifyParams;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/29/24.1	    zhulh		11/29/24		    Create
 * </pre>
 * @date 11/29/24
 */
public interface WfFlowDefinitionUserService extends JpaService<WfFlowDefinitionUserEntity, WfFlowDefinitionUserDao, Long> {
    /**
     * 同步更新流程定义用户信息
     *
     * @param flowDefinition
     * @param flowElement
     */
    void sync(FlowDefinition flowDefinition, FlowElement flowElement);

    /**
     * 根据流程定义UUID删除流程用户定义信息
     *
     * @param flowDefUuid
     */
    void deleteByFlowDefUuid(String flowDefUuid);

    /**
     * 修改流程定义的办理人信息
     *
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     */
    void modifyUserOfFlowElement(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams);

    /**
     * 验证要替换的人员是否在业务组织中
     *
     * @param definitionUserEntity
     * @param flowElement
     * @param modifyParams
     * @return
     */
    boolean validateBizOrgUser(WfFlowDefinitionUserEntity definitionUserEntity, FlowElement flowElement, FlowDefinitionUserModifyParams modifyParams);
}
