package com.wellsoft.pt.basicdata.printtemplate.dao.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintRecordDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintRecord;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description:  打印记录数据层访问类
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
@Repository
public class PrintRecordDaoImpl extends AbstractJpaDaoImpl<PrintRecord, String> implements PrintRecordDao {

}
