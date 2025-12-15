package com.wellsoft.pt.message.websocket.service;

import java.io.Serializable;

/**
 * @author yt
 * @title: MsgReqPayLoad
 * @date 2020/5/27 12:03 下午
 */
@SuppressWarnings("serial")
public class MsgPayLoad<T> implements Serializable {


    private MsgTypeEnum type;

    private T data;

    public MsgPayLoad() {

    }

    public MsgPayLoad(MsgTypeEnum type, T data) {
        this.type = type;
        this.data = data;
    }

    public MsgTypeEnum getType() {
        return type;
    }

    public void setType(MsgTypeEnum type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
