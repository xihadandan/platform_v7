package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRecordDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:56
 * @Description:
 */
public interface ISerialNumberRecordService extends JpaService<SerialNumberRecord, ISerialNumberRecordDao, String> {


}
