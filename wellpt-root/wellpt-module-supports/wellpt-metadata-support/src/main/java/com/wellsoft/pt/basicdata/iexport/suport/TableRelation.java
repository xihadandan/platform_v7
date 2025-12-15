/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.base.BaseObject;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public class TableRelation extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4106333649803040266L;

    // 所在关系组
    private String group;
    // 主表
    private String primaryTableName;
    // 从表
    private String slaveTableName;
    // 关联字段
    private String relationColumnName;

    /**
     * @param primaryTableName
     * @param slaveTableName
     * @param relationColumnName
     */
    public TableRelation(String group, String primaryTableName, String slaveTableName, String relationColumnName) {
        super();
        this.group = group;
        this.primaryTableName = primaryTableName;
        this.slaveTableName = slaveTableName;
        this.relationColumnName = relationColumnName;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group 要设置的group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the primaryTableName
     */
    public String getPrimaryTableName() {
        return primaryTableName;
    }

    /**
     * @param primaryTableName 要设置的primaryTableName
     */
    public void setPrimaryTableName(String primaryTableName) {
        this.primaryTableName = primaryTableName;
    }

    /**
     * @return the slaveTableName
     */
    public String getSlaveTableName() {
        return slaveTableName;
    }

    /**
     * @param slaveTableName 要设置的slaveTableName
     */
    public void setSlaveTableName(String slaveTableName) {
        this.slaveTableName = slaveTableName;
    }

    /**
     * @return the relationColumnName
     */
    public String getRelationColumnName() {
        return StringUtils.lowerCase(relationColumnName);
    }

    /**
     * @param relationColumnName 要设置的relationColumnName
     */
    public void setRelationColumnName(String relationColumnName) {
        this.relationColumnName = relationColumnName;
    }

}
