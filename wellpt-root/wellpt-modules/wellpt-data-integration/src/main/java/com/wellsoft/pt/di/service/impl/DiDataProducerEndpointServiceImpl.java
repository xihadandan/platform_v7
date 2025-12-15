/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.wellsoft.pt.di.dao.DiDataProducerEndpointDao;
import com.wellsoft.pt.di.entity.DiDataProducerEndpointEntity;
import com.wellsoft.pt.di.service.DiDataProducerEndpointService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 数据库表DI_DATA_PRODUCER_ENDPOINT的service服务接口实现类
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
public class DiDataProducerEndpointServiceImpl extends
        AbstractJpaServiceImpl<DiDataProducerEndpointEntity, DiDataProducerEndpointDao, String> implements
        DiDataProducerEndpointService {


    @Override
    public DiDataProducerEndpointEntity getByDiConfigUuid(String uuid) {
        return this.dao.listByFieldEqValue("diConfigUuid", uuid).get(0);
    }

    @Override
    @Transactional
    public void deleteByDiConfUuid(String uuid) {
        this.dao.delete(getByDiConfigUuid(uuid));
    }
}
