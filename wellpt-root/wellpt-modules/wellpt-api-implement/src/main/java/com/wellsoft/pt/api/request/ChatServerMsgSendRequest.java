/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ChatServerMsgSendRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-28 下午6:15:01
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ChatServerMsgSendResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ChatServerMsgSendRequest
 * @Description: TODO
 * @date 2014-12-28 下午6:15:01
 */
public class ChatServerMsgSendRequest extends WellptRequest<ChatServerMsgSendResponse> {
    /**
     * @Fields message : TODO
     */
    private String content;
    private String appId;
    private String userIds;

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.CHATSERVER_MESSAGE_SENG;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ChatServerMsgSendResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ChatServerMsgSendResponse.class;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the userIds
     */
    public String getUserIds() {
        return userIds;
    }

    /**
     * @param userIds the userIds to set
     */
    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
}
