package com.wellsoft.pt.report.echart.option;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/22    chenq		2019/5/22		Create
 * </pre>
 */
public class Label implements Serializable {
    private static final long serialVersionUID = 1897217624874063918L;

    private Boolean show;

    private String position;

    private String color;

    private String fontStyle;

    private String fontWeight;

    private String fontFamily;

    private Integer fontSize;

    private Integer rotate;

    private Integer[] offset = new Integer[2];

    private String align;

    private String verticalAlign;

    private Integer lineHeight;

    private String backgroundColor;

    private String borderColor;

    private Integer borderWidth;

    private Integer[] borderRadius = new Integer[4];

    private Integer padding;

    private String shadowColor;

    private Integer shadowBlur;

    private Integer shadowOffsetX;

    private Integer shadowOffsetY;

    private String width;

    private String height;

    private String textBorderColor;

    private Integer textBorderWidth;

    private String textShadowColor;

    private Integer textShadowBlur;

    private Integer textShadowOffsetX;

    private Integer textShadowOffsetY;


    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(String verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public Integer getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(Integer lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer[] getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(Integer[] borderRadius) {
        this.borderRadius = borderRadius;
    }

    public Integer getPadding() {
        return padding;
    }

    public void setPadding(Integer padding) {
        this.padding = padding;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public Integer getShadowBlur() {
        return shadowBlur;
    }

    public void setShadowBlur(Integer shadowBlur) {
        this.shadowBlur = shadowBlur;
    }

    public Integer getShadowOffsetX() {
        return shadowOffsetX;
    }

    public void setShadowOffsetX(Integer shadowOffsetX) {
        this.shadowOffsetX = shadowOffsetX;
    }

    public Integer getShadowOffsetY() {
        return shadowOffsetY;
    }

    public void setShadowOffsetY(Integer shadowOffsetY) {
        this.shadowOffsetY = shadowOffsetY;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTextBorderColor() {
        return textBorderColor;
    }

    public void setTextBorderColor(String textBorderColor) {
        this.textBorderColor = textBorderColor;
    }

    public Integer getTextBorderWidth() {
        return textBorderWidth;
    }

    public void setTextBorderWidth(Integer textBorderWidth) {
        this.textBorderWidth = textBorderWidth;
    }

    public String getTextShadowColor() {
        return textShadowColor;
    }

    public void setTextShadowColor(String textShadowColor) {
        this.textShadowColor = textShadowColor;
    }

    public Integer getTextShadowBlur() {
        return textShadowBlur;
    }

    public void setTextShadowBlur(Integer textShadowBlur) {
        this.textShadowBlur = textShadowBlur;
    }

    public Integer getTextShadowOffsetX() {
        return textShadowOffsetX;
    }

    public void setTextShadowOffsetX(Integer textShadowOffsetX) {
        this.textShadowOffsetX = textShadowOffsetX;
    }

    public Integer getTextShadowOffsetY() {
        return textShadowOffsetY;
    }

    public void setTextShadowOffsetY(Integer textShadowOffsetY) {
        this.textShadowOffsetY = textShadowOffsetY;
    }

    public Integer getRotate() {
        return rotate;
    }

    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }

    public Integer[] getOffset() {
        return offset;
    }

    public void setOffset(Integer[] offset) {
        this.offset = offset;
    }
}
