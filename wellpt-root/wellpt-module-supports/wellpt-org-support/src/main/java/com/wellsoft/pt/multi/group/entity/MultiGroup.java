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
@Table(name = "MULTI_GROUP")
@DynamicUpdate
@DynamicInsert
@ApiModel("集团")
public class MultiGroup extends IdEntity {


    private static final long serialVersionUID = 3702988419897564137L;


    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("编码")
    private String code;
    @ApiModelProperty("集团名称")
    private String name;
    @ApiModelProperty("描述")
    private String note;
    @ApiModelProperty("启用(0:否1:是)")
    private Integer isEnable;

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
}
