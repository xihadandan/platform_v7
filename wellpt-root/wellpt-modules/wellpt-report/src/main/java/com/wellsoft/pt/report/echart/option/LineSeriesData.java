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
public class LineSeriesData extends SeriesData {
    private static final long serialVersionUID = -1715724025951487679L;
    private String symbol;//单个数据标记的图形
    private Integer[] symbolSize = new Integer[2];//单个数据标记的大小
    private Double symbolRotate;//单个数据标记的旋转角度
    private Boolean symbolKeepAspect;//是否在缩放时保持该图形的长宽比
    private Double[] symbolOffset = new Double[2];//单个数据标记相对于原本位置的偏移

    public LineSeriesData() {

    }

    public LineSeriesData(String name, BigDecimal value) {
        super(name, value);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer[] getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(Integer[] symbolSize) {
        this.symbolSize = symbolSize;
    }

    public Double getSymbolRotate() {
        return symbolRotate;
    }

    public void setSymbolRotate(Double symbolRotate) {
        this.symbolRotate = symbolRotate;
    }

    public Boolean getSymbolKeepAspect() {
        return symbolKeepAspect;
    }

    public void setSymbolKeepAspect(Boolean symbolKeepAspect) {
        this.symbolKeepAspect = symbolKeepAspect;
    }

    public Double[] getSymbolOffset() {
        return symbolOffset;
    }

    public void setSymbolOffset(Double[] symbolOffset) {
        this.symbolOffset = symbolOffset;
    }
}
