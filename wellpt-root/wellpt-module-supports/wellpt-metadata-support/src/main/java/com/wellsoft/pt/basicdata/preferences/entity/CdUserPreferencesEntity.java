/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.preferences.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@ApiModel("用户偏好")
@Entity
@Table(name = "cd_user_preferences")
@DynamicUpdate
@DynamicInsert
public class CdUserPreferencesEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8257053637259915475L;

    // 模块ID
    @ApiModelProperty("模块ID")
    private String moduleId;

    // 功能ID
    @ApiModelProperty("功能ID")
    private String functionId;

    // 用户ID
    @ApiModelProperty("用户ID")
    private String userId;

    // 数据键
    @ApiModelProperty("数据键")
    private String dataKey;

    // 数据值
    @ApiModelProperty("数据值")
    private String dataValue;

    // 备注
    @ApiModelProperty("备注")
    private String remark;

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the functionId
     */
    public String getFunctionId() {
        return functionId;
    }

    /**
     * @param functionId 要设置的functionId
     */
    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the dataKey
     */
    public String getDataKey() {
        return dataKey;
    }

    /**
     * @param dataKey 要设置的dataKey
     */
    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    /**
     * @return the dataValue
     */
    public String getDataValue() {
        return dataValue;
    }

    /**
     * @param dataValue 要设置的dataValue
     */
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
