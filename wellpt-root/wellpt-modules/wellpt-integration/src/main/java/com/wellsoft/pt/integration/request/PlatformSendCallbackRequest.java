/*
 * @(#)2013-11-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

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
public class PlatformSendCallbackRequest extends Request {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8237428446894007008L;

    //	private String batchId;

    private String dataId;

    private int recVer;

    // 接入单位ID
    private String unitId;

    private int code;

    private String msg;
    private String matterId;

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    /**
     * @return the batchId
     */
    //	public String getBatchId() {
    //		return batchId;
    //	}
    //
    //	/**
    //	 * @param batchId 要设置的batchId
    //	 */
    //	public void setBatchId(String batchId) {
    //		this.batchId = batchId;
    //	}

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

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

}
