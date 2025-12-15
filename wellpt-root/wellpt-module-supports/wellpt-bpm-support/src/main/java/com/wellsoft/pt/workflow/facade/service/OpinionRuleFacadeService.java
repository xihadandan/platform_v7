/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.OpinionRuleDto;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.dto.SaveOpinionRuleDto;

import java.util.List;

/**
 * Description: 数据库表UF_OPINION_RULE的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface OpinionRuleFacadeService extends Facade {

    /**
     * 保存流程签署意见规则
     *
     * @param saveOpinionRuleDto
     * @return void
     **/
    public void saveOpinionRule(SaveOpinionRuleDto saveOpinionRuleDto);

    /**
     * 根据主键uuid集合删除意见规则
     *
     * @param uuids
     * @return void
     **/
    public void delete(List<String> uuids);

    /**
     * 判断意见规则是否被引用
     *
     * @param opinionRuleUuids
     * @return java.lang.Boolean
     **/
    public Boolean isReferencedByOpinionRuleUuids(List<String> opinionRuleUuids);

    /**
     * 获取所有校验规则列表接口
     * 没有单位的流程，获取超管创建的规则；有单位的流程，获取超管创建的规则和对应单位的规则
     *
     * @param flowId WF_FLOW_DEFINITION表的ID字段
     * @return java.util.List<com.wellsoft.pt.workflow.dto.OpinionRuleDto>
     **/
    public List<OpinionRuleDto> getCurrentUserBelongOpinionRuleList(String flowId);

    /**
     * 流程签署意见规则详情
     *
     * @param uuid
     * @return com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto
     **/
    public OpinionRuleIncludeItemDto getOpinionRuleDetail(String uuid);

}
