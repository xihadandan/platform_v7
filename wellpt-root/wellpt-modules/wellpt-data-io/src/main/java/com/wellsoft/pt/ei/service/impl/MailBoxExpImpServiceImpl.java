package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.mail.MailData;
import com.wellsoft.pt.ei.dto.mail.MailInfo;
import com.wellsoft.pt.ei.dto.mail.SendReceiveInfo;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.processor.utils.FieldTypeUtils;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.webmail.bean.WmMailBoxInfoBean;
import com.wellsoft.pt.webmail.entity.WmMailRelaTagEntity;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.service.WmMailRelaTagService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: yt
 * @Date: 2021/9/24 11:34
 * @Description:
 */

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class MailBoxExpImpServiceImpl implements ExpImpService<MailData, WmMailBoxInfoBean> {

    private static String hql = "from WmMailboxInfoUser where status in (:status) and systemUnitId=:systemUnitId";
    private static String sql = "select u.*, i.from_user_name, i.from_mail_address, i.to_user_name, i.to_mail_address, i.cc_user_name, i.cc_mail_address, " +
            "i.bcc_user_name, i.bcc_mail_address, i.subject, i.content, i.repo_file_names, i.repo_file_uuids, i.mail_size, " +
            "i.read_receipt_status as mailReadReceiptStatus, i.priority, i.send_time, i.is_public_email, i.actual_to_mail_address, i.actual_cc_mail_address, i.actual_bcc_mail_address " +
            "from WM_MAILBOX_INFO_USER u left join WM_MAILBOX_INFO i on u.mail_info_uuid = i.uuid " +
            "where u.status in (:status) and u.system_unit_id = :systemUnitId ";
    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;
    @Autowired
    private WmMailboxService wmMailboxService;
    @Autowired
    private DataImportTaskLogService taskLogService;
    @Autowired
    private WmMailRelaTagService wmMailRelaTagService;
    @Autowired
    private MongoFileService mongoFileService;
    private List<Integer> status;

    public MailBoxExpImpServiceImpl(Set<String> mailBoxSet) {
        status = Lists.newArrayList();
        for (String mailBox : mailBoxSet) {
            if ("receivce".equals(mailBox)) {
                status.add(WmMailBoxStatus.FETCH_SUCCESS.getCode());
            } else if ("send".equals(mailBox)) {
                status.add(WmMailBoxStatus.SEND_SUCCESS.getCode());
            } else if ("draft".equals(mailBox)) {
                status.add(WmMailBoxStatus.DRAFT.getCode());
            } else if ("recovery".equals(mailBox)) {
                status.add(WmMailBoxStatus.LOGICAL_DELETE.getCode());
            }
        }
    }

    @Override
    public int order() {
        return 3;
    }

    @Override
    public String fileName() {
        return "邮件_邮件";
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_MAIL;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = this.getParamMap(systemUnitId);
        long count = wmMailboxInfoService.getDao().countByHQL("select count(uuid) " + hql, params);
        return count;
    }

    @Override
    public List<WmMailBoxInfoBean> queryAll(String systemUnitId) {
        Map<String, Object> params = this.getParamMap(systemUnitId);
        List<WmMailBoxInfoBean> wmMailboxList = wmMailboxInfoService.queryAll(sql, params);
        return wmMailboxList;
    }

    @Override
    public List<WmMailBoxInfoBean> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = this.getParamMap(systemUnitId);
        List<WmMailBoxInfoBean> wmMailboxList = wmMailboxInfoService.queryByPage(sql, params, pagingInfo);
        return wmMailboxList;
    }


    private Map<String, Object> getParamMap(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitId", systemUnitId);
        params.put("status", status);
        return params;
    }

    @Override
    public Class dataClass() {
        return MailData.class;
    }

    @Override
    public String filePath() {
        return DataExportConstants.DATA_TYPE_MAIL;
    }

    @Override
    public MailData toData(WmMailBoxInfoBean wmMailbox) {
        MailData mailData = new MailData();
        mailData.setMailInfo(this.convertToMailInfo(wmMailbox));
        mailData.setSendReceiveInfo(this.convertToSendReceiveInfo(wmMailbox));
        return mailData;
    }

    @Override
    public String getUuid(WmMailBoxInfoBean wmMailbox) {
        return wmMailbox.getUuid();
    }

    @Override
    public String getDataUuid(MailData mailData) {
        return mailData.getMailInfo().getUuid();
    }


    @Override
    public String getId(WmMailBoxInfoBean wmMailbox) {
        return null;
    }

    @Override
    public String getDataId(MailData mailData) {
        return null;
    }


    @Override
    @Transactional
    public ImportEntity<WmMailBoxInfoBean, MailData> save(MailData mailData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {
        WmMailBoxInfoBean wmMailbox = null;
        if (replace) {
            DataImportTaskLog taskLog = taskLogService.getBySourceUuid(mailData.getMailInfo().getUuid());
            if (taskLog != null && taskLog.getAfterImportUuid() != null) {
                wmMailbox = wmMailboxInfoService.getByMailUuid(taskLog.getAfterImportUuid());
            }
        }
        if (wmMailbox == null) {
            wmMailbox = new WmMailBoxInfoBean();
        }
        wmMailbox.setSystemUnitId(systemUnitId);
        wmMailbox.setUserId(this.convertToNewId(mailData.getMailInfo().getUserId()));
        wmMailbox.setUserName(mailData.getMailInfo().getUserName());

        wmMailbox.setSubject(mailData.getMailInfo().getSubject());
        wmMailbox.setSendTime(mailData.getMailInfo().getSendTime());
        wmMailbox.setFromUserName(mailData.getSendReceiveInfo().getFromUserName());
        wmMailbox.setFromMailAddress(mailData.getSendReceiveInfo().getFromMailAddress());

        wmMailbox.setToUserName(mailData.getSendReceiveInfo().getToUserName());
        wmMailbox.setToMailAddress(this.convertToNewId(mailData.getSendReceiveInfo().getToMailAddress()));

        wmMailbox.setCcUserName(mailData.getSendReceiveInfo().getCcUserName());
        wmMailbox.setCcMailAddress(this.convertToNewId(mailData.getSendReceiveInfo().getCcMailAddress()));

        wmMailbox.setBccUserName(mailData.getSendReceiveInfo().getBccUserName());
        wmMailbox.setBccMailAddress(this.convertToNewId(mailData.getSendReceiveInfo().getBccMailAddress()));

        wmMailbox.setReadReceiptStatus(mailData.getMailInfo().getReadReceiptStatus());
        wmMailbox.setPriority(mailData.getMailInfo().getPriority());

        wmMailbox.setContent(mailData.getMailInfo().getContent());
        wmMailbox.setIsRead(Integer.parseInt(mailData.getMailInfo().getIsRead()));

        wmMailbox.setSendStatus(mailData.getSendReceiveInfo().getSendStatus());
        wmMailbox.setMailboxName(mailData.getMailInfo().getMailboxName());
        wmMailbox.setStatus(mailData.getMailInfo().getMailType());
        wmMailbox.setRevokeStatus(mailData.getMailInfo().getRevokeStatus());

        long mailSize = mailData.getMailInfo().getContent() != null ? mailData.getMailInfo().getContent().getBytes(
                Charsets.UTF_8).length : 0;
        List<String> repoFileUuidList = Lists.newArrayList();
        List<String> repoFileNameList = Lists.newArrayList();
        if (mailData.getMailInfo().getRepoFile() != null) {
            for (String fileUuid : mailData.getMailInfo().getRepoFile()) {
                MongoFileEntity file = mongoFileService.getFile(fileUuid);
                if (file != null) {
                    repoFileUuidList.add(fileUuid);
                    repoFileNameList.add(file.getFileName());
                    mailSize += file.getLength();
                }
            }
        }
        wmMailbox.setRepoFileUuids(StringUtils.join(repoFileUuidList, Separator.SEMICOLON.getValue()));
        wmMailbox.setRepoFileNames(StringUtils.join(repoFileNameList, Separator.SEMICOLON.getValue()));
        wmMailbox.setMailSize(mailSize);

        ImportEntity<WmMailBoxInfoBean, MailData> importEntity = new ImportEntity<>();
        /*if (StringUtils.isNotBlank(mailData.getMailInfo().getFromMailUuid())) {
            String fromMailUuid = this.getAfterImportUuid(mailData.getMailInfo().getFromMailUuid(), dependentDataMap);
            if (StringUtils.isNotBlank(fromMailUuid)) {
                wmMailbox.setMailInfoUuid(fromMailUuid);
            } else {
                importEntity.setPostProcess(true);
            }
        }

        List<String> tags = Lists.newArrayList();
        if (!importEntity.isPostProcess() && CollectionUtils.isNotEmpty(mailData.getMailInfo().getTags())) {
            for (String tagUuid : mailData.getMailInfo().getTags()) {
                String tagUuidImp = this.getAfterImportUuid(tagUuid, dependentDataMap);
                if (StringUtils.isNotBlank(tagUuidImp)) {
                    tags.add(tagUuidImp);
                } else {
                    importEntity.setPostProcess(true);
                    break;
                }
            }
        }

        if (!importEntity.isPostProcess()) {
            WmMailboxInfoUser wmMailboxInfoUser = new WmMailboxInfoUser();
            BeanUtils.copyProperties(wmMailbox, wmMailboxInfoUser);
            wmMailboxInfoUser.setReadReceiptStatus(wmMailbox.getUserReadReceiptStatus());
            wmMailboxInfoUserService.save(wmMailboxInfoUser);

            WmMailboxInfo wmMailboxInfo = new WmMailboxInfo();
            BeanUtils.copyProperties(wmMailbox, wmMailboxInfo, IdEntity.BASE_FIELDS);
            wmMailboxInfo.setUuid(wmMailboxInfoUser.getMailInfoUuid());
            wmMailboxInfoService.save(wmMailboxInfo);

            this.saveTags(systemUnitId, wmMailbox.getUuid(), tags);
            dependentDataMap.put(mailData.getMailInfo().getUuid(), wmMailbox.getUuid());
        }*/

        // 先更新邮件主体信息
        WmMailboxInfo wmMailboxInfo = null;
        if (StringUtils.isNotBlank(wmMailbox.getMailInfoUuid())) {
            // 如果邮件已存在，则从库中获取
            wmMailboxInfo = wmMailboxInfoService.getOne(wmMailbox.getMailInfoUuid());
        }
        if (null == wmMailboxInfo) {
            // 邮件不存在，则当成新邮件进行实例化
            wmMailboxInfo = new WmMailboxInfo();
        }

        BeanUtils.copyProperties(wmMailbox, wmMailboxInfo, IdEntity.BASE_FIELDS);
        // 保存或者更新邮件
        wmMailboxInfoService.save(wmMailboxInfo);

        WmMailboxInfoUser wmMailboxInfoUser = new WmMailboxInfoUser();
        BeanUtils.copyProperties(wmMailbox, wmMailboxInfoUser);
        wmMailboxInfoUser.setReadReceiptStatus(wmMailbox.getUserReadReceiptStatus());
        // 与邮件进行关联
        wmMailboxInfoUser.setMailInfoUuid(wmMailboxInfo.getUuid());
        wmMailboxInfoUserService.save(wmMailboxInfoUser);
        wmMailbox.setUuid(wmMailboxInfoUser.getUuid());

        List<String> tags = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(mailData.getMailInfo().getTags())) {
            for (String tagUuid : mailData.getMailInfo().getTags()) {
                String tagUuidImp = this.getAfterImportUuid(tagUuid, dependentDataMap);
                if (StringUtils.isNotBlank(tagUuidImp)) {
                    tags.add(tagUuidImp);
                }
            }
        }

        this.saveTags(systemUnitId, wmMailbox.getUuid(), tags);
        dependentDataMap.put(mailData.getMailInfo().getUuid(), wmMailbox.getUuid());

        importEntity.setObj(wmMailbox);
        importEntity.setSorce(mailData);
        return importEntity;
    }


    @Override
    @Transactional
    public void update(WmMailBoxInfoBean wmMailbox, MailData mailData, Map<String, String> dependentDataMap) {
        wmMailbox.setMailInfoUuid(this.getAfterImportUuid(mailData.getMailInfo().getFromMailUuid(), dependentDataMap));
        WmMailboxInfoUser wmMailboxInfoUser = new WmMailboxInfoUser();
        BeanUtils.copyProperties(wmMailbox, wmMailboxInfoUser);
        wmMailboxInfoUser.setReadReceiptStatus(wmMailbox.getUserReadReceiptStatus());
        wmMailboxInfoUserService.save(wmMailboxInfoUser);

        List<String> tags = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(mailData.getMailInfo().getTags())) {
            for (String tagUuid : mailData.getMailInfo().getTags()) {
                String tagUuidImp = this.getAfterImportUuid(tagUuid, dependentDataMap);
                if (StringUtils.isNotBlank(tagUuidImp)) {
                    tags.add(tagUuidImp);
                }
            }
        }

        this.saveTags(wmMailbox.getSystemUnitId(), wmMailbox.getUuid(), tags);
    }

    private void saveTags(String systemUnitId, String mailUuid, List<String> tags) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailUuid", mailUuid);
        for (String tagUuid : tags) {
            param.put("tagUuid", tagUuid);
            List exists = wmMailRelaTagService.listByHQL(
                    "select uuid from WmMailRelaTagEntity where tagUuid=:tagUuid and mailUuid=:mailUuid", param);
            if (CollectionUtils.isNotEmpty(exists)) {
                continue;
            }
            WmMailRelaTagEntity entity = new WmMailRelaTagEntity();
            entity.setTagUuid(tagUuid);
            entity.setMailUuid(mailUuid);
            entity.setSystemUnitId(systemUnitId);
            wmMailRelaTagService.save(entity);
        }
    }

    private String getAfterImportUuid(String sourceUuid, Map<String, String> dependentDataMap) {
        DataImportTaskLog taskLog = taskLogService.getBySourceUuid(sourceUuid);
        if (taskLog != null) {
            return taskLog.getAfterImportUuid();
        }
        return dependentDataMap.get(sourceUuid);
    }

    private String getAfterImportId(String sourceId) {
        DataImportTaskLog taskLog = taskLogService.getBySourceId(sourceId);
        if (taskLog == null) {
            throw new RuntimeException("数据依赖缺失");
        }
        return taskLog.getAfterImportId();
    }

    private String convertToNewId(String sourceId) {
        if (StringUtils.isBlank(sourceId)) {
            return sourceId;
        }
        String[] sourceIdStrs = StringUtils.split(sourceId, Separator.SEMICOLON.getValue());
        List<String> idList = Lists.newArrayList();
        for (String sourceIdStr : sourceIdStrs) {
            if (sourceIdStr.indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                idList.add(sourceIdStr);
            } else {
                if (sourceIdStr.indexOf(Separator.SLASH.getValue()) > -1) {
                    String[] ids = StringUtils.split(sourceIdStr, Separator.SLASH.getValue());
                    List<String> idNewList = Lists.newArrayList();
                    for (String id : ids) {
                        //跳过组织版本 导入数据不会导入组织版本
                        if (id.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                            continue;
                        }
                        idNewList.add(this.getAfterImportId(id));
                    }
                    idList.add(StringUtils.join(idNewList, Separator.SLASH.getValue()));
                } else {
                    idList.add(this.getAfterImportId(sourceIdStr));
                }
            }
        }
        String idStr = StringUtils.join(idList, Separator.SEMICOLON.getValue());
        return idStr;
    }


    private SendReceiveInfo convertToSendReceiveInfo(WmMailBoxInfoBean wmMailbox) {
        SendReceiveInfo sendReceiveInfo = new SendReceiveInfo();
        sendReceiveInfo.setFromUserName(wmMailbox.getFromUserName());
        sendReceiveInfo.setFromMailAddress(wmMailbox.getFromMailAddress());
        sendReceiveInfo.setToUserName(wmMailbox.getToUserName());
        sendReceiveInfo.setToMailAddress(wmMailbox.getToMailAddress());
        sendReceiveInfo.setCcUserName(wmMailbox.getCcUserName());
        sendReceiveInfo.setCcMailAddress(wmMailbox.getCcMailAddress());
        sendReceiveInfo.setBccUserName(wmMailbox.getBccUserName());
        sendReceiveInfo.setBccMailAddress(wmMailbox.getBccMailAddress());
        sendReceiveInfo.setSendStatus(wmMailbox.getSendStatus());
        sendReceiveInfo.setReadReceiptStatus(wmMailbox.getUserReadReceiptStatus());
        sendReceiveInfo.setActualToMailAddress(wmMailbox.getActualToMailAddress());
        sendReceiveInfo.setActualCcMailAddress(wmMailbox.getActualCcMailAddress());
        sendReceiveInfo.setActualBccMailAddress(wmMailbox.getActualBccMailAddress());
        return sendReceiveInfo;
    }

    private MailInfo convertToMailInfo(WmMailBoxInfoBean wmMailbox) {
        MailInfo mailInfo = new MailInfo();
        mailInfo.setUuid(wmMailbox.getUuid());
        mailInfo.setUserId(wmMailbox.getUserId());
        mailInfo.setUserName(wmMailbox.getUserName());
        mailInfo.setMailboxName(wmMailbox.getMailboxName());
        mailInfo.setSubject(wmMailbox.getSubject());
        mailInfo.setSendTime(wmMailbox.getSendTime());
        mailInfo.setContent(wmMailbox.getContent());
        mailInfo.setRepoFile(FieldTypeUtils.convertList(wmMailbox.getRepoFileUuids()));
        mailInfo.setReadReceiptStatus(wmMailbox.getReadReceiptStatus());
        mailInfo.setPriority(wmMailbox.getPriority());
        if (null != wmMailbox.getIsRead()) {
            mailInfo.setIsRead(wmMailbox.getIsRead().toString());
        }
        mailInfo.setMailType(wmMailbox.getStatus());
        mailInfo.setRevokeStatus(wmMailbox.getRevokeStatus());
        mailInfo.setFromMailUuid(wmMailbox.getMailInfoUuid());
        mailInfo.setSendCount(wmMailbox.getSendCount());
        mailInfo.setFailMsg(wmMailbox.getFailMsg());
        mailInfo.setNextSendTime(wmMailbox.getNextSendTime());

        List<WmMailRelaTagEntity> relaTagEntityList = wmMailRelaTagService.getDao().listByFieldEqValue("mailUuid", wmMailbox.getUuid());
        if (relaTagEntityList.size() > 0) {
            List<String> tags = Lists.newArrayList();
            for (WmMailRelaTagEntity wmMailRelaTagEntity : relaTagEntityList) {
                tags.add(wmMailRelaTagEntity.getTagUuid());
            }
            mailInfo.setTags(tags);
        }
        return mailInfo;
    }


}
