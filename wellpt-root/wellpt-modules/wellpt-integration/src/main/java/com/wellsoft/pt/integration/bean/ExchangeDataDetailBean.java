package com.wellsoft.pt.integration.bean;

import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.unit.entity.CommonUnit;

import java.util.Date;
import java.util.List;

/**
 * Description: ExchangeDataDetailBean
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-1.1	wangbx		2013-12-1		Create
 * </pre>
 * @date 2013-12-1
 */
public class ExchangeDataDetailBean {
    //数据类型id
    private String typeId;
    //数据
    private String typeName;
    //是否显示收件人（连同抄送、密送）
    private Boolean showToUnit;
    //数据类型绑定的动态表单id
    private String formId;
    //动态表单uuid
    private String dataUuid;
    //exchangeData uuid
    private String uuid;
    //源发送单位
    private String fromId;
    //源发送单位
    private String fromUnitName;
    //目标接收单位
    private String to;
    //抄送
    private String cc;
    //密送
    private String bcc;
    //统一查询号
    private String dataId;
    //数据版本号
    private Integer recVer;
    //所有收件单位（包括路由匹配得出的单位）
    private String allUnit;
    //预留字段，值为列表的标题
    private String title;
    //获取市下属区单位
    private List<CommonUnit> unitList;
    //是否有下属区单位
    private int hasUnderling;
    //按钮资源
    private List<Resource> btns;
    //	发送环节
    //	ing发送中
    //	end已到达
    //	sign已签收完
    //	abnormal未送达
    //	back退回件
    //	examineIng 待审核
    //	examineFail 未通过审核
    //	examineClose 审核关闭
    private String sendNode;
    //接收环节 1、wait待收   2、sign已签收完  3、back退回件
    private String receiveNode;
    private Integer sendLimitNum; //上传逾期天数
    //接收逾期天数
    private Integer replyLimitNum; //接收逾期天数
    //路由情况
    private List<ExchangeDataMonitorBean> exchangeDataMonitors;
    //是否有能注销的 unitId:unitName;unitId:unitName
    private String cancelUnits;
    //是否显示退回按钮(0、不能退回   1、能退回)
    private Integer showRefuse;

    private String fromUserName;

    private String examineFailMsg;
    //起草时间
    private Date draftTime;
    //起草人
    private String drafter;
    //发布人
    private String releaser;
    //发布时间
    private Date releaseTime;

    private String sendType;

    private String userUnitId;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getShowToUnit() {
        return showToUnit;
    }

    public void setShowToUnit(Boolean showToUnit) {
        this.showToUnit = showToUnit;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFromUnitName() {
        return fromUnitName;
    }

    public void setFromUnitName(String fromUnitName) {
        this.fromUnitName = fromUnitName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public String getAllUnit() {
        return allUnit;
    }

    public void setAllUnit(String allUnit) {
        this.allUnit = allUnit;
    }

    public List<ExchangeDataMonitorBean> getExchangeDataMonitors() {
        return exchangeDataMonitors;
    }

    public void setExchangeDataMonitors(List<ExchangeDataMonitorBean> exchangeDataMonitors) {
        this.exchangeDataMonitors = exchangeDataMonitors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommonUnit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<CommonUnit> unitList) {
        this.unitList = unitList;
    }

    public int getHasUnderling() {
        return hasUnderling;
    }

    public void setHasUnderling(int hasUnderling) {
        this.hasUnderling = hasUnderling;
    }

    public List<Resource> getBtns() {
        return btns;
    }

    public void setBtns(List<Resource> btns) {
        this.btns = btns;
    }

    public String getSendNode() {
        return sendNode;
    }

    public void setSendNode(String sendNode) {
        this.sendNode = sendNode;
    }

    public String getReceiveNode() {
        return receiveNode;
    }

    public void setReceiveNode(String receiveNode) {
        this.receiveNode = receiveNode;
    }

    public Integer getSendLimitNum() {
        return sendLimitNum;
    }

    public void setSendLimitNum(Integer sendLimitNum) {
        this.sendLimitNum = sendLimitNum;
    }

    public Integer getReplyLimitNum() {
        return replyLimitNum;
    }

    public void setReplyLimitNum(Integer replyLimitNum) {
        this.replyLimitNum = replyLimitNum;
    }

    public String getCancelUnits() {
        return cancelUnits;
    }

    public void setCancelUnits(String cancelUnits) {
        this.cancelUnits = cancelUnits;
    }

    public Integer getShowRefuse() {
        return showRefuse;
    }

    public void setShowRefuse(Integer showRefuse) {
        this.showRefuse = showRefuse;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getExamineFailMsg() {
        return examineFailMsg;
    }

    public void setExamineFailMsg(String examineFailMsg) {
        this.examineFailMsg = examineFailMsg;
    }

    public Date getDraftTime() {
        return draftTime;
    }

    public void setDraftTime(Date draftTime) {
        this.draftTime = draftTime;
    }

    public String getDrafter() {
        return drafter;
    }

    public void setDrafter(String drafter) {
        this.drafter = drafter;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getReleaser() {
        return releaser;
    }

    public void setReleaser(String releaser) {
        this.releaser = releaser;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getUserUnitId() {
        return userUnitId;
    }

    public void setUserUnitId(String userUnitId) {
        this.userUnitId = userUnitId;
    }

}
