/*
 * @(#)2018-12-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.config.dao.AppLoginPageConfigDao;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import org.springframework.stereotype.Repository;


/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的DAO接口实现类
 *
 * @author linst
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-12-07.1    leo        2018-12-07		Create
 * </pre>
 * @date 2018-12-07
 */
@Repository
public class AppLoginPageConfigDaoImpl extends AbstractJpaDaoImpl<AppLoginPageConfigEntity, String> implements AppLoginPageConfigDao {


}

