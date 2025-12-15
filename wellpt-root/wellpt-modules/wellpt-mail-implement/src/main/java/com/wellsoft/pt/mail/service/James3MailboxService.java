/*
 * @(#)2015-08-06 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.mail.dao.James3MailboxDao;
import com.wellsoft.pt.mail.entity.James3Mailbox;

import java.util.List;

/**
 * Description: james3邮箱服务
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
public interface James3MailboxService extends JpaService<James3Mailbox, James3MailboxDao, String> {

    /**
     * @param example
     * @return
     */
    List<James3Mailbox> findByExample(James3Mailbox example);

}
