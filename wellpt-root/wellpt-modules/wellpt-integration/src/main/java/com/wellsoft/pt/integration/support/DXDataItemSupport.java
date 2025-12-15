/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

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

public class DXDataItemSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8010156681232127855L;

    private String dataId;// (统一查询号)

    private int dataRecVer;// 版本号

    private String formUuid;//发送的表单id

    private String formDataUuid;//发送的表单数据id

    private Map<String, String> params;

    private String text;

    private Map<String, Map<String, Object>> xmlData;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getDataRecVer() {
        return dataRecVer;
    }

    public void setDataRecVer(int dataRecVer) {
        this.dataRecVer = dataRecVer;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getFormDataUuid() {
        return formDataUuid;
    }

    public void setFormDataUuid(String formDataUuid) {
        this.formDataUuid = formDataUuid;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Map<String, Object>> getXmlData() {
        return xmlData;
    }

    public void setXmlData(Map<String, Map<String, Object>> xmlData) {
        this.xmlData = xmlData;
    }

}
