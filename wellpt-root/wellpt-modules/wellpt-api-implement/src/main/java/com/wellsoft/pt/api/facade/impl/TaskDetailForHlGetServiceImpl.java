/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskDetailForHlGetRequest;
import com.wellsoft.pt.api.response.TaskDetailForHlGetResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_DETAIL_GET_FOR_HL)
@Transactional
public class TaskDetailForHlGetServiceImpl extends BaseServiceImpl implements WellptService<TaskDetailForHlGetRequest> {

    @Autowired
    private com.wellsoft.pt.bpm.engine.service.TaskService taskService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskDetailForHlGetRequest taskDetailGetRequest) {
        String taskInstUuid = taskDetailGetRequest.getUuid();
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        com.wellsoft.pt.bpm.engine.form.TaskForm taskForm = taskService.getTaskForm(taskInstUuid);
        // List<String> hideFields = taskForm.get
        // 任务UUID
        String uuid = taskInstance.getUuid();
        // 任务id
        String id = taskInstance.getId();
        // 任务名称
        String name = taskInstance.getName();
        // 任务开始时间
        Date startTime = taskInstance.getStartTime();
        // 动态表单数据
        DyFormData dyFormData = dyFormFacade.getDyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid());
        // JSONObject jsonObject = new JSONObject();
        Map<String, String> mapData = new HashMap<String, String>();
        try {
            mapData.put("taskUuid", uuid);
            Map<String, List<Map<String, Object>>> mapAll = dyFormData.getDisplayValues();
            for (String key : mapAll.keySet()) {
                List<DyFormData> dyFormDataList = dyFormData.getDyformDatas(key);
                List<Map<String, Object>> list = mapAll.get(key);
                for (int index = 0; index < list.size(); index++) {
                    Map<String, Object> map = list.get(index);
                    for (String field : map.keySet()) {
                        for (DyFormData dd : dyFormDataList) {
                            String displayValue = dd.getDisplayNameOfField(field);
                            boolean fieldStatus = dd.isFieldHide(field);
                            if (StringUtils.isNotBlank(displayValue) && fieldStatus != true) {
                                Object fieldValue = map.get(field);
                                if (fieldValue != null && fieldValue.toString().indexOf("LogicFileInfo") < 0) {
                                    mapData.put(displayValue, fieldValue.toString());
                                }
                            }
                        }
                    }
                }
            }
            // Map<String, Object> map =
            // dyFormData.getDisplayValues().get(taskInstance.getFormUuid()).get(0);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        TaskDetailForHlGetResponse response = new TaskDetailForHlGetResponse();
        // response
        response.setData(mapData);
        return response;
    }
}
