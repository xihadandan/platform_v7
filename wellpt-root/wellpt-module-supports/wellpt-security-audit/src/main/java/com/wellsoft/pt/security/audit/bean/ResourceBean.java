/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.bean;

import com.wellsoft.pt.security.audit.entity.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@ApiModel("资源Bean")
public class ResourceBean extends Resource {

    private static final long serialVersionUID = -1915269197999878671L;

    @ApiModelProperty("jqGrid的行标识")
    private String id;

    @ApiModelProperty("父节点名称")
    private String parentName;
    @ApiModelProperty("父节点UUID")
    private String parentUuid;

    @ApiModelProperty("按钮子结点")
    private Set<ResourceBean> buttons = new HashSet<ResourceBean>();
    private Set<ResourceBean> changedButtons = new HashSet<ResourceBean>();
    private Set<ResourceBean> deletedButtons = new HashSet<ResourceBean>();
    @ApiModelProperty("方法子结点")
    private Set<ResourceBean> methods = new HashSet<ResourceBean>();
    private Set<ResourceBean> changedMethods = new HashSet<ResourceBean>();
    private Set<ResourceBean> deletedMethods = new HashSet<ResourceBean>();

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
     * @return the parentName
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * @param parentName 要设置的parentName
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return the parentUuid
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the buttons
     */
    public Set<ResourceBean> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(Set<ResourceBean> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the changedButtons
     */
    public Set<ResourceBean> getChangedButtons() {
        return changedButtons;
    }

    /**
     * @param changedButtons 要设置的changedButtons
     */
    public void setChangedButtons(Set<ResourceBean> changedButtons) {
        this.changedButtons = changedButtons;
    }

    /**
     * @return the deletedButtons
     */
    public Set<ResourceBean> getDeletedButtons() {
        return deletedButtons;
    }

    /**
     * @param deletedButtons 要设置的deletedButtons
     */
    public void setDeletedButtons(Set<ResourceBean> deletedButtons) {
        this.deletedButtons = deletedButtons;
    }

    /**
     * @return the methods
     */
    public Set<ResourceBean> getMethods() {
        return methods;
    }

    /**
     * @param methods 要设置的methods
     */
    public void setMethods(Set<ResourceBean> methods) {
        this.methods = methods;
    }

    /**
     * @return the changedMethods
     */
    public Set<ResourceBean> getChangedMethods() {
        return changedMethods;
    }

    /**
     * @param changedMethods 要设置的changedMethods
     */
    public void setChangedMethods(Set<ResourceBean> changedMethods) {
        this.changedMethods = changedMethods;
    }

    /**
     * @return the deletedMethods
     */
    public Set<ResourceBean> getDeletedMethods() {
        return deletedMethods;
    }

    /**
     * @param deletedMethods 要设置的deletedMethods
     */
    public void setDeletedMethods(Set<ResourceBean> deletedMethods) {
        this.deletedMethods = deletedMethods;
    }

}
