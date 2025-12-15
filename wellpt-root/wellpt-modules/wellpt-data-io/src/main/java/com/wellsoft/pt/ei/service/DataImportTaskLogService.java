package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.bo.DataRecordDetailBo;
import com.wellsoft.pt.ei.dao.DataImportTaskLogDao;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据导入任务日志service
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
public interface DataImportTaskLogService extends JpaService<DataImportTaskLog, DataImportTaskLogDao, String> {

    /**
     * 查询日志记录
     *
     * @param taskUuids
     * @return
     * @author baozh
     * @date 2021/9/23 15:00
     */
    List<DataImportTaskLog> listByTaskUuids(List<String> taskUuids);

    /**
     * 根据sourceUuid 查询日志记录
     *
     * @param sourceUuid
     * @return
     */
    DataImportTaskLog getBySourceUuid(String sourceUuid);

    /**
     * 根据sourceId 查询日志记录
     *
     * @param sourceId
     * @return
     */
    DataImportTaskLog getBySourceId(String sourceId);

    long countByStatus(String taskUuid, int status);

    /**
     * 查询异常的任务日志
     *
     * @param taskUuids
     * @return
     * @author baozh
     * @date 2021/9/26 17:33
     */
    List<DataImportTaskLog> listErrorByTaskUuids(List<String> taskUuids);

    /**
     * 统计子类数量
     *
     * @param taskUuid,type 任务Id, (repeat重复,exception异常)
     * @return
     * @author baozh
     * @date 2021/9/26 17:41
     */
    DataRecordDetailBo countChild(String taskUuid, String type);


}
