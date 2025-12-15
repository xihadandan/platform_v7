package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.context.event.WorkFlowBuildIndexEvent;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.org.service.impl.WorkflowOrgServiceImpl;
import com.wellsoft.pt.bpm.engine.service.FlowIndexDocumentService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
import com.wellsoft.pt.fulltext.service.IndexDocTemplateService;
import com.wellsoft.pt.fulltext.service.WellFlowDoucmentIndexService;
import com.wellsoft.pt.fulltext.support.Attachment;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.fulltext.utils.AttachmentUtils;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月10日   chenq	 Create
 * </pre>
 */
@Service
public class FlowIndexDocumentServiceImpl extends WellptTransactionalEventListener<WorkFlowBuildIndexEvent> implements FlowIndexDocumentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IndexDocTemplateService indexDocTemplateService;
    @Autowired
    TaskInstanceService taskInstanceService;
    @Autowired
    private TaskService taskService;
    @Autowired
    FlowInstanceService flowInstanceService;
    //    @Autowired
//    OrgApiFacade orgApiFacade;
    @Autowired
    WorkflowOrgServiceImpl workflowOrgService;
    @Autowired
    WellFlowDoucmentIndexService wellFlowDoucmentIndexService;
    @Autowired
    DyFormFacade dyFormFacade;
    @Autowired(required = false)
    RestHighLevelClient restHighLevelClient;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    @Transactional(readOnly = true)
    public void indexWorkflowDocument(TaskData taskData) {
        if (restHighLevelClient == null) {
            return;
        }
        FulltextSetting fulltextSetting = getFulltextSetting(taskData);
        if (BooleanUtils.isNotTrue(fulltextSetting.getEnabled()) || StringUtils.equals(fulltextSetting.getUpdateMode(), FulltextSetting.UPDATE_MODE_REGULAR)) {
            return;
        }

        String system = fulltextSetting.getSystem();
        if (StringUtils.isBlank(system) && StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            system = RequestSystemContextPathResolver.system();
        }
        if (StringUtils.isBlank(system)) {
            system = flowInstanceService.getOne(taskData.getFlowInstUuid()).getSystem();
        }
        ApplicationContextHolder.getApplicationContext().publishEvent(new WorkFlowBuildIndexEvent(taskData, system));
    }

    private FulltextSetting getFulltextSetting(TaskData taskData) {
        FulltextSetting fulltextSetting = (FulltextSetting) taskData.get("_fulltextSetting");
        if (fulltextSetting == null) {
            fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(RequestSystemContextPathResolver.system());
            taskData.put("_fulltextSetting", fulltextSetting);
        }
        return fulltextSetting;
    }

    private void asyncIndex(WellFlowDocumentIndex index) {
        if (index != null) {
            wellFlowDoucmentIndexService.asyncIndex(index);
        }
    }

    @Override
    public WellFlowDocumentIndex buildIndex(TaskData taskData) {
        return buildIndex(taskData.getFlowInstUuid(), taskData);
    }

    @Override
    public void buildAndSaveIndex(TaskData taskData) {
        WellFlowDocumentIndex index = buildIndex(taskData);
        if (index != null) {
            wellFlowDoucmentIndexService.index(index);
        }
    }

    @Transactional(readOnly = true)
    public WellFlowDocumentIndex buildIndex(String flowInstUuid, TaskData taskData) {
        try {
            FlowInstance flowInstance = flowInstanceService.getOne(flowInstUuid);
            FlowDefinition flowDefinition = flowInstance.getFlowDefinition();

            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
            Map<String, Object> urlData = Maps.newHashMap();
            DyFormData dyFormData = taskData.getDyFormData(flowInstance.getDataUuid());
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(flowInstance.getFormUuid(), flowInstance.getDataUuid());
                taskData.setDyFormData(flowInstance.getDataUuid(), dyFormData);
            }

            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            String title = null;
            String content = null;
            String remark = null;
            urlData.put("flowInstance", flowInstance);
            Date createTime = flowInstance.getCreateTime();
            String creator = workflowOrgService.getNameById(flowInstance.getStartUserId());
            Date modifyTime = flowInstance.getModifyTime();
            String modifier = userDetails.getUserName();
            String url = String.format("/workflow/work/view/work?flowInstUuid=%s", flowInstUuid);
            if (flowInstanceService.isSubFlowInstance(flowInstance.getUuid())) {
                url = String.format("/workflow/work/view/subflow/share?flowInstUuid=%s", flowInstUuid);
            }
            String systemId = flowInstance.getSystem();
            userDetails.putExtraData("system", systemId);
            FulltextSetting fulltextSetting = getFulltextSetting(taskData);
            // 获取默认的流程索引数据定义
            FulltextSetting.WorkflowConfiguration workflowConfiguration = fulltextSetting.getBuiltIn("workflow", FulltextSetting.WorkflowConfiguration.class);
            PropertyElement propertyElement = flowDelegate.getFlow().getProperty();
            String indexType = propertyElement.getIndexType();
            // 流程配置上自定义的
            if (StringUtils.equals("define", indexType)) {
                title = propertyElement.getIndexTitleExpression();
                content = propertyElement.getIndexContentExpression();
                remark = propertyElement.getIndexRemarkExpression();
            } else {
                // 默认的流程索引数据定义
                title = workflowConfiguration.getTitleExpression();
                content = workflowConfiguration.getContentExpression();
                remark = workflowConfiguration.getRemarkExpression();
            }

            if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content)) {
                List<String> formDataVariableModes = workflowConfiguration.getEnumIndexModes();
                title = TitleExpressionUtils.generateExpressionString(title, flowDefinition, flowInstance, taskData, dyFormData, formDataVariableModes);
                content = TitleExpressionUtils.generateExpressionString(content, flowDefinition, flowInstance, taskData, dyFormData, formDataVariableModes);
                remark = TitleExpressionUtils.generateExpressionString(remark, flowDefinition, flowInstance, taskData, dyFormData, formDataVariableModes);
                // url = TemplateEngineFactory.getDefaultTemplateEngine().process(url, urlData);
                WellFlowDocumentIndex index = new WellFlowDocumentIndex();
                index.setUuid(flowInstUuid);
                index.setFlowInstUuid(flowInstUuid);
                index.setFormUuid(flowInstance.getFormUuid());
                index.setDataUuid(flowInstance.getDataUuid());
                index.setTitle(title);
                index.setContent(content);
                index.setRemark(remark);
                // index.setFileInfos(JsonUtils.object2Json(getFileInfos(dyFormData, flowInstUuid)));
                index.setAttachments(getAttachments(dyFormData, flowInstUuid, fulltextSetting));
                index.setCreator(creator);
                index.setCreatorId(flowInstance.getStartUserId());
                index.setCreateTime(createTime);
                index.setModifyTime(modifyTime);
                index.setModifier(modifier);
                index.setUrl(url);
                index.setFileNames(index.getAttachments().stream().map(attachment -> Objects.toString(attachment.getFileName()))
                        .collect(Collectors.joining(Separator.SEMICOLON.getValue())));
                index.setSystem(flowInstance.getSystem());
                index.setReaders(getFlowReaders(flowInstance));
                index.setIndexOrder(10);
                return index;
            }
        } catch (Exception e) {
            logger.error("流程实例UUID={} , 索引流程文档数据异常：{}", taskData.getFlowInstUuid(), Throwables.getStackTraceAsString(e));
            // throw new RuntimeException(e);
        }
        return null;
    }

    private List<Attachment> getAttachments(DyFormData dyFormData, String flowInstUuid, FulltextSetting fulltextSetting) {
        List<LogicFileInfo> fileInfos = getFileInfos(dyFormData, flowInstUuid);
        if (CollectionUtils.isEmpty(fileInfos)) {
            return Collections.emptyList();
        }

        FulltextSetting.WorkflowConfiguration workflowConfiguration = fulltextSetting.getBuiltIn("workflow", FulltextSetting.WorkflowConfiguration.class);
        return AttachmentUtils.logicFileInfos2Attachments(fileInfos, BooleanUtils.isTrue(workflowConfiguration.getIndexAttachment()));
    }

    private List<LogicFileInfo> getFileInfos(DyFormData dyFormData, String flowInstUuid) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isNotEmpty(formDatas)) {
            // 主表数据
            fileInfos.addAll(extractFileInfos(dyFormData));

            // 从表数据
            formDatas.forEach((formUuid, dataList) -> {
                if (!StringUtils.equals(formUuid, dyFormData.getFormUuid())) {
                    dataList.forEach(data -> {
                        DyFormData subformData = dyFormData.getDyFormData(formUuid, Objects.toString(data.get("uuid"), StringUtils.EMPTY));
                        fileInfos.addAll(extractFileInfos(subformData));
                    });
                }
            });
        }

        // 流程附件
        Map<String, List<LogicFileInfo>> fileMap = mongoFileService.getNonioFilesFromFolders(Lists.newArrayList(flowInstUuid));
        fileMap.forEach((k, v) -> {
            fileInfos.addAll(v);
        });
        return fileInfos;
    }

    private List<LogicFileInfo> extractFileInfos(DyFormData dyFormData) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        List<String> fieldNames = dyFormData.doGetFieldNames();
        List<String> fileIds = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            if (dyFormData.isFileField(fieldName)) {
                Object fieldValue = dyFormData.getFieldValue(fieldName);
                if (fieldValue instanceof Collection) {
                    ((Collection<?>) fieldValue).forEach(file -> {
                        if (file instanceof Map) {
                            String fileId = Objects.toString(((Map<String, Object>) file).get("fileID"), StringUtils.EMPTY);
                            if (StringUtils.isNotBlank(fileId)) {
                                fileIds.add(fileId);
                            }
                        } else {
                            fileInfos.add((LogicFileInfo) file);
                        }
                    });
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fileIds)) {
            fileInfos.addAll(mongoFileService.getLogicFileInfo(fileIds));
        }
        return fileInfos;
    }

    private Set<String> getFlowReaders(FlowInstance flowInstance) {
        Set<String> readerIds = Sets.newLinkedHashSet(taskService.getReaderUserIdsByFlowInstUuid(flowInstance.getUuid()));
        if (CollectionUtils.isEmpty(readerIds)) {
            readerIds.add(flowInstance.getCreator());
        }
        return readerIds;
    }

    @Override
    public void deleteIndex(TaskData taskData) {
        if (restHighLevelClient == null) {
            return;
        }
        DeleteDocumentIndexVo vo = new DeleteDocumentIndexVo();
        vo.setUuid(taskData.getFlowInstUuid());
        wellFlowDoucmentIndexService.deleteIndex(vo);
    }

    @Override
    public void deleteBySystem(String system) {
        wellFlowDoucmentIndexService.deleteIndexByFieldEq("system", system);
    }

    @Override
    public long countBySystem(String system) {
        return wellFlowDoucmentIndexService.countByFieldEq("system", system);
    }

    @Override
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        WorkFlowBuildIndexEvent event1 = (WorkFlowBuildIndexEvent) event;
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            if (eventListenerPair.getEvent() instanceof WorkFlowBuildIndexEvent) {
                WorkFlowBuildIndexEvent event2 = (WorkFlowBuildIndexEvent) eventListenerPair.getEvent();
                if (StringUtils.equals(event1.getTaskData().getFlowInstUuid(), event2.getTaskData().getFlowInstUuid())
                        && this.equals(eventListenerPair.getListener())) {
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return true;
    }

    @Override
    public void onApplicationEvent(WorkFlowBuildIndexEvent event) {
        TaskData taskData = event.getTaskData();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        scheduledExecutorService.execute(() -> {
            try {
                RequestSystemContextPathResolver.setSystem(event.getSystem());
                IgnoreLoginUtils.login(userDetails);
                doBuildIndex(taskData);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        });
    }

    /**
     * @param taskData
     */
    private void doBuildIndex(TaskData taskData) {
        SubmitResult submitResult = taskData.getSubmitResult();
        if (submitResult == null || submitResult.getNextTasks().size() == 0) {
            if (StringUtils.isBlank(taskData.getFlowInstUuid())) {
                taskData.setFlowInstUuid(submitResult.getFlowInstUuid());
            }
            WellFlowDocumentIndex index = buildIndex(taskData);
            asyncIndex(index);
        } else {
            // String nextTaskUuid = (String) submitResult.getNextTasks().get(0).get("uuid");
            //提交到下一节点
            WellFlowDocumentIndex index = buildIndex(taskData.getFlowInstUuid(), taskData);
            asyncIndex(index);
//            // 处理子流程，同步分发时生成索引
//            Boolean syncDispatch = (Boolean) taskData.get("syncDispatchSubFlow");
//            if (BooleanUtils.isTrue(syncDispatch)) {
//                if (submitResult.getSubFlowInstUUids().size() > 0) {
//                    for (String subFlowInstUUid : submitResult.getSubFlowInstUUids()) {
//                        index = buildIndex(subFlowInstUUid, taskData);
//                        asyncIndex(index);
//                    }
//                }
//            }
        }
    }

}
