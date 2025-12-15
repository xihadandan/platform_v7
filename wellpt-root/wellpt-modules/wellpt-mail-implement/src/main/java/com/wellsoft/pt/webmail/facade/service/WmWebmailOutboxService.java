/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件发件箱服务
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
public interface WmWebmailOutboxService extends BaseService {

    WmWebmailBean get(String mailboxUuid);

    WmWebmailBean getForEditAgain(String mailboxUuid);

    WmWebmailBean getForTransfer(String mailboxUuid);

    /**
     * 回复
     *
     * @param mailboxUuid
     * @return
     */
    WmWebmailBean getForReply(String mailboxUuid);

    /**
     * 回复全部
     *
     * @param mailboxUuid
     * @return
     */
    WmWebmailBean getForReplyAll(String mailboxUuid);

    /**
     * 保存为草稿
     *
     * @param webmailBean
     * @return
     */
    WmMailboxInfoUser saveDraft(WmWebmailBean webmailBean);

    long calculateWmMailSize(WmMailboxInfo wmMailbox);

    long calculateWmMailSize(WmMailbox wmMailbox);

    /**
     * 保存为正式的发件
     *
     * @param webmailBean
     * @return
     */
    WmMailbox saveNormal(WmWebmailBean webmailBean);

    void pushFilesToFolder(WmMailboxInfo wmMailbox);

    void pushFilesToFolder(WmMailbox wmMailbox);

    /**
     * @param mailBoxs
     */
    void saveAll(List<WmMailbox> mailBoxs);

    void delete(Collection<String> mailboxUuids);

    void deletePhysical(Collection<String> mailboxUuids);

    /**
     * 查询邮件投递状态
     *
     * @param mailboxUuid
     * @param fast
     * @return
     * @throws Exception
     */
    Map<String, Object> querySendStatus(String mailboxUuid, Boolean fastBreak) throws Exception;


}
