package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yt
 * @title: MessageClassify 消息分类
 * @date 2020/5/18 6:54 下午
 */
@Entity
@Table(name = "msg_message_classify")
@DynamicUpdate
@DynamicInsert
@ApiModel("消息分类")
public class MessageClassify extends TenantEntity {

    @NotBlank
    @ApiModelProperty("分类名称")
    private String name;//分类名称
    @ApiModelProperty("分类图标")
    private String icon;//分类图标
    @ApiModelProperty("分类图标颜色")
    private String iconBg;//分类图标颜色
    @ApiModelProperty("编号")
    private String code;//编号
    @ApiModelProperty("启用分类(0:否1:是)")
    private Integer isEnable;//启用分类(0:否1:是)
    @ApiModelProperty("描述")
    private String note;//描述

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconBg() {
        return iconBg;
    }

    public void setIconBg(String iconBg) {
        this.iconBg = iconBg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
