package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuEventDao;
import com.wellsoft.pt.app.feishu.entity.FeishuEventEntity;
import com.wellsoft.pt.app.feishu.service.FeishuEventService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeishuEventServiceImpl extends AbstractJpaServiceImpl<FeishuEventEntity, FeishuEventDao, Long> implements FeishuEventService {
}
