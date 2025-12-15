package com.wellsoft.pt.multi.group.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/2/9 16:27
 * @Description:
 */

@Entity
@Table(name = "MULTI_GROUP_TREE_NODE")
@DynamicUpdate
@DynamicInsert
@ApiModel("集团节点树")
public class MultiGroupTreeNode extends IdEntity {

    private static final long serialVersionUID = 3332617377717344063L;

    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("简称")
    private String shortName;
    @ApiModelProperty("类型（1，集团，2，单位，3，分类）")
    private Integer type;
    @ApiModelProperty("父级Id")
    private String parentId;
    @ApiModelProperty("集团关联节点ID")
    private String eleId;
    @ApiModelProperty("集团uuid")
    private String groupUuid;
    @ApiModelProperty("排序")
    private Integer seq;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
