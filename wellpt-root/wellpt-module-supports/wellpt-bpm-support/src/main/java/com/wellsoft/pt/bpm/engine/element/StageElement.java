/*
 * @(#)2018年6月2日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月2日.1	zhulh		2018年6月2日		Create
 * </pre>
 * @date 2018年6月2日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StageElement extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 381852208422344445L;

    private String newFlowId;
    private String newFlowName;
    private String taskUsers;
    private String taskUsersName;
    private String createInstanceWay;
    private String limitTime;
    private String limitTimeName;

    /**
     * @return the newFlowId
     */
    public String getNewFlowId() {
        return newFlowId;
    }

    /**
     * @param newFlowId 要设置的newFlowId
     */
    public void setNewFlowId(String newFlowId) {
        this.newFlowId = newFlowId;
    }

    /**
     * @return the newFlowName
     */
    public String getNewFlowName() {
        return newFlowName;
    }

    /**
     * @param newFlowName 要设置的newFlowName
     */
    public void setNewFlowName(String newFlowName) {
        this.newFlowName = newFlowName;
    }

    /**
     * @return the taskUsers
     */
    public String getTaskUsers() {
        return taskUsers;
    }

    /**
     * @param taskUsers 要设置的taskUsers
     */
    public void setTaskUsers(String taskUsers) {
        this.taskUsers = taskUsers;
    }

    /**
     * @return the taskUsersName
     */
    public String getTaskUsersName() {
        return taskUsersName;
    }

    /**
     * @param taskUsersName 要设置的taskUsersName
     */
    public void setTaskUsersName(String taskUsersName) {
        this.taskUsersName = taskUsersName;
    }

    /**
     * @return the createInstanceWay
     */
    public String getCreateInstanceWay() {
        return createInstanceWay;
    }

    /**
     * @param createInstanceWay 要设置的createInstanceWay
     */
    public void setCreateInstanceWay(String createInstanceWay) {
        this.createInstanceWay = createInstanceWay;
    }

    /**
     * @return the limitTime
     */
    public String getLimitTime() {
        return limitTime;
    }

    /**
     * @param limitTime 要设置的limitTime
     */
    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    /**
     * @return the limitTimeName
     */
    public String getLimitTimeName() {
        return limitTimeName;
    }

    /**
     * @param limitTimeName 要设置的limitTimeName
     */
    public void setLimitTimeName(String limitTimeName) {
        this.limitTimeName = limitTimeName;
    }

}
