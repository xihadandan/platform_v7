package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuEventHandleDao;
import com.wellsoft.pt.app.feishu.entity.FeishuEventHandleEntity;
import com.wellsoft.pt.app.feishu.service.FeishuEventHandleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeishuEventHandleServiceImpl extends AbstractJpaServiceImpl<FeishuEventHandleEntity, FeishuEventHandleDao, Long> implements FeishuEventHandleService {
}
