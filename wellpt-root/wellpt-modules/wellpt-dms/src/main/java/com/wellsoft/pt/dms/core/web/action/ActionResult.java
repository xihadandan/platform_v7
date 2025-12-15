/*
 * @(#)2017-02-17 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.context.web.controller.ResultMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 操作结果
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-17.1	zhulh		2017-02-17		Create
 * </pre>
 * @date 2017-02-17
 */
public class ActionResult extends ResultMessage {

    public static final String MSG_TYPE_SUCCESS = "success";
    public static final String MSG_TYPE_INFO = "info";
    public static final String MSG_TYPE_WARNING = "warning";
    public static final String MSG_TYPE_ERROR = "error";
    public static final String MSG_TYPE_CONFIRM = "confirm";

    // 关闭窗口
    private boolean close;
    // 刷新窗口
    private boolean refresh;
    // 刷新父窗口
    private boolean refreshParent;
    // 要附加的URL参数，存在替换，不存在附加
    private Map<String, String> appendUrlParams = new HashMap<String, String>(0);
    // 操作结果执行的JS模块，模块不为空时不处理其他属性行为
    private String executeJsModule;
    // 显示提示信息
    private boolean showMsg = true;
    // 操作结果提示
    private StringBuilder msg;
    // 消息提示类型
    private String msgType = MSG_TYPE_INFO;
    // 触发的事件
    private List<String> triggerEvents;

    /**
     * @return the close
     */
    public boolean isClose() {
        return close;
    }

    /**
     * @param close 要设置的close
     */
    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * @return the refresh
     */
    public boolean isRefresh() {
        return refresh;
    }

    /**
     * @param refresh 要设置的refresh
     */
    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * @return the refreshParent
     */
    public boolean isRefreshParent() {
        return refreshParent;
    }

    /**
     * @param refreshParent 要设置的refreshParent
     */
    public void setRefreshParent(boolean refreshParent) {
        this.refreshParent = refreshParent;
    }

    /**
     * @return the appendUrlParams
     */
    public Map<String, String> getAppendUrlParams() {
        return appendUrlParams;
    }

    /**
     * @param appendUrlParams 要设置的appendUrlParams
     */
    public void setAppendUrlParams(Map<String, String> appendUrlParams) {
        this.appendUrlParams = appendUrlParams;
    }

    /**
     * @return the executeJsModule
     */
    public String getExecuteJsModule() {
        return executeJsModule;
    }

    /**
     * @param executeJsModule 要设置的executeJsModule
     */
    public void setExecuteJsModule(String executeJsModule) {
        this.executeJsModule = executeJsModule;
    }

    /**
     * @return the showMsg
     */
    public boolean isShowMsg() {
        return showMsg;
    }

    /**
     * @param showMsg 要设置的showMsg
     */
    public void setShowMsg(boolean showMsg) {
        this.showMsg = showMsg;
    }

    /**
     * @return the msg
     */
    public StringBuilder getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(String msg) {
        this.msg = new StringBuilder(msg);
    }

    /**
     * @return the msgType
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * @param msgType 要设置的msgType
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * @return the triggerEvents
     */
    public List<String> getTriggerEvents() {
        return triggerEvents;
    }

    /**
     * @param triggerEvents 要设置的triggerEvents
     */
    public void setTriggerEvents(List<String> triggerEvents) {
        this.triggerEvents = triggerEvents;
    }

    /**
     * @param eventType
     * @return
     */
    public ActionResult addTriggerEvent(String eventType) {
        if (this.triggerEvents == null) {
            this.triggerEvents = new ArrayList<String>();
        }
        this.triggerEvents.add(eventType);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public ActionResult addAppendUrlParam(String key, String value) {
        if (appendUrlParams == null) {
            appendUrlParams = new HashMap<String, String>();
        }
        appendUrlParams.put(key, value);
        return this;
    }

}
