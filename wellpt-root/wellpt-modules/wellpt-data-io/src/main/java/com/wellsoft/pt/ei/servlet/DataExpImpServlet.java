package com.wellsoft.pt.ei.servlet;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.service.DataExportTaskService;
import com.wellsoft.pt.ei.service.DataImportTaskService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/10/19.1	liuyz		2021/10/19		Create
 * </pre>
 * @date 2021/10/19
 */
@WebServlet(loadOnStartup = 10, urlPatterns = {"/dataExpImpServlet"})
public class DataExpImpServlet extends HttpServlet {

    DataExportTaskService dataExportTaskService = null;
    DataImportTaskService dataImportTaskService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        dataExportTaskService = ApplicationContextHolder.getBean("dataExportTaskService", DataExportTaskService.class);
        dataImportTaskService = ApplicationContextHolder.getBean("dataImportTaskService", DataImportTaskService.class);

        // 启动时，对状态为导出中的任务进行重新导出
        DataExportTask task = new DataExportTask();
        task.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        List<DataExportTask> dataExportTasks = dataExportTaskService.listByEntity(task);
        for (DataExportTask tmp : dataExportTasks) {
            dataExportTaskService.restartTaskByUuid(tmp.getUuid());
        }

        // 对状态为导出中的导入任务进行重新导入
        DataImportTask dataImportTask = new DataImportTask();
        dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        List<DataImportTask> dataImportTasks = dataImportTaskService.listByEntity(dataImportTask);
        for (DataImportTask tmp : dataImportTasks) {
            dataImportTaskService.restartTaskByUuid(tmp.getUuid());
        }

    }
}
