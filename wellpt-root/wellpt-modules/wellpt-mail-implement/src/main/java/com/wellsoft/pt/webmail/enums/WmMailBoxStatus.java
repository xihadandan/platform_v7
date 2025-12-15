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
public enum WmMailBoxStatus {
    DRAFT(0, "草稿"), SEND_SUCCESS(1, "发送成功"), FETCH_SUCCESS(2, "接收成功"), LOGICAL_DELETE(-1, "删除"), PHYSICS_DELETE(-2,
            "彻底删除");

    private int code;
    private String name;

    WmMailBoxStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WmMailBoxStatus getStatusByCode(Integer code) {
        if (code == null) {
            return null;
        }
        WmMailBoxStatus[] statusArray = WmMailBoxStatus.values();
        for (WmMailBoxStatus s : statusArray) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(int code) {
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
