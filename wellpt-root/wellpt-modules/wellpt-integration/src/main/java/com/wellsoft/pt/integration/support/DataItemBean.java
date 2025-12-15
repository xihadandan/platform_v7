/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import org.apache.activemq.BlobMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-8.1	zhulh		2013-11-8		Create
 * </pre>
 * @date 2013-11-8
 */
public class DataItemBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -9061886370990483561L;

    // (统一查询号)
    private String dataId;

    // 版本号
    private int recVer;

    // 关联ID
    private String correlationId;

    // 关联版本号
    private int correlationRecVer;

    private String text;

    private List<BlobMessage> blobMessages;

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
     * @return the correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * @param correlationId 要设置的correlationId
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * @return the correlationRecVer
     */
    public int getCorrelationRecVer() {
        return correlationRecVer;
    }

    /**
     * @param correlationRecVer 要设置的correlationRecVer
     */
    public void setCorrelationRecVer(int correlationRecVer) {
        this.correlationRecVer = correlationRecVer;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    public List<BlobMessage> getBlobMessages() {
        return blobMessages;
    }

    public void setBlobMessages(List<BlobMessage> blobMessages) {
        this.blobMessages = blobMessages;
    }

}
