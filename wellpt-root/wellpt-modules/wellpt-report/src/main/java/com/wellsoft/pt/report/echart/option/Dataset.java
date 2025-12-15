package com.wellsoft.pt.report.echart.option;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 图表选项：数据集
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
public class Dataset implements Serializable {
    private static final long serialVersionUID = 2667586762640584258L;

    private String id;


    private ArrayList<Dimension> dimensions = Lists.newArrayList();//定义维度信息

    private Boolean sourceHeader;//第一行/列是否是 维度名 信息

    private ArrayList<SourceElement> source = Lists.newArrayList();//原始数据key-value

    private DatasetTransform transform = null;

    public static void main(String[] arrs) {
        Dataset dataset = new Dataset();
        dataset.getDimensions().add(new Dimension("nnn", DimensionTypeEnum.STRING, null));
        ObjectMapper map = new ObjectMapper();

        System.out.println(new Gson().toJson(dataset));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(ArrayList<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public Boolean getSourceHeader() {
        return sourceHeader;
    }

    public void setSourceHeader(Boolean sourceHeader) {
        this.sourceHeader = sourceHeader;
    }

    public ArrayList<SourceElement> getSource() {
        return source;
    }

    public void setSource(
            ArrayList<SourceElement> source) {
        this.source = source;
    }

    public DatasetTransform getTransform() {
        return transform;
    }

    public void setTransform(DatasetTransform transform) {
        this.transform = transform;
    }
}
