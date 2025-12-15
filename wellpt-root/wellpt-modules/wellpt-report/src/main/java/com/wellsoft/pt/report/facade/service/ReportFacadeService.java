package com.wellsoft.pt.report.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.report.echart.option.Dataset;
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
public interface ReportFacadeService extends Facade {

    /**
     * 查询echart数据集服务接口
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadEchartDatasetServiceData(Select2QueryInfo queryInfo);

    Select2QueryData loadEchartSeriesDataServiceData(Select2QueryInfo queryInfo);

    public List<Dataset> loadDataset(String datasetLoadClass, Map<String, Object> params);

    public List<? extends SeriesData> loadSeriesData(String seriesDataLoadClass,
                                                     Map<String, Object> params);
}
