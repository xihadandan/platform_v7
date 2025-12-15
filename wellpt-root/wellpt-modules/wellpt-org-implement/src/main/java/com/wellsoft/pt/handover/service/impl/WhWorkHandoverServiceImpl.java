/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.HandoverUtils;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.handover.dao.WhWorkHandoverDao;
import com.wellsoft.pt.handover.dto.*;
import com.wellsoft.pt.handover.entity.*;
import com.wellsoft.pt.handover.enums.HandoverContentTypeEnum;
import com.wellsoft.pt.handover.enums.HandoverItemStatusEnum;
import com.wellsoft.pt.handover.enums.WorkHandoverStatusEnum;
import com.wellsoft.pt.handover.service.*;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.support.MessageParams;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.entity.AclEntry;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Description: 工作交接的service服务接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
@Service
public class WhWorkHandoverServiceImpl extends AbstractJpaServiceImpl<WhWorkHandoverEntity, WhWorkHandoverDao, String>
        implements WhWorkHandoverService {
    @Autowired
    private WhWorkTypeToHandoverService whWorkTypeToHandoverService;
    @Autowired
    private CdDataStoreService cdDataStoreService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private AclService aclService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private WhWorkHandoverItemService whWorkHandoverItemService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private WhWorkSettingsService whWorkSettingsService;
    @Autowired
    private WhFlowDatasRecordService whFlowDatasRecordService;
    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;
    @Autowired
    private FlowDelegationSettingsService flowDelegationSettingsService;

    @Override
    public GetWorkHandoverByUuidDto getWorkHandoverByUuid(String handoverUuid) {
        WhWorkHandoverEntity whWorkHandoverEntity = this.getOne(handoverUuid);
        if (whWorkHandoverEntity == null) {
            throw new BusinessException("找不到数据，请检查handoverUuid参数");
        }

        GetWorkHandoverByUuidDto getWorkHandoverByUuidDto = new GetWorkHandoverByUuidDto();
        BeanUtils.copyProperties(whWorkHandoverEntity, getWorkHandoverByUuidDto);
        // 创建时间特殊处理
        int year = DateUtil.year(new Date());
        String createTimeStr = DateUtil.formatDateTime(whWorkHandoverEntity.getCreateTime());
        if (StringUtils.isNotBlank(createTimeStr)) {
            createTimeStr = createTimeStr.substring(0, createTimeStr.length() - 3);
            createTimeStr = createTimeStr.replace(String.valueOf(year) + "-", "");
            getWorkHandoverByUuidDto.setCreateTime(createTimeStr);
        }

        // 执行时间特殊处理
        String handoverWorkTimeStr = DateUtil.formatDateTime(whWorkHandoverEntity.getHandoverWorkTime());
        if (StringUtils.isNotBlank(handoverWorkTimeStr)) {
            handoverWorkTimeStr = handoverWorkTimeStr.replace(String.valueOf(year) + "-", "");
            handoverWorkTimeStr = handoverWorkTimeStr.substring(0, handoverWorkTimeStr.length() - 3);
            getWorkHandoverByUuidDto.setHandoverWorkTime(handoverWorkTimeStr);
        } else {
            // 未执行
            String dateStr = "";
            WhWorkSettingsEntity whWorkSettingsEntity = whWorkSettingsService
                    .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
            if (whWorkSettingsEntity == null) {
                dateStr = DateUtil.formatDateTime(HandoverUtils.getWorkDateTime("01:00"));
            } else {
                dateStr = DateUtil.formatDateTime(HandoverUtils.getWorkDateTime(whWorkSettingsEntity.getWorkTime()));
            }
            dateStr = dateStr.substring(0, dateStr.length() - 3);
            dateStr = dateStr.replace(String.valueOf(year) + "-", "");
            getWorkHandoverByUuidDto.setHandoverWorkTime(dateStr);
        }

        List<WhWorkTypeToHandoverEntity> whWorkTypeToHandoverEntityList = whWorkTypeToHandoverService
                .getAllListByWorkHandoverUuid(handoverUuid);
        List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtoList = Lists.newArrayList();
        List<WhWorkTypeToHandoverCountDto> workTypeToHandoverCountDtoList = Lists.newArrayList();
        if (whWorkTypeToHandoverEntityList.size() > 0) {
            WhWorkTypeToHandoverCountItemDto typeToHandoverCountItemDto = null;
            WhWorkTypeToHandoverCountDto workTypeToHandoverCountDto = null;

            for (WhWorkTypeToHandoverEntity whWorkTypeToHandoverEntity : whWorkTypeToHandoverEntityList) {
                typeToHandoverCountItemDto = new WhWorkTypeToHandoverCountItemDto();
                workTypeToHandoverCountDto = new WhWorkTypeToHandoverCountDto();
                BeanUtils.copyProperties(whWorkTypeToHandoverEntity, typeToHandoverCountItemDto);
                whWorkTypeToHandoverCountItemDtoList.add(typeToHandoverCountItemDto);
                BeanUtils.copyProperties(typeToHandoverCountItemDto, workTypeToHandoverCountDto);
                workTypeToHandoverCountDtoList.add(workTypeToHandoverCountDto);
            }
        }
        getWorkHandoverByUuidDto.setWhWorkTypeToHandoverCountItemDtoList(whWorkTypeToHandoverCountItemDtoList);
        WhFlowDatasRecordEntity flowDatasRecordEntity = whFlowDatasRecordService
                .getDatasRecordByHandoverUuid(handoverUuid);
        if (flowDatasRecordEntity != null) {
            for (WhWorkTypeToHandoverCountDto typeToHandoverCountDto : workTypeToHandoverCountDtoList) {
                HandoverContentTypeEnum handoverContentTypeEnum = HandoverContentTypeEnum
                        .getByValue(typeToHandoverCountDto.getHandoverContentType());
                switch (handoverContentTypeEnum) {
                    // 待办流程
                    case TODO:
                        typeToHandoverCountDto.setCount(
                                flowDatasRecordEntity.getTodoCount() == null ? 0 : flowDatasRecordEntity.getTodoCount());
                        break;
                    // 查阅流程
                    case CONSULT:
                        typeToHandoverCountDto.setCount(flowDatasRecordEntity.getConsultCount() == null ? 0
                                : flowDatasRecordEntity.getConsultCount());
                        break;
                    // 监控流程
                    case MONITOR:
                        typeToHandoverCountDto.setCount(flowDatasRecordEntity.getMonitorCount() == null ? 0
                                : flowDatasRecordEntity.getMonitorCount());
                        break;
                    // 已办流程
                    case DONE:
                        typeToHandoverCountDto.setCount(
                                flowDatasRecordEntity.getDoneCount() == null ? 0 : flowDatasRecordEntity.getDoneCount());
                        break;
                    // 督办流程
                    case SUPERVISE:
                        typeToHandoverCountDto.setCount(flowDatasRecordEntity.getSuperviseCount() == null ? 0
                                : flowDatasRecordEntity.getSuperviseCount());
                        break;
                    default:
                        break;
                }
            }
        }
        getWorkHandoverByUuidDto.setWorkTypeToHandoverCountDtoList(workTypeToHandoverCountDtoList);
        getWorkHandoverByUuidDto.setModifierName(orgApiFacade.getUserNameById(getWorkHandoverByUuidDto.getModifier()));
        return getWorkHandoverByUuidDto;
    }

    @Override
    @Transactional
    public void deleteWorkHandover(String workHandoverUuid) {
        this.delete(workHandoverUuid);
        whWorkTypeToHandoverService.deleteByWorkHandoverUuid(workHandoverUuid);
    }

    @Override
    public void saveWorkHandoverNow(SaveWhWorkHandoverDto saveWhWorkHandoverDto) {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        String userId = SpringSecurityUtils.getCurrentUserId();

        // 保存工作交接内容
        String workHandoverUuid = saveWorkHandover(saveWhWorkHandoverDto, WorkHandoverStatusEnum.Execution);
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                IgnoreLoginUtils.login(tenantId, userId);
                asyncWorkHandover(saveWhWorkHandoverDto, workHandoverUuid);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("异步处理工作交接的部分内容失败", e);
            } finally {
                IgnoreLoginUtils.logout();
            }
            System.out.println("future finished!");
            return "future finished!";
        });

    }

    @Override
    public void saveWorkHandoverFree(SaveWhWorkHandoverDto saveWhWorkHandoverDto) {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        String userId = SpringSecurityUtils.getCurrentUserId();

        // 保存工作交接内容
        String workHandoverUuid = saveWorkHandover(saveWhWorkHandoverDto, WorkHandoverStatusEnum.NotExecution);
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                IgnoreLoginUtils.login(tenantId, userId);
                asyncWorkHandover(saveWhWorkHandoverDto, null);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("异步处理工作交接的部分内容失败", e);
            } finally {
                IgnoreLoginUtils.logout();
            }
            System.out.println("future finished!");
            return "future finished!";
        });

        // xxlJob执行需要的参数
        ExecutionParam executionParam = new ExecutionParam().setTenantId(SpringSecurityUtils.getCurrentTenantId())
                .setUserId(SpringSecurityUtils.getCurrentUserId()).putKeyVal("workHandoverUuid", workHandoverUuid);
        String param = executionParam.toJson();
        // xxlJob定义
        TmpJobParam.Builder builder = TmpJobParam.toBuilder().setJobDesc("异步处理工作交接")
                .setExecutorHandler(JobHandlerName.Temp.WorkHandoverHandlerJob);
        // xxlJob执行时间+参数
        WhWorkSettingsEntity workSettingsEntity = whWorkSettingsService
                .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        String workTime = "";
        if (workSettingsEntity == null) {
            workTime = "01:00";
        } else {
            workTime = workSettingsEntity.getWorkTime();
        }
        Date date = HandoverUtils.getWorkDateTime(workTime);
        builder.addExecutionTimeParams(date, param);
        // 远程调用添加到xxlJobAdmin 并启动
        xxlJobService.addTmpStart(builder.build());
    }

    @Override
    public List<WhWorkTypeToHandoverCountDto> getFlowDatasRecords(GetFlowDatasRecordsDto getFlowDatasRecordsDto) {
        List<WhWorkTypeToHandoverCountDto> handoverCountDtos = Lists.newArrayList();
        // 获取满足选定的流程分类或流程定义集合
        String[] handoverContentsIdStrs = getFlowDatasRecordsDto.getHandoverContentsId().split(";");
        List<String> handoverContentsIds = Lists.newArrayList();
        for (String handoverContentsIdStr : handoverContentsIdStrs) {
            handoverContentsIds.add(handoverContentsIdStr);
        }
        // 取的值不是uuid而是id
        List<FlowDefinition> flowDefinitions = flowDefinitionService.getListByIdsOrCategoryUuids(handoverContentsIds);
        if (CollectionUtils.isEmpty(flowDefinitions)) {
            return handoverCountDtos;
        }
        if (CollectionUtils.isEmpty(getFlowDatasRecordsDto.getWhWorkTypeToHandoverCountItemDtos())) {
            return handoverCountDtos;
        }
        DataStoreData dataStoreData = null;
        List<Map<String, Object>> datas = null;
        // 工作类型对应的交接内容
        WhWorkTypeToHandoverCountDto handoverCountDto = null;
        for (WhWorkTypeToHandoverCountItemDto whWorkTypeToHandoverCountItemDto : getFlowDatasRecordsDto
                .getWhWorkTypeToHandoverCountItemDtos()) {
            HandoverContentTypeEnum handoverContentTypeEnum = HandoverContentTypeEnum
                    .getByValue(whWorkTypeToHandoverCountItemDto.getHandoverContentType());
            handoverCountDto = new WhWorkTypeToHandoverCountDto();
            BeanUtils.copyProperties(whWorkTypeToHandoverCountItemDto, handoverCountDto);
            switch (handoverContentTypeEnum) {
                // 待办流程
                case TODO:
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_TODO_USERID",
                            getFlowDatasRecordsDto.getHandoverPersonId());
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    handoverCountDto.setCount(datas.size());
                    break;
                // 查阅流程
                case CONSULT:
                    // 查阅流程包含抄送和设置了阅读者权限的
                    // 有阅读者权限
                    List<AclEntry> aclEntries = aclService
                            .getConsultListByUserId(getFlowDatasRecordsDto.getHandoverPersonId());
                    List<TaskInstance> taskInstances = getIntersectionFlowDefinitions(flowDefinitions, aclEntries);
                    // 抄送
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_COPY_TO_USERID",
                            getFlowDatasRecordsDto.getHandoverPersonId());
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    handoverCountDto.setCount(taskInstances.size() + datas.size());
                    break;
                // 监控流程
                case MONITOR:
                    // 不含已办
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_MONITOR_INCLUDE_OVER_USERID",
                            getFlowDatasRecordsDto.getHandoverPersonId());
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {

                        filterEndFlow(dataStoreData);
                    }
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    handoverCountDto.setCount(datas.size());
                    break;
                // 已办流程
                case DONE:
                    // 不含已办
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {
                        dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_DONE_USERID",
                                getFlowDatasRecordsDto.getHandoverPersonId());
                        // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集

                    } else {
                        // 含已办
                        dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_DONE_INCLUDE_OVER_USERID",
                                getFlowDatasRecordsDto.getHandoverPersonId());
                        // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集

                    }
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    handoverCountDto.setCount(datas.size());
                    break;
                // 督办流程
                case SUPERVISE:
                    // 不含已办
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_SUPERVISE_USERID",
                            getFlowDatasRecordsDto.getHandoverPersonId());
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {
                        filterEndFlow(dataStoreData);
                    }
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    handoverCountDto.setCount(datas.size());
                    break;
                default:
                    break;
            }
            handoverCountDtos.add(handoverCountDto);

        }

        return handoverCountDtos;
    }

    @Override
    public Integer checkWorkFlowTaskDelegation(String handoverPersonId) {
        List<String> handoverPersonIds = Lists.newArrayList();
        handoverPersonIds.add(handoverPersonId);
        List<FlowDelegationSettings> flowDelegationSettings = flowDelegationSettingsService
                .getNotDeactiveByUserIds(handoverPersonIds);
        return flowDelegationSettings.size();
    }

    /**
     * 通过数据仓库ID和指定用户，获取数据仓库列表数据
     *
     * @param
     * @return com.wellsoft.pt.basicdata.datastore.bean.DataStoreData
     **/
    private DataStoreData getDataStoreDataByStoreId(String storeId, String userId) {
        DataStoreParams dataStoreParams = new DataStoreParams();
        dataStoreParams.setDataStoreId(storeId);
        Map<String, Object> params = Maps.newHashMap();
        params.put("handoverUserId", userId);
        dataStoreParams.setParams(params);
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        return dataStoreData;
    }

    /**
     * //流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
     *
     * @param flowDefinitions
     * @param aclEntries      权限列表
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     **/
    private List<TaskInstance> getIntersectionFlowDefinitions(List<FlowDefinition> flowDefinitions,
                                                              List<AclEntry> aclEntries) {
        if (CollectionUtils.isEmpty(flowDefinitions) || CollectionUtils.isEmpty(aclEntries)) {
            return Lists.newArrayList();
        }
        List<Map<String, Object>> datas = Lists.newArrayList();
        List<String> taskInstanceUuids = Lists.newArrayList();
        for (AclEntry aclEntry : aclEntries) {
            taskInstanceUuids.add(aclEntry.getObjectIdIdentity());
        }
        List<TaskInstance> intersectionDatas = Lists.newArrayList();
        List<TaskInstance> taskInstances = taskInstanceService.listByUuids(taskInstanceUuids);
        if (CollectionUtils.isNotEmpty(taskInstances)) {
            for (TaskInstance taskInstance : taskInstances) {
                for (FlowDefinition flowDefinition : flowDefinitions) {
                    if (flowDefinition != null && StringUtils.isNotBlank(flowDefinition.getId())) {
                        // 交集的流程定义
                        if (flowDefinition.getId().equals(taskInstance.getFlowDefinition().getId())) {
                            intersectionDatas.add(taskInstance);
                            break;
                        }
                    }
                }
            }
        }

        return intersectionDatas;
    }

    /**
     * //流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
     *
     * @param flowDefinitions
     * @param dataStoreData
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     **/
    private List<Map<String, Object>> getIntersectionFlowDefinitions(List<FlowDefinition> flowDefinitions,
                                                                     DataStoreData dataStoreData) {
        if (CollectionUtils.isEmpty(flowDefinitions)) {
            return Lists.newArrayList();
        }
        List<Map<String, Object>> datas = Lists.newArrayList();
        for (Map<String, Object> datum : dataStoreData.getData()) {

            if (datum == null) {
                continue;
            }
            for (FlowDefinition flowDefinition : flowDefinitions) {
                if (flowDefinition != null && StringUtils.isNotBlank(flowDefinition.getId())) {
                    // 交集的流程定义
                    if (flowDefinition.getId().equals((String) datum.get("flowDefId"))) {
                        datas.add(datum);
                        break;
                    }
                }
            }
        }
        return datas;
    }

    /**
     * 交接结果对应类型的数量所对应的流程
     *
     * @param saveWhWorkHandoverDto
     * @return 返回的whWorkHandoverUuid为null 后续要赋值
     **/
    private List<WhWorkHandoverItemEntity> getwhWorkHandoverItemEntities(SaveWhWorkHandoverDto saveWhWorkHandoverDto) {
        List<WhWorkHandoverItemEntity> handoverItemEntityList = Lists.newArrayList();

        String handoverPersonId = saveWhWorkHandoverDto.getHandoverPersonId();
        // 工作类型对应的交接内容集合
        List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtos = saveWhWorkHandoverDto
                .getWhWorkTypeToHandoverCountItemDtos();
        String handoverContentsIdsStr = saveWhWorkHandoverDto.getHandoverContentsId();
        String[] handoverContentsIds = handoverContentsIdsStr.split(";");
        // 交接内容：流程定义内容 包含流程分类或流程定义 流程定义内容有值，则跟工作类型对应的交接内容集合数据取交集
        List<String> handoverContentsIdList = Lists.newArrayList();
        for (String handoverContentsId : handoverContentsIds) {
            handoverContentsIdList.add(handoverContentsId);
        }
        // 取的值不是uuid而是id
        List<FlowDefinition> flowDefinitions = flowDefinitionService
                .getListByIdsOrCategoryUuids(handoverContentsIdList);
        DataStoreData dataStoreData = null;
        List<Map<String, Object>> datas = null;
        for (WhWorkTypeToHandoverCountItemDto whWorkTypeToHandoverCountItemDto : whWorkTypeToHandoverCountItemDtos) {
            HandoverContentTypeEnum handoverContentTypeEnum = HandoverContentTypeEnum
                    .getByValue(whWorkTypeToHandoverCountItemDto.getHandoverContentType());
            WhWorkHandoverItemEntity itemEntity = new WhWorkHandoverItemEntity();
            switch (handoverContentTypeEnum) {
                // 待办流程
                case TODO:
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_TODO_USERID", handoverPersonId);
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    addWorkHandoverItems(datas, handoverItemEntityList, HandoverContentTypeEnum.TODO);
                    break;
                // 查阅流程
                case CONSULT:
                    // 查阅流程包含抄送和设置了阅读者权限的
                    List<AclEntry> aclEntries = aclService.getConsultListByUserId(handoverPersonId);
                    List<TaskInstance> taskInstances = getIntersectionFlowDefinitions(flowDefinitions, aclEntries);
                    addWorkHandoverItemsByTaskInstances(taskInstances, handoverItemEntityList,
                            HandoverContentTypeEnum.CONSULT);
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_COPY_TO_USERID", handoverPersonId);
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    addWorkHandoverItems(datas, handoverItemEntityList, HandoverContentTypeEnum.CONSULT);
                    break;
                // 监控流程
                case MONITOR:
                    // 不含已办
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_MONITOR_INCLUDE_OVER_USERID",
                            handoverPersonId);
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {

                        filterEndFlow(dataStoreData);
                    }
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    addWorkHandoverItems(datas, handoverItemEntityList, HandoverContentTypeEnum.MONITOR);
                    break;
                // 已办流程
                case DONE:
                    // 不含已办
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {
                        dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_DONE_USERID", handoverPersonId);
                        // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集

                    } else {
                        // 含已办
                        dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_DONE_INCLUDE_OVER_USERID",
                                handoverPersonId);
                        // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集

                    }
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    addWorkHandoverItems(datas, handoverItemEntityList, HandoverContentTypeEnum.DONE);
                    break;
                // 督办流程
                case SUPERVISE:
                    // 不含已办
                    dataStoreData = getDataStoreDataByStoreId("CD_DS_WORK_FLOW_SUPERVISE_USERID",
                            handoverPersonId);
                    if (whWorkTypeToHandoverCountItemDto.getCompletedFlowFlag() == 0) {
                        filterEndFlow(dataStoreData);
                    }
                    // 流程分类和流程定义与上面的交接内容选项在逻辑处理时，对应的流程实例数据是取交集
                    datas = getIntersectionFlowDefinitions(flowDefinitions, dataStoreData);
                    addWorkHandoverItems(datas, handoverItemEntityList, HandoverContentTypeEnum.SUPERVISE);
                    break;
                default:
                    break;
            }
        }

        return handoverItemEntityList;
    }

    /**
     * 执行工作交接任务接口
     *
     * @param workHandoverUuid
     * @return void
     **/
    @Transactional
    public void handoverTask(String workHandoverUuid) {
        WhWorkHandoverService workHandoverService = ApplicationContextHolder.getBean(WhWorkHandoverService.class);

        WhWorkHandoverEntity whWorkHandoverEntity = workHandoverService.getOne(workHandoverUuid);
        whWorkHandoverEntity.setWorkHandoverStatus(WorkHandoverStatusEnum.Execution.getValue());
        if (whWorkHandoverEntity.getHandoverWorkTime() == null) {
            whWorkHandoverEntity.setHandoverWorkTime(new Date());
        }
        workHandoverService.save(whWorkHandoverEntity);
        workHandoverService.flushSession();
        // 通过workHandoverUuid 获取工作交接项 按交接项类型进行区分
        List<WhWorkHandoverItemEntity> whWorkHandoverItemEntityList = whWorkHandoverItemService
                .getListByWorkHandoverUuid(workHandoverUuid);
        if (whWorkHandoverItemEntityList.size() == 0) {
            // 直接修改工作交接状态
            whWorkHandoverEntity.setWorkHandoverStatus(WorkHandoverStatusEnum.Completed.getValue());
            workHandoverService.save(whWorkHandoverEntity);
        }
        Map<String, List<WhWorkHandoverItemEntity>> workhandoverItemMap = Maps.newHashMap();
        for (WhWorkHandoverItemEntity itemEntity : whWorkHandoverItemEntityList) {
            List<WhWorkHandoverItemEntity> itemEntityList = workhandoverItemMap.get(itemEntity.getHandoverItemType());
            if (itemEntityList == null) {
                itemEntityList = Lists.newArrayList();
            }
            itemEntityList.add(itemEntity);
            workhandoverItemMap.put(itemEntity.getHandoverItemType(), itemEntityList);
        }
        WhFlowDatasRecordEntity flowDatasRecordEntity = new WhFlowDatasRecordEntity();
        for (String itemTypeKey : workhandoverItemMap.keySet()) {
            List<WhWorkHandoverItemEntity> itemEntityList = workhandoverItemMap.get(itemTypeKey);
            if (CollectionUtils.isEmpty(itemEntityList)) {
                continue;
            }
            HandoverContentTypeEnum handoverContentTypeEnum = HandoverContentTypeEnum.getByValue(itemTypeKey.trim());
            // 待办流程
            switch (handoverContentTypeEnum) {
                case TODO:
                    for (WhWorkHandoverItemEntity itemEntity : itemEntityList) {
                        taskService.addTodoPermission(whWorkHandoverEntity.getReceiverId(), itemEntity.getDataUuid());
                        taskService.removeTodoPermission(whWorkHandoverEntity.getHandoverPersonId(),
                                itemEntity.getDataUuid());
                    }
                    // 会签待办特殊处理
                    Integer signTodo = 2;
                    List<TaskIdentity> taskIdentityList = identityService
                            .getTodoByUserIdAndTodoType(whWorkHandoverEntity.getHandoverPersonId(), signTodo);
                    TaskIdentity newTaskIdentity = null;
                    for (TaskIdentity taskIdentity : taskIdentityList) {
                        newTaskIdentity = new TaskIdentity();
                        BeanUtils.copyProperties(taskIdentity, newTaskIdentity);
                        newTaskIdentity.setUuid(null);
                        newTaskIdentity.setUserId(whWorkHandoverEntity.getReceiverId());
                        newTaskIdentity.setOwnerId(whWorkHandoverEntity.getReceiverId());
                        identityService.addTodo(newTaskIdentity);
                        identityService.removeTodo(taskIdentity);
                    }
                    flowDatasRecordEntity.setTodoCount(itemEntityList.size() + taskIdentityList.size());
                    break;
                // 查阅流程
                case CONSULT:

                    for (WhWorkHandoverItemEntity itemEntity : itemEntityList) {
                        taskService.addConsultPermission(whWorkHandoverEntity.getReceiverId(), itemEntity.getDataUuid());
                    }
                    flowDatasRecordEntity.setConsultCount(itemEntityList.size());
                    break;
                // 监控流程
                case MONITOR:
                    for (WhWorkHandoverItemEntity itemEntity : itemEntityList) {
                        taskService.addMonitorPermission(whWorkHandoverEntity.getReceiverId(), itemEntity.getDataUuid());
                    }
                    flowDatasRecordEntity.setMonitorCount(itemEntityList.size());
                    break;
                // 已办流程
                case DONE:
                    for (WhWorkHandoverItemEntity itemEntity : itemEntityList) {
                        taskService.addDonePermission(whWorkHandoverEntity.getReceiverId(), itemEntity.getDataUuid());
                    }
                    flowDatasRecordEntity.setDoneCount(itemEntityList.size());
                    break;
                // 督办流程
                case SUPERVISE:
                    for (WhWorkHandoverItemEntity itemEntity : itemEntityList) {
                        taskService.addSupervisePermission(whWorkHandoverEntity.getReceiverId(), itemEntity.getDataUuid());
                    }
                    flowDatasRecordEntity.setSuperviseCount(itemEntityList.size());
                    break;
                default:
                    break;
            }
        }
        whWorkHandoverEntity.setWorkHandoverStatus(WorkHandoverStatusEnum.Completed.getValue());
        workHandoverService.save(whWorkHandoverEntity);
        flowDatasRecordEntity.setWhWorkHandoverUuid(whWorkHandoverEntity.getUuid());
        whFlowDatasRecordService.save(flowDatasRecordEntity);

        // 消息通知 交接完成通知
        sendHandoverCompleteMessage(whWorkHandoverEntity);
        workHandoverService.flushSession();

    }

    /**
     * 保存工作交接内容
     *
     * @param saveWhWorkHandoverDto  工作交接内容
     * @param workHandoverStatusEnum 交接状态
     * @return 工作交接uuid
     **/
    @Transactional
    String saveWorkHandover(SaveWhWorkHandoverDto saveWhWorkHandoverDto,
                            WorkHandoverStatusEnum workHandoverStatusEnum) {
        Boolean isUpdate = Boolean.FALSE;
        if (StringUtils.isNotBlank(saveWhWorkHandoverDto.getUuid())) {
            isUpdate = Boolean.TRUE;
        }
        // 保存主表工作交接数据
        WhWorkHandoverEntity entity = new WhWorkHandoverEntity();
        if (isUpdate) {
            entity = this.getOne(saveWhWorkHandoverDto.getUuid());
        }
        BeanUtils.copyProperties(saveWhWorkHandoverDto, entity);
        entity.setWorkHandoverStatus(workHandoverStatusEnum.getValue());
        if (WorkHandoverStatusEnum.Execution.equals(workHandoverStatusEnum)) {
            entity.setHandoverWorkTime(new Date());
        }
        this.save(entity);

        if (isUpdate) {
            whWorkTypeToHandoverService.deleteByWorkHandoverUuid(saveWhWorkHandoverDto.getUuid());
            whWorkHandoverItemService.deleteByWorkHandoverUuid(saveWhWorkHandoverDto.getUuid());
        }
        // 保存 工作类型对应的交接内容集合
        List<WhWorkTypeToHandoverCountItemDto> workTypeToHandoverCountItemDtos = saveWhWorkHandoverDto
                .getWhWorkTypeToHandoverCountItemDtos();
        WhWorkTypeToHandoverEntity whWorkTypeToHandoverEntity = null;
        List<WhWorkTypeToHandoverEntity> whWorkTypeToHandoverEntities = Lists.newArrayList();
        for (WhWorkTypeToHandoverCountItemDto workTypeToHandoverCountItemDto : workTypeToHandoverCountItemDtos) {
            whWorkTypeToHandoverEntity = new WhWorkTypeToHandoverEntity();
            BeanUtils.copyProperties(workTypeToHandoverCountItemDto, whWorkTypeToHandoverEntity);
            whWorkTypeToHandoverEntity.setWhWorkHandoverUuid(entity.getUuid());
            whWorkTypeToHandoverEntities.add(whWorkTypeToHandoverEntity);
        }
        whWorkTypeToHandoverService.saveAll(whWorkTypeToHandoverEntities);
        this.flushSession();
        return entity.getUuid();

    }

    /**
     * 消息通知 交接完成通知
     *
     * @param whWorkHandoverEntity
     * @return void
     **/
    private void sendHandoverCompleteMessage(WhWorkHandoverEntity whWorkHandoverEntity) {
        // 消息通知 交接完成通知（交接人）
        Set<String> recipientIds = new HashSet<>();
        Set<String> recipientNames = new HashSet<>();
        Map<Object, Object> dataMap = new HashMap<>();
        recipientIds.add(whWorkHandoverEntity.getCreator());
        recipientNames.add(orgApiFacade.getUserNameById(whWorkHandoverEntity.getCreator()));
        dataMap.put("receiverName", whWorkHandoverEntity.getReceiverName());
        dataMap.put("handoverPersonName", whWorkHandoverEntity.getHandoverPersonName());
        dataMap.put("workTypeName", whWorkHandoverEntity.getHandoverWorkTypeName());
        dataMap.put("uuid", whWorkHandoverEntity.getUuid());
        MessageParams params = new MessageParams();
        params.setTemplateId("MSG_HANDOVER_COMPLETE_TO_CREATOR_MESSAGE");
        params.setRecipientIds(recipientIds);
        params.setRecipientNames(recipientNames);
        params.setDataMap(dataMap);
        messageClientApiFacade.sendByParams(params);

        if (whWorkHandoverEntity.getNoticeHandoverPersonFlag() == 1) {
            params = new MessageParams();
            // 消息通知 交接完成通知（交接人）
            recipientIds = new HashSet<>();
            recipientNames = new HashSet<>();
            recipientIds.add(whWorkHandoverEntity.getReceiverId());
            recipientNames.add(whWorkHandoverEntity.getReceiverName());
            params.setTemplateId("MSG_HANDOVER_COMPLETE_TO_RECIPIENT_MESSAGE");
            params.setRecipientIds(recipientIds);
            params.setRecipientNames(recipientNames);
            params.setDataMap(dataMap);
            messageClientApiFacade.sendByParams(params);
        }

    }

    /**
     * 添加工作交接项
     *
     * @param datas
     * @param handoverItemEntityList
     * @param typeEnum
     * @return void
     **/
    private void addWorkHandoverItems(List<Map<String, Object>> datas,
                                      List<WhWorkHandoverItemEntity> handoverItemEntityList, HandoverContentTypeEnum typeEnum) {
        for (Map<String, Object> data : datas) {
            WhWorkHandoverItemEntity entity = new WhWorkHandoverItemEntity();
            entity.setHandoverItemType(typeEnum.getValue().trim());
            entity.setDataUuid((String) data.get("uuid"));
            entity.setHandoverItemStatus(HandoverItemStatusEnum.WaitHandover.getValue());
            handoverItemEntityList.add(entity);
        }
    }

    /**
     * 添加工作交接项
     *
     * @param taskInstances          环节实例列表
     * @param handoverItemEntityList
     * @param typeEnum
     * @return void
     **/
    private void addWorkHandoverItemsByTaskInstances(List<TaskInstance> taskInstances,
                                                     List<WhWorkHandoverItemEntity> handoverItemEntityList, HandoverContentTypeEnum typeEnum) {
        for (TaskInstance taskInstance : taskInstances) {
            WhWorkHandoverItemEntity entity = new WhWorkHandoverItemEntity();
            entity.setHandoverItemType(typeEnum.getValue());
            entity.setDataUuid(taskInstance.getUuid());
            handoverItemEntityList.add(entity);
        }
    }

    /**
     * //获取未执行的工作交接列表，按创建时间升序
     *
     * @param
     **/
    private List<WhWorkHandoverEntity> getTodoWorkHandoverList() {
        Map<String, Object> values = Maps.newHashMap();
        return this.dao.listByNameSQLQuery("getTodoWorkHandoverList", values);
    }

    /**
     * 过滤去掉已完结的流程
     *
     * @param dataStoreData
     * @return void
     **/
    private void filterEndFlow(DataStoreData dataStoreData) {
        List<Map<String, Object>> mapList = dataStoreData.getData();
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> flowObject = mapList.get(i);
            if (flowObject.get("endTime") != null) {
                dataStoreData.getData().set(i, null);
            }
        }
    }

    /**
     * 异步处理工作交接的部分内容
     *
     * @param saveWhWorkHandoverDto
     * @param workHandoverUuid      为null时，不执行
     * @return void
     **/
    @Transactional
    void asyncWorkHandover(SaveWhWorkHandoverDto saveWhWorkHandoverDto, String workHandoverUuid) {
        // 交接结果对应类型的数量所对应的流程
        List<WhWorkHandoverItemEntity> whWorkHandoverItemEntities = getwhWorkHandoverItemEntities(
                saveWhWorkHandoverDto);
        // 交接结果对应类型的数量所对应的流程，保存到工作交接项表
        for (WhWorkHandoverItemEntity whWorkHandoverItemEntity : whWorkHandoverItemEntities) {
            whWorkHandoverItemEntity.setWhWorkHandoverUuid(workHandoverUuid);
        }
        whWorkHandoverItemService.saveAll(whWorkHandoverItemEntities);
        // 调用执行工作交接任务接口
        if (StringUtils.isNotBlank(workHandoverUuid)) {
            handoverTask(workHandoverUuid);
        }

    }

}
