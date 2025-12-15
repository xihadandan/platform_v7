/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

import com.wellsoft.pt.integration.support.StreamingData;

import java.util.ArrayList;
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
 * 2014-6-10.1	ruanhg		2014-6-10		Create
 * </pre>
 * @date 2014-6-10
 */
public class DXRequestByModule extends Request {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3407029288275432232L;

    private String from;

    private String to;

    private String cc;

    private String bcc;

    private String typeId;

    private String dataId;

    private Integer recVer;

    private Map<String, String> params;

    private String text;

    private List<StreamingData> streamingDatas = new ArrayList<StreamingData>(0);

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from 要设置的from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to 要设置的to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * @param cc 要设置的cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * @return the bcc
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * @param bcc 要设置的bcc
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * @return the typeId
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * @param typeId 要设置的typeId
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<StreamingData> getStreamingDatas() {
        return streamingDatas;
    }

    public void setStreamingDatas(List<StreamingData> streamingDatas) {
        this.streamingDatas = streamingDatas;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

}
