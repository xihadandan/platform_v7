/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.di.facade.service.DiDataConsumerEndpointFacadeService;
import com.wellsoft.pt.di.service.DiDataConsumerEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表DI_DATA_CONSUMER_ENDPOINT的门面服务实现类
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
public class DiDataConsumerEndpointFacadeServiceImpl extends AbstractApiFacade implements DiDataConsumerEndpointFacadeService {

    @Autowired
    private DiDataConsumerEndpointService diDataConsumerEndpointService;


}
