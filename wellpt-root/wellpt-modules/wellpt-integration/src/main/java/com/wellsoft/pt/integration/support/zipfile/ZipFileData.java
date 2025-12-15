/*
 * @(#)2013-12-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.zipfile;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;

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
 * 2013-12-27.1	zhulh		2013-12-27		Create
 * </pre>
 * @date 2013-12-27
 */
public class ZipFileData {

    private String typeId;

    private String batchId;

    private String dataId;

    private int recVer;

    private Map<String, String> headerMap;

    private Map<String, Integer> extendHeadMap;

    private Map<String, Object> displayDataMap;

    private Map<String, Object> valueDataMap;

    private DyFormData dyFormData;

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
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId 要设置的batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

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
     * @return the headerMap
     */
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    /**
     * @param headerMap 要设置的headerMap
     */
    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    /**
     * @return the extendHeadMap
     */
    public Map<String, Integer> getExtendHeadMap() {
        return extendHeadMap;
    }

    /**
     * @param extendHeadMap 要设置的extendHeadMap
     */
    public void setExtendHeadMap(Map<String, Integer> extendHeadMap) {
        this.extendHeadMap = extendHeadMap;
    }

    /**
     * @return the displayDataMap
     */
    public Map<String, Object> getDisplayDataMap() {
        return displayDataMap;
    }

    /**
     * @param displayDataMap 要设置的displayDataMap
     */
    public void setDisplayDataMap(Map<String, Object> displayDataMap) {
        this.displayDataMap = displayDataMap;
    }

    /**
     * @return the valueDataMap
     */
    public Map<String, Object> getValueDataMap() {
        return valueDataMap;
    }

    /**
     * @param valueDataMap 要设置的valueDataMap
     */
    public void setValueDataMap(Map<String, Object> valueDataMap) {
        this.valueDataMap = valueDataMap;
    }

    public DyFormData getDyFormData() {
        return dyFormData;
    }

    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

}
