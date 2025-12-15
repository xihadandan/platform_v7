package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.ei.bo.DataExportRecordInfoBo;
import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dao.DataExportRecordDao;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.entity.DataExportTaskLog;
import com.wellsoft.pt.ei.service.DataExportRecordService;
import com.wellsoft.pt.ei.service.DataExportTaskLogService;
import com.wellsoft.pt.ei.service.DataExportTaskService;
import com.wellsoft.pt.ei.utils.DataRecordFormat;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 数据导出记录service实现类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/16.1	liuyz		2021/9/16		Create
 * </pre>
 * @date 2021/9/16
 */
@Service
public class DataExportRecordServiceImpl extends AbstractJpaServiceImpl<DataExportRecord, DataExportRecordDao, String> implements DataExportRecordService {


    @Autowired
    DataExportTaskService dataExportTaskService;
    @Autowired
    DataExportTaskLogService dataExportTaskLogService;

    @Autowired
    DataRecordFormat dataRecordFormat;

    @Override
    public DataExportRecordInfoBo getTaskRecordInfoByUuid(String uuid) {
        DataExportRecordInfoBo bo = new DataExportRecordInfoBo();
        //根据uuid查询记录详情
        DataExportRecord dataExportRecord = getOne(uuid);

        if (dataExportRecord == null) {
            throw new WellException("记录详情不存在");
        }
        BeanUtils.copyProperties(dataExportRecord, bo);
        String dataType2ZH = dataRecordFormat.dataType2ZH(bo.getDataTypeJson(), DataExportConstants.EXPORT);
        if (dataType2ZH != null) {//如果dataTypeJson不为空修改为前端显示格式
            bo.setDataType(dataType2ZH);
        }
        //根据uuid查询任务
        List<DataExportTask> dataExportTasks = dataExportTaskService.listByRecordUuid(uuid);
        if (dataExportTasks == null || dataExportTasks.size() == 0) {
            throw new WellException("导出任务未启动，请检查后重试");
        }
        List<DataRecordDetailBo> detailBos = dataExportTasks.stream().map(dataExportTask -> {
            DataRecordDetailBo detailBo = new DataRecordDetailBo();
            detailBo.setTaskUuid(dataExportTask.getUuid());
            detailBo.setName(dataExportTask.getDataType());
            detailBo.setCount(null);
            detailBo.setTaskStatus(dataExportTask.getTaskStatus());
            detailBo.setFinishTime(dataExportTask.getFinishTime());
            if (dataExportTask.getFinishTime() != null) {
                detailBo.setUsedTime(DateUtils.calculateAsFormatZH(dataExportTask.getExportTime(), dataExportTask.getFinishTime(), "minute"));
            } else {
                detailBo.setUsedTime(DateUtils.calculateAsFormatZH(dataExportTask.getExportTime(), new Date(), "minute"));
            }
            if (dataExportTask.getTaskStatus().equals(DataExportConstants.DATA_STATUS_ERROR)) {
                DataExportTaskLog log = new DataExportTaskLog();
                log.setTaskUuid(dataExportTask.getUuid());
                log.setExportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                List<DataExportTaskLog> results = dataExportTaskLogService.listAllByPage(log, null, "exportTime desc");
                if (CollectionUtils.isNotEmpty(results)) {
                    detailBo.setErrorMessage(results.get(0).getErrorMsg());
                }
            }
            return detailBo;
        }).collect(Collectors.toList());
        //根据任务uuid查询任务日志
        //List<DataExportTaskLog> dataExportTaskLogs = dataExportTaskLogService.listByTaskUuids(taskUuids);

        List<DataRecordDetailBo> recordDetailBos = dataExportTaskLogService.countChild(detailBos, dataExportRecord.getDataTypeJson());

        bo.setDataRecordDetailBos(recordDetailBos);

        return bo;
    }


}
