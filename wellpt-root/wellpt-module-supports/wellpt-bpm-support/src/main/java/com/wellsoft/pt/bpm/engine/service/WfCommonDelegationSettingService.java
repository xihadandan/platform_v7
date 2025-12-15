/*
 * @(#)2/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.WfCommonDelegationSettingDao;
import com.wellsoft.pt.bpm.engine.entity.WfCommonDelegationSettingEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface WfCommonDelegationSettingService extends JpaService<WfCommonDelegationSettingEntity, WfCommonDelegationSettingDao, Long> {

    /**
     * @param uuid
     */
    void usedByUuid(Long uuid);
}
