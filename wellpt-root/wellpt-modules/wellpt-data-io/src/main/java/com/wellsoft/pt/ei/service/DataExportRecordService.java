package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.bo.DataExportRecordInfoBo;
import com.wellsoft.pt.ei.dao.DataExportRecordDao;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据导出记录service
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
public interface DataExportRecordService extends JpaService<DataExportRecord, DataExportRecordDao, String> {

    /**
     * 查看导出记录详情
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/9/22 14:09
     */
    DataExportRecordInfoBo getTaskRecordInfoByUuid(String uuid);

}
