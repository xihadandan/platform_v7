package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberOldDataDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldData;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:33
 * @Description:
 */
@Repository
public class SerialNumberOldDataDaoImpl extends AbstractJpaDaoImpl<SerialNumberOldData, String> implements ISerialNumberOldDataDao {
}
