/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service;

import com.wellsoft.pt.jpa.service.EntityService;
import com.wellsoft.pt.task.dao.impl.QrtzTriggersDaoImpl;
import com.wellsoft.pt.task.entity.QrtzTriggersEntity;

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
public interface QrtzTriggersService extends EntityService<QrtzTriggersEntity, QrtzTriggersDaoImpl> {

    /**
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    boolean isExists(String triggerName, String triggerGroup);

}
