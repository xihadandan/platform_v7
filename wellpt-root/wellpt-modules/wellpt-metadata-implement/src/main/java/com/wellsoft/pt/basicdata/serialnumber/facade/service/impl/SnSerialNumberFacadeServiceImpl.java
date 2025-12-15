/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberRecordDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberMaintainEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRecordEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRelationEntity;
import com.wellsoft.pt.basicdata.serialnumber.enums.ObjectTypeEnum;
import com.wellsoft.pt.basicdata.serialnumber.enums.PointerResetPeriodEnum;
import com.wellsoft.pt.basicdata.serialnumber.enums.PointerResetTypeEnum;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberDefinitionFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberDefinitionService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberMaintainService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRelationService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberInfo;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/21/22.1	zhulh		7/21/22		Create
 * </pre>
 * @date 7/21/22
 */
@Service
public class SnSerialNumberFacadeServiceImpl extends AbstractApiFacade implements SnSerialNumberFacadeService {

    @Autowired
    private SnSerialNumberDefinitionService snSerialNumberDefinitionService;

    @Autowired
    private SnSerialNumberDefinitionFacadeService snSerialNumberDefinitionFacadeService;

    @Autowired
    private SnSerialNumberMaintainService serialNumberMaintainService;

    @Autowired
    private SnSerialNumberRelationService snSerialNumberRelationService;

    @Autowired
    private SnSerialNumberRecordService snSerialNumberRecordService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * 生成流水号
     *
     * @param params
     * @return
     */
    @Override
    @Transactional
    public SerialNumberInfo generateSerialNumber(SerialNumberBuildParams params) {
        // 流水号定义
        String serialNumberDefId = params.getSerialNumberId();
        SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getById(serialNumberDefId);
        if (entity == null) {
            return null;
        }

        // 获取变量参数MAP
        Map<String, Object> renderParams = getVariableParamMap(entity, params);

        // 获取流水号维护
        SnSerialNumberMaintainEntity maintainEntity = getSerialNumberMaintain(entity, renderParams, params);

        // 生成流水号
        SerialNumberInfo serialNumberInfo = generateSerialNumberInfo(entity, maintainEntity, params, renderParams);
        if (serialNumberInfo == null) {
            return null;
        }

        // 流水号占用
        if (BooleanUtils.isTrue(params.getOccupied())) {
            // 保存或更新流水号维护，强制指针占用不更新
            saveOrUpdateSerialMaintain(serialNumberInfo, maintainEntity, params);

            // 保存流水号记录
            saveSerialNumberRecord(serialNumberInfo, maintainEntity, params, renderParams);
        }
        return serialNumberInfo;
    }

    /**
     * 生成指定指针的流水号
     *
     * @param pointer
     * @param params
     * @return
     */
    @Override
    public ResultMessage generateSerialNumberWithPointer(Long pointer, SerialNumberBuildParams params) {
        ResultMessage resultMessage = new ResultMessage();
        // 流水号定义
        String serialNumberDefId = params.getSerialNumberId();
        SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getById(serialNumberDefId);

        // 获取变量参数MAP
        Map<String, Object> renderParams = getVariableParamMap(entity, params);

        // 获取流水号维护
        SnSerialNumberMaintainEntity maintainEntity = getSerialNumberMaintain(entity, renderParams, params);

        // 验证指定的指针是否有效
        if (!isValidPointer(pointer, entity, maintainEntity)) {
            resultMessage.setSuccess(false);
            resultMessage.getMsg().setLength(0);
            resultMessage.addMessage("指定的指针无效！");
            return resultMessage;
        }
        // 验证流水号记录是否存在该指针
        if (StringUtils.isNotBlank(maintainEntity.getUuid()) && snSerialNumberRecordService.isExistsPointer(pointer, maintainEntity.getUuid())) {
            resultMessage.setSuccess(false);
            resultMessage.getMsg().setLength(0);
            resultMessage.addMessage("指定的指针已存在流水号记录！");
            return resultMessage;
        }

        // 生成流水号
        SnSerialNumberMaintainEntity customMaintain = new SnSerialNumberMaintainEntity();
        BeanUtils.copyProperties(maintainEntity, customMaintain, IdEntity.BASE_FIELDS);
        customMaintain.setPointer(pointer.toString());
        SerialNumberInfo serialNumberInfo = generateSerialNumberInfo(entity, customMaintain, renderParams);
        if (serialNumberInfo == null) {
            resultMessage.setSuccess(false);
            resultMessage.getMsg().setLength(0);
            resultMessage.addMessage("生成流水号报错");
            return resultMessage;
        }

        resultMessage.setData(serialNumberInfo);
        return resultMessage;
    }

    /**
     * 验证指针的有效性
     *
     * @param pointer
     * @param entity
     * @param maintainEntity
     * @return
     */
    private boolean isValidPointer(Long pointer, SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity) {
        if (pointer == null || pointer <= 0) {
            return false;
        }
        Integer incremental = entity.getIncremental();
        Long currentPointer = Long.valueOf(maintainEntity.getPointer());
        if (pointer.equals(currentPointer)) {
            return false;
        } else {
            return Math.abs(pointer - currentPointer) % incremental == 0;
        }
    }

    /**
     * 根据流水号定义ID，获取可补的流水号记录(只能被当前重围周期内的数据)
     *
     * @param params
     * @return
     */
    @Override
    public List<SnSerialNumberRecordDto> listAvailableSerialNumber(SerialNumberBuildParams params) {
        List<SnSerialNumberRecordDto> recordDtos = Lists.newArrayList();
        // 流水号定义
        String serialNumberDefId = params.getSerialNumberId();
        SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getById(serialNumberDefId);

        // 获取变量参数MAP
        Map<String, Object> renderParams = getVariableParamMap(entity, params);

        // 获取流水号维护
        SnSerialNumberMaintainEntity maintainEntity = getSerialNumberMaintain(entity, renderParams, params);

        // 获取当前重置周期内可补的流水号
        if (StringUtils.isNotBlank(maintainEntity.getUuid())) {
            List<SnSerialNumberRecordEntity> entities = snSerialNumberRecordService.listAvailableSerialNumberRecord(serialNumberDefId, maintainEntity.getUuid());
            recordDtos.addAll(BeanUtils.copyCollection(entities, SnSerialNumberRecordDto.class));

            // 添加跳号的可补记录
            recordDtos.addAll(getRecordOfSkipPointers(entity, maintainEntity, renderParams));
        }

        // 生成最新可补的流水号
        SerialNumberInfo serialNumberInfo = generateSerialNumberInfo(entity, maintainEntity, renderParams);
        if (serialNumberInfo != null) {
            SnSerialNumberRecordDto dto = new SnSerialNumberRecordDto();
            dto.setPrefix(serialNumberInfo.getPrefix());
            dto.setPointer(serialNumberInfo.getPointer());
            dto.setSuffix(serialNumberInfo.getSuffix());
            dto.setSerialNo(serialNumberInfo.getSerialNo());
            dto.setMaintainUuid(serialNumberInfo.getMaintainUuid());
            recordDtos.add(dto);
        }

        Collections.sort(recordDtos);

        return recordDtos;
    }

    private List<SnSerialNumberRecordDto> getRecordOfSkipPointers(SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity, Map<String, Object> renderParams) {
        List<SnSerialNumberRecordDto> skipRecordDtos = Lists.newArrayList();
        String maintainUuid = maintainEntity.getUuid();
        if (StringUtils.isBlank(maintainUuid)) {
            return skipRecordDtos;
        }
        // 初始值
        Integer initialValue = Integer.valueOf(maintainEntity.getInitialValue());
        // 增量
        Integer increment = entity.getIncremental();
        // 当前指针
        Long currentPointer = Long.valueOf(maintainEntity.getPointer());
        // 跳号指针
        List<Long> skipPointers = snSerialNumberRecordService.listSkipPointers(initialValue, increment, currentPointer, maintainUuid);

        // 生成跳号指针当前的流水号记录
        skipPointers.forEach(pointer -> {
            SnSerialNumberMaintainEntity customMaintainEntity = new SnSerialNumberMaintainEntity();
            BeanUtils.copyProperties(maintainEntity, customMaintainEntity, IdEntity.BASE_FIELDS);
            customMaintainEntity.setPointer(pointer.toString());
            SerialNumberInfo serialNumberInfo = generateSerialNumberInfo(entity, customMaintainEntity, renderParams);
            SnSerialNumberRecordDto dto = new SnSerialNumberRecordDto();
            // 标记为跳号记录
            dto.setSkip(true);
            if (serialNumberInfo != null) {
                dto.setPrefix(serialNumberInfo.getPrefix());
                dto.setPointer(serialNumberInfo.getPointer());
                dto.setSuffix(serialNumberInfo.getSuffix());
                dto.setSerialNo(serialNumberInfo.getSerialNo());
                dto.setMaintainUuid(serialNumberInfo.getMaintainUuid());
            } else {
                dto.setPointer(pointer);
                dto.setSerialNo(pointer.toString());
                dto.setMaintainUuid(serialNumberInfo.getMaintainUuid());
            }
            skipRecordDtos.add(dto);
        });
        return skipRecordDtos;
    }

    /**
     * 检测流水号是否被占用
     *
     * @param pointer
     * @param params
     * @return
     */
    @Override
    public boolean checkIsOccupied(Long pointer, SerialNumberBuildParams params) {
        // 流水号定义
        String serialNumberDefId = params.getSerialNumberId();
        String serialNo = params.getSnValue();
        String maintainUuid = params.getUuid();
        SnSerialNumberMaintainEntity maintainEntity = null;

        boolean missingSerialNo = true;
        if (StringUtils.isBlank(maintainUuid)) {
            SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getById(serialNumberDefId);

            // 获取变量参数MAP
            Map<String, Object> renderParams = getVariableParamMap(entity, params);

            // 获取流水号维护
            maintainEntity = getSerialNumberMaintain(entity, renderParams, params);

            maintainUuid = maintainEntity.getUuid();
        }
        // 获取当前重置周期内流水号是否为漏号
        if (StringUtils.isNotBlank(maintainUuid)) {
            missingSerialNo = snSerialNumberRecordService.isMissingSerialNo(serialNo, serialNumberDefId, maintainUuid);
        }
        return !missingSerialNo;
    }

    /**
     * 根据流水号定义ID，获取流水号信息
     *
     * @param serialNumberDefId
     * @return
     */
    @Override
    public SnSerialNumberDefinitionDto getSerialNumberDefinitionById(String serialNumberDefId) {
        return snSerialNumberDefinitionFacadeService.getById(serialNumberDefId);
    }

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表，并进行使用人权限过滤
     *
     * @param categoryUuidOrIds
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionDto> listByCategoryUuidOrId(List<String> categoryUuidOrIds) {
        List<SnSerialNumberDefinitionDto> definitionDtos = snSerialNumberDefinitionFacadeService.listByCategoryUuidOrId(categoryUuidOrIds);
        List<SnSerialNumberDefinitionDto> retDots = filterCurrentUserAllowUseSerialNumberDefinitions(definitionDtos);
        return retDots;
    }

    /**
     * 当前用户是否允许使用流水号
     *
     * @param definitionDtos
     * @return
     */
    private List<SnSerialNumberDefinitionDto> filterCurrentUserAllowUseSerialNumberDefinitions(List<SnSerialNumberDefinitionDto> definitionDtos) {
        List<SnSerialNumberDefinitionDto> retDots = Lists.newArrayList();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Set<String> orgIdSet = null;

        for (SnSerialNumberDefinitionDto dto : definitionDtos) {
            // 使用人权限判断
            String ownerIds = dto.getOwnerIds();
            if (StringUtils.isNotBlank(ownerIds)) {
                if (orgIdSet == null) {
                    orgIdSet = Sets.newHashSet(userDetails.getUserId());
                    orgIdSet.addAll(orgApiFacade.getUserOrgIds(userDetails.getUserId()));
                }

                if (!isIncludeAnyOwerIds(orgIdSet, StringUtils.split(ownerIds, Separator.SEMICOLON.getValue()))) {
                    logger.error("当前用户[{0}]没有流水号[{1}]的使用权限", userDetails.getUserName(), dto.getId());
                } else {
                    retDots.add(dto);
                }
            } else {
                retDots.add(dto);
            }
        }

        return retDots;
    }

    private boolean isIncludeAnyOwerIds(Set<String> orgIdSet, String[] ownerIds) {
        for (String ownerId : ownerIds) {
            if (orgIdSet.contains(ownerId)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取生成流水号使用的参数变量
     *
     * @param entity
     * @param params
     * @return
     */
    private Map<String, Object> getVariableParamMap(SnSerialNumberDefinitionEntity entity, SerialNumberBuildParams params) {
        Map<String, Object> variables = Maps.newHashMap();

        // 添加时间变量
        variables.putAll(getDateVariables(entity.getNextYearStartDate()));

        // 添加表单变更
        variables.putAll(getDyformVariables(params));

        // 接口传入的变量
        variables.putAll(params.getRenderParams());

        return variables;
    }


    /**
     * 获取时间变量
     *
     * @param nextYearStartDate
     * @return
     */
    private Map<String, Object> getDateVariables(String nextYearStartDate) {
        Map<String, Object> variables = Maps.newHashMap();
        Calendar calendar = getRenderCalendar(nextYearStartDate);
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        templateEngine.addDefaultConstantFillFormat(variables, calendar);
        return variables;
    }

    /**
     * 根据新年度开始时间获取使用的时间
     *
     * @param nextYearStartDate
     * @return
     */
    private Calendar getRenderCalendar(String nextYearStartDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            int year = calendar.get(Calendar.YEAR);// 获取年份
            int month = 0;// 获取月份
            int day = 0;// 获取日
            if (StringUtils.isBlank(nextYearStartDate)) {
                nextYearStartDate = "01-01";
            }
            Date nextDate = DateUtils.parseDate(year + "-" + nextYearStartDate);
            if (calendar.getTime().before(nextDate)) {
                year--;
                month = 12;
                day = 31;
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return calendar;
    }


    /**
     * 获取表单变量
     *
     * @param params
     * @return
     */
    private Map<String, Object> getDyformVariables(SerialNumberBuildParams params) {
        String formUuid = params.getFormUuid();
        String dataUuid = params.getDataUuid();
        // 设置表单表名及字段信息
        Map<String, Object> variables = Maps.newHashMap();
        if (StringUtils.isNotBlank(params.getTableName())) {
            variables.put("tableName", params.getTableName());
        } else if (StringUtils.isNotBlank(formUuid)) {
            DyFormFormDefinition dyFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            if (dyFormDefinition != null) {
                variables.put("tableName", dyFormDefinition.getTableName());
            }
        }
        variables.put("fieldName", params.getFormField());


        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return variables;
        }
        DyFormData dyFormData = (DyFormData) params.getRenderParams().get("dyFormData");
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        Map<String, Object> dataOfMainform = dyFormData.getFormDataOfMainform();
        variables.put("dyform", dataOfMainform);

        List<DyformFieldDefinition> dyformFieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
        Map<String, DyformFieldDefinition> dyformFieldDefinitionMap = ConvertUtils.convertElementToMap(dyformFieldDefinitions, "name");
        for (Map.Entry<String, Object> entry : dataOfMainform.entrySet()) {
            DyformFieldDefinition dyformFieldDefinition = dyformFieldDefinitionMap.get(entry.getKey());
            if (dyformFieldDefinition != null) {
                variables.put("表单字段_" + StringUtils.trim(dyformFieldDefinition.getDisplayName()), entry.getValue());
            }
        }
        return variables;
    }

    /**
     * 获取流水号维护
     *
     * @param entity
     * @param renderParams
     * @return
     */
    private SnSerialNumberMaintainEntity getSerialNumberMaintain(SnSerialNumberDefinitionEntity entity, Map<String, Object> renderParams, SerialNumberBuildParams params) {
        SnSerialNumberMaintainEntity maintainEntity = null;
        // 外部传入的流水号维护UUID
        String maintainUuid = params.getUuid();
        if (StringUtils.isNotBlank(maintainUuid)) {
            maintainEntity = serialNumberMaintainService.getOne(maintainUuid);
            if (maintainEntity != null) {
                return maintainEntity;
            }
        }

        String serialNumberDefUuid = entity.getUuid();
        Boolean enablePointerReset = entity.getEnablePointerReset();
        String pointerResetType = entity.getPointerResetType();
        String pointerResetRule = entity.getPointerResetRule();
        String pointerResetRuleValue = null;
        if (BooleanUtils.isTrue(enablePointerReset)) {
            pointerResetRuleValue = generatePointerResetRuleValue(renderParams, pointerResetType, pointerResetRule);
            maintainEntity = serialNumberMaintainService.getOneBySerialNumberDefUuidAndPointerResetRule(serialNumberDefUuid, pointerResetType, pointerResetRule, pointerResetRuleValue);
        } else {
            maintainEntity = serialNumberMaintainService.getOneBySerialNumberDefUuidWithoutPointerResetRule(serialNumberDefUuid);
        }

        if (maintainEntity == null) {
            maintainEntity = new SnSerialNumberMaintainEntity();
            maintainEntity.setSerialNumberDefUuid(serialNumberDefUuid);
            maintainEntity.setInitialValue(entity.getInitialValue());
            maintainEntity.setPointer(entity.getInitialValue());
            if (BooleanUtils.isTrue(enablePointerReset)) {
                maintainEntity.setPointerResetType(pointerResetType);
                maintainEntity.setPointerResetRule(pointerResetRule);
                maintainEntity.setPointerResetRuleValue(pointerResetRuleValue);
            }
        }
        return maintainEntity;
    }

    /**
     * 生成指针重置规则值
     *
     * @param renderParams
     * @param pointerResetType
     * @param pointerResetRule
     * @return
     */
    private String generatePointerResetRuleValue(Map<String, Object> renderParams, String pointerResetType, String pointerResetRule) {
        String pointerResetRuleValue = null;
        PointerResetTypeEnum pointerResetTypeEnum = PointerResetTypeEnum.getByValue(pointerResetType);
        // 按周期重置
        if (PointerResetTypeEnum.ByPeriod.equals(pointerResetTypeEnum)) {
            PointerResetPeriodEnum pointerResetPeriodEnum = PointerResetPeriodEnum.getByValue(pointerResetRule);
            switch (pointerResetPeriodEnum) {
                case ByYear:
                    pointerResetRuleValue = Objects.toString(renderParams.get("YEAR"));
                    break;
                case ByMonth:
                    pointerResetRuleValue = Objects.toString(renderParams.get("YEAR")) + "-" + Objects.toString(renderParams.get("MONTH"));
                    break;
                case ByWeek:
                    pointerResetRuleValue = Objects.toString(renderParams.get("YEAR")) + "年, 第" + Objects.toString(renderParams.get("WEEK")) + "周";
                    break;
                case ByDay:
                    pointerResetRuleValue = Objects.toString(renderParams.get("YEAR")) + "-" + Objects.toString(renderParams.get("MONTH")) +
                            "-" + Objects.toString(renderParams.get("DAY"));
                    break;
            }
        } else {
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            // 按变量重置
            try {
                pointerResetRuleValue = templateEngine.process(replaceExpressIfRequired(pointerResetRule), renderParams);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return pointerResetRuleValue;
    }

    private String replaceExpressIfRequired(String express) {
        return StringUtils.replace(express, "表单字段.", "表单字段_");
    }


    /**
     * 生成流水号信息
     *
     * @param renderParams
     * @param entity
     * @param maintainEntity
     * @return
     */
    private SerialNumberInfo generateSerialNumberInfo(SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity,
                                                      SerialNumberBuildParams params, Map<String, Object> renderParams) {
        // 补号指定流水号记录
        String recordUuid = params.getRecordUuid();
        if (StringUtils.isNotBlank(recordUuid) && isMissingSerialNumberRecord(recordUuid, entity, maintainEntity)) {
            // 生成补号流水号
            return generateSerialNumberInfoByRecordUuid(recordUuid);
        }

        // 自动补号
        if (params.getAutomaticNumberSupplement()) {
            // 是否有漏号
            SnSerialNumberRecordEntity recordEntity = getMissingSerialNumberRecord(entity, maintainEntity, renderParams);
            if (recordEntity != null) {
                // 生成补号
                return generateAutoFillSerialNumberInfo(entity, maintainEntity, recordEntity, renderParams);
            } else {
                // 存在跳号
                List<SnSerialNumberRecordDto> snSerialNumberRecordDtos = getRecordOfSkipPointers(entity, maintainEntity, renderParams);
                if (CollectionUtils.isNotEmpty(snSerialNumberRecordDtos)) {
                    return generateAutoFillSerialNumberInfo(entity, maintainEntity, snSerialNumberRecordDtos.get(0), renderParams);
                } else {
                    // 生成流水号
                    return generateSerialNumberInfo(entity, maintainEntity, renderParams);
                }
            }
        } else {
            // 生成流水号
            // 指针占用
            Long occupiedPointer = params.getOccupiedPointer();
            if (occupiedPointer != null) {
                // 指针占用唯一性判断
                if (StringUtils.isNotBlank(maintainEntity.getUuid()) && snSerialNumberRecordService.isExistsPointer(occupiedPointer, maintainEntity.getUuid())) {
                    occupiedPointer = getNextPointer(occupiedPointer, entity.getIncremental(), maintainEntity.getUuid());
                }
                SnSerialNumberMaintainEntity customMaintainEntity = new SnSerialNumberMaintainEntity();
                BeanUtils.copyProperties(maintainEntity, customMaintainEntity, IdEntity.BASE_FIELDS);
                customMaintainEntity.setPointer(occupiedPointer.toString());
                return generateSerialNumberInfo(entity, customMaintainEntity, renderParams);
            } else {
                return generateSerialNumberInfo(entity, maintainEntity, renderParams);
            }
        }
    }

    /**
     * 是否存在指定的补号记录
     *
     * @param recordUuid
     * @param entity
     * @param maintainEntity
     * @return
     */
    private boolean isMissingSerialNumberRecord(String recordUuid, SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity) {
        return snSerialNumberRecordService.isMissingSerialNumberRecord(recordUuid, entity.getId(), maintainEntity.getUuid());
    }


    /**
     * 是否有漏号
     *
     * @param entity
     * @param maintainEntity
     * @param renderParams
     * @return
     */
    private SnSerialNumberRecordEntity getMissingSerialNumberRecord(SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity, Map<String, Object> renderParams) {
        return snSerialNumberRecordService.getMinimalMissingSerialNumberRecord(entity.getId(), maintainEntity.getUuid());
    }

    /**
     * 根据补号记录生成流水号信息
     *
     * @param recordUuid
     * @return
     */
    private SerialNumberInfo generateSerialNumberInfoByRecordUuid(String recordUuid) {
        SnSerialNumberRecordEntity recordEntity = snSerialNumberRecordService.getOne(recordUuid);
        String serialNo = recordEntity.getSerialNo();
        Long pointer = recordEntity.getPointer();
        String prefix = recordEntity.getPrefix();
        String suffix = recordEntity.getSuffix();
        String pointerValue = StringUtils.substringBeforeLast(StringUtils.substringAfter(serialNo, prefix), suffix);
        SerialNumberInfo
                serialNumber = new SerialNumberInfo(prefix, pointer, pointerValue, suffix, recordEntity.getMaintainUuid(), true, recordEntity.getUuid());
        return serialNumber;
    }

    /**
     * 生成自动补号
     *
     * @return
     */
    private SerialNumberInfo generateAutoFillSerialNumberInfo(SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity,
                                                              SnSerialNumberRecordEntity recordEntity, Map<String, Object> renderParams) {
        String serialNo = recordEntity.getSerialNo();
        Long pointer = recordEntity.getPointer();
        String prefix = recordEntity.getPrefix();
        String suffix = recordEntity.getSuffix();
        String pointerValue = StringUtils.substringBeforeLast(StringUtils.substringAfter(serialNo, prefix), suffix);
        SerialNumberInfo
                serialNumber = new SerialNumberInfo(prefix, pointer, pointerValue, suffix, maintainEntity.getUuid(), true, recordEntity.getUuid());
        return serialNumber;
    }

    /**
     * 生成流水号
     *
     * @param entity
     * @param maintainEntity
     * @param renderParams
     * @return
     */
    private SerialNumberInfo generateSerialNumberInfo(SnSerialNumberDefinitionEntity entity, SnSerialNumberMaintainEntity maintainEntity,
                                                      Map<String, Object> renderParams) {
        String prefix = entity.getPrefix();
        Integer increment = entity.getIncremental();
        Integer defaultDigits = entity.getDefaultDigits();
        String suffix = entity.getSuffix();

        // 当前指针
        String currentPointer = maintainEntity.getPointer();
        String maintainUuid = maintainEntity.getUuid();

        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        // 生成流水号
        String prefixValue = StringUtils.EMPTY;
        String pointerValue = StringUtils.EMPTY;
        Long newPointer = 0l;
        String sufixValue = StringUtils.EMPTY;
        try {
            // 前缀
            if (StringUtils.isNotBlank(prefix)) {
                prefixValue = templateEngine.process(replaceExpressIfRequired(prefix), renderParams);
            }
            // 流水号维护不存在时，指针为初始值
            if (StringUtils.isBlank(maintainUuid)) {
                newPointer = Long.valueOf(currentPointer);
            } else {
                // 获取下一指针，指针占用造成可能的流水号维护指针小于记录中的指针
                newPointer = getNextPointer(Long.valueOf(currentPointer), increment, maintainUuid);
            }
            // 格式化指针
            pointerValue = formatPointerIfRequired(newPointer, defaultDigits);
            // 后缀
            if (StringUtils.isNotBlank(suffix)) {
                sufixValue = templateEngine.process(replaceExpressIfRequired(suffix), renderParams);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        return new SerialNumberInfo(prefixValue, newPointer, pointerValue, sufixValue, maintainUuid);
    }

    /**
     * 获取下一指针
     *
     * @param currentPointer
     * @param increment
     * @param maintainUuid
     * @return
     */
    private Long getNextPointer(Long currentPointer, Integer increment, String maintainUuid) {
        Long nextPointer = currentPointer + increment;
        while (snSerialNumberRecordService.isExistsPointer(nextPointer, maintainUuid)) {
            nextPointer += increment;
        }
        return nextPointer;
    }


    /**
     * 格式化指针
     *
     * @param newPointer
     * @param defaultDigits
     * @return
     */
    private String formatPointerIfRequired(Long newPointer, Integer defaultDigits) {
        String pointerValue = newPointer.toString();
        if (defaultDigits == null) {
            return pointerValue;
        }
        int diffDigits = defaultDigits - pointerValue.length();
        if (diffDigits <= 0) {
            return pointerValue;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diffDigits; i++) {
            sb.append("0");
        }
        sb.append(pointerValue);
        return sb.toString();
    }

    /**
     * 保存或更新流水号维护
     *
     * @param serialNumberInfo
     * @param maintainEntity
     */
    public void saveOrUpdateSerialMaintain(SerialNumberInfo serialNumberInfo, SnSerialNumberMaintainEntity maintainEntity, SerialNumberBuildParams params) {
        // 流水号补号，不更新流水号维护指针
        if (serialNumberInfo.isFill()) {
            return;
        }

        // 不强制占用或强制占用的指针大于当前指针时才更新流水号维护指针
        if (params.getOccupiedPointer() == null || params.getOccupiedPointer() > Long.valueOf(maintainEntity.getPointer())) {
            maintainEntity.setPointer(serialNumberInfo.getPointer().toString());
        }
        serialNumberMaintainService.save(maintainEntity);
    }

    /**
     * 保存流水号历史记录
     *
     * @param serialNumberInfo
     * @param maintainEntity
     * @param params
     */
    private void saveSerialNumberRecord(SerialNumberInfo serialNumberInfo, SnSerialNumberMaintainEntity maintainEntity,
                                        SerialNumberBuildParams params, Map<String, Object> renderParams) {
        String tableName = Objects.toString(renderParams.get("tableName"));
        String fieldName = Objects.toString(renderParams.get("fieldName"));
        SnSerialNumberRelationEntity relationEntity = snSerialNumberRelationService.getOne(params.getSerialNumberId(), ObjectTypeEnum.TABLE.getType(), tableName, fieldName);
        String relationUuid = null;
        // 保存关系表
        if (relationEntity == null) {
            relationUuid = snSerialNumberRelationService.save(params.getSerialNumberId(), ObjectTypeEnum.TABLE.getType(), tableName, fieldName);
        } else {
            relationUuid = relationEntity.getUuid();
        }

        // 保存流水号记录
        if (serialNumberInfo.isFill() && StringUtils.isNotBlank(serialNumberInfo.getRecordUuid())) {
            // 补号记录更新对应记录关系表UUID
            snSerialNumberRecordService.updateRelationUuidByUuid(serialNumberInfo.getRecordUuid(), relationUuid);
        } else {
            String recordUuid = snSerialNumberRecordService.save(relationUuid, maintainEntity.getUuid(), serialNumberInfo);
            // 流水号占用，设置对应的记录UUID
            serialNumberInfo.setRecordUuid(recordUuid);
        }
    }

}
