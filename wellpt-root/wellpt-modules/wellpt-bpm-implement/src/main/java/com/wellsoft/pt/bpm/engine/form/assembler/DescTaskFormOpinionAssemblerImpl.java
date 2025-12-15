package com.wellsoft.pt.bpm.engine.form.assembler;

import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinion;
import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinionLog;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSuspensionState;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

/**
 * Description: 流程环节  信息记录  的内容组织形式：按时间降序组织
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022/6/1.1	liuyz		2022/6/1		Create
 * </pre>
 * @date 2022/6/1
 */
@Service
@Transactional
public class DescTaskFormOpinionAssemblerImpl implements TaskFormOpinionAssembler {

    @Autowired
    private TaskFormOpinionLogService taskFormOpinionLogService;

    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    @Autowired
    private DefaultTaskFormOpinionAssemblerImpl defaultTaskFormOpinionAssembler;

    @Override
    public String getName() {
        return "按时间降序组织";
    }

    @Override
    public String assemble(String flowInstUuid, String taskInstUuid, DyFormData dyFormData, String fieldName,
                           String appendedOpinion, String contentOrigin) {
        TaskFormOpinionLog example = new TaskFormOpinionLog();
        example.setFieldName(fieldName);
        example.setRecordWay(Integer.valueOf(Record.WAY_APPEND));
        example.setStatus(WorkFlowSuspensionState.Normal);
        example.setFlowInstUuid(flowInstUuid);
        // List<TaskFormOpinionLog> logs = taskFormOpinionLogService.findByExample(example, "createTime desc");
        if (taskFormOpinionLogService.countByEntity(example) <= 0) {
            // 第一个信息记录，直接按照默认来
            return defaultTaskFormOpinionAssembler.assemble(flowInstUuid, taskInstUuid, dyFormData, fieldName,
                    appendedOpinion, contentOrigin);
        }

        TaskFormOpinion taskFormOpinion = taskFormOpinionService.getNewTaskFormOpinion(flowInstUuid,
                dyFormData.getFormUuid(), dyFormData.getDataUuid(), fieldName, appendedOpinion, true);
        // 历史内容来源
        if (Record.CONTENT_ORIGIN_FORM_FIELD.equals(contentOrigin)) {
            String formRecordValue = Objects.toString(dyFormData.getFieldValue(fieldName), StringUtils.EMPTY);
            if (StringUtils.isNotBlank(formRecordValue)) {
                dyFormData.setFieldValue(fieldName, StringUtils.trimToEmpty(appendedOpinion) + formRecordValue);
            } else {
                dyFormData.setFieldValue(fieldName, StringUtils.trimToEmpty(appendedOpinion));
            }
        } else {
            dyFormData.setFieldValue(fieldName, taskFormOpinion.getContent());
        }
        return taskFormOpinion.getUuid();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
