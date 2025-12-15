/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfOpinionRuleItemDao;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;

import java.util.List;

/**
 * Description: 数据库表WF_OPINION_RULE_ITEM的service服务接口
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
public interface WfOpinionRuleItemService extends JpaService<WfOpinionRuleItemEntity, WfOpinionRuleItemDao, String> {

    /**
     * 通过流程意见规则Uuid获取校验项列表
     *
     * @param opinionRuleUuid
     * @return java.util.List<com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto>
     **/
    public List<WfOpinionRuleItemEntity> getWfOpinionRuleItemList(String opinionRuleUuid);

    /**
     * 通过流程意见规则Uuid集合获取校验项列表
     *
     * @param opinionRuleUuids
     * @return java.util.List<com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto>
     **/
    public List<WfOpinionRuleItemEntity> getWfOpinionRuleItemListByOpinionRuleUuids(List<String> opinionRuleUuids);

    /**
     * 通过流程意见规则Uuid删除校验项
     *
     * @param opinionRuleUuid
     * @return void
     **/
    public void deleteByOpinionRuleUuid(String opinionRuleUuid);

}
