/*
 * @(#)2013-12-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.ExchangeDataMonitorDao;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-29.1	zhulh		2013-12-29		Create
 * </pre>
 * @date 2013-12-29
 */
public interface ExchangeDataMonitorService extends JpaService<ExchangeDataMonitor, ExchangeDataMonitorDao, String> {

    /**
     * 根据监控UUID列表获取相应的数据交换对象
     *
     * @param uuids
     * @return
     */
    List<ExchangeData> getExchangeDatasByMonitorUuids(Collection<String> uuids);

    List<ExchangeDataMonitor> getRepeatExchangeDataMonitor();

    List<QueryItem> getJSYQ();

    List<String> getDistinctUnitList();

    ExchangeDataMonitor getObjByUnitAndDataId(String unitId, String dataId, Integer recVer);

    List<String> getUuidsByHQL(String hql1, Map<String, Object> hqlMap1);

    Long countByUnitId(String id);

    /**
     * @param sUuid
     */
    void deleteBySendId(String sUuid);

}
