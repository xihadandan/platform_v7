/*
 * @(#)2012-12-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.collection.List2Map;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 树结点
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-20.1	zhulh		2012-12-20		Create
 * </pre>
 * @date 2012-12-20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "树结点")
public class TreeNode implements Serializable {
    public static final String ROOT_ID = "-1";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2004348696810034711L;
    @ApiModelProperty("子结点列表")
    protected List<TreeNode> children = Collections.synchronizedList(Lists.newArrayList());
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("结点名称")
    private String name;
    private String subTitle;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("路径")
    private String path;
    @ApiModelProperty("url")
    private String url;
    @ApiModelProperty("是否公开")
    private boolean open;
    @ApiModelProperty("是否选中")
    private boolean checked;
    private Boolean hidden;
    @ApiModelProperty("nocheck")
    private boolean nocheck;
    @ApiModelProperty("是否父结点")
    private boolean isParent;
    @ApiModelProperty("图标")
    private String iconSkin;
    @ApiModelProperty("支持对图标的样式变更")
    private String iconStyle;
    @ApiModelProperty("级别")
    private int nodeLevel = 0;
    @ApiModelProperty("总节点数")
    private int total = 0;
    @ApiModelProperty("data")
    private Object data;
    private Long version;


    public TreeNode() {
    }

    public TreeNode(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @param open 要设置的open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked 要设置的checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the nocheck
     */
    public boolean isNocheck() {
        return nocheck;
    }

    /**
     * @param nocheck 要设置的nocheck
     */
    public void setNocheck(boolean nocheck) {
        this.nocheck = nocheck;
    }

    /**
     * @return the isParent
     */
    public boolean getIsParent() {
        return isParent;
    }

    /**
     * @param isParent 要设置的isParent
     */
    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    /**
     * @return the iconSkin
     */
    public String getIconSkin() {
        return iconSkin;
    }

    /**
     * @param iconSkin 要设置的iconSkin
     */
    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    /**
     * @return the children
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 按类型，获取所有的子节点， 不去重
     *
     * @param type
     * @param isRecursion, 是否递归查找， true : 是， false ：否
     * @return
     */
    List<TreeNode> queryChildNodeListByType(String type, boolean isRecursion) {
        List<TreeNode> list = new ArrayList<TreeNode>();
        if (!CollectionUtils.isEmpty(this.children)) {
            for (TreeNode treeNode : children) {
                if (type.equals(treeNode.getType())) {
                    list.add(treeNode);
                }
                if (isRecursion) {
                    List<TreeNode> childList = treeNode.queryChildNodeListByType(type, isRecursion);
                    if (!CollectionUtils.isEmpty(childList)) {
                        list.addAll(childList);
                    }
                }
            }
        }
        return list;

    }

    public TreeNode appendChild(TreeNode child) {
        if (child != null) {
            this.getChildren().add(child);
        }
        return this;
    }

    /**
     * 按类型，获取所有的子节点， 去重
     *
     * @param type
     * @param isRecursion, 是否递归查找， true : 是， false ：否
     * @return
     */
    public List<TreeNode> queryChildNodeListByTypeAndRemoveRepeat(String type, boolean isRecursion) {
        List<TreeNode> list = this.queryChildNodeListByType(type, isRecursion);
        if (!CollectionUtils.isEmpty(list)) {
            Map<String, TreeNode> map = new List2Map<TreeNode>() {
                @Override
                protected String getMapKey(TreeNode obj) {
                    return obj.getId();
                }
            }.convert(list);
            return new ArrayList<TreeNode>(map.values());
        }
        return null;
    }

    /**
     * 将整颗树递归转成list
     *
     * @return
     */
    public List<TreeNode> toList() {
        List<TreeNode> list = Lists.newArrayList(this);
        if (CollectionUtils.isNotEmpty(this.children)) {
            for (TreeNode child : this.children) {
                List<TreeNode> childList = child.toList();
                if (CollectionUtils.isNotEmpty(childList)) {
                    list.addAll(childList);
                }
            }
        }
        return list;
    }

    public String getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(String iconStyle) {
        this.iconStyle = iconStyle;
    }

    public int getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(int nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    // 请确保所有节点的ID是唯一的，否则会被覆盖掉
    public Map<String, TreeNode> toMapById() {
        Map<String, TreeNode> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(this.id)) {
            map.put(this.id, this);
        }
        if (!CollectionUtils.isEmpty(this.children)) {
            for (TreeNode subNode : children) {
                Map<String, TreeNode> subMap = subNode.toMapById();
                if (subMap != null && !subMap.isEmpty()) {
                    map.putAll(subMap);
                }
            }
        }
        return map;
    }

    // 请确保所有节点的PATH是唯一的，否则会被覆盖掉
    public Map<String, TreeNode> toMapByPath() {
        Map<String, TreeNode> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(this.path)) {
            map.put(this.path, this);
        }
        if (!CollectionUtils.isEmpty(this.children)) {
            for (TreeNode subNode : children) {
                Map<String, TreeNode> subMap = subNode.toMapByPath();
                if (subMap != null && !subMap.isEmpty()) {
                    map.putAll(subMap);
                }
            }
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TreeNode other = (TreeNode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public static class TreeContextHolder {

        private static ThreadLocal<Set<String>> nodeIds = new ThreadLocal<>();

        public static boolean addId(String id) {
            if (nodeIds.get() == null) {
                nodeIds.set(Sets.newLinkedHashSet());
            }
            return nodeIds.get().add(id);
        }

        public static void remove() {
            nodeIds.remove();
        }
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
