package com.wellsoft.pt.report.component;

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
public enum ReportComponentEnum {

    TABLE_REPORT("wTableReport", "表格报表组件"),
    LINE_CHART("wLineChart", "折线图组件"),
    BAR_CHART("wBarChart", "柱状图组件"),
    PIE_CHART("wPieChart", "饼图组件"),
    BARE_CHART("wBareChart", "空图表组件");

    private String name;
    private String type;
    ReportComponentEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
