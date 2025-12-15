/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.facade.service;


import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.di.dto.DiConfigDto;
import com.wellsoft.pt.di.enums.EdpParameterType;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表DI_CONFIG的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface DiConfigFacadeService extends Facade {


    String saveConfig(DiConfigDto diConfigDto);


    boolean startOrStopRouteByConfigUuid(String uuid);

    Select2QueryData endpointSelections(Select2QueryInfo queryInfo);

    Select2QueryData processorSelections(Select2QueryInfo queryInfo);


    Select2QueryData diJobSelections(Select2QueryInfo queryInfo);

    Select2QueryData diCallbackSelections(Select2QueryInfo queryInfo);

    DiConfigDto getDetails(String uuid);

    void deleteDiConfigs(List<String> uuids);

    List<Map<String, String>> getProcessorSupportParameters(String processorClazz);


    List<Map<String, String>> getEndpointSupportParameters(String endpointType,
                                                           EdpParameterType type);

}
