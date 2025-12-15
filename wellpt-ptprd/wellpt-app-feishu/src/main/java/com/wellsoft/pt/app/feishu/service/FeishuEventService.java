package com.wellsoft.pt.app.feishu.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.app.feishu.entity.FeishuEventEntity;
import com.wellsoft.pt.app.feishu.dao.FeishuEventDao;

public interface FeishuEventService extends JpaService<FeishuEventEntity, FeishuEventDao, Long> {
}