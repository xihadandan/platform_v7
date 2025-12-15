/*
 * @(#)28 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

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
 * 28 Feb 2017.1	Xiem		28 Feb 2017		Create
 * </pre>
 * @date 28 Feb 2017
 */
public class TreeNodeConfig implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3295049790604520906L;
    private String type;
    private String name;
    private Integer disableChecked; // 是否禁止选择 1：禁止选择，0：可以选择
    private Integer isShow; // 是否展示 1:展示， 0 ：不展示
    private TreeNodeIcon icon; // 节点对应的图标信息

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the disableChecked
     */
    public Integer getDisableChecked() {
        return disableChecked;
    }

    /**
     * @param disableChecked 要设置的disableChecked
     */
    public void setDisableChecked(Integer disableChecked) {
        this.disableChecked = disableChecked;
    }

    /**
     * @return the isShow
     */
    public Integer getIsShow() {
        return isShow;
    }

    /**
     * @param isShow 要设置的isShow
     */
    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    /**
     * @return the icon
     */
    public TreeNodeIcon getIcon() {
        return icon;
    }

    /**
     * @param icon 要设置的icon
     */
    public void setIcon(TreeNodeIcon icon) {
        this.icon = icon;
    }

}
