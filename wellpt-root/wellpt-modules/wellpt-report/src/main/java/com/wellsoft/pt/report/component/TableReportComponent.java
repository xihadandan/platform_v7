package com.wellsoft.pt.report.component;

import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

/**
 * Description:报表组件
 *
 * @author chenq
 * @date 2019/5/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/10    chenq		2019/5/10		Create
 * </pre>
 */
@Component
public class TableReportComponent extends UIDesignComponent {
    @Override
    public JavaScriptModule getJavaScriptModule() {
        return getJavaScriptModule("pt.js.module.widget_table_report.id", "widget_table_report");
    }

    @Override
    public String getType() {
        return ReportComponentEnum.TABLE_REPORT.getType();
    }

    @Override
    public String getName() {
        return ReportComponentEnum.TABLE_REPORT.getName();
    }

    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.REPORT;
    }

    @Override
    public int getOrder() {
        return ReportComponentEnum.TABLE_REPORT.ordinal();
    }
}
