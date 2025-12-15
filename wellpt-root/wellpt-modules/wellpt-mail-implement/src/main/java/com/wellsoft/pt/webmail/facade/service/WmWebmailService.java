/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.entity.WmMailbox;

import javax.mail.Message;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件服务
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
public interface WmWebmailService extends BaseService {

    void oldToNew(int pageSize);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return
     */
    WmWebmailBean getWmWebmailBean(String userId, String mailboxUuid);

    /**
     * 保存待发送
     *
     * @param webmailBean
     */
    Map<String, String> send(WmWebmailBean webmailBean);


    /**
     * 定时发送
     *
     * @param webmailBean
     * @return
     */
    ResultMessage timingSend(WmWebmailBean webmailBean);

    /**
     * 修改时间
     *
     * @param webmailBean
     */
    ResultMessage updateTime(WmWebmailBean webmailBean);

    /**
     * 回执
     *
     * @param mailboxUuid
     * @param status
     */
    ResultMessage receipt(String mailboxUuid, Integer status);

    /**
     * 标记已阅
     *
     * @param mailboxUuid
     * @param userId
     */
    void markRead(String mailboxUuid, String userId);

    /**
     * 更新已读/未读状态
     *
     * @param mailboxUuids
     * @param readStatus   1 已读   0 未读
     */
    void updateMailReadStatus(List<String> mailboxUuids, String readStatus);

    /**
     * 删除
     *
     * @param mailboxUuids
     */
    void delete(Collection<String> mailboxUuids);

    /**
     * 彻底删除
     *
     * @param mailboxUuids
     */
    void deletePhysical(Collection<String> mailboxUuids);

    /**
     * 获取数量信息
     *
     * @param userId
     * @return
     */
    String countInfo(String userId);

    /**
     * 获取用户的未阅数量
     *
     * @param userId
     * @return
     */
    Long getUnreadCount(String userId);

    /**
     * 收信
     *
     * @param mailUser
     */
    int refush(String userId);

    /**
     * 更新邮件的归属位置，达到移动邮件到相关文件夹的效果
     *
     * @param folderUuid
     * @param emailUuids
     */
    void updateMailBoxName(String folderUuid, List<String> emailUuids);

    void deleteByMailboxAndUserId(List<String> mailbox, String userId);

    void saveOuterMail(List<WmMailbox> mailbox,
                       WmMailUserEntity mailUserEntity);

    WmWebmailBean getWebMail(String mailboxUuid);


    void saveMailMessage(int mid, Message message, WmMailUserEntity user) throws Exception;

    void automaticReceipt(String mailboxUuid);


}
