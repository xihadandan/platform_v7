/*
 * @(#)10/23/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/23/23.1	zhulh		10/23/23		Create
 * </pre>
 * @date 10/23/23
 */
public class SnSerialNumberDefinitionUsedQueryItem implements BaseQueryItem {

    // 流水号名称
    private String name;

    // 流水号ID
    private String id;

    // 使用的功能类型
    private String functionType;

    // 使用的模块
    private String functionName;

    // 使用的功能名称
    private String functionItemName;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the functionType
     */
    public String getFunctionType() {
        return functionType;
    }

    /**
     * @param functionType 要设置的functionType
     */
    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * @param functionName 要设置的functionName
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * @return the functionItemName
     */
    public String getFunctionItemName() {
        return functionItemName;
    }

    /**
     * @param functionItemName 要设置的functionItemName
     */
    public void setFunctionItemName(String functionItemName) {
        this.functionItemName = functionItemName;
    }
}
