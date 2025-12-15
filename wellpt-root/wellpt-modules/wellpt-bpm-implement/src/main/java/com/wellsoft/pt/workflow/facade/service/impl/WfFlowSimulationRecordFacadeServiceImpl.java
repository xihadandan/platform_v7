/*
 * @(#)10/16/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationRecordDto;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSimulationRecordFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordItemService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/16/24.1	    zhulh		10/16/24		    Create
 * </pre>
 * @date 10/16/24
 */
@Service
public class WfFlowSimulationRecordFacadeServiceImpl extends AbstractApiFacade implements WfFlowSimulationRecordFacadeService {

    @Autowired
    private WfFlowSimulationRecordService flowSimulationRecordService;

    @Autowired
    private WfFlowSimulationRecordItemService flowSimulationRecordItemService;

    /**
     * @param uuid
     * @return
     */
    @Override
    public WfFlowSimulationRecordDto getDto(Long uuid) {
        WfFlowSimulationRecordDto dto = new WfFlowSimulationRecordDto();
        WfFlowSimulationRecordEntity recordEntity = flowSimulationRecordService.getOne(uuid);
        if (recordEntity != null) {
            BeanUtils.copyProperties(recordEntity, dto);
        }
        List<WfFlowSimulationRecordItemEntity> items = flowSimulationRecordItemService.listByRecordUuid(uuid);
        dto.setItems(items);
        // 按创建时间升序
        Collections.sort(dto.getItems(), (o1, o2) -> {
            if (StringUtils.equals(o2.getPreTaskInstUuid(), o1.getTaskInstUuid())) {
                return -1;
            }
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        });
        return dto;
    }

}
