package com.wellsoft.pt.dms.core.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 文档交换操作数据
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
public class DocExchangeActionData extends ActionData implements Serializable {

    private static final long serialVersionUID = -442387238387793486L;

    private String docExcRecordUuid;//文档交换记录UUID

    private String formUuid;//表单定义UUID

    private String dataUuid;//表单数据UUID;

    private String documentTitle;

    private String remark;//备注

    private DocExcOperateData extraSendData;//补发人员数据

    private DocExcOperateData urgeData;//催办记录数据

    private DocExcOperateData revokeData;//撤回数据

    private DocExcOperateData feedbackData;//反馈意见数据

    private DocExcOperateData forwardData;//转发数据


    public String getDocExcRecordUuid() {
        return docExcRecordUuid;
    }

    public void setDocExcRecordUuid(String docExcRecordUuid) {
        this.docExcRecordUuid = docExcRecordUuid;
    }

    public DocExcOperateData getExtraSendData() {
        return extraSendData;
    }

    public void setExtraSendData(DocExcOperateData extraSendData) {
        this.extraSendData = extraSendData;
    }

    public DocExcOperateData getUrgeData() {
        return urgeData;
    }

    public void setUrgeData(DocExcOperateData urgeData) {
        this.urgeData = urgeData;
    }

    public DocExcOperateData getRevokeData() {
        return revokeData;
    }

    public void setRevokeData(DocExcOperateData revokeData) {
        this.revokeData = revokeData;
    }

    public DocExcOperateData getFeedbackData() {
        return feedbackData;
    }

    public void setFeedbackData(
            DocExcOperateData feedbackData) {
        this.feedbackData = feedbackData;
    }

    public DocExcOperateData getForwardData() {
        return forwardData;
    }

    public void setForwardData(
            DocExcOperateData forwardData) {
        this.forwardData = forwardData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }


    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    //发送人员数据
    public static class ToUserData implements Serializable {

        private static final long serialVersionUID = 3828674667229819517L;

        private String toUserId;

        private String toUserName;

        private Date signTimeLimit;

        private Date feedbackTimeLimit;

        private String receiverUuid;

        private Integer revokeType;

        private List<DocExchangeNotifyWayEnum> notifyWays = Lists.newArrayList();

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public String getToUserName() {
            return toUserName;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public Date getSignTimeLimit() {
            return signTimeLimit;
        }

        public void setSignTimeLimit(Date signTimeLimit) {
            this.signTimeLimit = signTimeLimit;
        }

        public Date getFeedbackTimeLimit() {
            return feedbackTimeLimit;
        }

        public void setFeedbackTimeLimit(Date feedbackTimeLimit) {
            this.feedbackTimeLimit = feedbackTimeLimit;
        }

        public List<DocExchangeNotifyWayEnum> getNotifyWays() {
            return notifyWays;
        }

        public void setNotifyWays(List<DocExchangeNotifyWayEnum> notifyWays) {
            this.notifyWays = notifyWays;
        }

        public String getReceiverUuid() {
            return receiverUuid;
        }

        public void setReceiverUuid(String receiverUuid) {
            this.receiverUuid = receiverUuid;
        }

        public Integer getRevokeType() {
            return revokeType;
        }

        public void setRevokeType(Integer revokeType) {
            this.revokeType = revokeType;
        }
    }


    //操作数据
    public static class DocExcOperateData implements Serializable {


        private String fileUuids;

        private String fileNames;

        private String content;

        private List<ToUserData> toUserData;

        private String operationCode;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<ToUserData> getToUserData() {
            return toUserData;
        }

        public void setToUserData(List<ToUserData> toUserData) {
            this.toUserData = toUserData;
        }

        public String getOperationCode() {
            return operationCode;
        }

        public void setOperationCode(String operationCode) {
            this.operationCode = operationCode;
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
    }


}
