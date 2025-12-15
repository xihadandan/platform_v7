/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-5-6.1	ruanhg		2014-5-6		Create
 * </pre>
 * @date 2014-5-6
 */

public class DXExchangeSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8010156681232127855L;

    private String batchId;//批次号

    private String fromId;//发送单位

    private String fromUser;//发送用户id

    private String toId;//接收单位

    private String cc;//抄送

    private String bcc;//密送

    private String typeId;//数据类型

    private List<DXDataItemSupport> dataList;

    private Map<String, String> sendStatusMap;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<DXDataItemSupport> getDataList() {
        return dataList;
    }

    public void setDataList(List<DXDataItemSupport> dataList) {
        this.dataList = dataList;
    }

    public Map<String, String> getSendStatusMap() {
        return sendStatusMap;
    }

    public void setSendStatusMap(Map<String, String> sendStatusMap) {
        this.sendStatusMap = sendStatusMap;
    }

}
