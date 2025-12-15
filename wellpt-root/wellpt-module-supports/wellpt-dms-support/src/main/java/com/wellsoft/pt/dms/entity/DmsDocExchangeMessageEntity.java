package com.wellsoft.pt.dms.entity;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Map;

/**
 * Description: 文档交换消息实体（非数据库表映射），仅提供平台消息格式封装
 *
 * @author chenq
 * @date 2018/5/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/23    chenq		2018/5/23		Create
 * </pre>
 */
public class DmsDocExchangeMessageEntity extends TenantEntity {


    public static final String DOC_EXCHANGE_RECEIVE_MSG_TEMPLATE = "DOC_EXCHANGER_RECEIVE_NOTIFY";//收文通知消息格式ID
    public static final String DOC_EXCHANGE_REVOKE_MSG_TEMPLATE = "DOC_EXCHANGER_REVOKE_NOTIFY";//撤回通知消息格式ID
    public static final String DOC_EXCHANGER_URGE_MSG_TEMPLATE = "DOC_EXCHANGER_URGE_NOTIFY";//催办通知格式ID
    public static final String DOC_EXCHANGER_REQUEST_FEEDBACK_AGAIN_MSG_TEMPLATE = "DOC_EXCHANGER_FEEDBACK_AGAIN_NOTIFY";//要求再次反馈通知格式ID
    private static final long serialVersionUID = 8621963514294819773L;
    private String documentTitle;//文档交换的标题


    private String sendTime;//发送时间

    private String fromUnitName;//来源单位

    private String fromUserName;//来源用户名


    private String fileNames;//交换附件名称

    private Map<String, Object> formData = Maps.newHashMap(); //表单数据

    private String signTimeLimit;//签收时限

    private String feedbackTimeLimit;//反馈时限

    private String remark;//备注


    public DmsDocExchangeMessageEntity() {

    }

    public DmsDocExchangeMessageEntity(Date sendTime, String fromUnitName,
                                       String fromUserName,
                                       String fileNames,
                                       Map<String, Object> formData, Date signTimeLimit,
                                       Date feedbackTimeLimit,
                                       String documentTitle, String remark) {
        this.sendTime = sendTime != null ? DateFormatUtils.format(sendTime,
                "yyyy-MM-dd HH:mm:ss") : null;
        this.fromUnitName = fromUnitName;
        this.fromUserName = fromUserName;
        this.fileNames = fileNames;
        this.formData = formData;
        this.signTimeLimit = signTimeLimit != null ? DateFormatUtils.format(signTimeLimit,
                "yyyy-MM-dd HH:mm") : null;
        this.feedbackTimeLimit = feedbackTimeLimit != null ? DateFormatUtils.format(
                feedbackTimeLimit, "yyyy-MM-dd HH:mm") : null;
        this.documentTitle = documentTitle;
        this.remark = remark;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSignTimeLimit() {
        return signTimeLimit;
    }

    public void setSignTimeLimit(String signTimeLimit) {
        this.signTimeLimit = signTimeLimit;
    }

    public String getFromUnitName() {
        return fromUnitName;
    }

    public void setFromUnitName(String fromUnitName) {
        this.fromUnitName = fromUnitName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }


    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }


    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getFeedbackTimeLimit() {
        return feedbackTimeLimit;
    }

    public void setFeedbackTimeLimit(String feedbackTimeLimit) {
        this.feedbackTimeLimit = feedbackTimeLimit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
