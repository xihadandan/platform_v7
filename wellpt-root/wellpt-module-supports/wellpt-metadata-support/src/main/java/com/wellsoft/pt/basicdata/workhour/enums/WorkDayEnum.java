/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.enums;

/**
 * Description: 如何描述该类
 *
 * @author linxr
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	linxr		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public enum WorkDayEnum {
    MONDAY("MON", 1, "星期一"), TUESDAY("TUE", 2, "星期二"), WEDESDAY("WED", 3, "星期三"), THURSDAY("THU", 4, "星期四"), FRIDAY(
            "FRI", 5, "星期五"), SATURDAY("SAT", 6, "星期六"), SUNDAY("SUN", 7, "星期日");

    private String value;
    private int order;
    private String remark;

    private WorkDayEnum(String value, int order, String remark) {
        this.value = value;
        this.order = order;
        this.remark = remark;
    }

    public static String value2remark(String value) {
        if (value == null) {
            return null;
        }
        String remark = null;
        for (WorkDayEnum day : WorkDayEnum.values()) {
            if (day.value.equalsIgnoreCase(value)) {
                remark = day.remark;
            }
        }
        return remark;
    }

    public static int value2Order(String value) {
        if (value == null) {
            return 0;
        }
        int order = 0;
        for (WorkDayEnum day : WorkDayEnum.values()) {
            if (day.value.equalsIgnoreCase(value)) {
                order = day.order;
            }
        }
        return order;
    }

    public String getValue() {
        return value;
    }

    public int getOrder() {
        return order;
    }

    public String getRemark() {
        return remark;
    }

}
