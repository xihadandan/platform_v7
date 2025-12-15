package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskOperationTempDao;
import com.wellsoft.pt.bpm.engine.entity.TaskOperationTemp;
import com.wellsoft.pt.bpm.engine.service.TaskOperationTempService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/4/11 13:53
 * @Description: 环节保存操作意见
 */
@Service
public class TaskOperationTempServiceImpl extends AbstractJpaServiceImpl<TaskOperationTemp, TaskOperationTempDao, String> implements TaskOperationTempService {

    @Override
    @Transactional
    public TaskOperationTemp saveTemp(TaskOperationTemp operationTemp) {
        TaskOperationTemp temp = this.getTaskOperationTemp(operationTemp.getFlowInstUuid(), operationTemp.getTaskInstUuid(), operationTemp.getAssignee());
        temp.setAssigneeName(operationTemp.getAssigneeName());
        temp.setOpinionLabel(operationTemp.getOpinionLabel());
        temp.setOpinionValue(operationTemp.getOpinionValue());
        temp.setOpinionText(operationTemp.getOpinionText());
        temp.setTaskIdentityUuid(operationTemp.getTaskIdentityUuid());
        temp.setTaskId(operationTemp.getTaskId());
        temp.setTaskName(operationTemp.getTaskName());
        this.save(temp);
        return temp;
    }

    private TaskOperationTemp getTaskOperationTemp(String flowInstUuid, String taskInstUuid, String currentUserId) {
        TaskOperationTemp temp = new TaskOperationTemp();
        temp.setFlowInstUuid(flowInstUuid);
        if (StringUtils.isNotEmpty(taskInstUuid)) {
            temp.setTaskInstUuid(taskInstUuid);
        }
        temp.setAssignee(currentUserId);
        List<TaskOperationTemp> tempList = this.listByEntity(temp);
        if (CollectionUtils.isNotEmpty(tempList)) {
            temp = tempList.get(0);
        }
        return temp;
    }

    @Override
    public TaskOperationTemp queryTemp(String flowInstUuid, String taskInstUuid, String currentUserId) {
        return this.getTaskOperationTemp(flowInstUuid, taskInstUuid, currentUserId);
    }

    @Override
    @Transactional
    public void delTemp(String flowInstUuid, String taskInstUuid, String currentUserId) {
        TaskOperationTemp temp = new TaskOperationTemp();
        temp.setFlowInstUuid(flowInstUuid);
        if (StringUtils.isNotEmpty(taskInstUuid)) {
            temp.setTaskInstUuid(taskInstUuid);
        }
        temp.setAssignee(currentUserId);
        List<TaskOperationTemp> tempList = this.listByEntity(temp);
        if (CollectionUtils.isNotEmpty(tempList)) {
            this.deleteByEntities(tempList);
        }
    }
}
