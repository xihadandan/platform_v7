/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.dto.BizProcessItemDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessItemOperationDto;
import com.wellsoft.pt.biz.dto.BizWorkflowProcessItemDataDto;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.facade.service.BizProcessItemFacadeService;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Api(tags = "业务事项实例")
@RestController
@RequestMapping("/api/biz/process/item/instance")
public class ApiBizProcessItemInstanceController extends BaseController {

    @Autowired
    private BizProcessItemFacadeService bizProcessItemFacadeService;

    /**
     * 获取业务事项实例数据
     *
     * @param itemDataRequestParamDto
     * @return
     */
    @PostMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务事项实例数据", notes = "获取业务事项实例数据")
    public ApiResult<BizProcessItemDataDto> get(@RequestBody BizProcessItemDataRequestParamDto itemDataRequestParamDto) {
        BizProcessItemDataDto itemDataDto = bizProcessItemFacadeService.getItemData(itemDataRequestParamDto);
        return ApiResult.success(itemDataDto);
    }

    /**
     * 获取流程集成工作数据
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getWorkDataByWorkBean", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程集成工作数据", notes = "获取流程集成工作数据")
    @ApiOperationSupport(order = 30)
    public ApiResult<WorkBean> getWorkDataByWorkBean(@RequestParam("processDefId") String processDefId, @RequestParam("processItemIds") String processItemIds,
                                                     @RequestBody WorkBean workBean, HttpServletResponse response) throws IOException {
        WorkBean workData = bizProcessItemFacadeService.getWorkData(processDefId, processItemIds, workBean);
        return ApiResult.success(workData);
    }

    /**
     * 获取流程业务集成实例
     *
     * @param itemInstUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkflowBusinessIntegration", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程业务集成实例", notes = "获取流程业务集成实例")
    @ApiOperationSupport(order = 30)
    public ApiResult<BizBusinessIntegrationEntity> getWorkflowBusinessIntegration(@RequestParam(name = "itemInstUuid") String itemInstUuid) {
        BizBusinessIntegrationEntity entity = bizProcessItemFacadeService.getWorkflowBusinessIntegrationByItemInstUuid(itemInstUuid);
        return ApiResult.success(entity);
    }

    /**
     * 保存业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务事项实例数据", notes = "保存业务事项实例数据")
    public ApiResult<String> save(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.save(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 提交业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "提交业务事项实例数据", notes = "提交业务事项实例数据")
    public ApiResult<String> submit(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.submit(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 从流程单据提交业务事项实例数据
     *
     * @param flowInstUuid
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/startFromWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "从流程单据提交业务事项实例数据", notes = "从流程单据提交业务事项实例数据")
    public ApiResult<SubmitResult> startFromWorkflow(@RequestParam(name = "flowInstUuid") String flowInstUuid,
                                                     @RequestBody BizWorkflowProcessItemDataDto itemDataDto) {
        return ApiResult.success(bizProcessItemFacadeService.startFromWorkflow(flowInstUuid, itemDataDto));
    }

    /**
     * 启动计时器
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/startTimer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "启动计时器", notes = "启动计时器")
    public ApiResult<String> startTimer(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.startTimer(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 暂停计时器
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/pauseTimer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "暂停计时器", notes = "暂停计时器")
    public ApiResult<String> pauseTimer(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.pauseTimer(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 恢复计时器
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/resumeTimer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "恢复计时器", notes = "恢复计时器")
    public ApiResult<String> resumeTimer(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.resumeTimer(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 挂起业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/suspend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "挂起业务事项实例", notes = "挂起业务事项实例")
    public ApiResult<String> suspend(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.suspend(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 恢复业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/resume", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "恢复业务事项实例", notes = "恢复业务事项实例")
    public ApiResult<String> resume(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.resume(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 撤销业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "撤销业务事项实例", notes = "撤销业务事项实例")
    public ApiResult<String> cancel(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.cancel(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }


    /**
     * 根据流程数据，撤销业务事项实例
     *
     * @param workData
     * @return
     */
    @PostMapping(value = "/cancelByWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程数据，撤销业务事项实例", notes = "根据流程数据，撤销业务事项实例")
    public ApiResult<String> cancelByWorkData(@RequestBody WorkBean workData) {
        String itemInstUuid = bizProcessItemFacadeService.cancelByWorkData(workData);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 根据流程数据，撤销发起的业务事项实例
     *
     * @param workData
     * @return
     */
    @PostMapping(value = "/cancelOtherByWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程数据，撤销发起的业务事项实例", notes = "根据流程数据，撤销发起的业务事项实例")
    public ApiResult<String> cancelOtherByWorkData(@RequestBody WorkBean workData) {
        String itemInstUuid = bizProcessItemFacadeService.cancelOtherByWorkData(workData);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 完成业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "完成业务事项实例", notes = "完成业务事项实例")
    public ApiResult<String> complete(@RequestBody BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = bizProcessItemFacadeService.complete(itemDataDto);
        return ApiResult.success(itemInstUuid);
    }

    /**
     * 根据业务流程定义ID、事项ID列表获取相应的事项办理状态
     *
     * @param processDefId
     * @param itemIds
     * @return
     */
    @PostMapping(value = "/listItemStates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程定义ID、事项ID列表获取相应的事项办理状态", notes = "根据业务流程定义ID、事项ID列表获取相应的事项办理状态")
    public ApiResult<Map<String, String>> listItemStates(@RequestParam(name = "processDefId") String processDefId,
                                                         @RequestBody List<String> itemIds) {
        Map<String, String> itemStateMap =
                bizProcessItemFacadeService.listItemStatesByProcessDefIdAndItemIds(processDefId, itemIds);
        return ApiResult.success(itemStateMap);
    }

    /**
     * 根据业务事项实例UUID获取业务事项操作列表
     *
     * @param itemInstUuid
     * @return
     */
    @GetMapping(value = "/listProcessItemOperationByUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务事项实例UUID获取业务事项操作列表", notes = "根据业务事项实例UUID获取业务事项操作列表")
    public ApiResult<List<BizProcessItemOperationDto>> listProcessItemOperationByUuid
    (@RequestParam(name = "itemInstUuid") String itemInstUuid) {
        List<BizProcessItemOperationDto> processItemOperationDtos =
                bizProcessItemFacadeService.listProcessItemOperationByUuid(itemInstUuid);
        return ApiResult.success(processItemOperationDtos);
    }

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形材料
     *
     * @param itemId
     * @param processDefId
     * @return
     */
    @PostMapping(value = "/listItemSituationMaterial", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形材料", notes = "根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形材料")
    public ApiResult<List<ItemMaterial>> listItemSituationMaterial(@RequestParam(name = "itemId") String itemId,
                                                                   @RequestParam(name = "processDefId") String processDefId,
                                                                   @RequestParam(name = "conditonJsonMap") String conditonJsonMap) {
        Map<String, Object> values = Maps.newHashMap();
        if (StringUtils.isNotBlank(conditonJsonMap)) {
            values = JsonUtils.json2Object(conditonJsonMap, Map.class);
        }
        // 获取事项办理情形材料
        List<ItemMaterial> itemMaterials = bizProcessItemFacadeService.listItemSituationMaterial(itemId, processDefId, values);
        return ApiResult.success(itemMaterials);
    }

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形时限
     *
     * @param itemId
     * @param processDefId
     * @return
     */
    @PostMapping(value = "/listItemSituationTimeLimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形时限", notes = "根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形时限")
    public ApiResult<List<ItemTimeLimit>> listItemSituationTimeLimit(@RequestParam(name = "itemId") String itemId,
                                                                     @RequestParam(name = "processDefId") String processDefId,
                                                                     @RequestParam(name = "conditonJsonMap") String conditonJsonMap) {
        Map<String, Object> values = Maps.newHashMap();
        if (StringUtils.isNotBlank(conditonJsonMap)) {
            values = JsonUtils.json2Object(conditonJsonMap, Map.class);
        }
        // 获取事项办理情形时限
        List<ItemTimeLimit> itemMaterials = bizProcessItemFacadeService.listItemSituationTimeLimit(itemId, processDefId, values);
        return ApiResult.success(itemMaterials);
    }

}
