/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkSyncLogDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkSyncLogDetailEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkSyncLogEntity;
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
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
public interface DingtalkSyncLogService extends JpaService<DingtalkSyncLogEntity, DingtalkSyncLogDao, Long> {
    void saveLog(DingtalkSyncLogEntity dingtalkSyncLog, List<DingtalkSyncLogDetailEntity> details);
}
