/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * Description: 数据库表DMS_DOC_EXCHANGE_RELATED_DOC的对应的DTO类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-22.1	leo		2021-07-22		Create
 * </pre>
 * @date 2021-07-22
 */
public class DmsDocExchangeRelatedDocDto implements Serializable {

    private static final long serialVersionUID = 1626952335008L;

    // 文档交换-记录uuid
    private String docExchangeRecordUuid;
    // 来源文档交换-记录明细uuid
    private String fromRecordDetailUuid;
    // 处理方式
    private String processingMethod;
    // 文档标题
    private String docTitle;
    // 文档链接
    private String docLink;
    //创建时间
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFromRecordDetailUuid() {
        return fromRecordDetailUuid;
    }

    public void setFromRecordDetailUuid(String fromRecordDetailUuid) {
        this.fromRecordDetailUuid = fromRecordDetailUuid;
    }

    /**
     * @return the docExchangeRecordUuid
     */
    public String getDocExchangeRecordUuid() {
        return this.docExchangeRecordUuid;
    }

    /**
     * @param docExchangeRecordUuid
     */
    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    /**
     * @return the docTitle
     */
    public String getDocTitle() {
        return this.docTitle;
    }

    /**
     * @param docTitle
     */
    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    /**
     * @return the processingMethod
     */
    public String getProcessingMethod() {
        return this.processingMethod;
    }

    /**
     * @param processingMethod
     */
    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }

    /**
     * @return the docLink
     */
    public String getDocLink() {
        return this.docLink;
    }

    /**
     * @param docLink
     */
    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

}
