/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataRequestParamDto;
import com.wellsoft.pt.biz.facade.service.BizProcessNodeFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Api(tags = "过程节点实例")
@RestController
@RequestMapping("/api/biz/process/node/instance")
public class ApiBizProcessNodeInstanceController extends BaseController {

    @Autowired
    private BizProcessNodeFacadeService bizProcessNodeFacadeService;

    /**
     * 获取过程节点实例数据
     *
     * @param processNodeDataRequestParamDto
     * @return
     */
    @PostMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程实例数据", notes = "获取业务流程实例数据")
    public ApiResult<BizProcessNodeDataDto> get(@RequestBody BizProcessNodeDataRequestParamDto processNodeDataRequestParamDto) {
        BizProcessNodeDataDto processNodeDataDto = bizProcessNodeFacadeService.getProcessNodeData(processNodeDataRequestParamDto);
        return ApiResult.success(processNodeDataDto);
    }

    /**
     * 保存过程节点实例数据
     *
     * @param processNodeDataDto
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存过程节点实例数据", notes = "保存过程节点实例数据")
    public ApiResult<String> save(@RequestBody BizProcessNodeDataDto processNodeDataDto) {
        String processNodeInstUuid = bizProcessNodeFacadeService.save(processNodeDataDto);
        return ApiResult.success(processNodeInstUuid);
    }

    /**
     * 根据过程节点实例UUID获取业务事项实例列表
     *
     * @param processNodeInstUuid
     * @return
     */
    @GetMapping(value = "/listProcessItemInstanceByUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据过程节点实例UUID获取业务事项实例列表", notes = "根据过程节点实例UUID获取业务事项实例列表")
    public ApiResult<List<BizProcessItemInstanceDto>> listProcessNodeInstanceByUuid(@RequestParam(name = "processNodeInstUuid") String processNodeInstUuid) {
        List<BizProcessItemInstanceDto> processItemInstanceDtos =
                bizProcessNodeFacadeService.listProcessItemInstanceByUuid(processNodeInstUuid);
        return ApiResult.success(processItemInstanceDtos);
    }

}
