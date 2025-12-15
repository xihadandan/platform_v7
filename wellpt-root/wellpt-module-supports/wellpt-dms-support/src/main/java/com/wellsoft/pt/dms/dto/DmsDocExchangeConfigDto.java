package com.wellsoft.pt.dms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/7/14 14:16
 * @Description:
 */
@ApiModel("公文交换配置")
public class DmsDocExchangeConfigDto implements Serializable {

    private static final long serialVersionUID = 1626145242668L;

    @ApiModelProperty("uuid")
    protected String uuid;

    // 业务名称
    @ApiModelProperty(value = "业务名称", required = true)
    private String name;
    // 业务描述
    @ApiModelProperty(value = "业务描述")
    private String descriptor;

    // 文档交换类型：0 动态表单   1 文件
    @NotNull(message = "文档交换类型不能为空")
    @ApiModelProperty(value = "文档交换类型")
    private Integer exchangeType;
    // 动态表单uuid
    @ApiModelProperty(value = "动态表单uuid")
    private String dyformUuid;
    // 动态表单名称
    @ApiModelProperty(value = "动态表单名称")
    private String dyformName;
    // 文档密级
    @ApiModelProperty(value = "文档密级")
    private Integer docEncryptionLevel;
    @ApiModelProperty(value = "默认文档密级")
    private String defaultEncryptionLevel;
    // 文档缓急程度
    @ApiModelProperty(value = "文档缓急程度")
    private Integer docUrgeLevel;
    @ApiModelProperty(value = "默认文档缓急程度")
    private String defaultUrgeLevel;

    // 业务类别uuid
    @ApiModelProperty(value = "业务类别uuid")
    private String businessCategoryUuid;
    // 收件角色uuid(取业务类别定义的角色)
    @ApiModelProperty(value = "收件角色uuid(取业务类别定义的角色)")
    private String recipientRoleUuid;
    // 发件角色uuid(取业务类别定义的角色)
    @ApiModelProperty(value = "发件角色uuid(取业务类别定义的角色)")
    private String sendRoleUuid;
    // 是否发件审批
    @ApiModelProperty(value = "是否发件审批")
    private Integer approve;
    // 发件审批流程uuid
    @ApiModelProperty(value = "发件审批流程uuid")
    private String flowUuid;


    // 启用签收功能
    @ApiModelProperty(value = "启用签收功能")
    private Integer isNeedSign;
    // 按文档设置签收
    @ApiModelProperty(value = "按文档设置签收")
    private Integer docSign;
    // 默认需要签收
    @ApiModelProperty(value = "默认需要签收")
    private Integer defaultSign;
    // 启用签收时限
    @ApiModelProperty(value = "启用签收时限")
    private Integer signTimeLimit;
    // 签收事件监听
    @ApiModelProperty(value = "签收事件监听")
    private String signEvent;


    // 启用反馈功能
    @ApiModelProperty(value = "启用反馈功能")
    private Integer isNeedFeedback;
    // 按文档设置反馈
    @ApiModelProperty(value = "按文档设置反馈")
    private Integer docFeedback;
    // 默认需要反馈
    @ApiModelProperty(value = "默认需要反馈")
    private Integer defaultFeedback;
    // 启用反馈时限
    @ApiModelProperty(value = "启用反馈时限")
    private Integer feedbackTimeLimit;
    // 反馈事件监听
    @ApiModelProperty(value = "反馈事件监听")
    private String feedbackEvent;

    // 自动办结设置（发件被全部签收或反馈完成后自动办结）
    @ApiModelProperty(value = "自动办结设置（发件被全部签收或反馈完成后自动办结）")
    private Integer autoFinish;
    // 转发设置（收件单位可转发收件）
    @ApiModelProperty(value = "转发设置（收件单位可转发收件）")
    private Integer isForward;
    // 办理过程查看设置（发件单位查看收件单位办理过程相关文档）
    @ApiModelProperty(value = "办理过程查看设置（发件单位查看收件单位办理过程相关文档）")
    private Integer processView;
    // 收件单位可拒绝查看
    @ApiModelProperty(value = "收件单位可拒绝查看")
    private Integer refuseToView;

    // 编辑文档展示单据UUID
    @ApiModelProperty(value = "编辑发件文档展示单据UUID")
    private String dmsDocExchangeDyformUuid;
    // 接收文档展示单据UUID
    @ApiModelProperty(value = "编辑收件文档展示单据UUID")
    private String receiveDyformUuid;


    // 发件可选提醒方式 在线消息，短信，邮件
    @ApiModelProperty(value = "发件可选提醒方式 在线消息，短信，邮件")
    private String notifyTypes;
    // 默认提醒方式  在线消息，短信，邮件
    @ApiModelProperty(value = "默认提醒方式  在线消息，短信，邮件")
    private String defaultNotifyTypes;
    // 发件消息格式UUID
    @ApiModelProperty(value = "发件消息格式UUID")
    private String notifyMsgUuid;


    // 签收提醒 到期前 提前 数量
    @ApiModelProperty(value = "签收提醒 到期前 提前 数量")
    private Integer signBeforeNum;
    // 签收提醒 到期前 提前 单位 （工作日，时分秒等）
    @ApiModelProperty(value = "签收提醒 到期前 提前 单位 （工作日，时分秒等）")
    private Integer signBeforeUnit;
    // 签收提醒 到期前 消息格式UUID
    @ApiModelProperty(value = "签收提醒 到期前 消息格式UUID")
    private String signBeforeMsgUuid;

    // 签收提醒 逾期后 每隔 数量
    @ApiModelProperty(value = "签收提醒 逾期后 每隔 数量")
    private Integer signAfterNum;
    // 签收提醒 逾期后 每隔 单位 （工作日，时分秒等）
    @ApiModelProperty(value = "签收提醒 逾期后 每隔 单位 （工作日，时分秒等）")
    private Integer signAfterUnit;
    // 签收提醒 逾期后 提醒次数
    @ApiModelProperty(value = "签收提醒 逾期后 提醒次数")
    private Integer signAfterFrequency;
    // 签收提醒 逾期后 消息格式UUID
    @ApiModelProperty(value = "签收提醒 逾期后 消息格式UUID")
    private String signAfterMsgUuid;

    // 反馈提醒 到期前 提前 数量
    @ApiModelProperty(value = "反馈提醒 到期前 提前 数量")
    private Integer feedbackBeforeNum;
    // 反馈提醒 到期前 提前 单位 （工作日，时分秒等）
    @ApiModelProperty(value = "反馈提醒 到期前 提前 单位 （工作日，时分秒等）")
    private Integer feedbackBeforeUnit;
    // 反馈提醒 到期前 消息格式UUID
    @ApiModelProperty(value = "反馈提醒 到期前 消息格式UUID")
    private String feedbackBeforeMsgUuid;

    // 反馈提醒 逾期后 每隔 数量
    @ApiModelProperty(value = "反馈提醒 逾期后 每隔 数量")
    private Integer feedbackAfterNum;
    // 反馈提醒 逾期后 每隔 单位 （工作日，时分秒等）
    @ApiModelProperty(value = "反馈提醒 逾期后 每隔 单位 （工作日，时分秒等）")
    private Integer feedbackAfterUnit;
    // 反馈提醒 逾期后 提醒次数
    @ApiModelProperty(value = "反馈提醒 逾期后 提醒次数")
    private Integer feedbackAfterFrequency;
    // 反馈提醒 逾期后 消息格式UUID
    @ApiModelProperty(value = "反馈提醒 逾期后 消息格式UUID")
    private String feedbackAfterMsgUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
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

    public String getBusinessCategoryUuid() {
        return businessCategoryUuid;
    }

    public void setBusinessCategoryUuid(String businessCategoryUuid) {
        this.businessCategoryUuid = businessCategoryUuid;
    }

    public String getRecipientRoleUuid() {
        return recipientRoleUuid;
    }

    public void setRecipientRoleUuid(String recipientRoleUuid) {
        this.recipientRoleUuid = recipientRoleUuid;
    }

    public String getSendRoleUuid() {
        return sendRoleUuid;
    }

    public void setSendRoleUuid(String sendRoleUuid) {
        this.sendRoleUuid = sendRoleUuid;
    }

    public Integer getApprove() {
        return approve;
    }

    public void setApprove(Integer approve) {
        this.approve = approve;
    }

    public String getFlowUuid() {
        return flowUuid;
    }

    public void setFlowUuid(String flowUuid) {
        this.flowUuid = flowUuid;
    }

    public Integer getIsNeedSign() {
        return isNeedSign;
    }

    public void setIsNeedSign(Integer isNeedSign) {
        this.isNeedSign = isNeedSign;
    }

    public Integer getDocSign() {
        return docSign;
    }

    public void setDocSign(Integer docSign) {
        this.docSign = docSign;
    }

    public Integer getDefaultSign() {
        return defaultSign;
    }

    public void setDefaultSign(Integer defaultSign) {
        this.defaultSign = defaultSign;
    }

    public Integer getSignTimeLimit() {
        return signTimeLimit;
    }

    public void setSignTimeLimit(Integer signTimeLimit) {
        this.signTimeLimit = signTimeLimit;
    }

    public String getSignEvent() {
        return signEvent;
    }

    public void setSignEvent(String signEvent) {
        this.signEvent = signEvent;
    }

    public Integer getIsNeedFeedback() {
        return isNeedFeedback;
    }

    public void setIsNeedFeedback(Integer isNeedFeedback) {
        this.isNeedFeedback = isNeedFeedback;
    }

    public Integer getDocFeedback() {
        return docFeedback;
    }

    public void setDocFeedback(Integer docFeedback) {
        this.docFeedback = docFeedback;
    }

    public Integer getDefaultFeedback() {
        return defaultFeedback;
    }

    public void setDefaultFeedback(Integer defaultFeedback) {
        this.defaultFeedback = defaultFeedback;
    }

    public Integer getFeedbackTimeLimit() {
        return feedbackTimeLimit;
    }

    public void setFeedbackTimeLimit(Integer feedbackTimeLimit) {
        this.feedbackTimeLimit = feedbackTimeLimit;
    }

    public String getFeedbackEvent() {
        return feedbackEvent;
    }

    public void setFeedbackEvent(String feedbackEvent) {
        this.feedbackEvent = feedbackEvent;
    }

    public Integer getAutoFinish() {
        return autoFinish;
    }

    public void setAutoFinish(Integer autoFinish) {
        this.autoFinish = autoFinish;
    }

    public Integer getIsForward() {
        return isForward;
    }

    public void setIsForward(Integer isForward) {
        this.isForward = isForward;
    }

    public Integer getProcessView() {
        return processView;
    }

    public void setProcessView(Integer processView) {
        this.processView = processView;
    }

    public Integer getRefuseToView() {
        return refuseToView;
    }

    public void setRefuseToView(Integer refuseToView) {
        this.refuseToView = refuseToView;
    }

    public String getDmsDocExchangeDyformUuid() {
        return dmsDocExchangeDyformUuid;
    }

    public void setDmsDocExchangeDyformUuid(String dmsDocExchangeDyformUuid) {
        this.dmsDocExchangeDyformUuid = dmsDocExchangeDyformUuid;
    }

    public String getNotifyTypes() {
        return notifyTypes;
    }

    public void setNotifyTypes(String notifyTypes) {
        this.notifyTypes = notifyTypes;
    }

    public String getDefaultNotifyTypes() {
        return defaultNotifyTypes;
    }

    public void setDefaultNotifyTypes(String defaultNotifyTypes) {
        this.defaultNotifyTypes = defaultNotifyTypes;
    }

    public String getNotifyMsgUuid() {
        return notifyMsgUuid;
    }

    public void setNotifyMsgUuid(String notifyMsgUuid) {
        this.notifyMsgUuid = notifyMsgUuid;
    }

    public Integer getSignBeforeNum() {
        return signBeforeNum;
    }

    public void setSignBeforeNum(Integer signBeforeNum) {
        this.signBeforeNum = signBeforeNum;
    }

    public Integer getSignBeforeUnit() {
        return signBeforeUnit;
    }

    public void setSignBeforeUnit(Integer signBeforeUnit) {
        this.signBeforeUnit = signBeforeUnit;
    }

    public String getSignBeforeMsgUuid() {
        return signBeforeMsgUuid;
    }

    public void setSignBeforeMsgUuid(String signBeforeMsgUuid) {
        this.signBeforeMsgUuid = signBeforeMsgUuid;
    }

    public Integer getSignAfterNum() {
        return signAfterNum;
    }

    public void setSignAfterNum(Integer signAfterNum) {
        this.signAfterNum = signAfterNum;
    }

    public Integer getSignAfterUnit() {
        return signAfterUnit;
    }

    public void setSignAfterUnit(Integer signAfterUnit) {
        this.signAfterUnit = signAfterUnit;
    }

    public Integer getSignAfterFrequency() {
        return signAfterFrequency;
    }

    public void setSignAfterFrequency(Integer signAfterFrequency) {
        this.signAfterFrequency = signAfterFrequency;
    }

    public String getSignAfterMsgUuid() {
        return signAfterMsgUuid;
    }

    public void setSignAfterMsgUuid(String signAfterMsgUuid) {
        this.signAfterMsgUuid = signAfterMsgUuid;
    }

    public Integer getFeedbackBeforeNum() {
        return feedbackBeforeNum;
    }

    public void setFeedbackBeforeNum(Integer feedbackBeforeNum) {
        this.feedbackBeforeNum = feedbackBeforeNum;
    }

    public Integer getFeedbackBeforeUnit() {
        return feedbackBeforeUnit;
    }

    public void setFeedbackBeforeUnit(Integer feedbackBeforeUnit) {
        this.feedbackBeforeUnit = feedbackBeforeUnit;
    }

    public String getFeedbackBeforeMsgUuid() {
        return feedbackBeforeMsgUuid;
    }

    public void setFeedbackBeforeMsgUuid(String feedbackBeforeMsgUuid) {
        this.feedbackBeforeMsgUuid = feedbackBeforeMsgUuid;
    }

    public Integer getFeedbackAfterNum() {
        return feedbackAfterNum;
    }

    public void setFeedbackAfterNum(Integer feedbackAfterNum) {
        this.feedbackAfterNum = feedbackAfterNum;
    }

    public Integer getFeedbackAfterUnit() {
        return feedbackAfterUnit;
    }

    public void setFeedbackAfterUnit(Integer feedbackAfterUnit) {
        this.feedbackAfterUnit = feedbackAfterUnit;
    }

    public Integer getFeedbackAfterFrequency() {
        return feedbackAfterFrequency;
    }

    public void setFeedbackAfterFrequency(Integer feedbackAfterFrequency) {
        this.feedbackAfterFrequency = feedbackAfterFrequency;
    }

    public String getFeedbackAfterMsgUuid() {
        return feedbackAfterMsgUuid;
    }

    public void setFeedbackAfterMsgUuid(String feedbackAfterMsgUuid) {
        this.feedbackAfterMsgUuid = feedbackAfterMsgUuid;
    }

    public String getDyformName() {
        return dyformName;
    }

    public void setDyformName(String dyformName) {
        this.dyformName = dyformName;
    }

    public String getReceiveDyformUuid() {
        return receiveDyformUuid;
    }

    public void setReceiveDyformUuid(String receiveDyformUuid) {
        this.receiveDyformUuid = receiveDyformUuid;
    }

    public String getDefaultEncryptionLevel() {
        return defaultEncryptionLevel;
    }

    public void setDefaultEncryptionLevel(String defaultEncryptionLevel) {
        this.defaultEncryptionLevel = defaultEncryptionLevel;
    }

    public String getDefaultUrgeLevel() {
        return defaultUrgeLevel;
    }

    public void setDefaultUrgeLevel(String defaultUrgeLevel) {
        this.defaultUrgeLevel = defaultUrgeLevel;
    }
}
