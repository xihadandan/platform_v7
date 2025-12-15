/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkSyncLogDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkSyncLogDetailEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkSyncLogEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkSyncLogDetailService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkSyncLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
@Service
public class DingtalkSyncLogServiceImpl extends AbstractJpaServiceImpl<DingtalkSyncLogEntity, DingtalkSyncLogDao, Long> implements
        DingtalkSyncLogService {

    @Autowired
    private DingtalkSyncLogDetailService dingtalkSyncLogDetailService;

    @Override
    @Transactional
    public void saveLog(DingtalkSyncLogEntity dingtalkSyncLog, List<DingtalkSyncLogDetailEntity> details) {
        this.dao.save(dingtalkSyncLog);
        // 同步成功才保存详情
        if (Integer.valueOf(1).equals(dingtalkSyncLog.getSyncStatus())) {
            details.forEach(detail -> {
                detail.setSyncLogUuid(dingtalkSyncLog.getUuid());
                detail.setSystem(dingtalkSyncLog.getSystem());
                detail.setTenant(dingtalkSyncLog.getTenant());
            });
            dingtalkSyncLogDetailService.saveAll(details);
        }
    }

}
