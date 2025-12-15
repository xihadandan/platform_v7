/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service;


import com.wellsoft.pt.di.dao.DiDataProcessorLogDao;
import com.wellsoft.pt.di.dto.DiDataProcessorLogDto;
import com.wellsoft.pt.di.entity.DiDataProcessorLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表DI_DATA_PROCESSOR_LOG的service服务接口
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
public interface DiDataProcessorLogService extends
        JpaService<DiDataProcessorLogEntity, DiDataProcessorLogDao, String> {

    String saveInMessage(Object inMessage, String diConfigUuid, String diProcessorUuid,
                         String processorName,
                         String contentType,
                         String exchangeId);

    void saveOutMessage(Object outMessage, String outMessageType, Integer timeConsuming,
                        String uuid);


    List<DiDataProcessorLogDto> listLogsByExchangeId(String exchangeId);

    DiDataProcessorLogEntity getByDiConfigUuidAndProcessorUuid(String diConfigUuid,
                                                               String processorUuid);
}
