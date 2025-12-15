/*
 * @(#)2018年6月8日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.ApproveContentDto;
import com.wellsoft.pt.app.workflow.enums.WorkFlowApproveContentEnum;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowApproveService;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
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
 * 2018年6月8日.1	zhulh		2018年6月8日		Create
 * </pre>
 * @date 2018年6月8日
 */
@Service
public class WorkFlowApproveServiceImpl implements WorkFlowApproveService {

    @Autowired
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BotFacadeService botFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowApproveService#sendToApprove(java.lang.String)
     */
    @Override
    @Transactional
    public ResultMessage sendToApprove(ApproveContentDto approveContentDto, InteractionTaskData interactionTaskData) {
        String type = approveContentDto.getType();
        ResultMessage resultMessage = null;
        // 源文送审批
        if (WorkFlowApproveContentEnum.Source.getValue().equals(type)) {
            resultMessage = sendSourceToApprove(approveContentDto, interactionTaskData);
        } else if (WorkFlowApproveContentEnum.CopySource.getValue().equals(type)) {
            // 复制源文送审批
            resultMessage = sendCopySourceToApprove(approveContentDto, interactionTaskData);
        } else if (WorkFlowApproveContentEnum.Link.getValue().equals(type)) {
            // 作为文档链接送审批
            resultMessage = sendLinkToApprove(approveContentDto, interactionTaskData);
        }
        return resultMessage;
    }

    /**
     * 源文送审批
     *
     * @param approveContentDto
     * @return
     */
    private ResultMessage sendSourceToApprove(ApproveContentDto approveContentDto,
                                              InteractionTaskData interactionTaskData) {
        String formUuid = approveContentDto.getFormUuid();
        String dataUuid = approveContentDto.getDataUuid();
        String flowDefId = approveContentDto.getFlowDefId();
        WorkBean workBean = workService.newWorkById(flowDefId);
        workBean.setFormUuid(formUuid);
        workBean.setDataUuid(dataUuid);
        workBean.setDyFormData(dyFormFacade.getDyFormData(formUuid, dataUuid));

        // 复制交互式的参数
        BeanUtils.copyProperties(interactionTaskData, workBean);

        // 设置链接信息作为运行时参数
        addLinkContent(approveContentDto, workBean);

        // 提交流程
        ResultMessage resultMessage = workService.submit(workBean);
        return resultMessage;
    }

    /**
     * 复制源文送审批
     *
     * @param approveContentDto
     * @return
     */
    private ResultMessage sendCopySourceToApprove(ApproveContentDto approveContentDto,
                                                  InteractionTaskData interactionTaskData) {
        String flowDefId = approveContentDto.getFlowDefId();
        WorkBean workBean = workService.newWorkById(flowDefId);
        String formUuid = workBean.getFormUuid();
        // 复制表单字段数据
        String sourceFormUuid = approveContentDto.getFormUuid();
        String sourceDataUuid = approveContentDto.getDataUuid();
        String botRuleId = approveContentDto.getBotRuleId();
        String dataUuid = copyOrConvertFormData(sourceFormUuid, sourceDataUuid, botRuleId, formUuid);
        workBean.setDataUuid(dataUuid);
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        workBean.setDyFormData(dyFormData);

        TaskForm taskForm = workService.getTaskForm(workBean);
        // 必填字段检测
        checkRequireFields(dyFormData, taskForm);
        // 只填充可编辑字段，删除只读字段值
        removeOnlyReadFieldIfRequired(dyFormData, taskForm, approveContentDto.isOnlyFillEditableField());

        // 复制交互式的参数
        BeanUtils.copyProperties(interactionTaskData, workBean);

        // 设置链接信息作为运行时参数
        addLinkContent(approveContentDto, workBean);

        // 提交流程
        ResultMessage resultMessage = workService.submit(workBean);
        return resultMessage;
    }

    /**
     * @return
     */
    private String copyOrConvertFormData(String sourceFormUuid, String sourceDataUuid, String botRuleId,
                                         String targetFormUuid) {
        String dataUuid = null;
        if (StringUtils.isBlank(botRuleId)) {
            dataUuid = dyFormFacade.copyFormData(sourceFormUuid, sourceDataUuid, targetFormUuid);
        } else {
            DyFormData dyFormData = convertDyformDataByBotRuleId(sourceFormUuid, sourceDataUuid, botRuleId);
            if (StringUtils.equals(dyFormFacade.getFormIdByFormUuid(dyFormData.getFormUuid()),
                    dyFormFacade.getFormIdByFormUuid(targetFormUuid))) {
                dataUuid = dyFormFacade.saveFormData(dyFormData);
            } else {
                dataUuid = dyFormFacade.copyFormData(dyFormData, targetFormUuid);
            }
        }
        return dataUuid;
    }

    /**
     * 作为文档链接送审批
     *
     * @param approveContentDto
     * @return
     */
    private ResultMessage sendLinkToApprove(ApproveContentDto approveContentDto,
                                            InteractionTaskData interactionTaskData) {
        String flowDefId = approveContentDto.getFlowDefId();
        WorkBean workBean = workService.newWorkById(flowDefId);

        // 复制交互式的参数
        BeanUtils.copyProperties(interactionTaskData, workBean);

        // 设置链接信息作为运行时参数
        addLinkContent(approveContentDto, workBean);

        String formUuid = workBean.getFormUuid();
        DyFormData dyFormData = dyFormFacade.createDyformData(formUuid);
        dyFormData.setFieldValue(IdEntity.CREATOR, SpringSecurityUtils.getCurrentUserId());
        String dataUuid = dyFormFacade.saveFormData(dyFormData);
        workBean.setDataUuid(dataUuid);
        workBean.setDyFormData(dyFormData);
        TaskForm taskForm = workService.getTaskForm(workBean);
        // 必填字段检测
        // checkRequireFields(dyFormData, taskForm);

        // 提交流程
        ResultMessage resultMessage = workService.submit(workBean);
        return resultMessage;
    }

    /**
     * @param approveContentDto
     * @param workBean
     */
    private void addLinkContent(ApproveContentDto approveContentDto, WorkBean workBean) {
        Map<String, Object> sentContentMap = new HashMap<String, Object>();
        sentContentMap.put("type", approveContentDto.getType());
        sentContentMap.put("links", approveContentDto.getLinks());
//        sentContentMap.put("linkTitle", approveContentDto.getLinkTitle());
//        sentContentMap.put("linkUrl", approveContentDto.getLinkUrl());
        workBean.addExtraParam("custom_rt_sentContent", JsonUtils.object2Json(sentContentMap));
        workBean.addExtraParam("custom_rt_workViewFragment", "WorkViewSendToApproveFragment");
    }

    /**
     * @param dyFormData
     * @param taskForm
     * @param onlyFillEditableField
     */
    private void removeOnlyReadFieldIfRequired(DyFormData dyFormData, TaskForm taskForm,
                                               boolean onlyFillEditableField) {
        if (!onlyFillEditableField) {
            return;
        }
        String mainformUuid = dyFormData.getFormUuid();
        // 平台7.0不支持后端返回表单可编辑字段，将onlyFillEditableField默认调整为false
        List<String> editableFields = dyFormData.getEditableFields(mainformUuid);
        String formId = dyFormFacade.getFormIdByFormUuid(mainformUuid);
        List<Map<String, Object>> dataList = dyFormData.getFormDatasById(formId);
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map<String, Object> map : dataList) {
                List<String> onlyReadFields = Lists.newArrayList(map.keySet());
                onlyReadFields.remove(JpaEntity.UUID);
                onlyReadFields.removeAll(editableFields);
                for (String fieldName : onlyReadFields) {
                    map.remove(fieldName);
                }
            }
        }
    }

    /**
     * @param dyFormData
     * @param taskForm
     * @return
     */
    private boolean checkRequireFields(DyFormData dyFormData, TaskForm taskForm) {
        String formUuid = dyFormData.getFormUuid();
        List<String> emptyValueFields = new ArrayList<String>();
        // 主表判断
        List<String> requireFields = dyFormData.getRequiredFields(formUuid);
        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        if (notNullFieldMap.containsKey(formUuid)) {
            requireFields.addAll(notNullFieldMap.get(formUuid));
        }
        for (String requireField : requireFields) {
            if (dyFormData.isFieldExist(requireField) && dyFormData.getFieldValue(requireField) == null
                    || StringUtils.isBlank(dyFormData.getFieldValue(requireField).toString())) {
                emptyValueFields.add(requireField);
                break;
            }
        }
        String errorMsg = "流程表单数据存在空值的必填字段，无法自动提交送审批！";
        if (CollectionUtils.isNotEmpty(emptyValueFields)) {
            throw new BusinessException(errorMsg);
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowApproveService#isAllowedConvertDyformDataByBotRuleId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowedConvertDyformDataByBotRuleId(String sourceFormUuid, String botRuleId, String flowDefId) {
        BotRuleConfDto botRuleConfDto = botFacadeService.getRuleConfById(botRuleId);
        boolean sourceMatch = false;
        if (botRuleConfDto != null && StringUtils.isNotBlank(botRuleConfDto.getSourceObjId())
                && StringUtils.isNotBlank(botRuleConfDto.getTargetObjId())) {
            String sourceObjId = botRuleConfDto.getSourceObjId();
            String targetObjId = botRuleConfDto.getTargetObjId();
            sourceMatch = StringUtils.equals(dyFormFacade.getFormIdByFormUuid(sourceFormUuid),
                    dyFormFacade.getFormIdByFormUuid(sourceObjId));
            if (!sourceMatch) {
                return false;
            }
            if (StringUtils.isBlank(flowDefId)) {
                return true;
            }
            FlowDefinition flowDefinition = FlowEngine.getInstance().getFlowService().getFlowDefinitionById(flowDefId);
            return StringUtils.equals(dyFormFacade.getFormIdByFormUuid(flowDefinition.getFormUuid()),
                    dyFormFacade.getFormIdByFormUuid(targetObjId));
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowApproveService#convertDyformDataByBotRuleId(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public DyFormData convertDyformDataByBotRuleId(String sourceFormUuid, String sourceDataUuid, String botRuleId) {
        BotRuleConfDto botRuleConfDto = botFacadeService.getRuleConfById(botRuleId);
        if (botRuleConfDto == null) {
            throw new BusinessException("单据转换规则[" + botRuleId + "]不存在！");
        }
        if (BooleanUtils.isTrue(botRuleConfDto.getIsPersist())) {
            throw new BusinessException("送审批配置的单据转换规则[" + botRuleId + "]不能配置为保存单据！");
        }

        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(sourceDataUuid, dyFormFacade.getFormIdByFormUuid(sourceFormUuid));
        froms.add(botFromParam);
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        BotResult botResult = botFacadeService.startBot(botParam);
        Object data = botResult.getData();
        if (data instanceof Map) {
            Map<String, List<Map<String, Object>>> formDatas = (Map<String, List<Map<String, Object>>>) data;
            DyFormData dyFormData = dyFormFacade.createDyformData(formDatas.keySet().iterator().next());
            dyFormData.setFormDatas(formDatas, false);
            return dyFormData;
        }
        return null;
    }

}
