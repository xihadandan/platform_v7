/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.mail.support.James3Constant;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.*;
import com.wellsoft.pt.webmail.entity.*;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.ExtendsMailContactBookHolder;
import com.wellsoft.pt.webmail.support.WmMailUtils;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 邮件服务实现
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
@Service
@Transactional
public class WmWebmailServiceImpl extends BaseServiceImpl implements WmWebmailService {

    @Autowired
    private WmMailUserService wmMailUserService;
    @Autowired
    private WmWebmailOutboxService wmWebmailOutboxService;
    @Autowired
    private WmMailboxService wmMailboxService;
    @Resource
    private WmMailRecentContactService wmMailRecentContactService;
    @Autowired
    private WmMailConfigService wmMailConfigService;
    @Autowired
    private WmMailUseCapacityService wmMailUseCapacityService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private ExtendsMailContactBookHolder extendsMailContactBookHolder;
    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;
    @Autowired
    private MultiGroupTreeNodeService multiGroupTreeNodeService;
    @Autowired
    private HibernateTransactionManager transactionManager;
    @Resource
    private WmMailRevocationService wmMailRevocationService;

    @Override
    public synchronized void oldToNew(int pageSize) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
        TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
        List<WmMailbox> list = this.queryList(pageSize);
        if (list.size() == 0) {
            logger.info("执行完毕");
            return;
        }
        try {
            Map<String, WmMailbox> mailboxMap = list.stream().collect(Collectors.toMap(WmMailbox::getUuid, wmMailbox -> wmMailbox));
            while (mailboxMap.size() > 0) {
                WmMailbox fromMail = mailboxMap.get(mailboxMap.keySet().iterator().next());
                WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(fromMail.getUuid());
                if (wmMailboxInfo != null) {
                    mailboxMap.remove(fromMail.getUuid());
                    continue;
                }
                if (StringUtils.isNotBlank(fromMail.getFromMailUuid())) {
                    wmMailboxInfo = wmMailboxInfoService.getOne(fromMail.getFromMailUuid());
                    if (wmMailboxInfo != null) {
                        mailboxMap.remove(wmMailboxInfo.getUuid());
                        continue;
                    }
                    WmMailbox wmMailbox = wmMailboxService.getOne(fromMail.getFromMailUuid());
                    if (wmMailbox != null) {
                        fromMail = wmMailbox;
                    }
                }
                List<WmMailbox> toMailList = wmMailboxService.getDao().listByFieldEqValue("fromMailUuid", fromMail.getUuid());
                Set<String> uuidSet = this.addWmMailBoxInfo(fromMail, toMailList);
                this.updateSendCount(uuidSet);
                for (String key : uuidSet) {
                    mailboxMap.remove(key);
                }
            }
            transactionManager.commit(status);
            this.oldToNew(pageSize);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private Set<String> addWmMailBoxInfo(WmMailbox fromMail, List<WmMailbox> toMailList) {
        Set<String> uuidSet = new HashSet<>();

        WmMailboxInfo fromInfo = new WmMailboxInfo();
        BeanUtils.copyProperties(fromMail, fromInfo);
        //历史旧数据没有 实际接收人邮件地址 需要解析处理
        if (toMailList.size() > 0 && StringUtils.isBlank(fromInfo.getActualToMailAddress())) {
            this.actualToMail(fromInfo, toMailList);
        }
        wmMailboxInfoService.getDao().getSession().merge(fromInfo);

        WmMailboxInfoUser fromInfoUser = this.getWmMailboxInfoUser(fromMail, fromInfo.getUuid());
        wmMailboxInfoUserService.getDao().getSession().merge(fromInfoUser);

        uuidSet.add(fromInfo.getUuid());

        for (WmMailbox wmMailbox : toMailList) {
            WmMailboxInfoUser infoUser = this.getWmMailboxInfoUser(wmMailbox, fromInfo.getUuid());
            //未读 查询是否有退回记录
            if (infoUser.getIsRead() == 0) {
                WmMailRevocationDto paramDto = new WmMailRevocationDto(infoUser.getUserId(), fromInfo.getUuid(), true);
                List<WmMailRevocationEntity> existRevocations = wmMailRevocationService.queryMailRevocation(paramDto);
                if (CollectionUtils.isNotEmpty(existRevocations)) {// 有撤回记录
                    if (existRevocations.get(0).getIsRevokeSuccess()) {
                        infoUser.setRevokeStatus(1);
                    } else {
                        infoUser.setRevokeStatus(0);
                    }
                }
            }
            wmMailboxInfoUserService.getDao().getSession().merge(infoUser);
            uuidSet.add(infoUser.getUuid());
        }
        return uuidSet;
    }

    private void actualToMail(WmMailboxInfo wmMailbox, List<WmMailbox> toMailList) {
        Set<String> userIds = new HashSet<>();
        for (WmMailbox mailbox : toMailList) {
            userIds.add(mailbox.getUserId());
        }
        if (StringUtils.isNotBlank(wmMailbox.getToMailAddress())) {
            List<String> userIdList = getStrings(wmMailbox.getToMailAddress(), userIds);
            wmMailbox.setActualToMailAddress(StringUtils.join(userIdList, Separator.SEMICOLON.getValue()));
        }
        if (StringUtils.isNotBlank(wmMailbox.getCcMailAddress())) {
            List<String> userIdList = getStrings(wmMailbox.getCcMailAddress(), userIds);
            wmMailbox.setActualCcMailAddress(StringUtils.join(userIdList, Separator.SEMICOLON.getValue()));
        }
        if (StringUtils.isNotBlank(wmMailbox.getBccMailAddress())) {
            List<String> userIdList = getStrings(wmMailbox.getBccMailAddress(), userIds);
            wmMailbox.setActualBccMailAddress(StringUtils.join(userIdList, Separator.SEMICOLON.getValue()));
        }
    }

    private List<String> getStrings(String mailAddress, Set<String> userIds) {
        List<WmMailUserAddress> userAddressList = this.toOutAddress(mailAddress);
        Map<String, String> userNameMap = orgApiFacade.getUsersByOrgIds(mailAddress);
        List<String> userIdList = new ArrayList<>();
        for (String s : userNameMap.keySet()) {
            if (userIds.contains(s)) {
                userIdList.add(s);
            }
        }
        for (WmMailUserAddress userAddress : userAddressList) {
            userIdList.add(userAddress.getMailAddress());
        }
        return userIdList;
    }

    private List<WmMailUserAddress> toOutAddress(String mailAddress) {
        List<WmMailUserAddress> userAddressList = new ArrayList<>();
        if (StringUtils.isBlank(mailAddress)) {
            return userAddressList;
        }
        String[] eleIdList = StringUtils.split(mailAddress, Separator.SEMICOLON.getValue());
        for (String eleId : eleIdList) {
            // 收件人本身就是个邮件地址
            if (eleId.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                String address = null;
                if (eleId.indexOf("<") != -1 && eleId.endsWith(">")) {
                    int end = eleId.indexOf("<");
                    String name = StringUtils.trim(eleId.substring(0, end));
                    int start = eleId.indexOf("<") + 1;
                    address = eleId.substring(start, eleId.length() - 1);
                } else {
                    address = eleId;
                }
                WmMailUserEntity example = new WmMailUserEntity();
                example.setMailAddress(address);
                List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
                WmMailUserAddress userAddress = new WmMailUserAddress();
                if (wmMailUsers.isEmpty()) {
                    int end = eleId.indexOf(WmWebmailConstants.MAIL_SEPARATOR);
                    String name = StringUtils.trim(eleId.substring(0, end));
                    userAddress.setUserName(name);
                    userAddress.setMailAddress(eleId);

                } else {
                    userAddress.setUserId(wmMailUsers.get(0).getUserId());
                    userAddress.setUserName(wmMailUsers.get(0).getUserName());
                    userAddress.setMailAddress(wmMailUsers.get(0).getMailAddress());
                }
                userAddressList.add(userAddress);
            }
        }
        return userAddressList;
    }

    private WmMailboxInfoUser getWmMailboxInfoUser(WmMailbox fromMail, String mailInfoUuid) {
        WmMailboxInfoUser infoUser = new WmMailboxInfoUser();
        BeanUtils.copyProperties(fromMail, infoUser);
        infoUser.setIsRead(fromMail.getIsRead() == null ? null : Integer.valueOf(fromMail.getIsRead()));
        //历史mid错误，记录为负数
        infoUser.setMid(fromMail.getMid() == null ? null : Integer.valueOf("-" + fromMail.getMid()));
        WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(fromMail.getUserId());
        String mailAddress = null;
        if (userEntity != null) {
            mailAddress = userEntity.getMailAddress();
        } else {
            WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
            MultiOrgUserAccount userAccount = orgApiFacade.getAccountByUserId(fromMail.getUserId());
            // 用户登录名
            String loginName = userAccount.getLoginName();
            // 邮件地址
            mailAddress = loginName.toLowerCase() + "@" + configEntity.getDomain();
        }
        infoUser.setMailAddress(mailAddress);
        infoUser.setMailInfoUuid(mailInfoUuid);
        return infoUser;
    }


    private void updateSendCount(Set<String> uuidSet) {
        if (uuidSet.size() > 0) {
            StringBuilder sbHql = new StringBuilder("update wm_mailbox set send_count = -1 where ");
            Map<String, Object> params = new HashMap<>();
            HqlUtils.appendSql("uuid", params, sbHql, Sets.<Serializable>newHashSet(uuidSet));
            wmMailboxService.getDao().updateBySQL(sbHql.toString(), params);
        }
    }

    private List<WmMailbox> queryList(int pageSize) {
        PagingInfo pagingInfo = new PagingInfo(1, pageSize);
        List<WmMailbox> list = wmMailboxService.listByHQLAndPage("from WmMailbox where sendCount!=-1 or sendCount is null", null, pagingInfo);
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#getWmWebmailBean(java.lang.String, java.lang.String)
     */
    @Override
    public WmWebmailBean getWmWebmailBean(String userId, String mailboxUuid) {
        WmWebmailBean bean = new WmWebmailBean();
        // 获取邮件发件人地址列表
        WmMailUserEntity example = new WmMailUserEntity();
        example.setUserId(userId);
        List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
        List<WmMailUserDto> fromMailAddresses = new ArrayList<WmMailUserDto>();
        for (WmMailUserEntity wmMailUser : wmMailUsers) {
            WmMailUserDto wmMailUserDto = new WmMailUserDto();
            BeanUtils.copyProperties(wmMailUser, wmMailUserDto);
            if (wmMailUser.getIsInnerUser()) {
                wmMailUserDto.setUserName(orgApiFacade.getUserNameById(
                        wmMailUser.getUserId()));
            }
            fromMailAddresses.add(wmMailUserDto);
        }
        bean.setFromMailAddresses(fromMailAddresses);

        // 获取邮件信息
        if (StringUtils.isNotBlank(mailboxUuid)) {
            WmMailbox wmMailbox = wmMailboxService.get(mailboxUuid);
            BeanUtils.copyProperties(wmMailbox, bean, new String[]{"fromMailAddresses"});
            bean.setMailboxUuid(mailboxUuid);
        }
        return bean;
    }

    @Override
    @Transactional
    public ResultMessage receipt(String mailboxUuid, Integer status) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        if (wmMailboxInfoUser.getReadReceiptStatus() == 0) {
            return new ResultMessage("该邮件不需要阅读回执", false);
        }
        if (wmMailboxInfoUser.getReadReceiptStatus() > 1) {
            return new ResultMessage("已处理过回执", false);
        }
        //取消发送回执
        if (status == 3) {
            wmMailboxInfoUser.setReadReceiptStatus(3);
            wmMailboxInfoUserService.save(wmMailboxInfoUser);
            return new ResultMessage();
        } else {
            wmMailboxInfoUser.setReadReceiptStatus(2);
            wmMailboxInfoUserService.save(wmMailboxInfoUser);
            WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
            //发送回执邮件
            WmWebmailBean webmailBean = new WmWebmailBean();
            WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(userId);
            webmailBean.setFromUserName(userEntity.getUserName());
            webmailBean.setFromMailAddress(userEntity.getMailAddress());
            webmailBean.setToUserName(wmMailboxInfo.getFromUserName());
            webmailBean.setToMailAddress(wmMailboxInfo.getFromMailAddress());
            webmailBean.setSubject("已读回执：" + wmMailboxInfo.getSubject());
            String dateStr = DateUtils.formatDateTime(new Date());
            webmailBean.setContent("您的邮件【" + wmMailboxInfo.getSubject() + "】已于" + dateStr + "被【" + userEntity.getUserName() + "】打开。");
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setData(this.send(webmailBean));
            return resultMessage;
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#send(com.wellsoft.pt.webmail.bean.WmWebmailBean)
     */
    @Override
    @Transactional
    public Map<String, String> send(WmWebmailBean webmailBean) {
        WmMailboxInfoUser wmMailboxInfoUser = this.getWmMailboxInfoUser(webmailBean);
        wmMailboxInfoUserService.save(wmMailboxInfoUser);
        wmMailboxInfoUserService.flushSession();
        Map<String, String> dataMap = new HashMap<>();
        String uuid = wmMailboxInfoUser.getUuid();
        dataMap.put("mailboxUuid", uuid);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                wmMailboxInfoUserService.asyncSendMail(uuid);
            }
        });
        return dataMap;
    }

    private WmMailboxInfoUser getWmMailboxInfoUser(WmWebmailBean webmailBean) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        // 发送邮件地址
        String fromMailAddress = webmailBean.getFromMailAddress();
        List<WmMailUserEntity> sendMailUsers = wmMailUserService.getMailUser(userId, fromMailAddress);
        if (sendMailUsers.isEmpty()) {
            throw new RuntimeException("您的邮箱账号异常！请联系管理员！");
        }
        if (StringUtils.isBlank(webmailBean.getToMailAddress())) {
            throw new RuntimeException("收件人不能为空");
        }
        WmMailUserEntity sendMailUser = sendMailUsers.get(0);
        if (StringUtils.isBlank(webmailBean.getFromUserName())) {
            webmailBean.setFromUserName(sendMailUser.getUserName());
        }
        this.verifyAddress(webmailBean.getToMailAddress());
        this.verifyAddress(webmailBean.getCcMailAddress());
        this.verifyAddress(webmailBean.getBccMailAddress());
        StringBuilder sbMsg = new StringBuilder();
        String mailboxUuid = webmailBean.getMailboxUuid();
        WmMailboxInfoUser wmMailboxInfoUser = new WmMailboxInfoUser();
        WmMailboxInfo wmMailboxInfo = new WmMailboxInfo();
        long draftMailSize = 0L;
        String fromMailBox = null;
        if (StringUtils.isNotBlank(mailboxUuid)) {
            wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
            wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
            draftMailSize = wmMailboxInfo.getMailSize();
            fromMailBox = wmMailboxInfoUser.getMailboxName();
        } else {
            fromMailBox = WmWebmailConstants.OUTBOX;
        }
        // 属性复制
        BeanUtils.copyProperties(webmailBean, wmMailboxInfoUser, IdEntity.BASE_FIELDS);
        BeanUtils.copyProperties(webmailBean, wmMailboxInfo, IdEntity.BASE_FIELDS);
        wmMailboxInfoUser.setMailAddress(fromMailAddress);
        //公网邮箱配置
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        //默认非公网邮箱
        boolean isPublicEmail = configEntity.getIsPublicEmail() == null ? false : configEntity.getIsPublicEmail();
        //默认不存储到邮件服务
        boolean keepOnServer = configEntity.getKeepOnServer() == null ? false : configEntity.getKeepOnServer();
        if (isPublicEmail) {
            Session session = null;
            Transport transport = null;
            //公网邮箱 检查邮件服务器链接
            try {
                session = WmMailUtils.getSendSession(sendMailUser);
                transport = WmMailUtils.getTransport(session, sendMailUser);
                transport.connect();
            } catch (Exception e) {
                logger.error("邮箱服务连接异常：{}", Throwables.getStackTraceAsString(e));
                sbMsg.append("邮件服务异常！请联系管理员！</br>");
            } finally {
                WmMailUtils.close(transport);
            }
        } else {
            //非公网邮箱，检查发件人，收件人地址
            List<WmMailConfigEntity> configEntityList = wmMailConfigService.listAll();
            Set<String> domainSet = configEntityList.stream().map(WmMailConfigEntity::getDomain).collect(Collectors.toSet());
            if (this.existExternalMailboxes(fromMailAddress, domainSet)) {
                sbMsg.append("发送邮件地址为公网邮箱！</br>");
            }
            if (this.existExternalMailboxes(webmailBean.getToMailAddress(), domainSet)) {
                sbMsg.append("收件人地址包含公网邮箱！</br>");
            }
            if (this.existExternalMailboxes(webmailBean.getCcMailAddress(), domainSet)) {
                sbMsg.append("抄送人地址包含公网邮箱！</br>");
            }
            if (this.existExternalMailboxes(webmailBean.getBccMailAddress(), domainSet)) {
                sbMsg.append("密送人地址包含公网邮箱！</br>");
            }
        }

        if (StringUtils.isNotBlank(wmMailboxInfo.getContent())) {
            String orginalContent = wmMailboxInfo.getContent();
            // 替换掉信纸、签名等dom标志，防止回复转发时候新邮件的定位问题
            wmMailboxInfo.setContent(
                    orginalContent.replaceAll("well_mail_paper", "").replaceAll("well_signature",
                            "").replaceAll("well_sign_div", ""));
        }
        long mailSize = wmWebmailOutboxService.calculateWmMailSize(wmMailboxInfo);
        if (wmMailUseCapacityService.updateseCapacityTransform(draftMailSize,
                mailSize, fromMailBox,
                WmWebmailConstants.OUTBOX, userId) == 0) {
            sbMsg.append("您的邮箱空间已满！您可通过彻底删除邮件释放邮箱空间，或联系管理员！");
        }
        if (StringUtils.isNotBlank(sbMsg.toString())) {
            throw new RuntimeException(sbMsg.toString());
        }
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isBlank(fromMailAddress)) {
            fromMailAddress = sendMailUser.getMailAddress();
            webmailBean.setFromMailAddress(fromMailAddress);
        }
        wmMailboxInfo.setSendTime(webmailBean.getSendTime() == null ? new Date() : webmailBean.getSendTime());
        wmMailboxInfo.setMailSize(mailSize);
        wmMailboxInfo.setFromUserName(webmailBean.getFromUserName());
        wmMailboxInfo.setIsPublicEmail(isPublicEmail);
        wmMailboxInfo.setKeepOnServer(keepOnServer);
        wmMailboxInfoService.save(wmMailboxInfo);
        // 附件放入夹
        wmWebmailOutboxService.pushFilesToFolder(wmMailboxInfo);

        wmMailboxInfoUser.setRevokeStatus(null);
        wmMailboxInfoUser.setUserId(userId);
        wmMailboxInfoUser.setUserName(user.getUsername());
        wmMailboxInfoUser.setStatus(WmWebmailConstants.STATUS_SEND_SUCCESS);
        wmMailboxInfoUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_READ));
        wmMailboxInfoUser.setMailboxName(WmWebmailConstants.OUTBOX);
        wmMailboxInfoUser.setSystemUnitId(user.getSystemUnitId());
        wmMailboxInfoUser.setSendStatus(0);
        wmMailboxInfoUser.setMailInfoUuid(wmMailboxInfo.getUuid());
        return wmMailboxInfoUser;
    }

    private void verifyAddress(String mailAddress) {
        if (StringUtils.isNotBlank(mailAddress)) {
            List<String> addresses = Arrays.asList(StringUtils.split(mailAddress, Separator.SEMICOLON.getValue()));
            for (String address : addresses) {
                if (address.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1 ||
                        address.startsWith(IdPrefix.MULTI_GROUP.getValue()) ||
                        address.startsWith(IdPrefix.MULTI_GROUP_CATEGORY.getValue()) ||
                        address.startsWith(IdPrefix.DUTY.getValue()) ||
                        MultiOrgElement.isValidOrgId(address) ||
                        address.startsWith(IdPrefix.SYSTEM_UNIT.getValue()) ||
                        address.startsWith(IdPrefix.ORG_VERSION.getValue()) ||
                        extendsMailContactBookHolder.isExtendsMailAddressId(address)) {
                } else {
                    throw new RuntimeException("无效的邮件地址[" + address + "]!");
                }
            }
        }
    }

    @Override
    @Transactional
    public ResultMessage timingSend(WmWebmailBean webmailBean) {
        if (webmailBean.getSendTime() == null) {
            throw new RuntimeException("时间不能为空！");
        }
        WmMailboxInfoUser wmMailboxInfoUser = this.getWmMailboxInfoUser(webmailBean);
        wmMailboxInfoUser.setStatus(WmWebmailConstants.STATUS_DRAFT);
        wmMailboxInfoUser.setSendCount(0);
        wmMailboxInfoUser.setNextSendTime(webmailBean.getSendTime());
        wmMailboxInfoUserService.save(wmMailboxInfoUser);
        wmMailboxInfoUserService.flushSession();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("mailboxUuid", wmMailboxInfoUser.getUuid());
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(dataMap);
        return resultMessage;
    }

    @Override
    @Transactional
    public ResultMessage updateTime(WmWebmailBean webmailBean) {
        String mailboxUuid = webmailBean.getMailboxUuid();
        WmMailboxInfoUser wmMailbox = wmMailboxInfoUserService.getOne(mailboxUuid);
        if (wmMailbox.getStatus() == WmWebmailConstants.STATUS_SEND_SUCCESS) {
            throw new RuntimeException("已发送不能再修改时间");
        }
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailbox.getMailInfoUuid());
        wmMailboxInfo.setSendTime(webmailBean.getSendTime());
        wmMailbox.setNextSendTime(webmailBean.getSendTime());
        wmMailboxInfoService.update(wmMailboxInfo);
        wmMailboxInfoUserService.update(wmMailbox);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("mailboxUuid", wmMailbox.getUuid());
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(dataMap);
        return resultMessage;
    }

    private boolean existExternalMailboxes(String mailAddress, Set<String> domainSet) {
        if (StringUtils.isNotBlank(mailAddress) && mailAddress.indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
            List<String> addresses = Arrays.asList(StringUtils.split(mailAddress, Separator.SEMICOLON.getValue()));
            List<String> addresList = addresses.stream().filter(s -> {
                return s.indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1;
            }).collect(Collectors.toList());
            for (String addr : addresList) {
                String domain = addr.split(WmWebmailConstants.MAIL_SEPARATOR)[1];
                if (!domainSet.contains(domain)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 内部邮件账号合法性校验
     */
    private void localMailLegalCheck(MimeMessage message) {
        try {
            if (message.getRecipients(RecipientType.TO).length > 0) {
                boolean isLocalMail = ((InternetAddress) message.getRecipients(
                        RecipientType.TO)[0]).toUnicodeString().endsWith(
                        ((InternetAddress) message.getFrom()[0]).toUnicodeString().split(
                                WmWebmailConstants.MAIL_SEPARATOR)[1]);
                Set<String> addressStrSet = Sets.newHashSet();
                if (isLocalMail) {
                    List<Address> addressList = Lists.newArrayList();
                    addressList.add(message.getFrom()[0]);
                    if (message.getRecipients(RecipientType.TO) != null) {
                        addressList.addAll(Arrays.asList(message.getRecipients(RecipientType.TO)));
                    }
                    if (message.getRecipients(RecipientType.CC) != null) {
                        addressList.addAll(Arrays.asList(message.getRecipients(RecipientType.CC)));
                    }
                    if (message.getRecipients(RecipientType.BCC) != null) {
                        addressList.addAll(Arrays.asList(message.getRecipients(RecipientType.BCC)));
                    }
                    if (!addressList.isEmpty()) {
                        Map<String, String> addressNameMap = Maps.newHashMap();
                        for (Address ad : addressList) {
                            String address = ((InternetAddress) ad).toUnicodeString();
                            String email = address.substring(address.indexOf("<") + 1,
                                    address.indexOf(">"));
                            String name = address.substring(0, address.indexOf("<"));
                            addressStrSet.add(email);
                            addressNameMap.put(email, address);
                        }
                        Map<String, Object> values = Maps.newHashMap();
                        values.put("addresses", addressStrSet);
                        List<String> existJameUsers = this.getNativeDao(
                                James3Constant.DATA_SOURCE).query(
                                "select user_name from JAMES_USER where user_name in (:addresses) ",
                                values, String.class);
                        for (String juser : existJameUsers) {
                            addressNameMap.remove(juser);
                        }
                        if (!addressNameMap.isEmpty()) {
                            throw new AuthenticationFailedException(
                                    addressNameMap.values().toString().replaceAll("\"",
                                            "") + " 邮件账号不存在");
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("邮件账号不合法：", e);
            throw new RuntimeException(e.getMessage());
        }

    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#markRead(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void markRead(String mailboxUuid, String userId) {
        // 标记已读
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        if (wmMailboxInfoUser.getIsRead() == Integer.valueOf(WmWebmailConstants.FLAG_READ)) {
            return;
        }
        wmMailboxInfoUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_READ));
        wmMailboxInfoUser.setReadTime(new Date());
        wmMailboxInfoUserService.save(wmMailboxInfoUser);

    }

    @Override
    public void updateMailReadStatus(List<String> mailboxUuids, String readStatus) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("readStatus", Integer.valueOf(readStatus));
        param.put("uuids", mailboxUuids);
        param.put("readTime", new Date());
        this.dao.batchExecute("update WmMailboxInfoUser set isRead=:readStatus,readTime=:readTime where uuid in (:uuids)",
                param);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#delete(java.util.Collection)
     */
    @Override
    public void delete(Collection<String> mailboxUuids) {
        wmWebmailOutboxService.delete(mailboxUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#deletePhysical(java.util.Collection)
     */
    @Override
    public void deletePhysical(Collection<String> mailboxUuids) {
        wmWebmailOutboxService.deletePhysical(mailboxUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#countInfo(java.lang.String)
     */
    @Override
    public String countInfo(String userId) {
        String sql = "select t.status as status, count(t.status) as count from wm_mailbox t where t.user_id = :userId group by t.status";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        List<WmPollInfo> pollInfos = this.nativeDao.query(sql, values, WmPollInfo.class);
        Map<Integer, WmPollInfo> pollInfoMap = ConvertUtils.convertElementToMap(pollInfos,
                "status");
        Map<Integer, Long> countMap = new HashMap<Integer, Long>();
        for (Integer status : pollInfoMap.keySet()) {
            countMap.put(status, pollInfoMap.get(status).getCount());
        }

        WmMailbox example = new WmMailbox();
        example.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        example.setUserId(userId);
        example.setIsRead(WmWebmailConstants.FLAG_UNREAD);
        Long unreadCount = this.dao.countByExample(example);

        Map<String, String> pollMap = new HashMap<String, String>();
        // 收件箱未读邮件数/收件箱邮件数
        pollMap.put("unread", unreadCount + "");
        pollMap.put("inbox", unreadCount + "/" + getStatusCount(countMap,
                WmWebmailConstants.STATUS_RECEIVE_SUCCESS));
        // 草稿箱邮件数
        pollMap.put("draft", getStatusCount(countMap, WmWebmailConstants.STATUS_DRAFT) + "");
        // 删件邮件数
        pollMap.put("del", getStatusCount(countMap, WmWebmailConstants.STATUS_DELETE) + "");
        // 发件数
        pollMap.put("send", getStatusCount(countMap, WmWebmailConstants.STATUS_SEND_SUCCESS) + "");
        return JSONObject.fromObject(pollMap).toString();
    }

    private long getStatusCount(Map<Integer, Long> countMap, Integer status) {
        if (!countMap.containsKey(status)) {
            return 0;
        }
        return countMap.get(status);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#getUnreadCount(java.lang.String)
     */
    @Override
    public Long getUnreadCount(String userId) {
        WmMailbox example = new WmMailbox();
        example.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        example.setUserId(userId);
        example.setIsRead(WmWebmailConstants.FLAG_UNREAD);
        Long unreadCount = this.dao.countByExample(example);
        return unreadCount;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailService#refush(java.lang.String)
     */
    @Override
    public int refush(String userId) {
        int size = 0;
        try {
            WmMailbox example = new WmMailbox();
            example.setIsRead("-1");
            example.setCreator(SpringSecurityUtils.getCurrentUserId());
            List<WmMailbox> boxs = this.wmMailboxService.findByExample(example);
            size = boxs.size();
            // isRead -1:代表从james服务器拉回来而已，0代表：未读，1：已读？
            for (WmMailbox mailBox : boxs) {
                mailBox.setIsRead("0");
                wmMailboxService.save(mailBox);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return size;

    }

    @Override
    public void updateMailBoxName(String folderUuid, List<String> emailUuids) {
        wmMailboxService.updateMailBoxName(folderUuid, emailUuids);
    }

    @Override
    @Transactional
    public void deleteByMailboxAndUserId(List<String> mailboxes, String userId) {
        for (String mailbox : mailboxes) {
            WmMailbox example = new WmMailbox();
            example.setMailboxName(mailbox);
            example.setUserId(userId);
            wmMailboxService.delete(example);
            WmMailUseCapacityEntity useCapacityEntity = wmMailUseCapacityService.getByUserIdAndMailbox(
                    mailbox, userId);
            if (useCapacityEntity != null) {
                if (wmMailUseCapacityService.updateUseCapacity(-useCapacityEntity.getCapacityUsed(),
                        userId, useCapacityEntity.getSystemUnitId(), mailbox) == 0) {
                    throw new RuntimeException("邮件空间不足");
                }
                wmMailUseCapacityService.delete(useCapacityEntity);
            }
        }
    }


    @Override
    public void saveOuterMail(List<WmMailbox> mailbox,
                              WmMailUserEntity mailUserEntity) {
        for (WmMailbox bx : mailbox) {
            wmMailboxService.saveOuterMail(bx, mailUserEntity);
        }
    }

    @Override
    public WmWebmailBean getWebMail(String mailboxUuid) {
        WmMailboxBean bean = new WmMailboxBean();
        WmMailbox box = wmMailboxService.getOne(mailboxUuid);
        if (box != null) {
            org.springframework.beans.BeanUtils.copyProperties(box, bean);
        }
        return null;
    }

    @Override
    @Transactional
    public void saveMailMessage(int mid, Message message, WmMailUserEntity user) throws Exception {
        user.setSyncMessageNumber(mid);
        wmMailUserService.update(user);
        String[] fromMail = message.getHeader(WmWebmailConstants.HEADER_FROM_MAIL_ID);
        if (ArrayUtils.isNotEmpty(fromMail)) {
            String fromMailId = fromMail[0];
            //根据userId,mailAddress,fromMailId 找到记录更新mid
            WmMailboxInfoUser querInfoUser = new WmMailboxInfoUser();
            querInfoUser.setUserId(user.getUserId());
            querInfoUser.setMailAddress(user.getMailAddress());
            querInfoUser.setMailInfoUuid(fromMailId);
            List<WmMailboxInfoUser> infoUserList = wmMailboxInfoUserService.listByEntity(querInfoUser);
            for (WmMailboxInfoUser wmMailboxInfoUser : infoUserList) {
                //发送状态!=null 则为发送记录 跳过处理
                if (wmMailboxInfoUser.getSendStatus() != null) {
                    continue;
                }
                wmMailboxInfoUser.setMid(mid);
                wmMailboxInfoUserService.update(wmMailboxInfoUser);
            }
            return;
        }
        //占用收件箱的空间
        if (wmMailUseCapacityService.updateUseCapacity((long) message.getSize(), user.getUserId(), user.getSystemUnitId(), WmWebmailConstants.INBOX) == 0) {
            throw new RuntimeException("邮件空间不足");
        }
        WmMailboxInfo mailboxInfo = WmMailUtils.message2MailboxInfo(message);
        wmMailboxInfoService.save(mailboxInfo);
        WmMailboxInfoUser infoUser = new WmMailboxInfoUser();
        infoUser.setUserId(user.getUserId());
        infoUser.setUserName(user.getUserName());
        infoUser.setMailAddress(user.getMailAddress());
        infoUser.setSystemUnitId(user.getSystemUnitId());
        infoUser.setMailboxName(WmWebmailConstants.INBOX);
        infoUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_UNREAD));
        infoUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        infoUser.setMailInfoUuid(mailboxInfo.getUuid());
        infoUser.setReadReceiptStatus(mailboxInfo.getReadReceiptStatus());
        infoUser.setMid(mid);
        wmMailboxInfoUserService.save(infoUser);
    }


    @Override
    @Transactional
    public void automaticReceipt(String mailboxUuid) {
        //邮箱配置
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        //自动发送回执
        if (configEntity.getSendReceipt() != null && configEntity.getSendReceipt()) {
            this.receipt(mailboxUuid, 2);
        }
    }
}
