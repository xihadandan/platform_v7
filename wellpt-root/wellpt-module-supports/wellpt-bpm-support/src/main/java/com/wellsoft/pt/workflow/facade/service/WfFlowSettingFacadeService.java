/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.WfFlowSettingDto;

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
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
public interface WfFlowSettingFacadeService extends Facade {

    /**
     * 初始化流程设置
     *
     * @param system
     * @param tenant
     */
    void initBySystemAndTenant(String system, String tenant);
    
    /**
     * 根据UUID获取流程设置
     *
     * @param uuid
     * @return
     */
    WfFlowSettingDto getDto(Long uuid);

    /**
     * @param attrKey
     * @param system
     * @param tenant
     * @return
     */
    WfFlowSettingDto getDtoByAttrKeyAndSystemAndTenant(String attrKey, String system, String tenant);

    /**
     * 根据归属系统获取流程设置
     *
     * @param system
     * @return
     */
    List<WfFlowSettingDto> listBySystemAndTenant(String system, String tenant);

    /**
     * 保存流程设置
     *
     * @param dto
     */
    void saveDto(WfFlowSettingDto dto);

    /**
     * 批量保存流程设置
     *
     * @param dtos
     */
    void saveDtos(List<WfFlowSettingDto> dtos);

}
