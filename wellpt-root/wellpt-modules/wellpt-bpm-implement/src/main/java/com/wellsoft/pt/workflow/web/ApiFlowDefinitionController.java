/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.dto.FlowDefinitionDto;
import com.wellsoft.pt.bpm.engine.management.service.IdentityReplaceService;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto;
import com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService;
import com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import com.wellsoft.pt.workflow.work.bean.FlowHandingStateInfoDto;
import com.wellsoft.pt.workflow.work.service.WorkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程定义API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Api(tags = "流程定义")
@Controller
@RequestMapping("/api/workflow/definition")
public class ApiFlowDefinitionController extends BaseController {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowDefinitionFacadeService flowDefinitionFacadeService;

    @Autowired
    private WfFlowDefinitionCleanupConfigFacadeService flowDefinitionCleanupConfigFacadeService;

    @Autowired
    private FlowSchemeService flowSchemeService;

    @Autowired
    private WorkService workService;

    @Autowired
    private FlowDefineService flowDefineService;

    @Autowired
    private IdentityReplaceService identityReplaceService;

    /**
     * 流程定义查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "流程定义查询", notes = "流程定义查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "uuids", value = "流程定义UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNo", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "query", dataType = "int", required = false)})
    public ApiResult<List<FlowDefinitionQueryItem>> query(@RequestParam(name = "keyword") String keyword,
                                                          @RequestParam(name = "uuids", required = false) List<String> uuids,
                                                          @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                          @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(FlowDefinitionQuery.class);
        flowDefinitionQuery.distinctVersion();
        //		flowDefinitionQuery.permission(SpringSecurityUtils.getCurrentUserId(),
        //				Lists.newArrayList(AclPermission.READ, AclPermission.CREATE));
        if (StringUtils.isNotBlank(keyword)) {
            flowDefinitionQuery.nameLike(keyword);
        }
        if (CollectionUtils.isNotEmpty(uuids)) {
            flowDefinitionQuery.uuids(uuids);
        }
        flowDefinitionQuery.system(RequestSystemContextPathResolver.system());
//        flowDefinitionQuery.systemUnitIds(
//                Lists.newArrayList(MultiOrgSystemUnit.PT_ID, SpringSecurityUtils.getCurrentUserUnitId()));
        PagingInfo pagingInfo = new PagingInfo(pageNo, pageSize);
        flowDefinitionQuery.setFirstResult(pagingInfo.getFirst());
        flowDefinitionQuery.setMaxResults(pageSize);
        flowDefinitionQuery.orderByCodeAsc();
        List<FlowDefinitionQueryItem> flowDefinitions = flowDefinitionQuery.list();
        return ApiResult.success(flowDefinitions);
    }

    @ResponseBody
    @PostMapping(value = "/module/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "模块下的流程定义查询", notes = "流程定义查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleIds", value = "模块ID组", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<FlowDefinitionQueryItem>> query(@RequestBody List<String> moduleIds) {
        List<FlowDefinitionQueryItem> list = flowDefineService.queryAllModuleFlowDefs(moduleIds);
        return ApiResult.success(list);
    }

    /**
     * 根据流程定义ID获取总条数
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/countById", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取总条数", notes = "根据流程定义ID获取总条数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Long> countById(@RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowDefinitionService.countById(flowDefId));
    }

    /**
     * 根据流程定义ID获取流程定义信息
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID获取流程定义信息", notes = "根据流程定义ID获取流程定义信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowDefinitionDto> getById(@RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowDefinitionFacadeService.getById(flowDefId));
    }

    /**
     * 复制流程定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/copy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "复制流程定义", notes = "删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "被复制的流程定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "newFlowDefName", value = "新的流程定义名称", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "newFlowDefId", value = "新的流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> copy(@RequestParam(name = "uuid", required = true) String uuid,
                                  @RequestParam(name = "newFlowDefName", required = true) String newFlowDefName,
                                  @RequestParam(name = "newFlowDefId", required = true) String newFlowDefId) {
        return ApiResult.success(flowSchemeService.copy(uuid, newFlowDefName, newFlowDefId));
    }

    /**
     * 单个逻辑删除流程定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/logical/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "单个逻辑删除流程定义", notes = "单个逻辑删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> logicalDelete(@RequestParam(name = "uuid", required = true) String uuid) {
        flowDefinitionFacadeService.logicalDelete(uuid);
        return ApiResult.success();
    }

    /**
     * 批量逻辑删除流程定义
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/logical/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量逻辑删除流程定义", notes = "批量逻辑删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "流程定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> logicalDeleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowDefinitionFacadeService.logicalDeleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 判断是否有启用流程定义定时删除功能
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isEnableCleanup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断是否有启用流程定义定时删除功能", notes = "判断是否有启用流程定义定时删除功能")
    public ApiResult<Boolean> isEnableCleanup() {
        return ApiResult.success(flowDefinitionFacadeService.isEnableCleanup());
    }

    /**
     * 保存流程定义定时清除设置
     *
     * @param flowDefinitionCleanupConfigDto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/cleanup/config/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程定义定时清除设置", notes = "保存流程定义定时清除设置")
    public ApiResult<Void> cleanupConfigSave(
            @RequestBody WfFlowDefinitionCleanupConfigDto flowDefinitionCleanupConfigDto) {
        flowDefinitionCleanupConfigFacadeService.saveDto(flowDefinitionCleanupConfigDto);
        return ApiResult.success();
    }

    /**
     * 获取流程定义定时清除设置
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/cleanup/config/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程定义定时清除设置", notes = "获取流程定义定时清除设置")
    public ApiResult<WfFlowDefinitionCleanupConfigDto> cleanupConfigGet() {
        return ApiResult.success(flowDefinitionCleanupConfigFacadeService.getDto());
    }

    /**
     * 单个恢复流程定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/recovery", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "单个恢复流程定义", notes = "单个恢复流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> recovery(@RequestParam(name = "uuid", required = true) String uuid) {
        flowDefinitionFacadeService.recovery(uuid);
        return ApiResult.success();
    }

    /**
     * 批量恢复流程定义
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/recoveryAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量恢复流程定义", notes = "批量恢复流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "流程定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> recoveryAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowDefinitionFacadeService.recoveryAll(uuids);
        return ApiResult.success();
    }

    /**
     * 单个彻底删除流程定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/physical/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "单个彻底删除流程定义", notes = "单个彻底删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> physicalDelete(@RequestParam(name = "uuid", required = true) String uuid) {
        flowDefinitionFacadeService.physicalDelete(uuid);
        return ApiResult.success();
    }

    /**
     * 批量彻底删除流程定义
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/physical/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量彻底删除流程定义", notes = "批量彻底删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "流程定义UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> physicalDeleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        flowDefinitionFacadeService.physicalDeleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 删除流程定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除流程定义", notes = "删除流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> delete(@RequestParam(name = "uuid", required = true) String uuid) {
        flowDefineService.remove(uuid);
        return ApiResult.success();
    }


    @GetMapping("/updateFlowDefCategory")
    @ResponseBody
    public ApiResult<Boolean> updateFlowDefCategory(@RequestParam String uuid, @RequestParam String categoryUuid) {
        flowDefineService.updateFlowDefCategory(uuid, categoryUuid);
        return ApiResult.success(true);
    }

    /**
     * 查看修改日记
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listLogs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看修改日记", notes = "查看修改日记")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<QueryData> listLogs(@RequestParam(name = "uuid", required = true) String uuid,
                                         HttpServletRequest request) {
        QueryData queryData = identityReplaceService.getUpdateContentLog(new JqGridQueryInfo(), new QueryInfo(), uuid,
                request.getContextPath());
        return ApiResult.success(queryData);
    }

    /**
     * 查看修改日记比较的XML
     *
     * @param flowSchemaLogUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getLogCompareXml", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看修改日记比较的XML", notes = "查看修改日记比较的XML")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getLogCompareXml(
            @RequestParam(name = "flowSchemaLogUuid", required = true) String flowSchemaLogUuid) {
        Map<String, String> result = identityReplaceService.compareXml(flowSchemaLogUuid);
        return ApiResult.success("流程XML:" + result.get("flowXml") + "小版本XML:" + result.get("logXml") + "差异XML：" + result.get("diff"));
    }

    /**
     * 查看修改日记比较的XML
     *
     * @param flowSchemaLogUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getLogDiffXml", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看修改日记比较的XML", notes = "查看修改日记比较的XML")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, String>> getLogDiffXml(
            @RequestParam(name = "flowSchemaLogUuid", required = true) String flowSchemaLogUuid) {
        return ApiResult.success(identityReplaceService.compareXml(flowSchemaLogUuid));
    }

    /**
     * 查看修改日记比较的XML
     *
     * @param flowSchemaLogUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getLogDiff", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看修改日记比较L", notes = "查看修改日记比较")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, String>> getLogDiff(
            @RequestParam(name = "flowSchemaLogUuid", required = true) String flowSchemaLogUuid) {
        return ApiResult.success(identityReplaceService.getLogDiff(flowSchemaLogUuid));
    }

    /**
     * 根据流程定义ID，获取最高版本的流程定义的环节map<id, name>
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFlowTasks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID，获取最高版本的流程定义的环节map<id, name>", notes = "根据流程定义ID，获取最高版本的流程定义的环节map<id, name>")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<KeyValuePair>> getFlowTasks(
            @RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowSchemeService.getFlowTasks(flowDefId));
    }

    /**
     * 根据流程定义ID列表，获取最高版本的流程定义的环节map<flowDefId,list>
     *
     * @param flowDefIds
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFlowTaskMap", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID列表，获取最高版本的流程定义的环节map<flowDefId,list>", notes = "根据流程定义ID列表，获取最高版本的流程定义的环节map<flowDefId,list>")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefIds", value = "流程定义ID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, List<KeyValuePair>>> getFlowTaskMap(
            @RequestParam(name = "flowDefIds", required = true) List<String> flowDefIds) {
        Map<String, List<KeyValuePair>> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(flowDefIds)) {
            flowDefIds.forEach(flowDefId -> {
                result.put(flowDefId, flowSchemeService.getFlowTasks(flowDefId));
            });
        }
        return ApiResult.success(result);
    }

    /**
     * 根据流程定义ID，判断是否自动提交分支流
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isAutoSubmitForkTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID，判断是否自动提交分支流", notes = "根据流程定义ID，判断是否自动提交分支流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isAutoSubmitForkTask(@RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowSchemeService.isAutoSubmitForkTask(flowDefId));
    }

    /**
     * 根据流程定义ID获取总条数
     *
     * @param flowDefUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isExistsUnfinishedFlowInstanceByFlowDefUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义UUID判断是否存在未办结的流程实例", notes = "根据流程定义UUID判断是否存在未办结的流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true),})
    public ApiResult<Boolean> isExistsUnfinishedFlowInstanceByFlowDefUuid(
            @RequestParam(name = "flowDefUuid", required = true) String flowDefUuid) {
        return ApiResult.success(flowSchemeService.isExistsUnfinishedFlowInstanceByFlowDefUuid(flowDefUuid));
    }

    /**
     * 获取流程实例UUID获取流程办理状态信息
     *
     * @param flowInstUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFlowHandingStateInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程实例UUID获取流程办理状态信息", notes = "获取流程实例UUID获取流程办理状态信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),})
    public ApiResult<FlowHandingStateInfoDto> getFlowHandingStateInfo(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        return ApiResult.success(workService.getFlowHandingStateInfo(flowInstUuid));
    }

    /**
     * 获取所有可用的打印模板列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getPrintTemplates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取所有可用的打印模板列表", notes = "获取所有可用的打印模板列表")
    public ApiResult<List<TreeNode>> getPrintTemplates() {
        return ApiResult.success(flowSchemeService.getPrintTemplates(StringUtils.EMPTY));
    }

    /**
     * 获取所有可用的打印模板列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getMessageTemplates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取所有可用的消息模板列表", notes = "获取所有可用的消息模板列表")
    public ApiResult<List<QueryItem>> getMessageTemplates() {
        return ApiResult.success(flowSchemeService.getMessageTemplates());
    }

    /**
     * 获取所有流程消息模板分类
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getSelectFlowMessageTemplateType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取所有流程消息模板分类", notes = "获取所有流程消息模板分类")
    public ApiResult<Select2GroupData> getSelectFlowMessageTemplateType() {
        return ApiResult.success(flowSchemeService.getSelectFlowMessageTemplateType());
    }

    /**
     * 根据流程定义ID，获取流程计时器信息
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listFlowTimerByFlowId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID，获取流程计时器信息", notes = "根据流程定义ID，获取流程计时器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true),})
    public ApiResult<List<Map<String, Object>>> listFlowTimerByFlowId(
            @RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowSchemeService.listFlowTimerByFlowId(flowDefId));
    }

    /**
     * 获取分支流分发的自定义接口
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getCustomDispatcherBranchTaskInterfaces", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取分支流分发的自定义接口", notes = "获取分支流分发的自定义接口")
    public ApiResult<List<TreeNode>> getCustomDispatcherBranchTaskInterfaces() {
        return ApiResult.success(flowSchemeService.getCustomDispatcherBranchTaskInterfaces(StringUtils.EMPTY));
    }

    /**
     * 获取子流程分发的自定义接口
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getSubtaskDispatcherCustomInterfaces", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取子流程分发的自定义接口", notes = "获取子流程分发的自定义接口")
    public ApiResult<List<TreeNode>> getSubtaskDispatcherCustomInterfaces() {
        return ApiResult.success(flowSchemeService.getSubtaskDispatcherCustomInterfaces(StringUtils.EMPTY));
    }

    /**
     * 根据动态表单UUID，获取从表信息
     *
     * @param formUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getSubForms", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据动态表单UUID，获取从表信息", notes = "根据动态表单UUID，获取从表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TreeNode>> getSubForms(@RequestParam(name = "formUuid", required = true) String formUuid) {
        return ApiResult.success(flowSchemeService.getSubForms(StringUtils.EMPTY, formUuid));
    }

    /**
     * 根据动态表单UUID，获取表单字段信息
     *
     * @param formUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFormFields", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据动态表单UUID，获取表单字段信息", notes = "根据动态表单UUID，获取表单字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TreeNode>> getFormFields(@RequestParam(name = "formUuid", required = true) String formUuid) {
        return ApiResult.success(flowSchemeService.getFormFields(StringUtils.EMPTY, formUuid));
    }

    /**
     * 根据流程定义ID，获取表单字段信息
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFormFieldsByFlowDefId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID，获取表单字段信息", notes = "根据流程定义ID，获取表单字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true),})
    public ApiResult<List<TreeNode>> getFormFieldsByFlowDefId(
            @RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowSchemeService.getFormFieldsByFlowDefId(flowDefId));
    }

    /**
     * 根据动态表单UUID，获取表单区块信息
     *
     * @param formUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFormBlocks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据动态表单UUID，获取表单区块信息", notes = "根据动态表单UUID，获取表单区块信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TreeNode>> getFormBlocks(@RequestParam(name = "formUuid", required = true) String formUuid) {
        return ApiResult.success(flowSchemeService.getFormBlocks(StringUtils.EMPTY, formUuid));
    }

    /**
     * 根据流程定义ID，获取表单区块信息
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFormBlocksByFlowDefId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程定义ID，获取表单区块信息", notes = "根据流程定义ID，获取表单区块信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TreeNode>> getFormBlocksByFlowDefId(@RequestParam(name = "flowDefId", required = true) String flowDefId) {
        return ApiResult.success(flowSchemeService.getFormBlocksByFlowDefId(flowDefId));
    }

    /**
     * 根据动态表单UUID，获取表单页签信息
     *
     * @param formUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getFormSubtabs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据动态表单UUID，获取表单页签信息", notes = "根据动态表单UUID，获取表单页签信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TreeNode>> getFormSubtabs(@RequestParam(name = "formUuid", required = true) String formUuid) {
        return ApiResult.success(flowSchemeService.getFormSubtabs(StringUtils.EMPTY, formUuid));
    }

    /**
     * 根据存储单据UUID获取对应的显示单据
     *
     * @param pformUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getVformsByPformUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据存储单据UUID获取对应的显示单据", notes = "根据存储单据UUID获取对应的显示单据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pformUuid", value = "存储单据UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<DataItem>> getVformsByPformUuid(
            @RequestParam(name = "pformUuid", required = true) String pformUuid) {
        return ApiResult.success(flowSchemeService.getVformsByPformUuid(pformUuid));
    }


    /**
     * 根据流程分类UUID列表及流程定义ID列表获取表单定义
     *
     * @param flowCategoryUuids
     * @param flowDefIds
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listFormDefinition", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程分类UUID列表及流程定义ID列表获取表单定义", notes = "根据流程分类UUID列表及流程定义ID列表获取表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowCategoryUuids", value = "流程分类UUID列表", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowDefIds", value = "流程定义ID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<DyFormFormDefinition>> listFormDefinition(
            @RequestParam(name = "flowCategoryUuids", required = true) List<String> flowCategoryUuids,
            @RequestParam(name = "flowDefIds", required = true) List<String> flowDefIds) {
        return ApiResult.success(flowSchemeService.listFormDefinition(flowCategoryUuids, flowDefIds));
    }

    /**
     * 获取业务类别
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getBusinessTypes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取业务类别", notes = "获取业务类别")
    public ApiResult<List<DataItem>> getBusinessTypes() {
        return ApiResult.success(flowSchemeService.getBusinessTypes());
    }

    /**
     * 根据业务类别获取业务角色
     *
     * @param businessType
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getBusinessRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据业务类别获取业务角色", notes = "根据业务类别获取业务角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessType", value = "业务类别UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<DataItem>> getBusinessRoles(
            @RequestParam(value = "businessType", required = true) String businessType) {
        return ApiResult.success(flowSchemeService.getBusinessRoles(businessType));
    }

    /**
     * 检测表达式
     *
     * @param expressionConfig
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkUserCustomExpression", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "检测表达式", notes = "检测表达式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressionConfig", value = "表达式配置", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> checkUserCustomExpression(
            @RequestParam(value = "expressionConfig", required = true) String expressionConfig) {
        return ApiResult.success(flowSchemeService.checkUserCustomExpression(expressionConfig));
    }

    /**
     * 获取集合表达式
     *
     * @param expressionConfig
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getUserCustomExpression", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取集合表达式", notes = "获取集合表达式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressionConfig", value = "表达式配置", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getUserCustomExpression(
            @RequestParam(value = "expressionConfig", required = true) String expressionConfig) {
        return ApiResult.success(flowSchemeService.getUserCustomExpression(expressionConfig));
    }

    /**
     * 获取当前用户所在单位的组织版本
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getCurrentUserUnitOrgVersions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取当前用户所在单位的组织", notes = "获取当前用户所在单位的组织")
    public ApiResult<List<OrganizationDto>> getCurrentUserUnitOrgVersions() {
        return ApiResult.success(flowSchemeService.getCurrentUserUnitOrgVersions());
    }

    @ResponseBody
    @GetMapping(value = "/taskMultiUserSubmitType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> taskMultiUserSubmitType(@RequestParam String taskId, @RequestParam String flowDefinitionUuid) {
        return ApiResult.success(this.flowDefineService.getTaskMultiUserSubmitType(flowDefinitionUuid, taskId));
    }
}
