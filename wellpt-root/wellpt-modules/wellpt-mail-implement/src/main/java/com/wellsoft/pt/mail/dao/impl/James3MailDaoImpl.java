/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.dao.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.mail.dao.James3MailDao;
import com.wellsoft.pt.mail.entity.James3Mail;
import com.wellsoft.pt.mail.support.James3Constant;
import org.springframework.stereotype.Repository;

/**
 * Description: James3邮箱持久化服务
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月18日.1	chenqiong		2018年4月18日		Create
 * </pre>
 * @date 2018年4月18日
 */
@Repository
public class James3MailDaoImpl extends AbstractJpaDaoImpl<James3Mail, String> implements James3MailDao {

    @Override
    protected void initSessionFactory() {
        bindSessionFactory(James3Constant.DATA_SOURCE, Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY));
    }
}
