/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 如何描述该类
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
@Table(name = "MULTI_ORG_TREE_NODE")
@DynamicUpdate
@DynamicInsert
@ApiModel(value = "多组织节点")
public class MultiOrgTreeNode extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1511233285634L;

    // 对应的节点元素的ID
    @ApiModelProperty("对应的节点元素的ID")
    private String eleId;
    // 完整的节点ID全路径
    @ApiModelProperty("完整的节点ID全路径")
    private String eleIdPath;
    // 组织版本ID
    @ApiModelProperty("组织版本ID")
    private String orgVersionId;
    // 单位节点对应的参数
    @ApiModelProperty("单位节点对应的参数")
    private String jsonParams;

    // 按类型获取离的的最近的节点元素ID
    public static String getNearestEleIdByType(String eleIdPath, String type) {
        if (StringUtils.isNotBlank(eleIdPath)) {
            String[] ids = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            for (int i = ids.length - 1; i >= 0; i--) {
                if (ids[i].startsWith(type)) {
                    return ids[i];
                }
            }
        }
        return "";
    }

    public static String getNearestElePathByType(String eleIdPath, String type) {
        if (StringUtils.isNotBlank(eleIdPath)) {
            String[] ids = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            int i = 0;
            for (i = ids.length - 1; i >= 0; i--) {
                if (ids[i].startsWith(type)) {
                    break;
                }
            }
            return StringUtils.join(ids, MultiOrgService.PATH_SPLIT_SYSMBOL, 0, i + 1);
        }
        return "";
    }

    public static void main(String[] args) {
        String ss = "123456";
        int pos = ss.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1;
        System.out.println(ss.substring(pos));

    }

    @Transient
    @ApiModelProperty("上级节点path")
    public String getParentIdPath() {
        String path = this.getEleIdPath();
        int pos = path.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL);
        String parentPath = pos > 0 ? path.substring(0, pos) : TreeNode.ROOT_ID;
        return parentPath;
    }

    // 获取离的最近的部门ID
    @Transient
    @ApiModelProperty("获取离的最近的部门ID")
    public String getDeptId() {
        return getNearestEleIdByType(this.eleIdPath, IdPrefix.DEPARTMENT.getValue());
    }

    // 获取离的最近的部门ID路径
    @Transient
    @ApiModelProperty("获取离的最近的部门ID路径")
    public String getDeptIdPath() {
        return getNearestElePathByType(this.eleIdPath, IdPrefix.DEPARTMENT.getValue());
    }

    // 获取上级节点ID
    @Transient
    @ApiModelProperty("获取上级节点ID")
    public String getParentId() {
        String parentIdPath = this.getParentIdPath();
        if (StringUtils.isNotBlank(parentIdPath)) {
            String[] ids = parentIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            return ids[ids.length - 1];
        }
        return "";
    }

    // 获取根节点ID
    @Transient
    @ApiModelProperty("获取根节点ID")
    public String getBusinessUnitId() {
        if (StringUtils.isNotBlank(this.eleIdPath)) {
            return getNearestEleIdByType(this.eleIdPath, IdPrefix.BUSINESS_UNIT.getValue());
        }
        return "";
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
     * @return the eleIdPath
     */
    public String getEleIdPath() {
        return eleIdPath;
    }

    /**
     * @param eleIdPath 要设置的eleIdPath
     */
    public void setEleIdPath(String eleIdPath) {
        this.eleIdPath = eleIdPath;
    }

    /**
     * @return the eleId
     */
    public String getEleId() {
        return eleId;
    }

    /**
     * @param eleId 要设置的eleId
     */
    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    /**
     * @return the jsonParams
     */
    public String getJsonParams() {
        return jsonParams;
    }

    /**
     * @param jsonParams 要设置的jsonParams
     */
    public void setJsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
    }
}
