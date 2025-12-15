package com.wellsoft.pt.app.feishu.service.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuDeptAuditUserDao;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptUserAuditEntity;
import com.wellsoft.pt.app.feishu.service.FeishuDeptUserAuditService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeishuDeptUserAuditServiceImpl extends AbstractJpaServiceImpl<FeishuDeptUserAuditEntity, FeishuDeptAuditUserDao, Long> implements FeishuDeptUserAuditService {
}
