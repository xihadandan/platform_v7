/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.wellsoft.pt.di.dao.DiDataProcessorDao;
import com.wellsoft.pt.di.entity.DiDataProcessorEntity;
import com.wellsoft.pt.di.service.DiDataProcessorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表DI_DATA_PROCESSOR的service服务接口实现类
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
public class DiDataProcessorServiceImpl extends
        AbstractJpaServiceImpl<DiDataProcessorEntity, DiDataProcessorDao, String> implements
        DiDataProcessorService {


    @Override
    public List<DiDataProcessorEntity> listByDiConfigUuidOrderBySeqAsc(String uuid) {
        return this.dao.listByDiConfigUuidOrderBySeqAsc(uuid);
    }

    @Override
    @Transactional
    public void deleteByDiConfUuid(String uuid) {
        this.dao.deleteByEntities(listByDiConfigUuidOrderBySeqAsc(uuid));
    }
}
