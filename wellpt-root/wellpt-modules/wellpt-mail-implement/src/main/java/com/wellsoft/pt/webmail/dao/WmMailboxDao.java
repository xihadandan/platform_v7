/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.webmail.entity.WmMailbox;

import java.util.Map;

/**
 * Description: 邮件dao
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
public interface WmMailboxDao extends JpaDao<WmMailbox, String> {

    void deleteByMailBoxNameMidUserId(String mailboxName, String mid, String userId);

    Long countMailSizeByMailboxNameAndUserId(String mailboxName, String userId);

    Long countMailByParams(Map<String, Object> params);

    boolean isExistMail(int mid, String userId, String fromMailId);
}
