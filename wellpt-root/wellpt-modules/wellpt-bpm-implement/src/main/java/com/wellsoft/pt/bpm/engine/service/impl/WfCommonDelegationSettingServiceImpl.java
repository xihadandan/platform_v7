/*
 * @(#)2/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.WfCommonDelegationSettingDao;
import com.wellsoft.pt.bpm.engine.entity.WfCommonDelegationSettingEntity;
import com.wellsoft.pt.bpm.engine.service.WfCommonDelegationSettingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2/13/25.1	    zhulh		2/13/25		    Create
 * </pre>
 * @date 2/13/25
 */
@Service
public class WfCommonDelegationSettingServiceImpl extends AbstractJpaServiceImpl<WfCommonDelegationSettingEntity, WfCommonDelegationSettingDao, Long> implements
        WfCommonDelegationSettingService {

    @Override
    @Transactional
    public void usedByUuid(Long uuid) {
        WfCommonDelegationSettingEntity entity = this.getOne(uuid);
        if (entity != null) {
            entity.setUsedCount(entity.getUsedCount() + 1);
            this.dao.save(entity);
        }
    }

}
