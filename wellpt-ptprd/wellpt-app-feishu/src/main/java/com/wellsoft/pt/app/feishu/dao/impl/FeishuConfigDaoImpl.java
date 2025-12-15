package com.wellsoft.pt.app.feishu.dao.impl;

import com.wellsoft.pt.app.feishu.dao.FeishuConfigDao;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class FeishuConfigDaoImpl extends AbstractJpaDaoImpl<FeishuConfigEntity, Long> implements FeishuConfigDao {

}