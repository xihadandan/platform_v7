/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月24日.1	zyguo		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("组织树节点参数")
public class OrgTreeNodeParams implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3642360532751377906L;

    @ApiModelProperty("系统单位Id")
    private String systemUnitId;
    @ApiModelProperty("功能类型")
    private String functionType;
    @ApiModelProperty("自动升级(默认值：1)")
    private int autoUpgrade = 1;
    @ApiModelProperty("根版本ID")
    private String rootVersionId;

    /**
     * @return the functionType
     */
    public String getFunctionType() {
        return functionType;
    }

    /**
     * @param functionType 要设置的functionType
     */
    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    /**
     * @return the autoUpgrade
     */
    public int getAutoUpgrade() {
        return autoUpgrade;
    }

    /**
     * @param autoUpgrade 要设置的autoUpgrade
     */
    public void setAutoUpgrade(int autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * @return the rootVersionId
     */
    public String getRootVersionId() {
        return rootVersionId;
    }

    /**
     * @param rootVersionId 要设置的rootVersionId
     */
    public void setRootVersionId(String rootVersionId) {
        this.rootVersionId = rootVersionId;
    }
}
