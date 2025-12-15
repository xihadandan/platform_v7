/*
 * @(#)2016年7月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.entity;

/**
 * Description: 可配置性的ID实体抽象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月30日.1	zhulh		2016年7月30日		Create
 * </pre>
 * @date 2016年7月30日
 */
public interface ConfigurableIdEntity {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String REMARK = "remark";

    public String getName();

    public void setName(String name);

    public String getId();

    public void setId(String id);

    public String getCode();

    public void setCode(String code);

    public String getRemark();

    public void setRemark(String remark);

    public String getUuid();
}
