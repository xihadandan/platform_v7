package com.wellsoft.pt.multi.group.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2022/2/9 16:27
 * @Description:
 */

@ApiModel("集团")
public class MultiGroupVo implements Serializable {


    @ApiModelProperty("uuid")
    protected String uuid;
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("编码")
    private String code;
    @ApiModelProperty(value = "集团名称", required = true)
    @NotBlank(message = "集团名称不能为空")
    private String name;
    @ApiModelProperty("描述")
    private String note;
    @ApiModelProperty("启用(0:否1:是)")
    private Integer isEnable;
    @ApiModelProperty("节点数据")
    @Valid
    private TreeNodeVo treeNode;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public TreeNodeVo getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNodeVo treeNode) {
        this.treeNode = treeNode;
    }
}
