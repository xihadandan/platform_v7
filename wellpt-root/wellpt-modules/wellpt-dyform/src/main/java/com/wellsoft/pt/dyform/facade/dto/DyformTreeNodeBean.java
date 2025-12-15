/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.facade.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 表单定义树节点信息对象
 *
 * @author jiangmb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-27.1	jiangmb		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
public class DyformTreeNodeBean implements Serializable {
    private String id;
    private String name;
    private List<DyformTreeNodeBean> children;
    private boolean open;
    private boolean checked;
    private String isParent;

    public DyformTreeNodeBean() {
    }

    public DyformTreeNodeBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DyformTreeNodeBean> getChildren() {
        return children;
    }

    public void setChildren(List<DyformTreeNodeBean> children) {
        this.children = children;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }
}
