/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件标签
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "uf_app_my_calendar")
@DynamicUpdate
@DynamicInsert
public class MyCalendarEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1673871163474743423L;
    public static String RIGHT_ADD = "add"; // 新增日程权限
    public static String RIGHT_EDIT = "edit"; // 编辑日程权限
    public static String RIGHT_DEL = "del"; // 删除日程权限
    public static String RIGHT_STATUS = "status"; // 变更日程状态权限
    public static String PUBLIC_RANGE_NO = "0"; // 不公开
    public static String PUBLIC_RANGE_ALL = "1"; // 公开
    public static String PUBLIC_RANGE_PART = "2"; // 部分人员
    @NotBlank
    @ApiModelProperty("名称")
    private String calendarName; // 名称
    // @NotBlank
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("图标")
    private String icon;
    @ApiModelProperty("图标颜色")
    private String iconColor;
    @ApiModelProperty("备注")
    private String remark;
    private String managers; // 授权于
    private String managersShow;
    private String managersValue;
    private String publicRange; // 公开范围
    private String publicRangeValue;
    private String publicRangeShow;
    private String rightRange; // 权限范围
    private String rightRangeValue;
    private String rightRangeShow;
    private String partUsers; // 允许关注的部分人员范围
    private String partUsersValue;
    private String partUsersShow;
    private Integer isDefault; // 是否默认日历本

    /**
     * @return the calendarName
     */
    public String getCalendarName() {
        return calendarName;
    }

    /**
     * @param calendarName 要设置的calendarName
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /**
     * @return the managers
     */
    public String getManagers() {
        return managers;
    }

    /**
     * @param managers 要设置的managers
     */
    public void setManagers(String managers) {
        this.managers = managers;
    }

    /**
     * @return the managersShow
     */
    public String getManagersShow() {
        return managersShow;
    }

    /**
     * @param managersShow 要设置的managersShow
     */
    public void setManagersShow(String managersShow) {
        this.managersShow = managersShow;
    }

    /**
     * @return the managersValue
     */
    public String getManagersValue() {
        return managersValue;
    }

    /**
     * @param managersValue 要设置的managersValue
     */
    public void setManagersValue(String managersValue) {
        this.managersValue = managersValue;
    }

    /**
     * @return the publicRange
     */
    public String getPublicRange() {
        return publicRange;
    }

    /**
     * @param publicRange 要设置的publicRange
     */
    public void setPublicRange(String publicRange) {
        this.publicRange = publicRange;
    }

    /**
     * @return the publicRangeValue
     */
    public String getPublicRangeValue() {
        return publicRangeValue;
    }

    /**
     * @param publicRangeValue 要设置的publicRangeValue
     */
    public void setPublicRangeValue(String publicRangeValue) {
        this.publicRangeValue = publicRangeValue;
    }

    /**
     * @return the publicRangeShow
     */
    public String getPublicRangeShow() {
        return publicRangeShow;
    }

    /**
     * @param publicRangeShow 要设置的publicRangeShow
     */
    public void setPublicRangeShow(String publicRangeShow) {
        this.publicRangeShow = publicRangeShow;
    }

    /**
     * @return the rightRange
     */
    public String getRightRange() {
        return rightRange;
    }

    /**
     * @param rightRange 要设置的rightRange
     */
    public void setRightRange(String rightRange) {
        this.rightRange = rightRange;
    }

    /**
     * @return the rightRangeValue
     */
    public String getRightRangeValue() {
        return rightRangeValue;
    }

    /**
     * @param rightRangeValue 要设置的rightRangeValue
     */
    public void setRightRangeValue(String rightRangeValue) {
        this.rightRangeValue = rightRangeValue;
    }

    /**
     * @return the rightRangeShow
     */
    public String getRightRangeShow() {
        return rightRangeShow;
    }

    /**
     * @param rightRangeShow 要设置的rightRangeShow
     */
    public void setRightRangeShow(String rightRangeShow) {
        this.rightRangeShow = rightRangeShow;
    }

    /**
     * @return the partUsers
     */
    public String getPartUsers() {
        return partUsers;
    }

    /**
     * @param partUsers 要设置的partUsers
     */
    public void setPartUsers(String partUsers) {
        this.partUsers = partUsers;
    }

    /**
     * @return the partUsersValue
     */
    public String getPartUsersValue() {
        return partUsersValue;
    }

    /**
     * @param partUsersValue 要设置的partUsersValue
     */
    public void setPartUsersValue(String partUsersValue) {
        this.partUsersValue = partUsersValue;
    }

    /**
     * @return the partUsersShow
     */
    public String getPartUsersShow() {
        return partUsersShow;
    }

    /**
     * @param partUsersShow 要设置的partUsersShow
     */
    public void setPartUsersShow(String partUsersShow) {
        this.partUsersShow = partUsersShow;
    }

    /**
     * @return the isDefault
     */
    public Integer getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
