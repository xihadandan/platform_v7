/*
 * @(#)2013-12-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.dao.ExchangeDataMonitorDao;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.integration.service.ExchangeDataMonitorService;
import com.wellsoft.pt.integration.service.ExchangeDataService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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
@Service
public class ExchangeDataMonitorServiceImpl extends
        AbstractJpaServiceImpl<ExchangeDataMonitor, ExchangeDataMonitorDao, String> implements
        ExchangeDataMonitorService {

    private static final String QUERY_EXCHANGE_DATA_BY_MONITOR_UUID = "select edm.exchangeDataSendMonitor.exchangeData from ExchangeDataMonitor edm where edm.uuid = :monitorUuid";

    @Resource
    ExchangeDataService exchageDataService;

    @Override
    public List<ExchangeData> getExchangeDatasByMonitorUuids(Collection<String> uuids) {
        List<ExchangeData> exchangeDatas = new ArrayList<ExchangeData>();
        for (String uuid : uuids) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("monitorUuid", uuid);
            List<ExchangeData> datas = exchageDataService.listByHQL(QUERY_EXCHANGE_DATA_BY_MONITOR_UUID, values);
            exchangeDatas.addAll(datas);
        }
        return exchangeDatas;
    }

    @Override
    public List<ExchangeDataMonitor> getRepeatExchangeDataMonitor() {
        return dao.getRepeatExchangeDataMonitor();
    }

    @Override
    public List<QueryItem> getJSYQ() {
        return dao.getJSYQ();
    }

    @Override
    public List<String> getDistinctUnitList() {
        return dao.getDistinctUnitList();
    }

    @Override
    public ExchangeDataMonitor getObjByUnitAndDataId(String unitId, String dataId, Integer recVer) {
        return dao.getObjByUnitAndDataId(unitId, dataId, recVer);
    }

    @Override
    public List<String> getUuidsByHQL(String hql, Map<String, Object> params) {
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public Long countByUnitId(String id) {
        String hql = "select count(*) from ExchangeDataMonitor m where m.unitId=:unitId";
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("unitId", id);
        return this.dao.getNumberByHQL(hql, valueMap);
    }

    @Override
    @Transactional
    public void deleteBySendId(String sUuid) {
        this.dao.deleteBySQL("delete from is_exchange_data_monitor where send_id='" + sUuid + "'", null);
    }
}
