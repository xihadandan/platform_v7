/*
 * @(#)2019年8月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import java.io.Serializable;

/**
 * Description: 验证数据
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月19日.1	zhongzh		2019年8月19日		Create
 * </pre>
 * @date 2019年8月19日
 */
public class ValidateData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private String location;
    private String fieldName;
    private String displayName;
    private String msg;

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * formUuid#idx
     *
     * @param location 要设置的location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName 要设置的displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
