/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.ExchangeDataBatchDao;
import com.wellsoft.pt.integration.entity.ExchangeDataBatch;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface ExchangeDataBatchService extends JpaService<ExchangeDataBatch, ExchangeDataBatchDao, String> {
    /**
     * 根据typeId获得所有批次
     *
     * @param typeId
     * @return
     */
    List<ExchangeDataBatch> getExchangeDataBatchByTypeId(String typeId);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    List<ExchangeDataBatch> getExchangeDataBatchById(String id);

    /**
     * 获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    List<String> getDistinctFromUnitList();

    /**
     * 根据类别获取ExchangeData中所有不同的发送单位名
     *
     * @return
     */
    List<String> getDistinctFromUnitListByTypeId(String typeId);

    /**
     * 获取ExchangeData中所有的发送单位名
     *
     * @return
     */
    List<String> getFromUnitList();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<String> getAllUnitId();

    /**
     * 如何描述该方法
     *
     * @param batchId
     * @return
     */
    ExchangeDataBatch getById(String batchId);
}
