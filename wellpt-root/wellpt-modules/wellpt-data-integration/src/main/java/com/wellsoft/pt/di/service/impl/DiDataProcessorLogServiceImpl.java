/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.di.dao.DiDataProcessorLogDao;
import com.wellsoft.pt.di.dto.DiDataProcessorLogDto;
import com.wellsoft.pt.di.entity.DiDataProcessorLogEntity;
import com.wellsoft.pt.di.service.DiDataProcessorLogService;
import com.wellsoft.pt.di.service.DiDataProcessorService;
import com.wellsoft.pt.di.util.MessageUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表DI_DATA_PROCESSOR_LOG的service服务接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
@Service
public class DiDataProcessorLogServiceImpl extends
        AbstractJpaServiceImpl<DiDataProcessorLogEntity, DiDataProcessorLogDao, String> implements
        DiDataProcessorLogService {


    @Autowired
    DiDataProcessorService diDataProcessorService;

    @Override
    @Transactional
    public String saveInMessage(Object inMessage, String diConfigUuid,
                                String diProcessorUuid, String processorName, String contentType,
                                String exchangeId) {
        DiDataProcessorLogEntity logEntity = new DiDataProcessorLogEntity();
        logEntity.setDiConfigUuid(diConfigUuid);
        logEntity.setDiProcessorUuid(diProcessorUuid);
        logEntity.setProcessorName(processorName);
        logEntity.setInMessage(MessageUtils.object2ReadableJSON(inMessage));
        logEntity.setExchangeId(exchangeId);
        save(logEntity);
        return logEntity.getUuid();
    }


    @Override
    @Transactional
    public void saveOutMessage(Object outMessage, String outMessageType, Integer timeConsuming,
                               String uuid) {
        DiDataProcessorLogEntity logEntity = getOne(uuid);
        logEntity.setOutMessage(MessageUtils.object2ReadableJSON(outMessage));
        logEntity.setTimeConsuming(timeConsuming);
        save(logEntity);
    }


    @Override
    public List<DiDataProcessorLogDto> listLogsByExchangeId(String exchangeId) {
        DiDataProcessorLogEntity example = new DiDataProcessorLogEntity();
        example.setExchangeId(exchangeId);
        List<DiDataProcessorLogEntity> logEntities = this.dao.listByEntity(example, null,
                "createTime asc", null);
        List<DiDataProcessorLogDto> logDtos = Lists.newArrayList();
        for (DiDataProcessorLogEntity logEntity : logEntities) {
            DiDataProcessorLogDto logDto = new DiDataProcessorLogDto();
            BeanUtils.copyProperties(logEntity, logDto);
            logDtos.add(logDto);
        }
        return logDtos;

    }

    @Override
    public DiDataProcessorLogEntity getByDiConfigUuidAndProcessorUuid(String diConfigUuid,
                                                                      String processorUuid) {

        DiDataProcessorLogEntity example = new DiDataProcessorLogEntity();
        example.setDiConfigUuid(diConfigUuid);
        example.setDiProcessorUuid(processorUuid);
        List<DiDataProcessorLogEntity> logEntities = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(logEntities) ? logEntities.get(0) : null;
    }
}
