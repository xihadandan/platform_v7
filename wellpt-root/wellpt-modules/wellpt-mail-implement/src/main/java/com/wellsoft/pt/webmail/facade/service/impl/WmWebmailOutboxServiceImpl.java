/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoStatus;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.enums.WmMailboxInfoStatusEnum;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.WmMailUtils;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 发件箱
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
public class WmWebmailOutboxServiceImpl extends BaseServiceImpl implements WmWebmailOutboxService {

    @Autowired
    private WmMailboxService wmMailboxService;

    @Autowired
    private MongoFileService mongoFileService;

    @Resource
    private WmMailboxInfoStatusService wmMailboxInfoStatusService;


    @Resource
    private WmMailUseCapacityService wmMailUseCapacityService;

    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;

    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;

    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#get(java.lang.String)
     */
    @Override
    public WmWebmailBean get(String mailboxUuid) {
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        if (!SpringSecurityUtils.getCurrentUserId().equalsIgnoreCase(wmMailboxInfoUser.getUserId())) {
            return null;
        }
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
        WmWebmailBean bean = new WmWebmailBean();
        BeanUtils.copyProperties(wmMailboxInfo, bean);
        BeanUtils.copyProperties(wmMailboxInfoUser, bean);
        bean.setMailboxUuid(mailboxUuid);
        bean.setIsRead(wmMailboxInfoUser.getIsRead() != null ? wmMailboxInfoUser.getIsRead() + "" : null);
        List<String> nodeIdList = new ArrayList<>();
        List<String> nodeNameList = new ArrayList<>();
        List<String> toStrs = new ArrayList<>();
        List<String> toNameStrs = new ArrayList<>();
        this.addNodeIdList(toStrs, toNameStrs, bean.getToMailAddress(), bean.getToUserName(), nodeIdList, nodeNameList);
        List<String> ccStrs = new ArrayList<>();
        List<String> ccNameStrs = new ArrayList<>();
        this.addNodeIdList(ccStrs, ccNameStrs, bean.getCcMailAddress(), bean.getCcUserName(), nodeIdList, nodeNameList);
        List<String> bccStrs = new ArrayList<>();
        List<String> bccNameStrs = new ArrayList<>();
        this.addNodeIdList(bccStrs, bccNameStrs, bean.getBccMailAddress(), bean.getBccUserName(), nodeIdList, nodeNameList);
        Map<String, OrgNode> map = multiOrgTreeDialogService.smartName(nodeIdList, nodeNameList);
        this.addNodeIdList(toStrs, toNameStrs, map);
        this.addNodeIdList(ccStrs, ccNameStrs, map);
        this.addNodeIdList(bccStrs, bccNameStrs, map);
        if (toNameStrs.size() > 0) {
            bean.setToSmartUserName(StringUtils.join(toNameStrs, ";"));
        }
        if (ccNameStrs.size() > 0) {
            bean.setCcSmartUserName(StringUtils.join(ccNameStrs, ";"));
        }
        if (bccNameStrs.size() > 0) {
            bean.setBccSmartUserName(StringUtils.join(bccNameStrs, ";"));
        }
        // 加载MONGODB附件信息
        loadRepoFiles(bean);
        return bean;
    }

    @Override
    public WmWebmailBean getForEditAgain(String mailboxUuid) {
        WmWebmailBean webmailBean = this.get(mailboxUuid);
        webmailBean.setMailboxUuid(null);
        return webmailBean;
    }

    private void addNodeIdList(List<String> strs, List<String> nameStrs, String address, String name, List<String> nodeIdList, List<String> nodeNameList) {
        if (StringUtils.isNotBlank(address)) {
            strs.addAll(Lists.newArrayList(address.split(";")));
            if (name == null) {
                nameStrs.addAll(Lists.newArrayList(address.split(";")));
            } else {
                nameStrs.addAll(Lists.newArrayList(name.split(";")));
            }
            nodeNameList.addAll(Lists.newArrayList(nameStrs));
            nodeIdList.addAll(Lists.newArrayList(strs));
        }
    }

    private void addNodeIdList(List<String> strs, List<String> nameStrs, Map<String, OrgNode> map) {
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                String nodeId = strs.get(i);
                if (nodeId.startsWith(IdPrefix.ORG_VERSION.getValue())
                        && nodeId.contains(MultiOrgService.PATH_SPLIT_SYSMBOL)) {
                    String[] eleIds = nodeId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    nodeId = eleIds[eleIds.length - 1];
                }
                if (!nodeId.startsWith(IdPrefix.USER.getValue())) {
                    OrgNode orgNode = map.get(nodeId);
                    if (orgNode != null && StringUtils.isNotBlank(orgNode.getSmartNamePath())) {
                        nameStrs.set(i, orgNode.getSmartNamePath());
                    }
                }
            }
        }
    }

    /**
     * @param bean
     */
    private void loadRepoFiles(WmWebmailBean bean) {
        String repoFileUuids = bean.getRepoFileUuids();
        if (StringUtils.isBlank(repoFileUuids)) {
            return;
        }
        String[] fileIds = StringUtils.split(repoFileUuids, Separator.SEMICOLON.getValue());
        List<LogicFileInfo> logicFileInfos = new ArrayList<LogicFileInfo>();
        for (String fileId : fileIds) {
            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
            if (fileEntity != null) {
                logicFileInfos.add(fileEntity.getLogicFileInfo());
            }
        }
        bean.setRepoFiles(logicFileInfos);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#getForTransfer(java.lang.String)
     */
    @Override
    public WmWebmailBean getForTransfer(String mailboxUuid) {
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
        WmWebmailBean bean = new WmWebmailBean();
        // 主题
        bean.setSubject("转发: " + (StringUtils.isNotEmpty(wmMailboxInfo.getSubject()) ? wmMailboxInfo.getSubject() : ""));
        // 内容
        bean.setContent(WmMailUtils.wrapContent(wmMailboxInfoUser, wmMailboxInfo));
        if (wmMailboxInfoUser.getRevokeStatus() == null || wmMailboxInfoUser.getRevokeStatus() == 0) {
            // 附件
            bean.setRepoFileNames(wmMailboxInfo.getRepoFileNames());
            bean.setRepoFileUuids(wmMailboxInfo.getRepoFileUuids());
            // 加载MONGODB附件信息
            loadRepoFiles(bean);
        }
        return bean;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#getForReply(java.lang.String)
     */
    @Override
    public WmWebmailBean getForReply(String mailboxUuid) {
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
        WmWebmailBean bean = new WmWebmailBean();
        // 主题
        bean.setSubject((wmMailboxInfo.getSubject().indexOf(
                "回复: ") == 0 ? "" : "回复: ") + (wmMailboxInfo.getSubject() != null ? wmMailboxInfo.getSubject() : ""));

        bean.setContent(WmMailUtils.wrapContent(wmMailboxInfoUser, wmMailboxInfo));
        bean.setToUserName(wmMailboxInfo.getFromUserName());
        bean.setToMailAddress(wmMailboxInfo.getFromMailAddress());
        return bean;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#getForReplyAll(java.lang.String)
     */
    @Override
    public WmWebmailBean getForReplyAll(String mailboxUuid) {
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
        WmWebmailBean bean = new WmWebmailBean();
        // 主题
        bean.setSubject((wmMailboxInfo.getSubject().indexOf(
                "回复: ") == 0 ? "" : "回复: ") + (wmMailboxInfo.getSubject() != null ? wmMailboxInfo.getSubject() : ""));

        bean.setContent(WmMailUtils.wrapContent(wmMailboxInfoUser, wmMailboxInfo));

        Set<String> toUserNames = new LinkedHashSet<String>();
        Set<String> toMailAddress = new LinkedHashSet<String>();
        toUserNames.add(wmMailboxInfo.getFromUserName());
        toMailAddress.add(wmMailboxInfo.getFromMailAddress());
        if (StringUtils.isNotBlank(wmMailboxInfo.getToMailAddress())) {
            toUserNames.addAll(Arrays.asList(
                    StringUtils.split(wmMailboxInfo.getToUserName(), Separator.SEMICOLON.getValue())));
            toMailAddress.addAll(Arrays.asList(StringUtils.split(wmMailboxInfo.getToMailAddress(),
                    Separator.SEMICOLON.getValue())));
        }
        if (StringUtils.isNotBlank(wmMailboxInfo.getCcMailAddress())) {
            toUserNames.addAll(Arrays.asList(
                    StringUtils.split(wmMailboxInfo.getCcUserName(), Separator.SEMICOLON.getValue())));
            toMailAddress.addAll(Arrays.asList(StringUtils.split(wmMailboxInfo.getCcMailAddress(),
                    Separator.SEMICOLON.getValue())));
        }
        bean.setFromMailAddress(wmMailboxInfo.getFromMailAddress());
        bean.setToMailAddress(wmMailboxInfo.getToMailAddress());

        bean.setToMailAddress(StringUtils.join(toMailAddress, Separator.SEMICOLON.getValue()));
        // 收件人
        bean.setToUserName(StringUtils.join(toUserNames, Separator.SEMICOLON.getValue()));
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#saveDraft(com.wellsoft.pt.webmail.bean.WmWebmailBean)
     */
    @Override
    @Transactional
    public WmMailboxInfoUser saveDraft(WmWebmailBean webmailBean) {
        String mailboxUuid = webmailBean.getMailboxUuid();
        WmMailboxInfoUser wmMailboxInfoUser = new WmMailboxInfoUser();
        WmMailboxInfo wmMailboxInfo = new WmMailboxInfo();
        long mailSize = 0;
        if (StringUtils.isNotBlank(mailboxUuid)) {
            wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
            wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
            mailSize = wmMailboxInfo.getMailSize();
        }
        // 属性复制
        BeanUtils.copyProperties(webmailBean, wmMailboxInfoUser, IdEntity.BASE_FIELDS);
        BeanUtils.copyProperties(webmailBean, wmMailboxInfo, IdEntity.BASE_FIELDS);

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        wmMailboxInfoUser.setRevokeStatus(null);
        wmMailboxInfoUser.setUserId(user.getUserId());
        wmMailboxInfoUser.setUserName(user.getUserName());
        wmMailboxInfoUser.setStatus(WmWebmailConstants.STATUS_DRAFT);
        wmMailboxInfoUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_READ));
        wmMailboxInfoUser.setMailboxName(WmWebmailConstants.OUTBOX);
        long calculateSize = calculateWmMailSize(wmMailboxInfo);
        wmMailboxInfo.setMailSize(calculateSize);
        wmMailboxInfoService.save(wmMailboxInfo);
        wmMailboxInfoUser.setMailInfoUuid(wmMailboxInfo.getUuid());
        wmMailboxInfoUserService.save(wmMailboxInfoUser);
        if (wmMailUseCapacityService.updateUseCapacity(calculateSize - mailSize,
                wmMailboxInfoUser.getUserId(),
                user.getSystemUnitId(),
                WmWebmailConstants.DRAFT) == 0) {
            throw new RuntimeException("邮件空间不足");
        }
        return wmMailboxInfoUser;
    }

    @Override
    public long calculateWmMailSize(WmMailboxInfo wmMailbox) {
        int mailSize = wmMailbox.getContent() != null ? wmMailbox.getContent().getBytes(
                Charsets.UTF_8).length : 0;
        String repoFileUuids = wmMailbox.getRepoFileUuids();
        if (StringUtils.isNotBlank(repoFileUuids)) {
            String[] fileUuids = repoFileUuids.split(";");
            for (String fileUuid : fileUuids) {
                MongoFileEntity file = mongoFileService.getFile(fileUuid);
                if (file != null) {
                    mailSize += file.getLength();
                }
            }
        }
        return mailSize;
    }

    @Override
    public long calculateWmMailSize(WmMailbox wmMailbox) {
        int mailSize = wmMailbox.getContent() != null ? wmMailbox.getContent().getBytes(
                Charsets.UTF_8).length : 0;
        String repoFileUuids = wmMailbox.getRepoFileUuids();
        if (StringUtils.isNotBlank(repoFileUuids)) {
            String[] fileUuids = repoFileUuids.split(";");
            for (String fileUuid : fileUuids) {
                MongoFileEntity file = mongoFileService.getFile(fileUuid);
                if (file != null) {
                    mailSize += file.getLength();
                }
            }
        }
        return mailSize;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#saveNormal(com.wellsoft.pt.webmail.bean.WmWebmailBean)
     */
    @Override
    public WmMailbox saveNormal(WmWebmailBean webmailBean) {
        String mailboxUuid = webmailBean.getMailboxUuid();
        WmMailbox wmMailbox = new WmMailbox();
        long draftMailSize = 0L;
        if (StringUtils.isNotBlank(mailboxUuid)) {
            wmMailbox = wmMailboxService.get(mailboxUuid);
            draftMailSize = wmMailbox.getMailSize();
        }
        // 属性复制
        BeanUtils.copyProperties(webmailBean, wmMailbox);

        if (StringUtils.isNotEmpty(wmMailbox.getContent())) {
            String orginalContent = wmMailbox.getContent();
            // 替换掉信纸、签名等dom标志，防止回复转发时候新邮件的定位问题
            wmMailbox.setContent(
                    orginalContent.replaceAll("well_mail_paper", "").replaceAll("well_signature",
                            "").replaceAll("well_sign_div", ""));
        }

        // 确保收件人名称与ID一致
        if (StringUtils.isBlank(wmMailbox.getToUserName()) && !StringUtils.isBlank(
                wmMailbox.getToMailAddress())) {
            wmMailbox.setToUserName(
                    WmMailUtils.extractUserNameAsString(wmMailbox.getToMailAddress()));
        }
        // 确保抄送人名称与ID一致
        if (StringUtils.isBlank(wmMailbox.getCcUserName()) && !StringUtils.isBlank(
                wmMailbox.getCcMailAddress())) {
            wmMailbox.setCcUserName(
                    WmMailUtils.extractUserNameAsString(wmMailbox.getCcMailAddress()));
        }
        // 确保密送人名称与ID一致
        if (StringUtils.isBlank(wmMailbox.getBccUserName()) && !StringUtils.isBlank(
                wmMailbox.getBccMailAddress())) {
            wmMailbox.setBccUserName(
                    WmMailUtils.extractUserNameAsString(wmMailbox.getBccMailAddress()));
        }
        // 确保文件名称名称与ID一致
        if (StringUtils.isBlank(wmMailbox.getRepoFileNames()) && !StringUtils.isBlank(
                wmMailbox.getRepoFileUuids())) {
            wmMailbox.setRepoFileNames(
                    WmMailUtils.extractFileNameAsString(wmMailbox.getRepoFileUuids()));
        }
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        long mailSize = calculateWmMailSize(wmMailbox);
        if (wmMailUseCapacityService.updateseCapacityTransform(draftMailSize,
                mailSize, WmWebmailConstants.DRAFT,
                WmWebmailConstants.OUTBOX, user.getUserId()) == 0) {
            throw new RuntimeException("邮件空间不足");
        }
        wmMailbox.setUserId(user.getUserId());
        wmMailbox.setMailSize(mailSize);
        wmMailbox.setUserName(user.getUserName());
        wmMailbox.setFromUserName(webmailBean.getFromUserName());
        wmMailbox.setSendTime(new Date());
        wmMailbox.setStatus(WmWebmailConstants.STATUS_SEND_SUCCESS);
        wmMailbox.setIsRead(WmWebmailConstants.FLAG_READ);
        wmMailbox.setMailboxName(WmWebmailConstants.OUTBOX);
        wmMailbox.setSystemUnitId(user.getSystemUnitId());
        wmMailboxService.save(wmMailbox);

        // 附件放入夹
        pushFilesToFolder(wmMailbox);

        // 添加用户联系人
        // addUserContact(wmMailbox);
        return wmMailbox;
    }

    /**
     * 附件放入夹
     *
     * @param wmMailbox
     */
    @Override
    public void pushFilesToFolder(WmMailboxInfo wmMailbox) {
        String repoFileUuids = wmMailbox.getRepoFileUuids();
        if (StringUtils.isBlank(repoFileUuids)) {
            return;
        }
        mongoFileService.pushFilesToFolder(wmMailbox.getUuid(),
                Arrays.asList(StringUtils.split(repoFileUuids, Separator.SEMICOLON.getValue())),
                null);
    }

    @Override
    public void pushFilesToFolder(WmMailbox wmMailbox) {
        String repoFileUuids = wmMailbox.getRepoFileUuids();
        if (StringUtils.isBlank(repoFileUuids)) {
            return;
        }
        mongoFileService.pushFilesToFolder(wmMailbox.getUuid(),
                Arrays.asList(StringUtils.split(repoFileUuids, Separator.SEMICOLON.getValue())),
                null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#saveAll(java.util.List)
     */
    @Override
    public void saveAll(List<WmMailbox> mailBoxs) {
        for (WmMailbox wmMailbox : mailBoxs) {
            wmMailboxService.save(wmMailbox);
            // 附件放入夹
            pushFilesToFolder(wmMailbox);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#delete(java.util.Collection)
     */
    @Override
    public void delete(Collection<String> mailboxUuids) {
        for (String mailboxUuid : mailboxUuids) {
            WmMailboxInfoUser wmMailbox = wmMailboxInfoUserService.getOne(mailboxUuid);
            String fromMailbox = wmMailbox.translateMailbox();
            wmMailbox.setStatus(WmWebmailConstants.STATUS_DELETE);
            wmMailboxInfoUserService.save(wmMailbox);
            String toMailbox = wmMailbox.translateMailbox();
            WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailbox.getMailInfoUuid());
            if (wmMailUseCapacityService.updateseCapacityTransform(wmMailboxInfo.getMailSize(),
                    wmMailboxInfo.getMailSize(), fromMailbox, toMailbox,
                    wmMailbox.getUserId()) == 0) {
                throw new RuntimeException("邮件空间不足");
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService#deletePhysical(java.util.Collection)
     */
    @Override
    public void deletePhysical(Collection<String> mailboxUuids) {
        for (String mailboxUuid : mailboxUuids) {
            WmMailboxInfoUser wmMailbox = wmMailboxInfoUserService.getOne(mailboxUuid);
            WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailbox.getMailInfoUuid());
            if (wmMailUseCapacityService.updateUseCapacity(-wmMailboxInfo.getMailSize(),
                    wmMailbox.getUserId(), wmMailbox.getSystemUnitId(), wmMailbox.translateMailbox()) == 0) {
                throw new RuntimeException("邮件空间不足");
            }
            wmMailbox.setStatus(WmWebmailConstants.STATUS_PHYSICAL_DELETE);
            wmMailboxInfoUserService.save(wmMailbox);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> querySendStatus(String mailboxUuid,
                                               Boolean fastBreak) throws Exception {
        // 投递状态
        Map<String, Object> sendStatus = new HashMap();
        // 发件箱邮件
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailboxUuid);
        sendStatus.put("sendStatus", wmMailboxInfoUser.getSendStatus());
        if (wmMailboxInfoUser.getRevokeStatus() != null) {
            sendStatus.put("revokeStatus", wmMailboxInfoUser.getRevokeStatus());
        }
        if (wmMailboxInfoUser.getSendStatus() != null && wmMailboxInfoUser.getSendStatus() == 0) {
            return sendStatus;
        }
        if (!fastBreak) {
            WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
            //新数据
            if (wmMailboxInfo.getIsPublicEmail() != null) {
                return this.getSendStatusMap(sendStatus, wmMailboxInfoUser, wmMailboxInfo);
            }

            if (StringUtils.isEmpty(wmMailboxInfo.getActualToMailAddress())) {
                sendStatus.put("noToMailAddress", true);
                return sendStatus;
            }
            List<WmMailboxInfoUser> infoUserList = wmMailboxInfoUserService.getInboxList(wmMailboxInfoUser.getMailInfoUuid());
            if (infoUserList.size() == 0) {
                sendStatus.put("noToMailAddress", true);
                sendStatus.put("msg", "邮件服务异常");
                return sendStatus;
            }
            //val[投递，已读，撤回，阅读时间]
            HashMap<String, String[]> toStatus = new HashMap();
            HashMap<String, String[]> ccStatus = new HashMap();
            HashMap<String, String[]> bccStatus = new HashMap();
            sendStatus.put("To", toStatus);
            sendStatus.put("Cc", ccStatus);
            sendStatus.put("Bcc", bccStatus);
            Map<String, WmMailboxInfoUser> infoUserMap = infoUserList.stream().collect(Collectors.toMap(WmMailboxInfoUser::getUserId, infoUser -> infoUser));
            this.addStatus(wmMailboxInfo.getActualToMailAddress(), infoUserMap, toStatus);
            if (StringUtils.isNotEmpty(wmMailboxInfo.getActualCcMailAddress())) {
                this.addStatus(wmMailboxInfo.getActualCcMailAddress(), infoUserMap, ccStatus);
            }
            if (StringUtils.isNotEmpty(wmMailboxInfo.getActualBccMailAddress())) {
                this.addStatus(wmMailboxInfo.getActualBccMailAddress(), infoUserMap, bccStatus);
            }
        }
        return sendStatus;
    }

    private Map<String, Object> getSendStatusMap(Map<String, Object> sendStatus, WmMailboxInfoUser wmMailboxInfoUser, WmMailboxInfo wmMailboxInfo) {
        List<WmMailboxInfoStatus> infoStatusList = wmMailboxInfoStatusService.listByMialInfoUuid(wmMailboxInfo.getUuid());
        if (CollectionUtils.isEmpty(infoStatusList)) {
            sendStatus.put("noToMailAddress", true);
            return sendStatus;
        }
        List<WmMailboxInfoUser> infoUserList = wmMailboxInfoUserService.getInboxList(wmMailboxInfoUser.getMailInfoUuid());
        Map<String, WmMailboxInfoUser> infoUserMap = new HashMap<>();
        if (infoUserList.size() > 0) {
            infoUserMap = infoUserList.stream().collect(Collectors.toMap(WmMailboxInfoUser::getUserId, infoUser -> infoUser));
        }
        //val[投递，已读，撤回，阅读时间]
        HashMap<String, String[]> toStatus = new HashMap();
        HashMap<String, String[]> ccStatus = new HashMap();
        HashMap<String, String[]> bccStatus = new HashMap();

        sendStatus.put("To", toStatus);
        sendStatus.put("Cc", ccStatus);
        sendStatus.put("Bcc", bccStatus);
        for (WmMailboxInfoStatus infoStatus : infoStatusList) {
            String send = infoStatus.getStatus() == null ? null : infoStatus.getStatus() + "";
            String read = "0";
            String revoke = "0";
            String readTime = null;
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.HasBeenSent.ordinal() || infoStatus.getStatus() == WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal()) {
                if (StringUtils.isNotBlank(infoStatus.getUserId())) {
                    WmMailboxInfoUser infoUser = infoUserMap.get(infoStatus.getUserId());
                    if (infoUser != null) {
                        if (infoUser.getStatus() == WmWebmailConstants.STATUS_RECEIVE_FAIL) {
                            send = "-1";
                        }
                        read = infoUser.getIsRead() == null ? "0" : infoUser.getIsRead() + "";
                        revoke = infoUser.getRevokeStatus() == null ? "0" : infoUser.getRevokeStatus() + "";
                        if (read.equals(WmWebmailConstants.FLAG_READ) && infoUser.getReadTime() != null) {
                            readTime = DateUtils.convertDate(infoUser.getReadTime());
                        }
                    }
                } else {
                    read = "1";
                    revoke = "2";
                }
            } else {
                read = "-1";
                revoke = "-1";
            }

            String[] statusInt = new String[]{send, read, revoke, readTime};
            String mailName = infoStatus.getMailName();
            if (StringUtils.isNotBlank(infoStatus.getMailAddress())) {
                mailName = mailName + "<" + infoStatus.getMailAddress() + ">";
            }
            if (infoStatus.getRecipientType() == 1) {
                toStatus.put(mailName, statusInt);
            } else if (infoStatus.getRecipientType() == 2) {
                ccStatus.put(mailName, statusInt);
            } else if (infoStatus.getRecipientType() == 3) {
                bccStatus.put(mailName, statusInt);
            }
        }
        return sendStatus;
    }

    private void addStatus(String actualToMailAddress, Map<String, WmMailboxInfoUser> infoUserMap, HashMap<String, String[]> toStatus) {
        String[] userIds = actualToMailAddress.split(Separator.SEMICOLON.getValue());
        for (String userId : userIds) {
            if (userId.indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                toStatus.put(userId + "<" + userId + ">", new String[]{WmWebmailConstants.STATUS_RECEIVE_SUCCESS + "", "1", "2", null});
            } else {
                WmMailboxInfoUser wmMailboxInfoUser = infoUserMap.get(userId);
                if (wmMailboxInfoUser != null) {
                    String status = wmMailboxInfoUser.getStatus() == null ? null : wmMailboxInfoUser.getStatus() + "";
                    String isRead = wmMailboxInfoUser.getIsRead() == null ? null : wmMailboxInfoUser.getIsRead() + "";
                    String revokeStatus = wmMailboxInfoUser.getRevokeStatus() == null ? null : wmMailboxInfoUser.getRevokeStatus() + "";
                    String readTime = null;
                    if (isRead != null && isRead.equals(WmWebmailConstants.FLAG_READ)) {
                        if (wmMailboxInfoUser.getReadTime() != null) {
                            readTime = DateUtils.convertDate(wmMailboxInfoUser.getReadTime());
                        }
                    }
                    toStatus.put(wmMailboxInfoUser.getUserName() + "<" + wmMailboxInfoUser.getMailAddress() + ">", new String[]{status, isRead, revokeStatus, readTime});
                }
            }
        }
    }


}
