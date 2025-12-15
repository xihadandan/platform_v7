/*
 * @(#)2021年3月31日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.ApproveContentDto;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowApproveService;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 流程送审批接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月31日.1	zhulh		2021年3月31日		Create
 * </pre>
 * @date 2021年3月31日
 */
@Api(tags = "流程送审批")
@RestController
@RequestMapping("/api/workflow/approve")
public class ApiWorkflowApproveController extends BaseController {

    @Autowired
    private WorkFlowApproveService workFlowApproveService;

    /**
     * 判断是否允许通过单据转换规则转换源表单数据
     *
     * @param sourceFormUuid
     * @param botRuleId
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "判断是否允许通过单据转换规则转换源表单数据", notes = "判断是否允许通过单据转换规则转换源表单数据")
    @GetMapping("/isAllowedConvertDyformDataByBotRuleId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceFormUuid", value = "源表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "botRuleId", value = "单据转换规则ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isAllowedConvertDyformDataByBotRuleId(
            @RequestParam(name = "sourceFormUuid", required = true) String sourceFormUuid,
            @RequestParam(name = "botRuleId", required = true) String botRuleId,
            @RequestParam(name = "flowDefId", required = false) String flowDefId) {
        return ApiResult
                .success(workFlowApproveService.isAllowedConvertDyformDataByBotRuleId(sourceFormUuid, botRuleId, flowDefId));
    }

    /**
     * 通过单据转换规则转换源表单数据
     *
     * @param sourceFormUuid
     * @param sourceDataUuid
     * @param botRuleId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "通过单据转换规则转换源表单数据", notes = "通过单据转换规则转换源表单数据")
    @PostMapping("/convertDyformDataByBotRuleId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceFormUuid", value = "源表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "sourceDataUuid", value = "源表单数据UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "botRuleId", value = "单据转换规则ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<DyFormData> convertDyformDataByBotRuleId(
            @RequestParam(name = "sourceFormUuid", required = true) String sourceFormUuid,
            @RequestParam(name = "sourceDataUuid", required = true) String sourceDataUuid,
            @RequestParam(name = "botRuleId", required = true) String botRuleId) {
        return ApiResult.success(
                workFlowApproveService.convertDyformDataByBotRuleId(sourceFormUuid, sourceDataUuid, botRuleId));
    }

    /**
     * 送审批
     *
     * @param request
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "送审批", notes = "送审批")
    @PostMapping("/sendToApprove")
    public ResultMessage sendToApprove(@RequestBody WorkflowSendToApproveRequest request) {
        return workFlowApproveService.sendToApprove(request.getApproveContent(), request.getInteractionTaskData());
    }

    @ApiModel("流程送审批请求数据")
    private static final class WorkflowSendToApproveRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1674235428646033150L;

        @ApiModelProperty("送审批内容")
        private ApproveContentDto approveContent;

        @ApiModelProperty("交互式的环节数据")
        private InteractionTaskData interactionTaskData;

        /**
         * @return the approveContent
         */
        public ApproveContentDto getApproveContent() {
            return approveContent;
        }

        /**
         * @param approveContent 要设置的approveContent
         */
        public void setApproveContent(ApproveContentDto approveContent) {
            this.approveContent = approveContent;
        }

        /**
         * @return the interactionTaskData
         */
        public InteractionTaskData getInteractionTaskData() {
            return interactionTaskData;
        }

        /**
         * @param interactionTaskData 要设置的interactionTaskData
         */
        public void setInteractionTaskData(InteractionTaskData interactionTaskData) {
            this.interactionTaskData = interactionTaskData;
        }

    }

}
