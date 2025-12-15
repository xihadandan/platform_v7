package com.wellsoft.pt.bot.support;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.dto.BotRuleObjMappingDto;
import com.wellsoft.pt.bot.dto.BotRuleObjRelaMappingDto;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.entity.BotRuleObjMappginIgnoreEntity;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.implement.data.exceptions.FormDataValidateException;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 表单的单据转换
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
public class DyFormBoter extends AbstractBoter {


    public DyFormBoter(BotParam botParam) {
        super(botParam);
    }


    @Override
    public void prepare() {
        BotRuleConfDto conf = botRuleConfService.getDetailById(this.botParam.getRuleId());
        if (conf == null) {
            throw new BotException("未查询到规则ID=[" + this.botParam.getRuleId() + "]的单据转换规则");
        }
        Map<String, FormDefinition> formDefinitionMap = Maps.newHashMap();

        DyFormBoterPrepareData prepareData = new DyFormBoterPrepareData();
        prepareData.isPersist = conf.getIsPersist();
        prepareData.targetObjId = conf.getTargetObjId();
        prepareData.afterBotScript = conf.getScriptAfterTrans();
        prepareData.beforeBotScript = conf.getScriptBeforeTrans();

        String[] sourceObjIds = StringUtils.isNotBlank(conf.getSourceObjId()) ? conf.getSourceObjId().split(";") : null;
        FormDefinition targetFormDef = findDyFormFormDefinitionByFormUuid(conf.getTargetObjId(), formDefinitionMap);//目标单据表单定义
        prepareData.getFormDefinitionMap().put(conf.getTargetObjId(), targetFormDef);
        Map<String, String> formId2Uuid = Maps.newHashMap();//表单id->uuid
        Map<String, String> formUuid2Id = Maps.newHashMap();//表单uuid->id
        boolean isForm2Form = BotRuleConfEntity.TRANS_TYPE_FORM_2_FORM.equals(
                conf.getTransferType());
        if (isForm2Form) {
            //来源单据表单定义
            if (CollectionUtils.isNotEmpty(conf.getObjMappingDtos())) {
                for (BotRuleObjMappingDto objMappingDto : conf.getObjMappingDtos()) {
                    if (!objMappingDto.getIsReverseWrite()) {
                        FormDefinition def = findDyFormFormDefinitionByFormUuid(objMappingDto.getSourceObjId(), formDefinitionMap);
                        prepareData.getFormDefinitionMap().put(objMappingDto.getSourceObjId(), def);
                        formId2Uuid.put(def.getId(), def.getUuid());//form_id -> form_uuid
                        formUuid2Id.put(def.getUuid(), def.getId());
                    }
                }
            }

            if (sourceObjIds != null) {
                for (String s : sourceObjIds) {
                    if (!formUuid2Id.containsKey(s)) {
                        FormDefinition def = findDyFormFormDefinitionByFormUuid(s, formDefinitionMap);
                        prepareData.getFormDefinitionMap().put(s, def);
                        formId2Uuid.put(def.getId(), def.getUuid());//form_id -> form_uuid
                        formUuid2Id.put(def.getUuid(), def.getId());
                    }
                }
            }

            for (BotParam.BotFromParam from : this.botParam.getFroms()) {
                //支持传递参数为表单ID或者表单UUID
                String formUuid = formId2Uuid.containsKey(from.getFromObjId()) ? formId2Uuid.get(
                        from.getFromObjId()) : (formUuid2Id.containsKey(
                        from.getFromObjId()) ? from.getFromObjId() : null);
                Object fromObjectData = from.getFromObjData();
                if (StringUtils.isNotBlank(formUuid)) {
                    DyFormData dyFormData = null;
                    if (fromObjectData instanceof DyFormData) {
                        dyFormData = (DyFormData) fromObjectData;
                    } else {
                        dyFormData = dyFormFacade.getDyFormData(
                                formUuid,
                                from.getFromUuid(), false);
                    }
                    prepareData.getSourceDyFormDataMap().put(formUuid,
                            dyFormData);//表单定义uuid -> dyformData
                    prepareData.getFormDataMap().put(formUuid2Id.get(formUuid),
                            dyFormData.getFormDataOfMainform());//表单定义id -> Map<>
                    prepareData.getFormDataMap().put(formUuid,
                            dyFormData.getFormDataOfMainform());//表单定义uuid -> Map<>
                }
            }


            //如果有传递目标单据的UUID，则单据转换为保存更新到指定目标单据
            if (StringUtils.isNotBlank(this.botParam.getTargetUuid())) {
                DyFormData dyFormData = dyFormFacade.getDyFormData(targetFormDef.getUuid(),
                        this.botParam.getTargetUuid(), false);
                if (dyFormData.getFormDatas() == null) {
                    throw new BotException(
                            "单据转换异常：目标单据=[" + targetFormDef.getId() + "]，不存在uuid=[" + this.botParam.getTargetUuid() + "]的目标单据记录！");
                }
                prepareData.getTargetFormDataMap().put(targetFormDef.getId(), dyFormData.getFormDataOfMainform());
                prepareData.getTargetFormDataMap().put(targetFormDef.getUuid(), dyFormData.getFormDataOfMainform());
            }
        }

        Set<BotFieldMapping> mappings = Sets.newLinkedHashSet();
        Set<String> toFields = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(conf.getObjMappingDtos())) {
            //构建字段映射转换数据
            for (BotRuleObjMappingDto mappingDto : conf.getObjMappingDtos()) {
                BotFieldMapping mapping = BotFieldMapping.build().
                        fromObjId(mappingDto.getSourceObjId()).
                        toObjId(targetFormDef.getUuid()).
                        fromField(mappingDto.getSourceObjField()).
                        toField(mappingDto.getTargetObjField()).
                        reverse(mappingDto.getIsReverseWrite());
                mappings.add(mapping);
                if (!mappingDto.getIsReverseWrite()) {
                    toFields.add(mapping.getToObjId() + mapping.getToField());
                }
                //设置值计算
                if (isForm2Form) {
                    Map<String, Object> dataMap = Maps.newHashMap();
                    dataMap.put("formData", prepareData.getFormDataMap());
                    dataMap.put("targetFormData", prepareData.getTargetFormDataMap());
                    if (MapUtils.isNotEmpty(botParam.getJsonBody())) {//单据转换单据也兼容附带json报文
                        dataMap.put("jsonBody", botParam.getJsonBody());
                    }
                    Map<String, Object> sourceDataMap = prepareData.getFormDataMap().get(
                            mappingDto.getSourceObjId());
                    mapping.fieldValue(mappingDto.getRenderValueType(),
                            dataMap,
                            sourceDataMap != null ? sourceDataMap.get(
                                    mappingDto.getSourceObjField()) : null, null,
                            mappingDto.getRenderValueExpression());
                } else {
                    Map<String, Object> dataMap = Maps.newHashMap();
                    if (MapUtils.isNotEmpty(botParam.getJsonBody())) {
                        dataMap.put("jsonBody", botParam.getJsonBody());
                    }
                    mapping.fieldValue(mappingDto.getRenderValueType(), dataMap,
                            null, null,
                            mappingDto.getRenderValueExpression());
                }

            }
        }

        if (BooleanUtils.isTrue(conf.getAutoMapSameColumn()) && sourceObjIds != null) {
            //自动映射的
            List<BotRuleObjMappginIgnoreEntity> ignoreEntities = botRuleColIgnoreService.listByConfUuid(conf.getUuid());
            for (int i = (sourceObjIds.length - 1); i >= 0; i--) {
                List<BotFieldMapping> list = autoMapSameColumnName(prepareData.getFormDefinitionMap().get(sourceObjIds[i]), targetFormDef, ignoreEntities, prepareData);
                for (BotFieldMapping m : list) {
                    if (toFields.add(m.getToObjId() + m.getToField())) {
                        mappings.add(m);
                    }
                }
            }

        }
        prepareData.getFieldMappings().addAll(mappings);

        if (CollectionUtils.isNotEmpty(prepareData.getFieldMappings())) {
            //计算转换数据的值到目标单据
            List<Map<String, Object>> targetFormData = Lists.newArrayList();
            Map<String, Object> targetFiedDataMap = Maps.newHashMap();
            for (BotFieldMapping fieldMapping : prepareData.getFieldMappings()) {
                if (!fieldMapping.isReverse()) {
                    targetFiedDataMap.put(fieldMapping.getToField(),
                            fieldMapping.getFieldValue().value());
                }
            }
            targetFiedDataMap.put("uuid", this.botParam.getTargetUuid());//指定更新的目标单据uuid
            targetFormData.add(targetFiedDataMap);
            prepareData.getSaveTargetFormData().put(conf.getTargetObjId(), targetFormData);
            logger.info("单据转换规则=[{}]，单据转换->目标单据数据：", conf.getRuleName(),
                    new Gson().toJson(targetFiedDataMap));
        }

        if (conf.getRelaDto() != null && CollectionUtils.isNotEmpty(
                conf.getRelaDto().getRelaMappingDtos())) {
            //关系维护
            List<BotRuleObjRelaMappingDto> relaMappingDtos = conf.getRelaDto().getRelaMappingDtos();
            for (BotRuleObjRelaMappingDto relaDto : relaMappingDtos) {
                prepareData.getRelationFieldMappings().add(
                        BotFieldMapping.build().
                                fromObjId(relaDto.getSourceObjId()).
                                toObjId(conf.getRelaDto().getRelaObjId()).
                                fromField(relaDto.getSourceObjField()).
                                toField(relaDto.getRelaObjField()).
                                fieldValue(null,
                                        null,
                                        (prepareData.getFormDataMap().containsKey(
                                                relaDto.getSourceObjId()) ? prepareData.getFormDataMap().get(
                                                relaDto.getSourceObjId()).get(
                                                relaDto.getSourceObjField()) : null)
                                        , null, null)

                );
            }
        }

        this.boterPrepareData = prepareData;
    }

    /**
     * @param formUuid
     * @param formDefinitionMap
     * @return
     */
    private FormDefinition findDyFormFormDefinitionByFormUuid(String formUuid, Map<String, FormDefinition> formDefinitionMap) {
        if (formDefinitionMap == null) {
            return (FormDefinition) DyformCacheUtils.getDyformDefinitionByUuid(formUuid);
        }

        FormDefinition formDefinition = formDefinitionMap.get(formUuid);
        if (formDefinition != null) {
            return formDefinition;
        }
        formDefinition = (FormDefinition) DyformCacheUtils.getDyformDefinitionByUuid(formUuid);
        formDefinitionMap.put(formUuid, formDefinition);
        return formDefinition;
    }

    private List<BotFieldMapping> autoMapSameColumnName(FormDefinition source, FormDefinition target, List<BotRuleObjMappginIgnoreEntity> ignoreEntities, DyFormBoterPrepareData prepareData) {
        source.doGetFormDefinitionHandler();
        target.doGetFormDefinitionHandler();
        List<DyformFieldDefinition> targetFields = target.doGetFieldDefintions();
        List<DyformFieldDefinition> sourceFields = source.doGetFieldDefintions();
        Map<String, DyformFieldDefinition> sourceFieldMap = Maps.uniqueIndex(sourceFields, new Function<DyformFieldDefinition, String>() {
            @Nullable
            @Override
            public String apply(@Nullable DyformFieldDefinition input) {
                return input.getFieldName();
            }
        });
        Map<String, BotRuleObjMappginIgnoreEntity> ignores = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(ignoreEntities)) {
            ignores = Maps.uniqueIndex(ignoreEntities, new Function<BotRuleObjMappginIgnoreEntity, String>() {
                @Nullable
                @Override
                public String apply(@Nullable BotRuleObjMappginIgnoreEntity input) {
                    return input.getSourceObjId() + input.getSourceObjField() + input.getTargetObjId() + input.getTargetObjField();
                }
            });
        }
        List<BotFieldMapping> mappings = Lists.newArrayList();
        DyFormData sourceDyFormData = prepareData.getSourceDyFormDataMap().get(source.getUuid());
        for (DyformFieldDefinition field : targetFields) {
            if (sourceFieldMap.containsKey(field.getFieldName()) && !ignores.containsKey(
                    source.getUuid() + field.getFieldName() + target.getUuid() + field.getFieldName()
            )) {
                BotFieldMapping mapping = BotFieldMapping.build().
                        fromObjId(source.getUuid()).
                        toObjId(target.getUuid()).
                        fromField(field.getFieldName()).
                        toField(field.getFieldName()).
                        reverse(false);
                if (sourceDyFormData == null) {
                    throw new BotException(String.format("源单据[%s]的数据不存在！", source.getName()));
                }
                if (sourceDyFormData.isFileField(field.getFieldName())) {
                    List<String> files = sourceDyFormData.getValueOfFileIds(field.getFieldName(), sourceDyFormData.getDataUuid());
                    BotFieldFileMapValue fileCopyValue = new BotFieldFileMapValue(null, files, null, null);
                    mapping.setFieldValue(fileCopyValue);
                } else {
                    mapping.setFieldValue(new BasicBotFieldValue(sourceDyFormData.getFieldValue(field.getFieldName())));
                }
                mappings.add(mapping);
            }
        }
        return mappings;
    }


    @Override
    public BotResult startBot() {

        DyFormBoterPrepareData prepareData = (DyFormBoterPrepareData) this.boterPrepareData;

        if (boterPrepareData.isPersist) {
            // 校验表单数据
            validateFormData(prepareData.targetObjId, prepareData.getSaveTargetFormData());

            //保存目标单据
            String targetDataUuid = dyFormFacade.saveFormData(prepareData.targetObjId,
                    prepareData.getSaveTargetFormData(), null, null);
            DyFormData targetFormData = dyFormFacade.getDyFormData(
                    prepareData.targetObjId,
                    targetDataUuid, false);
            prepareData.getTargetFormDataMap().put(prepareData.targetObjId,
                    targetFormData.getFormDataOfMainform());
            prepareData.getTargetFormDataMap().put(
                    prepareData.getFormDefinitionMap().get(prepareData.targetObjId).getId(),
                    targetFormData.getFormDataOfMainform());
            //关联关系
            if (CollectionUtils.isNotEmpty(prepareData.getRelationFieldMappings())) {
                Map<String, Object> relationFormDataMap = Maps.newHashMap();
                Map<String, List<Map<String, Object>>> saveRelationFormData = Maps.newHashMap();
                String relaObjId = null;
                String condition = " 1=1 ";

                for (BotFieldMapping fieldMapping : prepareData.getRelationFieldMappings()) {
                    Map<String, Object> dataMap = Maps.newHashMap();
                    dataMap.put("formData", prepareData.getFormDataMap());
                    dataMap.put("targetFormData", prepareData.getTargetFormDataMap());
                    Map map = prepareData.getFormDataMap().get(fieldMapping.getFromObjId());
                    if (map == null) {
                        map = prepareData.getTargetFormDataMap().get(fieldMapping.getFromObjId());
                    }
                    //重新加载目标单据保存的表单数据用于计算值
                    fieldMapping.getFieldValue().reload(dataMap,
                            map.get(fieldMapping.getFromField()), null);
                    Object value = fieldMapping.getFieldValue().value();
                    relationFormDataMap.put(fieldMapping.getToField(),
                            value);
                    relaObjId = fieldMapping.getToObjId();
                    condition += " and " + fieldMapping.getToField() + "=" + "'" + value.toString() + "' ";
                }
                saveRelationFormData.put(relaObjId,
                        Lists.<Map<String, Object>>newArrayList(relationFormDataMap));
                FormDefinition relaFormDef = findDyFormFormDefinitionByFormUuid(relaObjId, null);//关系单据表单定义
                long cnt = dyFormFacade.queryTotalCountOfFormDataOfMainform(
                        relaFormDef.getTableName(),
                        condition);
                if (cnt == 0) {
                    // 校验表单数据
                    validateFormData(relaObjId, saveRelationFormData);
                    dyFormFacade.saveFormData(relaObjId,
                            saveRelationFormData, null, null);

                }
            }


            //反写
            Map<String, DyFormData> saveSourceFormData = Maps.newHashMap();
            for (BotFieldMapping fieldMapping : prepareData.getFieldMappings()) {
                if (fieldMapping.isReverse()) {
                    DyFormData sourceFormData = saveSourceFormData.containsKey(
                            fieldMapping.getFromObjId()) ? saveSourceFormData.get(
                            fieldMapping.getFromObjId()) : prepareData.getSourceDyFormDataMap().get(
                            fieldMapping.getFromObjId());
                    Map<String, Object> dataMap = Maps.newHashMap();
                    dataMap.put("formData", prepareData.getFormDataMap());
                    dataMap.put("targetFormData", prepareData.getTargetFormDataMap());
                    //重新加载目标单据保存的表单数据用于计算值
                    fieldMapping.getFieldValue().reload(dataMap,
                            sourceFormData.getFieldValue(fieldMapping.getFromField()),//源单据值
                            StringUtils.isNotBlank(
                                    fieldMapping.getToField()) ? targetFormData.getFieldValue(
                                    fieldMapping.getToField()) : null);

                    sourceFormData.setFieldValue(fieldMapping.getFromField(),
                            fieldMapping.getFieldValue().value());
                    if (!saveSourceFormData.containsKey(fieldMapping.getFromObjId())) {
                        saveSourceFormData.put(fieldMapping.getFromObjId(), sourceFormData);
                    }
                }
            }

            for (DyFormData src : saveSourceFormData.values()) {
                // 校验表单数据
                validateFormData(src);
                dyFormFacade.saveFormData(src);
            }


            return new BotResult(targetDataUuid, targetFormData.getFormDataOfMainform(), targetFormData);
        }

        return new BotResult(null, prepareData.getSaveTargetFormData(), null);
    }

    /**
     * 校验表单数据
     *
     * @param dyFormData
     */
    private void validateFormData(DyFormData dyFormData) {
        ValidateMsg validateMsg = dyFormData.validateFormDataWithDatabaseConstraints();
        if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
            validateMsg.setMsg("单据转换(" + this.botParam.getRuleId() + ")保存表单校验失败");
            throw new FormDataValidateException(validateMsg);
        }
    }

    /**
     * 校验表单数据
     *
     * @param formUuid
     * @param formData
     */
    private void validateFormData(String formUuid, Map<String, List<Map<String, Object>>> formData) {
        DyFormData dyFormData = dyFormFacade.createDyformData(formUuid);
        dyFormData.setFormDatas(formData, true);
        validateFormData(dyFormData);
    }


}
