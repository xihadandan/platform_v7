/*
 * @(#)Oct 20, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.tags;

import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.RequireJSJavaScriptModule;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author wujx    2016-11-11
 * @version 1.0
 */
public abstract class DyformTagSupport extends WebAppTagSupport {

    protected static final Logger logger = LoggerFactory.getLogger(DyformTagSupport.class);
    private static final long serialVersionUID = -8789959609371661877L;
    private static String moduleSeperate = ",";
    private static Map<String, String> dyformCssFileModules = new HashMap<String, String>();
    private static Map<String, String> dyformInputModeJavaScriptModules = new HashMap<String, String>();

    static {
        // 平台第三方
        dyformCssFileModules.put("pt.css.jquery-ui.id", "jquery-ui");
        dyformCssFileModules.put("pt.css.ui-jqgrid.id", "ui-jqgrid");
        dyformCssFileModules.put("pt.css.ui-multiselect.id", "ui-multiselect");
        dyformCssFileModules.put("pt.css.ztree.id", "ztree");
        dyformCssFileModules.put("pt.css.select2.id", "select2");
        dyformCssFileModules.put("pt.css.jquery-fileupload-ui.id", "jquery-fileupload-ui");
        // 表单定制
        dyformCssFileModules.put("pt.css.dyform.id", "dyform");
        dyformCssFileModules.put("pt.css.dyform-fileupload.id", "dyform-fileupload");
        dyformCssFileModules.put("pt.css.dyform-calendar.id", "dyform-calendar");
    }

    static {
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_Text, "wTextInput");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TEXTAREA, "wTextArea");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_CKEDIT, "wCkeditor");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_NUMBER, "wNumberInput");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_DATE, "wDatePicker");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_RADIO, "wRadio");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_CHECKBOX, "wCheckBox");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_SELECTMUTILFASE, "wComboBox");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_SELECT, "wSelect");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_COMBOSELECT, "wComboSelect");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TREESELECT, "wComboTree");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_SerialNumber, "wSerialNumber");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_UNEDITSERIALUMBER, "wSerialNumber");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECT, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTSTAFF, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTDEPARTMENT, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTSTADEP, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTADDRESS, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTJOB, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUP, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTMYDEPT, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTMYPARENTDEPT, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTMYUNIT, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUPSTA, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECTGROUP, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ORGSELECT2, "wUnit2");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TIMEEMPLOY, "wTimeEmploy");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORMEET, "wTimeEmploy");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORCAR, "wTimeEmploy");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TIMEEMPLOYFORDRIVER, "wTimeEmploy");

        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_VIEWDISPLAY, "wViewDisplay");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_DIALOG, "wDialog");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ACCESSORY3, "wFileUpload");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ACCESSORY2, "wFileUpload4Body");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ACCESSORY1, "wFileUpload4Icon,wFileUpload4Body");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_ACCESSORYIMG, "wFileUpload4Image");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_EMBEDDED, "wEmbedded");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_JOB, "wJobSelect");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_TABLEVIEW, "wTableView");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_TAGGROUP, "wTagGroup");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_CHAINED, "wChained");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_COLORS, "wColor");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_SWITCHS, "wSwitchs");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_PROGRESS, "wProgress");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTTYPE_PLACEHOLDER, "wPlaceholder");
        dyformInputModeJavaScriptModules.put(DyFormConfig.INPUTMODE_FILELIBRARY, "wFormFileLibrary");
        // TODO 做前后端分离，dyformInputModeJavaScriptModules映射关系可配置化
    }

    /**
     * @param formUuid
     * @return
     */
    public static Collection<CssFile> getDyformCssFiles(String formUuid) {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        // 表单样式
        for (Entry<String, String> entry : dyformCssFileModules.entrySet()) {
            cssFiles.add(getCssFile(entry.getKey(), entry.getValue()));
        }
        return cssFiles;
    }

    /**
     * @param formUuid
     * @return
     */
    public static Collection<JavaScriptModule> getDyformJavaScriptModules(String formUuid) {
        Set<JavaScriptModule> javaScriptModules = Sets.newHashSet();
        Set<String> javaScriptExplainRequireModules = Sets.newHashSet();
        DyFormFacade dyformApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition dyFormDefinition = dyformApiFacade.getFormDefinition(formUuid);
        if (dyFormDefinition != null) {
            //            throw new RuntimeException("UUID为" + formUuid + "的表单定义不存在!");
            logger.warn("UUID为" + formUuid + "的表单定义不存在！");

            List<DyformFieldDefinition> fieldDefinitionList = dyFormDefinition.doGetFieldDefintions();
            // 加载表单控件JS
            for (DyformFieldDefinition fieldDefinition : fieldDefinitionList) {
                String jsModuleId = dyformInputModeJavaScriptModules.get(fieldDefinition.getInputMode());
                String[] jsModuleIds = null;
                if (jsModuleId == null && DyFormConfig.INPUTMODE_TEMPLATE.equals(fieldDefinition.getInputMode())) {
                    //				DyFormFormDefinition dyFormFormDefinition = dyformApiFacade.getFormDefinitionById(fieldDefinition.getName());
                    //				if(dyFormFormDefinition == null || StringUtils.isBlank(dyFormFormDefinition.getUuid())){
                    continue;
                    //				}
                    //				List<JavaScriptModule> modules = (List)getDyformJavaScriptModules(dyFormFormDefinition.getUuid());
                    //				jsModuleIds = new String[modules.size()];
                    //				for(int i = 0;i < modules.size() ;i++){
                    //					if(modules.get(i) == null){
                    //						continue;
                    //					}
                    //					jsModuleIds[i] = modules.get(i).getId();
                    //				}
                } else {
                    jsModuleIds = jsModuleId.split(moduleSeperate);
                }
                for (String localJsModuleId : jsModuleIds) {
                    addJavaScriptModules(javaScriptModules, localJsModuleId);
                    javaScriptExplainRequireModules.add(localJsModuleId);
                }
            }
            if (dyFormDefinition.getDefinitionJson().indexOf("tableviewid") != -1) {
                //表格控件js
                addJavaScriptModules(javaScriptModules,
                        dyformInputModeJavaScriptModules.get(DyFormConfig.INPUTMODE_TABLEVIEW));
                javaScriptExplainRequireModules.add(dyformInputModeJavaScriptModules.get(DyFormConfig.INPUTMODE_TABLEVIEW));
            }
            if (dyFormDefinition.getDefinitionJson().indexOf("filelibraryid") != -1) {
                //文件目录控件js
                addJavaScriptModules(javaScriptModules,
                        dyformInputModeJavaScriptModules.get(DyFormConfig.INPUTMODE_FILELIBRARY));
                javaScriptExplainRequireModules.add(dyformInputModeJavaScriptModules
                        .get(DyFormConfig.INPUTMODE_FILELIBRARY));
            }


            // 加载从表控件JS
            List<DyformSubformFormDefinition> subformDefinitionList = dyFormDefinition.doGetSubformDefinitions();
            if (subformDefinitionList != null && subformDefinitionList.size() > 0) {
                addJavaScriptModules(javaScriptModules, "wSubForm");
                javaScriptExplainRequireModules.add("wSubForm");
                addJavaScriptModules(javaScriptModules, "wSubForm4Group");
                javaScriptExplainRequireModules.add("wSubForm4Group");
                for (DyformSubformFormDefinition subformDefinition : subformDefinitionList) {
                    // 加载从表子控件JS
                    List<DyformSubformFieldDefinition> subformFieldDefinitionList = subformDefinition
                            .getSubformFieldDefinitions();
                    for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitionList) {
                        try {
                            String subformFieldJsModuleId = dyformInputModeJavaScriptModules.get(subformFieldDefinition
                                    .getInputMode());
                            String[] subformFieldJsModuleIds = subformFieldJsModuleId.split(moduleSeperate);
                            for (String localJsModuleId : subformFieldJsModuleIds) {
                                addJavaScriptModules(javaScriptModules, localJsModuleId);
                                if (StringUtils.isNotBlank(localJsModuleId)) {
                                    javaScriptExplainRequireModules.add(localJsModuleId);
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
            // 加载页签JS
            if (dyFormDefinition.doHasLayout()) {
                addJavaScriptModules(javaScriptModules, "wLayoutManager");
                javaScriptExplainRequireModules.add("wLayoutManager");
            }
            // 加载二开JS
            String customJsModule = dyFormDefinition.getCustomJsModule();
            addJavaScriptModules(javaScriptModules, customJsModule);

        } else {
            for (String javaScriptModule : dyformInputModeJavaScriptModules.values()) {
                addJavaScriptModules(javaScriptModules, javaScriptModule);
            }
        }

        addJavaScriptModules(javaScriptModules, "formDefinitionMethod");
        addJavaScriptModules(javaScriptModules, "DyformFunction");
        addJavaScriptModules(javaScriptModules, "DyformDevelopment");

        javaScriptExplainRequireModules.add("wControlManager");
        // 表单解释添加控件依赖
        addJavaScriptModules(javaScriptModules, "DyformExplain", javaScriptExplainRequireModules);
        // 加载表单解析JS
        addJavaScriptModules(javaScriptModules, "dyform_explain", javaScriptExplainRequireModules);

        // 加载app.js模块及相关依赖
        addJavaScriptModules(javaScriptModules, "bootstrap");
        addJavaScriptModules(javaScriptModules, "jquery-ui");
        addJavaScriptModules(javaScriptModules, "appContext");
        addJavaScriptModules(javaScriptModules, "appWindowManager");
        addJavaScriptModules(javaScriptModules, "wCommonDialog");
        addJavaScriptModules(javaScriptModules, "appModal");
        addJavaScriptModules(javaScriptModules, "appDispatcher");
        addJavaScriptModules(javaScriptModules, "wWidget");
        addJavaScriptModules(javaScriptModules, "dependencyJQueryUIModule");
        addJavaScriptModules(javaScriptModules, "uuid");
        return javaScriptModules;
    }

    protected static void addJavaScriptModules(Collection<JavaScriptModule> javaScriptModules, String jsModuleId) {
        addJavaScriptModules(javaScriptModules, jsModuleId, (String) null);
    }

    protected static void addJavaScriptModules(Collection<JavaScriptModule> javaScriptModules, String jsModuleId, String dependency) {
        if (StringUtils.isNotBlank(jsModuleId)) {
            String id = Config.getValue("pt.js.module." + jsModuleId + ".id", jsModuleId);
            JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
            if (StringUtils.isNotBlank(dependency)) {
                if (jsModule.getDependencies() == null) {
                    ((RequireJSJavaScriptModule) jsModule).setDependencies(new LinkedHashSet<String>());
                }
                jsModule.getDependencies().add(dependency);
            }
            javaScriptModules.add(jsModule);
        }
    }

    protected static void addJavaScriptModules(Collection<JavaScriptModule> javaScriptModules, String jsModuleId,
                                               Set<String> dependencies) {
        if (StringUtils.isNotBlank(jsModuleId)) {
            String id = Config.getValue("pt.js.module." + jsModuleId + ".id", jsModuleId);
            JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
            if (dependencies != null && !dependencies.isEmpty()) {
                if (jsModule.getDependencies() == null) {
                    ((RequireJSJavaScriptModule) jsModule).setDependencies(new LinkedHashSet<String>());
                }
                jsModule.getDependencies().addAll(dependencies);
            }
            javaScriptModules.add(jsModule);
        }
    }

    public static void main(String[] args) {
        System.out.println(JsonUtils.object2Json(DyformTagSupport.dyformInputModeJavaScriptModules));
    }
}