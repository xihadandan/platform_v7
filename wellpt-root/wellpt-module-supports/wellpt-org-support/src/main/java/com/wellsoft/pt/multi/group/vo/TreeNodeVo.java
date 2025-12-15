package com.wellsoft.pt.multi.group.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/2/10 15:00
 * @Description:
 */

@ApiModel("节点树")
public class TreeNodeVo implements Serializable {
    private static final long serialVersionUID = 6256520869708012305L;
    @ApiModelProperty("子结点列表")
    @Valid
    protected List<TreeNodeVo> children = new ArrayList<>(0);
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    private String name;
    @ApiModelProperty("简称")
    private String shortName;
    @ApiModelProperty(value = "类型（1，集团，2，单位，3，分类）", required = true)
    @NotNull(message = "类型不能为空")
    private Integer type;
    @ApiModelProperty("集团关联节点ID")
    private String eleId;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public List<TreeNodeVo> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNodeVo> children) {
        this.children = children;
    }
}
