package com.wellsoft.pt.app.design.widget.mobile;

import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.mobile.MobileCalendarWidget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author yaolq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月15日.1	yaolq		2018年3月15日		Create
 * </pre>
 * @date 2018年3月15日
 */
@Component
public class MobileCalendarComponent extends UIDesignComponent {

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return MobileCalendarWidget.class;
    }

    @Override
    public String getType() {
        return "wMobileCalendar";
    }

    @Override
    public String getName() {
        return "手机资源日历组件";
    }

    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.APP;
    }

    @Override
    public int getOrder() {
        return 15;
    }

    @Override
    public JavaScriptModule getJavaScriptModule() {
        return getJavaScriptModule("pt.js.module.widget_mobilecalendar.id", "widget_mobilecalendar");
    }

    @Override
    public Map<String, Object> getDefaultOptions() {
        Map<String, Object> options = super.getDefaultOptions();
        options.put("configuration", new HashMap<String, Object>());
        return options;
    }

    /**
     * 添加页面设计样式
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefineCssFiles()
     */
    @Override
    public List<CssFile> getDefineCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.select2.id", "select2"));
        cssFiles.add(getCssFile("pt.css.bootstrap-datetimepicker.id", "bootstrap-datetimepicker"));
        cssFiles.add(getCssFile("pt.css.fullcalendar-resourceview.id", "fullcalendar-resourceview"));
        cssFiles.add(getCssFile("pt.css.fullcalendar.id", "fullcalendar"));
        return cssFiles;
    }

    /**
     * 添加页面解析样式
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainCssFiles()
     */
    @Override
    public List<CssFile> getExplainCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        // cssFiles.add(getCssFile("pt.css.select2.id", "select2"));
        // cssFiles.add(getCssFile("pt.css.bootstrap-datetimepicker.id", "bootstrap-datetimepicker"));
        // cssFiles.add(getCssFile("pt.css.fullcalendar-resourceview.id", "fullcalendar-resourceview"));
        // cssFiles.add(getCssFile("pt.css.fullcalendar.id", "fullcalendar"));
        return cssFiles;
    }
}
