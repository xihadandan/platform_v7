/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizProcessDataDto;
import com.wellsoft.pt.biz.dto.BizProcessDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizBiType;
import com.wellsoft.pt.biz.facade.service.BizProcessFacadeService;
import com.wellsoft.pt.biz.service.BizBusinessIntegrationService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping("/api/biz/process/instance")
public class ApiBizProcessInstanceController extends BaseController {

    @Autowired
    private BizProcessFacadeService bizProcessFacadeService;

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BizBusinessIntegrationService businessIntegrationService;

    /**
     * 获取业务流程实例数据
     *
     * @param processDataRequestParamDto
     * @return
     */
    @PostMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程实例数据", notes = "获取业务流程实例数据")
    public ApiResult<BizProcessDataDto> get(@RequestBody BizProcessDataRequestParamDto processDataRequestParamDto) {
        BizProcessDataDto processDataDto = bizProcessFacadeService.getProcessData(processDataRequestParamDto);
        return ApiResult.success(processDataDto);
    }

    /**
     * 保存业业务流程实例数据
     *
     * @param processDataDto
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业业务流程实例数据", notes = "保存业业务流程实例数据")
    public ApiResult<String> save(@RequestBody BizProcessDataDto processDataDto) {
        String processInstUuid = bizProcessFacadeService.save(processDataDto);
        return ApiResult.success(processInstUuid);
    }

    /**
     * 根据业务流程实例UUID获取过程结点实例列表
     *
     * @param processInstUuid
     * @param loadItemInstCount
     * @return
     */
    @GetMapping(value = "/listProcessNodeInstanceByUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程实例UUID获取过程结点实例列表", notes = "根据业务流程实例UUID获取过程结点实例列表")
    public ApiResult<List<BizProcessNodeInstanceDto>> listProcessNodeInstanceByUuid(@RequestParam(name = "processInstUuid") String processInstUuid,
                                                                                    @RequestParam(name = "loadItemInstCount", required = false, defaultValue = "true") boolean loadItemInstCount) {
        List<BizProcessNodeInstanceDto> processNodeInstanceDtos =
                bizProcessFacadeService.listProcessNodeInstanceByUuid(processInstUuid, loadItemInstCount);
        return ApiResult.success(processNodeInstanceDtos);
    }

    /**
     * @param processDefId
     * @param flowInstUuid
     * @param itemInstUuid
     * @param entityId
     * @return
     */
    @GetMapping(value = "/getEntityFormDataOfMainform", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程实例数据", notes = "获取业务流程实例数据")
    public ApiResult<Map<String, Object>> getEntityFormDataOfMainform(@RequestParam(name = "processDefId", required = false) String processDefId,
                                                                      @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
                                                                      @RequestParam(name = "itemInstUuid", required = false) String itemInstUuid,
                                                                      @RequestParam(name = "entityId") String entityId) {
        ProcessDefinitionJsonParser parser = null;
        if (StringUtils.isNotBlank(itemInstUuid)) {
            BizProcessItemInstanceEntity processItemInstanceEntity = processItemInstanceService.getOne(itemInstUuid);
            parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processItemInstanceEntity.getProcessDefUuid());
        } else if (StringUtils.isNotBlank(flowInstUuid)) {
            BizBusinessIntegrationEntity integrationEntity = businessIntegrationService.getByTypeAndBizInstUuid(EnumBizBiType.Workflow.getValue(), flowInstUuid);
            if (integrationEntity != null) {
                BizProcessItemInstanceEntity processItemInstanceEntity = processItemInstanceService.getOne(integrationEntity.getItemInstUuid());
                parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processItemInstanceEntity.getProcessDefUuid());
            } else if (StringUtils.isNotBlank(processDefId)) {
                parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
            }
        } else {
            parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        }

        ProcessDefinitionJson.ProcessEntityConfig entityConfig = parser.getProcessEntityConfig();
        String entityFormUuid = entityConfig.getFormUuid();
        String entityIdField = entityConfig.getEntityIdField();
        Map<String, Object> mainFormData = bizProcessFacadeService.getEntityFormDataOfMainform(entityFormUuid, entityId, entityIdField);
        return ApiResult.success(mainFormData);
    }

}
