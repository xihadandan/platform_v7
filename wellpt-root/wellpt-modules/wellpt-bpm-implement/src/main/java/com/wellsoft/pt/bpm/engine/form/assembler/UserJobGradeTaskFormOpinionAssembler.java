/*
 * @(#)2016年5月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form.assembler;

import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion;
import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinionLog;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSuspensionState;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.support.UsersGrade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月20日.1	zhulh		2016年5月20日		Create
 * </pre>
 * @date 2016年5月20日
 */
@Service
@Transactional
public class UserJobGradeTaskFormOpinionAssembler implements TaskFormOpinionAssembler {
    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;

    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    @Autowired
    private DefaultTaskFormOpinionAssemblerImpl defaultTaskFormOpinionAssembler;

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler#getName()
     */
    @Override
    public String getName() {
        return "按用户职位级别组装";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler#assemble(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String, java.lang.String)
     */
    @Override
    public String assemble(String flowInstUuid, String taskInstUuid, DyFormData dyFormData, String fieldName,
                           String appendedOpinion, String contentOrigin) {
        TaskFormOpinionLog example = new TaskFormOpinionLog();
        example.setFieldName(fieldName);
        example.setRecordWay(Integer.valueOf(Record.WAY_APPEND));
        example.setStatus(WorkFlowSuspensionState.Normal);
        example.setFlowInstUuid(flowInstUuid);
        List<TaskFormOpinionLog> logs = taskFormOpinionLogService.findByExample(example, "createTime asc");
        if (logs.isEmpty()) {
            return defaultTaskFormOpinionAssembler.assemble(flowInstUuid, taskInstUuid, dyFormData, fieldName,
                    appendedOpinion, null);
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String creator = user.getUserId();

        TaskFormOpinionLog furthure = new TaskFormOpinionLog();
        furthure.setCreator(creator);
        furthure.setCreateTime(Calendar.getInstance().getTime());
        furthure.setContent(appendedOpinion);
        logs.add(furthure);

        // 收集意见
        Map<String, List<TaskFormOpinionLog>> userOptions = new HashMap<String, List<TaskFormOpinionLog>>();
        Set<String> creators = new LinkedHashSet<String>();
        for (TaskFormOpinionLog taskFormOpinionLog : logs) {
            String userId = taskFormOpinionLog.getCreator();
            if (!userOptions.containsKey(userId)) {
                userOptions.put(userId, new ArrayList<TaskFormOpinionLog>());
            }
            List<TaskFormOpinionLog> options = userOptions.get(userId);
            options.add(taskFormOpinionLog);
            creators.add(userId);
        }

        // 对用户ID进行排序
        UsersGrade usersGrade = orgApiFacade.orderedUserIdsByJobGrade(creators);

        // 按用户职位级别重新组装办理意见
        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> gradeMap = usersGrade.getGradeMap();
        for (Entry<String, Set<String>> entry : gradeMap.entrySet()) {
            Set<String> userIds = entry.getValue();
            // 同一级别的用户按用户最先提交办理意见排序
            userIds = sort(userIds, creators);
            for (String userId : userIds) {
                List<TaskFormOpinionLog> options = userOptions.get(userId);
                for (TaskFormOpinionLog opinionLog : options) {
                    sb.append(opinionLog.getContent());
                }
            }
        }

        // 替换旧的意见字段
        String content = sb.toString();
        String dataUuid = dyFormData.getDataUuid();
        TaskFormOpinion taskFormOpinion = null;
        TaskFormOpinion opinionExample = new TaskFormOpinion();
        opinionExample.setFlowInstUuid(flowInstUuid);
        opinionExample.setDataUuid(dataUuid);
        opinionExample.setFieldName(fieldName);
        List<TaskFormOpinion> taskFormOpinions = taskFormOpinionService.findByExample(opinionExample);
        if (taskFormOpinions.isEmpty()) {
            taskFormOpinion = new TaskFormOpinion();
            taskFormOpinion.setFlowInstUuid(flowInstUuid);
            taskFormOpinion.setDataUuid(dataUuid);
            taskFormOpinion.setFieldName(fieldName);
            taskFormOpinion.setErrorData(false);
        } else {
            taskFormOpinion = taskFormOpinions.get(0);
        }
        taskFormOpinion.setContent(content);
        taskFormOpinionService.save(taskFormOpinion);

        // 设置到表单数据
        dyFormData.setFieldValue(fieldName, content);
        return taskFormOpinion.getUuid();
    }

    /**
     * @param userIds
     * @param userOptions
     * @return
     */
    private Set<String> sort(Set<String> userIds, Set<String> creators) {
        Set<String> tmpList = new LinkedHashSet<String>();
        tmpList.addAll(creators);
        tmpList.retainAll(userIds);
        return tmpList;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 1;
    }

}
