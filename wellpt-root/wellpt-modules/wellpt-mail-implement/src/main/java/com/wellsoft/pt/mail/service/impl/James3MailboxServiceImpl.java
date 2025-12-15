/*
 * @(#)2015-08-06 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.mail.dao.James3MailboxDao;
import com.wellsoft.pt.mail.entity.James3Mailbox;
import com.wellsoft.pt.mail.service.James3MailboxService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: james3邮箱服务Impl
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-08-06.1	zhulh		2015-08-06		Create
 * </pre>
 * @date 2015-08-06
 */
@Service
public class James3MailboxServiceImpl extends AbstractJpaServiceImpl<James3Mailbox, James3MailboxDao, String> implements
        James3MailboxService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mail.service.James3MailboxService#findByExample(James3Mailbox)
     */
    @Override
    public List<James3Mailbox> findByExample(James3Mailbox example) {
        return this.dao.listByEntity(example);
    }

}
