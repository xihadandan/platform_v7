package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRelationDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:47
 * @Description:
 */
@Repository
public class SerialNumberRelationDaoImpl extends AbstractJpaDaoImpl<SerialNumberRelation, String> implements ISerialNumberRelationDao {
}
