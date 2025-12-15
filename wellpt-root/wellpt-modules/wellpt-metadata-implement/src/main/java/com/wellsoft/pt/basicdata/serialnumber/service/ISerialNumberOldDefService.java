package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberOldDefDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldDef;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:37
 * @Description:
 */
public interface ISerialNumberOldDefService extends JpaService<SerialNumberOldDef, ISerialNumberOldDefDao, String> {

    void oldDataProcess(int pageSize);
}
