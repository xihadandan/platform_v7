/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.multi.org.bean.OrgGroupVo;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "MULTI_ORG_GROUP")
@DynamicUpdate
@DynamicInsert
public class MultiOrgGroup extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 718992999306360696L;
    public static int TYPE_PUBLIC_GROUP = 0; // 公共群组
    public static int TYPE_MY_GROUP = 1; // 个人群组
    // ID
    @ApiModelProperty("ID")
    private String id;
    // 编码
    @ApiModelProperty("编码")
    private String code;
    // 名字
    @ApiModelProperty("名字")
    private String name;
    // 备注
    @ApiModelProperty("备注")
    @MaxLength(max = 255)
    private String remark;
    // 类型 1：个人群组，0：公共群组
    @ApiModelProperty("类型 1：个人群组，0：公共群组")
    private Integer type;
    // 归属系统ID
    @ApiModelProperty("归属系统ID")
    private String systemUnitId;
    // 添加使用范围人员显示值字段
    @ApiModelProperty("添加使用范围人员显示值字段")
    private String userRangeDisplay;
    // 添加使用范围人员真实值字段
    @ApiModelProperty("添加使用范围人员真实值字段")
    private String userRangeReal;

    public String getUserRangeReal() {
        return userRangeReal;
    }

    public void setUserRangeReal(String userRangeReal) {
        this.userRangeReal = userRangeReal;
    }

    public String getUserRangeDisplay() {

        return userRangeDisplay;
    }

    public void setUserRangeDisplay(String userRangeDisplay) {
        this.userRangeDisplay = userRangeDisplay;
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
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 如何描述该方法
     *
     * @param vo
     */
    public void setAttrFromOrgGroupVo(OrgGroupVo vo) {
        this.name = vo.getName();
        this.code = vo.getCode();
        this.type = vo.getType();
        this.remark = vo.getRemark();
    }

    public TreeNode convert2TreeNode() {
        TreeNode treeNode = new TreeNode();
        treeNode.setId(this.getId());
        treeNode.setName(this.getName());
        treeNode.setType(IdPrefix.GROUP.getValue());
        treeNode.setIconSkin(IdPrefix.GROUP.getValue());
        return treeNode;
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
