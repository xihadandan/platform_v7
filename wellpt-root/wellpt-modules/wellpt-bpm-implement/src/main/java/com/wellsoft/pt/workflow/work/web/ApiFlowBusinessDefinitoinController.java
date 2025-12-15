/*
 * @(#)11/14/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.dto.WfFlowBusinessDefinitionDto;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessDefinitionFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
 * 11/14/22.1	zhulh		11/14/22		Create
 * </pre>
 * @date 11/14/22
 */
@Api(tags = "工作流程")
@RestController
@RequestMapping("/api/workflow/business/definition")
public class ApiFlowBusinessDefinitoinController extends BaseController {

    @Autowired
    private WfFlowBusinessDefinitionFacadeService flowBusinessDefinitionFacadeService;

    /**
     * 获取流程业务定义
     *
     * @return
     */
    @GetMapping(value = "/get/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程业务定义", notes = "获取流程业务定义")
    public ApiResult<WfFlowBusinessDefinitionDto> get(@PathVariable("uuid") String uuid) {
        WfFlowBusinessDefinitionDto dto = flowBusinessDefinitionFacadeService.getDto(uuid);
        return ApiResult.success(dto);
    }

    /**
     * 保存流程业务定义
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程业务定义", notes = "保存流程业务定义")
    public ApiResult<Void> save(@RequestBody WfFlowBusinessDefinitionDto dto) {
        flowBusinessDefinitionFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除流程业务定义
     *
     * @param uuids
     * @return
     */
    @GetMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除流程业务定义", notes = "删除流程业务定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "流程业务定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowBusinessDefinitionFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 根据流程定义ID获取表单字段下拉数据
     *
     * @return
     */
    @GetMapping(value = "/getFormFieldSelectDataByFlowDefId/{flowDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取表单字段下拉数据", notes = "根据流程定义ID获取表单字段下拉数据")
    public ApiResult<List<Select2DataBean>> getFormFieldSelectDataByFlowDefId(@PathVariable(name = "flowDefId") String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return ApiResult.success(Collections.emptyList());
        }
        List<Select2DataBean> list = flowBusinessDefinitionFacadeService.getFormFieldSelectDataByFlowDefId(flowDefId);
        return ApiResult.success(list);
    }

    /**
     * 根据流程定义ID获取流程环节下拉数据
     *
     * @return
     */
    @GetMapping(value = "/getFlowTaskSelectDataByFlowDefId/{flowDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取流程环节下拉数据", notes = "根据流程定义ID获取流程环节下拉数据")
    public ApiResult<List<Select2DataBean>> getFlowTaskSelectDataByFlowDefId(@PathVariable(name = "flowDefId") String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return ApiResult.success(Collections.emptyList());
        }
        List<Select2DataBean> list = flowBusinessDefinitionFacadeService.getFlowTaskSelectDataByFlowDefId(flowDefId);
        return ApiResult.success(list);
    }

    /**
     * 根据流程定义ID获取流程流向下拉数据
     *
     * @return
     */
    @GetMapping(value = "/getFlowDirectionSelectDataByFlowDefId/{flowDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取流程流向下拉数据", notes = "根据流程定义ID获取流程流向下拉数据")
    public ApiResult<List<Select2DataBean>> getFlowDirectionSelectDataByFlowDefId(@PathVariable(name = "flowDefId") String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return ApiResult.success(Collections.emptyList());
        }
        List<Select2DataBean> list = flowBusinessDefinitionFacadeService.getFlowDirectionSelectDataByFlowDefId(flowDefId);
        return ApiResult.success(list);
    }

    /**
     * 根据流程定义ID获取表单、环节、流向下拉数据
     *
     * @return
     */
    @GetMapping(value = "/getSelectDataByFlowDefId/{flowDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取表单、环节、流向下拉数据", notes = "根据流程定义ID获取表单、环节、流向下拉数据")
    public ApiResult<Map<String, Object>> getSelectDataByFlowDefId(@PathVariable(name = "flowDefId") String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return ApiResult.success(Collections.emptyMap());
        }
        Map<String, Object> map = Maps.newHashMap();
        List<Select2DataBean> formFields = flowBusinessDefinitionFacadeService.getFormFieldSelectDataByFlowDefId(flowDefId);
        List<Select2DataBean> taskIds = flowBusinessDefinitionFacadeService.getFlowTaskSelectDataByFlowDefId(flowDefId);
        List<Select2DataBean> directions = flowBusinessDefinitionFacadeService.getFlowDirectionSelectDataByFlowDefId(flowDefId);
        map.put("formFields", formFields);
        map.put("taskIds", taskIds);
        map.put("directions", directions);
        return ApiResult.success(map);
    }

    /**
     * 根据流程业务定义ID获取表单、环节、流向下拉数据
     *
     * @return
     */
    @GetMapping(value = "/getSelectDataByFlowBizDefId/{flowBizDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程业务定义ID获取表单、环节、流向下拉数据", notes = "根据流程业务定义ID获取表单、环节、流向下拉数据")
    public ApiResult<Map<String, Object>> getSelectDataByFlowBizDefId(@PathVariable(name = "flowBizDefId") String flowBizDefId) {
        if (StringUtils.isBlank(flowBizDefId)) {
            return ApiResult.success(Collections.emptyMap());
        }
        String flowDefId = flowBusinessDefinitionFacadeService.getFlowDefIdById(flowBizDefId);
        if (StringUtils.isBlank(flowDefId)) {
            return ApiResult.success(Collections.emptyMap());
        }
        Map<String, Object> map = Maps.newHashMap();
        List<Select2DataBean> formFields = flowBusinessDefinitionFacadeService.getFormFieldSelectDataByFlowDefId(flowDefId);
        List<Select2DataBean> taskIds = flowBusinessDefinitionFacadeService.getFlowTaskSelectDataByFlowDefId(flowDefId);
        List<Select2DataBean> directions = flowBusinessDefinitionFacadeService.getFlowDirectionSelectDataByFlowDefId(flowDefId);
        map.put("formFields", formFields);
        map.put("taskIds", taskIds);
        map.put("directions", directions);
        return ApiResult.success(map);
    }

}
