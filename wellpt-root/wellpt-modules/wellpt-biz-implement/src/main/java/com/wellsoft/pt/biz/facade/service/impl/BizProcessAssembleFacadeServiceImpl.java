/*
 * @(#)2/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizProcessAssembleDto;
import com.wellsoft.pt.biz.entity.BizProcessAssembleEntity;
import com.wellsoft.pt.biz.facade.service.BizProcessAssembleFacadeService;
import com.wellsoft.pt.biz.service.BizProcessAssembleService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class BizProcessAssembleFacadeServiceImpl extends AbstractApiFacade implements BizProcessAssembleFacadeService {

    @Autowired
    private BizProcessAssembleService processAssembleService;

    /**
     * 根据业务流程定义UUID获取业务装配信息
     *
     * @param processDefUuid
     * @return
     */
    @Override
    public BizProcessAssembleDto getByProcessDefUuid(String processDefUuid) {
        BizProcessAssembleDto dto = new BizProcessAssembleDto();
        BizProcessAssembleEntity entity = processAssembleService.getByProcessDefUuid(processDefUuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 保存业务流程装配
     *
     * @param processDefUuid
     * @param definitionJson
     * @return
     */
    @Override
    public Long saveDefinitionJson(String processDefUuid, String definitionJson) {
        BizProcessAssembleEntity entity = processAssembleService.getByProcessDefUuid(processDefUuid);
        if (entity != null) {
            entity.setDefinitionJson(definitionJson);
        } else {
            entity = new BizProcessAssembleEntity();
            entity.setProcessDefUuid(processDefUuid);
            entity.setDefinitionJson(definitionJson);
        }
        processAssembleService.save(entity);
        return entity.getUuid();
    }

}
