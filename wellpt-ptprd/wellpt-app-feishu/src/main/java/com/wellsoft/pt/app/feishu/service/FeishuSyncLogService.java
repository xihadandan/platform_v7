package com.wellsoft.pt.app.feishu.service;

import com.wellsoft.pt.app.feishu.dao.FeishuSyncLogDao;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogDetailEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

public interface FeishuSyncLogService extends JpaService<FeishuSyncLogEntity, FeishuSyncLogDao, Long> {

    void saveLog(FeishuSyncLogEntity feishuSyncLog, List<FeishuSyncLogDetailEntity> details);
}