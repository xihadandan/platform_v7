package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuSyncLogDetailDao;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogDetailEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuSyncLogDetailDaoImpl extends AbstractJpaDaoImpl<FeishuSyncLogDetailEntity, Long> implements FeishuSyncLogDetailDao {
}
