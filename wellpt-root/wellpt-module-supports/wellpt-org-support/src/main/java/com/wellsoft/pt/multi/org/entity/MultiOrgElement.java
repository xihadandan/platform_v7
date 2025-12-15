/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeVo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织节点基本类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_ELEMENT")
@DynamicUpdate
@DynamicInsert
public class MultiOrgElement extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 849587229843467231L;
    // ID
    @ApiModelProperty("组织节点的ID")
    private String id;
    // CODE
    @ApiModelProperty("组织节点的code编码")
    private String code;
    // NAME
    @NotBlank
    @ApiModelProperty("组织节点名称")
    private String name;
    // 简称
    @ApiModelProperty("简称")
    private String shortName;
    // SAP_CODE
    @ApiModelProperty("SAP_CODE")
    private String sapCode;
    // 备注
    @ApiModelProperty("备注")
    private String remark;
    // 类型
    @NotBlank
    @ApiModelProperty("类型")
    private String type;
    // 归属的系统单位ID
    @NotBlank
    @ApiModelProperty("归属的系统单位ID")
    private String systemUnitId;

    // 是否合法的组织元素ID
    public static boolean isValidElementId(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (id.startsWith(IdPrefix.ORG.getValue())) {
                return true;
            } else if (id.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                return true;
            } else if (id.startsWith(IdPrefix.DEPARTMENT.getValue()) && !id.startsWith(IdPrefix.DUTY.getValue())) {
                return true;
            } else if (id.startsWith(IdPrefix.JOB.getValue())) {
                return true;
            }
        }
        return false;
    }

    // 是否合法的组织ID,包含群组，职务，用户
    public static boolean isValidOrgId(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (id.startsWith(IdPrefix.USER.getValue())) {
                return true;
            } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                return true;
            } else if (id.startsWith(IdPrefix.DUTY.getValue())) {
                return true;
            } else if (isValidElementId(id)) {
                return true;
            }
        }
        return false;
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

    public void setAttrFromOrgTreeNodeVo(OrgTreeNodeVo vo, boolean isModify) {
        if (!isModify) { // ID一旦设置，不能修改
            this.id = vo.getEleId();
        }
        this.code = vo.getCode();
        this.name = vo.getName();
        this.sapCode = vo.getSapCode();
        this.shortName = vo.getShortName();
        this.remark = vo.getRemark();
        this.type = vo.getType();
        this.systemUnitId = vo.getSystemUnitId();
    }

    public TreeNode convertToTreeNode() {
        TreeNode node = new TreeNode();
        node.setId(this.id);
        node.setName(this.name);
        node.setType(this.type);
        return node;
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

}
