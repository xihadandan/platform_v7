/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.IsOpinionRuleCheckDto;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.dto.WfOpinionCheckSetDto;
import com.wellsoft.pt.workflow.dto.WfOpinionCheckSetIncludeRuleDto;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;
import com.wellsoft.pt.workflow.enums.ItemConditionEnum;
import com.wellsoft.pt.workflow.enums.SatisfyConditionEnum;
import com.wellsoft.pt.workflow.facade.service.WfOpinionCheckSetFacadeService;
import com.wellsoft.pt.workflow.service.OpinionRuleService;
import com.wellsoft.pt.workflow.service.WfOpinionCheckSetService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description: 数据库表WF_OPINION_CHECK_SET的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-05-11.1	zenghw		2021-05-11		Create
 * </pre>
 * @date 2021-05-11
 */
@Service
public class WfOpinionCheckSetFacadeServiceImpl extends AbstractApiFacade implements WfOpinionCheckSetFacadeService {

    @Autowired
    private WfOpinionCheckSetService wfOpinionCheckSetService;
    @Autowired
    private OpinionRuleService opinionRuleService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    public List<WfOpinionCheckSetDto> getOpinionCheckSets(String flowId) {
        List<WfOpinionCheckSetEntity> opinionCheckSetEntities = wfOpinionCheckSetService.getOpinionCheckSets(flowId);
        return BeanUtils.copyCollection(opinionCheckSetEntities, WfOpinionCheckSetDto.class);
    }

    @Override
    @Transactional
    public void saveOpinionCheckSetList(List<WfOpinionCheckSetEntity> dtos) {
        if (dtos.size() > 0) {
            wfOpinionCheckSetService.deleteByFlowId(dtos.get(0).getFlowDefId());
            wfOpinionCheckSetService.saveAll(dtos);
        }

    }

    /**
     * 保存流程的意见校验设置列表
     *
     * @param flowDefId
     * @param dtos
     */
    @Override
    @Transactional
    public void saveOpinionCheckSetList(String flowDefId, List<WfOpinionCheckSetEntity> dtos) {
        wfOpinionCheckSetService.deleteByFlowId(flowDefId);
        if (dtos.size() > 0) {
            wfOpinionCheckSetService.saveAll(dtos);
        }
    }

    /**
     * 校验签署意见是否符合规则
     *
     * @param opinionText 签署意见
     * @param flowId      流程定义ID
     * @param taskId      环节ID
     * @param scene       场景
     * @return com.wellsoft.pt.workflow.dto.IsOpinionRuleCheckDto
     **/
    @Override
    public IsOpinionRuleCheckDto isOpinionRuleCheck(String opinionText, String flowId, String taskId, String scene) {
        if (StringUtils.isBlank(opinionText)) {
            opinionText = "";
        } else {
            opinionText = opinionText.trim();
        }
        IsOpinionRuleCheckDto isOpinionRuleCheckDto = new IsOpinionRuleCheckDto();
        List<WfOpinionCheckSetDto> opinionCheckSetDtos = getOpinionCheckSets(flowId);
        if (opinionCheckSetDtos.size() == 0) {
            return isOpinionRuleCheckDto;
        }
        Set<String> ruleUuidSets = new HashSet<>();
        for (WfOpinionCheckSetDto wfOpinionCheckSetDto : opinionCheckSetDtos) {
            if (StringUtils.isNotBlank(wfOpinionCheckSetDto.getOpinionRuleUuid())) {
                ruleUuidSets.add(wfOpinionCheckSetDto.getOpinionRuleUuid());
            }
        }

        List<WfOpinionCheckSetIncludeRuleDto> opinionCheckSetIncludeRuleDtos = BeanUtils
                .copyCollection(opinionCheckSetDtos, WfOpinionCheckSetIncludeRuleDto.class);

        List<String> ruleUuidList = new ArrayList<>(ruleUuidSets);
        if (ruleUuidList.size() == 0) {
            return isOpinionRuleCheckDto;
        }
        List<OpinionRuleIncludeItemDto> opinionRuleIncludeItemDtos = opinionRuleService
                .getOpinionRuleIncludeItemDtos(ruleUuidList);

        Map<String, List<OpinionRuleIncludeItemDto>> ruleItemMap = new HashMap<>();
        for (OpinionRuleIncludeItemDto item : opinionRuleIncludeItemDtos) {
            List<OpinionRuleIncludeItemDto> itemEntities = ruleItemMap.get(item.getUuid());
            if (itemEntities == null) {
                itemEntities = new ArrayList<>();
            }
            itemEntities.add(item);
            ruleItemMap.put(item.getUuid(), itemEntities);
        }
        for (WfOpinionCheckSetIncludeRuleDto dto : opinionCheckSetIncludeRuleDtos) {
            dto.setOpinionRuleDto(ruleItemMap.get(dto.getOpinionRuleUuid()).get(0));
        }
        // 应用环节
        final String taskIds = "all";
        StringBuilder errorMsg = new StringBuilder();
        for (WfOpinionCheckSetIncludeRuleDto dto : opinionCheckSetIncludeRuleDtos) {
            if (scene.equals(dto.getScene())) {
                if (StringUtils.isNotBlank(dto.getTaskIds()) &&
                        (taskIds.equals(dto.getTaskIds()) || (dto.getTaskIds().indexOf(taskId) > -1))) {
                    // 适合所有环节 或有包含此环节 才校验
                    // 满足条件 SC01:全部 或//SC02:任何 算校验通过
                    Boolean isAny = false;
                    if (SatisfyConditionEnum.ANY.getValue().equals(dto.getOpinionRuleDto().getSatisfyCondition())) {
                        isAny = true;
                    }
                    Boolean isAnySuccess = false;
                    Boolean isSuccess = true;
                    for (WfOpinionRuleItemEntity opinionRuleItemEntity : dto.getOpinionRuleDto()
                            .getOpinionRuleItemEntitys()) {
                        ItemConditionEnum itemConditionEnum = ItemConditionEnum
                                .getByValue(opinionRuleItemEntity.getItemCondition());
                        if (opinionRuleItemEntity.getItemName().equals("意见内容")) {
                            String itemValue = opinionRuleItemEntity.getItemValue().trim();
                            switch (itemConditionEnum) {
                                case EQUAL:
                                    if (!opinionText.equals(itemValue)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case UNEQUAL:
                                    if (opinionText.equals(itemValue)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case INCLUDE:
                                    if (opinionText.indexOf(itemValue) == -1) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isSuccess = true;
                                            isAnySuccess = true;
                                        }
                                    }
                                    break;
                                case NOT_IN:
                                    if (opinionText.indexOf(itemValue) > -1) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isSuccess = true;
                                            isAnySuccess = true;
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            // 意见长度
                            Integer opinionTextLeng = opinionText.trim().length();
                            Integer itemValue = Integer.valueOf(opinionRuleItemEntity.getItemValue());
                            switch (itemConditionEnum) {
                                case EQUAL:
                                    if (!opinionTextLeng.equals(itemValue)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case UNEQUAL:
                                    if (opinionTextLeng.equals(itemValue)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case GREATER_THAN:
                                    if (!(opinionTextLeng.compareTo(itemValue) > 0)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case GE:
                                    if (opinionTextLeng.compareTo(itemValue) < 0) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case LESS_THAN:
                                    if (!(opinionTextLeng.compareTo(itemValue) < 0)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                case LE:
                                    if ((opinionTextLeng.compareTo(itemValue) > 0)) {
                                        isSuccess = false;

                                    } else {
                                        if (isAny) {
                                            isOpinionRuleCheckDto.setSuccess(true);
                                            isAnySuccess = true;
                                            isSuccess = true;
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (isAnySuccess) {
                            // 符合任一条件，直接校验通过
                            break;
                        }
                    }
                    if (!isSuccess) {
                        errorHandlerOpinionRuleCheck(errorMsg, dto, isOpinionRuleCheckDto);
                    }
                }
            }
        }
        if (!isOpinionRuleCheckDto.getSuccess()) {
            // 提示语的最后一个分号不要显示
            String errorMsg1 = errorMsg.toString().substring(0, errorMsg.length() - 1);
//            String msg = (String) ServiceInvokeUtils.invoke("translateService.translate", new Class[]{String.class, String.class, String.class}, errorMsg1, "zh", LocaleContextHolder.getLocale().getLanguage());
            isOpinionRuleCheckDto.setMessage(errorMsg1);
        }
        return isOpinionRuleCheckDto;
    }

    /**
     * 校验签署意见处理-错误处理
     *
     * @param errorMsg
     * @param dto
     * @param isOpinionRuleCheckDto
     * @return void
     **/
    private void errorHandlerOpinionRuleCheck(StringBuilder errorMsg, WfOpinionCheckSetIncludeRuleDto dto,
                                              IsOpinionRuleCheckDto isOpinionRuleCheckDto) {
        AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(dto.getOpinionRuleUuid(), null, "cueWords",
                new BigDecimal(1), IexportType.FLowOpinionRule, LocaleContextHolder.getLocale().toString());
        errorMsg.append(i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent()) ? i18nEntity.getContent() : dto.getOpinionRuleDto().getCueWords());
        errorMsg.append("；");
        isOpinionRuleCheckDto.setSuccess(Boolean.FALSE);
        // 1 为选中
        final String isChecked = "1";
        if (!dto.getOpinionRuleDto().getIsAlertAutoClose().equals(isChecked)) {
            // 如果有没自动关闭的，永久不自动关闭
            isOpinionRuleCheckDto.setAlertAutoClose(Boolean.FALSE);
        }

    }
}
