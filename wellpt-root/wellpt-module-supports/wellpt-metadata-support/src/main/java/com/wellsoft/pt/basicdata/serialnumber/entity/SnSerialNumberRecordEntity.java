/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流水号记录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/22/22.1	zhulh		7/22/22		Create
 * </pre>
 * @date 7/22/22
 */
@ApiModel("流水号记录")
@Entity
@Table(name = "sn_serial_number_record")
@DynamicUpdate
@DynamicInsert
public class SnSerialNumberRecordEntity extends TenantEntity {
    private static final long serialVersionUID = 8187147359721043063L;

    @ApiModelProperty("流水号关联表字段记录UUID")
    private String relationUuid;

    @ApiModelProperty("流水号关联表字段记录UUID")
    private String maintainUuid;

    @ApiModelProperty("前缀")
    private String prefix;

    @ApiModelProperty("指针")
    private Long pointer;

    @ApiModelProperty("后缀")
    private String suffix;

    @ApiModelProperty("流水号")
    private String serialNo;

    /**
     * @return the relationUuid
     */
    public String getRelationUuid() {
        return relationUuid;
    }

    /**
     * @param relationUuid 要设置的relationUuid
     */
    public void setRelationUuid(String relationUuid) {
        this.relationUuid = relationUuid;
    }

    /**
     * @return the maintainUuid
     */
    public String getMaintainUuid() {
        return maintainUuid;
    }

    /**
     * @param maintainUuid 要设置的maintainUuid
     */
    public void setMaintainUuid(String maintainUuid) {
        this.maintainUuid = maintainUuid;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix 要设置的prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the pointer
     */
    public Long getPointer() {
        return pointer;
    }

    /**
     * @param pointer 要设置的pointer
     */
    public void setPointer(Long pointer) {
        this.pointer = pointer;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix 要设置的suffix
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo 要设置的serialNo
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
