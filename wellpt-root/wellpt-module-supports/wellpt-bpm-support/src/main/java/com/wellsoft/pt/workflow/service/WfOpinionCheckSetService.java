/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfOpinionCheckSetDao;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;

import java.util.List;

/**
 * Description: 数据库表WF_OPINION_CHECK_SET的service服务接口
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
public interface WfOpinionCheckSetService extends JpaService<WfOpinionCheckSetEntity, WfOpinionCheckSetDao, String> {

    /**
     * 判断意见规则是否被引用
     *
     * @param opinionRuleUuids
     * @return java.lang.Boolean
     **/
    public Boolean isReferencedByOpinionRuleUuids(List<String> opinionRuleUuids);

    /**
     * 获取当前流程的意见校验设置列表接口
     *
     * @param flowId
     * @return java.util.List<com.wellsoft.pt.workflow.dto.OpinionRuleDto>
     **/
    public List<WfOpinionCheckSetEntity> getOpinionCheckSets(String flowId);

    /**
     * 删除意见规则设置通过流程定义ID
     *
     * @param flowId
     * @return void
     **/
    public void deleteByFlowId(String flowId);
}
