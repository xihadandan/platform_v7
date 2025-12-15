package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dao.DataExportTaskLogDao;
import com.wellsoft.pt.ei.entity.DataExportTaskLog;
import com.wellsoft.pt.ei.service.DataExportTaskLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 数据导出任务日志service实现类
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
public class DataExportTaskLogServiceImpl extends AbstractJpaServiceImpl<DataExportTaskLog, DataExportTaskLogDao, String> implements DataExportTaskLogService {


    @Override
    public List<DataExportTaskLog> listByTaskUuids(List<String> taskUuids) {
        if (taskUuids == null || taskUuids.size() == 0) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("taskUuids", taskUuids);
        return listByHQL("from DataExportTaskLog where taskUuid in (:taskUuids)", paramMap);
    }

    @Override
    public List<DataRecordDetailBo> countChild(List<DataRecordDetailBo> detailBos, String dataTypeJson) {
        //获取所有DataType
        Map<String, List<String>> dataTypeMap = DataExportConstants.getDataType(DataExportConstants.EXPORT);
        List<DataRecordDetailBo> listBo = new ArrayList<>();
        List<String> taskUuids = detailBos.stream().map(detailBo -> detailBo.getTaskUuid()).collect(Collectors.toList());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("taskUuids", taskUuids);
        //查询日志明细统计数据
        List<DataRecordDetailBo> dataRecordDetailBos = getDao().listItemByNameSQLQuery("exportChildCount", DataRecordDetailBo.class, paramMap, null);
        List<String> dataRecordDetailBoNameList = new ArrayList<>();
        for (DataRecordDetailBo dataRecordDetailBo : dataRecordDetailBos) {
            dataRecordDetailBoNameList.add(dataRecordDetailBo.getName());
        }

        //解析日志明细
        for (DataRecordDetailBo detailBo : detailBos) {
            List<String> dataTypes = dataTypeMap.get(detailBo.getName());

            List<String> choiceTypeList = DataExportConstants.getChoiceImportType(dataTypeJson, detailBo.getName());
            List<DataRecordDetailBo> childList = new ArrayList<>();
            for (String dataType : dataTypes) {
                //获取子类统计数据
                DataRecordDetailBo childDetailBo = getDetailBo(dataType, dataRecordDetailBos);

                if (choiceTypeList.contains(dataType) && CollectionUtils.isEmpty(dataRecordDetailBoNameList)) {
                    // 有选中的类型，但没有导入/导出日志 -> 设置数量为 0
                    childDetailBo.setCount(0L);
                }

                childList.add(childDetailBo);
                if (childDetailBo.getCount() != null || detailBo.getCount() != null) {
                    detailBo.setCount(
                            (detailBo.getCount() == null ? 0 : detailBo.getCount())
                                    +
                                    (childDetailBo.getCount() == null ? 0 : childDetailBo.getCount())
                    );
                }
            }
            detailBo.setChildDetails(childList);
            listBo.add(detailBo);
        }
        return listBo;
    }


    private DataRecordDetailBo getDetailBo(String dataType, List<DataRecordDetailBo> detailBos) {
        DataRecordDetailBo bo = null;
        for (DataRecordDetailBo detailBo : detailBos) {
            if (dataType.equals(detailBo.getName())) {
                bo = detailBo;
                break;
            } else if ((detailBo.getParentName() + "_" + dataType).equals(detailBo.getName())) {
                bo = new DataRecordDetailBo();
                bo.setCount(detailBo.getCount());
                bo.setName(dataType);
                break;
            } else if (DataExportConstants.DATA_TYPE_FLOW_ALL_FILE_NAME.equals(detailBo.getName()) && DataExportConstants.DATA_TYPE_FLOW_ALL.equals(dataType)) {
                bo = new DataRecordDetailBo();
                bo.setCount(detailBo.getCount());
                bo.setName(dataType);
                break;
            }
        }
        if (bo == null) {
            bo = new DataRecordDetailBo();
            bo.setCount(null);
            bo.setName(dataType);
        }
        return bo;
    }

}
