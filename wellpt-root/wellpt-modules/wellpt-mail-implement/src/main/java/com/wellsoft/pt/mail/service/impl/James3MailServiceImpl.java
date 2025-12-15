/*
 * @(#)2015-08-06 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.mail.dao.James3MailDao;
import com.wellsoft.pt.mail.entity.James3Mail;
import com.wellsoft.pt.mail.service.James3MailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
public class James3MailServiceImpl extends AbstractJpaServiceImpl<James3Mail, James3MailDao, String> implements
        James3MailService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mail.service.James3MailService#getTodoAsyncJames3Mail()
     */
    @Override
    public List<QueryItem> getTodoAsyncJames3Mail() {
        return this.dao.listQueryItemByNameSQLQuery("getTodoAsyncJamesMailQuery", null, null);
    }

    @Override
    public int getMailboxLastUid(String mailAddress) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userName", mailAddress);
        List<QueryItem> queryItemList = this.dao.listQueryItemByNameSQLQuery("getMailboxLastUid", values, null);
        if (queryItemList.size() > 0) {
            return queryItemList.get(0).getInt("mailboxLastUid");
        }
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mail.service.James3MailService#findByExample(James3Mail)
     */
    @Override
    public List<James3Mail> findByExample(James3Mail example) {
        return this.dao.listByEntity(example);
    }

}
