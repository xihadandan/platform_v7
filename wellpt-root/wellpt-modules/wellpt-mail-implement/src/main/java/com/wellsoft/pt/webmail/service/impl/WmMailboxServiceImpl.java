/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.mail.support.James3Constant;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.dao.WmMailboxDao;
import com.wellsoft.pt.webmail.entity.*;
import com.wellsoft.pt.webmail.enums.WmMailBoxName;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件服务实现
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
@Service
public class WmMailboxServiceImpl extends
        AbstractJpaServiceImpl<WmMailbox, WmMailboxDao, String> implements
        WmMailboxService {

    @Autowired
    WmMailUserService wmMailUserService;

    @Autowired
    WmMailUseCapacityService wmMailUseCapacityService;

    @Autowired
    WmWebmailOutboxService wmWebmailOutboxService;

    @Autowired
    WmWebmailService wmWebmailService;
    @Autowired
    WmMailUnfetchedService wmMailUnfetchedService;

    @Autowired
    WmMailboxInfoUserService wmMailboxInfoUserService;

    @Autowired
    WmMailboxInfoService wmMailboxInfoService;


    @Override
    public WmMailbox get(String uuid) {
        return this.dao.getOne(uuid);
    }


    @Override
    public List<WmMailbox> getAll() {
        return listAll();
    }


    @Override
    public List<WmMailbox> findByExample(WmMailbox example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void updateMailBoxName(String folder, List<String> emailUuids) {
        Map<String, Object> param = Maps.newHashMap();
        for (String emailUuid : emailUuids) {
            // 邮件位置变了，则状态位也需要变更
            WmMailboxInfoUser mail = wmMailboxInfoUserService.getOne(emailUuid);
            if (mail == null) {
                continue;
            }
            if (mail.getStatus() == WmMailBoxStatus.DRAFT.getCode()
                    && mail.getSendStatus() != null
                    && mail.getSendStatus() == 0
                    && mail.getNextSendTime() != null) {
                throw new RuntimeException("定时邮件不能移动");
            }
            WmMailboxInfo mailboxInfo = wmMailboxInfoService.getOne(mail.getMailInfoUuid());
            WmMailBoxStatus destStatus = statusTransferByMailBoxChanged(mail, folder);
            String fromMailbox = mail.translateMailbox();
            long fromMailSize = mailboxInfo.getMailSize();
            param.put("status", destStatus.getCode());
            mail.setMailboxName(folder);
            mail.setStatus(destStatus.getCode());
            String toMailbox = mail.translateMailbox();
            long toMailSize = mailboxInfo.getMailSize();
            if (wmMailUseCapacityService.updateseCapacityTransform(fromMailSize, toMailSize,
                    fromMailbox,
                    toMailbox, mail.getUserId()) == 0) {
                throw new RuntimeException("邮件空间不足");
            }
            wmMailboxInfoUserService.update(mail);
        }
    }

    /**
     * 由于邮件归属文件夹位置变更引起的状态位修改
     *
     * @param mail
     * @param folder
     * @return
     */
    private WmMailBoxStatus statusTransferByMailBoxChanged(WmMailboxInfoUser mail, String toFolder) {
        // 1.移入收件箱
        if (WmMailBoxName.IN_BOX.getCode().equals(toFolder)) {
            if (mail.getMailboxName().startsWith(WmMailFolderEntity.FOLDER_CODE_PREFIX)// 从自定义文件夹
                    || WmMailBoxStatus.LOGICAL_DELETE.getCode() == mail.getStatus()// 从回收站
                    || WmMailBoxStatus.DRAFT.getCode() == mail.getStatus()// 从草稿箱
                    || WmMailBoxName.OUT_BOX.getCode().equals(mail.getMailboxName())) {// 从发件箱

                return WmMailBoxStatus.FETCH_SUCCESS;
            }
        }

        // 2.移入发件箱
        if (WmMailBoxName.OUT_BOX.getCode().equals(toFolder)) {
            if (mail.getMailboxName().startsWith(WmMailFolderEntity.FOLDER_CODE_PREFIX)// 从自定义文件夹
                    || WmMailBoxStatus.LOGICAL_DELETE.getCode() == mail.getStatus()// 从回收站
                    || WmMailBoxStatus.DRAFT.getCode() == mail.getStatus()// 从草稿箱
                    || WmMailBoxName.IN_BOX.getCode().equals(mail.getMailboxName())) {// 从收件箱
                return WmMailBoxStatus.SEND_SUCCESS;
            }
        }

        // 3.移入自定义的文件夹
        if (toFolder.startsWith(WmMailFolderEntity.FOLDER_CODE_PREFIX)) {
            if (WmMailBoxStatus.LOGICAL_DELETE.getCode() == mail.getStatus()) {// 从回收站
                return WmMailBoxStatus.FETCH_SUCCESS;
            }
        }
        return WmMailBoxStatus.getStatusByCode(mail.getStatus());
    }

    @Override
    @Transactional
    public void updateMailBoxStatusByMailBoxName(String mailboxName, WmMailBoxStatus status) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailboxName", mailboxName);
        param.put("status", status.getCode());
        param.put("userId", SpringSecurityUtils.getCurrentUserId());
        this.dao.updateBySQL(
                "update wm_mailbox_info_user set status=:status where mailbox_name =:mailboxName and user_id=:userId",
                param);

    }

    @Override
    @Transactional
    public void updateMailBoxNameByMailBoxName(String toBoxName, String fromBoxName) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("toBoxName", toBoxName);
        param.put("fromBoxName", fromBoxName);
        param.put("userId", SpringSecurityUtils.getCurrentUserId());
        String status = null;
        if (WmMailBoxName.IN_BOX.getCode().equals(toBoxName)) {
            // 移动到收件箱，需要更改状态接收成功
            status = WmMailBoxStatus.FETCH_SUCCESS.getCode() + "";
        }

        if (WmMailBoxName.OUT_BOX.getCode().equals(toBoxName)) {
            // 移动到发件箱，需要更改状态发送成功
            status = WmMailBoxStatus.SEND_SUCCESS.getCode() + "";
        }
        if (status != null) {
            param.put("status", status);
        }
        this.dao.updateBySQL("update wm_mailbox_info_user set mailbox_name=:toBoxName "
                        + (status != null ? ",status=:status" : "") + "  where mailbox_name =:fromBoxName and user_id=:userId",
                param);

    }

    @Override
    @Transactional(readOnly = true)
    public String getReceiverMailReadStatus(String mailUuid, String userId) {
        WmMailbox fromMail = get(mailUuid);
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        values.put("mailUuid", mailUuid);
        // 原则是只有最多一条收件信息，为了兼容之前收件、抄送、密送中存在相同邮件账号会发多发的邮件数据
        List<String> statusList = this.dao.listCharSequenceBySQL(
                "select r.is_read from wm_mailbox r  where r.user_id=:userId and r.from_mail_uuid=:mailUuid",
                values);
        return CollectionUtils.isEmpty(statusList) ? null : statusList.get(0);
    }

    @Override
    @Transactional
    public void saveMailAndAsync(Map<String, Object> jamesMailAsync, WmMailbox box) {
        if (wmMailUseCapacityService.updateUseCapacity(
                wmWebmailOutboxService.calculateWmMailSize(box), box.getUserId(), box.getSystemUnitId(),
                WmWebmailConstants.INBOX) == 0) {
            throw new RuntimeException("用户=" + box.getUserId() + " 同步邮件异常，空间不足");
        }
        if (MapUtils.isNotEmpty(jamesMailAsync)) {
            // 保存同步数据，防止邮件被多次下载邮件
            SessionFactory jamesSessionFactory = this.dao.getSessionFactory(
                    James3Constant.DATA_SOURCE);
            Session session = jamesSessionFactory.getCurrentSession();

            Query query = session.createSQLQuery(
                    NamedQueryScriptLoader.generateDynamicNamedQueryString(jamesSessionFactory,
                            "insertJamesMailAsync", null));
            if (MapUtils.isNotEmpty(jamesMailAsync)) {
                query.setProperties(jamesMailAsync);
            }
            query.executeUpdate();
        }

        if (box != null) {// 重复的邮件，允许不保存本地
            this.dao.save(box);
            this.wmMailUserService.updateMailUserMid(box.getUserId(), box.getMid());

            if (StringUtils.isNotBlank(box.getFromMailUuid())) {
                wmMailUnfetchedService.deleteByMailUuidAndUserId(box.getFromMailUuid(), box.getUserId());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOuterMail(WmMailbox mailbox,
                              WmMailUserEntity mailUserEntity) {
        if (wmMailUseCapacityService.updateUseCapacity(
                wmWebmailOutboxService.calculateWmMailSize(mailbox), mailUserEntity.getUserId(), mailUserEntity.getSystemUnitId(),
                mailbox.getMailboxName()) == 0) {
            throw new RuntimeException("邮件空间不足");
        }
        Integer lastMessageNumber = Integer.parseInt(mailbox.getMid());
        mailUserEntity.setSyncMessageNumber(lastMessageNumber);
        wmMailUserService.save(mailUserEntity);
        this.dao.deleteByMailBoxNameMidUserId(mailbox.getMailboxName(), mailbox.getMid(),
                mailbox.getUserId());
        save(mailbox);

    }

    /**
     * 根据邮件文件夹 用户id
     *
     * @param mailboxName
     * @param userId
     * @return
     */
    @Override
    public Long countMailSizeByMailboxNameAndUserId(String mailboxName, String userId) {
        return this.dao.countMailSizeByMailboxNameAndUserId(mailboxName, userId);
    }

    @Override
    public Long countMailSumByMailboxNameAndUserId(String mailbox, String userId) {
        WmMailbox box = new WmMailbox();
        box.setUserId(userId);
        box.setMailboxName(mailbox);
        return this.dao.countByEntity(box);
    }

    @Override
    public Long countOutboxMailSumByUserId(String userId) {
        return this.countMailSum(userId, WmWebmailConstants.OUTBOX, null,
                WmWebmailConstants.STATUS_SEND_SUCCESS);
    }

    @Override
    public Long countUnreadMailSumByMailboxNameAndUserId(String mailbox, String userId) {
        return this.countMailSum(userId, mailbox, WmWebmailConstants.FLAG_UNREAD,
                null);
    }

    @Override
    public Long countDraftMailSumByUserId(String userId) {
        return this.countMailSum(userId, WmWebmailConstants.OUTBOX, null,
                WmWebmailConstants.STATUS_DRAFT);
    }

    @Override
    public Long countRecycleMailSumByUserId(String userId) {
        return this.countMailSum(userId, null, null,
                WmWebmailConstants.STATUS_DELETE);
    }

    @Override
    public Long countUnreadRecycleMailSumByUserId(String userId) {
        return this.countMailSum(userId, null, WmWebmailConstants.FLAG_UNREAD,
                WmWebmailConstants.STATUS_DELETE);
    }

    @Override
    public Long countReadMailSumByMailboxNameAndUserId(String mailbox, String userId) {
        return this.countMailSum(userId, mailbox, WmWebmailConstants.FLAG_READ, null);
    }

    @Override
    public Long countUnreadOutboxMailSumByUserId(String userId) {
        return this.countMailSum(userId, WmWebmailConstants.OUTBOX, WmWebmailConstants.FLAG_UNREAD,
                WmWebmailConstants.STATUS_SEND_SUCCESS);
    }

    @Override
    public List<WmMailbox> listByUserId(String userId) {
        return this.dao.listByFieldEqValue("userId", userId);
    }

    @Override
    public Long countUnreadInboxMailSumByUserId(String userId) {
        return this.countMailSum(userId, WmWebmailConstants.INBOX, WmWebmailConstants.FLAG_UNREAD,
                WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
    }

    @Override
    public Long countMailSum(String userId, String mailbox, String isRead, Integer status) {
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(userId)) {
            params.put("userId", userId);
        }
        if (StringUtils.isNotBlank(mailbox)) {
            params.put("mailbox", mailbox);
        }
        if (StringUtils.isNotBlank(isRead)) {
            params.put("isRead", Integer.valueOf(isRead));
        }
        if (status != null) {
            if (status == -3) {
                params.put("excludeDeleteStatus",
                        Lists.newArrayList(WmWebmailConstants.STATUS_PHYSICAL_DELETE,
                                WmWebmailConstants.STATUS_DELETE));
            } else {
                params.put("status", status);
            }
        }


        return this.dao.countMailByParams(params);
    }

    @Override
    public boolean isExistMail(int mid, String userId, String boxName) {

        return this.dao.isExistMail(mid, userId, boxName);
    }

    @Override
    public Long countInboxMailSumByUserId(String userId) {
        return this.countMailSum(userId, WmWebmailConstants.INBOX, null,
                WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
    }
}
