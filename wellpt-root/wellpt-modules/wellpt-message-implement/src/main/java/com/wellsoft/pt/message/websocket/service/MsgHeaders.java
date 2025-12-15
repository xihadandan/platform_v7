package com.wellsoft.pt.message.websocket.service;

import com.wellsoft.context.util.json.JsonUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author yt
 * @title: MsgHeaders
 * @date 2020/5/27 1:54 下午
 */
@SuppressWarnings("serial")
public class MsgHeaders implements Serializable {

    private String callBackUrl = "/app/callBack";

    private MsgPayLoad callBackJson;

    public MsgHeaders() {
    }

    public MsgHeaders(MsgPayLoad callBackJson) {
        this.callBackJson = callBackJson;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public MsgPayLoad getCallBackJson() {
        return callBackJson;
    }

    public void setCallBackJson(MsgPayLoad callBackJson) {
        this.callBackJson = callBackJson;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("callBackUrl", this.callBackUrl);
        map.put("callBackJson", JsonUtils.object2Json(this.callBackJson));
        return map;
    }
}
