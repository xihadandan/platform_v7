package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.constants.ExportCheckPathEnum;
import com.wellsoft.pt.ei.dao.DataExportTaskDao;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据导出任务service
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
public interface DataExportTaskService extends JpaService<DataExportTask, DataExportTaskDao, String> {


    List<DataExportTask> saveAll(DataExportRecord record);

    /**
     * 校验路径是否合法
     *
     * @param path
     * @return
     * @author baozh
     * @date 2021/9/18 16:05
     */
    ExportCheckPathEnum checkPath(String path);

    /**
     * 根据记录查询任务信息
     *
     * @param recordUuid
     * @return
     * @author baozh
     * @date 2021/9/22 14:18
     */
    List<DataExportTask> listByRecordUuid(String recordUuid);

    /**
     * 取消任务
     *
     * @return
     * @author baozh
     * @date 2021/9/22 15:55
     * @params *@params
     */
    void cancelTaskByUuid(String uuid);

    /**
     * 重启任务
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/22 15:56
     */
    void restartTaskByUuid(String uuid);

    /**
     * 徽标接口
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/22 16:28
     */
    long getExecutingCount();
}
