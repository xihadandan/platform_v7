package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 钉钉待办任务表
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Entity
@Table(name = "PT_T_WORK_RECORD")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class WorkRecord extends IdEntity {

    /**
     * 发起待办/待处理
     */
    public static final int RECORD_STATUS_0 = 0;
    /**
     * 发起待办/推送异常
     */
    public static final int RECORD_STATUS_1 = 1;
    /**
     * 更新待办/推送异常
     */
    public static final int RECORD_STATUS_2 = 2;
    /**
     * 发起待办/待推送
     */
    public static final int RECORD_STATUS_3 = 3;
    /**
     * 更新待办/待推送
     */
    public static final int RECORD_STATUS_4 = 4;

    /**
     * 待办同步推送
     */
    public static final String PUSH_MODE_SYNC = "sync";
    /**
     * 待办异步发送
     */
    public static final String PUSH_MODE_ASYNC = "async";

    /**
     *
     */
    private static final long serialVersionUID = 4979344160632393603L;
    private String bizId; // 平台业务id
    private String dingRecordId; // 钉钉待办记录唯一标识
    private String dingUserId;// 钉钉用户userid
    private Date operateTime;
    /**
     * recordStatus:
     * 0:发起待办/待处理
     * 1:发起待办/推送异常
     * 2:更新待办/推送异常
     * 3:发起待办/待推送
     * 4:更新待办/待推送
     */
    private int recordStatus; // 记录状态 0 发起待更新， 1 发起失败， 2
    // 更新失败；对于所有失败状态的数据需要平台纠错处理，异步更新。待办更新成功后，数据将被删除，不保存在数据库表中。

    private String title; // 待办标题
    private String userName; // 用户中文名称
    private String errCode; // 错误码
    private String errMsg; // 错误信息
    private String formItemList; // List<FormItemVo>的json字符串，com.dingtalk.api.request.OapiWorkrecordAddRequest.FormItemVo
    // #title #content
    private String url; // 待办跳转链接

    public String getDingRecordId() {
        return dingRecordId;
    }

    public void setDingRecordId(String dingRecordId) {
        this.dingRecordId = dingRecordId;
    }

    public String getDingUserId() {
        return dingUserId;
    }

    public void setDingUserId(String dingUserId) {
        this.dingUserId = dingUserId;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getFormItemList() {
        return formItemList;
    }

    public void setFormItemList(String formItemList) {
        this.formItemList = formItemList;
    }

}
