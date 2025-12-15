/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;

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
@ApiModel(value = "组织树弹出框参数对象")
public class OrgTreeDialogParams implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2846327381609514193L;
    @ApiModelProperty("// 是否需要用户 0：只搜非用户（部门，职位...）1:用户+部门")
    private int isNeedUser = 0;
    @ApiModelProperty("// 是否只在查找本单位内的数据")
    private boolean isInMyUnit = true;
    @ApiModelProperty("// 指定版本")
    private String orgVersionId;
    @ApiModelProperty("// 该参数对我的单位，我的集团才生效")
    private String unitId;
    @ApiModelProperty("// 该参数对我的下属，我的领导，我的部门，才生效")
    private String userId;

    @ApiModelProperty(" // 额外的参数配置，供给二开使用")
    private HashMap<String, String> otherParams = new HashMap<String, String>();

    /**
     * @return the isNeedUser
     */
    public int getIsNeedUser() {
        return isNeedUser;
    }

    /**
     * @param isNeedUser 要设置的isNeedUser
     */
    public void setIsNeedUser(int isNeedUser) {
        this.isNeedUser = isNeedUser;
    }

    /**
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    /**
     * @return the unitId
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * @param unitId 要设置的unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the isInMyUnit
     */
    public boolean getIsInMyUnit() {
        return isInMyUnit;
    }

    /**
     * @param isInMyUnit 要设置的isInMyUnit
     */
    public void setInMyUnit(boolean isInMyUnit) {
        this.isInMyUnit = isInMyUnit;
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
     * @return the otherParams
     */
    public HashMap<String, String> getOtherParams() {
        return otherParams;
    }

    /**
     * @param otherParams 要设置的otherParams
     */
    public void setOtherParams(HashMap<String, String> otherParams) {
        this.otherParams = otherParams;
    }

}
