/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.di.dto.DiDataInterationLogDto;
import com.wellsoft.pt.di.dto.DiDataProcessorLogDto;
import com.wellsoft.pt.di.entity.DiDataInterationLogEntity;
import com.wellsoft.pt.di.entity.DiDataProcessorLogEntity;
import com.wellsoft.pt.di.facade.service.DiDataProcessorLogFacadeService;
import com.wellsoft.pt.di.service.DiDataInterationLogService;
import com.wellsoft.pt.di.service.DiDataProcessorLogService;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.di.util.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表DI_DATA_PROCESSOR_LOG的门面服务实现类
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
public class DiDataProcessorLogFacadeServiceImpl extends AbstractApiFacade implements
        DiDataProcessorLogFacadeService {

    @Autowired
    private DiDataProcessorLogService diDataProcessorLogService;

    @Autowired
    private DiDataInterationLogService diDataInterationLogService;


    @Override
    public DiDataProcessorLogDto getProcessorLogDetails(String uuid) {
        DiDataProcessorLogEntity log = diDataProcessorLogService.getOne(uuid);
        DiDataProcessorLogDto logDto = new DiDataProcessorLogDto();
        BeanUtils.copyProperties(log, logDto);
        return logDto;
    }

    @Override
    public DiDataInterationLogDto getInterationLogDto(String uuid) {
        DiDataInterationLogEntity logEntity = diDataInterationLogService.getOne(uuid);
        DiDataInterationLogDto logDto = new DiDataInterationLogDto();
        BeanUtils.copyProperties(logEntity, logDto);
        return logDto;
    }


    @Override
    public List<DiDataProcessorLogDto> listLogsByExchangeId(String exchangeId) {
        return diDataProcessorLogService.listLogsByExchangeId(exchangeId);
    }

    @Override
    public void redeliverData(List<String> uuids) {
        for (String uid : uuids) {
            DiDataInterationLogEntity log = diDataInterationLogService.getOne(uid);
            if (log == null) {
                continue;
            }
            DiDataProcessorLogEntity processorLogEntity = diDataProcessorLogService.getByDiConfigUuidAndProcessorUuid(
                    log.getDiConfigUuid(), "0");
            if (processorLogEntity != null) {
                CamelContextUtils.producer().asyncCallbackSendBody(
                        "direct:retry_" + log.getDiConfigUuid(),
                        MessageUtils.fromJSON(processorLogEntity.getInMessage()),
                        new Synchronization() {
                            @Override
                            public void onComplete(Exchange exchange) {

                            }

                            @Override
                            public void onFailure(Exchange exchange) {


                            }
                        });
            }
        }


    }
}
