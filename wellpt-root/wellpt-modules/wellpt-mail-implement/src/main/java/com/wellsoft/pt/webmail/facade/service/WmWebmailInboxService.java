/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;

import java.util.Collection;

/**
 * Description: 邮件收件箱服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月3日.1	zhulh		2016年6月3日		Create
 * </pre>
 * @date 2016年6月3日
 */
public interface WmWebmailInboxService extends BaseService {

    /**
     * 查看
     *
     * @param mailboxUuid
     * @return
     */
    WmWebmailBean getForView(String mailboxUuid);

    /**
     * 删除
     *
     * @param mailboxUuids
     */
    void delete(Collection<String> mailboxUuids);

    /**
     * 物理删除
     *
     * @param mailboxUuids
     */
    void deletePhysical(Collection<String> mailboxUuids);

    void deleteMailByMailbox(String mailbox, String userId);

}
