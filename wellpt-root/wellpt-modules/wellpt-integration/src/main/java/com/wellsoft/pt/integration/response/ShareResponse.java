/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.response;

import com.wellsoft.pt.integration.support.StreamingData;

import javax.xml.bind.annotation.XmlType;
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
 * 2014-5-21.1	ruanhg		2014-5-21		Create
 * </pre>
 * @date 2014-5-21
 */
@XmlType(name = "ShareResponse")
public class ShareResponse extends Response {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5498475965981371965L;

    private List<String> records;

    private int totalRow;

    private Map<String, String> params;

    private List<StreamingData> streamingDatas;

    public List<String> getRecords() {
        return records;
    }

    public void setRecords(List<String> records) {
        this.records = records;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public List<StreamingData> getStreamingDatas() {
        return streamingDatas;
    }

    public void setStreamingDatas(List<StreamingData> streamingDatas) {
        this.streamingDatas = streamingDatas;
    }

}
