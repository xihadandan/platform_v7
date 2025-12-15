/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.dao.FulltextRebuildLogDao;
import com.wellsoft.pt.fulltext.entity.FulltextRebuildLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;
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
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
public interface FulltextRebuildLogService extends JpaService<FulltextRebuildLogEntity, FulltextRebuildLogDao, Long> {
    /**
     * @param settingUuid
     * @return
     */
    List<FulltextRebuildLogEntity> listBySettingUuid(Long settingUuid);

    long countBySettingUuidAndRuleIdAndExecuteState(Long settingUuid, String ruleId, String executeState, Date deadline);
}
