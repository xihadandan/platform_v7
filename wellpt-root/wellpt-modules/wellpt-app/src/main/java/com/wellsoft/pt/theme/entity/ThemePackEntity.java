package com.wellsoft.pt.theme.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
@Table(name = "THEME_PACK")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class ThemePackEntity extends Entity {

    private String name;
    private String themeClass;
    private String themeColors; // 主题可选色
    private String defaultThemeColor; // 默认主题色
    private String fontSizes; // 主题可选字体
    private String remark;
    private String logo;
    private String thumbnail;
    private Type type;
    private Status status;
    private String defJson;
    private Long specifyUuid;// 主题规范UUID

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public Long getSpecifyUuid() {
        return specifyUuid;
    }

    public void setSpecifyUuid(Long specifyUuid) {
        this.specifyUuid = specifyUuid;
    }

    public String getThemeClass() {
        return themeClass;
    }

    public void setThemeClass(String themeClass) {
        this.themeClass = themeClass;
    }

    public String getThemeColors() {
        return themeColors;
    }

    public void setThemeColors(String themeColors) {
        this.themeColors = themeColors;
    }

    public String getFontSizes() {
        return fontSizes;
    }

    public void setFontSizes(String fontSizes) {
        this.fontSizes = fontSizes;
    }

    public String getDefaultThemeColor() {
        return defaultThemeColor;
    }

    public void setDefaultThemeColor(String defaultThemeColor) {
        this.defaultThemeColor = defaultThemeColor;
    }

    public static enum Type {
        PC, MOBILE
    }

    public static enum Status {
        UNPUBLISHED, PUBLISHED
    }
}
