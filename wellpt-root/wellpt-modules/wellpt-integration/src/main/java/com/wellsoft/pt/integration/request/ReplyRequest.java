/*
 * @(#)2013-11-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-14.1	zhulh		2013-11-14		Create
 * </pre>
 * @date 2013-11-14
 */
public class ReplyRequest extends Request {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4951421846450792256L;

    // 数据ID
    private String dataId;

    // 数据版本号
    private int recVer;

    // 接入单位ID
    private String unitId;

    // 发生时间
    private Date replyTime;

    // 接收/拒收动作标识（接收(1)/拒收0）
    private int code;

    // 拒收说明
    private String msg;
    private String matterId;

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    /**
     * @return the dataId
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * @param dataId 要设置的dataId
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the recVer
     */
    public int getRecVer() {
        return recVer;
    }

    /**
     * @param recVer 要设置的recVer
     */
    public void setRecVer(int recVer) {
        this.recVer = recVer;
    }

    /**
     * @return the unitId
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * @param unitId 要设置的unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the replyTime
     */
    public Date getReplyTime() {
        return replyTime;
    }

    /**
     * @param replyTime 要设置的replyTime
     */
    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
