package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.ei.bo.DataImportRecordInfoBo;
import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dao.DataImportRecordDao;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.service.DataImportRecordService;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.DataImportTaskService;
import com.wellsoft.pt.ei.utils.DataRecordFormat;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据导入记录service实现类
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
public class DataImportRecordServiceImpl extends AbstractJpaServiceImpl<DataImportRecord, DataImportRecordDao, String> implements DataImportRecordService {

    @Autowired
    DataImportTaskService dataImportTaskService;

    @Autowired
    DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    DataRecordFormat dataRecordFormat;

    @Override
    public DataImportRecordInfoBo getTaskRecordInfoByUuid(String uuid) throws SQLException, IOException {

        DataImportRecordInfoBo bo = new DataImportRecordInfoBo();
        //根据uuid查询记录详情
        DataImportRecord dataImportRecord = getOne(uuid);
        if (dataImportRecord == null) {
            throw new WellException("记录详情不存在");
        }
        BeanUtils.copyProperties(dataImportRecord, bo);
        /*String dataType2ZH = dataRecordFormat.dataType2ZH(bo.getDataTypeJson(), DataExportConstants.IMPORT);
        if (dataType2ZH != null) {//如果dataTypeJson不为空修改为前端显示格式
            bo.setDataType(dataType2ZH);
        }*/

        if (null != dataImportRecord.getImportFiles()) {
            bo.setImportFiles(StringUtils.join(IOUtils.toString(dataImportRecord.getImportFiles().getCharacterStream()).split(Separator.COMMA.getValue()), "，"));
        }

        //根据uuid查询任务
        List<DataImportTask> dataImportTasks = dataImportTaskService.listByRecordUuid(uuid);
        if (dataImportTasks == null || dataImportTasks.size() == 0) {
            throw new WellException("导入任务未启动，请检查后重试");
        }
        DataImportTask dataTask = dataImportTasks.get(0);

        //根据任务uuid统计日志总信息
        DataRecordDetailBo totalDetail = dataImportTaskLogService.countChild(dataTask.getUuid(), DataExportConstants.DATA_TYPE_COUNT_TOTAL);
        totalDetail.setTaskUuid(dataTask.getUuid());
        totalDetail.setFinishTime(dataTask.getFinishTime());
        if (dataTask.getFinishTime() != null) {
            totalDetail.setUsedTime(DateUtils.calculateAsFormatZH(dataTask.getImportTime(), dataTask.getFinishTime(), "minute"));
        }
        totalDetail.setTaskStatus(dataTask.getTaskStatus());
        //获取所有DataType
        Map<String, List<String>> dataTypeMap = DataExportConstants.getDataType(DataExportConstants.IMPORT);

        for (String dataType : dataTypeMap.get(dataTask.getDataType())) {
            addChildDetailBo(dataType, totalDetail.getChildDetails());
        }

        List<String> types = DataExportConstants.getChoiceImportType(dataImportRecord.getDataTypeJson(), dataImportRecord.getDataType());
        totalDetail.getChildDetails().forEach(child -> {
            if (null == child.getCount()) {
                // 判断是没有这个类型的导入导致的null，还是因为没有数据导致的null
                if (types.contains(child.getName())) {
                    child.setCount(0L);
                }
            }
        });

        if (null == totalDetail.getCount()) {
            totalDetail.setCount(0L);
        }

        //根据任务uuid统计日志重复信息
        DataRecordDetailBo repeatDetail = dataImportTaskLogService.countChild(dataTask.getUuid(), DataExportConstants.DATA_TYPE_COUNT_REPEAT);
        repeatDetail.setTaskUuid(dataTask.getUuid());
        //根据任务uuid统计日志异常信息
        DataRecordDetailBo exceptionDetail = dataImportTaskLogService.countChild(dataTask.getUuid(), DataExportConstants.DATA_TYPE_COUNT_EXCEPTION);
        exceptionDetail.setTaskUuid(dataTask.getUuid());
        List<DataRecordDetailBo> dataRecordDetailBos = new ArrayList<>();
        dataRecordDetailBos.add(totalDetail);
        dataRecordDetailBos.add(repeatDetail);
        dataRecordDetailBos.add(exceptionDetail);
        bo.setDataRecordDetailBos(dataRecordDetailBos);
        return bo;
    }


    /**
     * 补充不足的明细统计
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/28 14:04
     */
    private void addChildDetailBo(String dataType, List<DataRecordDetailBo> detailBos) {
        boolean flag = true;
        for (DataRecordDetailBo detailBo : detailBos) {
            if (dataType.equals(detailBo.getName())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            DataRecordDetailBo bo = new DataRecordDetailBo();
//            bo.setCount(0L);
            bo.setName(dataType);
            detailBos.add(bo);
        }
    }

}
