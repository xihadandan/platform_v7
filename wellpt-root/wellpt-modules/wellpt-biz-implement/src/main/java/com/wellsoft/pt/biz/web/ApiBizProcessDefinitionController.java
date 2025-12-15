/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDto;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.facade.service.BizProcessDefinitionFacadeService;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.query.BizProcessDefinitionQueryItem;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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
@Api(tags = "业务流程定义")
@RestController
@RequestMapping("/api/biz/process/definition")
public class ApiBizProcessDefinitionController extends BaseController {

    @Autowired
    private BizProcessDefinitionFacadeService bizProcessDefinitionFacadeService;

    @Autowired
    private BizItemDefinitionService itemDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BizDefinitionTemplateService definitionTemplateService;

    /**
     * 获取业务流程定义
     *
     * @return
     */
    @GetMapping(value = "/get/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程定义", notes = "获取业务流程定义")
    public ApiResult<BizProcessDefinitionDto> get(@PathVariable("uuid") String uuid) {
        BizProcessDefinitionDto dto = bizProcessDefinitionFacadeService.getDto(uuid);
        return ApiResult.success(dto);
    }

    /**
     * 根据ID获取业务流程定义
     *
     * @return
     */
    @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据ID获取业务流程定义", notes = "根据ID获取业务流程定义")
    public ApiResult<BizProcessDefinitionDto> getById(@PathVariable("id") String id) {
        BizProcessDefinitionDto dto = bizProcessDefinitionFacadeService.getDtoById(id);
        return ApiResult.success(dto);
    }

    /**
     * 获取事项配置信息
     *
     * @param processDefId
     * @param processItemIds
     * @return
     */
    @GetMapping(value = "/getProcessItemConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取事项配置信息", notes = "获取事项配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "事项定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<ProcessItemConfig> getProcessItemConfig(@RequestParam(name = "processDefId", required = true) String processDefId,
                                                             @RequestParam(name = "processItemIds", required = true) String processItemIds) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        ProcessItemConfig processItemConfig = null;
        String[] itemIds = StringUtils.split(processItemIds, Separator.SEMICOLON.getValue());
        if (itemIds != null && itemIds.length > 0) {
            processItemConfig = parser.getProcessItemConfigById(itemIds[0]);
        }
        return ApiResult.success(processItemConfig);
    }

    /**
     * 保存业务流程定义
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务流程定义", notes = "保存业务流程定义")
    public ApiResult<Void> save(@RequestBody BizProcessDefinitionDto dto) {
        bizProcessDefinitionFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除业务流程定义
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除业务流程定义", notes = "删除业务流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "业务流程定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        bizProcessDefinitionFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 根据表单定义UUID获取表单定义JSON
     *
     * @return
     */
    @GetMapping(value = "/getFormDefinitionByFormUuid/{formUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据表单定义UUID获取表单定义JSON", notes = "根据表单定义UUID获取表单定义JSON")
    public ApiResult<String> getFormDefinitionByFormUuid(@PathVariable(name = "formUuid") String formUuid) {
        String definitionJson = StringUtils.EMPTY;
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        if (dyFormFormDefinition != null) {
            definitionJson = dyFormFormDefinition.getDefinitionJson();
        }
        return ApiResult.success(definitionJson);
    }

    /**
     * 根据业务事项定义ID获取使用的表单定义JSON
     *
     * @return
     */
    @GetMapping(value = "/getFormDefinitionByItemDefId/{itemDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务事项定义ID获取使用的表单定义JSON", notes = "根据业务事项定义ID获取使用的表单定义JSON")
    public ApiResult<String> getFormDefinitionByItemDefId(@PathVariable(name = "itemDefId") String itemDefId) {
        String definitionJson = StringUtils.EMPTY;
        BizItemDefinitionEntity itemDefinitionEntity = itemDefinitionService.getById(itemDefId);
        if (itemDefinitionEntity != null && StringUtils.isNotBlank(itemDefinitionEntity.getFormId())) {
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(itemDefinitionEntity.getFormId());
            if (dyFormFormDefinition != null) {
                definitionJson = dyFormFormDefinition.getDefinitionJson();
            }
        }
        return ApiResult.success(definitionJson);
    }

    /**
     * 保存业务流程定义配置
     *
     * @param processDefinitionJson
     * @return
     */
    @PostMapping(value = "/config/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务流程定义配置", notes = "保存业务流程定义配置")
    public ApiResult<String> configSave(@RequestBody ProcessDefinitionJson processDefinitionJson) {
        String processDefUuid = bizProcessDefinitionFacadeService.saveProcessDefinitionJson(processDefinitionJson);
        return ApiResult.success(processDefUuid);
    }

    /**
     * 保存业务流程定义配置为新版本
     *
     * @param processDefinitionJson
     * @return
     */
    @PostMapping(value = "/config/saveAsNewVersion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务流程定义配置为新版本", notes = "保存业务流程定义配置为新版本")
    public ApiResult<String> configSaveAsNewVersion(@RequestBody ProcessDefinitionJson processDefinitionJson) {
        String processDefUuid = bizProcessDefinitionFacadeService.saveProcessDefinitionJsonAsNewVersion(processDefinitionJson);
        return ApiResult.success(processDefUuid);
    }

    /**
     * 根据业务流程定义UUID获取过程结点信息
     *
     * @param uuid
     * @return
     */
    @GetMapping(value = "/listProcessNodeItemByUuid/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程定义UUID获取过程结点信息", notes = "根据业务流程定义UUID获取过程结点信息")
    public ApiResult<List<BizProcessNodeDto>> listProcessNodeItemByUuid(@PathVariable("uuid") String uuid) {
        List<BizProcessNodeDto> bizProcessNodeDtos = bizProcessDefinitionFacadeService.listProcessNodeItemByUuid(uuid);
        return ApiResult.success(bizProcessNodeDtos);
    }

    /**
     * 根据业务流程定义ID获取过程结点信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/listProcessNodeItemById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程定义ID获取过程结点信息", notes = "根据业务流程定义ID获取过程结点信息")
    public ApiResult<List<BizProcessNodeDto>> listProcessNodeItemById(@PathVariable("id") String id) {
        List<BizProcessNodeDto> bizProcessNodeDtos = bizProcessDefinitionFacadeService.listProcessNodeItemById(id);
        return ApiResult.success(bizProcessNodeDtos);
    }

    /**
     * 获取业务流程定义模板树
     *
     * @param businessId
     * @param excludeDefId
     * @return
     */
    @GetMapping(value = "/getTemplateTree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程定义ID获取过程结点信息", notes = "根据业务流程定义ID获取过程结点信息")
    public ApiResult<List<TreeNode>> getTemplateTree(@RequestParam("businessId") String businessId, @RequestParam("excludeDefId") String excludeDefId) {
        List<TreeNode> treeNodes = bizProcessDefinitionFacadeService.getTemplateTree(businessId, excludeDefId);
        return ApiResult.success(treeNodes);
    }

    /**
     * 获取业务流程定义模板定义项
     *
     * @param processDefUuid
     * @return
     */
    @GetMapping(value = "/listNodeAndItemTemplateItem", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务流程定义ID获取过程结点信息", notes = "根据业务流程定义ID获取过程结点信息")
    public ApiResult<List<BizDefinitionTemplateQueryItem>> listNodeAndItemTemplateItem(@RequestParam("processDefUuid") String processDefUuid) {
        List<BizDefinitionTemplateQueryItem> templateQueryItems = bizProcessDefinitionFacadeService.listNodeAndItemTemplateItemByUuid(processDefUuid);
        return ApiResult.success(templateQueryItems);
    }

    /**
     * 获取引用指定模板信息的流程定义
     *
     * @param refId
     * @param templateType
     * @param processDefUuid
     * @return
     */
    @GetMapping(value = "/listOfRefTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取引用指定模板信息的流程定义", notes = "获取引用指定模板信息的流程定义")
    public ApiResult<List<BizProcessDefinitionQueryItem>> listOfRefTemplate(@RequestParam("refId") String refId,
                                                                            @RequestParam("templateType") String templateType,
                                                                            @RequestParam("processDefUuid") String processDefUuid) {
        List<BizProcessDefinitionQueryItem> templateQueryItems = bizProcessDefinitionFacadeService.listOfRefTemplate(refId, templateType, processDefUuid);
        return ApiResult.success(templateQueryItems);
    }

    /**
     * 获取业务流程阶段定义信息
     *
     * @param nodeId
     * @param processDefUuid
     * @return
     */
    @GetMapping(value = "/node/{nodeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程阶段定义信息", notes = "获取业务流程阶段定义信息")
    public ApiResult<ProcessNodeConfig> getNodeDefinition(@PathVariable("nodeId") String nodeId, @RequestParam("processDefUuid") String processDefUuid) {
        ProcessNodeConfig processNodeConfig = definitionTemplateService.getOrCreateNodeDefinition(nodeId, processDefUuid);
        return ApiResult.success(processNodeConfig);
    }

    /**
     * 获取业务流程阶段定义信息
     *
     * @param itemId
     * @param processDefUuid
     * @return
     */
    @GetMapping(value = "/item/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务流程阶段定义信息", notes = "获取业务流程阶段定义信息")
    public ApiResult<ProcessItemConfig> getItemDefinition(@PathVariable("itemId") String itemId, @RequestParam("processDefUuid") String processDefUuid) {
        ProcessItemConfig processItemConfig = definitionTemplateService.getOrCreateItemDefinition(itemId, processDefUuid);
        return ApiResult.success(processItemConfig);
    }

}
