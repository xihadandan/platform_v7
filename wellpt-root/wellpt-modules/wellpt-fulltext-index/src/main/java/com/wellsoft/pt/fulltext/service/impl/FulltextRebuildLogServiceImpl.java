/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.fulltext.dao.FulltextRebuildLogDao;
import com.wellsoft.pt.fulltext.entity.FulltextRebuildLogEntity;
import com.wellsoft.pt.fulltext.service.FulltextRebuildLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Service
public class FulltextRebuildLogServiceImpl extends AbstractJpaServiceImpl<FulltextRebuildLogEntity, FulltextRebuildLogDao, Long> implements FulltextRebuildLogService {

    @Override
    public List<FulltextRebuildLogEntity> listBySettingUuid(Long settingUuid) {
        FulltextRebuildLogEntity entity = new FulltextRebuildLogEntity();
        entity.setSettingUuid(settingUuid);
        return this.dao.listByEntity(entity, null, "createTime desc", new PagingInfo(1, 100));
    }

    @Override
    public long countBySettingUuidAndRuleIdAndExecuteState(Long settingUuid, String ruleId, String executeState, Date deadline) {
        String hql = "from FulltextRebuildLogEntity t where t.settingUuid = :settingUuid and t.ruleId = :ruleId and t.executeState = :executeState and t.createTime >= :deadline";
        Map<String, Object> params = Maps.newHashMap();
        params.put("settingUuid", settingUuid);
        params.put("ruleId", ruleId);
        params.put("executeState", executeState);
        params.put("deadline", deadline);
        return this.dao.countByHQL(hql, params);
    }

}
