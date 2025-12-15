/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.config.dao.MultiUserLoginSettingsDao;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import org.springframework.stereotype.Repository;


/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的DAO接口实现类
 *
 * @author baozh
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-11-18.1    baozh        2021-11-18		Create
 * </pre>
 * @date 2021-11-18
 */
@Repository
public class MultiUserLoginSettingsDaoImpl extends AbstractJpaDaoImpl<MultiUserLoginSettingsEntity, String> implements MultiUserLoginSettingsDao {


}

