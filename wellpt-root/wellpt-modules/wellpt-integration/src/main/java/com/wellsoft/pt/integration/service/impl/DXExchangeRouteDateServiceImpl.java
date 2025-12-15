/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.DXExchangeRouteDateDao;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
import com.wellsoft.pt.integration.service.DXExchangeRouteDateService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

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
public class DXExchangeRouteDateServiceImpl extends
        AbstractJpaServiceImpl<DXExchangeRouteDate, DXExchangeRouteDateDao, String> implements
        DXExchangeRouteDateService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeRouteDateService#getObjByUnitAndDataId(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public DXExchangeRouteDate getObjByUnitAndDataId(String unitId, String dataId, Integer recVer) {
        return dao.getObjByUnitAndDataId(unitId, dataId, recVer);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeRouteDateService#getDistinctUnitList()
     */
    @Override
    public List<String> getDistinctUnitList() {
        return dao.getDistinctUnitList();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeRouteDateService#getJSYQ()
     */
    @Override
    public List<QueryItem> getJSYQ() {
        return dao.getJSYQ();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeRouteDateService#getDXExchangeDataReceiveByBatchId(java.lang.String)
     */
    @Override
    public List<DXExchangeRouteDate> getDXExchangeDataReceiveByBatchId(String batchId) {
        return dao.getDXExchangeDataReceiveByBatchId(batchId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.DXExchangeRouteDateService#getRepeatDXExchangeDataReceive()
     */
    @Override
    public List<DXExchangeRouteDate> getRepeatDXExchangeDataReceive() {
        return dao.getRepeatDXExchangeDataReceive();
    }

}
