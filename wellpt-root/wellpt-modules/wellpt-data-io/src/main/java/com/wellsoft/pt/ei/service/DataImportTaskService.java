package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.dao.DataImportTaskDao;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据导入任务service
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
public interface DataImportTaskService extends JpaService<DataImportTask, DataImportTaskDao, String> {
    /**
     * 根据记录uuid,查找导入任务
     *
     * @param recordUuid
     * @return
     * @author baozh
     * @date 2021/9/23 14:59
     */
    List<DataImportTask> listByRecordUuid(String recordUuid);

    /**
     * 取消任务
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/9/23 15:06
     */
    void cancelTaskByUuid(String uuid);

    /**
     * 重启导入
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/9/23 15:09
     */
    void restartTaskByUuid(String uuid);

    /**
     * 统计导入中的数量
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/23 15:10
     */
    long getExecutingCount();
}
