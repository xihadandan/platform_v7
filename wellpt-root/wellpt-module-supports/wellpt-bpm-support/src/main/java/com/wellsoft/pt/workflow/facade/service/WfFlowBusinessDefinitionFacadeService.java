/*
 * @(#)11/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.WfFlowBusinessDefinitionDto;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;

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
 * 11/11/22.1	zhulh		11/11/22		Create
 * </pre>
 * @date 11/11/22
 */
public interface WfFlowBusinessDefinitionFacadeService extends Facade {
    /**
     * 根据UUID获取流程业务定义
     *
     * @param uuid
     * @return
     */
    WfFlowBusinessDefinitionDto getDto(String uuid);

    /**
     * 根据ID获取流程定义ID
     *
     * @param id
     * @return
     */
    String getFlowDefIdById(String id);

    /**
     * 保存流程业务定义
     *
     * @param dto
     */
    void saveDto(WfFlowBusinessDefinitionDto dto);

    /**
     * 保存流程业务定义
     *
     * @param flowBusinessDefinitionJsons
     */
    void saveAll(Collection<FlowBusinessDefinitionJson> flowBusinessDefinitionJsons);

    /**
     * 根据流程业务定义UUID列表删除流程业务定义
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * 根据流程定义ID获取表单字段下拉选项
     *
     * @param flowDefId
     * @return
     */
    List<Select2DataBean> getFormFieldSelectDataByFlowDefId(String flowDefId);

    /**
     * 根据流程定义ID获取流程环节下拉数据
     *
     * @param flowDefId
     * @return
     */
    List<Select2DataBean> getFlowTaskSelectDataByFlowDefId(String flowDefId);

    /**
     * 根据流程定义ID获取流程流向下拉数据
     *
     * @param flowDefId
     * @return
     */
    List<Select2DataBean> getFlowDirectionSelectDataByFlowDefId(String flowDefId);

    /**
     * 获取流程业务监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listFlowBusinessListenerSelectData(Select2QueryInfo queryInfo);

    /**
     * 服务端返回传入的阶段树
     *
     * @param treeNodes
     * @return
     */
    List<TreeNode> serverReturnStageTree(List<TreeNode> treeNodes);

}
