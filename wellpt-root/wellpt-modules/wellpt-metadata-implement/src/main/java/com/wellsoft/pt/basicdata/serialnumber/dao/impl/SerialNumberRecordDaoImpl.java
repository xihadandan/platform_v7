package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRecordDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:49
 * @Description:
 */
@Repository
public class SerialNumberRecordDaoImpl extends AbstractJpaDaoImpl<SerialNumberRecord, String> implements ISerialNumberRecordDao {
}
