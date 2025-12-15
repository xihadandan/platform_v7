/*
 * @(#)2015-1-8 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.expression.Condition;
import com.wellsoft.pt.bpm.engine.expression.ConditionFactory;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.RecordCondition;
import com.wellsoft.pt.bpm.engine.form.assembler.DefaultTaskFormOpinionAssemblerImpl;
import com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.parser.activity.*;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dao.FlowFormatDao;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import ognl.Ognl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-8.1	Administrator		2015-1-8		Create
 * </pre>
 * @date 2015-1-8
 */
@Service
@Transactional
public class TaskFormOpinionServiceImpl extends BaseServiceImpl implements TaskFormOpinionService {

    private static final String GET_BY_FLOW_INST_UUID = "from TaskFormOpinion t where t.flowInstUuid = :flowInstUuid";
    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskFormOpinion t where t.flowInstUuid = :flowInstUuid";
    private static Logger LOG = LoggerFactory.getLogger(TaskFormOpinionServiceImpl.class);
    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;

    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private FlowFormatService flowFormatService;

    @Autowired
    private FlowFormatDao formatDao;

    /**
     * Java过滤HTML标签实例
     *
     * @param inputString
     * @return
     */
    public static String html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }

        return textStr == null ? textStr : textStr.trim();// 返回文本字符串
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#get(java.lang.String)
     */
    @Override
    public TaskFormOpinion get(String uuid) {
        return this.dao.get(TaskFormOpinion.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#save(com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion)
     */
    @Override
    public void save(TaskFormOpinion taskFormOpinion) {
        this.dao.save(taskFormOpinion);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion)
     */
    @Override
    public List<TaskFormOpinion> findByExample(TaskFormOpinion example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#getNewTaskFormOpinion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public TaskFormOpinion getNewTaskFormOpinion(String flowInstUuid, String formUuid, String dataUuid,
                                                 String fieldName, String appendedOpinion, boolean appendFirst) {
        TaskFormOpinion taskFormOpinion = null;
        TaskFormOpinion example = new TaskFormOpinion();
        example.setFlowInstUuid(flowInstUuid);
        example.setDataUuid(dataUuid);
        example.setFieldName(fieldName);
        List<TaskFormOpinion> taskFormOpinions = this.dao.findByExample(example);
        if (taskFormOpinions.isEmpty()) {
            taskFormOpinion = new TaskFormOpinion();
            taskFormOpinion.setFlowInstUuid(flowInstUuid);
            taskFormOpinion.setDataUuid(dataUuid);
            taskFormOpinion.setFieldName(fieldName);
            taskFormOpinion.setErrorData(false);
        } else {
            taskFormOpinion = taskFormOpinions.get(0);
        }
        String oldContent = taskFormOpinion.getContent();
        String newContent = null;
        if (appendFirst) {
            newContent = StringUtils.trimToEmpty(appendedOpinion) + StringUtils.trimToEmpty(oldContent);
        } else {
            newContent = StringUtils.trimToEmpty(oldContent) + StringUtils.trimToEmpty(appendedOpinion);
        }
        taskFormOpinion.setContent(newContent);
        this.dao.save(taskFormOpinion);
        return taskFormOpinion;
    }

    /**
     * select 'update ' || t3.name || ' t set t.' || t1.field_name || '= ''' ||
     * t1.content || ''' where t.uuid = ''' || t1.data_uuid || ''';'
     * from wf_task_form_opinion t1
     * inner join wf_flow_instance t2
     * on t1.flow_inst_uuid = t2.uuid
     * inner join dyform_form_definition t3
     * on t2.form_uuid = t3.uuid;
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#fixErrorData()
     */
    @Override
    public void fixErrorData() {
        String hql = "from TaskFormOpinion t where t.errorData is null";
        List<TaskFormOpinion> taskFormOpinions = this.dao.query(hql, null, TaskFormOpinion.class);
        System.out.println("all size" + taskFormOpinions.size());
        int i = 0;
        for (TaskFormOpinion taskFormOpinion : taskFormOpinions) {
            i++;
            if (taskFormOpinion.getErrorData() != null) {
                return;
            }
            Map<String, StringBuilder> opinionMap = new HashMap<String, StringBuilder>();

            String fieldName = taskFormOpinion.getFieldName();
            String key = taskFormOpinion.getDataUuid() + fieldName;
            opinionMap.put(key, new StringBuilder());

            String flowInstUuid = taskFormOpinion.getFlowInstUuid();
            TaskActivity example = new TaskActivity();
            example.setFlowInstUuid(flowInstUuid);
            List<TaskActivity> taskActivities = this.dao.findByExample(example, "createTime asc");

            // DyFormData dyFormData = null;

            for (TaskActivity taskActivity : taskActivities) {
                String taskInstUuid = taskActivity.getTaskInstUuid();
                TaskInstance taskInstance = this.dao.get(TaskInstance.class, taskInstUuid);

                // String formUuid = taskInstance.getFormUuid();
                // String dataUuid = taskInstance.getDataUuid();
                // if (dyFormData == null) {
                // // dyFormData = dyFormApiFacade.getDyFormData(formUuid,
                // dataUuid);
                // }
                String taskId = taskInstance.getId();

                // 环节操作
                TaskOperation example2 = new TaskOperation();
                example2.setFlowInstUuid(flowInstUuid);
                example2.setTaskInstUuid(taskInstUuid);
                List<TaskOperation> taskOperations = this.dao.findByExample(example2, "createTime asc");

                List<IdEntity> entities = new ArrayList<IdEntity>();
                TaskInstance taskInstanceModel = new TaskInstance();
                BeanUtils.copyProperties(taskInstance, taskInstanceModel);
                entities.add(taskInstanceModel);
                FlowInstance flowInstanceModel = new FlowInstance();
                BeanUtils.copyProperties(taskInstance.getFlowInstance(), flowInstanceModel);
                entities.add(flowInstanceModel);

                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                List<Record> records = flowDelegate.getTaskForm(taskId).getRecords();
                for (Record record : records) {
                    if (fieldName.equals(record.getField())) {
                        for (TaskOperation taskOperation : taskOperations) {
                            Map<String, Object> values = new HashMap<String, Object>();
                            values.put("taskName", taskInstance.getName());
                            String actionType = taskOperation.getActionType();

                            if (!(WorkFlowOperation.SUBMIT.equals(actionType)
                                    || WorkFlowOperation.TRANSFER.equals(actionType)
                                    || WorkFlowOperation.COUNTER_SIGN.equals(actionType)
                                    || WorkFlowOperation.ROLLBACK.equals(actionType)
                                    || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)
                                    || WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)
                                    || WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType))) {
                                continue;
                            }

                            if (WorkFlowOperation.SUBMIT.equals(actionType)) {

                            } else if (WorkFlowOperation.TRANSFER.equals(actionType)) {
                                TaskIdentity identity = new TaskIdentity();
                                identity.setSuspensionState(null);
                                identity.setTaskInstUuid(taskInstance.getUuid());
                                identity.setTodoType(WorkFlowTodoType.Transfer);
                                identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
                                List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
                                List<String> taskUserIds = new ArrayList<String>();
                                for (TaskIdentity taskIdentity : taskIdentities) {
                                    taskUserIds.add(taskIdentity.getUserId());
                                }
                                String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
                                values.put("transferUserNames", taskUserNames);
                            } else if (WorkFlowOperation.COUNTER_SIGN.equals(actionType)) {
                                TaskIdentity identity = new TaskIdentity();
                                identity.setSuspensionState(null);
                                identity.setTaskInstUuid(taskInstance.getUuid());
                                identity.setTodoType(WorkFlowTodoType.CounterSign);
                                identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
                                List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
                                List<String> taskUserIds = new ArrayList<String>();
                                for (TaskIdentity taskIdentity : taskIdentities) {
                                    taskUserIds.add(taskIdentity.getUserId());
                                }
                                String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
                                values.put("counterSignUserNames", taskUserNames);
                            } else if (WorkFlowOperation.ROLLBACK.equals(actionType)) {

                            } else if (WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)) {

                            } else if (WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)) {
                                values.put("actionType", WorkFlowOperation.SUBMIT);
                            } else if (WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType)) {
                                values.put("actionType", WorkFlowOperation.SUBMIT);
                            }
                            entities.add(taskOperation);

                            String recordValue = getFormatValue(record.getValue(), entities,
                                    new HashMap<Object, Object>(), values, taskOperation);
                            opinionMap.get(key).append(StringUtils.stripToEmpty(recordValue));
                        }
                    }
                }

            }

            taskFormOpinion.setContent(opinionMap.get(key).toString());
            taskFormOpinion.setErrorData(true);
            this.dao.save(taskFormOpinion);
            System.out.println(i);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#getFormatValue(java.lang.String, java.util.List, java.util.Map)
     */
    public String getFormatValue(String code, List<IdEntity> entities, Map<?, ?> dytableMap,
                                 Map<String, Object> extraData, TaskOperation taskOperation) {
        String result = "";
        try {
            FlowFormat format = this.formatDao.getByCode(code);
            String source = format.getValue();

            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            Map<Object, Object> root = templateEngine.mergeDataAsMap(entities, dytableMap, extraData, true, true);
            root.put("currentUsername", taskOperation.getAssigneeName());
            root.put("currentTime", DateUtils.formatDateTime(taskOperation.getCreateTime()));
            root.put("currentDate", DateUtils.formatDate(taskOperation.getCreateTime()));
            result = templateEngine.process(templateEngine.decorateSource(source), root);

            // 清空HTML格式
            if (Boolean.TRUE.equals(format.getIsClear())) {
                result = html2Text(result);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#fixErrorData(java.lang.String)
     */
    @Override
    public String fixErrorData(String inputFlowInstUuid) {
        String hql = "from TaskFormOpinion t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values1 = new HashMap<String, Object>();
        values1.put("flowInstUuid", inputFlowInstUuid);
        List<TaskFormOpinion> taskFormOpinions = this.dao.query(hql, values1, TaskFormOpinion.class);
        System.out.println("all size" + taskFormOpinions.size());
        int i = 0;
        for (TaskFormOpinion taskFormOpinion : taskFormOpinions) {
            i++;
            Map<String, StringBuilder> opinionMap = new HashMap<String, StringBuilder>();

            String fieldName = taskFormOpinion.getFieldName();
            String key = taskFormOpinion.getDataUuid() + fieldName;
            opinionMap.put(key, new StringBuilder());

            String flowInstUuid = taskFormOpinion.getFlowInstUuid();
            TaskActivity example = new TaskActivity();
            example.setFlowInstUuid(flowInstUuid);
            List<TaskActivity> taskActivities = this.dao.findByExample(example, "createTime asc");

            // DyFormData dyFormData = null;

            for (TaskActivity taskActivity : taskActivities) {
                String taskInstUuid = taskActivity.getTaskInstUuid();
                TaskInstance taskInstance = this.dao.get(TaskInstance.class, taskInstUuid);

                // String formUuid = taskInstance.getFormUuid();
                // String dataUuid = taskInstance.getDataUuid();
                // if (dyFormData == null) {
                // // dyFormData = dyFormApiFacade.getDyFormData(formUuid,
                // dataUuid);
                // }
                String taskId = taskInstance.getId();

                // 环节操作
                TaskOperation example2 = new TaskOperation();
                example2.setFlowInstUuid(flowInstUuid);
                example2.setTaskInstUuid(taskInstUuid);
                List<TaskOperation> taskOperations = this.dao.findByExample(example2, "createTime asc");

                List<IdEntity> entities = new ArrayList<IdEntity>();
                TaskInstance taskInstanceModel = new TaskInstance();
                BeanUtils.copyProperties(taskInstance, taskInstanceModel);
                entities.add(taskInstanceModel);
                FlowInstance flowInstanceModel = new FlowInstance();
                BeanUtils.copyProperties(taskInstance.getFlowInstance(), flowInstanceModel);
                entities.add(flowInstanceModel);

                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                List<Record> records = flowDelegate.getTaskForm(taskId).getRecords();
                for (Record record : records) {
                    if (fieldName.equals(record.getField())) {
                        for (TaskOperation taskOperation : taskOperations) {
                            Map<String, Object> values = new HashMap<String, Object>();
                            values.put("taskName", taskInstance.getName());
                            String actionType = taskOperation.getActionType();

                            if (!(WorkFlowOperation.SUBMIT.equals(actionType)
                                    || WorkFlowOperation.TRANSFER.equals(actionType)
                                    || WorkFlowOperation.COUNTER_SIGN.equals(actionType)
                                    || WorkFlowOperation.ROLLBACK.equals(actionType)
                                    || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)
                                    || WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)
                                    || WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType))) {
                                continue;
                            }

                            if (WorkFlowOperation.SUBMIT.equals(actionType)) {

                            } else if (WorkFlowOperation.TRANSFER.equals(actionType)) {
                                TaskIdentity identity = new TaskIdentity();
                                identity.setSuspensionState(null);
                                identity.setTaskInstUuid(taskInstance.getUuid());
                                identity.setTodoType(WorkFlowTodoType.Transfer);
                                identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
                                List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
                                List<String> taskUserIds = new ArrayList<String>();
                                for (TaskIdentity taskIdentity : taskIdentities) {
                                    taskUserIds.add(taskIdentity.getUserId());
                                }
                                String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
                                values.put("transferUserNames", taskUserNames);
                            } else if (WorkFlowOperation.COUNTER_SIGN.equals(actionType)) {
                                TaskIdentity identity = new TaskIdentity();
                                identity.setSuspensionState(null);
                                identity.setTaskInstUuid(taskInstance.getUuid());
                                identity.setTodoType(WorkFlowTodoType.CounterSign);
                                identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
                                List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
                                List<String> taskUserIds = new ArrayList<String>();
                                for (TaskIdentity taskIdentity : taskIdentities) {
                                    taskUserIds.add(taskIdentity.getUserId());
                                }
                                String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
                                values.put("counterSignUserNames", taskUserNames);
                            } else if (WorkFlowOperation.ROLLBACK.equals(actionType)) {

                            } else if (WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)) {

                            } else if (WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)) {
                                values.put("actionType", WorkFlowOperation.SUBMIT);
                            } else if (WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType)) {
                                values.put("actionType", WorkFlowOperation.SUBMIT);
                            }
                            entities.add(taskOperation);

                            String recordValue = getFormatValue(record.getValue(), entities,
                                    new HashMap<Object, Object>(), values, taskOperation);
                            opinionMap.get(key).append(StringUtils.stripToEmpty(recordValue));
                        }
                    }
                }

            }

            taskFormOpinion.setContent(opinionMap.get(key).toString());
            taskFormOpinion.setErrorData(true);
            this.dao.save(taskFormOpinion);
            System.out.println(i);
        }

        StringBuilder sb = new StringBuilder();
        List<QueryItem> queryItems = this.nativeDao.namedQuery("getTaskFormOpinionUpdateText", values1,
                QueryItem.class);
        if (!queryItems.isEmpty()) {
            sb.append("意见已重新生成");
            for (QueryItem queryItem : queryItems) {
                String updateSql = queryItem.getString("updateText");
                logger.error(updateSql);
                this.nativeDao.batchExecute(updateSql, null);
            }
        }

        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#rebuildTaskFormOpinion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void rebuildTaskFormOpinion(String rebuildTaskInstUuid, String formUuid, String dataUuid) {
        List<TaskActivityQueryItem> allTaskActivities = taskActivityService
                .getAllActivityByTaskInstUuid(rebuildTaskInstUuid);
        List<TaskOperation> allTaskOperations = taskOperationService
                .getAllTaskOperationByTaskInstUuid(rebuildTaskInstUuid);

        if (allTaskActivities.isEmpty()) {
            return;
        }

        String rebuildFlowInstUuid = allTaskActivities.get(0).getFlowInstUuid();
        TaskFormOpinion opinion = new TaskFormOpinion();
        opinion.setFlowInstUuid(rebuildFlowInstUuid);
        List<TaskFormOpinion> taskFormOpinions = this.dao.findByExample(opinion);
        if (taskFormOpinions.isEmpty()) {
            return;
        }

        TaskActivityStack stack = TaskActivityStackFactary.build(null, allTaskActivities, allTaskOperations);
        List<TaskActivityItem> taskActivityItems = new ArrayList<TaskActivityItem>();
        Iterator<TaskActivityItem> it = stack.iterator();
        while (it.hasNext()) {
            taskActivityItems.add(it.next());
        }

        Map<String, TaskOperation> taskOperationMap = ConvertUtils.convertElementToMap(allTaskOperations,
                IdEntity.UUID);
        FlowDelegate flowDelegate = FlowDelegateUtils
                .getFlowDelegate(this.dao.get(TaskInstance.class, rebuildTaskInstUuid).getFlowDefinition());
        // 生成意见
        for (TaskFormOpinion taskFormOpinion : taskFormOpinions) {
            Map<String, StringBuilder> opinionMap = new HashMap<String, StringBuilder>();

            String fieldName = taskFormOpinion.getFieldName();
            String key = taskFormOpinion.getDataUuid() + fieldName;
            opinionMap.put(key, new StringBuilder());

            String flowInstUuid = taskFormOpinion.getFlowInstUuid();
            TaskActivity example = new TaskActivity();
            example.setFlowInstUuid(flowInstUuid);

            for (TaskActivityItem taskActivity : taskActivityItems) {
                String taskInstUuid = taskActivity.getTaskInstUuid();
                TaskInstance taskInstance = this.dao.get(TaskInstance.class, taskInstUuid);
                String taskId = taskInstance.getId();

                List<IdEntity> entities = new ArrayList<IdEntity>();
                TaskInstance taskInstanceModel = new TaskInstance();
                BeanUtils.copyProperties(taskInstance, taskInstanceModel);
                entities.add(taskInstanceModel);
                FlowInstance flowInstanceModel = new FlowInstance();
                BeanUtils.copyProperties(taskInstance.getFlowInstance(), flowInstanceModel);
                entities.add(flowInstanceModel);

                List<Record> records = flowDelegate.getTaskForm(taskId).getRecords();
                for (Record record : records) {
                    if (fieldName.equals(record.getField())) {
                        TaskOperationStack operationStack = taskActivity.getOperationStack();
                        Iterator<TaskOperationItem> optIt = operationStack.iterator();
                        List<TaskOperationItem> taskOperationItems = new ArrayList<TaskOperationItem>();
                        while (optIt.hasNext()) {
                            taskOperationItems.add(optIt.next());
                        }
                        for (TaskOperationItem taskOperationItem : taskOperationItems) {
                            String recordValue = generateOpinion(taskOperationMap, opinionMap, key, taskInstance,
                                    entities, record, taskOperationItem);
                        }
                    }
                }

            }

            // 生成新的办理意见
            String newContent = opinionMap.get(key).toString();
            if (!newContent.equals(taskFormOpinion.getContent())) {
                taskFormOpinion.setContent(newContent);
                this.dao.save(taskFormOpinion);

                // 更新表单数据
                DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
                dyFormData.setFieldValue(fieldName, newContent);
                dyFormFacade.saveFormData(dyFormData);
            }
        }

    }

    /**
     * 如何描述该方法
     *
     * @param taskOperationMap
     * @param opinionMap
     * @param key
     * @param taskInstance
     * @param entities
     * @param record
     * @param taskOperationItem
     * @return
     */
    private String generateOpinion(Map<String, TaskOperation> taskOperationMap, Map<String, StringBuilder> opinionMap,
                                   String key, TaskInstance taskInstance, List<IdEntity> entities, Record record,
                                   TaskOperationItem taskOperationItem) {
        TaskOperation taskOperation = taskOperationMap.get(taskOperationItem.getUuid());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskName", taskInstance.getName());
        String actionType = taskOperation.getActionType();

        if (!(WorkFlowOperation.SUBMIT.equals(actionType) || WorkFlowOperation.TRANSFER.equals(actionType)
                || WorkFlowOperation.COUNTER_SIGN.equals(actionType) || WorkFlowOperation.ROLLBACK.equals(actionType)
                || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)
                || WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)
                || WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType)
                || WorkFlowOperation.DELEGATION_SUBMIT.equals(actionType))) {
            return null;
        }

        if (WorkFlowOperation.SUBMIT.equals(actionType)) {
        } else if (WorkFlowOperation.TRANSFER.equals(actionType)) {
            TaskIdentity identity = new TaskIdentity();
            identity.setSuspensionState(null);
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.Transfer);
            identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
            List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
            List<String> taskUserIds = new ArrayList<String>();
            for (TaskIdentity taskIdentity : taskIdentities) {
                taskUserIds.add(taskIdentity.getUserId());
            }
            String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
            values.put("transferUserNames", taskUserNames);
        } else if (WorkFlowOperation.COUNTER_SIGN.equals(actionType)) {
            TaskIdentity identity = new TaskIdentity();
            identity.setSuspensionState(null);
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.CounterSign);
            identity.setSourceTaskIdentityUuid(taskOperation.getTaskIdentityUuid());
            List<TaskIdentity> taskIdentities = this.dao.findByExample(identity);
            List<String> taskUserIds = new ArrayList<String>();
            for (TaskIdentity taskIdentity : taskIdentities) {
                taskUserIds.add(taskIdentity.getUserId());
            }
            String taskUserNames = IdentityResolverStrategy.resolveAsNames(taskUserIds);
            values.put("counterSignUserNames", taskUserNames);
        } else if (WorkFlowOperation.ROLLBACK.equals(actionType)) {

        } else if (WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)) {

        } else if (WorkFlowOperation.COUNTER_SIGN_SUBMIT.equals(actionType)) {
            values.put("actionType", WorkFlowOperation.SUBMIT);
        } else if (WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType)) {
            values.put("actionType", WorkFlowOperation.SUBMIT);
        } else if (WorkFlowOperation.DELEGATION_SUBMIT.equals(actionType)) {
            values.put("actionType", WorkFlowOperation.SUBMIT);
        }
        entities.add(taskOperation);

        String recordValue = getFormatValue(record.getValue(), entities, new HashMap<Object, Object>(), values,
                taskOperation);
        opinionMap.get(key).append(StringUtils.stripToEmpty(recordValue));
        return recordValue;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskFormOpinion> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.find(GET_BY_FLOW_INST_UUID, values, TaskFormOpinion.class);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.batchExecute(REMOVE_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#recordTaskFormOpinion(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    public List<String> recordTaskFormOpinion(WorkBean workBean, FlowDelegate flowDelegate) {
        // 意见日志信息
        List<String> opinionLogUuids = new ArrayList<String>();
        String taskInstUuid = workBean.getTaskInstUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        List<Record> records = workBean.getRecords();
        if (records == null || records.isEmpty()) {
            return opinionLogUuids;
        }

        List<IdEntity> entities = new ArrayList<IdEntity>();
        DyFormData dyFormData = workBean.getDyFormData();
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(workBean.getFormUuid(), workBean.getDataUuid());
        }
        Map<String, Object> values = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
            values.put("taskName", MapUtils.isNotEmpty(workBean.getI18n()) ? StringUtils.defaultIfBlank(workBean.getI18n().get(taskInstance.getId() + ".taskName"), taskInstance.getName()) : taskInstance.getName());
            values.put("taskId", taskInstance.getId());
            TaskInstance taskInstanceModel = new TaskInstance();
            BeanUtils.copyProperties(taskInstance, taskInstanceModel);
            entities.add(taskInstanceModel);
            FlowInstance flowInstanceModel = new FlowInstance();
            BeanUtils.copyProperties(taskInstance.getFlowInstance(), flowInstanceModel);
            entities.add(flowInstanceModel);
        } else if (StringUtils.isNotBlank(workBean.getTaskName())) {
            values.put("taskId", workBean.getTaskId());
            values.put("taskName", MapUtils.isNotEmpty(workBean.getI18n()) ? StringUtils.defaultIfBlank(workBean.getI18n().get(workBean.getTaskId() + ".taskName"), workBean.getTaskName()) : workBean.getTaskName());
        }
        Integer todoType = workBean.getTodoType();
        values.put("todoType", todoType);
        values.put("flowDelegate", flowDelegate);
        values.put("taskNode", flowDelegate.getTaskNode(workBean.getTaskId()));
        if (WorkFlowTodoType.Delegation.equals(todoType)) {
            String taskIdentityUuid = workBean.getTaskIdentityUuid();
            TaskDelegation taskDelegation = taskDelegationService.get(SpringSecurityUtils.getCurrentUserId(),
                    taskInstUuid, taskIdentityUuid);
            if (taskDelegation != null) {
                values.put("trusteeName", taskDelegation.getTrusteeName());
                values.put("consignorName", taskDelegation.getConsignorName());
            } else {
                values.put("todoType", WorkFlowTodoType.Submit);
            }
        }
        if (StringUtils.isNotBlank(workBean.getToDirectionId())) {
            Direction toDirection = flowDelegate.getDirection(workBean.getToDirectionId());
            if (toDirection != null) {
                values.put("toTaskId", toDirection.getToID());
            } else {
                values.put("toTaskId", workBean.getToTaskId());
            }
        } else {
            values.put("toTaskId", workBean.getToTaskId());
        }
        TaskOperation taskOperation = new TaskOperation();
        String opinionText = workBean.getOpinionText();
        List<LogicFileInfo> opinionFiles = workBean.getOpinionFiles();
        taskOperation.setOpinionText(opinionText);
        taskOperation.setOpinionLabel(workBean.getOpinionLabel());
        taskOperation.setOpinionValue(workBean.getOpinionValue());
        if (CollectionUtils.isNotEmpty(opinionFiles)) {
            taskOperation.setOpinionFileIds(opinionFiles.stream().map(file -> file.getFileID()).collect(Collectors.joining(Separator.SEMICOLON.getValue())));
        }
        taskOperation.setAction(workBean.getAction());
        taskOperation.setActionType(workBean.getActionType());
        entities.add(taskOperation);
        values.put("opinionText", opinionText);
        values.put("opinionLabel", workBean.getOpinionLabel());
        values.put("opinionValue", workBean.getOpinionValue());
        values.put("opinionFiles", workBean.getOpinionFiles());
        // 解析并保存信息记录
        opinionLogUuids = saveOpinionRecords(opinionText, taskInstUuid, flowInstUuid, entities, dyFormData, records,
                values);
        workBean.setDyFormData(dyFormData);
        return opinionLogUuids;
    }

    /**
     * 如何描述该方法
     *
     * @param opinionText
     * @param taskInstUuid
     * @param flowInstUuid
     * @param entities
     * @param dyFormData
     * @param records
     * @param values
     * @return
     */
    @Override
    public List<String> saveOpinionRecords(String opinionText, String taskInstUuid, String flowInstUuid,
                                           List<IdEntity> entities, DyFormData dyFormData, List<Record> records, Map<String, Object> values) {
        List<String> opinionLogUuids = new ArrayList<String>();
        boolean isRequiredCheckPreCondition = isRequiredCheckPreCondition(records);
        // 设置信息格式
        for (Record record : records) {
            String fieldNotValidate = record.getFieldNotValidate();
            if (Record.FIELD_NOT_VALIDATE_TRUE.equals(fieldNotValidate)) {
                FormDataHandler.addNotValidateField(dyFormData.getFormId(), record.getField());
            }
            String ignoreEmpty = record.getIgnoreEmpty();
            if (Record.IGNORE_EMPTY_VALUE_TRUE.equals(ignoreEmpty) && StringUtils.isBlank(opinionText)) {
                continue;
            }
            // 前置条件判断
            if (isRequiredCheckPreCondition && record.isEnablePreCondition()
                    && !checkRecordPreCondition(taskInstUuid, flowInstUuid, null, record, dyFormData, values)) {
                continue;
            }

            String recordWay = record.getWay();
            String assembler = record.getAssembler();
            String recordField = record.getField();
            String recordValue = flowFormatService.getFormatValue(record.getValue(), entities,
                    dyFormData.getFormDataOfMainform(), values);
            // 生成的意见为空判断
            if (Record.IGNORE_EMPTY_VALUE_TRUE.equals(ignoreEmpty) && StringUtils.isBlank(recordValue)) {
                continue;
            }
            if (Record.WAY_NO_REPLACE.equals(recordWay)) {
                String formRecordValue = Objects.toString(dyFormData.getFieldValue(recordField), StringUtils.EMPTY);
                if (StringUtils.isBlank(formRecordValue)) {
                    dyFormData.setFieldValue(recordField, recordValue);

                    // 意见日志
                    String opinionLogUuid = taskFormOpinionLogService.log(taskInstUuid, flowInstUuid, null, recordField,
                            recordValue, Integer.valueOf(recordWay));
                    opinionLogUuids.add(opinionLogUuid);
                }
            } else if (Record.WAY_REPLACE.equals(recordWay)) {
                dyFormData.setFieldValue(recordField, recordValue);

                // 意见日志
                String opinionLogUuid = taskFormOpinionLogService.log(taskInstUuid, flowInstUuid, null, recordField,
                        recordValue, Integer.valueOf(recordWay));
                opinionLogUuids.add(opinionLogUuid);
            } else if (Record.WAY_APPEND.equals(recordWay)) {
                TaskFormOpinionAssembler opinionAssembler = getTaskFormOpinionAssembler(assembler);
                String taskFormOpinionUuid = opinionAssembler.assemble(flowInstUuid, taskInstUuid, dyFormData,
                        recordField, recordValue, record.getContentOrigin());
                // 意见日志
                String opinionLogUuid = taskFormOpinionLogService.log(taskInstUuid, flowInstUuid, taskFormOpinionUuid,
                        recordField, recordValue, Integer.valueOf(recordWay));
                opinionLogUuids.add(opinionLogUuid);
            }
        }
        return opinionLogUuids;
    }

    /**
     * @param records
     * @return
     */
    private boolean isRequiredCheckPreCondition(List<Record> records) {
        for (Record record : records) {
            if (record.isEnablePreCondition() && CollectionUtils.isNotEmpty(record.getRecordConditions())) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService#checkRecordPreCondition(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.bpm.engine.form.Record, com.wellsoft.pt.dyform.facade.dto.DyFormData, java.util.Map)
     */
    public boolean checkRecordPreCondition(String taskInstUuid, String flowInstUuid, String flowDefUuid, Record record,
                                           DyFormData dyFormData, Map<String, Object> values) {
        FlowDelegate flowDelegate = (FlowDelegate) values.get("flowDelegate");
        Token token = (Token) values.get("token");
        TaskNode taskNode = (TaskNode) values.get("taskNode");
        if (token == null) {
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            TaskData taskData = new TaskData();
            taskData.setUserId(user.getUserId());
            taskData.setUserName(user.getUserName());
            taskData.setTaskInstUuid(taskInstUuid);
            taskData.setFormUuid(dyFormData.getFormUuid());
            taskData.setDataUuid(dyFormData.getDataUuid());
            taskData.setDyFormData(dyFormData.getDataUuid(), dyFormData);
            // 设置签署意见
            String key = taskInstUuid + user.getUserId();
            taskData.setOpinionText(key, (String) values.get("opinionText"));
            taskData.setOpinionLabel(key, (String) values.get("opinionLabel"));
            taskData.setOpinionValue(key, (String) values.get("opinionValue"));
            if (StringUtils.isNotBlank(taskInstUuid)) {
                TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
                token = new Token(taskInstance, taskData);
                if (flowDelegate == null) {
                    flowDelegate = token.getFlowDelegate();
                }
                flowInstanceDao.getSession().evict(taskInstance);
            } else if (StringUtils.isNotBlank(flowInstUuid)) {
                FlowInstance flowInstance = flowInstanceDao.getOne(flowInstUuid);
                token = new Token(flowInstance, taskData);
                if (flowDelegate == null) {
                    flowDelegate = token.getFlowDelegate();
                }
                flowInstanceDao.getSession().evict(flowInstance);
            } else if (StringUtils.isNotBlank(flowDefUuid)) {
                token = new Token(taskData);
                if (flowDelegate == null) {
                    flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
                }
                token.setFlowDelegate(flowDelegate);
            } else {
                token = new Token(taskData);
            }
        }
        return evaluate(flowDelegate, token, taskNode, dyFormData, record);
    }

    /**
     * 判断分支条件
     *
     * @param token
     * @param direction
     * @return
     */
    private boolean evaluate(FlowDelegate flowDelegate, Token token, TaskNode taskNode, DyFormData dyFormData,
                             Record record) {
        List<RecordCondition> conditions = record.getRecordConditions();
        List<Condition> conds = new ArrayList<Condition>();
        for (int index = 0; index < conditions.size(); index++) {
            RecordCondition recordCondition = conditions.get(index);
            conds.add(ConditionFactory.getCondition(recordCondition.getValue(), recordCondition.getData(), recordCondition.getType()));
        }
        // 获取表单数据
        TaskData taskData = token.getTaskData();
        if (taskData.getDyFormData(taskData.getDataUuid()) == null) {
            taskData.setDyFormData(taskData.getDataUuid(), dyFormData);
        }

        Node to = taskNode;
        StringBuilder expression = new StringBuilder();
        for (Condition condition : conds) {
            expression.append(condition.evaluate(token, to));
        }
        Object result = 0;
        try {
            result = Ognl.getValue(expression.toString(), new HashMap<String, String>());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
    }

    /**
     * @param assembler
     * @return
     */
    private TaskFormOpinionAssembler getTaskFormOpinionAssembler(String assembler) {
        if (StringUtils.isBlank(assembler)) {
            return ApplicationContextHolder.getBean(DefaultTaskFormOpinionAssemblerImpl.class);
        }
        return ApplicationContextHolder.getBean(assembler, TaskFormOpinionAssembler.class);
    }

}
