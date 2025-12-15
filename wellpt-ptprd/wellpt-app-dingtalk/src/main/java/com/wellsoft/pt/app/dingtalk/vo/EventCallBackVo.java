package com.wellsoft.pt.app.dingtalk.vo;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/12/10.1	liuyz		2021/12/10		Create
 * </pre>
 * @date 2021/12/10
 */
@Deprecated
public class EventCallBackVo {
    private String eventType; // 事件类型
    private String eventName; // 事件名称
    private String dingTimeStamp; // 时间戳
    private String dingUserId; // 用户发生变更的userid列表
    private String dingDeptId; // 部门发生变更的deptId列表
    private String dingLabelId; // 角色发生变更的deptId列表
    private String dingCorpId; // 发生通讯录变更的企业
    private int status; // 数据状态 0：未处理 1：已处理 2：处理失败
    private Date optTime; // 处理时间
    private String remark; // 备注，status为2失败时，需要写入失败原因到备注中
    private String syncContent; // 同步内容
    private int isMultiDepts; // 是否多部门人员
    private int deptStatus;
    private int userStatus;
    private int userWorkStatus;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDingTimeStamp() {
        return dingTimeStamp;
    }

    public void setDingTimeStamp(String dingTimeStamp) {
        this.dingTimeStamp = dingTimeStamp;
    }

    public String getDingUserId() {
        return dingUserId;
    }

    public void setDingUserId(String dingUserId) {
        this.dingUserId = dingUserId;
    }

    public String getDingDeptId() {
        return dingDeptId;
    }

    public void setDingDeptId(String dingDeptId) {
        this.dingDeptId = dingDeptId;
    }

    public String getDingLabelId() {
        return dingLabelId;
    }

    public void setDingLabelId(String dingLabelId) {
        this.dingLabelId = dingLabelId;
    }

    public String getDingCorpId() {
        return dingCorpId;
    }

    public void setDingCorpId(String dingCorpId) {
        this.dingCorpId = dingCorpId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSyncContent() {
        return syncContent;
    }

    public void setSyncContent(String syncContent) {
        this.syncContent = syncContent;
    }

    public int getIsMultiDepts() {
        return isMultiDepts;
    }

    public void setIsMultiDepts(int isMultiDepts) {
        this.isMultiDepts = isMultiDepts;
    }

    public int getDeptStatus() {
        return deptStatus;
    }

    public void setDeptStatus(int deptStatus) {
        this.deptStatus = deptStatus;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserWorkStatus() {
        return userWorkStatus;
    }

    public void setUserWorkStatus(int userWorkStatus) {
        this.userWorkStatus = userWorkStatus;
    }
}
