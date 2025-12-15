package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberOldDataDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldData;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:39
 * @Description:
 */
public interface ISerialNumberOldDataService extends JpaService<SerialNumberOldData, ISerialNumberOldDataDao, String> {
}
