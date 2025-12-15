/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.WmMailboxDao;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件服务
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
public interface WmMailboxService extends JpaService<WmMailbox, WmMailboxDao, String> {

    public static final String HAS_INBOX_MAIL_HQL = "select count(t.uuid) from WM_MAILBOX t where t.from_mail_address = :fromMailAddress and t.subject = :subject and t.mailbox_name = 'INBOX' and (t.user_id = :toMailAddress1 or exists (select 1 from wm_mail_user tt where tt.user_id = t.user_id and tt.mail_address = :toMailAddress2))";

    /**
     * 根据UUID获取邮件实体
     *
     * @param uuid
     * @return
     */
    WmMailbox get(String uuid);

    /**
     * 获取所有邮件数据
     *
     * @return
     */
    List<WmMailbox> getAll();

    /**
     * 根据实例查询邮件列表
     *
     * @param example
     * @return
     */
    List<WmMailbox> findByExample(WmMailbox example);


    /**
     * 批量更新邮件位置名称
     *
     * @param folder
     * @param emailUuids 邮件id
     */
    void updateMailBoxName(String folder, List<String> emailUuids);

    /**
     * 按邮件归属位置批量更新邮件状态
     *
     * @param uuid
     */
    void updateMailBoxStatusByMailBoxName(String boxName, WmMailBoxStatus status);

    /**
     * 按邮件归属位置批量更新邮件的归属位置
     *
     * @param string
     * @param uuid
     */
    void updateMailBoxNameByMailBoxName(String toBoxName, String fromBoxName);

    /**
     * 根据发件uuid，与收件用户id，获取收件邮件是否已读状态
     */
    String getReceiverMailReadStatus(String mailUuid, String userId);

    /**
     * 保存从james服务器获取的邮件数据，以及同步记录（防止多次下载邮件）
     */
    void saveMailAndAsync(Map<String, Object> james3MailAsync, WmMailbox box);

    /**
     * 保存外部邮箱账号的邮件信息
     *
     * @param wmMailboxes
     * @param mailUserEntity
     */
    void saveOuterMail(WmMailbox wmMailbox,
                       WmMailUserEntity mailUserEntity);

    /**
     * 根据邮件文件夹 用户id 统计邮件大小
     *
     * @param mailbox
     * @param userId
     * @return
     */
    Long countMailSizeByMailboxNameAndUserId(String mailbox, String userId);

    /**
     * 根据邮件文件夹 用户id 统计邮件数量
     *
     * @param mailbox
     * @param userId
     * @return
     */
    Long countMailSumByMailboxNameAndUserId(String mailbox, String userId);

    /**
     * 根据用户ID 统计发件箱邮件数量
     *
     * @param userId
     * @return
     */
    Long countOutboxMailSumByUserId(String userId);

    /**
     * 根据邮件文件夹 用户id 统计 未读邮件数量
     *
     * @param mailbox
     * @param userId
     * @return
     */
    Long countUnreadMailSumByMailboxNameAndUserId(String mailbox, String userId);

    /**
     * 根据用户ID 统计草稿箱邮件数量
     *
     * @param userId
     * @return
     */
    Long countDraftMailSumByUserId(String userId);

    /**
     * 根据用户ID 统计回收站邮件数量
     *
     * @param userId
     * @return
     */
    Long countRecycleMailSumByUserId(String userId);

    /**
     * 根据用户ID 统计回收站邮件未读数量
     *
     * @param userId
     * @return
     */
    Long countUnreadRecycleMailSumByUserId(String userId);

    /**
     * 根据邮件文件夹 用户id 统计 已读邮件数量
     *
     * @param mailbox
     * @param userId
     * @return
     */
    Long countReadMailSumByMailboxNameAndUserId(String mailbox, String userId);

    /**
     * 根据 用户id 统计 发件箱未读邮件数量
     *
     * @param userId
     * @return
     */
    Long countUnreadOutboxMailSumByUserId(String userId);

    /**
     * 根据用户Id 查询邮件列表
     *
     * @param userId
     * @return
     */
    List<WmMailbox> listByUserId(String userId);

    /**
     * 根据 用户id 统计 收件箱邮件数量
     *
     * @param userId
     * @return
     */
    Long countInboxMailSumByUserId(String userId);

    /**
     * 根据 用户id 统计 收件箱未读邮件数量
     *
     * @param userId
     * @return
     */
    Long countUnreadInboxMailSumByUserId(String userId);

    /**
     * 根据 用户Id 文件夹 是否已读，状态 统计邮件数量
     *
     * @param userId
     * @param mailbox
     * @param isRead
     * @param status
     * @return
     */
    Long countMailSum(String userId, String mailbox, String isRead, Integer status);

    /**
     * 根据 邮件Message-ID，从邮件服务器上取到的邮件唯一标识  用户Id 文件夹 查询邮件是否存在
     *
     * @param mid
     * @param userId
     * @param boxName
     * @return
     */
    boolean isExistMail(int mid, String userId, String boxName);


}
