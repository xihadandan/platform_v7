/*
 * @(#)2020年11月10日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.context.web.converter.MappingCodehausJacksonHttpMessageConverter;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.entity.TaskOperationTemp;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import com.wellsoft.pt.bpm.engine.log.support.FlowOperationLoggerHolder;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceToppingService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.support.groupchat.FlowGroupChatProvider;
import com.wellsoft.pt.bpm.engine.support.groupchat.StartGroupChat;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.jpa.datasource.SelectDatasource;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.*;
import com.wellsoft.pt.workflow.work.service.ListWorkService;
import com.wellsoft.pt.workflow.work.service.WorkService;
import com.wellsoft.pt.workflow.work.util.ConcurrentService;
import com.wellsoft.pt.workflow.work.vo.SubTaskDataVo;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Description: 工作流程api
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年11月10日.1	zhulh		2020年11月10日		Create
 * </pre>
 * @date 2020年11月10日
 */
@Api(tags = "工作流程")
@Controller
@RequestMapping("/api/workflow/work")
public class ApiWorkController extends BaseController {

    /**
     * 流程-流程实例-workbean缓存是否启用
     **/
    private static final String WORKFLOW_WORKBEAN_ENABLE = "workflow_workbean_enable";

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private WorkService workService;

    @Autowired
    private ListWorkService listWorkService;

    @Autowired
    private TaskInstanceToppingService taskInstanceToppingService;

    @Autowired
    private CdDataStoreService cdDataStoreService;
    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private ConcurrentService concurrentService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowService flowService;
    @Autowired
    private FlowLogService flowLogService;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 待办工作数据查询接口
     *
     * @param dataStoreParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/todo/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "待办工作数据查询接口", notes = "待办工作数据查询接口")
    @ApiOperationSupport(order = 10)
    public ApiResult<DataStoreData> todoQuery(@RequestBody FlowDataStoreParams dataStoreParams) {
        // 将流程加载规则作为视图查询参数传入
        FlowLoadingRules flowLoadingRules = dataStoreParams.getLoadingRules();
        if (flowLoadingRules == null) {
            flowLoadingRules = new FlowLoadingRules();
        }
        dataStoreParams.getParams().put("flowLoadingRules", flowLoadingRules);
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        return ApiResult.success(dataStoreData);
    }

    /**
     * 待办工作数据查询总数接口
     *
     * @param dataStoreParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/todo/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "待办工作数据查询总数接口", notes = "待办工作数据查询总数接口")
    @ApiOperationSupport(order = 10)
    public ApiResult<Long> todoCount(@RequestBody FlowDataStoreParams dataStoreParams) {
        // 将流程加载规则作为视图查询参数传入
        long count = cdDataStoreService.loadCount(dataStoreParams);
        return ApiResult.success(count);
    }

    /**
     * 待办工作数据按计时状态分组取总数
     *
     * @param dataStoreParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/todo/groupAndCountByTimingState", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "待办工作数据按计时状态分组取总数", notes = "待办工作数据查询接口")
    @ApiOperationSupport(order = 10)
    public ApiResult<DataStoreData> groupByTimingState(@RequestBody FlowDataStoreParams dataStoreParams) {
        // 将流程加载规则作为视图查询参数传入
        FlowLoadingRules flowLoadingRules = dataStoreParams.getLoadingRules();
        if (flowLoadingRules == null) {
            flowLoadingRules = new FlowLoadingRules();
        }
        dataStoreParams.getParams().put("flowLoadingRules", flowLoadingRules);
        dataStoreParams.getParams().put("groupAndCountByTimingState", true);
        PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        dataStoreParams.setPagingInfo(pagingInfo);
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        List<Map<String, Object>> retDataList = Lists.newArrayList();
        List<Map<String, Object>> dataList = dataStoreData.getData();
        for (Map<String, Object> map : dataList) {
            Map<String, Object> retMap = Maps.newHashMap();
            retMap.put("timingState", map.get("timingState"));
            retMap.put("count", map.get("reservedNumber1"));
            retDataList.add(retMap);
        }
        dataStoreData.setData(retDataList);
        return ApiResult.success(dataStoreData);
    }

    /**
     * 待办工作数据按流程定义ID分组取总数
     *
     * @param dataStoreParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/todo/groupAndCountByFlowDefId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "待办工作数据按流程定义ID分组取总数", notes = "待办工作数据查询接口")
    @ApiOperationSupport(order = 10)
    public ApiResult<DataStoreData> groupByFlowDefId(@RequestBody FlowDataStoreParams dataStoreParams) {
        // 将流程加载规则作为视图查询参数传入
        FlowLoadingRules flowLoadingRules = dataStoreParams.getLoadingRules();
        if (flowLoadingRules == null) {
            flowLoadingRules = new FlowLoadingRules();
        }
        dataStoreParams.getParams().put("flowLoadingRules", flowLoadingRules);
        dataStoreParams.getParams().put("groupAndCountByFlowDefId", true);
        PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        dataStoreParams.setPagingInfo(pagingInfo);
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        List<Map<String, Object>> retDataList = Lists.newArrayList();
        List<Map<String, Object>> dataList = dataStoreData.getData();
        for (Map<String, Object> map : dataList) {
            Map<String, Object> retMap = Maps.newHashMap();
            retMap.put("flowName", map.get("flowName"));
            retMap.put("flowDefId", map.get("reservedText1"));
            retMap.put("count", map.get("reservedNumber1"));
            retDataList.add(retMap);
        }
        dataStoreData.setData(retDataList);
        return ApiResult.success(dataStoreData);
    }

    /**
     * 待办工作数据查询下一笔数据
     *
     * @param dataIndex
     * @param dataStoreParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/todo/next/record", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "待办工作数据查询下一笔数据", notes = "待办工作数据查询下一笔数据")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataIndex", value = "下一笔数据的数据索引，从0开始", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, Object>> nextRecord(@RequestParam(name = "dataIndex", required = true) int dataIndex,
                                                     @RequestBody FlowDataStoreParams dataStoreParams) {
        // 将流程加载规则作为视图查询参数传入
        FlowLoadingRules flowLoadingRules = dataStoreParams.getLoadingRules();
        if (flowLoadingRules == null) {
            flowLoadingRules = new FlowLoadingRules();
        }
        dataStoreParams.getParams().put("flowLoadingRules", flowLoadingRules);
        // 下一笔数据分页信息
        PagingInfo pagingInfo = new PagingInfo(dataIndex + 1, 1, false);
        dataStoreParams.setPagingInfo(pagingInfo);
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        Map<String, Object> record = Maps.newHashMap();
        List<Map<String, Object>> dataList = dataStoreData.getData();
        if (CollectionUtils.isNotEmpty(dataList)) {
            record.putAll(dataList.get(0));
        }
        return ApiResult.success(record);
    }

    /**
     * 获取流程工作数据
     *
     * @param taskInstUuid
     * @param flowDefUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程工作数据", notes = "获取流程工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = false)})
    public ApiResult<WorkBean> getWorkData(@RequestParam(name = "taskInstUuid", required = false) String taskInstUuid,
                                           @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
                                           @RequestParam(name = "flowDefUuid", required = false) String flowDefUuid,
                                           @RequestParam(name = "flowDefId", required = false) String flowDefId,
                                           @RequestParam(name = "formUuid", required = false) String formUuid,
                                           @RequestParam(name = "dataUuid", required = false) String dataUuid,
                                           HttpServletRequest request) {
        WorkBean workBean = null;
        if (StringUtils.isNotBlank(taskInstUuid)) {
            Set<Permission> permissions = taskService.getCurrentUserPermissions(taskInstUuid, flowDefUuid);
            workBean = workService.getByPermissions(taskInstUuid, flowInstUuid, Lists.newArrayList(permissions));
        } else if (StringUtils.isNotBlank(flowInstUuid)) {
            String lastTaskInstUuid = taskService.getLastTaskInstanceUuidByFlowInstUuid(flowInstUuid);
            if (StringUtils.isNotBlank(lastTaskInstUuid)) {
                Set<Permission> permissions = taskService.getCurrentUserPermissions(lastTaskInstUuid, flowDefUuid);
                workBean = workService.getByPermissions(lastTaskInstUuid, flowInstUuid, Lists.newArrayList(permissions));
            } else {
                workBean = workService.getDraft(flowInstUuid);
            }
        } else {
            String system = RequestSystemContextPathResolver.system();
            if (StringUtils.isNotBlank(system) && !SpringSecurityUtils.getAccessableSystem().contains(system)) {
                String errorMsg = AppCodeI18nMessageSource.getMessage("User.AccessSystemForbidden", "pt-usr-app");
                throw new BusinessException(StringUtils.defaultIfBlank(errorMsg, "禁止用户访问系统"));
            }
            if (StringUtils.isNotBlank(flowDefUuid)) {
                workBean = workService.newWork(flowDefUuid);
            } else {
                workBean = workService.newWorkById(flowDefId);
            }
            if (StringUtils.isNotBlank(formUuid)) {
                workBean.setFormUuid(formUuid);
                if (StringUtils.isNotBlank(dataUuid)) {
                    workBean.setDataUuid(dataUuid);
                }
            }
        }
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程工作数据
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getWorkDataByWorkBean", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程工作数据", notes = "获取流程工作数据")
    @ApiOperationSupport(order = 30)
    public ApiResult<WorkBean> getWorkDataByWorkBean(@RequestBody WorkBean workBean, HttpServletRequest request) {
        try {
            WorkBean workBeanNew = workService.getWorkData(workBean);
            flowLogService.logReader(workBeanNew, SpringSecurityUtils.getCurrentUser(), request);
            return ApiResult.success(workBeanNew);
        } catch (Exception e) {
            logger.error("获取流程工作数据异常：", e);
            return ApiResult.fail("获取流程工作数据异常");
        }
    }

    /**
     * 获取流程待办工作数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getTodoWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程待办工作数据", notes = "获取流程待办工作数据")
    @ApiOperationSupport(order = 30)
    public ApiResult<WorkBean> getTodoWorkData(@RequestBody WorkGetTodoWorkDataRequest request, HttpServletRequest servletRequest) {
        WorkBean workBean = null;
        if (StringUtils.isNotBlank(request.getTaskInstUuid())) {
            workBean = workService.getTodo(request.getTaskInstUuid(), null);
            if (StringUtils.isNotBlank(request.getSameUserSubmitTaskOperationUuid())) {
                TaskOperation taskOperation = workService.getTaskOperation(request.getSameUserSubmitTaskOperationUuid());
                workBean.setOpinionText(taskOperation.getOpinionText());
                workBean.setOpinionLabel(taskOperation.getOpinionLabel());
                workBean.setOpinionValue(taskOperation.getOpinionValue());
                if (StringUtils.isNotBlank(taskOperation.getOpinionFileIds())) {
                    workBean.setOpinionFiles(mongoFileService.getNonioFilesFromFolder(taskOperation.getFlowInstUuid(), taskOperation.getUuid()));
                }
            }
        } else {
            workBean = workService.newWork(request.getFlowDefUuid());
        }
        workBean.setLoadDyFormData(request.isLoadDyFormData());
        workBean = workService.getWorkData(workBean);
        if (request.isLoadDyFormData()) {
            workService.fillTaskTodoTempIfRequired(workBean);
        }
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), servletRequest);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程已办工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getDoneWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程已办工作数据", notes = "获取流程已办工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getDoneWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "taskIdentityUuid", required = false) String taskIdentityUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getDone(taskInstUuid, taskIdentityUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程办结工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getOverWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办结工作数据", notes = "获取流程办结工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getOverWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getOver(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程未阅工作数据
     *
     * @param taskInstUuid
     * @param openToRead
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getUnreadWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程未阅工作数据", notes = "获取流程未阅工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "openToRead", value = "打开单据后是否标记为已读，默认为true", paramType = "query", dataType = "String", required = false)})
    public ApiResult<WorkBean> getUnreadWorkData(
            @RequestParam(name = "taskInstUuid", required = false) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            @RequestParam(name = "openToRead", required = true, defaultValue = "true") boolean openToRead,
            HttpServletRequest request) {
        WorkBean workBean = workService.getUnread(taskInstUuid, flowInstUuid, openToRead);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程己阅工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getReadWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程己阅工作数据", notes = "获取流程己阅工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getReadWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getRead(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程关注工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getAttentionWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程关注工作数据", notes = "获取流程关注工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getAttentionWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getAttention(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程督办工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getSuperviseWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程督办工作数据", notes = "获取流程督办工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getSuperviseWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getSupervise(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程监控工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getMonitorWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程监控工作数据", notes = "获取流程监控工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getMonitorWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getMonitor(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 获取流程查阅的工作数据
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getViewerWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程查阅的工作数据", notes = "获取流程查阅的工作数据")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkBean> getViewerWorkData(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid,
            @RequestParam(name = "flowInstUuid", required = false) String flowInstUuid,
            HttpServletRequest request) {
        WorkBean workBean = workService.getViewer(taskInstUuid, flowInstUuid);
        workBean = workService.getWorkData(workBean);
        flowLogService.logReader(workBean, SpringSecurityUtils.getCurrentUser(), request);
        return ApiResult.success(workBean);
    }

    /**
     * 稍后处理接口
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @PostMapping(value = {"/dealLater", "/todo/dealLater"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "稍后处理接口", notes = "稍后处理接口")
    @ApiOperationSupport(order = 40)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> dealLater(@RequestParam(name = "taskInstUuid", required = true) String taskInstUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        taskInstanceToppingService.increaseLowPriorityByTaskInstUuidAndUserId(taskInstUuid, userId);
        return ApiResult.success();
    }

    /**
     * 保存流程
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程", notes = "保存流程")
    @ApiOperationSupport(order = 50)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Map<String, String>> save(@RequestBody WorkBean workBean) {
        if (StringUtils.isNotBlank(workBean.getTaskInstUuid())) {
            return ApiResult.success(workService.saveTemp(workBean));
        } else {
            return ApiResult.success(workService.save(workBean));
        }
    }

    /**
     * 是否允许提交流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedSubmit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许提交流程", notes = "是否允许提交流程")
    @ApiOperationSupport(order = 110)
    public ApiResult<Boolean> isAllowedSubmit(@RequestBody WorkIsAllowedSubmitRequest request) {
        return ApiResult.success(listWorkService.isAllowedSubmit(request.getTaskInstUuids()));
    }

    /**
     * 提交流程
     *
     * @param requestBody
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "提交流程", notes = "提交流程")
    @ApiOperationSupport(order = 60)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ResultMessage submit(@RequestBody String requestBody, HttpServletRequest request) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkBean workBean = convertWorkBean(requestBody, WorkBean.class);
            ResultMessage resultMessage = workService.submit(workBean);

            FlowOperationLoggerHolder.commit(resultMessage.getData(), request);
            return resultMessage;
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
    }

    /**
     * @param requestBody
     * @return
     */
    private <T extends Object> T convertWorkBean(String requestBody, Class<T> clazz) {
        try {
            List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
            for (HttpMessageConverter<?> converter : converters) {
                if (converter instanceof MappingCodehausJacksonHttpMessageConverter) {
                    return ((MappingCodehausJacksonHttpMessageConverter) converter).getObjectMapper().readValue(requestBody, clazz);
                }
            }
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("JSON parse error: " + e.getMessage(), e);
        }
        return JsonUtils.json2Object(requestBody, clazz);
    }

    /**
     * 完成流程环节
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "完成流程环节", notes = "完成流程环节")
    @ApiOperationSupport(order = 60)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ResultMessage complete(@RequestBody WorkBean workBean) {
        return workService.complete(workBean);
    }

    /**
     * 是否需要提交意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredSubmitOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要提交意见", notes = "是否需要提交意见")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredSubmitOpinion(@RequestBody WorkIsRequiredSubmitOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredSignOpinion(request.getTaskInstUuids()));
    }

    /**
     * 必填字段是否为空
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/requiredFieldIsBlank", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "必填字段是否为空", notes = "必填字段是否为空")
    @ApiOperationSupport(order = 200)
    public ApiResult<Boolean> requiredFieldIsBlank(@RequestBody WorkBean workBean) {
        return ApiResult.success(workService.requiredFieldIsBlank(workBean));
    }

    /**
     * 是否允许直接退回流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedDirectRollback", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许直接退回流程", notes = "是否允许直接退回流程")
    @ApiOperationSupport(order = 110)
    public ApiResult<Boolean> isAllowedDirectRollback(@RequestBody WorkIsAllowedDirectRollbackRequest request) {
        return ApiResult.success(listWorkService.isAllowedDirectRollback(request.getTaskInstUuids()));
    }

    /**
     * 退回流程
     *
     * @param requestBody
     * @param servletRequest
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/rollback", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "退回流程", notes = "退回流程")
    @ApiOperationSupport(order = 70)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ResultMessage rollback(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkBean workBean = convertWorkBean(requestBody, WorkBean.class);
            ResultMessage resultMessage = workService.rollbackWithWorkData(workBean);

            FlowOperationLoggerHolder.commit(resultMessage.getData(), servletRequest);
            return resultMessage;
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
    }

    /**
     * 是否需要退回意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredRollbackOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要退回意见", notes = "是否需要退回意见")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredRollbackOpinion(@RequestBody WorkIsRequiredRollbackOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredRollbackOpinion(request.getTaskInstUuids()));
    }

    /**
     * 退回主流程
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/rollbackToMainFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "退回主流程", notes = "退回主流程")
    @ApiOperationSupport(order = 80)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ResultMessage rollbackToMainFlow(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkBean workBean = convertWorkBean(requestBody, WorkBean.class);
            ResultMessage resultMessage = workService.rollbackToMainFlowWithWorkData(workBean);

            FlowOperationLoggerHolder.commit(resultMessage.getData(), servletRequest);
            return resultMessage;
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
    }

    /**
     * 是否允许撤回流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedCancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许撤回流程", notes = "是否允许撤回流程")
    @ApiOperationSupport(order = 90)
    public ApiResult<Boolean> isAllowedCancel(@RequestBody WorkIsAllowedCancelRequest request) {
        return ApiResult.success(listWorkService.isAllowedCancel(request.getTaskInstUuids()));
    }

    /**
     * 是否需要撤回意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredCancelOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要撤回意见", notes = "是否需要撤回意见")
    @ApiOperationSupport(order = 90)
    public ApiResult<Boolean> isRequiredCancelOpinion(@RequestBody WorkIsRequiredCancelOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredCancelOpinion(request.getTaskInstUuids()));
    }

    /**
     * 撤回流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "撤回流程", notes = "撤回流程")
    @ApiOperationSupport(order = 90)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> cancel(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkCancelRequest request = convertWorkBean(requestBody, WorkCancelRequest.class);
            workService.cancelWithOpinion(request.getTaskInstUuids(), request.getOpinionText());

            FlowOperationLoggerHolder.commit(null, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success();
    }

    /**
     * 撤回流程
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/cancelWithWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "撤回流程", notes = "撤回流程")
    @ApiOperationSupport(order = 90)
    public ApiResult<Void> cancelWithWorkData(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkBean workBean = convertWorkBean(requestBody, WorkBean.class);
            workService.cancelWithWorkData(workBean);

            FlowOperationLoggerHolder.commit(null, servletRequest);
            return ApiResult.success();
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
    }

    /**
     * 根据流程实例获取待办环节信息
     *
     * @param flowInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getTodoTaskByFlowInstUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程实例获取待办环节信息", notes = "根据流程实例获取待办环节信息")
    @ApiOperationSupport(order = 100)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TaskInstance> getTodoTaskByFlowInstUuid(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        return ApiResult.success(workService.getTodoTaskByFlowInstUuid(flowInstUuid));
    }

    /**
     * 是否允许转办流程
     *
     * @param taskInstUuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedTransfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许转办流程", notes = "是否允许转办流程")
    @ApiOperationSupport(order = 110)
    public ApiResult<Boolean> isAllowedTransfer(@RequestBody WorkIsAllowedTransferRequest request) {
        return ApiResult.success(listWorkService.isAllowedTransfer(request.getTaskInstUuids()));
    }

    /**
     * 转办流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "转办流程", notes = "转办流程")
    @ApiOperationSupport(order = 110)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<SubmitResult> transfer(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        SubmitResult result = new SubmitResult();
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkTransferRequest request = convertWorkBean(requestBody, WorkTransferRequest.class);
            WorkBean workBean = request.getWorkBean();
            if (workBean != null) {
                workService.transferWithWorkData(workBean, request.getUserIds());
            } else {
                workService.transfer(request.getTaskInstUuids(), request.getUserIds(), request.getOpinionLabel(),
                        request.getOpinionValue(), request.getOpinionText(), request.getOpinionFiles());
            }
            result.setFormDataUpdated(FormDataHandler.isUpdated());

            FlowOperationLoggerHolder.commit(result, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success(result);
    }

    /**
     * 是否需要转办意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredTransferOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要转办意见", notes = "是否需要转办意见")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredTransferOpinion(@RequestBody WorkIsRequiredHandOverOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredTransferOpinion(request.getTaskInstUuids()));
    }

    /**
     * 是否允许会签流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedCounterSign", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许会签流程", notes = "是否允许会签流程")
    @ApiOperationSupport(order = 110)
    public ApiResult<Boolean> isAllowedCounterSign(@RequestBody WorkIsAllowedTransferRequest request) {
        return ApiResult.success(listWorkService.isAllowedCounterSign(request.getTaskInstUuids()));
    }

    /**
     * 会签流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/counterSign", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "会签流程", notes = "会签流程")
    @ApiOperationSupport(order = 120)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<SubmitResult> counterSign(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        SubmitResult result = new SubmitResult();
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkCounterSignRequest request = convertWorkBean(requestBody, WorkCounterSignRequest.class);
            WorkBean workBean = request.getWorkBean();
            if (workBean != null) {
                workService.counterSignWithWorkData(workBean, request.getUserIds());
            } else {
                workService.counterSign(request.getTaskInstUuids(), request.getUserIds(), StringUtils.EMPTY,
                        StringUtils.EMPTY, request.getOpinionText(), request.getOpinionFiles());
            }
            result.setFormDataUpdated(FormDataHandler.isUpdated());

            FlowOperationLoggerHolder.commit(result, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success(result);
    }

    /**
     * 加签流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/addSign", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "加签流程", notes = "加签流程")
    @ApiOperationSupport(order = 130)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<SubmitResult> addSign(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        SubmitResult result = new SubmitResult();
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkCounterSignRequest request = convertWorkBean(requestBody, WorkCounterSignRequest.class);
            WorkBean workBean = request.getWorkBean();
            workService.addSignWithWorkData(workBean, request.getUserIds());
            result.setFormDataUpdated(FormDataHandler.isUpdated());

            FlowOperationLoggerHolder.commit(result, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success(result);
    }

    /**
     * 是否需要会签意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredCounterSignOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要会签意见", notes = "是否需要会签意见")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredCounterSignOpinion(@RequestBody WorkCounterSignRequest request) {
        return ApiResult.success(listWorkService.isRequiredCounterSignOpinion(request.getTaskInstUuids()));
    }

    /**
     * 抄送流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/copyTo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "抄送流程", notes = "抄送流程")
    @ApiOperationSupport(order = 130)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> copyTo(@RequestBody WorkCopyToRequest request) {
        workService.copyTo(request.getTaskInstUuids(), request.getUserIds(), request.getAclRole());
        return ApiResult.success();
    }

    /**
     * 标记已阅
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/markRead", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "标记已阅", notes = "标记已阅")
    @ApiOperationSupport(order = 131)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> markRead(@RequestBody WorkMarkReadRequest request) {
        workService.markRead(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 标记未阅
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/markUnread", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "标记未阅", notes = "标记未阅")
    @ApiOperationSupport(order = 131)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> markUnread(@RequestBody WorkMarkUnreadRequest request) {
        workService.markUnread(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 关注流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/attention", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "关注流程", notes = "关注流程")
    @ApiOperationSupport(order = 140)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> attention(@RequestBody WorkAttentionRequest request) {
        workService.attention(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 取消关注流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/unfollow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "取消关注流程", notes = "取消关注流程")
    @ApiOperationSupport(order = 150)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> unfollow(@RequestBody WorkUnfollowRequest request) {
        workService.unfollow(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 催办流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/remind", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "催办流程", notes = "催办流程")
    @ApiOperationSupport(order = 160)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "opinionText", value = "催办意见", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> remind(@RequestBody WorkRemindRequest request) {
        workService.remind(request.getTaskInstUuids(), request.getOpinionLabel(), request.getOpinionValue(),
                request.getOpinionText(), request.getOpinionFiles());
        return ApiResult.success();
    }

    /**
     * 获取流程配置的套打模板
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getPrintTemplates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程配置的套打模板", notes = "获取流程配置的套打模板")
    @ApiOperationSupport(order = 170)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<List<PrintTemplate>> getPrintTemplates(@RequestBody WorkGetPrintTemplateRequest request) {
        return ApiResult.success(workService.getPrintTemplates(request.getTaskInstUuid(), request.getFlowInstUuid()));
    }

    /**
     * 获取流程配置的套打模板
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getPrintTemplateTree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程配置的套打模板（树）", notes = "获取流程配置的套打模板（树）")
    @ApiOperationSupport(order = 170)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<List<TreeNode>> getPrintTemplateTree(@RequestBody WorkGetPrintTemplateRequest request) {
        return ApiResult
                .success(workService.getPrintTemplatesTree(request.getTaskInstUuid(), request.getFlowInstUuid()));
    }

    /**
     * 套打流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/print", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "套打流程", notes = "套打流程")
    @ApiOperationSupport(order = 180)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<LogicFileInfo> print(@RequestBody WorkPrintRequest request) {
        if (StringUtils.isNotBlank(request.getPrintTemplateUuid())
                && StringUtils.isNotBlank(request.getPrintTemplateLang())) {
            return ApiResult.success(workService.print(request.getTaskInstUuid(), request.getPrintTemplateId(),
                    request.getPrintTemplateUuid(), request.getPrintTemplateLang()));
        } else {
            return ApiResult.success(workService.print(request.getTaskInstUuid(), request.getPrintTemplateId()));
        }
    }

    /**
     * 获取流程配置的表单字段
     *
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getDyformFileFieldDefinitions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程配置的表单字段", notes = "获取流程配置的表单字段")
    @ApiOperationSupport(order = 190)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<DyformFieldDefinition>> getDyformFileFieldDefinitions(
            @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid) {
        return ApiResult.success(workService.getDyformFileFieldDefinitions(taskInstUuid));
    }

    /**
     * 是否允许特送个人
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedHandOver", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许特送个人", notes = "是否允许特送个人")
    @ApiOperationSupport(order = 200)
    public ApiResult<Boolean> isAllowedHandOver(@RequestBody WorkIsAllowedHandOverRequest request) {
        return ApiResult.success(listWorkService.isAllowedHandOver(request.getTaskInstUuids()));
    }

    /**
     * 是否需要特送个人意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredHandOverOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要特送个人意见", notes = "是否需要特送个人意见")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredHandOverOpinion(@RequestBody WorkIsRequiredHandOverOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredHandOverOpinion(request.getTaskInstUuids()));
    }

    /**
     * 特送个人
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/handOver", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "特送个人", notes = "特送个人")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> handOver(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkHandOverRequest request = convertWorkBean(requestBody, WorkHandOverRequest.class);
            listWorkService.handOver(request.getTaskInstUuids(), request.getUserIds(), request.getOpinionLabel(),
                    request.getOpinionValue(), request.getOpinionText(), request.getOpinionFiles());

            FlowOperationLoggerHolder.commit(null, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success();
    }

    /**
     * 是否允许环节跳转
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isAllowedGotoTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否允许环节跳转", notes = "是否允许环节跳转")
    @ApiOperationSupport(order = 210)
    public ResultMessage isAllowedGotoTask(@RequestBody WorkIsAllowedGotoTaskRequest request) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            resultMessage.setData(listWorkService.isAllowedGotoTask(request.getTaskInstUuids()));
            // listWorkService.checkGotoTasks(request.getTaskInstUuids());
        } catch (Exception e) {
            resultMessage.clear();
            resultMessage.addMessage(e.getMessage());
            resultMessage.setCode(-1);
            resultMessage.setData(false);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    /**
     * 是否需要环节跳转意见
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isRequiredGotoTaskOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "是否需要环节跳转意见", notes = "是否需要环节跳转意见")
    @ApiOperationSupport(order = 210)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isRequiredGotoTaskOpinion(@RequestBody WorkIsRequiredGotoTaskOpinionRequest request) {
        return ApiResult.success(listWorkService.isRequiredGotoTaskOpinion(request.getTaskInstUuids()));
    }

    /**
     * 环节跳转
     *
     * @param taskInstUuids
     * @param gotoTaskId
     * @param userIds
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @PostMapping(value = "/gotoTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "环节跳转", notes = "环节跳转")
    @ApiOperationSupport(order = 210)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "gotoTaskId", value = "跳转的目标环节ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "userIds", value = "跳转的目标环节办理人ID，多个以逗号隔开", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> gotoTask(@RequestParam(name = "taskInstUuids", required = true) List<String> taskInstUuids,
                                    @RequestParam(name = "gotoTaskId", required = true) String gotoTaskId,
                                    @RequestParam(name = "userIds", required = true) List<String> userIds, HttpServletRequest servletRequest) {
        try {
            Map<String, Object> params = Maps.newHashMap();
            params.put("taskInstUuids", taskInstUuids);
            params.put("gotoTaskId", gotoTaskId);
            params.put("userIds", userIds);
            FlowOperationLoggerHolder.create(JsonUtils.object2Json(params));
            listWorkService.gotoTasks(taskInstUuids, gotoTaskId, userIds);

            FlowOperationLoggerHolder.commit(null, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success();
    }

    /**
     * 环节跳转
     *
     * @param workBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/gotoTaskWithWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "环节跳转", notes = "环节跳转")
    @ApiOperationSupport(order = 210)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> gotoTaskWithWorkData(@RequestBody String requestBody, HttpServletRequest servletRequest) {
        try {
            FlowOperationLoggerHolder.create(requestBody);
            WorkBean workBean = convertWorkBean(requestBody, WorkBean.class);
            workService.gotoTask(workBean);

            FlowOperationLoggerHolder.commit(null, servletRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            FlowOperationLoggerHolder.clear();
        }
        return ApiResult.success();
    }

    /**
     * 挂起流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/suspend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "挂起流程", notes = "挂起流程")
    @ApiOperationSupport(order = 220)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> suspend(@RequestBody WorkSuspendRequest request) {
        workService.suspend(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 恢复流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/resume", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "恢复流程", notes = "恢复流程")
    @ApiOperationSupport(order = 221)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> resume(@RequestBody WorkResumeRequest request) {
        workService.resume(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 置顶流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/topping", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "置顶流程", notes = "置顶流程")
    @ApiOperationSupport(order = 230)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> topping(@RequestBody WorkToppingRequest request) {
        workService.topping(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 取消置顶流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/untopping", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "取消置顶流程", notes = "取消置顶流程")
    @ApiOperationSupport(order = 230)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> untopping(@RequestBody WorkUntoppingRequest request) {
        workService.untopping(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 删除草稿流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除草稿流程", notes = "删除草稿流程")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> deleteDraft(@RequestBody WorkDraftDeleteRequest request) {
        workService.deleteDraft(request.getFlowInstUuids());
        return ApiResult.success();
    }

    /**
     * 删除流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除流程", notes = "删除流程")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> delete(@RequestBody WorkDeleteRequest request) {
        workService.delete(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 管理员删除流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteByAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "管理员删除流程", notes = "管理员删除流程")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> deleteByAdmin(@RequestBody WorkDeleteRequest request) {
        workService.deleteByAdmin(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 管理员删除流程-假删除
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/logicalDeleteByAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "管理员逻辑删除流程", notes = "管理员逻辑删除流程")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> logicalDeleteByAdmin(@RequestBody WorkDeleteRequest request) {
        workService.logicalDeleteByAdmin(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 恢复流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/recover", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "恢复流程", notes = "恢复流程")
    @ApiOperationSupport(order = 241)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isMobileApp", value = "是否移动端应用true/false", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> recover(@RequestBody WorkDeleteRequest request) {
        workService.recover(request.getTaskInstUuids());
        return ApiResult.success();
    }

    /**
     * 根据操作ID获取操作提示信息
     *
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getActionTipWithActionId/{actionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据操作ID获取操作提示信息", notes = "根据操作ID获取操作提示信息")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionId", value = "操作ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "actionCode", value = "操作编码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskId", value = "环节ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "toTaskId", value = "提交到的环节ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskIdentityUuid", value = "环节待办标识", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, Object>> getActionTipWithActionId(@PathVariable(name = "actionId") String actionId,
                                                                   @RequestParam(name = "actionCode") String actionCode,
                                                                   @RequestParam(name = "taskInstUuid") String taskInstUuid,
                                                                   @RequestParam(name = "taskId") String taskId,
                                                                   @RequestParam(name = "toTaskId", required = false) String toTaskId,
                                                                   @RequestParam(name = "taskIdentityUuid") String taskIdentityUuid,
                                                                   @RequestParam(name = "flowInstUuid") String flowInstUuid,
                                                                   @RequestParam(name = "flowDefUuid") String flowDefUuid) {
        // 送结束环节ID编码判断处理
        String submitToTaskId = toTaskId;
        if (StringUtils.contains(submitToTaskId, "%")) {
            try {
                submitToTaskId = URLDecoder.decode(submitToTaskId, Encoding.UTF8.getValue());
            } catch (UnsupportedEncodingException e) {
            }
        }
        Map<String, Object> actionTip = workService.getActionTipWithActionId(actionId, actionCode, taskInstUuid, taskId, submitToTaskId, taskIdentityUuid, flowInstUuid, flowDefUuid);
        return ApiResult.success(actionTip);
    }


    /**
     * 根据环节实例UUID获取环节的所有者名称
     *
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getTaskOwnerNames/{taskInstUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据环节实例UUID获取环节的所有者名称", notes = "根据环节实例UUID获取环节的所有者名称")
    @ApiOperationSupport(order = 240)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<String>> getTaskOwnerNames(@PathVariable(name = "taskInstUuid") String taskInstUuid) {
        Set<String> ownerIds = taskService.getTaskOwners(taskInstUuid);
        String ownerNames = IdentityResolverStrategy.resolveAsNames(Lists.newArrayList(ownerIds));
        return ApiResult.success(Arrays.asList(StringUtils.split(ownerNames, Separator.SEMICOLON.getValue())));
    }

    /**
     * 根据流程环节实例UUID、流程定义ID获取环节办理信息
     *
     * @param taskInstUuid
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @SelectDatasource
    @GetMapping(value = "/getTaskProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取环节办理信息", notes = "根据流程环节实例UUID、流程定义ID获取环节办理信息")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Map<String, Map<String, Object>>> getTaskProcess(
            @RequestParam(name = "taskInstUuid", required = false) String taskInstUuid,
            @RequestParam(name = "flowDefId") String flowDefId) {
        try {
            Map<String, Map<String, Object>> taskProcesses = workService.getTaskProcessByTaskInstUuidAndFlowDefId(taskInstUuid, flowDefId);
            return ApiResult.success(taskProcesses);
        } catch (Exception e) {
            logger.error("获取环节办理信息异常：", e);
            return ApiResult.fail("获取环节办理信息异常");
        }
    }

    /**
     * 根据流程实例UUID获取流程办理过程
     *
     * @param flowInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getWorkProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办理过程", notes = "根据流程实例UUID获取流程办理过程")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)
    public ApiResult<List<WorkProcessBean>> getWorkProcess(@RequestParam(name = "flowInstUuid") String flowInstUuid) {
        List<WorkProcessBean> workProcessBeans = workService.getWorkProcess(flowInstUuid);
        return ApiResult.success(workProcessBeans);
    }

    /**
     * 根据流程实例UUID获取流程办理过程和流程意见立场配置信息
     *
     * @param flowInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getWorkProcessAndOpinionPositionConfigs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办理过程和流程意见立场配置信息", notes = "根据流程实例UUID获取流程办理过程和流程意见立场配置信息")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)
    public ApiResult<Map<String, Object>> getWorkProcessAndOpinionPositionConfigs(@RequestParam(name = "flowInstUuid") String flowInstUuid) {
        Map<String, Object> map = Maps.newHashMap();
        List<WorkProcessBean> workProcessBeans = workService.getWorkProcess(flowInstUuid);
        List<TaskOpinionPositionConfig> opinionPositionConfig = workService
                .getOpinionPositionConfigsByFlowInstUuid(flowInstUuid);
        map.put("workProcesses", workProcessBeans);
        map.put("opinionPositionConfig", opinionPositionConfig);
        return ApiResult.success(map);
    }

    /**
     * 根据流程实例UUID获取流程办理过程和流程意见立场配置信息
     *
     * @param flowInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getWorkProcessAndOpinionPositionConfigs/{flowInstUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办理过程和流程意见立场配置信息", notes = "根据流程实例UUID获取流程办理过程和流程意见立场配置信息")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)
    public ApiResult<Map<String, Object>> getWorkProcessAndOpinionPositionConfigsByPath(@PathVariable(name = "flowInstUuid") String flowInstUuid) {
        Map<String, Object> map = Maps.newHashMap();
        List<WorkProcessBean> workProcessBeans = workService.getWorkProcess(flowInstUuid);
        List<TaskOpinionPositionConfig> opinionPositionConfig = workService
                .getOpinionPositionConfigsByFlowInstUuid(flowInstUuid);
        map.put("workProcesses", workProcessBeans);
        map.put("opinionPositionConfig", opinionPositionConfig);
        return ApiResult.success(map);
    }

    /**
     * 根据表单定义UUID、数据UUID获取流程办理过程和流程意见立场配置信息
     *
     * @param dataUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkProcessAndOpinionPositionConfigsByFormDataUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办理过程和流程意见立场配置信息", notes = "根据表单定义UUID、数据UUID获取流程办理过程和流程意见立场配置信息")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = true)
    public ApiResult<Map<String, Object>> getWorkProcessAndOpinionPositionConfigsByFormDataUuid(@RequestParam(name = "dataUuid") String dataUuid) {
        Map<String, Object> map = Maps.newHashMap();
        List<WorkProcessBean> workProcessBeans = Lists.newArrayList();
        List<TaskOpinionPositionConfig> opinionPositionConfig = Lists.newArrayList();
        if (StringUtils.isNotBlank(dataUuid)) {
            String flowInstUuid = flowService.getFlowInstUuidByFormDataUuid(dataUuid);
            if (StringUtils.isNotBlank(flowInstUuid)) {
                workProcessBeans = workService.getWorkProcess(flowInstUuid);
                opinionPositionConfig = workService.getOpinionPositionConfigsByFlowInstUuid(flowInstUuid);
            }
        }
        map.put("workProcesses", workProcessBeans);
        map.put("opinionPositionConfig", opinionPositionConfig);
        return ApiResult.success(map);
    }

    /**
     * 根据流程实例UUID列表获取流程办理过程
     *
     * @param flowInstUuids
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getWorkProcesses", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程办理过程", notes = "根据流程实例UUID列表获取流程办理过程")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "flowInstUuids", value = "流程实例UUID列表", paramType = "query", dataType = "String", required = true)
    public ApiResult<Map<String, List<WorkProcessBean>>> getWorkProcesses(@RequestParam(name = "flowInstUuids") List<String> flowInstUuids) {
        Map<String, List<WorkProcessBean>> workProcessBeanMap = workService.getWorkProcesses(flowInstUuids);
        return ApiResult.success(workProcessBeanMap);
    }

    /**
     * 根据流程实例UUID获取数据快照列表
     *
     * @param flowInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/listFlowDataSnapshotWithoutDyformDataByFlowInstUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取数据快照列表", notes = "根据流程实例UUID获取数据快照列表")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)
    public ApiResult<List<FlowDataSnapshot>> listFlowDataSnapshotWithoutDyformDataByFlowInstUuid(@RequestParam(name = "flowInstUuid") String flowInstUuid) {
        List<FlowDataSnapshot> flowDataSnapshots = workService
                .listFlowDataSnapshotWithoutDyformDataByFlowInstUuid(flowInstUuid);
        return ApiResult.success(flowDataSnapshots);
    }

    /**
     * 根据流程实例UUID获取数据快照列表
     *
     * @param request
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @PostMapping(value = "/getFlowDataSnapshotByIds", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取数据快照列表", notes = "根据流程实例UUID获取数据快照列表")
    @ApiOperationSupport(order = 250)
    public ApiResult<List<String>> getFlowDataSnapshotByIds(@RequestBody WorkGetFlowDataSnapshotRequest request) {
        List<String> flowDataSnapshots = workService
                .getFlowDataSnapshotByIds(request.getSnapshotIds().toArray(new String[0]));
        return ApiResult.success(flowDataSnapshots);
    }

    /**
     * @param objectId
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getFlowDataSnapshotAuditLog", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取数据快照审计日志", notes = "获取数据快照审计日志")
    @ApiOperationSupport(order = 250)
    public ApiResult<FlowDataSnapshotAuditLog> getFlowDataSnapshotAuditLog(@RequestParam("objectId") String objectId) {
        FlowDataSnapshotAuditLog flowDataSnapshotAuditLog = workService.getFlowDataSnapshotAuditLogByObjecId(objectId);
        return ApiResult.success(flowDataSnapshotAuditLog);
    }

    /**
     * @param objectId
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @PostMapping(value = "/getDyformFieldModifyInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单数据字段修改信息", notes = "获取表单数据字段修改信息")
    @ApiOperationSupport(order = 250)
    public ApiResult<FlowDataDyformFieldModifyInfo> getDyformFieldModifyInfo(@RequestBody DyformFieldModifyInfoRequest request) {
        FlowDataDyformFieldModifyInfo dyformFieldModifyInfo = workService.getDyformFieldModifyInfo(request.getFlowInstUuid(), request.getFieldNames());
        return ApiResult.success(dyformFieldModifyInfo);
    }

    /**
     * 获取当前用户的意见用于签署
     *
     * @param flowDefUuid
     * @param taskId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getCurrentUserOpinion2Sign", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取当前用户的意见用于签署", notes = "获取当前用户的意见用于签署")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskId", value = "环节ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkOpinionBean> getCurrentUserOpinion2Sign(@RequestParam(name = "flowDefUuid") String flowDefUuid,
                                                                 @RequestParam(name = "taskId") String taskId) {
        WorkOpinionBean workOpinionBean = null;
        try {
            workOpinionBean = workService.getCurrentUserOpinion2Sign(flowDefUuid, taskId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return ApiResult.success(workOpinionBean);
    }

    /**
     * 获取当前用户的意见用于签署
     *
     * @param flowDefUuid
     * @param taskId
     * @return
     */
    @ResponseBody
    @SelectDatasource
    @GetMapping(value = "/getCurrentUserOpinion2Sign/{flowDefUuid}/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取当前用户的意见用于签署", notes = "获取当前用户的意见用于签署")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskId", value = "环节ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkOpinionBean> getCurrentUserOpinion2SignByPath(@PathVariable(name = "flowDefUuid") String flowDefUuid, @PathVariable(name = "taskId") String taskId) {
        WorkOpinionBean workOpinionBean = null;
        try {
            workOpinionBean = workService.getCurrentUserOpinion2Sign(flowDefUuid, taskId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return ApiResult.success(workOpinionBean);
    }

    /**
     * @param flowInstUuid
     * @param taskInstUuid
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @GetMapping(value = "/getTaskOperationTemp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取临时保存的意见", notes = "获取临时保存的意见")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "taskInstUuid", value = "任务实例UUID", paramType = "query", dataType = "String", required = false)})
    public ApiResult<TaskOperationTemp> getTaskOperationTemp(@RequestParam(name = "flowInstUuid") String flowInstUuid,
                                                             @RequestParam(name = "taskInstUuid", required = false) String taskInstUuid) {
        return ApiResult.success(workService.getTaskOperationTemp(flowInstUuid, taskInstUuid));
    }

    /**
     * 加载分支流程数据
     *
     * @param branchTaskData
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadBranchTaskData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "加载分支流程数据", notes = "加载分支流程数据")
    @ApiOperationSupport(order = 30)
    public ApiResult<BranchTaskData> loadBranchTaskData(@RequestBody BranchTaskData branchTaskData) {
        return ApiResult.success(workService.loadBranchTaskData(branchTaskData));
    }

    /**
     * 加载子流程数据
     *
     * @param subTaskData
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadSubTaskData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "加载子流程数据", notes = "加载子流程数据")
    @ApiOperationSupport(order = 30)
    public ApiResult<SubTaskData> loadSubTaskData(@RequestBody SubTaskData subTaskData) {
        return ApiResult.success(workService.loadSubTaskData(subTaskData));
    }

    /**
     * 加载子流程默认展开的配置
     *
     * @param subTaskData
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadExpandSetting", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "加载子流程默认展开的配置，需要参数：flowInstUuid、taskInstUuid", notes = "加载子流程默认展开的配置，需要参数：flowInstUuid、taskInstUuid")
    @ApiOperationSupport(order = 30)
    public ApiResult loadExpandSetting(@RequestBody SubTaskData subTaskData) {
        return ApiResult.success(workService.loadExpandSetting(subTaskData));
    }

    @ResponseBody
    @PostMapping(value = "/getUndertakeSituationDatas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程的承办信息", notes = "获取流程的承办信息-返回不包含办理信息列表shareItems")
    @ApiOperationSupport(order = 30)
    public ApiResult<List<FlowShareData>> getUndertakeSituationDatas(@RequestBody SubTaskData subTaskData) {
        try {
            List<FlowShareData> flowShareDatas = taskSubFlowService.getUndertakeSituationDatas(subTaskData);
            if (CollectionUtils.isNotEmpty(flowShareDatas)) {
                for (FlowShareData flowShareData : flowShareDatas) {
                    String expandListFlag = workService.loadExpandSetting(flowShareData.getBelongToFlowInstUuid(),
                            flowShareData.getBelongToTaskInstUuid());
                    flowShareData.setExpandListFlag(expandListFlag);
                }
            } else {
                flowShareDatas = Lists.newArrayList();
            }
            for (FlowShareData flowShareData : flowShareDatas) {
                String expandListFlag = workService.loadExpandSetting(flowShareData.getBelongToFlowInstUuid(),
                        flowShareData.getBelongToTaskInstUuid());
                flowShareData.setExpandListFlag(expandListFlag);
            }
            return ApiResult.success(flowShareDatas);
        } catch (Exception e) {
            logger.error("获取流程的承办信息异常：", e);
            return ApiResult.fail("获取流程的承办信息异常");
        }

    }

    @ResponseBody
    @PostMapping(value = "/getUndertakeSituationDatasAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程的承办信息", notes = "获取流程的承办信息-返回有包含办理信息列表shareItems")
    @ApiOperationSupport(order = 30)
    public ApiResult<List<FlowShareData>> getUndertakeSituationDatasAll(@RequestBody SubTaskData subTaskData) {
        try {
            List<FlowShareData> flowShareDatas = taskSubFlowService.getUndertakeSituationDatasAll(subTaskData);
            if (CollectionUtils.isNotEmpty(flowShareDatas)) {
                for (FlowShareData flowShareData : flowShareDatas) {
                    String expandListFlag = workService.loadExpandSetting(flowShareData.getBelongToFlowInstUuid(),
                            flowShareData.getBelongToTaskInstUuid());
                    flowShareData.setExpandListFlag(expandListFlag);
                }
            } else {
                flowShareDatas = Lists.newArrayList();
            }
            return ApiResult.success(flowShareDatas);
        } catch (Exception e) {
            logger.error("获取流程的承办信息异常：", e);
            return ApiResult.fail("获取流程的承办信息异常");
        }

    }

    /**
     * 加载子流程承办信息
     *
     * @param vo
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadShareDatasByPage")
    @ApiOperation(value = "加载子流程承办信息", notes = "加载子流程承办信息")
    @ApiOperationSupport(order = 31)
    public ApiResult<List<FlowShareData>> loadShareDatasByPage(@RequestBody SubTaskDataVo vo) {
        try {
            return ApiResult.success(workService.loadShareDatasByPage(vo));
        } catch (Exception e) {
            logger.error("loadShareDatasByPage异常：", e);
            return ApiResult.fail(e.getMessage());
        }

    }

    /**
     * 加载信息分发
     *
     * @param vo
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadDistributeInfosByPage")
    @ApiOperation(value = "加载信息分发", notes = "加载信息分发")
    @ApiOperationSupport(order = 31)
    public ApiResult<Page<TaskInfoDistributionData>> loadDistributeInfosByPage(@RequestBody SubTaskDataVo vo) {
        return ApiResult.success(workService.loadDistributeInfosByPage(vo));
    }

    /**
     * 加载操作记录
     *
     * @param vo
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loadRelateOperationByPage")
    @ApiOperation(value = "加载操作记录", notes = "加载操作记录")
    @ApiOperationSupport(order = 31)
    public ApiResult<Page<TaskOperationData>> loadRelateOperationByPage(@RequestBody SubTaskDataVo vo) {
        return ApiResult.success(workService.loadRelateOperationByPage(vo));
    }

    /**
     * 获取排序字段
     *
     * @return
     * @author baozh
     * @date 2022/1/5 16:31
     * @params *@params
     */
    @ResponseBody
    @GetMapping(value = "/sortFields")
    @ApiOperation(value = "获取排序字段", notes = "获取排序字段")
    @ApiOperationSupport(order = 32)
    public ApiResult getSortFields() {
        return ApiResult.success(workService.getSortFields());
    }

    /**
     * 获取分支流办理过程
     *
     * @param request
     * @return
     */
    @SelectDatasource
    @ResponseBody
    @PostMapping(value = "/getBranchTaskProcesses", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取分支流办理过程", notes = "获取分支流办理过程")
    @ApiOperationSupport(order = 30)
    public ApiResult<Map<String, List<WorkProcessBean>>> getBranchTaskProcesses(@RequestBody WorkGetBranchTaskProcessesRequest request) {
        return ApiResult.success(workService.getBranchTaskProcesses(request.getTaskInstUuids()));
    }

    /**
     * 根据数据来源、操作类型、业务类别获取表单应用配置
     *
     * @param dataSource
     * @param actionType
     * @param businessType
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getBusinessApplicationConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据数据来源、操作类型、业务类别获取表单应用配置", notes = "根据数据来源、操作类型、业务类别获取表单应用配置")
    @ApiOperationSupport(order = 250)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataSource", value = "数据来源", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "actionType", value = "操作类型", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "businessType", value = "业务类别", paramType = "query", dataType = "String", required = true)})
    public ApiResult<BusinessApplicationConfigDto> getBusinessApplicationConfig(@RequestParam(name = "dataSource") String dataSource, @RequestParam(name = "actionType") String actionType,
                                                                                @RequestParam(name = "businessType") String businessType) {
        BusinessApplicationConfigDto businessApplicationConfigDto = listWorkService
                .getBusinessApplicationConfig(dataSource, actionType, businessType);
        return ApiResult.success(businessApplicationConfigDto);
    }

    /**
     * 发起子流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/startSubFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发起子流程", notes = "发起子流程")
    @ApiOperationSupport(order = 30)
    public ApiResult<List<String>> startSubFlow(@RequestBody WorkStartSubFlowRequest request) {
        List<String> flowInstUuids = listWorkService.startSubFlow(request.getBelongToTaskInstUuid(),
                request.getBelongToFlowInstUuid(), request.getNewFlowId(), request.isMajor(), request.getTaskUsers(),
                request.getBusinessType(), request.getBusinessRole(), request.getActionName());
        return ApiResult.success(flowInstUuids);
    }

    /**
     * 一键补发子流程
     *
     * @param belongToTaskInstUuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/resendSubFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "一键补发子流程", notes = "一键补发子流程")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "belongToTaskInstUuid", value = "所属环节实例UUID", paramType = "query", dataType = "String", required = false)})
    public ApiResult<Void> resendSubFlow(@RequestParam("belongToTaskInstUuid") String belongToTaskInstUuid) {
        listWorkService.resendSubFlow(belongToTaskInstUuid);
        return ApiResult.success();
    }

    /**
     * 发起分支流
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/startBranchTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发起分支流", notes = "发起分支流")
    @ApiOperationSupport(order = 30)
    public ApiResult<List<String>> startBranchTask(@RequestBody WorkStartBranchTaskRequest request) {
        List<String> taskInstUuids = listWorkService.startBranchTask(request.getBelongToTaskInstUuid(),
                request.getBelongToFlowInstUuid(), request.isMajor(), request.getTaskUsers(), request.getBusinessType(),
                request.getBusinessRole(), request.getActionName());
        return ApiResult.success(taskInstUuids);
    }

    /**
     * 根据环节实例UUID、主协办角色标记获取子流程定义标签信息
     *
     * @param taskInstUuid
     * @param roleFlag
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getNewFlowLabelInfos", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据环节实例UUID、主协办角色标记获取子流程定义标签信息", notes = "根据环节实例UUID、主协办角色标记获取子流程定义标签信息")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "roleFlag", value = "主协办角色标记", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<NewFlowLabel>> getNewFlowLabelInfos(@RequestParam(name = "taskInstUuid") String taskInstUuid,
                                                              @RequestParam(name = "roleFlag") String roleFlag) {
        List<NewFlowLabel> newFlowLabels = listWorkService.getNewFlowLabelInfos(taskInstUuid, roleFlag);
        return ApiResult.success(newFlowLabels);
    }

    /**
     * 重做流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/redoFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "重做流程", notes = "重做流程")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> redoFlow(@RequestBody WorkRedoFlowRequest request) {
        listWorkService.redoFlow(request.getTaskInstUuids(), request.getOpinionText());
        return ApiResult.success();
    }

    /**
     * 重做分支流
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/redoBranchTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "重做分支流", notes = "重做分支流")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> redoBranchTask(@RequestBody WorkRedoBranchTaskRequest request) {
        listWorkService.redoBranchTask(request.getTaskInstUuids(), request.getOpinionText());
        return ApiResult.success();
    }

    /**
     * 终止流程
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/stopFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "终止流程", notes = "终止分支流")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> stopFlow(@RequestBody WorkStopFlowRequest request) {
        listWorkService.stopFlow(request.getTaskInstUuids(), request.getOpinionText(), request.getInteractionTaskData());
        return ApiResult.success();
    }

    /**
     * 终止分支流
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/stopBranchTask", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "终止分支流", notes = "终止分支流")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> stopBranchTask(@RequestBody WorkStopBranchTaskRequest request) {
        listWorkService.stopBranchTask(request.getTaskInstUuids(), request.getOpinionText(), request.getInteractionTaskData());
        return ApiResult.success();
    }

    /**
     * 信息分发
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/distributeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "信息分发", notes = "信息分发")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> distributeInfo(@RequestBody WorkDistributeInfoRequest request) {
        listWorkService.distributeInfo(request.getTaskInstUuids(), request.getContent(), request.getFileIds());
        return ApiResult.success();
    }

    /**
     * 查看主流程设置
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/setViewMainFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看主流程设置", notes = "查看主流程设置")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> setViewMainFlow(@RequestParam(name = "flowInstUuid") String flowInstUuid,
                                           @RequestParam(name = "taskId") String taskId,
                                           @RequestParam(name = "childLookParent") String childLookParent) {
        workService.setViewMainFlow(flowInstUuid, taskId, childLookParent);
        return ApiResult.success();
    }

    /**
     * 根据流程环节数据，变更流程到期时间
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/changeTaskDueTime", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "变更环节到期时间", notes = "根据流程环节数据，变更流程到期时间")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> changeTaskDueTime(@RequestBody WorkChangeTaskDueTimeRequest request) {
        listWorkService.changeTaskDueTime(request.getTaskDataItems(), request.getDueTime(), request.getOpinionText());
        return ApiResult.success();
    }

    /**
     * 根据流程实例UUID列表，变更流程到期时间
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/changeFlowDueTime", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "变更流程到期时间", notes = "根据流程实例UUID列表，变更流程到期时间")
    @ApiOperationSupport(order = 30)
    public ApiResult<Void> changeFlowDueTime(@RequestBody WorkChangeFlowDueTimeRequest request) {
        listWorkService.changeFlowDueTime(request.getFlowInstUuids(), request.getDueTime(), request.getOpinionText());
        return ApiResult.success();
    }

    /**
     * 工作加锁
     *
     * @param data
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/lockWork", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "工作加锁", notes = "工作加锁")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> lockWork(@RequestBody Map<String, String> data) {
        String taskInstUuid = data.get("taskInstUuid");
        if (StringUtils.isBlank(taskInstUuid)) {
            return ApiResult.fail();
        }
        workService.lockWork(taskInstUuid);
        return ApiResult.success();
    }

    /**
     * 工作解锁
     *
     * @param taskInstUuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/unlockWork", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "工作解锁", notes = "工作解锁")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> unlockWork(@RequestParam(name = "taskInstUuid") String taskInstUuid) {
        workService.unlockWork(taskInstUuid);
        return ApiResult.success();
    }

    /**
     * 获取环节锁信息
     *
     * @param taskInstUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listLockInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取环节锁信息", notes = "获取环节锁信息")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuid", value = "环节实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TaskLockInfo>> listLockInfo(@RequestParam(name = "taskInstUuid") String taskInstUuid) {
        return ApiResult.success(workService.listLockInfo(taskInstUuid));
    }

    /**
     * 获取环节锁信息
     *
     * @param taskInstUuids
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listAllLockInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取环节锁信息", notes = "获取环节锁信息")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, List<TaskLockInfo>>> listAllLockInfo(@RequestParam(name = "taskInstUuids") Collection<String> taskInstUuids) {
        return ApiResult.success(workService.listAllLockInfo(taskInstUuids));
    }

    /**
     * 释放锁
     *
     * @param taskInstUuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/releaseAllLock", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "释放锁", notes = "释放锁")
    @ApiOperationSupport(order = 30)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskInstUuids", value = "环节实例UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> releaseAllLock(@RequestParam(name = "taskInstUuids") Collection<String> taskInstUuids) {
        workService.releaseAllLock(taskInstUuids);
        return ApiResult.success();
    }

    /**
     * 检查信息记录前置条件
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/checkRecordPreCondition", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "检查信息记录前置条件", notes = "检查信息记录前置条件")
    @ApiOperationSupport(order = 30)
    public ApiResult<Boolean> checkRecordPreCondition(@RequestBody CheckRecordPreConditionRequest request) {
        String taskInstUuid = request.getTaskInstUuid();
        String flowInstUuid = request.getFlowInstUuid();
        String flowDefUuid = request.getFlowDefUuid();
        Record record = request.getRecord();
        DyFormData dyFormData = request.getDyFormData();
        String opinionLabel = request.getOpinionLabel();
        String opinionValue = request.getOpinionValue();
        String opinionText = request.getOpinionText();
        return ApiResult.success(workService.checkRecordPreCondition(taskInstUuid, flowInstUuid, flowDefUuid,
                dyFormData, record, opinionLabel, opinionValue, opinionText));
    }

    /**
     * 根据流程实例UUID，获取流程实例使用的组织版本ID列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getOrgVersionIds", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据流程实例UUID，获取流程实例使用的组织版本ID列表", notes = "根据流程实例UUID，获取流程实例使用的组织版本ID列表")
    @ApiOperationSupport(order = 130)
    public ApiResult<Set<String>> getOrgVersionIds(@RequestParam(name = "flowInstUuid") String flowInstUuid,
                                                   @RequestParam(name = "flowDefUuid") String flowDefUuid) {
        return ApiResult.success(flowService.getOrgVersionIdsByFlowInstUuid(flowInstUuid, flowDefUuid));
    }

    /**
     * 获取群聊提供者列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listGroupChatProvider", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取群聊提供者列表", notes = "获取群聊提供者列表")
    @ApiOperationSupport(order = 140)
    public ApiResult<List<FlowGroupChatProvider.ProviderInfo>> listGroupChatProvider() {
        return ApiResult.success(workService.listGroupChatProvider());
    }

    /**
     * 发起群聊
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/startGroupChat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发起群聊", notes = "发起群聊")
    @ApiOperationSupport(order = 150)
    public ApiResult<String> startGroupChat(@RequestBody WorkStartGroupChatRequest request) {
        StartGroupChat startGroupChat = new StartGroupChat();
        BeanUtils.copyProperties(request, startGroupChat);
        String chatId = workService.startGroupChat(request.getProvider(), startGroupChat);
        return ApiResult.success(chatId);
    }

    @ApiModel("工作发起群聊请求数据")
    public static class WorkStartGroupChatRequest extends BaseObject {

        private static final long serialVersionUID = 3435087745287938356L;
        @ApiModelProperty("流程实例UUID")
        private String flowInstUuid;

        @ApiModelProperty("环节实例UUID")
        private String taskInstUuid;

        @ApiModelProperty("流程实例标题")
        private String title;

        @ApiModelProperty("群名称")
        private String groupName;

        @ApiModelProperty("群成员ID，以分号隔开")
        private String memberIds;

        @ApiModelProperty("群主")
        private String ownerId;

        @ApiModelProperty("群聊提供者")
        private String provider;

        /**
         * @return the flowInstUuid
         */
        public String getFlowInstUuid() {
            return flowInstUuid;
        }

        /**
         * @param flowInstUuid 要设置的flowInstUuid
         */
        public void setFlowInstUuid(String flowInstUuid) {
            this.flowInstUuid = flowInstUuid;
        }

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title 要设置的title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the groupName
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * @param groupName 要设置的groupName
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        /**
         * @return the memberIds
         */
        public String getMemberIds() {
            return memberIds;
        }

        /**
         * @param memberIds 要设置的memberIds
         */
        public void setMemberIds(String memberIds) {
            this.memberIds = memberIds;
        }

        /**
         * @return the ownerId
         */
        public String getOwnerId() {
            return ownerId;
        }

        /**
         * @param ownerId 要设置的ownerId
         */
        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        /**
         * @return the provider
         */
        public String getProvider() {
            return provider;
        }

        /**
         * @param provider 要设置的provider
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    @ApiModel("工作数据获取请求数据")
    public static class WorkGetTodoWorkDataRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6466409856931360598L;

        @ApiModelProperty("环节实例UUID")
        private String taskInstUuid;

        @ApiModelProperty("流程定义UUID")
        private String flowDefUuid;

        @ApiModelProperty("是否加载表单数据")
        private boolean loadDyFormData;

        @ApiModelProperty("与前环节相同办理人的操作")
        private String sameUserSubmitTaskOperationUuid;

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the flowDefUuid
         */
        public String getFlowDefUuid() {
            return flowDefUuid;
        }

        /**
         * @param flowDefUuid 要设置的flowDefUuid
         */
        public void setFlowDefUuid(String flowDefUuid) {
            this.flowDefUuid = flowDefUuid;
        }

        /**
         * @return the loadDyFormData
         */
        public boolean isLoadDyFormData() {
            return loadDyFormData;
        }

        /**
         * @param loadDyFormData 要设置的loadDyFormData
         */
        public void setLoadDyFormData(boolean loadDyFormData) {
            this.loadDyFormData = loadDyFormData;
        }

        /**
         * @return the sameUserSubmitTaskOperationUuid
         */
        public String getSameUserSubmitTaskOperationUuid() {
            return sameUserSubmitTaskOperationUuid;
        }

        /**
         * @param sameUserSubmitTaskOperationUuid 要设置的sameUserSubmitTaskOperationUuid
         */
        public void setSameUserSubmitTaskOperationUuid(String sameUserSubmitTaskOperationUuid) {
            this.sameUserSubmitTaskOperationUuid = sameUserSubmitTaskOperationUuid;
        }

    }

    @ApiModel("工作撤回请求数据")
    public static class WorkCancelRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 75586559487951535L;

        @ApiModelProperty("办理意见")
        private String opinionText;

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

    }

    @ApiModel("工作转办请求数据")
    public static class WorkTransferRequest extends WorkCopyToRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6354414825431429802L;

        @ApiModelProperty("流程工作数据")
        private WorkBean workBean;

        @ApiModelProperty("意见立场文本")
        private String opinionLabel;

        @ApiModelProperty("意见立场值")
        private String opinionValue;

        @ApiModelProperty("办理意见")
        private String opinionText;

        @ApiModelProperty("签署的附件")
        private List<LogicFileInfo> opinionFiles;

        /**
         * @return the workBean
         */
        public WorkBean getWorkBean() {
            return workBean;
        }

        /**
         * @param workBean 要设置的workBean
         */
        public void setWorkBean(WorkBean workBean) {
            this.workBean = workBean;
        }

        /**
         * @return the opinionLabel
         */
        public String getOpinionLabel() {
            return opinionLabel;
        }

        /**
         * @param opinionLabel 要设置的opinionLabel
         */
        public void setOpinionLabel(String opinionLabel) {
            this.opinionLabel = opinionLabel;
        }

        /**
         * @return the opinionValue
         */
        public String getOpinionValue() {
            return opinionValue;
        }

        /**
         * @param opinionValue 要设置的opinionValue
         */
        public void setOpinionValue(String opinionValue) {
            this.opinionValue = opinionValue;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

        /**
         * @return the opinionFiles
         */
        public List<LogicFileInfo> getOpinionFiles() {
            return opinionFiles;
        }

        /**
         * @param opinionFiles 要设置的opinionFiles
         */
        public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
            this.opinionFiles = opinionFiles;
        }
    }

    @ApiModel("工作会签请求数据")
    public static class WorkCounterSignRequest extends WorkTransferRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 9167134905757103239L;

    }

    @ApiModel("工作会签请求数据")
    public static class WorkCopyToRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -6721396646774411928L;

        @ApiModelProperty(value = "环节实例UUID列表", required = true)
        private List<String> taskInstUuids;

        @ApiModelProperty(value = "用户ID列表", required = true)
        private List<String> userIds;

        @ApiModelProperty(value = "ACL角色", required = true)
        private String aclRole;

        /**
         * @return the taskInstUuids
         */
        public List<String> getTaskInstUuids() {
            return taskInstUuids;
        }

        /**
         * @param taskInstUuids 要设置的taskInstUuids
         */
        public void setTaskInstUuids(List<String> taskInstUuids) {
            this.taskInstUuids = taskInstUuids;
        }

        /**
         * @return the userIds
         */
        public List<String> getUserIds() {
            return userIds;
        }

        /**
         * @param userIds 要设置的userIds
         */
        public void setUserIds(List<String> userIds) {
            this.userIds = userIds;
        }

        /**
         * @return the aclRole
         */
        public String getAclRole() {
            return aclRole;
        }

        /**
         * @param aclRole 要设置的aclRole
         */
        public void setAclRole(String aclRole) {
            this.aclRole = aclRole;
        }
    }

    @ApiModel("工作标记已阅请求数据")
    public static class WorkMarkReadRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -429534589370350520L;

    }

    @ApiModel("工作标记未阅请求数据")
    public static class WorkMarkUnreadRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7522365782636436738L;

    }

    @ApiModel("工作关注请求数据")
    public static class WorkAttentionRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1121027494104925829L;

        @ApiModelProperty(value = "环节实例UUID列表", required = true)
        private List<String> taskInstUuids;

        /**
         * @return the taskInstUuids
         */
        public List<String> getTaskInstUuids() {
            return taskInstUuids;
        }

        /**
         * @param taskInstUuids 要设置的taskInstUuids
         */
        public void setTaskInstUuids(List<String> taskInstUuids) {
            this.taskInstUuids = taskInstUuids;
        }
    }

    @ApiModel("工作取消关注请求数据")
    public static class WorkUnfollowRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1045458049518441833L;

    }

    @ApiModel("工作催办请求数据")
    public static class WorkRemindRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -889246639913380172L;

        @ApiModelProperty("意见立场文本")
        private String opinionLabel;

        @ApiModelProperty("意见立场值")
        private String opinionValue;

        @ApiModelProperty("办理意见")
        private String opinionText;

        @ApiModelProperty("签署的附件")
        private List<LogicFileInfo> opinionFiles;

        /**
         * @return the opinionLabel
         */
        public String getOpinionLabel() {
            return opinionLabel;
        }

        /**
         * @param opinionLabel 要设置的opinionLabel
         */
        public void setOpinionLabel(String opinionLabel) {
            this.opinionLabel = opinionLabel;
        }

        /**
         * @return the opinionValue
         */
        public String getOpinionValue() {
            return opinionValue;
        }

        /**
         * @param opinionValue 要设置的opinionValue
         */
        public void setOpinionValue(String opinionValue) {
            this.opinionValue = opinionValue;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

        /**
         * @return the opinionFiles
         */
        public List<LogicFileInfo> getOpinionFiles() {
            return opinionFiles;
        }

        /**
         * @param opinionFiles 要设置的opinionFiles
         */
        public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
            this.opinionFiles = opinionFiles;
        }

    }

    @ApiModel("获取套打模板请求数据")
    public static class WorkGetPrintTemplateRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7848252467026575014L;

        @ApiModelProperty("环节实例UUID")
        private String taskInstUuid;

        @ApiModelProperty("流程实例UUID")
        private String flowInstUuid;

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the flowInstUuid
         */
        public String getFlowInstUuid() {
            return flowInstUuid;
        }

        /**
         * @param flowInstUuid 要设置的flowInstUuid
         */
        public void setFlowInstUuid(String flowInstUuid) {
            this.flowInstUuid = flowInstUuid;
        }

    }

    @ApiModel("工作套打请求数据")
    public static class WorkPrintRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -4157966489530709032L;

        @ApiModelProperty("环节实例UUID")
        private String taskInstUuid;

        @ApiModelProperty("套打模板ID")
        private String printTemplateId;

        @ApiModelProperty("套打模板UUID")
        private String printTemplateUuid;

        @ApiModelProperty("套打模板语言")
        private String printTemplateLang;

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the printTemplateId
         */
        public String getPrintTemplateId() {
            return printTemplateId;
        }

        /**
         * @param printTemplateId 要设置的printTemplateId
         */
        public void setPrintTemplateId(String printTemplateId) {
            this.printTemplateId = printTemplateId;
        }

        /**
         * @return the printTemplateUuid
         */
        public String getPrintTemplateUuid() {
            return printTemplateUuid;
        }

        /**
         * @param printTemplateUuid 要设置的printTemplateUuid
         */
        public void setPrintTemplateUuid(String printTemplateUuid) {
            this.printTemplateUuid = printTemplateUuid;
        }

        /**
         * @return the printTemplateLang
         */
        public String getPrintTemplateLang() {
            return printTemplateLang;
        }

        /**
         * @param printTemplateLang 要设置的printTemplateLang
         */
        public void setPrintTemplateLang(String printTemplateLang) {
            this.printTemplateLang = printTemplateLang;
        }

    }

    @ApiModel("工作特送个人请求数据")
    public static class WorkHandOverRequest extends WorkTransferRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -990924988181799769L;

        @ApiModelProperty("签署的附件")
        private List<LogicFileInfo> opinionFiles;

        /**
         * @return the opinionFiles
         */
        public List<LogicFileInfo> getOpinionFiles() {
            return opinionFiles;
        }

        /**
         * @param opinionFiles 要设置的opinionFiles
         */
        public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
            this.opinionFiles = opinionFiles;
        }

    }

    @ApiModel("工作挂起请求数据")
    public static class WorkSuspendRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 2941937548426302773L;
    }

    @ApiModel("工作恢复请求数据")
    public static class WorkResumeRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6818131331889465884L;

    }

    @ApiModel("工作删除请求数据")
    public static class WorkDeleteRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5847291090298784462L;

    }

    @ApiModel("工作草稿删除请求数据")
    public static class WorkDraftDeleteRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4206635357990786190L;

        @ApiModelProperty(value = "流程实例UUID列表", required = true)
        private List<String> flowInstUuids;

        /**
         * @return the flowInstUuids
         */
        public List<String> getFlowInstUuids() {
            return flowInstUuids;
        }

        /**
         * @param flowInstUuids 要设置的flowInstUuids
         */
        public void setFlowInstUuids(List<String> flowInstUuids) {
            this.flowInstUuids = flowInstUuids;
        }

    }

    @ApiModel("工作置顶请求数据")
    public static class WorkToppingRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3417893445348625530L;
    }

    @ApiModel("工作取消置顶请求数据")
    public static class WorkUntoppingRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3284797635588455457L;
    }

    @ApiModel("工作是否允许提交请求数据")
    public static class WorkIsAllowedSubmitRequest extends WorkAttentionRequest {
        private static final long serialVersionUID = -3698117573063773791L;
    }

    @ApiModel("工作是否允许转办请求数据")
    public static class WorkIsAllowedTransferRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3228319730001542272L;

    }

    @ApiModel("工作是否允许直接退回请求数据")
    public static class WorkIsAllowedDirectRollbackRequest extends WorkAttentionRequest {
        private static final long serialVersionUID = -1233217260722467031L;
    }

    @ApiModel("工作是否允许环节跳转请求数据")
    public static class WorkIsAllowedGotoTaskRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1489951815167121644L;

    }

    @ApiModel("工作是否允许移交请求数据")
    public static class WorkIsAllowedHandOverRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6970239271323578331L;

    }

    @ApiModel("工作是否需要提交意见请求数据")
    public static class WorkIsRequiredSubmitOpinionRequest extends WorkAttentionRequest {
        private static final long serialVersionUID = -4079421185223095843L;
    }

    @ApiModel("工作是否允许撤回请求数据")
    public static class WorkIsAllowedCancelRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3133078551985343829L;

    }

    @ApiModel("工作是否需要退回意见请求数据")
    public static class WorkIsRequiredRollbackOpinionRequest extends WorkAttentionRequest {
        private static final long serialVersionUID = 7599127455182600215L;
    }

    @ApiModel("工作撤回是否需要撤回意见请求数据")
    public static class WorkIsRequiredCancelOpinionRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4455286049706105519L;

    }

    @ApiModel("工作是否需要特送个人意见请求数据")
    public static class WorkIsRequiredHandOverOpinionRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3486612705764789671L;

    }

    @ApiModel("工作是否需要环节跳转意见请求数据")
    public static class WorkIsRequiredGotoTaskOpinionRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -7057820192012460909L;

    }

    @ApiModel("工作获取分支流办理过程请求数据")
    public static class WorkGetBranchTaskProcessesRequest extends WorkAttentionRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7610847584863764154L;

    }

    @ApiModel("工作发起子流程请求数据")
    public static class WorkStartSubFlowRequest extends WorkStartBranchTaskRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5839941918878435429L;

        @ApiModelProperty(value = "子流程ID", required = true)
        private String newFlowId;

        /**
         * @return the newFlowId
         */
        public String getNewFlowId() {
            return newFlowId;
        }

        /**
         * @param newFlowId 要设置的newFlowId
         */
        public void setNewFlowId(String newFlowId) {
            this.newFlowId = newFlowId;
        }

    }

    @ApiModel("工作发起分支流请求数据")
    public static class WorkStartBranchTaskRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2236987024174088745L;

        @ApiModelProperty(value = "所属环节实例UUID", required = true)
        private String belongToTaskInstUuid;
        @ApiModelProperty(value = "所属流程实例UUID", required = true)
        private String belongToFlowInstUuid;
        @ApiModelProperty(value = "是否主办", required = true)
        private boolean isMajor;
        @ApiModelProperty(value = "办理人员", required = true)
        private List<String> taskUsers;
        @ApiModelProperty(value = "业务类别")
        private String businessType;
        @ApiModelProperty(value = "业务角色")
        private String businessRole;
        @ApiModelProperty(value = "操作名称")
        private String actionName;

        /**
         * @return the belongToTaskInstUuid
         */
        public String getBelongToTaskInstUuid() {
            return belongToTaskInstUuid;
        }

        /**
         * @param belongToTaskInstUuid 要设置的belongToTaskInstUuid
         */
        public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
            this.belongToTaskInstUuid = belongToTaskInstUuid;
        }

        /**
         * @return the belongToFlowInstUuid
         */
        public String getBelongToFlowInstUuid() {
            return belongToFlowInstUuid;
        }

        /**
         * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
         */
        public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
            this.belongToFlowInstUuid = belongToFlowInstUuid;
        }

        /**
         * @return the isMajor
         */
        public boolean isMajor() {
            return isMajor;
        }

        /**
         * @param isMajor 要设置的isMajor
         */
        public void setMajor(boolean isMajor) {
            this.isMajor = isMajor;
        }

        /**
         * @return the taskUsers
         */
        public List<String> getTaskUsers() {
            return taskUsers;
        }

        /**
         * @param taskUsers 要设置的taskUsers
         */
        public void setTaskUsers(List<String> taskUsers) {
            this.taskUsers = taskUsers;
        }

        /**
         * @return the businessType
         */
        public String getBusinessType() {
            return businessType;
        }

        /**
         * @param businessType 要设置的businessType
         */
        public void setBusinessType(String businessType) {
            this.businessType = businessType;
        }

        /**
         * @return the businessRole
         */
        public String getBusinessRole() {
            return businessRole;
        }

        /**
         * @param businessRole 要设置的businessRole
         */
        public void setBusinessRole(String businessRole) {
            this.businessRole = businessRole;
        }

        /**
         * @return the actionName
         */
        public String getActionName() {
            return actionName;
        }

        /**
         * @param actionName 要设置的actionName
         */
        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

    }

    @ApiModel("工作重办流程请求数据")
    public static class WorkRedoFlowRequest extends BaseObject {
        private static final long serialVersionUID = 825042816616924021L;

        @ApiModelProperty(value = "环节实例UUID列表", required = true)
        private List<String> taskInstUuids;

        @ApiModelProperty("重办原因")
        private String opinionText;

        /**
         * @return the taskInstUuids
         */
        public List<String> getTaskInstUuids() {
            return taskInstUuids;
        }

        /**
         * @param taskInstUuids 要设置的taskInstUuids
         */
        public void setTaskInstUuids(List<String> taskInstUuids) {
            this.taskInstUuids = taskInstUuids;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }
    }

    @ApiModel("工作重办分支流请求数据")
    public static class WorkRedoBranchTaskRequest extends WorkRedoFlowRequest {
        private static final long serialVersionUID = 8027695988561334242L;

    }

    @ApiModel("工作终止流程请求数据")
    public static class WorkStopFlowRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2381289169282779672L;

        @ApiModelProperty(value = "环节实例UUID列表", required = true)
        private List<String> taskInstUuids;

        @ApiModelProperty("交互式的环节数据")
        private InteractionTaskData interactionTaskData;

        @ApiModelProperty("终止原因")
        private String opinionText;

        /**
         * @return the taskInstUuids
         */
        public List<String> getTaskInstUuids() {
            return taskInstUuids;
        }

        /**
         * @param taskInstUuids 要设置的taskInstUuids
         */
        public void setTaskInstUuids(List<String> taskInstUuids) {
            this.taskInstUuids = taskInstUuids;
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

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }
    }

    @ApiModel("工作终止分支流请求数据")
    public static class WorkStopBranchTaskRequest extends WorkStopFlowRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6013065629145142545L;

    }

    @ApiModel("工作信息分发请求数据")
    public static class WorkDistributeInfoRequest extends WorkStopFlowRequest {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -9172116945681264448L;

        @ApiModelProperty(value = "环节实例UUID列表", required = true)
        private List<String> taskInstUuids;
        @ApiModelProperty(value = "分发内容")
        private String content;
        @ApiModelProperty(value = "分发文件ID列表")
        private List<String> fileIds;

        /**
         * @return the taskInstUuids
         */
        public List<String> getTaskInstUuids() {
            return taskInstUuids;
        }

        /**
         * @param taskInstUuids 要设置的taskInstUuids
         */
        public void setTaskInstUuids(List<String> taskInstUuids) {
            this.taskInstUuids = taskInstUuids;
        }

        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content 要设置的content
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * @return the fileIds
         */
        public List<String> getFileIds() {
            return fileIds;
        }

        /**
         * @param fileIds 要设置的fileIds
         */
        public void setFileIds(List<String> fileIds) {
            this.fileIds = fileIds;
        }

    }

    @ApiModel("工作变更环节到期时间请求数据")
    public static class WorkChangeTaskDueTimeRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -744458573145601276L;

        @ApiModelProperty(value = "流程环节数据", required = true)
        private List<FlowShareItem> taskDataItems;

        @ApiModelProperty(value = "到期时间", required = true)
        private String dueTime;

        @ApiModelProperty("变更时限原因")
        private String opinionText;

        /**
         * @return the taskDataItems
         */
        public List<FlowShareItem> getTaskDataItems() {
            return taskDataItems;
        }

        /**
         * @param taskDataItems 要设置的taskDataItems
         */
        public void setTaskDataItems(List<FlowShareItem> taskDataItems) {
            this.taskDataItems = taskDataItems;
        }

        /**
         * @return the dueTime
         */
        public String getDueTime() {
            return dueTime;
        }

        /**
         * @param dueTime 要设置的dueTime
         */
        public void setDueTime(String dueTime) {
            this.dueTime = dueTime;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }
    }

    @ApiModel("工作变更流程到期时间请求数据")
    public static class WorkChangeFlowDueTimeRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -8778444514369147962L;

        @ApiModelProperty(value = "流程实例UUID列表", required = true)
        private List<String> flowInstUuids;

        @ApiModelProperty(value = "到期时间", required = true)
        private String dueTime;

        @ApiModelProperty("变更时限原因")
        private String opinionText;

        /**
         * @return the flowInstUuids
         */
        public List<String> getFlowInstUuids() {
            return flowInstUuids;
        }

        /**
         * @param flowInstUuids 要设置的flowInstUuids
         */
        public void setFlowInstUuids(List<String> flowInstUuids) {
            this.flowInstUuids = flowInstUuids;
        }

        /**
         * @return the dueTime
         */
        public String getDueTime() {
            return dueTime;
        }

        /**
         * @param dueTime 要设置的dueTime
         */
        public void setDueTime(String dueTime) {
            this.dueTime = dueTime;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }
    }

    @ApiModel("工作根据快照ID列表获取快照请求数据")
    public static class WorkGetFlowDataSnapshotRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4566843467884907911L;

        private List<String> snapshotIds;

        /**
         * @return the snapshotIds
         */
        public List<String> getSnapshotIds() {
            return snapshotIds;
        }

        /**
         * @param snapshotIds 要设置的snapshotIds
         */
        public void setSnapshotIds(List<String> snapshotIds) {
            this.snapshotIds = snapshotIds;
        }

    }

    @ApiModel("检查信息记录前置条件请求数据")
    public static class CheckRecordPreConditionRequest extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        @ApiModelProperty("流程实例UUID")
        private String flowInstUuid;

        @ApiModelProperty("环节实例UUID")
        private String taskInstUuid;

        @ApiModelProperty("流程定义UUID")
        private String flowDefUuid;

        @ApiModelProperty("信息记录")
        private Record record;

        @ApiModelProperty("表单数据")
        @JsonDeserialize(using = DyFormDataDeserializer.class)
        private DyFormData dyFormData;

        @ApiModelProperty("意见立场文本")
        private String opinionLabel;

        @ApiModelProperty("意见立场值")
        private String opinionValue;

        @ApiModelProperty("办理意见")
        private String opinionText;

        /**
         * @return the flowInstUuid
         */
        public String getFlowInstUuid() {
            return flowInstUuid;
        }

        /**
         * @param flowInstUuid 要设置的flowInstUuid
         */
        public void setFlowInstUuid(String flowInstUuid) {
            this.flowInstUuid = flowInstUuid;
        }

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the flowDefUuid
         */
        public String getFlowDefUuid() {
            return flowDefUuid;
        }

        /**
         * @param flowDefUuid 要设置的flowDefUuid
         */
        public void setFlowDefUuid(String flowDefUuid) {
            this.flowDefUuid = flowDefUuid;
        }

        /**
         * @return the record
         */
        public Record getRecord() {
            return record;
        }

        /**
         * @param record 要设置的record
         */
        public void setRecord(Record record) {
            this.record = record;
        }

        /**
         * @return the dyFormData
         */
        public DyFormData getDyFormData() {
            return dyFormData;
        }

        /**
         * @param dyFormData 要设置的dyFormData
         */
        public void setDyFormData(DyFormData dyFormData) {
            this.dyFormData = dyFormData;
        }

        /**
         * @return the opinionLabel
         */
        public String getOpinionLabel() {
            return opinionLabel;
        }

        /**
         * @param opinionLabel 要设置的opinionLabel
         */
        public void setOpinionLabel(String opinionLabel) {
            this.opinionLabel = opinionLabel;
        }

        /**
         * @return the opinionValue
         */
        public String getOpinionValue() {
            return opinionValue;
        }

        /**
         * @param opinionValue 要设置的opinionValue
         */
        public void setOpinionValue(String opinionValue) {
            this.opinionValue = opinionValue;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

    }

    @ApiModel("表单字段修改信息请求数据")
    public static class DyformFieldModifyInfoRequest extends BaseObject {
        private static final long serialVersionUID = 5188942343778165126L;

        private String flowInstUuid;
        private List<String> fieldNames;

        /**
         * @return the flowInstUuid
         */
        public String getFlowInstUuid() {
            return flowInstUuid;
        }

        /**
         * @param flowInstUuid 要设置的flowInstUuid
         */
        public void setFlowInstUuid(String flowInstUuid) {
            this.flowInstUuid = flowInstUuid;
        }

        /**
         * @return the fieldNames
         */
        public List<String> getFieldNames() {
            return fieldNames;
        }

        /**
         * @param fieldNames 要设置的fieldNames
         */
        public void setFieldNames(List<String> fieldNames) {
            this.fieldNames = fieldNames;
        }
    }

}
