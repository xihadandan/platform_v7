/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.integration.dao.ExchangeDataBatchDao;
import com.wellsoft.pt.integration.entity.ExchangeDataBatch;
import com.wellsoft.pt.integration.service.ExchangeDataBatchService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
@Service
public class ExchangeDataBatchServiceImpl extends
        AbstractJpaServiceImpl<ExchangeDataBatch, ExchangeDataBatchDao, String> implements ExchangeDataBatchService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getExchangeDataBatchByTypeId(java.lang.String)
     */
    @Override
    public List<ExchangeDataBatch> getExchangeDataBatchByTypeId(String typeId) {
        return dao.getExchangeDataBatchByTypeId(typeId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getExchangeDataBatchById(java.lang.String)
     */
    @Override
    public List<ExchangeDataBatch> getExchangeDataBatchById(String id) {
        return dao.getExchangeDataBatchById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getDistinctFromUnitList()
     */
    @Override
    public List<String> getDistinctFromUnitList() {
        return dao.getDistinctFromUnitList();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getDistinctFromUnitListByTypeId(java.lang.String)
     */
    @Override
    public List<String> getDistinctFromUnitListByTypeId(String typeId) {
        return dao.getDistinctFromUnitListByTypeId(typeId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getFromUnitList()
     */
    @Override
    public List<String> getFromUnitList() {
        return dao.getFromUnitList();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getAllUnitId()
     */
    @Override
    public List<String> getAllUnitId() {
        return dao.getAllUnitId();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataBatchService#getById(java.lang.String)
     */
    @Override
    public ExchangeDataBatch getById(String batchId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", batchId);
        return dao.getOneByHQL("from ExchangeDataBatch where id=:id", param);
    }

}
