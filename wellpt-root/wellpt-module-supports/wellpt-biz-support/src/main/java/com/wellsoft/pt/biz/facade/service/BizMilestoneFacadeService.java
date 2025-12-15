/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizMilestoneDto;

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
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
public interface BizMilestoneFacadeService extends Facade {

    /**
     * 保存里程碑事件信息
     *
     * @param dto
     */
    void saveDto(BizMilestoneDto dto);

    /**
     * 保存里程碑事件信息
     *
     * @param dtos
     */
    void saveAllDto(List<BizMilestoneDto> dtos);

}
