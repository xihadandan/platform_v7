/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.DXExchangeDataDao;
import com.wellsoft.pt.integration.entity.DXExchangeData;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
import com.wellsoft.pt.integration.service.DXExchangeDataService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

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
@Service
public class DXExchangeDataServiceImpl extends AbstractJpaServiceImpl<DXExchangeData, DXExchangeDataDao, String>
        implements DXExchangeDataService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDataByDataId(java.lang.String, java.lang.Integer)
     */
    @Override
    public DXExchangeData getDXExchangeDataByDataId(String dataId, Integer recVer) {
        return dao.getDXExchangeDataByDataId(dataId, recVer);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDataByDataIdAll(java.lang.String, java.lang.Integer)
     */
    @Override
    public DXExchangeData getDXExchangeDataByDataIdAll(String dataId, Integer recVer) {
        return dao.getDXExchangeDataByDataIdAll(dataId, recVer);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDatasByBatchId(java.lang.String)
     */
    @Override
    public List<DXExchangeData> getDXExchangeDatasByBatchId(String batchId) {
        return dao.getDXExchangeDatasByBatchId(batchId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDatasByBatchUuid(java.lang.String)
     */
    @Override
    public List<DXExchangeData> getDXExchangeDatasByBatchUuid(String batchUuid) {
        return dao.getDXExchangeDatasByBatchUuid(batchUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDatasByDataId(java.lang.String)
     */
    @Override
    public DXExchangeData getDXExchangeDatasByDataId(String dataId) {
        return dao.getDXExchangeDatasByDataId(dataId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDataListByDataId(java.lang.String)
     */
    @Override
    public List<DXExchangeData> getDXExchangeDataListByDataId(String dataId) {
        return dao.getDXExchangeDataListByDataId(dataId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDataMonitorByCorrelationandUnitId(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    public DXExchangeRouteDate getDXExchangeDataMonitorByCorrelationandUnitId(String correlationId,
                                                                              Integer correlationRecVer, String unitId) {
        return dao.getDXExchangeDataMonitorByCorrelationandUnitId(correlationId, correlationRecVer, unitId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getSBYQ()
     */
    @Override
    public List<QueryItem> getSBYQ() {
        return dao.getSBYQ();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getBMXK()
     */
    @Override
    public List<QueryItem> getBMXK() {
        return dao.getBMXK();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getAllCommercialSubjectByTypeId(java.lang.String)
     */
    @Override
    public long getAllCommercialSubjectByTypeId(String typeId) {
        return dao.getAllCommercialSubjectByTypeId(typeId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getCurrentMonthCommercialRegistrationByTypeId(java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public long getCurrentMonthCommercialRegistrationByTypeId(String typeId, Date firstDay, Date lastDay) {
        return dao.getCurrentMonthCommercialRegistrationByTypeId(typeId, firstDay, lastDay);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#findNewestDataByYwlshAndTypeId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<DXExchangeData> findNewestDataByYwlshAndTypeId(String ywlsh, String typeId, String other) {
        return dao.findNewestDataByYwlshAndTypeId(ywlsh, typeId, other);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeDataService#getDXExchangeDatesBySpecial(java.lang.String, java.lang.String)
     */
    @Override
    public List<DXExchangeData> getDXExchangeDatesBySpecial(String zch, String typeId) {
        return dao.getDXExchangeDatesBySpecial(zch, typeId);
    }

}
