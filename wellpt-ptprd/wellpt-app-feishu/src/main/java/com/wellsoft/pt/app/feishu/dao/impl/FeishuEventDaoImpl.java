package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuEventDao;
import com.wellsoft.pt.app.feishu.entity.FeishuEventEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuEventDaoImpl extends AbstractJpaDaoImpl<FeishuEventEntity, Long> implements FeishuEventDao {
}
