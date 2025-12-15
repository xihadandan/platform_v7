package com.wellsoft.pt.report.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.SeriesData;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.report.echart.support.EchartSeriesDataLoad;
import com.wellsoft.pt.report.facade.service.ReportFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class ReportFacadeServiceImpl extends AbstractApiFacade implements ReportFacadeService {

    @Resource
    private List<EchartDatasetLoad> datasetLoadService;

    @Resource
    private List<EchartSeriesDataLoad> seriesDataLoads;

    @Override
    public Select2QueryData loadEchartDatasetServiceData(Select2QueryInfo queryInfo) {
        Select2QueryData select2QueryData = new Select2QueryData();
        if (CollectionUtils.isNotEmpty(datasetLoadService)) {
            for (EchartDatasetLoad load : datasetLoadService)
                select2QueryData.addResultData(
                        new Select2DataBean(AopUtils.getTargetClass(load).getCanonicalName(),
                                load.datasetName()));
        }

        return select2QueryData;
    }

    @Override
    public Select2QueryData loadEchartSeriesDataServiceData(Select2QueryInfo queryInfo) {
        Select2QueryData select2QueryData = new Select2QueryData();
        if (CollectionUtils.isNotEmpty(seriesDataLoads)) {
            for (EchartSeriesDataLoad load : seriesDataLoads)
                select2QueryData.addResultData(
                        new Select2DataBean(AopUtils.getTargetClass(load).getCanonicalName(),
                                load.dataName()));
        }

        return select2QueryData;
    }


    @Override
    public List<Dataset> loadDataset(String datasetLoadClass, Map<String, Object> params) {
        try {
            EchartDatasetLoad load = (EchartDatasetLoad) ApplicationContextHolder.getBean(
                    Class.forName(datasetLoadClass));
            return load.loadDataset(params);
        } catch (Exception e) {
            logger.error("无法获取class=[{}]的图表数据集加载服务", datasetLoadClass);
        }


        return null;
    }

    @Override
    public List<? extends SeriesData> loadSeriesData(String seriesDataLoadClass,
                                                     Map<String, Object> params) {
        try {
            EchartSeriesDataLoad load = (EchartSeriesDataLoad) ApplicationContextHolder.getBean(
                    Class.forName(seriesDataLoadClass));
            return load.loadSeriesData(params);
        } catch (Exception e) {
            logger.error("无法获取class=[{}]的图表系列数据加载服务", seriesDataLoadClass);
        }


        return null;
    }
}
