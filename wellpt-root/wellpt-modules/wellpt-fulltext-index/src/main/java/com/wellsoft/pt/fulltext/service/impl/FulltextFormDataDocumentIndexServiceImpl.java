/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.facade.support.DyformTitleUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.dto.FieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.fulltext.dto.FulltextModelDto;
import com.wellsoft.pt.fulltext.facade.service.FulltextModelFacadeService;
import com.wellsoft.pt.fulltext.index.FormDataDocumentIndex;
import com.wellsoft.pt.fulltext.index.FulltextAttachmentIndex;
import com.wellsoft.pt.fulltext.service.FulltextFormDataDocumentIndexService;
import com.wellsoft.pt.fulltext.support.Attachment;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.fulltext.support.ModelConditionConfig;
import com.wellsoft.pt.fulltext.utils.AttachmentUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
@Service
public class FulltextFormDataDocumentIndexServiceImpl extends AbstractDocumentIndexServiceImpl<FormDataDocumentIndex>
        implements FulltextFormDataDocumentIndexService {

    @Autowired
    private FulltextModelFacadeService fulltextModelFacadeService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private MongoFileService mongoFileService;

    @Override
    public String index(String formUuid, String dataUuid, Object dyformData, FulltextSetting fulltextSetting) {
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        String dataModeUuid = getDataModeUuid(dyFormFormDefinition);
        Set<String> categoryCodes = getCategoryCodes(dataModeUuid, (DyFormData) dyformData);
        if (CollectionUtils.isEmpty(categoryCodes)) {
            return null;
        }

        // 取最新数据
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        if (dyFormData == null) {
            dyFormData = (DyFormData) dyformData;
        }
        FormDataDocumentIndex index = buildIndex(formUuid, dataUuid, dyFormData, dyFormFormDefinition, fulltextSetting);
        index.setDataModeUuid(dataModeUuid);
        // index.setCategoryCodes(Sets.newHashSet("form_data"));
        index.setCategoryCodes(categoryCodes);
        fulltextDocumentIndexService.saveIndex(index);

        // 附件索引
        // indexAttachement(index, fulltextSetting);
        return index.getUuid();
    }

    private void indexAttachement(FormDataDocumentIndex index, FulltextSetting fulltextSetting) {
        String fileInfos = null;// index.getFileInfos();
        if (StringUtils.isBlank(fileInfos) || StringUtils.equals(fileInfos, "[]")) {
            return;
        }

        Collection<LogicFileInfo> logicFileInfos = JsonUtils.toCollection(fileInfos, LogicFileInfo.class, true);
        if (CollectionUtils.isEmpty(logicFileInfos)) {
            return;
        }

        if (BooleanUtils.isTrue(fulltextSetting.getIndexAttachment())) {
            logicFileInfos.forEach(fileInfo -> {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileInfo.getFileID());
                if (mongoFileEntity == null) {
                    return;
                }
                FulltextAttachmentIndex attachmentIndex = new FulltextAttachmentIndex();
                BeanUtils.copyProperties(index, attachmentIndex);
                attachmentIndex.setUuid(attachmentIndex.getUuid() + "_" + fileInfo.getFileID());
                attachmentIndex.setTitle(fileInfo.getFileName());
                attachmentIndex.setFileNames(fileInfo.getFileName());
                //attachmentIndex.setFileInfos(JsonUtils.object2Json(Lists.newArrayList(fileInfo)));
                attachmentIndex.setContent(AttachmentUtils.getFileContent(mongoFileEntity, fileInfo.getContentType()));
                fulltextDocumentIndexService.saveIndex(attachmentIndex);
            });
        } else {
            FulltextAttachmentIndex attachmentIndex = new FulltextAttachmentIndex();
            BeanUtils.copyProperties(index, attachmentIndex);
            String fileNames = logicFileInfos.stream()
                    .map(fileInfo -> fileInfo.getFileName()).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            attachmentIndex.setTitle(fileNames);
            attachmentIndex.setFileNames(fileNames);
            // attachmentIndex.setFileInfos(JsonUtils.object2Json(Collections.emptyList()));
            attachmentIndex.setContent(Base64.getEncoder().encodeToString(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8)));
            fulltextDocumentIndexService.saveIndex(attachmentIndex);
        }
    }

    private String getDataModeUuid(DyFormFormDefinition dyFormFormDefinition) {
        FormDefinition formDefinition = (FormDefinition) dyFormFormDefinition;
        String definitionVjson = formDefinition.getDefinitionVjson();
        if (StringUtils.isBlank(definitionVjson)) {
            return fulltextModelFacadeService.getDataModeUuidByDataModelId(formDefinition.getId());
        }
        JSONObject jsonObject = new JSONObject(definitionVjson);
        if (!jsonObject.has("dataModelUuid")) {
            return fulltextModelFacadeService.getDataModeUuidByDataModelId(formDefinition.getId());
        }
        return jsonObject.getString("dataModelUuid");
    }


    private Set<String> getCategoryCodes(String dataModelUuid, DyFormData dyFormData) {
        if (StringUtils.isBlank(dataModelUuid)) {
            return Collections.emptySet();
        }

        List<Long> parentCategoryUuids = Lists.newArrayList();
        List<FulltextModelDto> fulltextModelDtos = fulltextModelFacadeService.listByDataModelUuid(Long.valueOf(dataModelUuid));
        fulltextModelDtos.forEach(modelDto -> {
            if (evaluate(dyFormData, modelDto.getMatchJson())) {
                parentCategoryUuids.add(modelDto.getCategoryUuid());
            }
        });

        List<Long> categoryUuids = fulltextModelFacadeService.getAllCategoryUuidsByParentCategoryUuids(parentCategoryUuids);
        return categoryUuids.stream().map(categoryUuid -> categoryUuid.toString()).collect(Collectors.toSet());
    }

    private boolean evaluate(DyFormData dyFormData, String matchJson) {
        if (StringUtils.isBlank(matchJson)) {
            return true;
        }

        ModelConditionConfig modelConditionConfig = JsonUtils.json2Object(matchJson, ModelConditionConfig.class);
        List<ModelConditionConfig.FormFieldCondition> conditions = modelConditionConfig.getConditions();
        if (CollectionUtils.isEmpty(conditions)) {
            return true;
        }

        boolean evalateResult = false;
        boolean matchAll = ModelConditionConfig.MATCH_ALL.equals(modelConditionConfig.getMatch());
        if (matchAll) {
            evalateResult = true;
        }
        for (ModelConditionConfig.FormFieldCondition condition : conditions) {
            boolean result = evalateFormFieldCondition(condition, dyFormData);
            if (matchAll) {
                if (!result) {
                    evalateResult = false;
                    break;
                }
            } else {
                if (result) {
                    evalateResult = true;
                    break;
                }
            }
        }

        return evalateResult;
    }

    /**
     * @param condition
     * @param dyFormData
     * @return
     */
    private boolean evalateFormFieldCondition(ModelConditionConfig.FormFieldCondition condition, DyFormData dyFormData) {
        String code = condition.getCode();
        String operator = condition.getOperator();
        String value = condition.getValue();

        Map<String, Object> properties = Maps.newHashMap();
        if (dyFormData.getFormDataOfMainform() != null) {
            properties.putAll(dyFormData.getFormDataOfMainform());
        }
        List<String> fieldNames = dyFormData.doGetFieldNames();
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            fieldNames.forEach(fieldName -> {
                if (!properties.containsKey(fieldName)) {
                    properties.put(fieldName, null);
                }
            });
        }
        if (!properties.containsKey(code) && properties.containsKey(StringUtils.lowerCase(code))) {
            code = StringUtils.lowerCase(code);
        }

        StringBuilder script = new StringBuilder("return ");
        switch (operator) {
            case "true":
                String trueValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(Boolean.TRUE.equals(trueValue) || StringUtils.isNotBlank(trueValue));
                break;
            case "false":
                String falseValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(Boolean.FALSE.equals(falseValue) || StringUtils.isBlank(falseValue));
                break;
            case "in":
                script.append(StringUtils.isNotBlank(value) && StringUtils.contains(value, Objects.toString(properties.get(code), StringUtils.EMPTY)));
                break;
            case "not in":
                script.append(StringUtils.isNotBlank(value) && !StringUtils.contains(value, Objects.toString(properties.get(code), StringUtils.EMPTY)));
                break;
            case "contain":
                String containValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(containValue) && StringUtils.contains(containValue, value));
                break;
            case "not contain":
                String notContainValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(notContainValue) && !StringUtils.contains(notContainValue, value));
                break;
            default:
                if (NumberUtils.isNumber(value)) {
                    if (NumberUtils.isNumber(Objects.toString(properties.get(code), StringUtils.EMPTY))) {
                        properties.put(code, Double.valueOf(properties.get(code).toString()));
                    }
                    if (StringUtils.equals("==", operator) &&
                            NumberUtils.isNumber(Objects.toString(properties.get(code), StringUtils.EMPTY))) {
                        script.append("Double.valueOf(" + value + ").equals(" + code + ")");
                    } else {
                        script.append(code).append(" ").append(operator).append(" ").append(value);
                    }
                } else {
                    if (StringUtils.equals("==", operator)) {
                        script.append("(\"" + value + "\").equals(" + code + ")");
                    } else if ((StringUtils.startsWith(value, "\"") && StringUtils.endsWith(value, "\""))) {
                        script.append(code).append(" ").append(operator).append(" ").append(value);
                    } else {
                        script.append(code).append(" ").append(operator).append(" \"").append(value).append("\"");
                    }
                }
                break;
        }
        Object result = GroovyUtils.run(script.toString(), properties);
        return Boolean.TRUE.equals(result);
    }

    private FormDataDocumentIndex buildIndex(String formUuid, String dataUuid, DyFormData dyFormData, DyFormFormDefinition dyFormFormDefinition, FulltextSetting fulltextSetting) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String url = String.format("/dyform-viewer/data?formUuid=%s&dataUuid=%s", formUuid, dataUuid);
        List<LogicFileInfo> fileInfos = getFileInfos(dyFormData, dyFormFormDefinition);
        FormDataDocumentIndex index = new FormDataDocumentIndex();
        index.setUuid(dataUuid);
        index.setTitle(getTitle(dyFormData, dyFormFormDefinition));
        index.setContent(getContent(dyFormData, dyFormFormDefinition, fulltextSetting));
        index.setRemark(getRemark(dyFormData, dyFormFormDefinition, fulltextSetting));
        // index.setFileInfos(JsonUtils.object2Json(fileInfos));
        index.setAttachments(getAttachments(fileInfos, fulltextSetting));
        index.setFormUuid(formUuid);
        index.setDataUuid(dataUuid);
        index.setUrl(url);
        index.setFileNames(index.getAttachments().stream().map(attachment -> Objects.toString(attachment.getFileName()))
                .collect(Collectors.joining(Separator.SEMICOLON.getValue())));
        index.setCreator(getCreator(dyFormData));
        index.setCreatorId(getCreatorId(dyFormData));
        index.setCreateTime(getCreateTime(dyFormData));
        index.setModifier(userDetails.getUserName());
        index.setModifyTime(getModifyTime(dyFormData));
        index.setReaders(Sets.newHashSet("all"));
        index.setSystem(fulltextSetting.getSystem());
        index.setIndexOrder(30);
        return index;
    }

    private String getTitle(DyFormData dyFormData, DyFormFormDefinition dyFormFormDefinition) {
        Map<String, Object> mainformData = dyFormData.getFormDataOfMainform();
        String creator = null;
        if (MapUtils.isNotEmpty(mainformData)) {
            creator = (String) mainformData.get(EnumSystemField.creator.getName());
        }
        if (StringUtils.isBlank(creator)) {
            creator = SpringSecurityUtils.getCurrentUserId();
        }
        return DyformTitleUtils.generateDyformTitle(creator, dyFormFormDefinition, dyFormData);
    }

    /**
     * @param dyFormData
     * @param dyFormFormDefinition
     * @param fulltextSetting
     * @return
     */
    private String getContent(DyFormData dyFormData, DyFormFormDefinition dyFormFormDefinition, FulltextSetting fulltextSetting) {
        Integer fieldCount = getFieldCount(fulltextSetting);
        List<String> excludeFields = getExcludeFields(fulltextSetting);
        return getFieldIndexContent(fieldCount, excludeFields, dyFormData, dyFormFormDefinition, fulltextSetting);
    }

    private String getRemark(DyFormData dyFormData, DyFormFormDefinition dyFormFormDefinition, FulltextSetting fulltextSetting) {
        Integer indexFieldCount = getIndexFieldCount(fulltextSetting);
        List<String> excludeIndexFields = getExcludeIndexFields(fulltextSetting);
        return getFieldIndexContent(indexFieldCount, excludeIndexFields, dyFormData, dyFormFormDefinition, fulltextSetting);
    }

    private String getFieldIndexContent(Integer fieldCount, List<String> excludeFields, DyFormData dyFormData,
                                        DyFormFormDefinition dyFormFormDefinition, FulltextSetting fulltextSetting) {
        Iterator<DyformFieldDefinition> it = dyFormFormDefinition.doGetFieldDefintions().iterator();
        List<String> fieldValues = Lists.newArrayList();
        boolean indexDisplayValue = isIndexDisplayValue(fulltextSetting);
        boolean indexRealValue = isIndexRealValue(fulltextSetting);
        while (it.hasNext()) {
            FieldDefinition fieldDefinition = (FieldDefinition) it.next();
            if (isExcludeField(fieldDefinition, excludeFields)) {
                continue;
            }

            String displayValueString = StringUtils.EMPTY;
            String realValueString = StringUtils.EMPTY;
            String fieldName = fieldDefinition.getFieldName();

            // 显示值
            if (indexDisplayValue) {
                Object displayValue = dyFormData.getFieldDisplayValue(fieldName);
                displayValueString = StringUtils.trim(Objects.toString(displayValue, StringUtils.EMPTY));
            }
            // 真实值
            if (indexRealValue && !dyFormData.isFileField(fieldName)) {
                realValueString = Objects.toString(dyFormData.getFieldValue(fieldName), StringUtils.EMPTY);
                if (StringUtils.equals(displayValueString, realValueString) && StringUtils.isNotBlank(realValueString)) {
                    realValueString = StringUtils.EMPTY;
                }
            }

            // 拼接显示值、真实值
            if (StringUtils.isNotBlank(displayValueString) && StringUtils.isNotBlank(realValueString)) {
                if (StringUtils.equals(displayValueString, realValueString)) {
                    fieldValues.add(displayValueString);
                } else {
                    fieldValues.add(displayValueString + "(" + realValueString + ")");
                }
            } else if (StringUtils.isNotBlank(displayValueString)) {
                fieldValues.add(displayValueString);
            } else if (StringUtils.isNotBlank(realValueString)) {
                fieldValues.add(realValueString);
            }

            if (CollectionUtils.size(fieldValues) >= fieldCount) {
                break;
            }
        }

        return StringUtils.join(fieldValues, Separator.SEMICOLON.getValue());
    }

    /**
     * @param fieldDefinition
     * @param excludeFields
     * @return
     */
    private boolean isExcludeField(FieldDefinition fieldDefinition, List<String> excludeFields) {
        boolean exclude = false;
        JSONObject jsonObject = fieldDefinition.getFieldDefinitionJson();
        if (jsonObject.has("configuration")) {
            JSONObject configuration = jsonObject.getJSONObject("configuration");
            // 数字输入框
            if (configuration.has("dataType") && "number".equalsIgnoreCase(Objects.toString(configuration.get("dataType")))) {
                if (excludeFields.contains(configuration.getString("dataType"))) {
                    exclude = true;
                }
            } else if (configuration.has("dbDataType") && "2".equalsIgnoreCase(Objects.toString(configuration.get("dbDataType")))) {
                // 日期时间、日期时间范围
                if (excludeFields.contains("date") || excludeFields.contains("date-range")) {
                    exclude = true;
                }
            } else if (configuration.has("inputMode") && "6".equalsIgnoreCase(Objects.toString(configuration.get("inputMode"))) && configuration.has("type")) {
                // 文件上传、图片
                if ("picture".equalsIgnoreCase(Objects.toString(configuration.get("type"))) && excludeFields.contains("picture")) {
                    exclude = true;
                } else if (excludeFields.contains("file")) {
                    exclude = true;
                }
            } else if (configuration.has("contentFormat")) {
                // 手机号码、电话、身份证、邮箱、密码
                String contentFormat = Objects.toString(configuration.get("contentFormat"));
                if (excludeFields.contains(contentFormat)) {
                    exclude = true;
                }
            } else {
                // 大文本字段
                if (excludeFields.contains("clob") &&
                        StringUtils.equals(DyFormConfig.DbDataType._clob, fieldDefinition.getInputMode())
                        || (configuration.has("dbDataType") && "16".equals(Objects.toString(configuration.get("dbDataType"))))) {
                    exclude = true;
                }
            }
        }
        return exclude;
    }

    private List<LogicFileInfo> getFileInfos(DyFormData dyFormData, DyFormFormDefinition dyFormFormDefinition) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isEmpty(formDatas)) {
            return fileInfos;
        }

        // 主表数据
        fileInfos.addAll(extractFileInfos(dyFormData));

        // 从表数据
        formDatas.forEach((formUuid, dataList) -> {
            if (!StringUtils.equals(formUuid, dyFormData.getFormUuid())) {
                dataList.forEach(data -> {
                    DyFormData subformData = dyFormData.getDyFormData(formUuid, Objects.toString(data.get("uuid"), StringUtils.EMPTY));
                    fileInfos.addAll(extractFileInfos(subformData));
                });
            }
        });
        return fileInfos;
    }

    private List<LogicFileInfo> extractFileInfos(DyFormData dyFormData) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        List<String> fieldNames = dyFormData.doGetFieldNames();
        List<String> fileIds = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            if (dyFormData.isFileField(fieldName)) {
                Object fieldValue = dyFormData.getFieldValue(fieldName);
                if (fieldValue instanceof Collection) {
                    ((Collection<?>) fieldValue).forEach(file -> {
                        if (file instanceof Map) {
                            String fileId = Objects.toString(((Map<String, Object>) file).get("fileID"), StringUtils.EMPTY);
                            if (StringUtils.isNotBlank(fileId)) {
                                fileIds.add(fileId);
                            }
                        } else {
                            fileInfos.add((LogicFileInfo) file);
                        }
                    });
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fileIds)) {
            fileInfos.addAll(mongoFileService.getLogicFileInfo(fileIds));
        }
        return fileInfos;
    }

    private List<Attachment> getAttachments(List<LogicFileInfo> fileInfos, FulltextSetting fulltextSetting) {
        if (CollectionUtils.isEmpty(fileInfos)) {
            return Collections.emptyList();
        }

        boolean indexAttachment = BooleanUtils.isTrue(fulltextSetting.getIndexAttachment());
        return AttachmentUtils.logicFileInfos2Attachments(fileInfos, indexAttachment);
    }

    private String getCreator(DyFormData dyFormData) {
        Map<String, Object> mainformData = dyFormData.getFormDataOfMainform();
        String creator = null;
        if (MapUtils.isNotEmpty(mainformData)) {
            creator = (String) mainformData.get(EnumSystemField.creator.getName());
        }
        if (StringUtils.isBlank(creator)) {
            creator = SpringSecurityUtils.getCurrentUserId();
        }
        return orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(creator)).get(creator);
    }

    private String getCreatorId(DyFormData dyFormData) {
        Map<String, Object> mainformData = dyFormData.getFormDataOfMainform();
        String creator = null;
        if (MapUtils.isNotEmpty(mainformData)) {
            creator = (String) mainformData.get(EnumSystemField.creator.getName());
        }
        if (StringUtils.isBlank(creator)) {
            creator = SpringSecurityUtils.getCurrentUserId();
        }
        return creator;
    }

    private Date getCreateTime(DyFormData dyFormData) {
        Map<String, Object> mainformData = dyFormData.getFormDataOfMainform();
        Object createTime = null;
        if (MapUtils.isNotEmpty(mainformData)) {
            createTime = mainformData.get(EnumSystemField.create_time.getName());
        }
        if (createTime == null) {
            createTime = Calendar.getInstance().getTime();
        }
        if (createTime instanceof String) {
            try {
                createTime = DateUtils.parse((String) createTime);
            } catch (Exception e) {
                createTime = Calendar.getInstance().getTime();
            }
        }
        return (Date) createTime;
    }


    private Date getModifyTime(DyFormData dyFormData) {
        Map<String, Object> mainformData = dyFormData.getFormDataOfMainform();
        Object modifyTime = null;
        if (MapUtils.isNotEmpty(mainformData)) {
            modifyTime = mainformData.get(EnumSystemField.create_time.getName());
        }
        if (modifyTime == null) {
            modifyTime = Calendar.getInstance().getTime();
        }
        if (modifyTime instanceof String) {
            try {
                modifyTime = DateUtils.parse((String) modifyTime);
            } catch (Exception e) {
                modifyTime = Calendar.getInstance().getTime();
            }
        }
        return (Date) modifyTime;
    }

    private List<String> getExcludeFields(FulltextSetting fulltextSetting) {
        List<String> excludeFields = fulltextSetting.getExcludeFields();
        return excludeFields != null ? excludeFields : Collections.emptyList();
    }

    private Integer getFieldCount(FulltextSetting fulltextSetting) {
        Integer fieldCount = fulltextSetting.getFieldCount();
        return fieldCount != null ? fieldCount : 6;
    }

    private Integer getIndexFieldCount(FulltextSetting fulltextSetting) {
        boolean indexAllField = BooleanUtils.isTrue(fulltextSetting.getIndexAllField());
        if (indexAllField) {
            return Integer.MAX_VALUE;
        }

        Integer fieldCount = fulltextSetting.getIndexFieldCount();
        return fieldCount != null ? fieldCount : 6;
    }

    private List<String> getExcludeIndexFields(FulltextSetting fulltextSetting) {
        List<String> excludeFields = fulltextSetting.getExcludeIndexFields();
        return excludeFields != null ? excludeFields : Collections.emptyList();
    }

    private boolean isIndexDisplayValue(FulltextSetting fulltextSetting) {
        List<String> enumIndexModes = fulltextSetting.getEnumIndexModes();
        return CollectionUtils.isNotEmpty(enumIndexModes) && enumIndexModes.contains("label");
    }

    private boolean isIndexRealValue(FulltextSetting fulltextSetting) {
        List<String> enumIndexModes = fulltextSetting.getEnumIndexModes();
        return CollectionUtils.isNotEmpty(enumIndexModes) && enumIndexModes.contains("value");
    }

}
