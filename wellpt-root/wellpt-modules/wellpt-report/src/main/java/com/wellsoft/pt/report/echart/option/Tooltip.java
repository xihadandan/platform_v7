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
public class Tooltip implements Serializable {
    private static final long serialVersionUID = 6777088402768313078L;

    private String formatter;

    private String backgroundColor;

    private String borderColor;

    private Integer borderWidth;

    private Integer[] padding = new Integer[4];

    private TextStyle textStyle;

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
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

    public Integer[] getPadding() {
        return padding;
    }

    public void setPadding(Integer[] padding) {
        this.padding = padding;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }
}
