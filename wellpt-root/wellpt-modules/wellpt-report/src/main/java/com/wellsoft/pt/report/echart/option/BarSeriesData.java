package com.wellsoft.pt.report.echart.option;

import java.math.BigDecimal;

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
public class BarSeriesData extends SeriesData {
    private static final long serialVersionUID = 101552405746490993L;

    public BarSeriesData() {

    }

    public BarSeriesData(String name, BigDecimal value) {
        super(name, value);
    }
}
