/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.wellsoft.pt.app.weixin.dao.WeixinSyncLogDao;
import com.wellsoft.pt.app.weixin.entity.WeixinSyncLogDetailEntity;
import com.wellsoft.pt.app.weixin.entity.WeixinSyncLogEntity;
import com.wellsoft.pt.app.weixin.service.WeixinSyncLogDetailService;
import com.wellsoft.pt.app.weixin.service.WeixinSyncLogService;
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
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
@Service
public class WeixinSyncLogServiceImpl extends AbstractJpaServiceImpl<WeixinSyncLogEntity, WeixinSyncLogDao, Long> implements WeixinSyncLogService {

    @Autowired
    private WeixinSyncLogDetailService weixinSyncLogDetailService;

    @Override
    @Transactional
    public void saveLog(WeixinSyncLogEntity weixinSyncLog, List<WeixinSyncLogDetailEntity> details) {
        this.dao.save(weixinSyncLog);
        // 同步成功才保存详情
        if (Integer.valueOf(1).equals(weixinSyncLog.getSyncStatus())) {
            details.forEach(detail -> {
                detail.setSyncLogUuid(weixinSyncLog.getUuid());
                detail.setSystem(weixinSyncLog.getSystem());
                detail.setTenant(weixinSyncLog.getTenant());
            });
            weixinSyncLogDetailService.saveAll(details);
        }
    }

}
