/*
 * @(#)Feb 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 8, 2018.1	zhulh		Feb 8, 2018		Create
 * </pre>
 * @date Feb 8, 2018
 */
public enum DataImportRuleTypeEnum {
    OTHER("other", ""), REGULAR("regular", "自定义正则表达式"), GROOVY("groovy", "自定义groovy脚本");

    // 成员变量
    private String type;
    private String name;

    // 构造方法
    private DataImportRuleTypeEnum(String type, String name) {
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
