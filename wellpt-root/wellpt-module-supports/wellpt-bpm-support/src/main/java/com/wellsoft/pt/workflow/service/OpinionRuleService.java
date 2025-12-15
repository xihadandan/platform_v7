/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.OpinionRuleDao;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.entity.OpinionRuleEntity;

import java.util.List;

/**
 * Description: 数据库表UF_OPINION_RULE的service服务接口
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
public interface OpinionRuleService extends JpaService<OpinionRuleEntity, OpinionRuleDao, String> {

    /**
     * 获取校验规则列表
     *
     * @param systemUintIds 创建校验规则的单位ID
     * @return java.util.List<com.wellsoft.pt.workflow.entity.OpinionRuleEntity>
     **/
    public List<OpinionRuleEntity> getOpinionRuleListBySystemUintIds(List<String> systemUintIds);

    /**
     * 获取规则列表
     *
     * @param ruleUuidList 规则uuid集合
     * @return java.util.List<com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto>
     **/
    public List<OpinionRuleIncludeItemDto> getOpinionRuleIncludeItemDtos(List<String> ruleUuidList);
}
