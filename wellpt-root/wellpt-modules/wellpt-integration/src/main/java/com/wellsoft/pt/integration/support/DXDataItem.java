/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class DXDataItem implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -9061886370990483561L;

    // (统一查询号)
    private String dataId;

    // 版本号
    private int recVer;

    private Map<String, String> params;

    private String text;

    private List<StreamingData> streamingDatas = new ArrayList<StreamingData>(0);

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

    /**
     * @return the streamingDatas
     */
    public List<StreamingData> getStreamingDatas() {
        return streamingDatas;
    }

    /**
     * @param streamingDatas 要设置的streamingDatas
     */
    public void setStreamingDatas(List<StreamingData> streamingDatas) {
        this.streamingDatas = streamingDatas;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
