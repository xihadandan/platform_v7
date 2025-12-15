/*
 * @(#)2021年3月31日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationWorkData;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.app.workflow.facade.service.impl.WorkFlowSimulationServiceImpl;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRuntimeParams;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.exception.*;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.service.UserInfoService;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationRecordDto;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSimulationRecordFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 流程仿真接口
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
@Api(tags = "流程仿真")
@RestController
@RequestMapping("/api/workflow/simulation")
public class ApiWorkflowSimulationController extends BaseController {

    @Autowired
    private WorkFlowSimulationService workFlowSimulationService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private WfFlowSimulationRecordService flowSimulationRecordService;

    @Autowired
    private WfFlowSimulationRecordFacadeService flowSimulationRecordFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 根据流程定义ID列出所有环节
     *
     * @param flowDefId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "根据流程定义ID列出所有环节", notes = "根据流程定义ID列出所有环节")
    @GetMapping("/listTaskByFlowDefId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefId", value = "流程定义ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<Map<String, Object>>> listTaskByFlowDefId(
            @RequestParam(name = "flowDefId", required = true) String flowDefId) {
        List<Map<String, Object>> taskMap = workFlowSimulationService.listTaskByFlowDefId(flowDefId);
        return ApiResult.success(taskMap);
    }

    /**
     * 获取仿真数据
     *
     * @param flowInstUuid
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "获取仿真数据", notes = "获取仿真数据")
    @GetMapping("/getSimulationData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "流程实例UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<FlowSimulationDataDto> getSimulationData(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        FlowSimulationDataDto flowSimulationDataDto = workFlowSimulationService.getSimulationData(flowInstUuid);
        return ApiResult.success(flowSimulationDataDto);
    }

    /**
     * 仿真保存表单数据
     *
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "仿真保存表单数据", notes = "仿真保存表单数据")
    @PostMapping("/saveFormData")
    public ApiResult<String> saveFormData(@RequestBody String jsonData) {
        DyFormData dyFormData = this.dyFormFacade.parseDyformData(jsonData);
        return ApiResult.success(workFlowSimulationService.simulationSaveFormData(dyFormData));
    }

    /**
     * 仿真提交数据
     *
     * @param simulationData
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "仿真提交数据", notes = "仿真提交数据")
    @PostMapping("/simulationSubmit")
    public ResultMessage simulationSubmit(@RequestBody FlowSimulationDataDto simulationData) throws Exception {
        FlowSimulationDataDto handleSimulationData = simulationData;
        WorkFlowSimulationServiceImpl.SimulationTaskInfo subTaskInfo = simulationData.getSubTaskInfo();
        if (subTaskInfo != null) {
            handleSimulationData = createSubTaskSimulationData(subTaskInfo, handleSimulationData);
        }
        try {
            if (TaskNodeType.CollaborationTask.getValueAsInt().equals(handleSimulationData.getTaskType())
                    && StringUtils.isNotBlank(handleSimulationData.getSuperviseUserId())) {
                return workFlowSimulationService.simulationComplete(handleSimulationData);
            } else {
                return workFlowSimulationService.simulationSubmit(handleSimulationData);
            }
        } catch (Exception e) {
            return handleSimulationError(e, handleSimulationData);
        }
    }

    /**
     * @param subTaskInfo
     * @param handleSimulationData
     * @return
     */
    private FlowSimulationDataDto createSubTaskSimulationData(WorkFlowSimulationServiceImpl.SimulationTaskInfo subTaskInfo, FlowSimulationDataDto handleSimulationData) {
        FlowSimulationDataDto data = new FlowSimulationDataDto();
        BeanUtils.copyProperties(handleSimulationData, data);
        BeanUtils.copyProperties(subTaskInfo, data);
        return data;
    }

    /**
     * @param e
     * @param simulationData
     */
    private ResultMessage handleSimulationError(Exception e, FlowSimulationDataDto simulationData) throws Exception {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        FlowSimulationRuntimeParams runtimeParams = simulationData.getRuntimeParams();
        JSONObject flowSimulation = new JSONObject(workFlowSettings.getFlowSimulation());
        if (flowSimulation != null) {
            JSONObject runtime = flowSimulation.getJSONObject("runtime");
            // 找不到办理人时
            if (e instanceof TaskNotAssignedUserException) {
                String taskUserNoFound = runtime.getString("taskUserNoFound");
                String assignTaskUserId = runtime.getString("assignTaskUserId");
                Map<String, Object> data = (Map<String, Object>) ((TaskNotAssignedUserException) e).getData();
                runtimeParams.setTaskUserNoFound(getTaskId(data), taskUserNoFound);
                if (StringUtils.equals("current", taskUserNoFound)) {
                    simulationData.getTaskUsers().put(getTaskId(data), Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
                } else if (StringUtils.equals("assign", taskUserNoFound) && StringUtils.isNotBlank(assignTaskUserId)) {
                    simulationData.getTaskUsers().put(getTaskId(data), Arrays.asList(StringUtils.split(assignTaskUserId, Separator.SEMICOLON.getValue())));
                } else {
                    throw e;
                }
            } else if (e instanceof TaskNotAssignedCopyUserException) {
                // 找不到抄送人时
                String taskCopyUserNoFound = runtime.getString("taskCopyUserNoFound");
                String assignTaskCopyUserId = runtime.getString("assignTaskCopyUserId");
                Map<String, Object> data = (Map<String, Object>) ((TaskNotAssignedCopyUserException) e).getData();
                runtimeParams.setTaskCopyUserNoFound(getTaskId(data), taskCopyUserNoFound);
                if (StringUtils.equals("current", taskCopyUserNoFound)) {
                    simulationData.getTaskCopyUsers().put(getTaskId(data), Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
                } else if (StringUtils.equals("assign", taskCopyUserNoFound) && StringUtils.isNotBlank(assignTaskCopyUserId)) {
                    simulationData.getTaskCopyUsers().put(getTaskId(data), Arrays.asList(StringUtils.split(assignTaskCopyUserId, Separator.SEMICOLON.getValue())));
                } else {
                    throw e;
                }
            } else if (e instanceof TaskNotAssignedMonitorException) {
                // 找不到督办人时
                String taskSuperviseUserNoFound = runtime.getString("taskSuperviseUserNoFound");
                String assignTaskSuperviseUserId = runtime.getString("assignTaskSuperviseUserId");
                Map<String, Object> data = (Map<String, Object>) ((TaskNotAssignedMonitorException) e).getData();
                runtimeParams.setTaskSuperviseUserNoFound(getTaskId(data), taskSuperviseUserNoFound);
                if (StringUtils.equals("current", taskSuperviseUserNoFound)) {
                    simulationData.getTaskMonitors().put(getTaskId(data), Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
                } else if (StringUtils.equals("assign", taskSuperviseUserNoFound) && StringUtils.isNotBlank(assignTaskSuperviseUserId)) {
                    simulationData.getTaskMonitors().put(getTaskId(data), Arrays.asList(StringUtils.split(assignTaskSuperviseUserId, Separator.SEMICOLON.getValue())));
                } else {
                    throw e;
                }
            } else if (e instanceof TaskNotAssignedDecisionMakerException) {
                // 找不到决策人时
                String taskDecisionMakerNoFound = runtime.getString("taskDecisionMakerNoFound");
                String assignTaskDecisionMakerId = runtime.getString("assignTaskDecisionMakerId");
                Map<String, Object> data = (Map<String, Object>) ((TaskNotAssignedDecisionMakerException) e).getData();
                runtimeParams.setTaskDecisionMakerNoFound(getTaskId(data), taskDecisionMakerNoFound);
                if (StringUtils.equals("current", taskDecisionMakerNoFound)) {
                    simulationData.getTaskDecisionMakers().put(getTaskId(data), Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
                } else if (StringUtils.equals("assign", taskDecisionMakerNoFound) && StringUtils.isNotBlank(assignTaskDecisionMakerId)) {
                    simulationData.getTaskDecisionMakers().put(getTaskId(data), Arrays.asList(StringUtils.split(assignTaskDecisionMakerId, Separator.SEMICOLON.getValue())));
                } else {
                    throw e;
                }
            } else if (e instanceof ChooseSpecificCopyUserException) {
                // 二次选择抄送人
                throw e;
            } else if (e instanceof ChooseSpecificUserException) {
                // 多个办理人选择多个具体办理人时
                String chooseMultiUser = runtime.getString("chooseMultiUser");
                int randomUserCount = runtime.getInt("randomUserCount");
                Map<String, Object> data = (Map<String, Object>) ((ChooseSpecificUserException) e).getData();
                List<String> userIds = (List<String>) data.get("userIds");
                runtimeParams.setChooseMultiUser(getTaskId(data), chooseMultiUser);
                runtimeParams.setRandomUserCount(getTaskId(data), randomUserCount);
                runtimeParams.setCandidateUserIds(getTaskId(data), userIds);
                if (StringUtils.equals("all", chooseMultiUser)) {
                    simulationData.getTaskUsers().put(getTaskId(data), userIds);
                } else if (StringUtils.equals("random", chooseMultiUser)) {
                    Random random = new Random();
                    int count = Integer.valueOf(randomUserCount);
                    List<String> randomUserIds = Lists.newArrayList(userIds);
                    List<String> selectedUserIds = Lists.newArrayList();
                    while (count > 0 && CollectionUtils.isNotEmpty(randomUserIds)) {
                        String userId = randomUserIds.get(random.nextInt(randomUserIds.size()));
                        selectedUserIds.add(userId);
                        randomUserIds.remove(userId);
                        count--;
                    }
                    if (CollectionUtils.isNotEmpty(selectedUserIds)) {
                        simulationData.getTaskUsers().put(getTaskId(data), selectedUserIds);
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            } else if (e instanceof OnlyChooseOneUserException) {
                // 多个办理人选择一个具体办理人时
                String chooseOneUser = runtime.getString("chooseOneUser");
                Map<String, Object> data = (Map<String, Object>) ((OnlyChooseOneUserException) e).getData();
                List<String> userIds = Lists.newArrayList((List<String>) data.get("userIds"));
                Set<String> orgVersionIdSet = (Set<String>) data.get("orgVersionIds");
                // 用户ID按用户拼音排序
                List<UserInfoEntity> userInfoEntities = userInfoService.listByUserIds(Sets.newLinkedHashSet(userIds),
                        null, orgVersionIdSet.toArray(new String[0]));
                List<String> sortedUserIds = userInfoEntities.stream().sorted((u1, u2) -> {
                    if (StringUtils.isBlank(u1.getPinYin())) {
                        return -1;
                    }
                    if (StringUtils.isBlank(u2.getPinYin())) {
                        return 1;
                    }
                    return StringUtils.substring(u1.getPinYin(), 0, 1).compareTo(StringUtils.substring(u2.getPinYin(), 0, 1));
                }).map(u -> u.getUserId()).collect(Collectors.toList());
                userIds.removeAll(sortedUserIds);
                sortedUserIds.addAll(userIds);
                userIds = sortedUserIds;
                runtimeParams.setChooseOneUser(getTaskId(data), chooseOneUser);
                runtimeParams.setCandidateUserIds(getTaskId(data), userIds);
                if (StringUtils.equals("first", chooseOneUser)) {
                    simulationData.getTaskUsers().put(getTaskId(data), userIds.subList(0, 1));
                } else if (StringUtils.equals("random", chooseOneUser)) {
                    Random random = new Random();
                    simulationData.getTaskUsers().put(getTaskId(data), Lists.newArrayList(userIds.get(random.nextInt(userIds.size()))));
                } else if (StringUtils.equals("last", chooseOneUser)) {
                    simulationData.getTaskUsers().put(getTaskId(data), userIds.subList(userIds.size() - 1, userIds.size()));
                } else {
                    throw e;
                }
            } else if (e instanceof JudgmentBranchFlowNotFoundException || e instanceof MultipleJudgmentBranchFlowException) {
                return handleSimulationJudgmentError(e, simulationData, workFlowSettings);
            } else if (e instanceof MultiJobNotSelectedException) {
                // 一人多职流转选择职位时
                String chooseOneJob = runtime.getString("chooseOneJob");
                Map<String, Object> data = (Map<String, Object>) ((MultiJobNotSelectedException) e).getData();
                List<OrgUserJobDto> jobs = (List<OrgUserJobDto>) data.get("jobs");
                if (StringUtils.equals("first", chooseOneJob)) {
                    simulationData.setJobSelected(jobs.get(0).getJobId());
                } else if (StringUtils.equals("random", chooseOneJob)) {
                    Random random = new Random();
                    simulationData.setJobSelected(jobs.get(random.nextInt(jobs.size())).getJobId());
                } else if (StringUtils.equals("last", chooseOneJob)) {
                    simulationData.setJobSelected(jobs.get(jobs.size() - 1).getJobId());
                } else {
                    throw e;
                }
            } else if (e instanceof ChooseArchiveFolderException) {
                // 流程归档选择归档夹时
                String chooseArchiveFolder = runtime.getString("chooseArchiveFolder");
                Map<String, Object> data = (Map<String, Object>) ((ChooseArchiveFolderException) e).getData();
                String assignArchiveFolderUuid = runtime.getString("assignArchiveFolderUuid");
                if (StringUtils.equals("assign", chooseArchiveFolder) && StringUtils.isNotBlank(assignArchiveFolderUuid)) {
                    simulationData.setArchiveFolderUuid(assignArchiveFolderUuid);
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        } else {
            throw e;
        }
        return simulationSubmit(simulationData);
    }

    /**
     * @param e
     * @param simulationData
     * @param workFlowSettings
     * @return
     */
    private ResultMessage handleSimulationJudgmentError(Exception e, FlowSimulationDataDto simulationData, WorkFlowSettings workFlowSettings) throws Exception {
        JSONObject flowSimulation = new JSONObject(workFlowSettings.getFlowSimulation());
        JSONObject runtime = flowSimulation.getJSONObject("runtime");
        Map<String, Object> data = null;
        if (e instanceof JudgmentBranchFlowNotFoundException) {
            data = (Map<String, Object>) ((JudgmentBranchFlowNotFoundException) e).getData();
        } else {
            data = (Map<String, Object>) ((MultipleJudgmentBranchFlowException) e).getData();
        }
        boolean useDirection = BooleanUtils.isTrue((Boolean) data.get("useDirection"));
        boolean multiselect = BooleanUtils.isTrue((Boolean) data.get("multiselect"));
        String fromTaskId = Objects.toString(data.get("fromTaskId"));
        List<Map<String, Object>> toTasks = (List<Map<String, Object>>) data.get("toTasks");
        String chooseOneDirection = runtime.getString("chooseOneDirection");
        String chooseMultiDirection = runtime.getString("chooseMultiDirection");
        int randomDirectionCount = runtime.getInt("randomDirectionCount");
        if (multiselect) {
            if (StringUtils.equals("all", chooseMultiDirection)) {
                setSelectedTasks(fromTaskId, toTasks, useDirection, simulationData);
            } else if (StringUtils.equals("random", chooseMultiDirection)) {
                Random random = new Random();
                int count = Integer.valueOf(randomDirectionCount);
                List<Map<String, Object>> randomTasks = Lists.newArrayList(toTasks);
                List<Map<String, Object>> selectedTasks = Lists.newArrayList();
                while (count > 0 && CollectionUtils.isNotEmpty(randomTasks)) {
                    Map<String, Object> task = randomTasks.get(random.nextInt(randomTasks.size()));
                    selectedTasks.add(task);
                    randomTasks.remove(task);
                    count--;
                }
                if (CollectionUtils.isNotEmpty(selectedTasks)) {
                    setSelectedTasks(fromTaskId, selectedTasks, useDirection, simulationData);
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        } else {
            if (StringUtils.equals("random", chooseOneDirection)) {
                Random random = new Random();
                Map<String, Object> toTask = toTasks.get(random.nextInt(toTasks.size()));
                setSelectedTasks(fromTaskId, Lists.newArrayList(toTask), useDirection, simulationData);
            } else if (StringUtils.equals("traverse", chooseOneDirection)) {
                setSelectedTasks(fromTaskId, toTasks, useDirection, simulationData);
            } else {
                throw e;
            }
        }

        return simulationSubmit(simulationData);
    }

    /**
     * @param toTasks
     * @param useDirection
     * @param simulationData
     */
    private void setSelectedTasks(String fromTaskId, List<Map<String, Object>> toTasks, boolean useDirection, FlowSimulationDataDto simulationData) {
        Map<String, String> toDirectionIds = simulationData.getToDirectionIds();
        Map<String, String> toTaskIds = simulationData.getToTaskIds();
        if (MapUtils.isEmpty(toDirectionIds)) {
            toDirectionIds = Maps.newHashMap();
        }
        if (MapUtils.isEmpty(toTaskIds)) {
            toTaskIds = Maps.newHashMap();
        }
        if (useDirection) {
            String toDirectionId = toTasks.stream().map(toTask -> Objects.toString(toTask.get("directionId")))
                    .collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            toDirectionIds.put(fromTaskId, toDirectionId);
        } else {
            String toTaskId = toTasks.stream().map(toTask -> Objects.toString(toTask.get("id")))
                    .collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            toTaskIds.put(fromTaskId, toTaskId);
        }

        simulationData.setToDirectionIds(toDirectionIds);
        simulationData.setToTaskIds(toTaskIds);
    }

    /**
     * @param data
     * @return
     */
    private String getTaskId(Map<String, Object> data) {
        return (String) data.get("taskId");
    }

    /**
     * 仿真转办数据
     *
     * @param simulationData
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "仿真转办数据", notes = "仿真转办数据")
    @PostMapping("/simulationTransfer")
    public ApiResult<Void> simulationTransfer(@RequestBody FlowSimulationDataDto simulationData) {
        try {
            workFlowSimulationService.simulationTransfer(simulationData);
            return ApiResult.success();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 仿真会签数据
     *
     * @param simulationData
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "仿真会签数据", notes = "仿真会签数据")
    @PostMapping("/simulationCounterSign")
    public ApiResult<Void> simulationCounterSign(@RequestBody FlowSimulationDataDto simulationData) {
        try {
            workFlowSimulationService.simulationCounterSign(simulationData);
            return ApiResult.success();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 仿真跳转数据
     *
     * @param simulationData
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "仿真跳转数据", notes = "仿真跳转数据")
    @PostMapping("/simulationGotoTask")
    public ApiResult<Void> simulationGotoTask(@RequestBody FlowSimulationDataDto simulationData) {
        try {
            workFlowSimulationService.simulationGotoTask(simulationData);
            return ApiResult.success();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 清理仿真数据
     *
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "清理仿真数据", notes = "清理仿真数据")
    @GetMapping("/cleanSimulationData")
    public ApiResult<Void> cleanSimulationData() {
        workFlowSimulationService.cleanSimulationData();
        return ApiResult.success();
    }

    /**
     * 根据流程定义ID列出所有环节
     *
     * @param flowDefUuid
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "根据流程定义UUID列出所有仿真记录", notes = "根据流程定义UUID列出所有仿真记录")
    @GetMapping("/record/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowDefUuid", value = "流程定义UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<WfFlowSimulationRecordEntity>> listRecord(
            @RequestParam(name = "flowDefUuid", required = true) String flowDefUuid) {
        List<WfFlowSimulationRecordEntity> recordEntities = flowSimulationRecordService.listByFlowDefUuid(flowDefUuid);
        // 按创建时间降序
        Collections.sort(recordEntities, (o1, o2) -> {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return -t1.compareTo(t2);
        });
        return ApiResult.success(recordEntities);
    }

    /**
     * 获取仿真记录
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/record/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取仿真记录", notes = "获取仿真记录")
    public ApiResult<WfFlowSimulationRecordDto> getRecord(@RequestParam(name = "uuid", required = true) Long uuid) {
        WfFlowSimulationRecordDto dto = flowSimulationRecordFacadeService.getDto(uuid);
        return ApiResult.success(dto);
    }

    /**
     * 删除仿真记录
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/record/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除仿真记录", notes = "删除仿真记录")
    public ApiResult<Void> deleteRecord(@RequestParam(name = "uuids", required = true) List<Long> uuids) {
        flowSimulationRecordService.deleteAllByUuids(uuids);
        return ApiResult.success();
    }

    /**
     * 更新仿真记录状态
     *
     * @param uuid
     * @param state
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/record/updateState", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "更新仿真记录状态", notes = "更新仿真记录状态")
    public ApiResult<Void> updateState(@RequestParam(name = "uuid", required = true) Long uuid, @RequestParam(name = "state", required = true) String state) {
        flowSimulationRecordService.updateStateByUuid(uuid, state);
        return ApiResult.success();
    }

    /**
     * 获取仿真流程数据
     *
     * @param recordUuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取仿真流程数据", notes = "获取仿真流程数据")
    public ApiResult<FlowSimulationWorkData> getWorkData(@RequestParam(name = "recordUuid", required = true) Long recordUuid,
                                                         @RequestParam(name = "taskInstUuid", required = true) String taskInstUuid) {
        FlowSimulationWorkData simulationWorkData = workFlowSimulationService.getSimulationWorkDataByRecordUuidAndTaskInstUuid(recordUuid, taskInstUuid);
        return ApiResult.success(simulationWorkData);
    }

    /**
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/paramsChanged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断仿真参数是否变更", notes = "判断仿真参数是否变更")
    public ApiResult<Boolean> paramsChanged(@RequestBody List<FlowSimulationDataDto.SimulationParams> params) {
        boolean changed = false;
        if (CollectionUtils.size(params) != 2) {
            return ApiResult.success(changed);
        }

        FlowSimulationDataDto.SimulationParams params1 = params.get(0);
        FlowSimulationDataDto.SimulationParams params2 = params.get(1);
        changed = !StringUtils.equals(JsonUtils.object2Json(params1), JsonUtils.object2Json(params2));
        return ApiResult.success(changed);
    }
}
