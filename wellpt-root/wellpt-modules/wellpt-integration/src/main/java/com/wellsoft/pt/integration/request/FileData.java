/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

import com.wellsoft.pt.integration.support.StreamingData;

import java.util.ArrayList;
import java.util.List;

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
public class FileData {

    private String title;

    private List<StreamingData> streamingDatas = new ArrayList<StreamingData>(0);

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<StreamingData> getStreamingDatas() {
        return streamingDatas;
    }

    public void setStreamingDatas(List<StreamingData> streamingDatas) {
        this.streamingDatas = streamingDatas;
    }

}
