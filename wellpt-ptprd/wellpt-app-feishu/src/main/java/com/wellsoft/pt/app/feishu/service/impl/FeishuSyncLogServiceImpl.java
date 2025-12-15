package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuSyncLogDao;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogDetailEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogEntity;
import com.wellsoft.pt.app.feishu.service.FeishuSyncLogDetailService;
import com.wellsoft.pt.app.feishu.service.FeishuSyncLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeishuSyncLogServiceImpl extends AbstractJpaServiceImpl<FeishuSyncLogEntity, FeishuSyncLogDao, Long> implements FeishuSyncLogService {

    @Autowired
    private FeishuSyncLogDetailService feishuSyncLogDetailService;

    @Override
    @Transactional
    public void saveLog(FeishuSyncLogEntity feishuSyncLog, List<FeishuSyncLogDetailEntity> details) {
        this.dao.save(feishuSyncLog);
        // 同步成功才保存详情
        if (Integer.valueOf(1).equals(feishuSyncLog.getSyncStatus())) {
            details.forEach(detail -> {
                detail.setSyncLogUuid(feishuSyncLog.getUuid());
                detail.setSystem(feishuSyncLog.getSystem());
                detail.setTenant(feishuSyncLog.getTenant());
            });
            feishuSyncLogDetailService.saveAll(details);
        }
    }

}