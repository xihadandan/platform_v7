/*
 * @(#)2018年2月28日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.enums;

/**
 * Description: 邮件状态位
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月28日.1	chenqiong		2018年2月28日		Create
 * </pre>
 * @date 2018年2月28日
 */
public enum WmMailBoxName {
    IN_BOX("INBOX", "收件箱"), OUT_BOX("OUTBOX", "发件箱");

    private String code;
    private String name;

    WmMailBoxName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

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

}
