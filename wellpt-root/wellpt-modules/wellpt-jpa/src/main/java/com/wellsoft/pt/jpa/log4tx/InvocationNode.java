/*
 * @(#)2019年11月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 调用节点
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月12日.1	zhongzh		2019年11月12日		Create
 * </pre>
 * @date 2019年11月12日
 */
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvocationNode implements Serializable {
    public static final String ROOT_ID = "-1";
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String type;
    private String path;
    private boolean open;
    private String iconSkin;// 图标
    private String iconStyle;// 支持对图标的样式变更
    private Object data;
    //
    @JsonIgnore
    private InvocationNode parent;
    private List<InvocationNode> children;

    public InvocationNode() {
    }

    public InvocationNode(InvocationNode parent) {
        this.parent = parent;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    public String getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(String iconStyle) {
        this.iconStyle = iconStyle;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public InvocationNode getParent() {
        return parent;
    }

    public void setParent(InvocationNode parent) {
        this.parent = parent;
    }

    public List<InvocationNode> getChildren() {
        return children;
    }

    public void setChildren(List<InvocationNode> children) {
        this.children = children;
    }

    // Operation Method
    public InvocationNode addChildren() {
        InvocationNode node = new InvocationNode(this);
        if (null == children) {
            children = new ArrayList<InvocationNode>();
        }
        children.add(node);
        return node;
    }

    public InvocationNode firstChild() {
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public InvocationNode lastChild() {
        int length;
        if (children == null || (length = children.size()) == 0) {
            return null;
        }
        return children.get(length - 1);
    }

    public InvocationNode findO(String id) {
        if (children != null && false == children.isEmpty()) {
            for (InvocationNode node : children) {
                if (StringUtils.equals(node.getId(), id)) {
                    return node;
                }
            }
        }
        return null;
    }

    public void print(StringBuilder writer, String prefix) {
        writer.append(prefix);
        writer.append(id);
        if (null != name) {
            writer.append(' ');
            writer.append(name);
        }
        if (null != type) {
            writer.append(' ');
            writer.append(type);
        }
        Object data = getData();
        if (false == data instanceof Throwable) {
            writer.append(' ');
            writer.append(data.toString());
        }
        if (children == null || children.isEmpty()) {
            return;
        }
        for (InvocationNode node : children) {
            writer.append(TxLogUtils.nl);
            node.print(writer, prefix + "\t");
        }
        if (data instanceof Throwable) {
            writer.append(TxLogUtils.nl);// 最后一次性输出隐藏
            writer.append(ExceptionUtils.getStackTrace((Throwable) data));
        }
    }

    @Override
    public String toString() {
        StringBuilder helper = new StringBuilder("Call Tree:\n");
        print(helper, "");
        return helper.toString();
    }

}
