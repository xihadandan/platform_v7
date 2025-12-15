package com.wellsoft.context.component.tree;

import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author yt
 * @title: OrgNode
 * @date 2020/6/18 15:11
 */
// 序列化 自动忽略 字段值（null，""）减少返回到前端的json数据量
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@ApiModel(value = "组织结点")
public class OrgNode implements Serializable {

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("组织节点名称")
    private String name;
    @ApiModelProperty("简称")
    private String shortName;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("iconSkin")
    private String iconSkin;
    @ApiModelProperty("是否父级 null=false")
    private Boolean isParent;// 是否父级 null=false
    @ApiModelProperty("是否勾选  null=false")
    private Boolean checked;// 是否勾选 null=false
    @ApiModelProperty("是否半勾选 null=false")
    private Boolean halfCheck;// 是否半勾选 null=false
    @ApiModelProperty("是否隐藏勾选框 null=false")
    private Boolean nocheck;// 是否隐藏勾选框 null=false
    @ApiModelProperty("是否打开 null=false")
    private Boolean open;// 是否打开 null=false
    @ApiModelProperty("子节点")
    private List<OrgNode> children;// 子节点
    @ApiModelProperty("id路径")
    private String idPath;// id路径
    @ApiModelProperty("名称全路径")
    private String namePath;// 名称路径
    @ApiModelProperty("智能名称路径")
    private String smartNamePath;

    @JsonIgnore
    private TreeMap<String, List<OrgNode>> treeMap;// 子节点 不参与序列化，用于计算同级节点根据treeMap key 排序

    public TreeMap<String, List<OrgNode>> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<String, List<OrgNode>> treeMap) {
        this.treeMap = treeMap;
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

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean parent) {
        isParent = parent;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getHalfCheck() {
        return halfCheck;
    }

    public void setHalfCheck(Boolean halfCheck) {
        this.halfCheck = halfCheck;
    }

    public Boolean getNocheck() {
        return nocheck;
    }

    public void setNocheck(Boolean nocheck) {
        this.nocheck = nocheck;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getShortName() {
        return shortName;
    }

    public OrgNode setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getSmartNamePath() {
        return smartNamePath;
    }

    public void setSmartNamePath(String smartNamePath) {
        this.smartNamePath = smartNamePath;
    }


    public List<OrgNode> getChildren() {
        if (children != null) {
            return children;
        }
        if (treeMap == null) {
            return null;
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        for (List<OrgNode> value : treeMap.values()) {
            orgNodes.addAll(value);
        }
        return orgNodes;
    }

    public void setChildren(List<OrgNode> children) {
        this.children = children;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof OrgNode))
            return false;
        OrgNode orgNode = (OrgNode) o;
        return this.getId() != null && this.getId().equals(orgNode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
