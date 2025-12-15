/*
 * @(#)8/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.biz.dao.BizProcessEntityTimerDao;
import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.service.BizProcessEntityTimerService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.enums.EnumTimerStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/6/24.1	    zhulh		8/6/24		    Create
 * </pre>
 * @date 8/6/24
 */
@Service
public class BizProcessEntityTimerServiceImpl extends AbstractJpaServiceImpl<BizProcessEntityTimerEntity, BizProcessEntityTimerDao, Long> implements BizProcessEntityTimerService {

    /**
     * @param entityId
     * @param stateField
     * @param stateCode
     * @param configId
     * @param processInstUuid
     * @return
     */
    @Override
    public BizProcessEntityTimerEntity getNormalByEntityIdAndState(String entityId, String stateField, String stateCode,
                                                                   String configId, String processInstUuid) {
        Assert.hasLength(entityId, "业务主体ID不能为空！");
        Assert.hasLength(stateField, "业务主体状态字段不能为空！");
        Assert.hasLength(configId, "业务主体状态计时配置ID不能为空！");
        Assert.hasLength(processInstUuid, "业务流程实例UUID不能为空！");

        String hql = "from BizProcessEntityTimerEntity t where t.entityId = :entityId and t.stateField = :stateField and t.id = :id and t.processInstUuid = :processInstUuid and t.timerState in(:timerStates)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("entityId", entityId);
        params.put("stateField", stateField);
        params.put("id", configId);
        params.put("processInstUuid", processInstUuid);
        params.put("timerStates", Lists.newArrayList(EnumTimerStatus.STARTED.getValue(), EnumTimerStatus.PASUE.getValue()));
        List<BizProcessEntityTimerEntity> entities = this.dao.listByHQL(hql, params);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     */
    @Override
    @Transactional
    public void updateTimerData(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto) {
        String timerUuid = tsTimerDto.getUuid();
        Integer timerState = tsTimerDto.getStatus();
        Integer timingState = tsTimerDto.getTimingState();
        Date dueTime = tsTimerDto.getDueTime();
        Double usedTimeLimit = tsTimerDto.getInitTimeLimit() - tsTimerDto.getTimeLimit();

        timerEntity.setTimerUuid(timerUuid);
        timerEntity.setTimerState(timerState);
        timerEntity.setTimingState(timingState);
        timerEntity.setDueTime(dueTime);
        timerEntity.setTotalTime(usedTimeLimit);

        this.dao.save(timerEntity);
    }

    /**
     * @param timerUuid
     * @return
     */
    @Override
    public BizProcessEntityTimerEntity getByTimerUuid(String timerUuid) {
        return this.dao.getOneByFieldEq("timerUuid", timerUuid);
    }

}
