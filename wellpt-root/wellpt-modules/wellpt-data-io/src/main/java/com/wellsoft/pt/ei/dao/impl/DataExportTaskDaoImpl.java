package com.wellsoft.pt.ei.dao.impl;

import com.wellsoft.pt.ei.dao.DataExportTaskDao;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 数据导出任务dao实现类
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
@Repository
public class DataExportTaskDaoImpl extends AbstractJpaDaoImpl<DataExportTask, String> implements DataExportTaskDao {
}
