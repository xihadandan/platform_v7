package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuEventHandleDao;
import com.wellsoft.pt.app.feishu.entity.FeishuEventHandleEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuEventHandleDaoImpl extends AbstractJpaDaoImpl<FeishuEventHandleEntity, Long> implements FeishuEventHandleDao {
}
