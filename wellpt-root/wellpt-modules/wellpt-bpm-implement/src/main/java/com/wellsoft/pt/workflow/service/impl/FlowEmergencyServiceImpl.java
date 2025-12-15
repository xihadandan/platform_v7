package com.wellsoft.pt.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.workflow.service.FlowEmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/25.1	    zenghw		2021/8/25		    Create
 * </pre>
 * @date 2021/8/25
 */
@Service
public class FlowEmergencyServiceImpl implements FlowEmergencyService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskOperationService taskOperationService;
    @Autowired
    private TaskService taskService;

    @Override
    public void addDonePermission(String startDateTime, String endDateTime) {
        Date startDateTimeD = DateUtil.parseDate(startDateTime);
        Date endDateTimeD = DateUtil.parseDate(endDateTime);
        List<TaskOperation> taskOperationList = taskOperationService.getTaskOperationListByTimeInterval(
                DateUtil.formatDateTime(DateUtil.beginOfDay(startDateTimeD)),
                DateUtil.formatDateTime(DateUtil.endOfDay(endDateTimeD)));

        for (TaskOperation taskOperation : taskOperationList) {
            TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(taskOperation.getFlowInstUuid());
            taskService.addDonePermission(taskOperation.getCreator(), taskInstance.getUuid());
        }

    }
}
