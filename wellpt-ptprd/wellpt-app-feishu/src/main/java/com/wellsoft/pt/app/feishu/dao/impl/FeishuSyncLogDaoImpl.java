package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuSyncLogDao;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuSyncLogDaoImpl extends AbstractJpaDaoImpl<FeishuSyncLogEntity, Long> implements FeishuSyncLogDao {
}
