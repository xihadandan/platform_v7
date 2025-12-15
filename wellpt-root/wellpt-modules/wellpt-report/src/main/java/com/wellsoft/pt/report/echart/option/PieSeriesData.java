package com.wellsoft.pt.report.echart.option;

import java.math.BigDecimal;

/**
 * Description:饼图系列数据定义
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
public class PieSeriesData extends SeriesData {
    private static final long serialVersionUID = 5983862760698667031L;

    private Boolean selected;

    public PieSeriesData() {

    }

    public PieSeriesData(String name, BigDecimal value) {
        super(name, value);
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }


}
