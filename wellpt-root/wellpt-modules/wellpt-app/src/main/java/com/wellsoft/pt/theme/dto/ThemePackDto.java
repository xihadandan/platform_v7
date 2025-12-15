package com.wellsoft.pt.theme.dto;

import com.wellsoft.pt.theme.entity.ThemePackEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月20日   chenq	 Create
 * </pre>
 */
public class ThemePackDto implements Serializable {

    private static final long serialVersionUID = 8367241150484201592L;
    private Long uuid;
    private String name;
    private String themeClass;
    private String themeColors;
    private String fontSizes; // 主题可选字体
    private String defaultThemeColor;
    private String remark;
    private String logo;
    private String thumbnail;
    private ThemePackEntity.Type type;
    private ThemePackEntity.Status status;
    private String defJson;
    private Long specifyUuid;// 主题规范UUID

    private List<Long> tagUuids;
    private List<String> tagNames;

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

    public ThemePackEntity.Type getType() {
        return type;
    }

    public void setType(ThemePackEntity.Type type) {
        this.type = type;
    }

    public ThemePackEntity.Status getStatus() {
        return status;
    }

    public void setStatus(ThemePackEntity.Status status) {
        this.status = status;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public List<Long> getTagUuids() {
        return tagUuids;
    }

    public void setTagUuids(List<Long> tagUuids) {
        this.tagUuids = tagUuids;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Long getSpecifyUuid() {
        return specifyUuid;
    }

    public void setSpecifyUuid(Long specifyUuid) {
        this.specifyUuid = specifyUuid;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
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

    public String getDefaultThemeColor() {
        return defaultThemeColor;
    }

    public void setDefaultThemeColor(String defaultThemeColor) {
        this.defaultThemeColor = defaultThemeColor;
    }

    public String getFontSizes() {
        return fontSizes;
    }

    public void setFontSizes(String fontSizes) {
        this.fontSizes = fontSizes;
    }
}
