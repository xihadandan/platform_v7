/*
 * @(#)4/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support.groupchat;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/11/25.1	    zhulh		4/11/25		    Create
 * </pre>
 * @date 4/11/25
 */
public class StartGroupChat extends BaseObject {
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;

    @ApiModelProperty("流程实例标题")
    private String title;

    @ApiModelProperty("群名称")
    private String groupName;

    @ApiModelProperty("群成员ID，以分号隔开")
    private String memberIds;

    @ApiModelProperty("群主")
    private String ownerId;

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
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName 要设置的groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the memberIds
     */
    public String getMemberIds() {
        return memberIds;
    }

    /**
     * @param memberIds 要设置的memberIds
     */
    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
