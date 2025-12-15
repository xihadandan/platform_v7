package com.wellsoft.pt.repository.entity;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年07月17日   chenq	 Create
 * </pre>
 */
public class WatermarkStyle {

    private String text;

    private String imageFileId;

    private BufferedImage image;

    private WatermarkType type;

    private BigDecimal scale;

    private BigDecimal opacity;

    private WatermarkLayout layout;

    private Integer fontSize;

    private String fontColor;

    private String fontFamily = "微软雅黑";

    private Align horizontalAlign;

    private Align verticalAlign;

    public WatermarkStyle() {
    }

    public WatermarkStyle(String text) {
        this.text = text;
    }

    public Color getColor() {
        String hex = this.fontColor;
        if (StringUtils.isNotBlank(hex)) {
            if (hex.startsWith("#")) {
                hex = hex.substring(1); // 去掉 #
            }

            // 解析 Alpha、Red、Green、Blue 分量
            int a, r, g, b;

            if (hex.length() == 8) { // #AARRGGBB 格式（带 Alpha）
                a = Integer.parseInt(hex.substring(0, 2), 16);
                r = Integer.parseInt(hex.substring(2, 4), 16);
                g = Integer.parseInt(hex.substring(4, 6), 16);
                b = Integer.parseInt(hex.substring(6, 8), 16);
            } else if (hex.length() == 6) { // #RRGGBB 格式（不带 Alpha，默认不透明）
                a = 255; // 完全不透明
                r = Integer.parseInt(hex.substring(0, 2), 16);
                g = Integer.parseInt(hex.substring(2, 4), 16);
                b = Integer.parseInt(hex.substring(4, 6), 16);
            } else {
                throw new IllegalArgumentException("Invalid hex color format: " + hex);
            }

            return new Color(r, g, b, a);
        } else {
            return new Color(72, 72, 72);
        }
    }


    public static enum WatermarkType {
        text, picture;
    }


    public static enum WatermarkLayout {
        horizontal, diagonal;
    }

    public static enum Align {
        left, center, right, top, bottom;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public WatermarkType getType() {
        return type;
    }

    public void setType(WatermarkType type) {
        this.type = type;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public WatermarkLayout getLayout() {
        return layout;
    }

    public void setLayout(WatermarkLayout layout) {
        this.layout = layout;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }


    public String getImageFileId() {
        return imageFileId;
    }

    public void setImageFileId(String imageFileId) {
        this.imageFileId = imageFileId;
    }

    public BigDecimal getOpacity() {
        return opacity;
    }

    public void setOpacity(BigDecimal opacity) {
        this.opacity = opacity;
    }

    public Align getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(Align horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public Align getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(Align verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    @JsonIgnore
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
