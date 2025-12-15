/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
public interface BizBusinessIntegrationFacadeService extends Facade {

    /**
     * 发起业务集成
     *
     * @param itemInstanceEntity
     * @param dyFormData
     * @param parser
     * @param extraParams
     */
    void startBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, DyFormData dyFormData,
                                  ProcessDefinitionJsonParser parser, Map<String, Object> extraParams);

    /**
     * 撤回业务集成
     *
     * @param itemInstanceEntity
     * @param parser
     * @param extraParams
     */
    void cancelBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, ProcessDefinitionJsonParser parser, Map<String, Object> extraParams);

    /**
     * 重新开始业务集成
     *
     * @param itemInstanceEntity
     * @param parser
     * @param newItemRelationEntity
     */
    void restartBusinessIntegration(BizProcessItemInstanceEntity itemInstanceEntity, ProcessDefinitionJsonParser parser, BizNewItemRelationEntity newItemRelationEntity);
}
