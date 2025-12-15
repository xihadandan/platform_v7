/*
 * @(#)2015-1-8 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

import java.util.List;
import java.util.Map;

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
public interface TaskFormOpinionService extends BaseService {

    /**
     * @param taskFormOpinionUuid
     * @return
     */
    TaskFormOpinion get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param taskFormOpinion
     */
    void save(TaskFormOpinion taskFormOpinion);

    List<TaskFormOpinion> findByExample(TaskFormOpinion example);

    TaskFormOpinion getNewTaskFormOpinion(String flowInstUuid, String formUuid, String dataUuid, String fieldName,
                                          String appendedOpinion, boolean appendFirst);

    /**
     * 重新生成办理意见
     *
     * @param taskInstUuid
     * @param formUuid
     * @param dataUuid
     * @return
     */
    void rebuildTaskFormOpinion(String taskInstUuid, String formUuid, String dataUuid);

    void fixErrorData();

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    String fixErrorData(String inputFlowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskFormOpinion> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    List<String> recordTaskFormOpinion(WorkBean workBean, FlowDelegate flowDelegate);

    List<String> saveOpinionRecords(String opinionText, String taskInstUuid, String flowInstUuid,
                                    List<IdEntity> entities, DyFormData dyFormData, List<Record> records, Map<String,
            Object> values);

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @param record
     * @param dyFormData
     * @param values
     * @return
     */
    boolean checkRecordPreCondition(String taskInstUuid, String flowInstUuid, String flowDefUuid, Record record,
                                    DyFormData dyFormData, Map<String, Object> values);
}
