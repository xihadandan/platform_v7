package com.wellsoft.pt.report.echart.option;


import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.support.DimensionTypeSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Description:维度信息
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
public class Dimension implements Serializable {
    private static final long serialVersionUID = -2271888625755736019L;

    private String name;

    @JsonSerialize(using = DimensionTypeSerializer.class)
    private DimensionTypeEnum type;

    private String displayName;


    public Dimension() {
    }

    public Dimension(String name) {
        this.name = name;
    }

    public Dimension(String name, DimensionTypeEnum type, String displayName) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DimensionTypeEnum getType() {
        return type;
    }

    public void setType(DimensionTypeEnum type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


}
