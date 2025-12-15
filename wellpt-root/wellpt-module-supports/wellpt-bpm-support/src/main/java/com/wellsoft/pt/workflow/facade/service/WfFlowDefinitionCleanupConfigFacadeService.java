/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto;

import java.util.List;

/**
 * Description: 流程定义定时清理配置对外服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
public interface WfFlowDefinitionCleanupConfigFacadeService extends Facade {

    /**
     * 获取流程定义定时清理配置
     *
     * @param uuid
     * @return
     */
    WfFlowDefinitionCleanupConfigDto getDto();

    /**
     * 获取所有流程定义定时清理配置
     *
     * @return
     */
    List<WfFlowDefinitionCleanupConfigDto> listAllDto();

    /**
     * 保存流程定义定时清理配置
     *
     * @param uuid
     * @return
     */
    void saveDto(WfFlowDefinitionCleanupConfigDto dto);

}
