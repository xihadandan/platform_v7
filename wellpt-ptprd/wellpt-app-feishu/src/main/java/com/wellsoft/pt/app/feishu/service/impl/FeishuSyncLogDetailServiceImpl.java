package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuSyncLogDetailDao;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogDetailEntity;
import com.wellsoft.pt.app.feishu.service.FeishuSyncLogDetailService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeishuSyncLogDetailServiceImpl extends AbstractJpaServiceImpl<FeishuSyncLogDetailEntity, FeishuSyncLogDetailDao, Long> implements FeishuSyncLogDetailService {
}
