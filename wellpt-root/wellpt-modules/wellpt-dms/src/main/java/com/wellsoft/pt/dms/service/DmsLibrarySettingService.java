/*
 * @(#)11/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsLibrarySettingDao;
import com.wellsoft.pt.dms.entity.DmsLibrarySettingEntity;
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
 * 11/20/25.1	    zhulh		11/20/25		    Create
 * </pre>
 * @date 11/20/25
 */
public interface DmsLibrarySettingService extends JpaService<DmsLibrarySettingEntity, DmsLibrarySettingDao, Long> {
    DmsLibrarySettingEntity getBySystem(String system);
}
