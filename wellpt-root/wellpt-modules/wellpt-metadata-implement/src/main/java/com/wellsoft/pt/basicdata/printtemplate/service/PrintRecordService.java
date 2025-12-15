package com.wellsoft.pt.basicdata.printtemplate.service;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintRecordDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintRecord;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 打印记录服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-3.1	zhouyq		2013-4-3		Create
 * </pre>
 * @date 2013-4-3
 */

public interface PrintRecordService extends JpaService<PrintRecord, PrintRecordDao, String> {

    /**
     * @param printObjectUuid
     * @return
     */
    List<PrintRecord> queryByPrintObject(String printObjectUuid);

}
