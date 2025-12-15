package com.wellsoft.pt.report.echart.example;

import com.google.common.collect.Lists;
import com.wellsoft.pt.report.echart.option.ItemStyle;
import com.wellsoft.pt.report.echart.option.PieSeriesData;
import com.wellsoft.pt.report.echart.support.EchartSeriesDataLoad;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
@Component
public class DemoPieEchartSeriesDataLoad implements EchartSeriesDataLoad {
    @Override
    public String dataName() {
        return "样例_饼图_统计视频网站收视率系列数据";
    }

    @Override
    public List<PieSeriesData> loadSeriesData(Map<String, Object> params) {

        List<PieSeriesData> pieSeriesDataList = Lists.newArrayList();

        pieSeriesDataList.add(new PieSeriesData("优酷", new BigDecimal("1.12")));
        pieSeriesDataList.add(new PieSeriesData("芒果TV", new BigDecimal("0.77")));
        pieSeriesDataList.add(new PieSeriesData("爱奇艺", new BigDecimal("1.01")));

        PieSeriesData designData = new PieSeriesData("哔哩哔哩", new BigDecimal("0.5"));
        ItemStyle itemStyle = new ItemStyle();
        itemStyle.setColor("#cacc21");
        designData.setItemStyle(itemStyle);
        pieSeriesDataList.add(designData);
        return pieSeriesDataList;
    }
}
