package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRecordDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:56
 * @Description:
 */
@Service
public class SerialNumberRecordServiceImpl extends AbstractJpaServiceImpl<SerialNumberRecord, ISerialNumberRecordDao, String> implements ISerialNumberRecordService {


}
