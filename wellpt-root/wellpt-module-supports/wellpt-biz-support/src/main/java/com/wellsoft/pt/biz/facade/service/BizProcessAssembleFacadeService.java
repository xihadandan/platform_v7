/*
 * @(#)2/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizProcessAssembleDto;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/28/24.1	zhulh		2/28/24		Create
 * </pre>
 * @date 2/28/24
 */
public interface BizProcessAssembleFacadeService extends Facade {

    /**
     * 根据业务流程定义UUID获取业务装配信息
     *
     * @param processDefUuid
     * @return
     */
    BizProcessAssembleDto getByProcessDefUuid(String processDefUuid);

    /**
     * 保存业务流程装配
     *
     * @param processDefUuid
     * @param definitionJson
     * @return
     */
    Long saveDefinitionJson(String processDefUuid, String definitionJson);
}
