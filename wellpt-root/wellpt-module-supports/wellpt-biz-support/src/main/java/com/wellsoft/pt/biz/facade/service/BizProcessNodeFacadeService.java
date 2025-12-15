/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataRequestParamDto;

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
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
public interface BizProcessNodeFacadeService extends Facade {

    /**
     * 根据过程节点实例UUID获取过程节点办件实例数据
     *
     * @param processNodeInstUuid
     * @return
     */
    BizProcessNodeDataDto getProcessNodeByUuid(String processNodeInstUuid);

    /**
     * 获取过程节点办件实例数据
     *
     * @param requestParamDto
     * @return
     */
    BizProcessNodeDataDto getProcessNodeData(BizProcessNodeDataRequestParamDto requestParamDto);

    /**
     * 保存过程节点办件实例数据
     *
     * @param processNodeDataDto
     * @return
     */
    String save(BizProcessNodeDataDto processNodeDataDto);

    /**
     * 根据过程节点实例UUID获取业务事项实例列表
     *
     * @param processNodeInstUuid
     * @return
     */
    List<BizProcessItemInstanceDto> listProcessItemInstanceByUuid(String processNodeInstUuid);

}
