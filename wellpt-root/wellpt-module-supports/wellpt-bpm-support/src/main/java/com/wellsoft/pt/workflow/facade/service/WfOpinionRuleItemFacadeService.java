/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;

import java.util.List;

/**
 * Description: 数据库表WF_OPINION_RULE_ITEM的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface WfOpinionRuleItemFacadeService extends Facade {

    /**
     * 保存流程意见规则检验项
     *
     * @param opinionRuleUuid         关联流程意见规则UUID
     * @param opinionRuleItemEntities
     * @return void
     **/
    public void saveOpinionRuleItems(String opinionRuleUuid, List<WfOpinionRuleItemEntity> opinionRuleItemEntities);

    /**
     * 通过流程意见规则Uuid获取校验项列表
     *
     * @param opinionRuleUuid
     * @return java.util.List<com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto>
     **/
    public List<WfOpinionRuleItemDto> getWfOpinionRuleItemList(String opinionRuleUuid);

    /**
     * 通过流程意见规则Uuid集合获取校验项列表
     *
     * @param opinionRuleUuids
     * @return java.util.List<com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto>
     **/
    public List<WfOpinionRuleItemDto> getWfOpinionRuleItemListByOpinionRuleUuids(List<String> opinionRuleUuids);

    /**
     * 通过流程意见规则Uuid删除校验项
     *
     * @param opinionRuleUuid
     * @return void
     **/
    public void deleteByOpinionRuleUuid(String opinionRuleUuid);

}
