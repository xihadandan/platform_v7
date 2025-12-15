/*
 * @(#)11/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsRoleModelDao;
import com.wellsoft.pt.dms.entity.DmsRoleModelEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/25/25.1	    zhulh		11/25/25		    Create
 * </pre>
 * @date 11/25/25
 */
public interface DmsRoleModelService extends JpaService<DmsRoleModelEntity, DmsRoleModelDao, Long> {
    void initBySystemAndTenant(String system, String tenant);

    List<DmsRoleModelEntity> listBySystem(String system);

    List<DmsRoleModelEntity> listByRoleUuids(List<String> uuids);
}
