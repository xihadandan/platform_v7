package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuUserDao;
import com.wellsoft.pt.app.feishu.entity.FeishuUserEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuUserDaoImpl extends AbstractJpaDaoImpl<FeishuUserEntity, Long> implements FeishuUserDao {
}
