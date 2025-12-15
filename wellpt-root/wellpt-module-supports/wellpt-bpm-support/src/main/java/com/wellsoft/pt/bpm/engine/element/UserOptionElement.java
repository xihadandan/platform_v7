/*
 * @(#)12/10/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/10/24.1	    zhulh		12/10/24		    Create
 * </pre>
 * @date 12/10/24
 */
public class UserOptionElement extends BaseObject {
    private static final long serialVersionUID = -6206113328316439730L;

    // 值
    private String value;
    // 名称
    private String argValue;
    // 业务组织角色ID，多个以分号隔开
    private String bizRoleId;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the argValue
     */
    public String getArgValue() {
        return argValue;
    }

    /**
     * @param argValue 要设置的argValue
     */
    public void setArgValue(String argValue) {
        this.argValue = argValue;
    }

    /**
     * @return the bizRoleId
     */
    public String getBizRoleId() {
        return bizRoleId;
    }

    /**
     * @param bizRoleId 要设置的bizRoleId
     */
    public void setBizRoleId(String bizRoleId) {
        this.bizRoleId = bizRoleId;
    }
}
