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
public class TextStyle implements Serializable {
    private static final long serialVersionUID = -5787151083288698780L;

    private String color;

    private String fontStyle;

    private String fontWeight;

    private String fontFamily;

    private Integer fontSize;

    private Integer lineHeight;

    private String width;

    private String height;

    private String textBorderColor;

    private String textShadowColor;

    private Integer textBorderWidth;

    private Integer textShadowBlur;

    private Integer textShadowOffsetX;

    private Integer textShadowOffsetY;

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

    public Integer getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(Integer lineHeight) {
        this.lineHeight = lineHeight;
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

    public String getTextShadowColor() {
        return textShadowColor;
    }

    public void setTextShadowColor(String textShadowColor) {
        this.textShadowColor = textShadowColor;
    }

    public Integer getTextBorderWidth() {
        return textBorderWidth;
    }

    public void setTextBorderWidth(Integer textBorderWidth) {
        this.textBorderWidth = textBorderWidth;
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
}
