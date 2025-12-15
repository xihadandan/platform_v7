/*
 * @(#)2014-4-30 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 条件查询的请求参数
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-30.1	wubin		2014-4-30		Create
 * </pre>
 * @date 2014-4-30
 */
public class CondSelectAskInfoNew implements Serializable {

    private String optionTitle;//备选标题字段(原title)

    private String optionValue;//备选值（原value）

    private String appointColumn;//备选项对应的列名称

    private String beginTime;//日期查询开始时间

    private String endTime;//日期查询结束时间

    private String searchField;//查询的字段

    private String searchFieldTypeId;//查询的字段的类型Id

    private Boolean isArea;//是否区域查询

    private Boolean isExact;//是否精确查询

    private Boolean isLike;//是否模糊查询

    private String exactValue;//精确查询的条件

    private String searchValue;//查询的字段的值;

    private List<Map<String, String>> keyWords;//关键字

    private String orderbyArr; //点击查询的默认排序方式

    private String orderTitle;//点击查询的标题

    /**
     * @return the searchFieldTypeId
     */
    public String getSearchFieldTypeId() {
        return searchFieldTypeId;
    }

    /**
     * @param searchFieldTypeId 要设置的searchFieldTypeId
     */
    public void setSearchFieldTypeId(String searchFieldTypeId) {
        this.searchFieldTypeId = searchFieldTypeId;
    }

    /**
     * @return the isArea
     */
    public Boolean getIsArea() {
        return isArea;
    }

    /**
     * @param isArea 要设置的isArea
     */
    public void setIsArea(Boolean isArea) {
        this.isArea = isArea;
    }

    /**
     * @return the isExact
     */
    public Boolean getIsExact() {
        return isExact;
    }

    /**
     * @param isExact 要设置的isExact
     */
    public void setIsExact(Boolean isExact) {
        this.isExact = isExact;
    }

    /**
     * @return the isLike
     */
    public Boolean getIsLike() {
        return isLike;
    }

    /**
     * @param isLike 要设置的isLike
     */
    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    /**
     * @return the exactValue
     */
    public String getExactValue() {
        return exactValue;
    }

    /**
     * @param exactValue 要设置的exactValue
     */
    public void setExactValue(String exactValue) {
        this.exactValue = exactValue;
    }

    /**
     * @return the searchValue
     */
    public String getSearchValue() {
        return searchValue;
    }

    /**
     * @param searchValue 要设置的searchValue
     */
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    /**
     * @return the optionTitle
     */
    public String getOptionTitle() {
        return optionTitle;
    }

    /**
     * @param optionTitle 要设置的optionTitle
     */
    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    /**
     * @return the optionValue
     */
    public String getOptionValue() {
        return optionValue;
    }

    /**
     * @param optionValue 要设置的optionValue
     */
    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    /**
     * @return the orderbyArr
     */
    public String getOrderbyArr() {
        return orderbyArr;
    }

    /**
     * @param orderbyArr 要设置的orderbyArr
     */
    public void setOrderbyArr(String orderbyArr) {
        this.orderbyArr = orderbyArr;
    }

    /**
     * @return the orderTitle
     */
    public String getOrderTitle() {
        return orderTitle;
    }

    /**
     * @param orderTitle 要设置的orderTitle
     */
    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    /**
     * @return the appointColumn
     */
    public String getAppointColumn() {
        return appointColumn;
    }

    /**
     * @param appointColumn 要设置的appointColumn
     */
    public void setAppointColumn(String appointColumn) {
        this.appointColumn = appointColumn;
    }

    /**
     * @return the beginTime
     */
    public String getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime 要设置的beginTime
     */
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the searchField
     */
    public String getSearchField() {
        return searchField;
    }

    /**
     * @param searchField 要设置的searchField
     */
    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    /**
     * @return the keyWords
     */
    public List<Map<String, String>> getKeyWords() {
        return keyWords;
    }

    /**
     * @param keyWords 要设置的keyWords
     */
    public void setKeyWords(List<Map<String, String>> keyWords) {
        this.keyWords = keyWords;
    }

}
