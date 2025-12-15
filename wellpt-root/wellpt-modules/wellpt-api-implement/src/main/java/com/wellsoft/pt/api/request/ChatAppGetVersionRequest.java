/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ChatAppGetVersionRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:16:50
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ChatAppGetVersionResponse;

/**
 * @author Administrator
 * @ClassName: ChatAppGetVersionRequest
 * @Description: TODO
 * @date 2014-12-11 上午9:16:50
 */
public class ChatAppGetVersionRequest extends WellptRequest<ChatAppGetVersionResponse> {

    /**
     * @Fields appostype : 客户端系统类型，1为android，2为ios
     */
    private int appostype;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.CHATAPP_GETVERSION;
    }

    @Override
    public Class<ChatAppGetVersionResponse> getResponseClass() {
        return ChatAppGetVersionResponse.class;
    }

    /**
     * @return the appostype
     */
    public int getAppostype() {
        return appostype;
    }

    /**
     * @param appostype the appostype to set
     */
    public void setAppostype(int appostype) {
        this.appostype = appostype;
    }
}
