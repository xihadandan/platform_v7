package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuDeptAuditUserDao;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptUserAuditEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuDeptAuditUserDaoImpl extends AbstractJpaDaoImpl<FeishuDeptUserAuditEntity, Long> implements FeishuDeptAuditUserDao {
}
