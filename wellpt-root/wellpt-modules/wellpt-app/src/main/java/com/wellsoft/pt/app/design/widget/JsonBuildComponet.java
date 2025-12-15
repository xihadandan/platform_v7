package com.wellsoft.pt.app.design.widget;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.client.widget.JsonBuildWidget;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 基于json配置生成的组件定义类
 */
public class JsonBuildComponet extends UIDesignComponent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String type;
    private String name;
    private ComponentCategory category;
    private int order = 0;
    private boolean isConfigurable = false;
    private List<String> scope = Lists.newArrayList("wPage");
    private List<String> css;
    private List<String> explainJs;
    private String defineJs;
    private boolean enable = true;
    private Class<? extends Widget> viewClass;

    private JsonBuildComponet(Map<String, Map<String, Object>> map) {
        this(map, 0);
    }

    private JsonBuildComponet(Map<String, Map<String, Object>> map, int order) {
        type = map.keySet().iterator().next();
        name = map.get(type).get("name").toString();
        enable = (boolean) map.get(type).get("enable");
        String categoryStr = map.get(type).get("category").toString();
        if (SimpleComponentCategory.APP.getId().equalsIgnoreCase(categoryStr)) {
            category = SimpleComponentCategory.APP;
        } else if (SimpleComponentCategory.BASIC.getId().equalsIgnoreCase(categoryStr)) {
            category = SimpleComponentCategory.BASIC;
        } else if (SimpleComponentCategory.REPORT.getId().equalsIgnoreCase(categoryStr)) {
            category = SimpleComponentCategory.REPORT;
        } else if (SimpleComponentCategory.LAYOUT.getId().equalsIgnoreCase(categoryStr)) {
            category = SimpleComponentCategory.LAYOUT;
        }
        this.order = order;
        if (map.get(type).containsKey("configurable")) {
            this.isConfigurable = (boolean) map.get(type).get("configurable");
        }
        Object scope = map.get(type).get("scope");
        if (scope != null) {
            if (scope instanceof String) {
                this.scope = Lists.newArrayList(map.get(type).get("scope").toString());
            } else {
                this.scope = (List<String>) map.get(type).get("scope");
            }
        }

        if (map.get(type).containsKey("enable")) {
            this.enable = (boolean) map.get(type).get("enable");
        }
        this.css = (List<String>) map.get(type).get("css");
        Object explainJs = map.get(type).get("explainJs");
        if (explainJs != null) {
            if (explainJs instanceof String) {
                this.explainJs = Lists.newArrayList(map.get(type).get("explainJs").toString());
            } else {
                this.explainJs = (List<String>) map.get(type).get("explainJs");
            }
        }


        if (map.get(type).containsKey("defineJs")) {
            this.defineJs = (String) map.get(type).get("defineJs");
        } else {
            String camelPart = type.indexOf("_") != -1 ? type.substring(0, type.indexOf("_")) : type;
            String[] parts = StringUtils.splitByCharacterTypeCamelCase(camelPart);
            parts[0] = "widget";
            for (int i = 1; i < parts.length; i++) {
                parts[i] = StringUtils.lowerCase(parts[i]);
            }
            this.defineJs = StringUtils.join(parts, "_");
        }

        if (map.get(type).containsKey("viewClass")) {
            try {
                this.viewClass = (Class<? extends Widget>) Class.forName(map.get(type).get("viewClass").toString());
            } catch (Exception e) {
                logger.error("创建组件实例，获取组件对应的视图解析类异常：", e);
            }

        }
    }

    public static JsonBuildComponet build(Map<String, Map<String, Object>> map) {
        return new JsonBuildComponet(map);
    }

    @Override
    public JavaScriptModule getJavaScriptModule() {
        return AppContextHolder.getContext().getJavaScriptModule(this.defineJs);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ComponentCategory getCategory() {
        return category;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isConfigurable() {
        return isConfigurable;
    }

    @Override
    public Class<? extends View> getViewClass() {
        return this.viewClass == null ? JsonBuildWidget.class : this.viewClass;
    }

    public List<String> getScope() {
        return scope;
    }

    @Override
    public List<CssFile> getDefineCssFiles() {
        if (CollectionUtils.isNotEmpty(this.css)) {
            List<CssFile> cssFiles = new ArrayList<CssFile>();
            for (String cs : this.css) {
                CssFile cssFile = AppContextHolder.getContext().getCssFile(cs);
                if (cssFile != null) {
                    cssFiles.add(cssFile);
                }
            }
            return cssFiles;
        }
        return Collections.EMPTY_LIST;

    }

    @Override
    public List<JavaScriptModule> getExplainJavaScriptModules(AppContext appContext, View view, HttpServletRequest request, HttpServletResponse response) {
        if (CollectionUtils.isNotEmpty(this.explainJs)) {
            List<JavaScriptModule> javaScriptModules = Lists.newArrayList();
            for (String j : explainJs) {
                JavaScriptModule javaScriptModule = AppContextHolder.getContext().getJavaScriptModule(j);
                if (javaScriptModule != null) {
                    javaScriptModules.add(javaScriptModule);
                }
            }
            return javaScriptModules;
        }

        return Collections.EMPTY_LIST;
    }

    public boolean isEnable() {
        return enable;
    }

    public List<String> getExplainJs() {
        return explainJs;
    }

    public void setExplainJs(List<String> explainJs) {
        this.explainJs = explainJs;
    }

    public String getDefineJs() {
        return defineJs;
    }

    public void setDefineJs(String defineJs) {
        this.defineJs = defineJs;
    }
}
