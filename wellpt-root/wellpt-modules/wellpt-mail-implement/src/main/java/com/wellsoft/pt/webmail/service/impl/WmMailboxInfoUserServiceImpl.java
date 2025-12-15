package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailRecentContactDto;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoUserDao;
import com.wellsoft.pt.webmail.entity.*;
import com.wellsoft.pt.webmail.enums.WmMailboxInfoStatusEnum;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.ExtendsMailContactBookHolder;
import com.wellsoft.pt.webmail.support.WmMailUtils;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
@Service
public class WmMailboxInfoUserServiceImpl extends AbstractJpaServiceImpl<WmMailboxInfoUser, WmMailboxInfoUserDao, String> implements WmMailboxInfoUserService {

    private static ConcurrentLinkedQueue<String> lockedList = new ConcurrentLinkedQueue<>();
    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;
    @Autowired
    private WmMailUserService wmMailUserService;
    @Resource
    private WmMailRecentContactService wmMailRecentContactService;
    @Autowired
    private WmMailConfigService wmMailConfigService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private ExtendsMailContactBookHolder extendsMailContactBookHolder;
    @Autowired
    private MultiGroupTreeNodeService multiGroupTreeNodeService;
    @Autowired
    private WmMailUseCapacityService wmMailUseCapacityService;
    @Autowired
    private WmMailboxInfoStatusService wmMailboxInfoStatusService;
    @Autowired
    private WmWebmailOutboxService wmWebmailOutboxService;

    @Override
    @Transactional
    public int receiveFailMail(String userId) {
        WmMailboxInfoUser example = new WmMailboxInfoUser();
        example.setStatus(WmWebmailConstants.STATUS_RECEIVE_FAIL);
        example.setUserId(userId);
        List<WmMailboxInfoUser> userList = this.listByEntity(example);
        int success = 0;
        for (WmMailboxInfoUser infoUser : userList) {
            WmMailboxInfo mailboxInfo = wmMailboxInfoService.getOne(infoUser.getMailInfoUuid());
            //占用收件箱的空间
            if (wmMailUseCapacityService.updateUseCapacity(mailboxInfo.getMailSize(), userId, SpringSecurityUtils.getCurrentUserUnitId(), WmWebmailConstants.INBOX) == 0) {
                continue;
            }
            infoUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
            this.update(infoUser);
            success++;
        }
        return userList.size() - success;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#getUnreadCount(java.lang.String)
     */
    @Override
    public Long getUnreadCount(String userId) {
        WmMailboxInfoUser example = new WmMailboxInfoUser();
        example.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        example.setUserId(userId);
        example.setIsRead(Integer.parseInt(WmWebmailConstants.FLAG_UNREAD));
        Long unreadCount = this.dao.countByEntity(example);
        return unreadCount;
    }

    @Override
    public boolean isExistMail(int mid, String userId, String mailAddress) {
        WmMailboxInfoUser querInfoUser = new WmMailboxInfoUser();
        querInfoUser.setUserId(userId);
        querInfoUser.setMid(mid);
        querInfoUser.setMailAddress(mailAddress);
        long count = this.getDao().countByEntity(querInfoUser);
        return count > 0;
    }

    @Override
    public boolean isExistMailPid(String pid, String userId, String mailAddress) {
        WmMailboxInfoUser querInfoUser = new WmMailboxInfoUser();
        querInfoUser.setUserId(userId);
        querInfoUser.setPid(pid);
        querInfoUser.setMailAddress(mailAddress);
        long count = this.getDao().countByEntity(querInfoUser);
        return count > 0;
    }

    @Override
    public List<WmMailboxInfoUser> getInboxList(String mailInfoUuid) {
        Map<String, Object> params = new HashMap<>();
        params.put("mailInfoUuid", mailInfoUuid);
        List<WmMailboxInfoUser> infoUserList = this.listByHQL("from WmMailboxInfoUser where mailInfoUuid=:mailInfoUuid and sendStatus is null ", params);
        return infoUserList;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void syncSendMail(String sendMailUuid) {
        this.sendMailLocked(sendMailUuid);
    }

    @Override
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void asyncSendMail(String sendMailUuid) {
        this.sendMailLocked(sendMailUuid);
    }

    private void sendMailLocked(String sendMailUuid) {
        if (StringUtils.isBlank(sendMailUuid)) {
            return;
        }
        //已存在跳过
        if (lockedList.contains(sendMailUuid)) {
            return;
        }
        try {
            lockedList.add(sendMailUuid);
            WmMailboxInfoUser mailboxInfoUser = this.getOne(sendMailUuid);
            if (mailboxInfoUser == null) {
                Thread.sleep(500);
                mailboxInfoUser = this.getOne(sendMailUuid);
            }
            if (mailboxInfoUser.getSendStatus() == 1) {
                logger.warn("邮件[{}]已发送", sendMailUuid);
                return;
            }
            WmMailboxInfo wmMailbox = wmMailboxInfoService.getOne(mailboxInfoUser.getMailInfoUuid());
            if (wmMailbox == null) {
                logger.error("邮件信息[{}]不存在", mailboxInfoUser.getMailInfoUuid());
                return;
            }
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, mailboxInfoUser.getUserId());
            this.sendMail(mailboxInfoUser, wmMailbox);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            lockedList.remove(sendMailUuid);
        }
    }

    private void sendMail(WmMailboxInfoUser mailboxInfoUser, WmMailboxInfo wmMailbox) {
        mailboxInfoUser.setStatus(WmWebmailConstants.STATUS_SEND_SUCCESS);

        List<WmMailboxInfoStatus> infoStatusList = new ArrayList<>();
        if (mailboxInfoUser.getSendStatus() == 0) {
            //先解析
            this.resolveRecipients(mailboxInfoUser, wmMailbox, infoStatusList);
            if (mailboxInfoUser.getSendStatus() != 4) {
                this.update(mailboxInfoUser);
                wmMailboxInfoService.update(wmMailbox);
                //失败 发送提醒邮件
                this.sendReminderMail(mailboxInfoUser, wmMailbox, infoStatusList);
                return;
            }
        } else {
            infoStatusList = wmMailboxInfoStatusService.listByMialInfoUuid(wmMailbox.getUuid());
        }
        //发送次数+1
        mailboxInfoUser.setSendCount(mailboxInfoUser.getSendCount() == null ? 1 : mailboxInfoUser.getSendCount() + 1);

        Map<String, List<WmMailboxInfoStatus>> realSendingAddress = new HashMap<>();
        for (WmMailboxInfoStatus infoStatus : infoStatusList) {
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.ToBeSent.ordinal()) {
                if (StringUtils.isEmpty(infoStatus.getUserId())) {
                    if (wmMailbox.getIsPublicEmail()) {
                        this.putRealSendMap(realSendingAddress, infoStatus);
                    } else {
                        infoStatus.setStatus(WmMailboxInfoStatusEnum.ThePublicMailboxIsNotOpened.ordinal());
                    }
                } else {
                    if (wmMailbox.getIsPublicEmail() && wmMailbox.getKeepOnServer()) {
                        this.putRealSendMap(realSendingAddress, infoStatus);
                    } else {
                        infoStatus.setStatus(WmMailboxInfoStatusEnum.HasBeenSent.ordinal());
                    }
                }
            }
        }
        if (realSendingAddress.size() > 0) {
            this.sendMail(mailboxInfoUser, wmMailbox, realSendingAddress);
        }
        Set<String> excludeUserIdSet = new HashSet<>();
        this.sendMail(mailboxInfoUser, wmMailbox, infoStatusList, excludeUserIdSet);
    }

    private void sendMail(WmMailboxInfoUser mailboxInfoUser, WmMailboxInfo wmMailbox, List<WmMailboxInfoStatus> infoStatusList, Set<String> excludeUserIdSet) {
        wmMailboxInfoStatusService.saveAll(infoStatusList);
        //最近联系人
        List<WmMailRecentContactDto> dtoList = new ArrayList<>();
        //关联收件人
        List<WmMailboxInfoUser> toUserList = new ArrayList<>();
        Date currentTime = new Date();
        boolean successFlg = true;
        for (WmMailboxInfoStatus infoStatus : infoStatusList) {
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.HasBeenSent.ordinal() || infoStatus.getStatus() == WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal()) {
                if (StringUtils.isNotBlank(infoStatus.getUserId())) {
                    if (!excludeUserIdSet.contains(infoStatus.getUserId())) {
                        excludeUserIdSet.add(infoStatus.getUserId());

                        WmMailboxInfoUser toUser = new WmMailboxInfoUser();
                        toUser.setUserId(infoStatus.getUserId());
                        toUser.setUserName(infoStatus.getMailName());
                        toUser.setMailAddress(infoStatus.getMailAddress());
                        toUser.setSystemUnitId(infoStatus.getSystemUnitId());
                        toUser.setMailboxName(WmWebmailConstants.INBOX);
                        toUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_UNREAD));
                        toUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
                        toUser.setMailInfoUuid(wmMailbox.getUuid());
                        toUser.setReadReceiptStatus(wmMailbox.getReadReceiptStatus());
                        int row = wmMailUseCapacityService.updateUseCapacity(wmMailbox.getMailSize(), toUser.getUserId(), toUser.getSystemUnitId(), WmWebmailConstants.INBOX);
                        if (row == 0) {
                            //接收失败（空间不足）
                            toUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_FAIL);
                        }
                        toUserList.add(toUser);
                        WmMailRecentContactDto contactDto = new WmMailRecentContactDto(mailboxInfoUser.getUserId(),
                                mailboxInfoUser.getSystemUnitId(),
                                toUser.getUserId(), toUser.getUserName(), currentTime);
                        dtoList.add(contactDto);
                    }
                } else {
                    if (!excludeUserIdSet.contains(infoStatus.getUserId())) {
                        excludeUserIdSet.add(infoStatus.getMailAddress());

                        WmMailRecentContactDto contactDto = new WmMailRecentContactDto(mailboxInfoUser.getUserId(),
                                mailboxInfoUser.getSystemUnitId(),
                                infoStatus.getMailAddress(), infoStatus.getMailName(), currentTime);
                        dtoList.add(contactDto);
                    }
                }
            } else {
                successFlg = false;
            }
        }
        //保存关联收件人
        this.saveAll(toUserList);
        //保存最近联系人
        wmMailRecentContactService.saveRecentContact(dtoList);
        if (successFlg) {
            //发送成功
            mailboxInfoUser.setSendStatus(1);
        } else {
            //发送失败
            mailboxInfoUser.setSendStatus(3);
        }
        this.update(mailboxInfoUser);
        wmMailboxInfoService.update(wmMailbox);
        if (mailboxInfoUser.getSendStatus() == 3) {
            //失败 发送提醒邮件
            this.sendReminderMail(mailboxInfoUser, wmMailbox, infoStatusList);
        }
    }

    private void sendReminderMail(WmMailboxInfoUser infoUser, WmMailboxInfo wmMailbox, List<WmMailboxInfoStatus> infoStatusList) {
        WmMailboxInfo mailboxInfo = new WmMailboxInfo();
        mailboxInfo.setToUserName(wmMailbox.getFromUserName());
        mailboxInfo.setToMailAddress(wmMailbox.getFromMailAddress());
        mailboxInfo.setFromUserName("系统");
        mailboxInfo.setIsPublicEmail(false);
        mailboxInfo.setKeepOnServer(false);
        mailboxInfo.setSendTime(new Date());
        mailboxInfo.setSubject("投递失败提醒：" + wmMailbox.getSubject());
        mailboxInfo.setContent(WmMailUtils.failureReport(infoUser, wmMailbox, infoStatusList));
        mailboxInfo.setPriority(3);
        mailboxInfo.setReadReceiptStatus(0);
        long mailSize = wmWebmailOutboxService.calculateWmMailSize(mailboxInfo);
        mailboxInfo.setMailSize(mailSize);

        wmMailboxInfoService.save(mailboxInfo);

        WmMailboxInfoUser toUser = new WmMailboxInfoUser();
        toUser.setUserId(infoUser.getUserId());
        toUser.setUserName(infoUser.getUserName());
        toUser.setMailAddress(infoUser.getMailAddress());
        toUser.setSystemUnitId(infoUser.getSystemUnitId());
        toUser.setMailboxName(WmWebmailConstants.INBOX);
        toUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_UNREAD));
        toUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        toUser.setReadReceiptStatus(0);
        toUser.setMailInfoUuid(mailboxInfo.getUuid());
        int row = wmMailUseCapacityService.updateUseCapacity(wmMailbox.getMailSize(), toUser.getUserId(), toUser.getSystemUnitId(), WmWebmailConstants.INBOX);
        if (row == 0) {
            //接收失败（空间不足）
            toUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_FAIL);
        }
        this.save(toUser);
    }


    private void putRealSendMap(Map<String, List<WmMailboxInfoStatus>> realSendingAddress, WmMailboxInfoStatus infoStatus) {
        List<WmMailboxInfoStatus> realSendList = realSendingAddress.get(infoStatus.getMailAddress());
        if (realSendList == null) {
            realSendList = new ArrayList<>();
            realSendingAddress.put(infoStatus.getMailAddress(), realSendList);
        }
        realSendList.add(infoStatus);
    }


    private void resolveRecipients(WmMailboxInfoUser mailboxInfoUser, WmMailboxInfo wmMailbox, List<WmMailboxInfoStatus> infoStatusList) {
        try {
            if (wmMailbox.getIsPublicEmail() == null) {
                WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(mailboxInfoUser.getSystemUnitId());
                //默认非公网邮箱
                wmMailbox.setIsPublicEmail(configEntity.getIsPublicEmail() == null ? false : configEntity.getIsPublicEmail());
                //默认不存储到邮件服务
                wmMailbox.setKeepOnServer(configEntity.getKeepOnServer() == null ? false : configEntity.getKeepOnServer());
            }
            if (StringUtils.isNotBlank(wmMailbox.getToMailAddress()) && StringUtils.isBlank(wmMailbox.getToUserName())) {
                wmMailbox.setToUserName(this.getEleIdName(wmMailbox.getToMailAddress()));
            }
            if (StringUtils.isNotBlank(wmMailbox.getCcMailAddress()) && StringUtils.isBlank(wmMailbox.getCcUserName())) {
                wmMailbox.setCcUserName(this.getEleIdName(wmMailbox.getCcMailAddress()));
            }
            if (StringUtils.isNotBlank(wmMailbox.getBccMailAddress()) && StringUtils.isBlank(wmMailbox.getBccUserName())) {
                wmMailbox.setBccUserName(this.getEleIdName(wmMailbox.getBccMailAddress()));
            }
            // 确保文件名称名称与ID一致
            if (StringUtils.isBlank(wmMailbox.getRepoFileNames()) && !StringUtils.isBlank(wmMailbox.getRepoFileUuids())) {
                wmMailbox.setRepoFileNames(WmMailUtils.extractFileNameAsString(wmMailbox.getRepoFileUuids()));
            }
            if (CollectionUtils.isEmpty(infoStatusList)) {
                this.convertInfoStatusList(wmMailbox, mailboxInfoUser.getUuid(), infoStatusList);
            }
        } catch (Exception e) {
            //解析失败
            mailboxInfoUser.setSendStatus(2);
            mailboxInfoUser.setFailMsg(e.getMessage());
            return;
        }
        if (infoStatusList.size() == 0) {
            //解析失败
            mailboxInfoUser.setSendStatus(2);
            mailboxInfoUser.setFailMsg("未找到具体收件人");
            return;
        }
        mailboxInfoUser.setSendStatus(4);
    }

    private void convertInfoStatusList(WmMailboxInfo mailboxInfo, String infoUserUuid, List<WmMailboxInfoStatus> infoStatusList) {
        if (StringUtils.isNotBlank(mailboxInfo.getToMailAddress())) {
            infoStatusList.addAll(this.getInfoStatusList(mailboxInfo.getUuid(), infoUserUuid, mailboxInfo.getToMailAddress(), mailboxInfo.getToUserName(), 1));
        }
        if (StringUtils.isNotBlank(mailboxInfo.getCcMailAddress())) {
            infoStatusList.addAll(this.getInfoStatusList(mailboxInfo.getUuid(), infoUserUuid, mailboxInfo.getCcMailAddress(), mailboxInfo.getCcUserName(), 2));
        }
        if (StringUtils.isNotBlank(mailboxInfo.getBccMailAddress())) {
            infoStatusList.addAll(this.getInfoStatusList(mailboxInfo.getUuid(), infoUserUuid, mailboxInfo.getBccMailAddress(), mailboxInfo.getBccUserName(), 3));
        }
    }

    private List<WmMailboxInfoStatus> getInfoStatusList(String infoUuid, String infoUserUuid, String mailAddress, String mailName, int type) {
        List<WmMailboxInfoStatus> infoStatusList = new ArrayList<>();
        Set<String> excludeDuplSet = new HashSet<>();
        String[] eleIdList = StringUtils.split(mailAddress, Separator.SEMICOLON.getValue());
        String[] eleNameList = StringUtils.split(mailName, Separator.SEMICOLON.getValue());
        for (int i = 0; i < eleIdList.length; i++) {
            String eleId = eleIdList[i];
            String eleIdName = eleNameList[i];
            // 收件人本身就是个邮件地址
            if (eleId.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                String address = WmMailUtils.extractAddress(eleId);
                WmMailboxInfoStatus infoStatus = this.getWmMailboxInfoStatus(infoUuid, infoUserUuid, type, eleIdName, address);
                this.addInfoStatus(infoStatusList, excludeDuplSet, infoStatus);
            } else if (extendsMailContactBookHolder.isExtendsMailAddressId(eleId)) {
                List<Address> addressList = extendsMailContactBookHolder.getMailAddress(eleId);
                if (addressList != null) {
                    for (Address address : addressList) {
                        String name = WmMailUtils.extractUserName(address);
                        String userAddress = WmMailUtils.extractAddress(address);
                        WmMailboxInfoStatus infoStatus = this.getWmMailboxInfoStatus(infoUuid, infoUserUuid, type, name, userAddress);
                        this.addInfoStatus(infoStatusList, excludeDuplSet, infoStatus);
                    }
                }
            } else {
                List<String> orgIdList = new ArrayList<>();
                if (eleId.startsWith(IdPrefix.MULTI_GROUP.getValue()) ||
                        eleId.startsWith(IdPrefix.MULTI_GROUP_CATEGORY.getValue()) ||
                        eleId.startsWith(IdPrefix.SYSTEM_UNIT.getValue())) {
                    orgIdList.addAll(multiGroupTreeNodeService.getOrgVersionIdById(eleId));
                } else {
                    orgIdList.add(eleId);
                }
                Map<String, String> userMap = orgApiFacade.getUsersByOrgIds(orgIdList);
                List<WmMailUserEntity> userEntityList = wmMailUserService.querByUserIds(userMap.keySet());
                Map<String, WmMailUserEntity> userEntityMap = userEntityList.stream().filter(WmMailUserEntity::getIsInnerUser)
                        .collect(Collectors.toMap(WmMailUserEntity::getUserId, wmMailUserEntity -> wmMailUserEntity));

                for (String userId : userMap.keySet()) {
                    WmMailUserEntity userEntity = userEntityMap.get(userId);
                    WmMailboxInfoStatus infoStatus = new WmMailboxInfoStatus();
                    infoStatus.setMailInfoUuid(infoUuid);
                    infoStatus.setMailInfoUserUuid(infoUserUuid);
                    infoStatus.setRecipientType(type);
                    infoStatus.setUserId(userId);
                    infoStatus.setMailName(userMap.get(userId));
                    infoStatus.setStatus(WmMailboxInfoStatusEnum.ToBeSent.ordinal());
                    if (userEntity != null) {
                        infoStatus.setMailName(userEntity.getUserName());
                        infoStatus.setSystemUnitId(userEntity.getSystemUnitId());
                        infoStatus.setMailAddress(userEntity.getMailAddress());
                    }
                    if (StringUtils.isBlank(infoStatus.getMailAddress())) {
                        infoStatus.setStatus(WmMailboxInfoStatusEnum.AddressDoesNotExist.ordinal());
                    }
                    this.addInfoStatus(infoStatusList, excludeDuplSet, infoStatus);
                }
            }
        }
        return infoStatusList;
    }

    private void addInfoStatus(List<WmMailboxInfoStatus> infoStatusList, Set<String> excludeDuplSet, WmMailboxInfoStatus infoStatus) {
        String key = infoStatus.getUserId();
        if (StringUtils.isBlank(key)) {
            key = infoStatus.getMailAddress();
        }
        if (!excludeDuplSet.contains(key)) {
            excludeDuplSet.add(key);
            infoStatusList.add(infoStatus);
        }

    }

    private WmMailboxInfoStatus getWmMailboxInfoStatus(String infoUuid, String infoUserUuid, int type, String eleIdName, String address) {
        WmMailboxInfoStatus infoStatus = new WmMailboxInfoStatus();
        infoStatus.setMailInfoUuid(infoUuid);
        infoStatus.setMailInfoUserUuid(infoUserUuid);
        infoStatus.setRecipientType(type);
        infoStatus.setMailName(eleIdName);
        infoStatus.setMailAddress(address);
        infoStatus.setStatus(WmMailboxInfoStatusEnum.ToBeSent.ordinal());
        WmMailUserEntity wmMailUserEntity = wmMailUserService.getByMailAddress(address);
        if (wmMailUserEntity != null && wmMailUserEntity.getIsInnerUser()) {
            infoStatus.setUserId(wmMailUserEntity.getUserId());
            infoStatus.setMailName(wmMailUserEntity.getUserName());
            infoStatus.setSystemUnitId(wmMailUserEntity.getSystemUnitId());
        }
        return infoStatus;
    }


    private String getEleIdName(String mailAddress) {
        String[] eleIdList = StringUtils.split(mailAddress, Separator.SEMICOLON.getValue());
        List<String> eleNameList = new ArrayList<>();
        for (String eleId : eleIdList) {
            // 收件人本身就是个邮件地址
            if (eleId.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                String address = WmMailUtils.extractAddress(eleId);
                String name = WmMailUtils.extractUserName(eleId);
                WmMailUserEntity example = new WmMailUserEntity();
                example.setMailAddress(address);
                List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
                if (wmMailUsers.isEmpty()) {
                    eleNameList.add(name);
                } else {
                    eleNameList.add(wmMailUsers.get(0).getUserName());
                }
                // 收件人是对应的节点
            } else if (extendsMailContactBookHolder.isExtendsMailAddressId(eleId)) {
                eleNameList.add(eleId);
            } else {
                String name = orgApiFacade.getNameByOrgEleId(eleId);
                eleNameList.add(name);
            }
        }
        return StringUtils.join(eleNameList, Separator.SEMICOLON.getValue());
    }

    private Message getMessage(Session session, WmMailboxInfo wmMailbox, String domain) throws Exception {
        // 创建MimeMessage实例对象
        MimeMessage message = new MimeMessage(session);
        // 设置主题
        message.setSubject(wmMailbox.getSubject());
        // 设置发送人
        message.setFrom(WmMailUtils.getMailAddress(wmMailbox.getFromUserName(), wmMailbox.getFromMailAddress()));
        // 设置收件人
        if (StringUtils.isNotBlank(wmMailbox.getToMailAddress())) {
            List<Address> addressList = this.getAddressList(wmMailbox.getToMailAddress(), wmMailbox.getToUserName(), domain);
            message.setRecipients(Message.RecipientType.TO, addressList.toArray(new Address[addressList.size()]));
        }
        if (StringUtils.isNotBlank(wmMailbox.getCcMailAddress())) {
            List<Address> addressList = this.getAddressList(wmMailbox.getCcMailAddress(), wmMailbox.getCcUserName(), domain);
            message.setRecipients(Message.RecipientType.CC, addressList.toArray(new Address[addressList.size()]));
        }
        if (StringUtils.isNotBlank(wmMailbox.getBccMailAddress())) {
            List<Address> addressList = this.getAddressList(wmMailbox.getBccMailAddress(), wmMailbox.getBccUserName(), domain);
            message.setRecipients(Message.RecipientType.BCC, addressList.toArray(new Address[addressList.size()]));
        }
        if (wmMailbox.getPriority() != null) {
            // 设置优先级(1:紧急 3:普通 5:低)
            message.setHeader(WmWebmailConstants.HEADER_PRIORITY, wmMailbox.getPriority() + "");
        } else {
            // 设置优先级(3:普通)
            message.setHeader(WmWebmailConstants.HEADER_PRIORITY, WmWebmailConstants.PRIORITY_NORMAL);
        }
        // 设置发送时间
        message.setSentDate(wmMailbox.getSendTime());
        // 设置来源信id
        message.setHeader(WmWebmailConstants.HEADER_FROM_MAIL_ID, wmMailbox.getUuid());
        message.setHeader(WmWebmailConstants.HEADER_MAIL_SEND_TIMESTAMP, wmMailbox.getSendTime().getTime() + "");//发送时间戳
        message.setHeader(WmWebmailConstants.HEADER_TO_ADDRESS, wmMailbox.getToMailAddress());
        if (StringUtils.isNotBlank(wmMailbox.getCcMailAddress())) {
            message.setHeader(WmWebmailConstants.HEADER_CC_ADDRESS, wmMailbox.getCcMailAddress());
        }
        if (StringUtils.isNotBlank(wmMailbox.getBccMailAddress())) {
            message.setHeader(WmWebmailConstants.HEADER_BCC_ADDRESS, wmMailbox.getBccMailAddress());
        }
        if (StringUtils.isNotBlank(wmMailbox.getToUserName())) {
            message.setHeader(WmWebmailConstants.HEADER_TO_NAME, MimeUtility.encodeText(wmMailbox.getToUserName()));
        }
        if (StringUtils.isNotBlank(wmMailbox.getCcUserName())) {
            message.setHeader(WmWebmailConstants.HEADER_CC_NAME, MimeUtility.encodeText(wmMailbox.getCcUserName()));
        }
        if (StringUtils.isNotBlank(wmMailbox.getBccUserName())) {
            message.setHeader(WmWebmailConstants.HEADER_BCC_NAME, MimeUtility.encodeText(wmMailbox.getBccUserName()));
        }
        // 要求阅读回执(收件人阅读邮件时会提示回复发件人,表明邮件已收到,并已阅读)
        if (wmMailbox.getReadReceiptStatus() != null && wmMailbox.getReadReceiptStatus() == 1) {
            message.setHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT, wmMailbox.getFromMailAddress());
        }
        // 设置内容
        message.setContent(WmMailUtils.getContent(wmMailbox));
        // 保存邮件内容修改
        message.saveChanges();
        return message;
    }

    private void sendMail(WmMailboxInfoUser mailboxInfoUser, WmMailboxInfo wmMailbox, Map<String, List<WmMailboxInfoStatus>> realSendingAddress) {
        String fromMailAddress = wmMailbox.getFromMailAddress();
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(mailboxInfoUser.getSystemUnitId());
        List<WmMailUserEntity> sendMailUserList = wmMailUserService.getMailUser(mailboxInfoUser.getUserId(), fromMailAddress);
        WmMailUserEntity sendMailUser = sendMailUserList.get(0);
        // 发送邮件地址
        Session session = WmMailUtils.getSendSession(sendMailUser);
        //发送邮件
        Transport transport = WmMailUtils.getTransport(session, sendMailUser);
        try {
            Message message = getMessage(session, wmMailbox, configEntity.getDomain());
            transport.connect();
            Collection<List<WmMailboxInfoStatus>> infoStatusList = realSendingAddress.values();
            List<Address> addresses = new ArrayList<>();
            for (List<WmMailboxInfoStatus> infoStatus : infoStatusList) {
                addresses.add(WmMailUtils.getMailAddress(infoStatus.get(0).getMailName(), infoStatus.get(0).getMailAddress()));
            }
            transport.sendMessage(message, addresses.toArray(new Address[addresses.size()]));
            for (List<WmMailboxInfoStatus> infoStatus : realSendingAddress.values()) {
                for (WmMailboxInfoStatus status : infoStatus) {
                    status.setStatus(WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal());
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e instanceof SendFailedException) {
                logger.error("邮件信息【{}】发送异常 SendFailedException:{}", mailboxInfoUser.getUuid(), errorMsg);
                SendFailedException sendFail = (SendFailedException) e;
                this.updateStatus(realSendingAddress, sendFail.getInvalidAddresses(), WmMailboxInfoStatusEnum.InvalidEmailAddress.ordinal());
                this.updateStatus(realSendingAddress, sendFail.getValidSentAddresses(), WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal());
                if (sendFail.getValidUnsentAddresses() != null && sendFail.getValidUnsentAddresses().length > 0) {
                    Map<String, List<WmMailboxInfoStatus>> sendAddress = new HashMap<>();
                    for (Address address : sendFail.getValidUnsentAddresses()) {
                        String addressKey = WmMailUtils.extractAddress(address);
                        sendAddress.put(addressKey, realSendingAddress.get(addressKey));
                    }
                    this.sendMail(mailboxInfoUser, wmMailbox, sendAddress);
                }
            } else {
                logger.error("邮件信息【{}】发送异常:", mailboxInfoUser.getUuid(), e);
                for (List<WmMailboxInfoStatus> infoStatus : realSendingAddress.values()) {
                    for (WmMailboxInfoStatus status : infoStatus) {
                        status.setStatus(WmMailboxInfoStatusEnum.MailServiceException.ordinal());
                    }
                }
            }
        } finally {
            WmMailUtils.close(transport);
        }
    }

    private void updateStatus(Map<String, List<WmMailboxInfoStatus>> realSendingAddress, Address[] addresses, int status) {
        if (addresses != null && addresses.length > 0) {
            for (Address address : addresses) {
                for (WmMailboxInfoStatus infoStatus : realSendingAddress.get(WmMailUtils.extractAddress(address))) {
                    infoStatus.setStatus(status);
                }
            }
        }
    }

    private List<Address> getAddressList(String address, String userName, String domain) throws Exception {
        List<Address> addressList = new ArrayList<>();
        List<String> eleIdList = Arrays.asList(StringUtils.split(address, Separator.SEMICOLON.getValue()));
        List<String> eleNameList = Arrays.asList(StringUtils.split(userName, Separator.SEMICOLON.getValue()));
        for (int i = 0; i < eleIdList.size(); i++) {
            String eleId = eleIdList.get(i);
            String eleName = eleNameList.get(i);
            if (eleId.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                addressList.add(WmMailUtils.getMailAddress(eleName, WmMailUtils.extractAddress(eleId)));
            } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
                WmMailUserEntity mailUserEntity = wmMailUserService.getInnerMailUser(eleId);
                addressList.add(WmMailUtils.getMailAddress(mailUserEntity.getUserName(), mailUserEntity.getMailAddress()));
            } else {
                addressList.add(WmMailUtils.getMailAddress(eleName, eleId + WmWebmailConstants.MAIL_SEPARATOR + domain));
            }
        }
        return addressList;
    }


    @Override
    public long capacityUsed(String userId, String mailbox) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("mailbox", mailbox);
        params.put("mailbox", mailbox);
        List<Number> capacityUsed = this.getDao().listCharSequenceBySQL("select sum(b.mail_size) from wm_mailbox_info_user a left join wm_mailbox_info b on a.mail_info_uuid = b.uuid  " +
                "where a.user_id =:userId and a.mailbox_name=:mailbox and a.status != -2 ", params);
        if (capacityUsed.size() > 0) {
            return capacityUsed.get(0) == null ? 0 : capacityUsed.get(0).longValue();
        }
        return 0;
    }

    @Override
    @Transactional
    public void resend(String mailboxUuid) {
        WmMailboxInfoUser mailboxInfoUser = this.getOne(mailboxUuid);
        WmMailboxInfo wmMailbox = wmMailboxInfoService.getOne(mailboxInfoUser.getMailInfoUuid());
        //非发送失败状态不处理
        if (mailboxInfoUser.getSendStatus() != 3) {
            return;
        }
        List<WmMailboxInfoStatus> infoStatusList = wmMailboxInfoStatusService.listByMialInfoUuid(wmMailbox.getUuid());
        if (CollectionUtils.isEmpty(infoStatusList)) {
            return;
        }
        //公网邮箱配置
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        //默认非公网邮箱
        boolean isPublicEmail = configEntity.getIsPublicEmail() == null ? false : configEntity.getIsPublicEmail();
        //默认不存储到邮件服务
        boolean keepOnServer = configEntity.getKeepOnServer() == null ? false : configEntity.getKeepOnServer();
        wmMailbox.setIsPublicEmail(isPublicEmail);
        wmMailbox.setKeepOnServer(keepOnServer);
        //发送次数+1
        mailboxInfoUser.setSendCount(mailboxInfoUser.getSendCount() == null ? 1 : mailboxInfoUser.getSendCount() + 1);

        Map<String, List<WmMailboxInfoStatus>> realSendingAddress = new HashMap<>();
        Set<String> excludeUserIdSet = new HashSet<>();
        for (WmMailboxInfoStatus infoStatus : infoStatusList) {
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.ToBeSent.ordinal() || infoStatus.getStatus() == WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal()) {
                continue;
            }
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.HasBeenSent.ordinal()) {
                excludeUserIdSet.add(infoStatus.getUserId());
            }
            if (StringUtils.isNotEmpty(infoStatus.getUserId()) && infoStatus.getStatus() == WmMailboxInfoStatusEnum.AddressDoesNotExist.ordinal()) {
                WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(infoStatus.getUserId());
                if (userEntity != null && StringUtils.isNotEmpty(userEntity.getMailAddress())) {
                    infoStatus.setMailAddress(userEntity.getMailAddress());
                }
            }
            if (StringUtils.isEmpty(infoStatus.getUserId())) {
                if (wmMailbox.getIsPublicEmail()) {
                    this.putRealSendMap(realSendingAddress, infoStatus);
                } else {
                    infoStatus.setStatus(WmMailboxInfoStatusEnum.ThePublicMailboxIsNotOpened.ordinal());
                }
            } else if (StringUtils.isNotEmpty(infoStatus.getMailAddress())) {
                if (wmMailbox.getIsPublicEmail() && wmMailbox.getKeepOnServer()) {
                    this.putRealSendMap(realSendingAddress, infoStatus);
                } else {
                    infoStatus.setStatus(WmMailboxInfoStatusEnum.HasBeenSent.ordinal());
                }
            }
        }
        if (realSendingAddress.size() > 0) {
            this.sendMail(mailboxInfoUser, wmMailbox, realSendingAddress);
        }
        this.sendMail(mailboxInfoUser, wmMailbox, infoStatusList, excludeUserIdSet);
    }
}
