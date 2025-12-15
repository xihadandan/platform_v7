/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.ExchangeDataDao;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;
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
public interface ExchangeDataService extends JpaService<ExchangeData, ExchangeDataDao, String> {
    List<ExchangeData> getExchangeDatesBySpecial(String zch, String typeId);

    List<ExchangeData> findNewestDataByYwlshAndTypeId(String ywlsh, String typeId, String other);

    long getCurrentMonthCommercialRegistrationByTypeId(String typeId, Date firstDay, Date lastDay);

    long getAllCommercialSubjectByTypeId(String typeId);

    List<QueryItem> getBMXK();

    List<QueryItem> getSBYQ();

    ExchangeDataMonitor getExchangeDataMonitorByCorrelationandUnitId(String correlationId, Integer correlationRecVer,
                                                                     String unitId);

    List<ExchangeData> getExchangeDataListByDataId(String dataId);

    ExchangeData getExchangeDatasByDataId(String dataId);

    List<ExchangeData> getExchangeDatasByBatchId(String batchId);

    ExchangeData getExchangeDataByDataIdAll(String dataId, Integer recVer);

    ExchangeData getExchangeDataByDataId(String dataId, Integer recVer);
}
