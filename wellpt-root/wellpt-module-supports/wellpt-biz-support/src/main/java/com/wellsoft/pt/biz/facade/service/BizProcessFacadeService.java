/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizProcessDataDto;
import com.wellsoft.pt.biz.dto.BizProcessDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;

import java.util.List;
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
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
public interface BizProcessFacadeService extends Facade {

    /**
     * 根据业务流程实例UUID获取业务流程办件实例数据
     *
     * @param processInstUuid
     * @return
     */
    BizProcessDataDto getProcessByUuid(String processInstUuid);

    /**
     * 获取业务流程办件实例数据
     *
     * @param requestParamDto
     * @return
     */
    BizProcessDataDto getProcessData(BizProcessDataRequestParamDto requestParamDto);

    /**
     * 保存业务流程办件实例数据
     *
     * @param processDataDto
     * @return
     */
    String save(BizProcessDataDto processDataDto);

    /**
     * 根据业务流程实例UUID获取过程结点实例列表
     *
     * @param processInstUuid
     * @param loadItemInstCount
     * @return
     */
    List<BizProcessNodeInstanceDto> listProcessNodeInstanceByUuid(String processInstUuid, boolean loadItemInstCount);

    /**
     * 获取业务主体主表数据
     *
     * @param entityFormUuid
     * @param entityIdValue
     * @param entityIdField
     * @return
     */
    Map<String, Object> getEntityFormDataOfMainform(String entityFormUuid, String entityIdValue, String entityIdField);
    
}
