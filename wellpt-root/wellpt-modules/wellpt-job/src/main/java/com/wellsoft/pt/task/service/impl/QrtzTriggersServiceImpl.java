/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.service.impl.AbstractEntityServiceImpl;
import com.wellsoft.pt.task.dao.impl.QrtzTriggersDaoImpl;
import com.wellsoft.pt.task.entity.QrtzTriggersEntity;
import com.wellsoft.pt.task.service.QrtzTriggersService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
 * 2018年11月13日.1	zhulh		2018年11月13日		Create
 * </pre>
 * @date 2018年11月13日
 */
@Service
public class QrtzTriggersServiceImpl extends AbstractEntityServiceImpl<QrtzTriggersEntity, QrtzTriggersDaoImpl>
        implements QrtzTriggersService {

    private String hql = "select count(*) from QrtzTriggersEntity t where t.triggerName = :triggerName and t.triggerGroup = :triggerGroup and t.triggerState in(:triggerStates)";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.QrtzFiredTriggersService#isExists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isExists(String triggerName, String triggerGroup) {
        QrtzTriggersEntity entity = new QrtzTriggersEntity();
        entity.setTriggerName(triggerName);
        entity.setTriggerGroup(triggerGroup);
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> triggerStates = Lists.newArrayList();
        triggerStates.add("WAITING");
        triggerStates.add("ACQUIRED");
        values.put("triggerName", triggerName);
        values.put("triggerGroup", triggerGroup);
        values.put("triggerStates", triggerStates);
        return this.dao.countByHQL(hql, values) > 0;
    }

}
