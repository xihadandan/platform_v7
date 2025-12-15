/*
 * @(#)Mar 2, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 2, 2017.1	zhulh		Mar 2, 2017		Create
 * </pre>
 * @date Mar 2, 2017
 */
public enum DataType {
    // 数据类型
    DYFORM("动态表单", "DYFORM"), // 动态表单
    TABLE("数据库表", "TABLE"), // 数据库表
    DATA_STORE("数据源", "DATA_STORE"), // 数据源
    FILE("文件", "FILE"), // 文件实体
    MIXTURE("混合模式", "MIXTURE"); // 混合模式

    private String name;
    private String id;

    private DataType(String name, String id) {
        this.name = name;
        this.id = id;
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

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

}
