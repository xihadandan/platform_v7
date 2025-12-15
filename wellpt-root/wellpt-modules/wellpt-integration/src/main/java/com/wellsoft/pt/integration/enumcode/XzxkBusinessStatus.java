/*
 * @(#)2016年5月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.enumcode;

/**
 * 商事管理-行政许可  业务状态 枚举类
 *
 * @author {wangdj}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月17日.1	{wangdj}		2016年5月17日		Create
 * </pre>
 * @date 2016年5月17日
 */
public enum XzxkBusinessStatus {

    XK(0, "许可"),

    YX(1, "延续"),

    BG(2, "变更"),

    FH(3, "复核"),

    YSBB(4, "遗失补办"),

    ZX(5, "注销");

    private Integer value;//枚举的值
    private String name;

    /**
     * 如何描述该构造方法
     *
     * @param value
     * @param name
     */
    private XzxkBusinessStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    // 获取枚举类
    public static XzxkBusinessStatus getType(Integer value) {
        for (XzxkBusinessStatus state : XzxkBusinessStatus.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return null;
    }

    /**
     * @return the value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Integer value) {
        this.value = value;
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
    }//显示值

}
