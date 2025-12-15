/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.dao.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberDefinitionDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
@Repository
public class SnSerialNumberDefinitionDaoImpl extends AbstractJpaDaoImpl<SnSerialNumberDefinitionEntity, String>
        implements SnSerialNumberDefinitionDao {
}
