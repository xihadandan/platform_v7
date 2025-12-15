/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import java.io.Serializable;
import java.util.HashMap;
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
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public class IexportDataRecordSet implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3020484795804528792L;

    private Object rawData;

    private IexportDataMetaData iexportDataMetaData;

    private String pkName;

    private String fkName;

    private String pkValue;

    private String fkValue;

    private Map<String, Object> data;

    private Map<String, List<IexportDataRecordSet>> listData = new HashMap<String, List<IexportDataRecordSet>>();

    /**
     * @return the rawData
     */
    public Object getRawData() {
        return rawData;
    }

    /**
     * @param rawData 要设置的rawData
     */
    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }

    /**
     * @return the iexportDataMetaData
     */
    public IexportDataMetaData getIexportDataMetaData() {
        return iexportDataMetaData;
    }

    /**
     * @param iexportDataMetaData 要设置的iexportDataMetaData
     */
    public void setIexportDataMetaData(IexportDataMetaData iexportDataMetaData) {
        this.iexportDataMetaData = iexportDataMetaData;
    }

    /**
     * @return the pkName
     */
    public String getPkName() {
        return pkName;
    }

    /**
     * @param pkName 要设置的pkName
     */
    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    /**
     * @return the pkValue
     */
    public String getPkValue() {
        return pkValue;
    }

    /**
     * @param pkValue 要设置的pkValue
     */
    public void setPkValue(String pkValue) {
        this.pkValue = pkValue;
    }

    /**
     * @return the fkName
     */
    public String getFkName() {
        return fkName;
    }

    /**
     * @param fkName 要设置的fkName
     */
    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    /**
     * @return the fkValue
     */
    public String getFkValue() {
        return fkValue;
    }

    /**
     * @param fkValue 要设置的fkValue
     */
    public void setFkValue(String fkValue) {
        this.fkValue = fkValue;
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @return the listData
     */
    public Map<String, List<IexportDataRecordSet>> getListData() {
        return listData;
    }

    /**
     * @param listData 要设置的listData
     */
    public void setListData(Map<String, List<IexportDataRecordSet>> listData) {
        this.listData = listData;
    }

}
