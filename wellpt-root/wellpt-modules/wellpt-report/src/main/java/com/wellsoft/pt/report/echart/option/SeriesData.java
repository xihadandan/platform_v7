package com.wellsoft.pt.report.echart.option;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description:系列的数据
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
public abstract class SeriesData implements Serializable {
    private static final long serialVersionUID = -8482705031350696962L;
    private String name;//数据项名称
    private BigDecimal value;//数据项值
    private Label label;
    private ItemStyle itemStyle;
    private Emphasis emphasis;

    public SeriesData() {

    }

    public SeriesData(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }


    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public ItemStyle getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    public Emphasis getEmphasis() {
        return emphasis;
    }

    public void setEmphasis(Emphasis emphasis) {
        this.emphasis = emphasis;
    }
}
