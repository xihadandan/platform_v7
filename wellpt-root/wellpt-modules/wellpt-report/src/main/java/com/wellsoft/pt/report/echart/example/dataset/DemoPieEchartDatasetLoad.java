package com.wellsoft.pt.report.echart.example.dataset;

import com.google.common.collect.Lists;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.springframework.stereotype.Component;

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
@Component
public class DemoPieEchartDatasetLoad implements EchartDatasetLoad {
    @Override
    public String datasetName() {
        return "样例_饼图_统计视频网站收视率";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {

        Dataset dataset = new Dataset();
        //添加维度信息
        dataset.getDimensions().add(new Dimension("视频网站"));
        dataset.getDimensions().add(new Dimension("收视率", DimensionTypeEnum.FLOAT, null));

        //添加原始数据key-value
        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("视频网站", "优酷")
                        .add("收视率", new Float(1.11)));
        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("视频网站", "爱奇艺")
                        .add("收视率", new Float(2.13)));
        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("视频网站", "哔哩哔哩")
                        .add("收视率", new Float(0.78)));
        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("视频网站", "芒果TV")
                        .add("收视率", new Float(1.01)));

//        Dataset trasform = new Dataset();
//        trasform.setTransform(new DatasetTransform().put("dimension", "视频网站").put("value", "爱奇艺"));

        return Lists.newArrayList(dataset);
    }
}
