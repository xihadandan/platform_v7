/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.DXExchangeRouteDateDao;
import com.wellsoft.pt.integration.entity.DXExchangeRouteDate;
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
public interface DXExchangeRouteDateService extends JpaService<DXExchangeRouteDate, DXExchangeRouteDateDao, String> {
    DXExchangeRouteDate getObjByUnitAndDataId(String unitId, String dataId, Integer recVer);

    /**
     * 获取所有不同的接收单位名
     *
     * @return
     */
    List<String> getDistinctUnitList();

    /**
     * 接收逾期汇总
     *
     * @return
     */
    List<QueryItem> getJSYQ();

    /**
     * 获得批次下的所有收件
     *
     * @param batchId
     * @return
     */
    List<DXExchangeRouteDate> getDXExchangeDataReceiveByBatchId(String batchId);

    /**
     * 获得需要重发收件
     *
     * @return
     */
    List<DXExchangeRouteDate> getRepeatDXExchangeDataReceive();
}
