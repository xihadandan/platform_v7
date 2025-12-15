package com.wellsoft.pt.report.echart.support;

import com.wellsoft.pt.report.echart.option.SeriesData;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/14    chenq		2019/5/14		Create
 * </pre>
 */
public interface EchartSeriesDataLoad {

    /**
     * 数据名称
     *
     * @return
     */
    public String dataName();

    /**
     * 加载数据集方法
     *
     * @param params
     * @return
     */
    List<? extends SeriesData> loadSeriesData(Map<String, Object> params);
}
