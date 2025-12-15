package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.dao.DataExportTaskLogDao;
import com.wellsoft.pt.ei.entity.DataExportTaskLog;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据导出任务日志service
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
public interface DataExportTaskLogService extends JpaService<DataExportTaskLog, DataExportTaskLogDao, String> {


    /**
     * 根据任务uuid返回任务日志
     *
     * @param taskUuids
     * @return
     * @author baozh
     * @date 2021/9/22 14:24
     */
    List<DataExportTaskLog> listByTaskUuids(List<String> taskUuids);

    /**
     * 统计子类数量
     *
     * @param detailBos
     * @return
     * @author baozh
     * @date 2021/9/27 11:29
     */
    List<DataRecordDetailBo> countChild(List<DataRecordDetailBo> detailBos, String dataTypeJson);
}
