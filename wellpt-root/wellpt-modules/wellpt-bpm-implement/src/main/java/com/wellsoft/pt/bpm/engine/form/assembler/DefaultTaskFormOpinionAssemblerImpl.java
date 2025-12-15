/*
 * @(#)2016年5月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form.assembler;

import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

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
public class DefaultTaskFormOpinionAssemblerImpl implements TaskFormOpinionAssembler {

    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    /**
     * @param object
     * @return
     */
    private static final String objectToString(Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler#getName()
     */
    @Override
    public String getName() {
        return "按时间升序组织";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.form.assembler.TaskFormOpinionAssembler#assemble(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String, java.lang.String)
     */
    @Override
    public String assemble(String flowInstUuid, String taskInstUuid, DyFormData dyFormData, String fieldName,
                           String appendedOpinion, String contentOrigin) {
        String taskFormOpinionUuid = null;
        if (StringUtils.isBlank(flowInstUuid) || StringUtils.isBlank(dyFormData.getDataUuid())) {
            Object oldValue = dyFormData.getFieldValue(fieldName);
            dyFormData.setFieldValue(fieldName, objectToString(oldValue) + objectToString(appendedOpinion));
        } else {
            TaskFormOpinion taskFormOpinion = taskFormOpinionService.getNewTaskFormOpinion(flowInstUuid,
                    dyFormData.getFormUuid(), dyFormData.getDataUuid(), fieldName, appendedOpinion, false);
            // 历史内容来源
            if (Record.CONTENT_ORIGIN_FORM_FIELD.equals(contentOrigin)) {
                String formRecordValue = Objects.toString(dyFormData.getFieldValue(fieldName), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(formRecordValue)) {
                    dyFormData.setFieldValue(fieldName, formRecordValue + StringUtils.trimToEmpty(appendedOpinion));
                } else {
                    dyFormData.setFieldValue(fieldName, StringUtils.trimToEmpty(appendedOpinion));
                }
            } else {
                dyFormData.setFieldValue(fieldName, taskFormOpinion.getContent());
            }
            taskFormOpinionUuid = taskFormOpinion.getUuid();
        }
        return taskFormOpinionUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
