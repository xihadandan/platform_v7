/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.dao.WmMailboxDao;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件dao实现
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
public class WmMailboxDaoImpl extends AbstractJpaDaoImpl<WmMailbox, String> implements
        WmMailboxDao {


    @Override
    @Transactional
    public void deleteByMailBoxNameMidUserId(String mailboxName, String mid, String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailboxName", mailboxName);
        param.put("mid", mid);
        param.put("userId", userId);
        this.deleteByHQL(
                "delete from WmMailbox where mailboxName=:mailboxName and userId=:userId and mid=:mid",
                param);
    }

    @Override
    public Long countMailSizeByMailboxNameAndUserId(String mailboxName, String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailboxName", mailboxName);
        param.put("userId", userId);
        Number mailSize = this.getCharSequenceBySQL("select sum(b.mail_size) from wm_mailbox_info_user a left join wm_mailbox_info b " +
                "on a.mail_info_uuid = b.uuid where a.mailbox_name=:mailboxName and a.user_id=:userId", param);
        if (mailSize == null) {
            return 0l;
        }
        return mailSize.longValue();
    }

    @Override
    public Long countMailByParams(Map<String, Object> params) {
        List<QueryItem> numbers = this.listItemByNameHQLQuery("countMailByParams", QueryItem.class,
                params,
                null);
        return numbers.get(0).getLong("total");

    }

    @Override
    public boolean isExistMail(int mid, String userId, String boxName) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailbox", boxName);
        param.put("userId", userId);
        param.put("mid", mid + "");
        List<QueryItem> numbers = this.listItemByNameHQLQuery("countMailByParams", QueryItem.class,
                param,
                null);
        return numbers.get(0).getLong("total") > 0;
    }


}
