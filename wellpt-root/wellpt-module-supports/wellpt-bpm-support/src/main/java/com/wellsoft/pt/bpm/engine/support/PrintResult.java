/*
 * @(#)2014-12-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Description: 打印结果
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-9.1	zhulh		2014-12-9		Create
 * </pre>
 * @date 2014-12-9
 */
public class PrintResult implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3957418821836826566L;

    private InputStream stream;

    private String templateId;
    private String templateUuid;

    private String fileName;

    /**
     * @return the stream
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * @param stream 要设置的stream
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId 要设置的templateId
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the templateUuid
     */
    public String getTemplateUuid() {
        return templateUuid;
    }

    /**
     * @param templateUuid 要设置的templateUuid
     */
    public void setTemplateUuid(String templateUuid) {
        this.templateUuid = templateUuid;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName 要设置的fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
