package com.wellsoft.pt.report.component;

import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

/**
 * Description: 空的报表定义组件
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
public class BareChartComponent extends UIDesignComponent {
    @Override
    public JavaScriptModule getJavaScriptModule() {
        return getJavaScriptModule("pt.js.module.widget_bare_chart.id",
                "widget_bare_chart");
    }

    @Override
    public String getType() {
        return ReportComponentEnum.BARE_CHART.getType();
    }

    @Override
    public String getName() {
        return ReportComponentEnum.BARE_CHART.getName();
    }

    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.REPORT;
    }

    @Override
    public int getOrder() {
        return ReportComponentEnum.BARE_CHART.ordinal();
    }
}