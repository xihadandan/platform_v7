package com.wellsoft.pt.basicdata.printtemplate.service.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintRecordDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintRecord;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 打印记录实现类
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
@Service
public class PrintRecordServiceImpl extends AbstractJpaServiceImpl<PrintRecord, PrintRecordDao, String> implements
        PrintRecordService {

    @Override
    public List<PrintRecord> queryByPrintObject(String printObjectUuid) {
        return dao.listByFieldEqValue("printObject", printObjectUuid);
    }

}
