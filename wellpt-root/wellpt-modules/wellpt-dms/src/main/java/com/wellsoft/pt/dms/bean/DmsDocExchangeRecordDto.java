package com.wellsoft.pt.dms.bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */
public class DmsDocExchangeRecordDto implements Serializable {
    private static final long serialVersionUID = 6752395529394735475L;

    private String uuid;

    private String userId;

    private String userName;

    private String fromUnitId;

    private String userUnitName;

    private Integer exchangeType;

    private String dyformUuid;

    private String dataUuid;

    private Date feedbackTimeLimit;

    private Date signTimeLimit;

    private Integer docEncryptionLevel;

    private Integer docUrgeLevel;

    private Boolean isSmsNotify;

    private Boolean isImNotify;

    private Boolean isMailNotify;

    private Integer recordStatus;

    private String fromRecordUuid;

    private String toUserIds;

    private String toUserNames;

    private String fileUuids;

    private String fileNames;

    private String fromRecordDetailUuid;

    private Boolean isNeedSign;

    private Boolean isNeedFeedback;

    private String docTitle;

    private String configurationJson;//配置项json

    private String configUuid;//配置项uuid

    private Date sendTime;//发件时间

    private Integer refuseToView; //拒绝查看

    private Integer noReminders; //不再提醒


    private List<DmsDocExchangeRecordDetailDto> receiveDetailDtoList = Lists.newArrayList(); //记录明细

    private DmsDocExchangeForwardDto forwardDto; //转发详情

    private Set<DocExchangeNotifyWayEnum> notifyWays = Sets.newLinkedHashSet(); //提醒方式


    public String getConfigUuid() {
        return configUuid;
    }

    public void setConfigUuid(String configUuid) {
        this.configUuid = configUuid;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getDyformUuid() {
        return dyformUuid;
    }

    public void setDyformUuid(String dyformUuid) {
        this.dyformUuid = dyformUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Date getFeedbackTimeLimit() {
        return feedbackTimeLimit;
    }

    public void setFeedbackTimeLimit(Date feedbackTimeLimit) {
        this.feedbackTimeLimit = feedbackTimeLimit;
    }

    public Date getSignTimeLimit() {
        return signTimeLimit;
    }

    public void setSignTimeLimit(Date signTimeLimit) {
        this.signTimeLimit = signTimeLimit;
    }

    public Integer getDocEncryptionLevel() {
        return docEncryptionLevel;
    }

    public void setDocEncryptionLevel(Integer docEncryptionLevel) {
        this.docEncryptionLevel = docEncryptionLevel;
    }

    public Integer getDocUrgeLevel() {
        return docUrgeLevel;
    }

    public void setDocUrgeLevel(Integer docUrgeLevel) {
        this.docUrgeLevel = docUrgeLevel;
    }

    public Boolean getIsSmsNotify() {
        return isSmsNotify;
    }

    public void setIsSmsNotify(Boolean smsNotify) {
        isSmsNotify = smsNotify;
        if (BooleanUtils.isTrue(smsNotify))
            this.getNotifyWays().add(DocExchangeNotifyWayEnum.SMS);
    }

    public Boolean getIsImNotify() {
        return isImNotify;
    }

    public void setIsImNotify(Boolean imNotify) {
        isImNotify = imNotify;
        if (BooleanUtils.isTrue(imNotify))
            this.getNotifyWays().add(DocExchangeNotifyWayEnum.IM);
    }

    public Boolean getIsMailNotify() {
        return isMailNotify;
    }

    public void setIsMailNotify(Boolean mailNotify) {
        isMailNotify = mailNotify;
        if (BooleanUtils.isTrue(mailNotify))
            this.getNotifyWays().add(DocExchangeNotifyWayEnum.MAIL);
    }

    public Integer getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getFromRecordUuid() {
        return fromRecordUuid;
    }

    public void setFromRecordUuid(String fromRecordUuid) {
        this.fromRecordUuid = fromRecordUuid;
    }

    public List<DmsDocExchangeRecordDetailDto> getReceiveDetailDtoList() {
        return receiveDetailDtoList;
    }

    public void setReceiveDetailDtoList(List<DmsDocExchangeRecordDetailDto> receiveDetailDtoList) {
        this.receiveDetailDtoList = receiveDetailDtoList;
    }

    public String getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(String toUserIds) {
        this.toUserIds = toUserIds;
    }

    public String getToUserNames() {
        return toUserNames;
    }

    public void setToUserNames(String toUserNames) {
        this.toUserNames = toUserNames;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUnitName() {
        return userUnitName;
    }

    public void setUserUnitName(String userUnitName) {
        this.userUnitName = userUnitName;
    }

    public Set<DocExchangeNotifyWayEnum> getNotifyWays() {
        return notifyWays;
    }

    public void setNotifyWays(Set<DocExchangeNotifyWayEnum> notifyWays) {
        this.notifyWays = notifyWays;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileUuids() {
        return fileUuids;
    }

    public void setFileUuids(String fileUuids) {
        this.fileUuids = fileUuids;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public DmsDocExchangeForwardDto getForwardDto() {
        return forwardDto;
    }

    public void setForwardDto(DmsDocExchangeForwardDto forwardDto) {
        this.forwardDto = forwardDto;
    }

    public String getFromRecordDetailUuid() {
        return fromRecordDetailUuid;
    }

    public void setFromRecordDetailUuid(String fromRecordDetailUuid) {
        this.fromRecordDetailUuid = fromRecordDetailUuid;
    }

    public Boolean getIsNeedSign() {
        return isNeedSign;
    }

    public void setIsNeedSign(Boolean needSign) {
        isNeedSign = needSign;
    }

    public Boolean getIsNeedFeedback() {
        return isNeedFeedback;
    }

    public void setIsNeedFeedback(Boolean needFeedback) {
        isNeedFeedback = needFeedback;
    }


    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getConfigurationJson() {
        return configurationJson;
    }

    public void setConfigurationJson(String configurationJson) {
        this.configurationJson = configurationJson;
    }

    public Integer getRefuseToView() {
        return refuseToView;
    }

    public void setRefuseToView(Integer refuseToView) {
        this.refuseToView = refuseToView;
    }

    public Integer getNoReminders() {
        return noReminders;
    }

    public void setNoReminders(Integer noReminders) {
        this.noReminders = noReminders;
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(String fromUnitId) {
        this.fromUnitId = fromUnitId;
    }
}
