/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.ExchangeDataSendMonitorDao;
import com.wellsoft.pt.integration.entity.ExchangeDataSendMonitor;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
public interface ExchangeDataSendMonitorService extends
        JpaService<ExchangeDataSendMonitor, ExchangeDataSendMonitorDao, String> {

    /**
     * @param hql
     * @param values
     * @return
     */
    List<String> getUuidsByHQL(String hql, Map<String, Object> values);

}
