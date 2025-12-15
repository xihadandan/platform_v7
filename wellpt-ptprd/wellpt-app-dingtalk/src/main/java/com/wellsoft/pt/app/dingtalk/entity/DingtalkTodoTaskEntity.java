/*
 * @(#)4/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/25/25.1	    zhulh		4/25/25		    Create
 * </pre>
 * @date 4/25/25
 */
@javax.persistence.Entity
@Table(name = "dingtalk_todo_task")
@DynamicUpdate
@DynamicInsert
public class DingtalkTodoTaskEntity extends SysEntity {
    private static final long serialVersionUID = -6589248092821651900L;

    private Long configUuid;

    private String appId;

    private String title;

    private String flowInstUuid;

    private String taskInstUuid;

    private String url;

    private String content;

    private String oaUserId;

    private String userUnionId;

    private String ownerUnionId;

    // 钉钉待办任务推送返回的ID
    private String dtTaskId;

    private State state;

    private String errMsg;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the oaUserId
     */
    public String getOaUserId() {
        return oaUserId;
    }

    /**
     * @param oaUserId 要设置的oaUserId
     */
    public void setOaUserId(String oaUserId) {
        this.oaUserId = oaUserId;
    }

    /**
     * @return the userUnionId
     */
    public String getUserUnionId() {
        return userUnionId;
    }

    /**
     * @param userUnionId 要设置的userUnionId
     */
    public void setUserUnionId(String userUnionId) {
        this.userUnionId = userUnionId;
    }

    /**
     * @return the ownerUnionId
     */
    public String getOwnerUnionId() {
        return ownerUnionId;
    }

    /**
     * @param ownerUnionId 要设置的ownerUnionId
     */
    public void setOwnerUnionId(String ownerUnionId) {
        this.ownerUnionId = ownerUnionId;
    }

    /**
     * @return the dtTaskId
     */
    public String getDtTaskId() {
        return dtTaskId;
    }

    /**
     * @param dtTaskId 要设置的dtTaskId
     */
    public void setDtTaskId(String dtTaskId) {
        this.dtTaskId = dtTaskId;
    }

    /**
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @return the errMsg
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * @param errMsg 要设置的errMsg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public enum Type {
        SYSTEM, USER
    }

    public enum State {
        ToSend, Sent, Completed, Cancelled, Deleted
    }

}
