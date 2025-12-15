/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.facade.service;


import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.di.dto.DiDataInterationLogDto;
import com.wellsoft.pt.di.dto.DiDataProcessorLogDto;

import java.util.List;

/**
 * Description: 数据库表DI_DATA_PROCESSOR_LOG的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface DiDataProcessorLogFacadeService extends Facade {


    DiDataProcessorLogDto getProcessorLogDetails(String uuid);

    DiDataInterationLogDto getInterationLogDto(String uuid);


    List<DiDataProcessorLogDto> listLogsByExchangeId(String exchangeId);

    void redeliverData(List<String> uuids);


}
