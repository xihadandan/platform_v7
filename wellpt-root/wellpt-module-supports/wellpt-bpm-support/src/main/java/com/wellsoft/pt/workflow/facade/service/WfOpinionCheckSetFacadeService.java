/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.IsOpinionRuleCheckDto;
import com.wellsoft.pt.workflow.dto.WfOpinionCheckSetDto;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;

import java.util.List;

/**
 * Description: 数据库表WF_OPINION_CHECK_SET的门面服务接口，提供给其他模块以及前端调用的业务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-05-11.1	zenghw		2021-05-11		Create
 * </pre>
 * @date 2021-05-11
 */
public interface WfOpinionCheckSetFacadeService extends Facade {

    /**
     * 获取当前流程的意见校验设置列表接口
     *
     * @param flowId 流程定义ID:对应WF_FLOW_DEFINITION表的ID字段
     * @return java.util.List<com.wellsoft.pt.workflow.dto.OpinionRuleDto>
     **/
    public List<WfOpinionCheckSetDto> getOpinionCheckSets(String flowId);

    /**
     * 保存流程的意见校验设置列表
     *
     * @param dtos
     * @return void
     **/
    @Deprecated
    public void saveOpinionCheckSetList(List<WfOpinionCheckSetEntity> dtos);

    /**
     * 保存流程的意见校验设置列表
     *
     * @param flowDefId
     * @param dtos
     */
    void saveOpinionCheckSetList(String flowDefId, List<WfOpinionCheckSetEntity> dtos);

    /**
     * 校验签署意见是否符合规则
     *
     * @param opinionText 签署意见
     * @param flowId      流程定义ID
     * @param taskId      环节ID
     * @param scene       场景
     * @return com.wellsoft.pt.workflow.dto.IsOpinionRuleCheckDto
     **/
    public IsOpinionRuleCheckDto isOpinionRuleCheck(String opinionText, String flowId, String taskId, String scene);

}
