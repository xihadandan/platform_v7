package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberOldDefDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldDef;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:34
 * @Description:
 */
@Repository
public class SerialNumberOldDefDaoImpl extends AbstractJpaDaoImpl<SerialNumberOldDef, String> implements ISerialNumberOldDefDao {
}
