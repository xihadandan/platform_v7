/*
 * @(#)Feb 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.enums;

/**
 * Description: 重复策略
 *
 * @author
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月11日.1			2018年10月11日		Create
 * </pre>
 * @date 2018年10月11日
 */
public enum DataImportStrategyEnum {
    BLANK("", "请选择!"), OVERLAP("overlap", "覆盖操作"), ERROR("error", "报错操作");

    // 成员变量
    private String type;
    private String name;

    // 构造方法
    private DataImportStrategyEnum(String type, String name) {
        this.type = type;
        this.name = name;
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
