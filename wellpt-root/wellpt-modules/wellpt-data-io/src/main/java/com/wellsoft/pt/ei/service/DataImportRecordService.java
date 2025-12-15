package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.bo.DataImportRecordInfoBo;
import com.wellsoft.pt.ei.dao.DataImportRecordDao;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.jpa.service.JpaService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Description: 数据导入记录service
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
public interface DataImportRecordService extends JpaService<DataImportRecord, DataImportRecordDao, String> {

    /**
     * 方法描述
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/23 14:55
     */
    DataImportRecordInfoBo getTaskRecordInfoByUuid(String uuid) throws SQLException, IOException;
}
