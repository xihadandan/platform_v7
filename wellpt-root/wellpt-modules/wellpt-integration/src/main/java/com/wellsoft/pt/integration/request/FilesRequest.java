/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

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
public class FilesRequest extends Request {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3140568573454128622L;

    private String batchId;

    private List<FileData> fileDatas;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public List<FileData> getFileDatas() {
        return fileDatas;
    }

    public void setFileDatas(List<FileData> fileDatas) {
        this.fileDatas = fileDatas;
    }

}
