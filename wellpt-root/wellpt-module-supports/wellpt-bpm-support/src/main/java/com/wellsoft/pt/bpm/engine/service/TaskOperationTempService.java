package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskOperationTempDao;
import com.wellsoft.pt.bpm.engine.entity.TaskOperationTemp;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * @Auther: yt
 * @Date: 2022/4/11 13:47
 * @Description: 环节保存操作意见
 */
public interface TaskOperationTempService extends JpaService<TaskOperationTemp, TaskOperationTempDao, String> {

    TaskOperationTemp saveTemp(TaskOperationTemp operationTemp);

    TaskOperationTemp queryTemp(String flowInstUuid, String taskInstUuid, String currentUserId);

    void delTemp(String flowInstUuid, String taskInstUuid, String currentUserId);
}
