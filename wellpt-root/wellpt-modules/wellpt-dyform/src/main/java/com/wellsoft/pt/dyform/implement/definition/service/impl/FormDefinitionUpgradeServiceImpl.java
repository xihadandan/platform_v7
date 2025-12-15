/*
 * @(#)2021年3月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFormPropertyName;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionUpgradeService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
@Service
public class FormDefinitionUpgradeServiceImpl implements FormDefinitionUpgradeService {

    private static Set<String> SUBFORM_BUILT_IN_BTN_CODES = Sets.newHashSet("btn_add", "btn_edit", "btn_del",
            "btn_clear", "btn_up", "btn_down", "btn_add_sub", "btn_exp_subform", "btn_imp_subform");
    private Logger logger = LoggerFactory.getLogger(FormDefinitionUpgradeServiceImpl.class);
    @Autowired
    private FormDefinitionService formDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionUpgradeService#upgrade2v6_2_3()
     */
    @Override
    public List<String> upgrade2v6_2_3(String formUuid) {
        List<String> updatedFormInfos = Lists.newArrayList();
        List<FormDefinition> formDefinitions = null;
        if (StringUtils.isBlank(formUuid)) {
            formDefinitions = formDefinitionService.getAllFormDefintions();
        } else {
            formDefinitions = formDefinitionService.getFormDefinitionsById(dyFormFacade.getFormIdByFormUuid(formUuid));
        }
        Set<FormDefinition> toUpdateDefinitions = Sets.newHashSet();
        for (FormDefinition formDefinition : formDefinitions) {
            try {
                addFormDefinitionUpgrade2v6_2_3IfRequired(formDefinition, toUpdateDefinitions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (FormDefinition formDefinition : toUpdateDefinitions) {
            try {
                updatedFormInfos.add(formDefinition.getName() + ":" + formDefinition.getVersion());
                formDefinitionService.updateFormDefinitionAndFormTable(formDefinition,
                        Lists.<String>newArrayListWithExpectedSize(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updatedFormInfos;
    }

    public List<String> upgrade_v6_2_3_repair_json(String formUuid) {
        List<String> updatedFormInfos = Lists.newArrayList();
        List<FormDefinition> formDefinitions = null;
        if (StringUtils.isBlank(formUuid)) {
            formDefinitions = formDefinitionService.getAllFormDefintions();
        } else {
            formDefinitions = formDefinitionService.getFormDefinitionsById(dyFormFacade.getFormIdByFormUuid(formUuid));
        }
        Set<FormDefinition> toUpdateDefinitions = Sets.newHashSet();
        for (FormDefinition formDefinition : formDefinitions) {
            try {
                addFormDefinitionUpgrade2v6_2_3_RepaieJson(formDefinition, toUpdateDefinitions);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        for (FormDefinition formDefinition : toUpdateDefinitions) {
            try {
                updatedFormInfos.add(formDefinition.getName() + ":" + formDefinition.getVersion());
                formDefinitionService.updateFormDefinitionAndFormTable(formDefinition,
                        Lists.<String>newArrayListWithExpectedSize(0));
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return updatedFormInfos;
    }

    public List<String> upgrade_v6_2_5_repair_readonly_style(String formUuid) {
        List<String> updatedFormInfos = Lists.newArrayList();
        List<FormDefinition> formDefinitions = null;
        if (StringUtils.isBlank(formUuid)) {
            formDefinitions = formDefinitionService.getAllFormDefintions();
        } else {
            formDefinitions = formDefinitionService.getFormDefinitionsById(dyFormFacade.getFormIdByFormUuid(formUuid));
        }
        Set<FormDefinition> toUpdateDefinitions = Sets.newHashSet();
        for (FormDefinition formDefinition : formDefinitions) {
            try {
                addFormDefinitionUpgrade2v6_2_3_RepaieReadonlyStyle(formDefinition, toUpdateDefinitions);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        for (FormDefinition formDefinition : toUpdateDefinitions) {
            try {
                updatedFormInfos.add(formDefinition.getName() + ":" + formDefinition.getVersion());
                formDefinitionService.updateFormDefinitionAndFormTable(formDefinition,
                        Lists.<String>newArrayListWithExpectedSize(0));
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return updatedFormInfos;
    }

    /**
     * @param formDefinition
     * @throws JSONException
     */
    private void addFormDefinitionUpgrade2v6_2_3IfRequired(FormDefinition formDefinition,
                                                           Set<FormDefinition> toUpdateDefinitions) throws Exception {
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        // 字段定义升级
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            for (String fieldName : fieldNames) {
                JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
                String inputMode = formDefinitionHandler.getPropertyValueOfInputMode(fieldName);
                // 列表附件
                if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode)) {
                    if (fileListUpgrade2v6_2_3(jsonObject)) {
                        toUpdateDefinitions.add(formDefinition);
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORY1.equals(inputMode)) {
                    // 图标附件
                    if (iconUpgrade2v6_2_3(jsonObject)) {
                        toUpdateDefinitions.add(formDefinition);
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORYIMG.equals(inputMode)) {
                    // 图片附件
                    if (imageUpgrade2v6_2_3(jsonObject)) {
                        toUpdateDefinitions.add(formDefinition);
                    }
                }
            }
        }

        // 从表定义升级
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                JSONObject jsonObject = formDefinitionHandler.getSubformJSONObject(subformUuid);
                if (tableButtonInfoUpgrade2v6_2_3(jsonObject)) {
                    toUpdateDefinitions.add(formDefinition);
                }
            }
        }

        if (toUpdateDefinitions.contains(formDefinition)) {
            formDefinition.setDefinitionJson(formDefinitionHandler.toString());
        }
    }

    /**
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private boolean fileListUpgrade2v6_2_3(JSONObject jsonObject) throws JSONException {
        boolean hasChange = false;
        if (!jsonObject.has("secDevBtnIdStr")) {
            List<String> secDevBtnIdStrs = Lists.newArrayList();
            if (jsonObject.has("allowDelete") && jsonObject.getBoolean("allowDelete")) {
                secDevBtnIdStrs.add("09691fb3-fdd3-4790-863d-6b77d4aa4bbd");
            }
            if (jsonObject.has("allowUpload") && jsonObject.getBoolean("allowUpload")) {
                secDevBtnIdStrs.add("00b13afb-8afc-4a9e-b1e2-28f321f48924");
            }
            if (jsonObject.has("allowDownload") && jsonObject.getBoolean("allowDownload")) {
                secDevBtnIdStrs.add("4ee25050-f1e5-49d2-a635-9c19ef1785dc");
            }
            secDevBtnIdStrs.add("954dac1c-8ae8-4bd2-86a6-e3283325989c");
            secDevBtnIdStrs.add("5f82f10a-9450-4a18-8c8d-e38e3767b466");
            secDevBtnIdStrs.add("4ee25050-f1e5-49d2-a635-9c19ef1785dc");
            jsonObject.put("secDevBtnIdStr", StringUtils.join(secDevBtnIdStrs, ";"));
            hasChange = true;
        }
        return hasChange;
    }

    /**
     * @param jsonObject
     * @throws Exception
     */
    private boolean iconUpgrade2v6_2_3(JSONObject jsonObject) throws Exception {
        boolean hasChange = false;
        if (!jsonObject.has("operateBtns")) {
            List<String> operateBtns = Lists.<String>newArrayList();
            if (jsonObject.has("allowDelete") && jsonObject.getBoolean("allowDelete")) {
                operateBtns.add("14");
                operateBtns.add("3");
            }
            if (jsonObject.has("allowUpload") && jsonObject.getBoolean("allowUpload")) {
                if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                    operateBtns.add("1");
                    operateBtns.add("4");
                    operateBtns.add("5");
                } else {
                    operateBtns.add("1");
                    operateBtns.add("4");
                }
            }
            if (jsonObject.has("allowDownload") && jsonObject.getBoolean("allowDownload")) {
                if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                    operateBtns.add("2");
                } else {
                    operateBtns.add("6");
                    operateBtns.add("2");
                }
            }
            operateBtns.add("15");
            operateBtns.add("12");
            operateBtns.add("7");
            // 下载、复制名称、编辑、重命名、上移、下移["2","13","8","9","10","11"]
            operateBtns.add("2");
            operateBtns.add("13");
            operateBtns.add("8");
            operateBtns.add("9");
            operateBtns.add("10");
            operateBtns.add("11");
            jsonObject.put("operateBtns", operateBtns);
            hasChange = true;
        } else {
            JSONArray jsonArray = jsonObject.getJSONArray("operateBtns");
            if (!containsOperateBtns(jsonArray, "15")) {
                jsonArray.put("15");
            }
            if (!containsOperateBtns(jsonArray, "4")) {
                jsonArray.put("4");
            }
            if (!containsOperateBtns(jsonArray, "12")) {
                jsonArray.put("12");
            }
            if (!containsOperateBtns(jsonArray, "7")) {
                jsonArray.put("7");
            }
            hasChange = true;
        }
        return hasChange;
    }

    /**
     * @param jsonArray
     * @param operateBtn
     * @return
     * @throws JSONException
     */
    private boolean containsOperateBtns(JSONArray jsonArray, String operateBtn) throws JSONException {
        for (int index = 0; index < jsonArray.length(); index++) {
            if (StringUtils.equals(operateBtn, ObjectUtils.toString(jsonArray.get(index)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param jsonObject
     * @throws Exception
     */
    private boolean imageUpgrade2v6_2_3(JSONObject jsonObject) throws Exception {
        boolean requiredUpdate = false;
        if (!jsonObject.has("allowPreview")) {
            jsonObject.put("allowPreview", true);
            requiredUpdate = true;
        }
        if (!jsonObject.has("mutiselect")) {
            jsonObject.put("mutiselect", true);
            requiredUpdate = true;
        }
        return requiredUpdate;
    }

    /**
     * @param jsonObject
     * @throws Exception
     */
    private boolean tableButtonInfoUpgrade2v6_2_3(JSONObject jsonObject) throws Exception {
        boolean requiredUpdate = false;
        if (!jsonObject.has("tableButtonInfo")) {
            return requiredUpdate;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("tableButtonInfo");
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject buttonInfo = jsonArray.getJSONObject(index);
            if (buttonInfo.has("type")) {
                continue;
            }
            String code = buttonInfo.getString("code");
            if (isBuiltInButtonCode(code)) {
                buttonInfo.put("type", "内置按钮");
                if ("btn_exp_subform".equals(code)) {
                    buttonInfo.put("operate", "显示类操作");
                } else {
                    buttonInfo.put("operate", "编辑类操作");
                }
            } else {
                buttonInfo.put("type", "扩展按钮");
                buttonInfo.put("operate", "edit");
            }
            requiredUpdate = true;
        }
        if (!hasCopyBtn(jsonArray)) {
            addCopyBtn(jsonArray);
            requiredUpdate = true;
        }
        return requiredUpdate;
    }

    /**
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    private boolean hasCopyBtn(JSONArray jsonArray) throws JSONException {
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject buttonInfo = jsonArray.getJSONObject(index);
            if (StringUtils.equals("btn_copy", buttonInfo.getString("code"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param jsonArray
     */
    private void addCopyBtn(JSONArray jsonArray) {
        Map<String, Object> copyBtn = Maps.newHashMap();
        copyBtn.put("uuid", UuidUtils.createUuid());
        copyBtn.put("text", "复制行");
        copyBtn.put("code", "btn_copy");
        copyBtn.put("cssClass", "");
        copyBtn.put("type", "内置按钮");
        copyBtn.put("operate", "编辑类操作");
        copyBtn.put("displayText", "鼠标点击事件;");
        copyBtn.put("position", Lists.<String>newArrayList("2"));
        Map<String, Object> buttonEvents = Maps.newHashMap();
        buttonEvents.put("click",
                "DyformFacade.addRowData(DyformFacade.get$dyform().getFormId(),data.formUuid, data, dysubform, true);");
        copyBtn.put("buttonEvents", buttonEvents);
        jsonArray.put(copyBtn);
    }

    /**
     * @param code
     * @return
     */
    private boolean isBuiltInButtonCode(String code) {
        return SUBFORM_BUILT_IN_BTN_CODES.contains(code);
    }

    /**
     * @param formDefinition
     * @throws JSONException
     */
    private void addFormDefinitionUpgrade2v6_2_3_RepaieJson(FormDefinition formDefinition,
                                                            Set<FormDefinition> toUpdateDefinitions) throws Exception {
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        String html = formDefinitionHandler.getFormPropertyOfStringType(EnumFormPropertyName.html);
        if (StringUtils.isBlank(html)) {
            return;
        }
        Document document = Jsoup.parse(html);
        // 子表单（非脱离）
        Elements elements = document.select(".template-wrapper[templateUuid]");
        if (CollectionUtils.isNotEmpty(elements)) {
            JSONObject templatesObj = formDefinitionHandler.getFormPropertyOfJSONType(EnumFormPropertyName.templates);
            if (JSONObject.NULL.equals(templatesObj)) {
                templatesObj = new JSONObject();
            }
            boolean update = false;
            for (Element element : elements) {
                String templateuuid = element.attr("templateuuid");
                if (templatesObj.has(templateuuid)) {
                    continue;
                }
                // 脱离表单
                String templateFlag = element.attr("templateFlag");
                if (StringUtils.equalsIgnoreCase("true", templateFlag)) {
                    continue;
                }
                update = true;
                String templatename = element.attr("templatename");
                JSONObject templateObj = new JSONObject();
                templateObj.put("templateUuid", templateuuid);
                templateObj.put("templateName", templatename);
                templateObj.put("templateFlag", templateFlag);
                templatesObj.put(templateuuid, templateObj);
            }
            if (update) {
                toUpdateDefinitions.add(formDefinition);
                formDefinitionHandler.addFormProperty(EnumFormPropertyName.templates, templatesObj);
            }
            // 移除子表单（子表单下的区块不需要重构）
            elements.remove();
        }
        // 区块
        elements = document.select(".title[blockcode]");
        if (CollectionUtils.isNotEmpty(elements)) {
            JSONObject blocksObj = formDefinitionHandler.getBlockDefinitionJSONObjects();
            boolean update = false;
            for (Element element : elements) {
                String blockcode = element.attr("blockcode");
                if (blocksObj.has(blockcode)) {
                    continue;
                }
                update = true;
                String blockTitle = element.text();
                String blockanchor = element.attr("blockanchor");
                String blockhide = element.attr("blockhide");
                JSONObject blockObj = new JSONObject();
                blockObj.put("blockCode", blockcode);
                blockObj.put("blockTitle", blockTitle);
                blockObj.put("hide", Boolean.valueOf(blockhide));
                blockObj.put("blockAnchor", Boolean.valueOf(blockanchor));
                blocksObj.put(blockcode, blockObj);
            }
            if (update) {
                toUpdateDefinitions.add(formDefinition);
                formDefinitionHandler.addFormProperty(EnumFormPropertyName.blocks, blocksObj);
            }
        }
        // 页签
        elements = document.select(".layout[name]");
        if (CollectionUtils.isNotEmpty(elements)) {
            JSONObject layoutsObj = formDefinitionHandler.getLayOutDefinitionJSONObjects();
            for (Element element : elements) {
                String name = element.attr("name");
                if (false == layoutsObj.has(name)) {
                    loggerFormError(formDefinition, "缺少页签定义[" + name + "]");
                    continue;
                }
                JSONObject layoutObj = layoutsObj.getJSONObject(name);
                Elements elements2 = element.select(">.subtab-design[name]");
                if (CollectionUtils.isNotEmpty(elements2)) {
                    JSONObject subtabsObj = layoutObj.getJSONObject("subtabs");
                    for (Element element2 : elements2) {
                        String name2 = element2.attr("name");
                        if (false == subtabsObj.has(name2)) {
                            loggerFormError(formDefinition, "缺少页签项定义[" + name2 + "]");
                        }
                    }
                }
            }
        }
        if (toUpdateDefinitions.contains(formDefinition)) {
            formDefinition.setDefinitionJson(formDefinitionHandler.toString());
        }
    }

    private void addFormDefinitionUpgrade2v6_2_3_RepaieReadonlyStyle(FormDefinition formDefinition,
                                                                     Set<FormDefinition> toUpdateDefinitions) throws Exception {
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        JSONObject fieldDefinitionJSONObjects = formDefinitionHandler.getFieldDefinitions();
        if (null == fieldDefinitionJSONObjects || JSONObject.NULL.equals(fieldDefinitionJSONObjects)) {
            return;
        }
        Set<Object> keys = fieldDefinitionJSONObjects.keySet();
        for (Object key : keys) {
            JSONObject fieldObject = fieldDefinitionJSONObjects.getJSONObject((String) key);
            String inputMode = fieldObject.optString("inputMode");
            String showType = fieldObject.optString("showType");
            String readStyle = fieldObject.optString("readStyle");
            // 标签组
            String tagEditable = fieldObject.optString("tagEditable");
            String editProgress = fieldObject.optString("editProgress");
			/*if ("126".equals(inputMode) && StringUtils.isNotBlank(tagEditable)) {
				if (StringUtils.equals(tagEditable, "1")) {
					fieldObject.put("showType", "2");
				} else if (StringUtils.equals(tagEditable, "2")) {
					fieldObject.put("showType", "1");
				}
				fieldObject.remove("tagEditable");
				toUpdateDefinitions.add(formDefinition);
			} else*/
            // 进度条
            if ("129".equals(inputMode) && StringUtils.isNotBlank(editProgress)) {
                if (StringUtils.equals(editProgress, "0")) {
                    fieldObject.put("showType", "2");
                } else if (StringUtils.equals(editProgress, "1")) {
                    fieldObject.put("showType", "1");
                }
                fieldObject.remove("editProgress");
                toUpdateDefinitions.add(formDefinition);
            } else
                // 1、只读、显示文本>不可编辑
                if ("3".equals(showType)) {
                    fieldObject.put("showType", "2");
                    toUpdateDefinitions.add(formDefinition);
                }
            // 2、不可编辑时样式
            if (StringUtils.isBlank(readStyle) && StringUtils.isNotBlank(inputMode)) {
                switch (inputMode) {
                    case "30":// 日期
                    case "16":// 树形下拉框
                    case "191":// 分组下拉框
                    case "2":// 富文本框
                    case "26":// 弹出框|搜索框
                    case "40":// 嵌入页面
                    case "41":// 职位
                    case "61":// 级联
                    case "126":// 标签组
                    case "128":// 开关按钮
                    case "129":// 进度条
                    {
                        fieldObject.put("readStyle", "2");// 显示文本
                        break;
                    }
                    case "17":// 单选框
                    case "18":// 复选框
                    case "127":// 颜色
                    {
                        fieldObject.put("readStyle", "3");// 只读
                        break;
                    }
                    case "31":// 数字控件
                    case "199":// 下拉框
                    case "1":// 单行文本框
                    case "20":// 多行文本框
                    case "7":// 流水号
                    case "43":// 组织选择框
                    {
                        if (StringUtils.equals("2", showType)) {
                            fieldObject.put("readStyle", "2");// 显示文本
                        } else if (StringUtils.equals("3", showType)) {
                            fieldObject.put("readStyle", "3");// 只读
                        } else {
                            fieldObject.put("readStyle", "2");// 显示文本
                        }
                        break;
                    }
                    default: {
                        fieldObject.put("readStyle", "2");// 显示文本
                        break;
                    }
                }
                toUpdateDefinitions.add(formDefinition);
            }
        }
        if (toUpdateDefinitions.contains(formDefinition)) {
            formDefinition.setDefinitionJson(formDefinitionHandler.toString());
        }
    }

    private void loggerFormError(FormDefinition formDefinition, String message) {
        logger.error(formDefinition.getName() + "(" + formDefinition.getVersion() + "):" + message);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionUpgradeService#upgrade2v6_2_5(java.lang.String)
     */
    @Override
    public List<String> upgrade2v6_2_5(String formUuid) {
        List<String> updatedFormInfos = Lists.newArrayList();
        List<FormDefinition> formDefinitions = null;
        if (StringUtils.isBlank(formUuid)) {
            formDefinitions = formDefinitionService.getAllFormDefintions();
        } else {
            formDefinitions = formDefinitionService.getFormDefinitionsById(dyFormFacade.getFormIdByFormUuid(formUuid));
        }
        Set<FormDefinition> toUpdateDefinitions = Sets.newHashSet();
        for (FormDefinition formDefinition : formDefinitions) {
            try {
                addFormDefinitionUpgrade2v6_2_5IfRequired(formDefinition, toUpdateDefinitions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (FormDefinition formDefinition : toUpdateDefinitions) {
            try {
                updatedFormInfos.add(formDefinition.getName() + ":" + formDefinition.getVersion());
                formDefinitionService.updateFormDefinitionAndFormTable(formDefinition,
                        Lists.<String>newArrayListWithExpectedSize(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updatedFormInfos;
    }

    /**
     * @param formDefinition
     * @param toUpdateDefinitions
     */
    private void addFormDefinitionUpgrade2v6_2_5IfRequired(FormDefinition formDefinition,
                                                           Set<FormDefinition> toUpdateDefinitions) throws Exception {
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        // 字段定义升级
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            for (String fieldName : fieldNames) {
                JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
                String inputMode = formDefinitionHandler.getPropertyValueOfInputMode(fieldName);
                // 列表附件
                if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode)) {
                    if (fileListUpgrade2v6_2_5(jsonObject)) {
                        toUpdateDefinitions.add(formDefinition);
                    }
                }
            }
        }
        if (toUpdateDefinitions.contains(formDefinition)) {
            formDefinition.setDefinitionJson(formDefinitionHandler.toString());
        }
    }

    /**
     * @param jsonObject
     * @return
     */
    private boolean fileListUpgrade2v6_2_5(JSONObject jsonObject) throws JSONException {
        boolean hasChange = false;
        if (jsonObject.has("secDevBtnIdStr")) {
            String secDevBtnIdStr = jsonObject.getString("secDevBtnIdStr");
            if (StringUtils.isNotBlank(secDevBtnIdStr)) {
                List<String> secDevBtnIdStrs = Lists.newArrayList(StringUtils.split(secDevBtnIdStr, ";"));
                // 添加全部下载
                if (!secDevBtnIdStrs.contains("ac764fe8-3cab-4100-b128-9f514be135c0")) {
                    secDevBtnIdStrs.add("ac764fe8-3cab-4100-b128-9f514be135c0");
                    jsonObject.put("secDevBtnIdStr", StringUtils.join(secDevBtnIdStrs, ";"));
                    hasChange = true;
                }
            }
        }
        return hasChange;
    }

}
