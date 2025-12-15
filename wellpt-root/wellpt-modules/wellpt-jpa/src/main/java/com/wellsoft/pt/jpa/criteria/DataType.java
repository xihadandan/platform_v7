/*
 * @(#)2016年10月28日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月28日.1	xiem		2016年10月28日		Create
 * </pre>
 * @date 2016年10月28日
 */
public enum DataType {
    S("String", "字符串", String.class), I("Integer", "整型", Integer.class), D("Double", "双精度", Double.class), F("Float",
            "浮点型", Float.class), L("Long", "长整型", Long.class), T("Date", "时间", Date.class), B("Boolean", "布尔型",
            Boolean.class);
    private String type;
    private String name;
    private Class<?> clz;

    private DataType(String type, String name, Class<?> clz) {
        this.setType(type);
        this.setName(name);
        this.setClz(clz);
    }

    public static DataType getByType(String type) {
        for (DataType dataType : DataType.values()) {
            if (dataType.getType().equals(type)) {
                return dataType;
            }
        }
        return null;
    }

    public static DataType getCovert4JavaType(Class<?> type) {
        if (Integer.class.equals(type) || "int".equals(type.getName())) {
            return DataType.I;
        }
        if (Long.class.equals(type) || "long".equals(type.getName())) {
            return DataType.L;
        }
        if (Float.class.equals(type) || "float".equals(type.getName())) {
            return DataType.F;
        }
        if (Double.class.equals(type) || "double".equals(type.getName())) {
            return DataType.D;
        }
        if (Boolean.class.equals(type) || "bool".equals(type.getName())) {
            return DataType.B;
        }
        if (Date.class.equals(type) || type.isAssignableFrom(Date.class)) {
            return DataType.T;
        }
        return DataType.S;
    }

    public static DataType getCovert4DB(String type) {
        type = type.toUpperCase();
        if (type.startsWith("NUMBER") || type.startsWith("INT")) {
            return DataType.D;
        }
        if (type.startsWith("Date") || type.startsWith("TIMESTAMP") || type.startsWith("DATE")) {
            return DataType.T;
        }
        return DataType.S;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the clz
     */
    public Class<?> getClz() {
        return clz;
    }

    /**
     * @param clz 要设置的clz
     */
    public void setClz(Class<?> clz) {
        this.clz = clz;
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
