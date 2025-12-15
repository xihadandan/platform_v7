/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service;


import com.wellsoft.pt.di.dao.DiDataProducerEndpointDao;
import com.wellsoft.pt.di.entity.DiDataProducerEndpointEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表DI_DATA_PRODUCER_ENDPOINT的service服务接口
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
public interface DiDataProducerEndpointService extends
        JpaService<DiDataProducerEndpointEntity, DiDataProducerEndpointDao, String> {

    DiDataProducerEndpointEntity getByDiConfigUuid(String uuid);

    void deleteByDiConfUuid(String uuid);
}
