/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Map;

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
@ApiModel("组织树节点")
public class OrgTreeNodeDto extends MultiOrgTreeNode implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2734382644907473611L;
    @ApiModelProperty("eleUuid")
    private String eleUuid;
    // CODE
    @ApiModelProperty("CODE")
    private String code;
    // NAME
    @ApiModelProperty("NAME")
    private String name;
    private String localName;
    // 简称
    @ApiModelProperty("简称")
    private String shortName;
    private String localShortName;
    // 用户拼音
    @ApiModelProperty("用户拼音")
    private String userNamePy;
    // SAP_CODE
    @ApiModelProperty("SAP_CODE")
    private String sapCode;
    // 备注
    @ApiModelProperty("备注")
    private String remark;
    // 类型
    @ApiModelProperty("类型")
    private String type;
    // 完整的路径
    @ApiModelProperty("完整的路径 // 根节点是组织开始")
    private String eleNamePath;

    private String localEleNamePath;

    // 单位路径
    @ApiModelProperty("单位路径 // 从归属单位开始算路径")
    private String unitNamePath;
    // 部门路径
    @ApiModelProperty("部门路径 // 从部门开始算路径")
    private String deptNamePath;


    @ApiModelProperty("部门路径 // 从部门开始算路径")
    private String localDeptNamePath;

    // 归属的系统单位Id
    @ApiModelProperty("归属的系统单位Id")
    private String systemUnitId;
    @ApiModelProperty("functionType")
    private String functionType;

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

    public String getUserNamePy() {
        return userNamePy;
    }

    public void setUserNamePy(String userNamePy) {
        this.userNamePy = userNamePy;
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

    public OrgTreeNode convert2TreeNode() {
        OrgTreeNode treeNode = new OrgTreeNode();
        treeNode.setId(this.getEleId());
        treeNode.setName(this.getName());
        treeNode.setType(this.getType());
        treeNode.setOrgVersionId(this.getOrgVersionId());
        treeNode.setData(this);
        // 1、初始化节点数据时，根据 treeNode.children 属性判断，有子节点则设置为 true，否则为 false
        // 2、初始化节点数据时，如果设定 treeNode.isParent = true，即使无子节点数据，也会设置为父节点
        treeNode.setIsParent(false);
        treeNode.setPath(this.getEleIdPath());
        treeNode.setSystemUnitId(this.getSystemUnitId());
        if ("man".equals(remark) || "women".equals(remark)) {
            treeNode.setIconSkin(remark);
        } else {
            treeNode.setIconSkin(this.getType());
        }
        treeNode.setNamePath(this.getEleNamePath());
        return treeNode;
    }

    public OrgNode convert2OrgNode() {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(this.getEleId());
        orgNode.setType(this.getType());
        orgNode.setName(this.getName());
        if ("man".equals(remark) || "women".equals(remark)) {
            orgNode.setIconSkin(remark);
        } else {
            orgNode.setIconSkin(this.getType());
        }
        return orgNode;
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
     * @return the eleUuid
     */
    public String getEleUuid() {
        return eleUuid;
    }

    /**
     * @param eleUuid 要设置的eleUuid
     */
    public void setEleUuid(String eleUuid) {
        this.eleUuid = eleUuid;
    }

    /**
     * @return the eleNamePath
     */
    public String getEleNamePath() {
        return eleNamePath;
    }

    /**
     * @param eleNamePath 要设置的eleNamePath
     */
    public void setEleNamePath(String eleNamePath) {
        this.eleNamePath = eleNamePath;
    }

    /**
     * 获取元素名称
     */
    @Transient
    public String getEleName() {
        return this.getName();
        // if (this.eleNamePath == null) {
        // return "";
        // }
        // int pos =
        // this.eleNamePath.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1;
        // return this.eleNamePath.substring(pos);
    }

    /**
     * 计算自己的父节点IdPath，如果是根节点则是自己，如果是子节点，则算出父节点PATH
     */
    @Transient
    public String getParentNamePath() {
        String path = this.getEleNamePath();
        if (path == null) {
            return "";
        }
        int pos = path.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String parentPath = pos > 0 ? path.substring(0, pos) : "";
        return parentPath;
    }

    @Transient
    public String getParentLocalNamePath() {
        String path = this.getLocalEleNamePath();
        if (path == null) {
            return "";
        }
        int pos = path.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String parentPath = pos > 0 ? path.substring(0, pos) : "";
        return parentPath;
    }

    /**
     * 如何描述该方法
     *
     * @param allEleMap
     * @param unitEle
     * @return
     */
    public void computeEleNamePath(Map<String, MultiOrgElement> allEleMap) {
        String eleIdPath = this.getEleIdPath();
        if (CollectionUtils.isEmpty(allEleMap) || eleIdPath == null) {
        } else {
            String[] ids = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            ArrayList<String> paths = new ArrayList<String>();
            ArrayList<String> unitPaths = new ArrayList<String>();
            ArrayList<String> deptPaths = new ArrayList<String>();
            // 根节点是从组织版本开始的，所有 i 要从 1开始取
            for (int i = 0; i < ids.length; i++) {
                MultiOrgElement ele = allEleMap.get(ids[i]);
                if (null == ele) {
                    throw new BusinessException("未找到组织元素:" + eleIdPath);
                }
                paths.add(ele.getName());
                if (i > 0) {
                    unitPaths.add(ele.getName());
                }
                if (i > 1) { // 部门路径需要扣除单位和组织类型
                    deptPaths.add(ele.getName());
                }
            }
            this.eleNamePath = StringUtils.join(paths, MultiOrgService.PATH_SPLIT_SYSMBOL);
            this.unitNamePath = StringUtils.join(unitPaths, MultiOrgService.PATH_SPLIT_SYSMBOL);
            this.deptNamePath = StringUtils.join(deptPaths, MultiOrgService.PATH_SPLIT_SYSMBOL);
        }
    }

    /**
     * 获取部门路径
     */
    @Transient
    public String getDeptNamePath() {
        return this.deptNamePath;
    }

    /**
     * @param deptPath 要设置的deptPath
     */
    public void setDeptNamePath(String deptPath) {
        this.deptNamePath = deptPath;
    }

    @Transient
    public String getDeptName() {
        if (StringUtils.isNotBlank(this.deptNamePath)) {
            String[] paths = this.deptNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            return paths[paths.length - 1];
        }
        return "";
    }

    @Transient
    public String getDeptNameI18n() {
        if (StringUtils.isNotBlank(this.deptNamePath)) {
            String[] paths = this.deptNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            return paths[paths.length - 1];
        }
        return "";
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
     * @return the unitPath
     */
    public String getUnitNamePath() {
        return unitNamePath;
    }

    /**
     * @param unitPath 要设置的unitPath
     */
    public void setUnitNamePath(String unitPath) {
        this.unitNamePath = unitPath;
    }

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

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalShortName() {
        return localShortName;
    }

    public void setLocalShortName(String localShortName) {
        this.localShortName = localShortName;
    }

    public String getLocalEleNamePath() {
        return localEleNamePath;
    }

    public void setLocalEleNamePath(String localEleNamePath) {
        this.localEleNamePath = localEleNamePath;
    }

    public String getLocalDeptNamePath() {
        return localDeptNamePath;
    }

    public void setLocalDeptNamePath(String localDeptNamePath) {
        this.localDeptNamePath = localDeptNamePath;
    }
}
