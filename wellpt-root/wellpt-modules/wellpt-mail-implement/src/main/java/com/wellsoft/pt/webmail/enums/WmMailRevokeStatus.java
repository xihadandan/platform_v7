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
public enum WmMailRevokeStatus {
    REVOKE_FAIL("撤回失败"), REVOKE_SUCCESS("撤回成功"), REVOKE_PARTICAL_SUCCESS("部分撤回成功");

    private String name;

    WmMailRevokeStatus(String name) {
        this.name = name;
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
