/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;

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
public class OrgTreeNodeDo extends MultiOrgTreeNode {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2734382644907473611L;
    // ID
    private String id;
    // CODE
    private String code;
    // NAME
    private String name;
    // 简称
    private String shortName;
    // sap_CODE
    private String sapCode;
    // 备注
    private String remark;
    // 类型
    private String type;

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

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
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
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName 要设置的shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
     * @return the sapCode
     */
    public String getSapCode() {
        return sapCode;
    }

    /**
     * @param sapCode 要设置的sapCode
     */
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

}
